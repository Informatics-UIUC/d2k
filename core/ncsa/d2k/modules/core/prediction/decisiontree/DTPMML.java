package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.core.modules.*;

import java.util.*;
import java.io.*;

import org.dom4j.*;
import org.dom4j.io.*;
import org.dom4j.util.*;

public class DTPMML extends DataPrepModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
    return in;
  }

  public String[] getOutputTypes() {
    return null;
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getInputName(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getOutputName(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  public void doit() {
    DecisionTreeModel dtm = (DecisionTreeModel) pullInput(0);

    Document document = DocumentHelper.createDocument();
    document.addDocType("PMML", "pmml20.dtd", "pmml20.dtd");

    Element root = document.addElement("PMML");
    root.addAttribute("version", "2.0");

    Element header = root.addElement("Header");
    header.addAttribute("copyright", "NCSA ALG");
    header.addAttribute("description", "a tree model");

    String[] inputNames = dtm.getInputColumnLabels();
    String[] inputTypes = dtm.getInputFeatureTypes();
    String[] outNames = dtm.getOutputColumnLabels();
    String[] outTypes = dtm.getOutputFeatureTypes();
    Element dictionary = root.addElement("DataDictionary");
    dictionary.addAttribute("numberOfFields", Integer.toString(inputNames.length+outNames.length));

    // for each of the inputs, create a DataField element
    for(int i = 0; i < inputNames.length; i++) {
      Element field = dictionary.addElement("DataField");
      field.addAttribute("name", inputNames[i]);

      // if it is Scalar, mark the input as continuous
      if(inputTypes[i].equals("Scalar"))
        field.addAttribute("optype", "continuous");

      // otherwise it is categorical.  mark it as such and list
      // all the values
      else {
        Element catAtt = field.addAttribute("optype", "categorical");
        // now get the unique values
        String[] vals = dtm.getUniqueInputValues(i);
        for(int j = 0; j < vals.length; j++) {
          Element e = catAtt.addElement("Value");
          e.addAttribute("value", vals[j]);
        }
      }
    }

    // for each of the outputs, create a DataField element
    for(int i = 0; i < outNames.length; i++) {
      Element field = dictionary.addElement("DataField");
      field.addAttribute("name", outNames[i]);

      // if it is scalar, mark the output as continous
      if(outTypes[i].equals("Scalar"))
        field.addAttribute("optype", "continuous");

      // otherwise it is categorical.  mark it as such and list
      // all the values
      else {
        Element catAtt = field.addAttribute("optype", "categorical");
        // now get the unique values
        String[] vals = dtm.getUniqueOutputValues();
        for(int j = 0; j < vals.length; j++) {
          Element e = catAtt.addElement("Value");
          e.addAttribute("value", vals[j]);
        }
      }
    }
    // finished with the dictionary.

    Element treeModel = root.addElement("TreeModel");
    treeModel.addAttribute("functionName", "classification");
    Element schema = treeModel.addElement("MiningSchema");
    for(int i = 0; i < inputNames.length; i++) {
      Element field = schema.addElement("MiningField");
      field.addAttribute("name", inputNames[i]);
    }

    for(int i = 0; i < outNames.length; i++) {
      Element field = schema.addElement("MiningField");
      field.addAttribute("name", outNames[i]);
      field.addAttribute("usageType", "predicted");
    }

    // now we must make the TreeModel element
    DecisionTreeNode treeRoot = dtm.getRoot();
    Element treeRootElement = treeModel.addElement("Node");
    treeRootElement.addAttribute("score", "xxx");
    treeRootElement.addElement("True");
    writeNode(treeRoot, treeRootElement);

    try {
      XMLWriter writer = new XMLWriter(new FileWriter("test.pmml"),
                                       OutputFormat.createPrettyPrint());
      writer.write(document);
      writer.flush();
      writer.close();

    }
    catch(Exception e) {
      e.printStackTrace();
    }

  }

  private void writeNode(DecisionTreeNode nde, Element rtElement) {
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
    for(int i = 0; i < nde.getNumChildren(); i++) {
      // otherwise
      // make the node tag

      DecisionTreeNode childNde = nde.getChild(i);
      Element newNode = rtElement.addElement("Node");

      //newNode.addAttribute("score", "xxx");
      String label = nde.getBranchLabel(i);
      System.out.println("LABEL: "+label);
      System.out.println("CHILD: "+childNde);
      //if(childNde instanceof NumericDecisionTreeNode) {
      // now make a tag for the predicate
      NumericSet ns = new NumericSet(label);
      //System.out.println(ns.left);
      //System.out.println(ns.op);
      //System.out.println(ns.right);
      Element pred = newNode.addElement("SimplePredicate");
      pred.addAttribute("field", ns.left);
      if(ns.op.equals(LESS_THAN))
        pred.addAttribute("operator", "lessThan");
      else if(ns.op.equals(GREATER_THAN_EQUAL_TO))
        pred.addAttribute("operator", "greaterOrEqual");
      else
        pred.addAttribute("operator", "equal");
      pred.addAttribute("value", ns.right);
            /*}
            else {
                NumericSet cs = new CategoricalSet(label);
                Element pred = newNode.addElement("SimplePredicate");
                pred.addAttribute("field", cs.left);
                pred.addAttribute("operator", "equal");
                pred.addAttribute("value", cs.right);
            }*/

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
        writeNode(childNde, newNode);
      }
    }
  }

  private class NumericSet {
    String left;
    String op;
    String right;

    NumericSet(String s) {
      int index = s.indexOf(LESS_THAN);
      if(index != -1) {
        left = s.substring(0, index);
        op = s.substring(index, index+LESS_THAN.length());
        right = s.substring(index+LESS_THAN.length());
      }
      else {
        index = s.indexOf(GREATER_THAN_EQUAL_TO);
        if(index != -1) {
          left = s.substring(0, index);
          op = s.substring(index, index+GREATER_THAN_EQUAL_TO.length());
          right = s.substring(index+GREATER_THAN_EQUAL_TO.length());
        }
        else {
          index = s.indexOf(EQUALS);
          left = s.substring(0, index);
          op = s.substring(index, index+EQUALS.length());
          right = s.substring(index+EQUALS.length());
        }
      }
    }
  }

  private static final String LESS_THAN = " < ";
  private static final String GREATER_THAN_EQUAL_TO = " >= ";
  private static final String EQUALS = " = ";
}