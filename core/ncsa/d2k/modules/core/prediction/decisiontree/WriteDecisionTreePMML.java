package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.core.modules.*;

import java.util.*;
import java.io.*;

import org.dom4j.*;
import org.dom4j.io.*;
import org.dom4j.util.*;

/**
 * Write the contents of a DecisionTreeModel out as PMML.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class WriteDecisionTreePMML
    extends OutputModule
    implements DecisionTreePMMLTags {

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel",
        "java.lang.String"};
    return in;
  }

  public String[] getOutputTypes() {
    return null;
  }

  public String getInputInfo(int i) {
    if (i == 0) {
      return "A DecisionTreeModel";
    }
    else {
      return "The file name";
    }
  }

  public String getInputName(int i) {
    if (i == 0) {
      return "Decision Tree Model";
    }
    else {
      return "File Name";
    }
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getOutputName(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "write out the pmml.";
  }

  public void doit() throws Exception {
    DecisionTreeModel dtm = (DecisionTreeModel) pullInput(0);
    String fn = (String) pullInput(1);
    writePMML(dtm, fn);
  }

  /**
   * Write out the PMML for a DecisionTreeModel.
   * @param dtm
   * @param fileName
   */
  public static void writePMML(DecisionTreeModel dtm, String fileName) throws
      Exception {

    Document document = DocumentHelper.createDocument();
    document.addDocType("PMML", "pmml20.dtd", "pmml20.dtd");

    Element root = document.addElement(PMML);
    root.addAttribute("version", "2.0");

    Element header = root.addElement("Header");
    header.addAttribute("copyright", "NCSA ALG");
    header.addAttribute("description", "Tree model");

    String[] inputNames = dtm.getInputColumnLabels();
    String[] outNames = dtm.getOutputColumnLabels();
    String[] outValues = dtm.getUniqueOutputValues();
    Element dictionary = root.addElement(DATA_DICT);
    dictionary.addAttribute("numberOfFields",
                            Integer.toString(inputNames.length +
                                             outNames.length));

    // for each of the inputs, create a DataField element
    for (int i = 0; i < inputNames.length; i++) {
      Element field = dictionary.addElement(DATA_FIELD);
      field.addAttribute(NAME, inputNames[i]);

      // if it is Scalar, mark the input as continuous
      if (dtm.scalarInput(i)) {
        field.addAttribute(OPTYPE, CONTINUOUS);

        // otherwise it is categorical.  mark it as such and list
        // all the values
      }
      else {
        Element catAtt = field.addAttribute(OPTYPE, CATEGORICAL);
        // now get the unique values
        String[] vals = dtm.getUniqueInputValues(i);
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
      if (dtm.scalarOutput(i)) {
        field.addAttribute(OPTYPE, CONTINUOUS);

        // otherwise it is categorical.  mark it as such and list
        // all the values
      }
      else {
        Element catAtt = field.addAttribute(OPTYPE, CATEGORICAL);
        // now get the unique values
        String[] vals = dtm.getUniqueOutputValues();
        for (int j = 0; j < vals.length; j++) {
          Element e = catAtt.addElement(VALUE_ELEMENT);
          e.addAttribute(VALUE, vals[j]);
        }
      }
    }
    // finished with the dictionary.

    Element treeModel = root.addElement(TREE_MODEL);
    treeModel.addAttribute("functionName", "classification");
    Element schema = treeModel.addElement(MINING_SCHEMA);
    for (int i = 0; i < inputNames.length; i++) {
      Element field = schema.addElement(MINING_FIELD);
      field.addAttribute(NAME, inputNames[i]);
    }

    for (int i = 0; i < outNames.length; i++) {
      Element field = schema.addElement(MINING_FIELD);
      field.addAttribute(NAME, outNames[i]);
      field.addAttribute(USAGE_TYPE, PREDICTED);
    }

    // now we must make the TreeModel element
    DecisionTreeNode treeRoot = dtm.getRoot();
    Element treeRootElement = treeModel.addElement("Node");
    treeRootElement.addAttribute(SCORE, "xxx");
    treeRootElement.addAttribute(RECORD_COUNT,
                                 Integer.toString(treeRoot.getTotal()));
    treeRootElement.addElement("True");
    try {
      for (int value = 0; value < outValues.length; value++) {
        String outputvalue = outValues[value];
        int tally = treeRoot.getOutputTally(outputvalue);
        Element distribution = treeRootElement.addElement(SCORE_DISTRIBUTION);
        distribution.addAttribute(VALUE, outputvalue);
        distribution.addAttribute("recordCount", Integer.toString(tally));
      }
    }
    catch (Exception exception) {
      System.out.println(exception);
    }
    writeNode(treeRoot, treeRootElement, outValues);

    //try {
    XMLWriter writer = new XMLWriter(new FileWriter(fileName),
                                     OutputFormat.createPrettyPrint());
    writer.write(document);
    writer.flush();
    writer.close();

    /*}
         catch(Exception e) {
      e.printStackTrace();
         }*/

  }

  private static void writeNode(DecisionTreeNode nde, Element rtElement,
                                String[] outValues) {
    /*
         if(nde.isLeaf()) {
         // make the leaf tag
      String label = nde.getLabel();
      Element leaf = rtElement.addElement("Node");
      leaf.addAttribute("score", label);
      leaf.addElement("True");
      return;
         }
     */

    // Element nodeTag;
    for (int i = 0; i < nde.getNumChildren(); i++) {
      // otherwise
      // make the node tag

      DecisionTreeNode childNde = nde.getChild(i);
      Element newNode = rtElement.addElement("Node");
      newNode.addAttribute("recordCount", Integer.toString(childNde.getTotal()));

      //newNode.addAttribute("score", "xxx");
      String label = nde.getBranchLabel(i);
      //System.out.println("LABEL: "+label);
      //System.out.println("CHILD: "+childNde);
      //if(childNde instanceof NumericDecisionTreeNode) {
      // now make a tag for the predicate
      NumericSet ns = new NumericSet(label);
      //System.out.println(ns.left);
      //System.out.println(ns.op);
      //System.out.println(ns.right);
      Element pred = newNode.addElement("SimplePredicate");
      pred.addAttribute("field", ns.left);
      if (ns.op.equals(LESS_THAN)) {
        pred.addAttribute("operator", "lessThan");
      }
      else if (ns.op.equals(GREATER_THAN_EQUAL_TO)) {
        pred.addAttribute("operator", "greaterOrEqual");
      }
      else {
        pred.addAttribute("operator", "equal");
      }
      pred.addAttribute(VALUE, ns.right);
      /*}
             else {
             NumericSet cs = new CategoricalSet(label);
             Element pred = newNode.addElement("SimplePredicate");
             pred.addAttribute("field", cs.left);
             pred.addAttribute("operator", "equal");
             pred.addAttribute("value", cs.right);
             }*/

      try {
        for (int value = 0; value < outValues.length; value++) {
          String outputvalue = outValues[value];
          int tally = childNde.getOutputTally(outputvalue);
          Element distribution = newNode.addElement("ScoreDistribution");
          distribution.addAttribute(VALUE, outputvalue);
          distribution.addAttribute("recordCount", Integer.toString(tally));
        }
      }
      catch (Exception exception) {
        System.out.println(exception);
      }

      if (childNde.isLeaf()) {
        // make the leaf tag
        newNode.addAttribute("score", childNde.getLabel());
        //Element leaf = rtElement.addElement("Node");
        //leaf.addAttribute("score", label);
        //leaf.addElement("True");
        //return;
      }
      else {
        newNode.addAttribute("score", "xxx");
        writeNode(childNde, newNode, outValues);
      }
    }
  }

  private static class NumericSet {
    String left;
    String op;
    String right;

    NumericSet(String s) {
      int index = s.indexOf(LESS_THAN);
      if (index != -1) {
        left = s.substring(0, index);
        op = s.substring(index, index + LESS_THAN.length());
        right = s.substring(index + LESS_THAN.length());
      }
      else {
        index = s.indexOf(GREATER_THAN_EQUAL_TO);
        if (index != -1) {
          left = s.substring(0, index);
          op = s.substring(index, index + GREATER_THAN_EQUAL_TO.length());
          right = s.substring(index + GREATER_THAN_EQUAL_TO.length());
        }
        else {
          index = s.indexOf(EQUALS);
          left = s.substring(0, index);
          op = s.substring(index, index + EQUALS.length());
          right = s.substring(index + EQUALS.length());
        }
      }
    }
  }

  private static final String LESS_THAN = " < ";
  private static final String GREATER_THAN_EQUAL_TO = " >= ";
  private static final String EQUALS = " = ";
}