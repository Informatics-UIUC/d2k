package ncsa.d2k.modules.core.prediction.naivebayes;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.BinTree;

import org.dom4j.*;
import org.dom4j.io.*;

import java.io.*;
import java.util.*;

public class CreateNBModelFromPMML extends InputModule implements NaiveBayesPMMLTags {

    public String[] getInputTypes() {
      String[] in = {"java.lang.String"};
      return in;
    }

    public String getInputInfo(int i) {
        return "";
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
        return out;
    }

    public String getOutputInfo(int i) {
        return "";
    }

    public String getModuleInfo() {
        return "";
    }

    public void doit() throws Exception {
        String fileName = (String)pullInput(0);

        // read in the file
        FileInputStream fis = new FileInputStream(fileName);
        SAXReader reader = new SAXReader(false);

        Document document = reader.read(fis);

        // get the root element
        Element pmml = document.getRootElement();

        // get the data dictionary
        Element dictionary = pmml.element(DATA_DICT);
        // get the naive bayes model element
        Element model = pmml.element(NBM);
        String modelName = model.attributeValue(MODEL_NAME);
        String functionName = model.attributeValue(FUNCTION_NAME);
        String threshold = model.attributeValue(THRESHOLD);

        // get the mining schema
        Element miningSchema = model.element(MINING_SCHEMA);

        List ins = new LinkedList();
        List outs = new LinkedList();

        // process mining schema
        // get lists of the inputs (active) and outputs (predicted)
        List miningFields = miningSchema.elements(MINING_FIELD);
        for(int i = 0; i < miningFields.size(); i++) {
            Element miningFld = (Element)miningFields.get(i);
            Attribute usage = miningFld.attribute(USAGE_TYPE);
            if(usage == null || usage.getValue().equals("active"))
                ins.add(miningFld.attributeValue(NAME));
            else if(usage.getValue().equals(PREDICTED))
                outs.add(miningFld.attributeValue(NAME));
        }

        String[] inputs = new String[ins.size()];
        String[] outputs = new String[outs.size()];
        for(int i = 0; i < inputs.length; i++)
          inputs[i] = (String) ins.get(i);
        for(int i = 0; i < outputs.length; i++)
          outputs[i] = (String) outs.get(i);

        String[] uniqueOutputs = null;

        HashSet categoricalInputs = new HashSet();
        HashSet continuousInputs = new HashSet();

        // now go back and process the data dictionary.
        List dataFields = dictionary.elements(DATA_FIELD);
        for(int i = 0; i < dataFields.size(); i++) {
            // all we need here is the output's data field
            Element dataField = (Element)dataFields.get(i);
            String name = dataField.attributeValue(NAME);
            if(name.equals(outputs[0])) {
                String optype = dataField.attributeValue(OPTYPE);
                if(!optype.equals(CATEGORICAL))
                    throw new Exception("Not a categorical output");
                List uniques = dataField.elements(VALUE_ELEMENT);
                uniqueOutputs = new String[uniques.size()];
                for(int j = 0; j < uniques.size(); j++) {
                    Element val = (Element)uniques.get(j);
                    uniqueOutputs[j] = val.attributeValue(VALUE);
                }
            }
            else {
                String optype = dataField.attributeValue(OPTYPE);
                if(optype.equals(CATEGORICAL))
                    categoricalInputs.add(name);
                else if(optype.equals(CONTINUOUS))
                    continuousInputs.add(name);
                else
                    throw new Exception("Unknown type input!");
            }
        }

        // create the bin tree to hold the counts
        BinTree binTree = new BinTree(uniqueOutputs, inputs);
        int totalClassified = 0;

        Element inputsElement = model.element(BAYES_INPUTS);
        List inputsList = inputsElement.elements(BAYES_INPUT);

        // the first thing to do is to find all the bins.
        // loop through each input and add bins appropriately

        // for each input
        for(int i = 0; i < inputsList.size(); i++) {

            // get the input
            Element input = (Element)inputsList.get(i);
            String name = input.attributeValue(FIELD_NAME);

            // it is a categorical input.  easy.
            if(categoricalInputs.contains(name)) {
                // get the pair counts
                List pairCts = input.elements(PAIR_COUNTS);
                // for each pair count, add the bin.
                for(int j = 0; j < pairCts.size(); j++) {
                  Element pairCt = (Element)pairCts.get(j);
                  String val = pairCt.attributeValue(VALUE);

                  // add the bin
                  binTree.addStringBin(name, val, val);

                  // now that the bin is added, we can set the tallies
                  Element tvcs = pairCt.element(TARGET_VALUE_COUNTS);
                  List counts = tvcs.elements(TARGET_VALUE_COUNT);
                  for(int k = 0; k < counts.size(); k++) {
                    Element ct = (Element)counts.get(k);
                    String outVal = ct.attributeValue(VALUE);
                    String outCount = ct.attributeValue(COUNT);

                    // now set the tally.
                    binTree.setBinTally(outVal, name, val, Integer.parseInt(outCount));
                  }
                }
            }
            // it is a continuous input.  harder.
            else {
              // get the derived field
              Element derivedFld = input.element(DERIVED_FIELD);
              Element discretize = derivedFld.element(DISCRETIZE);
              List bins = discretize.elements(DISCRETIZE_BIN);

              List binnames = new LinkedList();

              for(int j = 0; j < bins.size(); j++) {
                Element bin = (Element)bins.get(j);
                String binname = bin.attributeValue(BIN_VALUE);
                binnames.add(binname);

                Element interval = (Element)bin.element(INTERVAL);
                String closure = interval.attributeValue(CLOSURE);
                String leftMargin = interval.attributeValue(LEFT_MARGIN);
                String rightMargin = interval.attributeValue(RIGHT_MARGIN);

                boolean includeLo = false;
                boolean includeHi = false;

                // now find the closures
                if(closure.equals(CLOSED_OPEN)) {
                  includeLo = false;
                  includeHi = true;
                }
                else if(closure.equals(CLOSED_CLOSED)) {
                  includeLo = false;
                  includeHi = false;
                }
                else if(closure.equals(OPEN_CLOSED)) {
                  includeLo = true;
                  includeHi = false;
                }
                else if(closure.equals(OPEN_OPEN)) {
                  includeLo = true;
                  includeHi = true;
                }

                double lower = Double.parseDouble(leftMargin);
                double upper = Double.parseDouble(rightMargin);

                binTree.addNumericBin(name, binname, lower, includeLo,
                                        upper, includeHi);
              }

              // now get the tallies from the pair counts
              List pairCts = input.elements(PAIR_COUNTS);
              for(int j = 0; j < pairCts.size(); j++) {
                Element pairct = (Element)pairCts.get(j);
                String binname = pairct.attributeValue(VALUE);

                Element tvc = pairct.element(TARGET_VALUE_COUNTS);
                List tvcs = tvc.elements(TARGET_VALUE_COUNT);
                for(int k = 0; k < tvcs.size(); k++) {
                  Element ct = (Element)tvcs.get(k);
                  String outVal = ct.attributeValue(VALUE);
                  String outCount = ct.attributeValue(COUNT);

                  // now set the tally.
                  binTree.setBinTally(outVal, name, binname, Integer.parseInt(outCount));
                }
              }
            }
        }

        // define the bins

        Element outputElement = model.element(BAYES_OUTPUT);

        // for each output get the class total
        Element tvc = outputElement.element(TARGET_VALUE_COUNTS);
        List tvcs = tvc.elements(TARGET_VALUE_COUNT);
        for(int i = 0; i < tvcs.size(); i++) {
          Element count = (Element)tvcs.get(i);
          String outName = count.attributeValue(VALUE);
          String ct = count.attributeValue(COUNT);
          binTree.setClassTotal(outName, Integer.parseInt(ct));
          totalClassified += Integer.parseInt(ct);
        }

        binTree.setTotalClassified(totalClassified);

        // now make the ExampleTable.
        int[] inFeat = new int[inputs.length];
        int[] outFeat = new int[outputs.length];

        MutableTableImpl ti = new MutableTableImpl();
        for(int i = 0; i < inputs.length; i++) {
          if(categoricalInputs.contains(inputs[i])) {
            ti.addColumn(new String[0]);
            ti.setColumnIsNominal(true, i);
            ti.setColumnIsScalar(false, i);
            ti.setColumnLabel(inputs[i], i);
          }
          else {
            ti.addColumn(new double[0]);
            ti.setColumnIsNominal(false, i);
            ti.setColumnIsScalar(true, i);
            ti.setColumnLabel(inputs[i], i);
          }
          inFeat[i] = i;
        }

        for(int i = 0; i < outputs.length; i++) {
          ti.addColumn(new String[0]);
          ti.setColumnIsNominal(true, i+inputs.length);
            ti.setColumnLabel(outputs[i], i+inputs.length);
          outFeat[i] = i+inputs.length;
        }

        ExampleTable et = ti.toExampleTable();
        et.setInputFeatures(inFeat);
        et.setOutputFeatures(outFeat);

        NaiveBayesModel nbm = new NaiveBayesModel(binTree, et);
        pushOutput(nbm, 0);
    }
}