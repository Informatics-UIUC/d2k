package ncsa.d2k.modules.core.prediction.naivebayes;

import java.io.*;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;

public class WriteNaiveBayesPMML
    extends OutputModule
    implements NaiveBayesPMMLTags {

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return in;
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String[] getOutputTypes() {
    return null;
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  private static final String NO_FIELDS = "numberOfFields";

  public void doit() throws Exception {
    NaiveBayesModel nbm = (NaiveBayesModel) pullInput(0);
    ExampleTable et = (ExampleTable) pullInput(1);

    Document document = DocumentHelper.createDocument();
    Element root = document.addElement("PMML");
    root.addAttribute("version", "2.0");

    Element header = root.addElement("Header");
    header.addAttribute("copyright", "NCSA ALG");
    header.addAttribute("description", "a naive bayes model");

    String[] inputNames = nbm.getInputColumnLabels();
    int[] inputTypes = nbm.getInputFeatureTypes();
    String[] outNames = nbm.getOutputColumnLabels();
    int[] outTypes = nbm.getOutputFeatureTypes();

    HashMap inputToIndexMap = new HashMap();
    for (int i = 0; i < inputNames.length; i++) {
      for (int j = 0; j < et.getNumColumns(); j++) {
        if (et.getColumnLabel(j).equals(inputNames[i])) {
          inputToIndexMap.put(inputNames[i], new Integer(j));
          break;
        }
      }
    }

    HashMap outputToIndexMap = new HashMap();
    for (int i = 0; i < outNames.length; i++) {
      for (int j = 0; j < et.getNumColumns(); j++) {
        if (et.getColumnLabel(j).equals(outNames[i])) {
          outputToIndexMap.put(outNames[i], new Integer(j));
          break;
        }
      }
    }

    // do the data dictionary
    Element dictionary = root.addElement(DATA_DICT);
    dictionary.addAttribute(NO_FIELDS,
                            Integer.toString(inputNames.length +
                                             outNames.length));

    // for each of the inputs, create a DataField element
    for (int i = 0; i < inputNames.length; i++) {
      Element field = dictionary.addElement(DATA_FIELD);
      field.addAttribute(NAME, inputNames[i]);

      // if it is Scalar, mark the input as continuous
      Integer idx = (Integer) inputToIndexMap.get(inputNames[i]);
      if (idx == null) {
        throw new Exception("Could not find input column: " + inputNames[i]);
      }

      if (et.isColumnScalar(idx.intValue())) {
        field.addAttribute(OPTYPE, "continuous");
      }

      // otherwise it is categorical.  mark it as such and list
      // all the values
      else {
        Element catAtt = field.addAttribute(OPTYPE, CATEGORICAL);
        // now get the unique values
        // !!!!!
        // String[] vals = nbm.getUniqueInputValues(i);
        String[] vals = TableUtilities.uniqueValues(et, idx.intValue());
        for (int j = 0; j < vals.length; j++) {
          Element e = catAtt.addElement(VALUE_ELEMENT);
          e.addAttribute(VALUE, vals[j]);
        }
      }
    }

    // for each of the outputs, create a DataField element
    for (int i = 0; i < outNames.length; i++) {
      Element field = dictionary.addElement(DATA_FIELD);
      field.addAttribute(NAME, outNames[i]);

      // if it is scalar, mark the output as continous
      //if(outTypes[i].equals("Scalar"))
      //    field.addAttribute("optype", "continuous");

      // if it is Scalar, mark the input as continuous
      Integer idx = (Integer) outputToIndexMap.get(outNames[i]);
      if (idx == null) {
        throw new Exception("Could not find output column: " + outNames[i]);
      }

      if (et.isColumnScalar(idx.intValue())) {
        field.addAttribute(OPTYPE, "continuous");
      }

      // otherwise it is categorical.  mark it as such and list
      // all the values
      else {
        Element catAtt = field.addAttribute(OPTYPE, CATEGORICAL);
        // now get the unique values
//                String[] vals = dtm.getUniqueOutputValues();
        String[] vals = TableUtilities.uniqueValues(et, idx.intValue());
        for (int j = 0; j < vals.length; j++) {
          Element e = catAtt.addElement(VALUE_ELEMENT);
          e.addAttribute(VALUE, vals[j]);
        }
      }
    }
    // finished with the dictionary.

    Element modelElement = root.addElement("NaiveBayesModel");
    modelElement.addAttribute("modelName", "foo");
    modelElement.addAttribute("functionName", "classification");
    modelElement.addAttribute("threshold", "0.001");

    // add the mining schema
    Element schema = modelElement.addElement("MiningSchema");
    for (int i = 0; i < inputNames.length; i++) {
      Element field = schema.addElement("MiningField");
      field.addAttribute("name", inputNames[i]);
    }

    for (int i = 0; i < outNames.length; i++) {
      Element field = schema.addElement("MiningField");
      field.addAttribute("name", outNames[i]);
      field.addAttribute(USAGE_TYPE, "predicted");
    }

    // get the bin tree
    BinTree binTree = nbm.getBinTree();

    Element bayesInputs = modelElement.addElement(BAYES_INPUTS);
    String[] outputs = nbm.getClassNames();

    // for each input
    for (int i = 0; i < inputNames.length; i++) {
      String inputName = inputNames[i];
      Element bayesInput = bayesInputs.addElement(BAYES_INPUT);
      bayesInput.addAttribute(FIELD_NAME, inputName);

      // now add a PairCounts for each bin...

      Integer idx = (Integer) inputToIndexMap.get(inputNames[i]);
      // if it is nominal....
      if (!et.isColumnScalar(idx.intValue())) {
        String[] binNames = binTree.getBinNames(inputName);

        // for each bin, create a pair counts
        for (int j = 0; j < binNames.length; j++) {
          Element pairCounts = bayesInput.addElement(PAIR_COUNTS);
          pairCounts.addAttribute(VALUE, binNames[j]);
          Element targetValueCounts = pairCounts.addElement(TARGET_VALUE_COUNTS);
          for (int k = 0; k < outputs.length; k++) {
            Element targetValue = targetValueCounts.addElement(
                TARGET_VALUE_COUNT);
            targetValue.addAttribute(VALUE, outputs[k]);
            int tally = binTree.getTally(outputs[k], inputName, binNames[j]);
            targetValue.addAttribute(COUNT, Integer.toString(tally));
          }
        }
      }
      // if it is a scalar..
      else {
        String[] binNames = binTree.getBinNames(inputName);

        Element derivedField = bayesInput.addElement("DerivedField");
        Element discretize = derivedField.addElement("Discretize");
        discretize.addAttribute("field", inputName);

        // for each bin
        for (int j = 0; j < binNames.length; j++) {
          Element bin = discretize.addElement("DiscretizeBin");
          bin.addAttribute("binValue", binNames[j]);

          Element interval = bin.addElement("Interval");

          boolean includeLower = binTree.includeLowerBound(
              outputs[0], inputName, binNames[j]);
          boolean includeUpper = binTree.includeUpperBound(
              outputs[0], inputName, binNames[j]);

          if (includeLower && includeUpper) {
            interval.addAttribute("closure", "openOpen");
          }
          else if (includeLower && !includeUpper) {
            interval.addAttribute("closure", "openClosed");
          }
          else if (!includeLower && includeUpper) {
            interval.addAttribute("closure", "closedOpen");
          }
          else if (!includeLower && !includeUpper) {
            interval.addAttribute("closure", "closedClosed");

          }
          double lowerBound = binTree.getLowerBound(outputs[0],
              inputName, binNames[j]);
          double upperBound = binTree.getUpperBound(outputs[0],
              inputName, binNames[j]);

          interval.addAttribute("leftMargin", Double.toString(lowerBound));
          interval.addAttribute("rightMargin", Double.toString(upperBound));
        }

        // for each bin, create a pair counts
        for (int j = 0; j < binNames.length; j++) {
          Element pairCounts = bayesInput.addElement(PAIR_COUNTS);
          pairCounts.addAttribute(VALUE, binNames[j]);
          Element targetValueCounts = pairCounts.addElement(TARGET_VALUE_COUNTS);
          for (int k = 0; k < outputs.length; k++) {
            Element targetValue = targetValueCounts.addElement(
                TARGET_VALUE_COUNT);
            targetValue.addAttribute(VALUE, outputs[k]);
            int tally = binTree.getTally(outputs[k], inputName, binNames[j]);
            targetValue.addAttribute(COUNT, Integer.toString(tally));
          }
        }
      }
    }

    Element bayesOutput = modelElement.addElement(BAYES_OUTPUT);
    bayesOutput.addAttribute(FIELD_NAME, outNames[0]);
    Element targetValueCounts = bayesOutput.addElement(TARGET_VALUE_COUNTS);
    for (int i = 0; i < outputs.length; i++) {
      Element tvc = targetValueCounts.addElement(TARGET_VALUE_COUNT);
      int tally = binTree.getClassTotal(outputs[i]);
      tvc.addAttribute("value", outputs[i]);
      tvc.addAttribute("count", Integer.toString(tally));
    }

    try {
      XMLWriter writer = new XMLWriter(new FileWriter("/home/clutter/test.pmml"),
                                       OutputFormat.createPrettyPrint());
      writer.write(document);
      writer.flush();
      writer.close();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}