package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;

import org.dom4j.*;
import org.dom4j.io.*;

import java.util.*;

public class PMMLDT extends InputModule {
  HashMap datafields;

  public void doit() throws Exception {
    String pmml = (String) pullInput(0);
    datafields = new HashMap();

    SAXReader reader = new SAXReader();
    Document document = reader.read(pmml);

    Node compound = document.selectSingleNode("//CompoundPredicate");
    if (compound != null)
      throw new Exception("Compound predicates not supported");

    Node set = document.selectSingleNode("//SimpleSetPredicate");
    if (set != null)
      throw new Exception("Simple predicate sets not supported");

    Element dictionary = (Element) document.selectSingleNode("/PMML/DataDictionary");
    for (int count=0; count < dictionary.nodeCount(); count++) {
      Node node = dictionary.node(count);
      if (node.getName().equals("DataField")) {
        Element field = (Element) node;
        String name = field.valueOf("@name");
        String type = field.valueOf("@optype");
        datafields.put(name, type);
      }
    }

    Element element = (Element) document.selectSingleNode("/PMML/TreeModel/Node");
    MarkupNode markupnode = new MarkupNode(element);
    walk(element, markupnode);
    attributes(markupnode);

    DecisionTreeNode decisionnode;
    if (markupnode.isContinuous())
      decisionnode = new NumericDecisionTreeNode(markupnode.childfield, null);
    else
      decisionnode = new CategoricalDecisionTreeNode(markupnode.childfield, null);
    build(markupnode, decisionnode);

    pushOutput(decisionnode, 0);
  }

  void walk(Element element, MarkupNode markupnode) {
    System.out.println(element.getUniquePath());

    for (int count=0; count < element.nodeCount(); count++) {
      Node childnode = element.node(count);

      if (childnode instanceof Element) {
        Element childelement = (Element) childnode;

        if (childelement.getName().equals("Node")) {
          MarkupNode childmarkup = new MarkupNode(childelement);
          childmarkup.parent = markupnode;
          markupnode.children.add(childmarkup);
          walk(childelement, childmarkup);
          attributes(childmarkup);
        }
      }
    }
  }

  void build(MarkupNode markupnode, DecisionTreeNode decisionnode) throws Exception {
    Object[] outputs = (Object[]) markupnode.tallies.keySet().toArray();
    for (int index=0; index < outputs.length; index++) {
      Integer count = (Integer) markupnode.tallies.get((String) outputs[index]);
      decisionnode.setOutputTally((String) outputs[index], count.intValue());
    }

    if (markupnode.isLeaf()) {
      System.out.println("Leaf, " + markupnode.element.getUniquePath());
      return;
    }

    for (int child=0; child < markupnode.children.size(); child++) {
      System.out.println("Node, " + markupnode.element.getUniquePath());

      if (markupnode.isContinuous()) {
        if (markupnode.children.size() > 2)
          throw new Exception("Only binary split values supported for continuous data");

        MarkupNode leftmarkup = (MarkupNode) markupnode.children.get(child++);
        MarkupNode rightmarkup = (MarkupNode) markupnode.children.get(child);

        DecisionTreeNode leftdecision;
        if (leftmarkup.isContinuous())
          leftdecision = new NumericDecisionTreeNode(leftmarkup.childfield, decisionnode);
        else
          leftdecision = new CategoricalDecisionTreeNode(leftmarkup.childfield, decisionnode);

        DecisionTreeNode rightdecision;
        if (rightmarkup.isContinuous())
          rightdecision = new NumericDecisionTreeNode(rightmarkup.childfield, decisionnode);
        else
          rightdecision = new CategoricalDecisionTreeNode(rightmarkup.childfield, decisionnode);

        double split = Double.parseDouble(leftmarkup.value);
        String leftlabel = leftmarkup.field + " < " + split;
        String rightlabel = leftmarkup.field + " >= " + split;

        decisionnode.addBranches(split, leftlabel, leftdecision, rightlabel, rightdecision);

        build(leftmarkup, leftdecision);
        build(rightmarkup, rightdecision);
      }
      else {
        MarkupNode childmarkup = (MarkupNode) markupnode.children.get(child);

        DecisionTreeNode childdecision;
        if (childmarkup.isContinuous())
          childdecision = new NumericDecisionTreeNode(childmarkup.childfield, decisionnode);
        else
          childdecision = new CategoricalDecisionTreeNode(childmarkup.childfield, decisionnode);

        decisionnode.addBranch(childmarkup.value, childdecision);
        build(childmarkup, childdecision);
      }
    }
  }

  void attributes(MarkupNode markupnode) {
    Element element = markupnode.element;
    Element predicate = (Element) element.selectSingleNode(element.getUniquePath() + "/SimplePredicate");

    markupnode.score = element.valueOf("@score");

    if (predicate != null) {
      markupnode.field = predicate.valueOf("@field");
      markupnode.operator = predicate.valueOf("@operator");
      markupnode.value = predicate.valueOf("@value");
    }

    if (!markupnode.isLeaf()) {
      MarkupNode node = (MarkupNode) markupnode.children.get(0);
      element = node.element;

      predicate = (Element) element.selectSingleNode(element.getUniquePath() + "/SimplePredicate");
      markupnode.childfield = predicate.valueOf("@field");
    }
    else
      markupnode.childfield = markupnode.score;

    List list = element.selectNodes(element.getUniquePath() + "/ScoreDistribution");
    for (int index=0; index < list.size(); index++) {
      Element distribution = (Element) list.get(index);
      String output = distribution.valueOf("@value");
      int count = Integer.parseInt(distribution.valueOf("@recordCount"));
      markupnode.tallies.put(output, new Integer(count));
    }
  }

  private class MarkupNode {
    Element element;
    MarkupNode parent;
    ArrayList children;

    String field, operator, value;
    String score;

    String childfield;

    HashMap tallies;

    private MarkupNode(Element element) {
      this.element = element;
      children = new ArrayList();
      tallies = new HashMap();
    }

    private boolean isLeaf() {
      return children.size() == 0;
    }

    private boolean isContinuous() {
      if (isLeaf())
        return datafields.get(field).equals("continuous");
      else
        return datafields.get(childfield).equals("continuous");
    }
  }

  public String getModuleInfo() {
    return "";
  }

  public String getInputInfo(int index) {
    return "";
  }

  public String getOutputInfo(int index) {
    return "";
  }

  public String[] getInputTypes() {
    String[] types = {"java.lang.String"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode"};
    return types;
  }

  public String getInputName(int index) {
    return "";
  }

  public String getOutputName(int index) {
    return "";
  }
}