package ncsa.d2k.modules.core.discovery.ruleassociation;

import java.util.*;


import gnu.trove.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.discovery.ruleassociation.*;

/**
 *
 */
public class RuleTable extends MutableTableImpl {

    private static final int IF = 0;
    private static final int THEN = 1;
    private static final int SUPPORT = 3;
    private static final int CONFIDENCE = 2;

    private double minimumConfidence;
    private double minimumSupport;
    private int numberOfTransactions;

    private List items;

    private List itemSets;

    //--------------------

    /**
     * Create a new RuleTable
     * @param rls
     * @param minCon
     * @param minSupp
     * @param names
     * @param sets
     */
    public RuleTable(TableImpl rls, double minCon, double minSupp, int numTrans, List names, List sets) {
        // the support and confidence columns are in the wrong order when we get them.
        Column[] c = {rls.getColumn(0), rls.getColumn(1),
            rls.getColumn(3), rls.getColumn(2)};
        this.columns = c;
        numberOfTransactions = numTrans;
        minimumConfidence = minCon;
        minimumSupport = minSupp;
        this.items = names;
        this.itemSets = sets;
    }

    /**
     * Sort the rules by confidence.
     */
    public void sortByConfidence() {
        sortByColumn(CONFIDENCE);
    }

    /**
     * Sort the rules by support.
     */
    public void sortBySupport() {
        sortByColumn(SUPPORT);
    }

    /**
     * Get the minimum confidence
     * @return
     */
    public double getMinimumConfidence() {
        return minimumConfidence;
    }

    /**
     * Get the minimum support
     * @return
     */
    public double getMinimumSupport() {
        return minimumSupport;
    }

    /**
     * Get the number of transactions
     * @return
     */
    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    /**
     * Get the confidence for a specific rule
     * @param row
     * @return
     */
    public double getConfidence(int row) {
        return getDouble(getNumRows()-1-row, CONFIDENCE);
    }

    /**
     * Get the support for a specific rule
     * @param row
     * @return
     */
    public double getSupport(int row) {
        return getDouble(getNumRows()-1-row, SUPPORT);
    }

    /**
     * Get the antecedent for a specific rule
     * @param row
     * @return
     */
    public int[] getRuleAntecedent(int row) {
        int idx = getInt(getNumRows()-1-row, IF);
        FreqItemSet fis = (FreqItemSet)itemSets.get(idx);
        TIntArrayList set = fis.items;
        return set.toNativeArray();
    }

    /**
     * Get the ID of the antecedent for a specific rule
     * @param row
     * @return
     */
    public int getRuleAntecedentID(int row) {
        return getInt(getNumRows()-1-row, IF);
    }

    /**
     * Get the consequent for a specific rule
     * @param row
     * @return
     */
    public int[] getRuleConsequent(int row) {
        int idx = getInt(getNumRows()-1-row, THEN);
        FreqItemSet fis = (FreqItemSet)itemSets.get(idx);
        TIntArrayList set = fis.items;
        return set.toNativeArray();
    }

    /**
     * Get the ID of the consequent for a specific rule
     * @param row
     * @return
     */
    public int getRuleConsequentID(int row) {
        return getInt(getNumRows()-1-row, THEN);
    }

    /**
     * Get the number of rules.
     * @return
     */
    public int getNumRules() {
        return getNumRows();
    }

    /**
     * Get the list of names.
     * @return
     */
    public List getNamesList() {
        return items;
    }

    /**
     * Get the list of FreqItemSets
     * @return
     */
    public List getItemSetsList() {
        return itemSets;
    }
}