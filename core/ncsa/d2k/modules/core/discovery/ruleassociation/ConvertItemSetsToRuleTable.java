package ncsa.d2k.modules.core.discovery.ruleassociation;

import java.util.*;


import gnu.trove.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

public class ConvertItemSetsToRuleTable extends DataPrepModule {

    public String[] getInputTypes() {
        String[] in = {"[[I","ncsa.d2k.modules.core.discovery.ruleassociation.ItemSets",
            "ncsa.d2k.modules.core.datatype.table.Table"};
        return in;
    }

    public String[] getOutputTypes() {
        //String[] out = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl",
        //    "java.util.ArrayList"};
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

    public void doit() {
        int[][] rules = (int[][])pullInput(0);
        ItemSets sets = (ItemSets)pullInput(1);
        Table table = (Table)pullInput(2);

        int numExamples = sets.numExamples;

        int numRules = rules.length;
        int[] head = new int[numRules];
        int[] body = new int[numRules];
        double[] confidence = new double[numRules];
        double[] support = new double[numRules];

        HashMap itemSets = new HashMap();

        for(int i = 0; i < numRules; i++) {
            int[] rule = rules[i];
            int numIf = rule.length-3;
            int numThen = 1;

            int[] ifstmt = new int[numIf];
            int[] thenstmt = new int[numThen];
            for(int j = 0; j < numIf; j++)
                ifstmt[j] = rule[j];
            thenstmt[0] = rule[rule.length-3];

            TIntArrayList ifitem = new TIntArrayList(ifstmt);
            TIntArrayList thenitem = new TIntArrayList(thenstmt);

            int ifidx;
            int thenidx;

            if(itemSets.containsKey(ifitem)) {
                ifidx = ((Integer)itemSets.get(ifitem)).intValue();
            }
            else {
                ifidx = itemSets.size();
                itemSets.put(ifitem, new Integer(ifidx));
            }
            if(itemSets.containsKey(thenitem))
                thenidx = ((Integer)itemSets.get(thenitem)).intValue();
            else {
                thenidx = itemSets.size();
                itemSets.put(thenitem, new Integer(thenidx));
            }

            head[i] = ifidx;
            body[i] = thenidx;
            double conf = (double)rule[rule.length-1];
            conf /= 1000000.0;
            double supp = rule[rule.length-2];
            supp /= numExamples;
            //System.out.println("RULE "+i+" "+conf+" "+supp);
            //confidence.setDouble(conf, i);
            //support.setDouble(supp, i);
            confidence[i] = conf;
            support[i] = supp;
        }
        IntColumn headColumn = new IntColumn(head);
        headColumn.setLabel(HEAD);
        IntColumn bodyColumn = new IntColumn(body);
        bodyColumn.setLabel(BODY);
        DoubleColumn confidenceCol = new DoubleColumn(confidence);
        confidenceCol.setLabel(CONFIDENCE);
        DoubleColumn supportCol = new DoubleColumn(support);
        supportCol.setLabel(SUPPORT);
        Column[] c = {headColumn, bodyColumn, supportCol, confidenceCol};
        TableImpl ti = new TableImpl(c);
        String[] names = sets.names;
        ArrayList al = new ArrayList(names.length);
        for(int i = 0; i < names.length; i++)
            al.add(names[i].replace('^', '='));

        Object[] freqSets = new Object[itemSets.size()];
        Iterator iter = itemSets.keySet().iterator();
        while(iter.hasNext()) {
            TIntArrayList itemset = (TIntArrayList)iter.next();
            int idx = ((Integer)itemSets.get(itemset)).intValue();
            FreqItemSet fis = new FreqItemSet();
            fis.items = itemset;
            fis.numberOfItems = itemset.size();
            /** !!!!!!!!!!!!!!!!!!!!!! **/
            fis.support = 0;
            freqSets[idx] = fis;
        }

        ArrayList allsets = new ArrayList(freqSets.length);
        for(int i = 0; i < freqSets.length; i++)
            allsets.add(freqSets[i]);

        RuleTable rt = new RuleTable(ti, 0, 0, table.getNumRows(), al, allsets);
        rt.sortByConfidence();
        pushOutput(rt, 0);
    }

    private static final String HEAD = "Head";
    private static final String BODY = "Body";
    private static final String CONFIDENCE = "Confidence";
    private static final String SUPPORT = "Support";
}
