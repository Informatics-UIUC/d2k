package ncsa.d2k.modules.core.discovery.ruleassociation;

import java.io.*;
import java.util.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.discovery.ruleassociation.*;
import org.dom4j.*;
import org.dom4j.io.*;

/**
 *
 */
public class WriteRuleAssocPMML extends OutputModule {

    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.projects.clutter.RuleTable", "java.lang.String"};
        return in;
    }

    public String getInputInfo(int i) {
        if(i == 0)
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

    public String getModuleInfo() {
        return "Save a RuleTable to a PMML format.";
    }

    public void doit() throws Exception {
        RuleTable rt = (RuleTable)pullInput(0);
        String fn = (String)pullInput(1);
        writePMML(rt, fn);
    }

    public static void writePMML(RuleTable rt, String fileName) {
        // make assoc items
        List items = rt.getNamesList();
        List itemSets = rt.getItemSetsList();

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("PMML");
        root.addAttribute("version", "1.1");

        Element header = root.addElement("Header");
        header.addAttribute("copyright", "NCSA ALG");
        header.addAttribute("description", "association rules");

        // make data dictionary
        Element dataDictionary = root.addElement("DataDictionary");

        // make assoc input stats
        Element assocInputStats = root.addElement("AssocInputStats");
        assocInputStats.addAttribute("numberOfTransactions",
                                     Integer.toString(rt.getNumberOfTransactions()));
        assocInputStats.addAttribute("minimumSupport",
                                     Double.toString(rt.getMinimumSupport()));
        assocInputStats.addAttribute("minimumConfidence",
                                     Double.toString(rt.getMinimumConfidence()));
        assocInputStats.addAttribute("numberOfItems",
                                     Integer.toString(items.size()));
        assocInputStats.addAttribute("numberOfItemsets",
                                     Integer.toString(itemSets.size()));
        assocInputStats.addAttribute("numberOfRules",
                                     Integer.toString(rt.getNumRules()));

        // make assoc items
        for(int i = 0; i < items.size(); i++) {
            Element assocItem = root.addElement(ASSOC_ITEM);
            assocItem.addAttribute(ID, Integer.toString(i));
            assocItem.addAttribute(VALUE, (String)items.get(i));
        }

        // make assoc itemsets
        for(int i = 0; i < itemSets.size(); i++) {
            FreqItemSet fis = (FreqItemSet)itemSets.get(i);

            Element itemset = root.addElement(ITEM_SET);
            itemset.addAttribute(ID, Integer.toString(i));
            itemset.addAttribute(SUPPORT, Integer.toString((int)fis.support));
            int[] vals = fis.items.toNativeArray();
            for(int j = 0; j < vals.length; j++) {
                Element assocItemRef = itemset.addElement(ASSOC_ITEM_REF);
                assocItemRef.addAttribute(ITEM_REF, Integer.toString(vals[j]));
            }
        }

        // make assoc rules
        for(int i = 0; i < rt.getNumRules(); i++) {
            int hd = rt.getRuleAntecedentID(i);
            int bd = rt.getRuleConsequentID(i);
            double conf = rt.getConfidence(i);
            double supp = rt.getSupport(i);

            Element assocRule = root.addElement(ASSOC_RULE);
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

    private static final String CONSEQUENT = "consequent";
    private static final String ANTECEDENT = "antecedent";
    private static final String SUPPORT = "support";
    private static final String CONFIDENCE = "confidence";
    private static final String ID = "id";
    private static final String VALUE = "value";
    private static final String ITEM_SET = "AssocItemSet";
    private static final String ASSOC_ITEM = "AssocItem";
    private static final String ASSOC_RULE = "AssocRule";
    private static final String ASSOC_ITEM_REF = "AssocItemRef";
    private static final String ITEM_REF = "itemRef";
}
