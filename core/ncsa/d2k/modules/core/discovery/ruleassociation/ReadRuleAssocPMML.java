package ncsa.d2k.modules.core.discovery.ruleassociation;

import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;
import org.dom4j.util.*;

import gnu.trove.*;

public class ReadRuleAssocPMML extends InputModule implements RulePMMLTags {

    public String[] getInputTypes() {
        String[] in = {"java.lang.String"};
        return in;
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.discovery.ruleassociation.RuleTable"};
        return out;
    }

    public String getInputInfo(int i) {
        return "";
    }

    public String getOutputInfo(int i) {
        return "";
    }

    public String getModuleInfo() {
        return "";
    }

    public void doit() throws Exception {
        String filename = (String)pullInput(0);

        SAXReader reader;
        XMLErrorHandler errorHandler;
        Document document;

        try {
            reader = new SAXReader("org.apache.xerces.parsers.SAXParser", false);
        }
        catch(Exception e) {
            throw e;
        }

        try {
            errorHandler = new XMLErrorHandler();
            reader.setErrorHandler(errorHandler);
        }
        catch(Exception e) {
            throw e;
        }

        try {
            File tempFile = new File(filename);
            document = reader.read(tempFile);
        }
        catch(Exception e) {
            throw e;
        }

        Element root = document.getRootElement();
        String version = root.attribute("version").getValue();
        if(Double.parseDouble(version) != 2)
            throw new Exception("PMML version 2.0 requried.");

        Element stats = root.element(ASSOC_MODEL);

        String functionName = stats.attribute("functionName").getValue();
        int numberOfTransactions = Integer.parseInt(stats.attribute(NUM_TRANS).getValue());
        double minimumSupport = Double.parseDouble(stats.attribute(MIN_SUP).getValue());
        double minimumConfidence = Double.parseDouble(stats.attribute(MIN_CON).getValue());
        int numberOfItems = Integer.parseInt(stats.attribute(NUM_ITEM).getValue());
        int numberOfItemsets = Integer.parseInt(stats.attribute(this.NUM_ITEMSETS).getValue());
        int numberOfRules = Integer.parseInt(stats.attribute(this.NUM_RULE).getValue());

        String[] items = new String[numberOfItems];
        FreqItemSet[] fis = new FreqItemSet[numberOfItemsets];

        int[] antecedents = new int[numberOfRules];
        int[] consequents = new int[numberOfRules];
        double[] support = new double[numberOfRules];
        double[] confidence = new double[numberOfRules];

        // read in the items
        Iterator itemIterator = stats.elementIterator(ITEM);
        while(itemIterator.hasNext()) {
            Element currentItem = (Element)itemIterator.next();
            int id = Integer.parseInt(currentItem.attribute(ID).getValue());
            String value = currentItem.attribute(VALUE).getValue();
            items[id] = value;
        }

        // read in the item sets
        Iterator itemsetIterator = stats.elementIterator(ITEMSET);
        while(itemsetIterator.hasNext()) {
            Element currentItemset = (Element)itemsetIterator.next();
            int id = Integer.parseInt(currentItemset.attribute(ID).getValue());
            Attribute sup = currentItemset.attribute(SUPPORT);
            double supp = 0;
            if(sup != null)
                supp = Double.parseDouble(sup.getValue());

            TIntArrayList itms = new TIntArrayList();
            Iterator itemrefIter = currentItemset.elementIterator(ITEMREF);
            while(itemrefIter.hasNext()) {
                Element ir = (Element)itemrefIter.next();
                int ref = Integer.parseInt(ir.attribute(ITEM_REF).getValue());
                itms.add(ref);
            }

            // now make a new FreqItemSet
            FreqItemSet is = new FreqItemSet();
            is.numberOfItems = itms.size();
            is.items = itms;
            is.support = supp;
            fis[id] = is;
        }

        int idx = 0;
        Iterator ruleIterator = stats.elementIterator(ASSOC_RULE);
        while(ruleIterator.hasNext()) {
            Element rule = (Element)ruleIterator.next();
            double sup = Double.parseDouble(rule.attribute(SUPPORT).getValue());
            double conf = Double.parseDouble(rule.attribute(CONFIDENCE).getValue());
            int ant = Integer.parseInt(rule.attribute(ANTECEDENT).getValue());
            int cons = Integer.parseInt(rule.attribute(CONSEQUENT).getValue());
            support[idx] = sup;
            confidence[idx] = conf;
            antecedents[idx] = ant;
            consequents[idx] = cons;
            idx++;
        }
        IntColumn ac = new IntColumn(antecedents);
        ac.setLabel("Head");
        IntColumn cc = new IntColumn(consequents);
        cc.setLabel("Body");
        DoubleColumn sc = new DoubleColumn(support);
        sc.setLabel("Support");
        DoubleColumn conc = new DoubleColumn(confidence);
        conc.setLabel("Confidence");

        Column[] cols = {ac, cc, sc, conc};
        TableImpl ti = new MutableTableImpl(cols);

        ArrayList names = new ArrayList(items.length);
        for(int i = 0; i < items.length; i++)
            names.add(items[i]);

        ArrayList sets = new ArrayList(fis.length);
        for(int i = 0; i < fis.length; i++) {
            sets.add(fis[i]);
        }

        RuleTable rt = new RuleTable(ti, minimumConfidence, minimumSupport, numberOfTransactions,
                                     names, sets);
        pushOutput(rt, 0);
    }
}
