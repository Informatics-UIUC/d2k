package ncsa.d2k.modules.core.discovery.ruleassociation;

import java.io.*;
import java.util.*;

import ncsa.d2k.core.modules.*;

import org.dom4j.*;
import org.dom4j.io.*;

public class WriteRuleAssocPMML extends OutputModule implements RulePMMLTags {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.projects.clutter.RuleTable", "java.lang.String"};
    return in;
  }

  public String getInputInfo(int i) {
    if (i == 0)
      return "The RuleTable";
    else
      return "Filename to write PMML to.";
  }

  public String[] getOutputTypes() {
    return null;
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getInputName(int i) {
    if (i == 0)
     return "Rule Table";
    else
      return "PMML File";
  }

  public String getOutputName(int index) {
    return "";
  }

  public String getModuleInfo() {
    String s = "<p>Overview: Write a RuleAssociationModel out as a PMML file."+
               "<p>Detailed Description: Write a RuleAssociationModel out in PMML format "+
               "complying with the PMML 2.0 DTD."+
               "<p>Data Type Restrictions: A RuleTable must be the input to this module."+
               "<p>Data Handling: The module does not destroy or modify the input data."+
               "<p>Scalability: The module creates a DOM for the AssociationRuleModel and "+
               "queries the model for several statistics.";
    return s;
  }

  public void doit() throws Exception {
    RuleTable rt = (RuleTable) pullInput(0);
    String fn = (String) pullInput(1);
    writePMML(rt, fn);
  }

  public static void writePMML(RuleTable rt, String fileName) {

    Document document = DocumentHelper.createDocument();
    document.addDocType("PMML", "http://www.dmg.org/v2-0/pmml_v2_0.dtd",
                        "http://www.dmg.org/v2-0/pmml_v2_0.dtd");

    // Root
    Element root = document.addElement("PMML");
    root.addAttribute("version", "2.0");

    // Header
    Element header = root.addElement("Header");
    header.addAttribute("copyright", "NCSA ALG");
    header.addAttribute("description", "association rules");

    // Data dictionary
    Element dataDictionary = root.addElement(DATA_DICT);
    Element datafield = dataDictionary.addElement(DATA_FIELD);

    datafield.addAttribute(NAME, "transaction");
    datafield.addAttribute(OPTYPE, CATEGORICAL);

    datafield = dataDictionary.addElement(DATA_FIELD);
    datafield.addAttribute(NAME, "item");
    datafield.addAttribute(OPTYPE, CATEGORICAL);

    // Association model
    List items = rt.getNamesList();
    List itemSets = rt.getItemSetsList();

    Element assocModel = root.addElement(ASSOC_MODEL);
    assocModel.addAttribute(FUNCTION_NAME, "associationRules");
    assocModel.addAttribute(NUM_TRANS,
                            Integer.toString(rt.getNumberOfTransactions()));
    assocModel.addAttribute(MIN_SUP,
                            Double.toString(rt.getMinimumSupport()));
    assocModel.addAttribute(MIN_CON,
                            Double.toString(rt.getMinimumConfidence()));
    assocModel.addAttribute(NUM_ITEM,
                            Integer.toString(items.size()));
    assocModel.addAttribute(NUM_ITEMSETS,
                            Integer.toString(itemSets.size()));
    assocModel.addAttribute(NUM_RULE,
                            Integer.toString(rt.getNumRules()));
    //Mining schema
    Element miningSchema = assocModel.addElement(MINING_SCHEMA);
    Element miningField = miningSchema.addElement(MINING_FIELD);

    miningField.addAttribute(NAME, "transaction");

    miningField = miningSchema.addElement(MINING_FIELD);
    miningField.addAttribute(NAME, "item");

    // Association items
    for(int i = 0; i < items.size(); i++) {
      Element assocItem = assocModel.addElement(ITEM);
      assocItem.addAttribute(ID, Integer.toString(i));
      assocItem.addAttribute(VALUE, (String)items.get(i));
    }

    // Association itemsets
    for(int i = 0; i < itemSets.size(); i++) {
      FreqItemSet fis = (FreqItemSet) itemSets.get(i);

      Element set = assocModel.addElement(ITEMSET);
      set.addAttribute(ID, Integer.toString(i));
      set.addAttribute(SUPPORT, Integer.toString((int)fis.support));

      int[] vals = fis.items.toNativeArray();
      for(int j = 0; j < vals.length; j++) {
        Element assocItemRef = set.addElement(ITEMREF);
        assocItemRef.addAttribute(ITEM_REF, Integer.toString(vals[j]));
      }
    }

    // Association rules
    for(int i = 0; i < rt.getNumRules(); i++) {
      int hd = rt.getRuleAntecedentID(i);
      int bd = rt.getRuleConsequentID(i);
      double conf = rt.getConfidence(i);
      double supp = rt.getSupport(i);

      Element assocRule = assocModel.addElement(ASSOC_RULE);
      assocRule.addAttribute(SUPPORT, Double.toString(supp));
      assocRule.addAttribute(CONFIDENCE, Double.toString(conf));
      assocRule.addAttribute(ANTECEDENT, Integer.toString(hd));
      assocRule.addAttribute(CONSEQUENT, Integer.toString(bd));
    }

    try {
      XMLWriter writer = new XMLWriter(new FileWriter(fileName),
                                       OutputFormat.createPrettyPrint());
      writer.write(document);
      writer.flush();
      writer.close();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
}