package ncsa.d2k.modules.core.prediction.naivebayes;

import java.io.*;

import org.dom4j.*;
import org.dom4j.io.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.transform.binning.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;


public class WriteNaiveBayesPMML
    extends OutputModule
    implements NaiveBayesPMMLTags {

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel",
        "java.lang.String"};
    return in;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case (0):
        return "The NaiveBayesModel to write out.";
      case (1):
        return "The absolute path for the file to write the XML file to.";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch(i) {
      case(0):
        return "Naive Bayes Model";
      case(1):
        return "File Name";
      default:
        return "Unknown";
    }
  }

  public String[] getOutputTypes() {
    return null;
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    String s = "<p>Overview: Write a NaiveBayesModel out as a PMML file."+
        "<p>Detailed Description: Write a NaiveBayesModel out in PMML format "+
        "complying with the PMML 2.0 DTD. The output file will be placed in the directory"+
        " where d2k was installed. " +
        "<p>Data Type Restrictions: A NaiveBayesModel must be the input to this module. "+
        "The NaiveBayesModel does not have to be prepared for visualization. " +
        "<p>Data Handling: The module does not destroy or modify the input data."+
        "<p>Scalability: The module creates a DOM for the NaiveBayesModel and "+
        "queries the model for several statistics.";
    return s;
  }

  public String getModuleName() {
    return "Write NaiveBayesModel as PMML";
  }

  public void doit() throws Exception {
    NaiveBayesModel nbm = (NaiveBayesModel) pullInput(0);
//    ExampleTable et = (ExampleTable) pullInput(1);
    String filename = (String) pullInput(1);

    writePMML(nbm, filename);
  }

  public static void writePMML(NaiveBayesModel nbm, String filename) throws Exception {
    // get the bin tree
    BinTree binTree = nbm.getBinTree();

    Document document = DocumentHelper.createDocument();
    document.addDocType(PMML, "pmml20.dtd", "pmml20.dtd");

    Element pmml = document.addElement(PMML);
    pmml.addAttribute("version", "2.0");

    Element header = pmml.addElement("Header");
    header.addAttribute("copyright", "NCSA ALG");
    header.addAttribute("description", "A naive bayes model");

    String[] inputNames = nbm.getInputColumnLabels();
    int[] inputTypes = nbm.getInputFeatureTypes();
    String[] outNames = nbm.getOutputColumnLabels();
    int[] outTypes = nbm.getOutputFeatureTypes();

/*    HashMap inputToIndexMap = new HashMap();
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
    }*/

    // do the data dictionary
    Element dictionary = pmml.addElement(DATA_DICT);
    dictionary.addAttribute(NO_FIELDS,
                            Integer.toString(inputNames.length +
                                             outNames.length));

    // for each of the inputs, create a DataField element
    for (int i = 0; i < inputNames.length; i++) {
      Element field = dictionary.addElement(DATA_FIELD);
      field.addAttribute(NAME, inputNames[i]);

      // if it is Scalar, mark the input as continuous
      /*Integer idx = (Integer) inputToIndexMap.get(inputNames[i]);
      if (idx == null) {
        throw new Exception(getAlias() + ": Could not find input column: " +
                            inputNames[i]);
      }*/

      //if (et.isColumnScalar(idx.intValue())) {
      if (nbm.getScalarInputs()[i]) {
        field.addAttribute(OPTYPE, CONTINUOUS);
      }

      // otherwise it is categorical.  mark it as such and list
      // all the values
      else {
        Element catAtt = field.addAttribute(OPTYPE, CATEGORICAL);
        // now get the unique values
        // !!!!!
        // String[] vals = nbm.getUniqueInputValues(i);

        // this is also the names of the bins for this input..
        String[] vals = binTree.getBinNames(inputNames[i]);

        //String[] vals = TableUtilities.uniqueValues(et, idx.intValue());
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

      // if it is Scalar, mark the input as continuous
/*      Integer idx = (Integer) outputToIndexMap.get(outNames[i]);
      if (idx == null) {
        throw new Exception(getAlias() + ": Could not find output column: " +
                            outNames[i]);
      }*/

      //if (et.isColumnScalar(idx.intValue())) {
      if (nbm.getScalarOutputs()[i]) {
        field.addAttribute(OPTYPE, CONTINUOUS);
      }

      // otherwise it is categorical.  mark it as such and list
      // all the values
      else {
        Element catAtt = field.addAttribute(OPTYPE, CATEGORICAL);
        // now get the unique values
//                String[] vals = dtm.getUniqueOutputValues();
        String[] vals = binTree.getClassNames();
//        String[] vals = TableUtilities.uniqueValues(et, idx.intValue());
        for (int j = 0; j < vals.length; j++) {
          Element e = catAtt.addElement(VALUE_ELEMENT);
          e.addAttribute(VALUE, vals[j]);
        }
      }
    }
    // finished with the dictionary.

    Element modelElement = pmml.addElement(NBM);
    modelElement.addAttribute("modelName", nbm.getName());
    modelElement.addAttribute("functionName", "classification");
    modelElement.addAttribute("threshold", "0.001");

    // add the mining schema
    Element schema = modelElement.addElement(MINING_SCHEMA);
    for (int i = 0; i < inputNames.length; i++) {
      Element field = schema.addElement(MINING_FIELD);
      field.addAttribute(NAME, inputNames[i]);
    }

    for (int i = 0; i < outNames.length; i++) {
      Element field = schema.addElement(MINING_FIELD);
      field.addAttribute(NAME, outNames[i]);
      field.addAttribute(USAGE_TYPE, PREDICTED);
    }


    Element bayesInputs = modelElement.addElement(BAYES_INPUTS);
    String[] outputs = nbm.getClassNames();

    // for each input
    for (int i = 0; i < inputNames.length; i++) {
      String inputName = inputNames[i];
      Element bayesInput = bayesInputs.addElement(BAYES_INPUT);
      bayesInput.addAttribute(FIELD_NAME, inputName);

      // now add a PairCounts for each bin...

//      Integer idx = (Integer) inputToIndexMap.get(inputNames[i]);
      // if it is nominal....
      //if (!et.isColumnScalar(idx.intValue())) {
      if (!nbm.getScalarInputs()[i]) {
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

        Element derivedField = bayesInput.addElement(DERIVED_FIELD);
        Element discretize = derivedField.addElement(DISCRETIZE);
        discretize.addAttribute("field", inputName);

        // for each bin
        for (int j = 0; j < binNames.length; j++) {
          Element bin = discretize.addElement(DISCRETIZE_BIN);
          bin.addAttribute("binValue", binNames[j]);

          Element interval = bin.addElement(INTERVAL);

          boolean includeLower = binTree.includeLowerBound(
              outputs[0], inputName, binNames[j]);
          boolean includeUpper = binTree.includeUpperBound(
              outputs[0], inputName, binNames[j]);

          if (includeLower && includeUpper) {
            interval.addAttribute(CLOSURE, OPEN_OPEN);
          }
          else if (includeLower && !includeUpper) {
            interval.addAttribute(CLOSURE, OPEN_CLOSED);
          }
          else if (!includeLower && includeUpper) {
            interval.addAttribute(CLOSURE, CLOSED_OPEN);
          }
          else if (!includeLower && !includeUpper) {
            interval.addAttribute(CLOSURE, CLOSED_CLOSED);

          }
          double lowerBound = binTree.getLowerBound(outputs[0],
              inputName, binNames[j]);
          double upperBound = binTree.getUpperBound(outputs[0],
              inputName, binNames[j]);

          interval.addAttribute(LEFT_MARGIN, Double.toString(lowerBound));
          interval.addAttribute(RIGHT_MARGIN, Double.toString(upperBound));
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
      tvc.addAttribute(VALUE, outputs[i]);
      tvc.addAttribute(COUNT, Integer.toString(tally));
    }

    XMLWriter writer = new XMLWriter(new FileWriter(filename),
                                     OutputFormat.createPrettyPrint());
    writer.write(document);
    writer.flush();
    writer.close();
  }
}
// QA comments Anca
// added more specifics in moduleInfo
