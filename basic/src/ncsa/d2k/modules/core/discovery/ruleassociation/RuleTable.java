package ncsa.d2k.modules.core.discovery.ruleassociation;

import java.util.*;


import gnu.trove.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.discovery.ruleassociation.*;

/** This class contains the following components:
 * minimumSupport - minimum support specified by a user
 * minimumConfidence - minimum confidence specified by a user
 * numberOfTransactions - number of records in the data set
 * ruleTable - an ExampleTable to hold all rules
 * items - a List to hold all item labels
 * itemSets - a List to hold all frequent item sets. Each frequent item set
 *            is represented by a list of item indexes.
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
     * @param rls an ExampleTable to hold all rules
     * @param minCon the minimum confidence specified by a user
     * @param minSupp the minimum support specified by a user
     * @param names list of item labels
     * @param sets list of frequent item sets
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
     * cleanup removes attribute-value combinations from the visualization
     * that are not part of any rule. First the remap field identifies which
     * items(attribute-values) are not used. Then the remap field is used to
     * determine the items to be removed and identify the new value that items
     * should use.  Then it adjusts the frequent item sets.
     * This method By Loretta Auvil
     */
    public void cleanup() {
        // assign value of 1 if attribute-value is used
        int [] remap = new int[items.size()];
        for (int i=0; i < getNumRules(); i++) {
          int[] ante = getRuleAntecedent(i);
          for (int j=0; j < ante.length; j++)
            if (remap[ante[j]] != 1)
              remap[ante[j]] = 1;
          int[] conseq = getRuleConsequent(i);
          for (int j=0; j < conseq.length; j++)
            if (remap[conseq[j]] != 1)
              remap[conseq[j]] = 1;
        }
        //calculate new index for attribute-value
        int adjustment = 0;
        int len = items.size();
        for (int i=0; i < len; i++)
          if (remap[i] != 1) {
            items.remove(i-adjustment);
            adjustment++;
            remap[i] = -1;
          }
          else
            remap[i] = i-adjustment;
        //adjust the values of frequent item sets by the remap data
        for (int i=0; i < itemSets.size(); i++) {
          FreqItemSet fis = (FreqItemSet)itemSets.get(i);
          len = fis.items.size();
          for (int j=0; j<len; j++)
            fis.items.set(j,remap[fis.items.get(j)]);
        }
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
     * @return the confidence a user specified
     */
    public double getMinimumConfidence() {
        return minimumConfidence;
    }

    /**
     * Get the minimum support
     * @return the support a user specified
     */
    public double getMinimumSupport() {
        return minimumSupport;
    }

    /**
     * Get the number of transactions
     * @return the number of transactions in the data set
     */
    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    /**
     * Get the confidence for a specific rule
     * @param row the rule to check.
     * @return the confidence of the rule.
     */
    public double getConfidence(int row) {
        return getDouble(getNumRows()-1-row, CONFIDENCE);
    }

    /**
     * Get the support for a specific rule
     * @param row the rule to check.
     * @return the support of the rule.
     */
    public double getSupport(int row) {
        return getDouble(getNumRows()-1-row, SUPPORT);
    }

    /**
     * Get the antecedent for a specific rule
     * @param row the rule to check.
     * @return the antecedent of the rule represented by a list of item indexes.
     */
    public int[] getRuleAntecedent(int row) {
        int idx = getInt(getNumRows()-1-row, IF);
        FreqItemSet fis = (FreqItemSet)itemSets.get(idx);
        TIntArrayList set = fis.items;
        return set.toNativeArray();
    }

    /**
     * Get the ID of the antecedent for a specific rule
     * @param row the rule to check
     * @return the frequent set ID for the antecedent of the rule.
     */
    public int getRuleAntecedentID(int row) {
        return getInt(getNumRows()-1-row, IF);
    }

    /**
     * Get the consequent for a specific rule
     * @param row the rule to check for.
     * @return the consequent of the rule represented by a list of item indexes.
     */
    public int[] getRuleConsequent(int row) {
        int idx = getInt(getNumRows()-1-row, THEN);
        FreqItemSet fis = (FreqItemSet)itemSets.get(idx);
        TIntArrayList set = fis.items;
        return set.toNativeArray();
    }

    /**
     * Get the ID of the consequent for a specific rule
     * @param row the rule to check for.
     * @return the frequent set ID for the consequent of the rule.
     */
    public int getRuleConsequentID(int row) {
        return getInt(getNumRows()-1-row, THEN);
    }

    /**
     * Get the number of rules.
     * @return the number of rules for the data set.
     */
    public int getNumRules() {
        return getNumRows();
    }

    /**
     * Get the list of names.
     * @return the list of item labels.
     */
    public List getNamesList() {
        return items;
    }

    /**
     * Get the list of FreqItemSets
     * @return the list of FreqItemSets.
     */
    public List getItemSetsList() {
        return itemSets;
    }
}