package ncsa.d2k.modules.core.prediction.naivebayes;

import java.util.HashMap;
import java.io.Serializable;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.Column;
/**
   NaiveBayesPieChartData contains all the data contained in a pie
   chart in the NaiveBayesVis.  The ratios are sorted from least to
   greatest, so calling getRatio(0) will return the smallest ratio, and
   so on.
*/
final class NaiveBayesPieChartData extends MutableTableImpl implements Serializable {

   /** The total number of tallies in this pie */
   private int total;

   /** The attribute name we represent */
   private String attributeName;

   /** The name of the pie */
   private String binName;

   /** The total of all the pies for this attribute */
   private int rowTotal;

    /** constant for class column index */
   static final int CLASS = 0;
    /** constant for tally column index */
   static final int TALLY = 1;
    /** constant for ratio column index */
   static final int RATIO = 2;

   /**
      Constructor
    @param an attribute name
    @param bn bin name
    @param n class names
    @param t tallies
   */
   NaiveBayesPieChartData(String an, String bn, String []n, int[]t) {
      total = 0;
      rowTotal = 0;
      attributeName = an;
      binName = bn;

      StringColumn cn = new StringColumn(n.length);
      IntColumn tc = new IntColumn(n.length);
      DoubleColumn rc = new DoubleColumn(n.length);

      for(int i = 0; i < n.length; i++) {
         cn.setString(n[i], i);
         tc.setInt(t[i], i);
         total += t[i];
      }

      for(int i = 0; i < n.length; i++) {
         if(total == 0)
            rc.setDouble(0, i);
         else {
            double ratio = ((double)t[i])/((double)total);
            rc.setDouble(ratio, i);
         }
      }
      Column []c = new Column[3];
      c[0] = cn;
      c[1] = tc;
      c[2] = rc;
      setColumns(c);
   /*
      try {
         sortByColumn(TALLY);
      }
      catch(NotSupportedException e) {e.printStackTrace ();}
      */

      classLookup = new HashMap();
      int numRows = getNumRows();
      for(int i = 0; i < numRows; i++) {
         classLookup.put(getString(i, CLASS), new Integer(i));
      }
   }

   /**
      Constructor only used when creating the evidence.
    @param an attribute name
    @param bn bin name
    @param n class names
    @param r class ratios
   */
   NaiveBayesPieChartData(String an, String bn, String []n, double[]r) {
      total = 0;
      rowTotal = 0;
      attributeName = an;
      binName = bn;

      double ratioTotal = 0;

      StringColumn cn = new StringColumn(n.length);
      IntColumn tc = new IntColumn(n.length);
      DoubleColumn rc = new DoubleColumn(n.length);
      for(int i = 0; i < n.length; i++) {
         cn.setString(n[i], i);
         rc.setDouble(r[i], i);
         tc.setInt(0, i);
         ratioTotal += r[i];
      }

      for(int i = 0; i < n.length; i++) {
         if(ratioTotal == 0)
            rc.setDouble(0, i);
         else {
            double ratio = rc.getDouble(i)/ratioTotal;
            rc.setDouble(ratio, i);
         }
      }

      Column []c = new Column[3];
      c[0] = cn;
      c[1] = tc;
      c[2] = rc;
      setColumns(c);
/*
      try {
         sortByColumn(RATIO);
      }
      catch(NotSupportedException e) {}
*/
      classLookup = new HashMap();
      int numRows = getNumRows();
      for(int i = 0; i < numRows; i++) {
         classLookup.put(getString(i, CLASS), new Integer(i));
      }
   }

    /**
     * Sort this table by column c.
     * @param c column index
     */
   public void sortByColumn(int c) {
  	    super.sortByColumn(c);
      	classLookup = new HashMap();
        int numRows = getNumRows();
      	for(int i = 0; i < numRows; i++) {
       		classLookup.put(getString(i, CLASS), new Integer(i));
      	}
   }

    /**
     * Print for debugging
     */
   void printMe() {
      System.out.println("bn: "+binName+" "+total);
      print();
   }

    /** maps class name to a row in this table */
     private HashMap classLookup;

     /**
     *  Get the ratio for a class given the class name
     * @param c class name
     * @return ratio
     */
   double getClass(String c) {
      Integer row = (Integer)classLookup.get(c);
      return getDouble(row.intValue(), RATIO);
   }

   /**
      Get the total number of tallies in this pie
      @return the total number of tallies
   */
   int getTotal() {
      return total;
   }

   /**
      Get the ith class name.  Since the data is sorted, this will
      be the class with the ith largest slice of the pie.
      @param i the row
      @return The class that is in the ith row.
   */
   String getClassName(int i) {
      return getString(i, CLASS);
   }

   /**
      Get the ith tally.  This is the ith largest tally.
      @param i the row to look up
      @return the ith largest tally
   */
   int getTally(int i) {
      return getInt(i, TALLY);
   }

   /**
      Get the ith ratio.  This is the ith largest ratio.
      @param i the row to look up
      @return the ith largest tally
   */
   double getRatio(int i) {
      return getDouble(i, RATIO);
   }

   /**
      Get the attribute name
      @return the attribute that this pie represents
   */
   String getAttributeName() {
      return attributeName;
   }

   /**
      Get the name of the pie.
      @return the name of the bin that this pie represents
   */
   String getBinName() {
      return binName;
   }

   /**
      Get the total number of tallies for this attribute.
      @return the total number of tallies in this row
   */
   int getRowTotal() {
      return rowTotal;
   }

   /**
      Set the total number of tallies for this attribute.
    @param i the total
   */
   void setRowTotal(int i) {
      rowTotal = i;
   }
}
