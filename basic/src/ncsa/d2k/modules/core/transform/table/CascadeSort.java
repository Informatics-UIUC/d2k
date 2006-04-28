package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;
/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CascadeSort {

        MutableTable table;
//      int[] sortorder;
//              int sortsize;
                int[] runs = null;
                boolean first = true;



        public CascadeSort(Table toSort) {
         table = (MutableTable)toSort;

        }

        public int[] getDefaultSortOrder() {
                 int [] sortorder = new int[table.getNumColumns()];
                 for(int i = 0; i < sortorder.length; i++)
                   sortorder[i] = i;
                 return sortorder;
           }


           /**
                Find the indices where a run ends in a column.  A run is a collection of
                similar values in a column.  The column is assumed to be sorted.
                Returns an array whose values are the indices of the
                rows that end a run.
                @param col the column of interest
                @return
                */
           private int[] findRuns(int col) {
                 if (first) {
                   //System.out.println("FindRuns: "+table.getColumnLabel(col));
                   ArrayList runList = new ArrayList();
                   if(table.isColumnScalar(col)) {
                         double currentVal = table.getDouble(0, col);
                         for(int i = 1; i < table.getNumRows(); i++) {
                           double rowVal = table.getDouble(i, col);
                           if(rowVal != currentVal) {
                                 runList.add(new Integer(i-1));
                                 currentVal = rowVal;
                           }
                         }
                   }
                   else {
                         String currentVal = table.getString(0, col);
                         for(int i = 1; i < table.getNumRows(); i++) {
                           String rowVal = table.getString(i, col);
                           if(!rowVal.equals(currentVal)) {
                                 runList.add(new Integer(i-1));
                                 currentVal = rowVal;
                           }
                         }
                   }
                   runList.add(new Integer(table.getNumRows()-1));
                   int[] retVal = new int[runList.size()];
                   for(int i = 0; i < retVal.length; i++) {
                         retVal[i] = ((Integer)runList.get(i)).intValue();
                         //System.out.println("Run["+i+"]: "+retVal[i]);
                   }
                   return retVal;
                 } else {
                   //Sort through the runs
                   ArrayList runList = new ArrayList();
                   if(table.isColumnScalar(col)) {
                         double currentVal = table.getDouble(0, col);
                         for (int i = 0; i <= runs[0]; i++) {
                           double rowVal = table.getDouble(i, col);
                           if (rowVal != currentVal) {
                                 runList.add(new Integer(i-1));
                                 currentVal = rowVal;
                           }
                         }
                         for (int j = 1; j < runs.length; j++) {
                           runList.add(new Integer(runs[j-1]));
                           for (int i = runs[j-1] + 1; i <= runs[j]; i++) {
                                 double rowVal = table.getDouble(i, col);
                                 if (rowVal != currentVal) {
                                   runList.add(new Integer(i-1));
                                   currentVal = rowVal;
                                 }
                           }
                         }
                   } else {
                         String currentVal = table.getString(0, col);
                         for (int i = 0; i <= runs[0]; i++) {
                           String rowVal = table.getString(i, col);
                           if (!rowVal.equals(currentVal)) {
                                 runList.add(new Integer(i-1));
                                 currentVal = rowVal;
                           }
                         }
                         for (int j = 1; j < runs.length; j++) {
                           runList.add(new Integer(runs[j-1]));
                           for (int i = runs[j-1] + 1; i <= runs[j]; i++) {
                                 String rowVal = table.getString(i, col);
                                 if (!rowVal.equals(currentVal)) {
                                   runList.add(new Integer(i-1));
                                   currentVal = rowVal;
                                 }
                           }
                         }
                   }
                   runList.add(new Integer(table.getNumRows()-1));
                   int[] retVal = new int[runList.size()];
                   for(int i = 0; i < retVal.length; i++) {
                         retVal[i] = ((Integer)runList.get(i)).intValue();
                         //System.out.println("Run["+i+"]: "+retVal[i]);
                   }
                   return retVal;
                 }
           }


        /**
                 * Sort the table for each column selected
                 */
                public void sort(int [] sortorder) {
                  if(sortorder.length > 0) {
                        table.sortByColumn(sortorder[0]);
                        for(int i = 1; i < sortorder.length; i++) {
                          // Now find the runs in the (i-1)th column and do a
                          // table.sortByColumn(col, begin, end) for each run
                          runs = findRuns(sortorder[i-1]);
                          first = false;


                          // Do the first sort outside the loop
                          table.sortByColumn(sortorder[i], 0, runs[0]);
                          for(int j = 1; j < runs.length; j++) {
                                // Now sort from the end of the last run to the end
                                // of the current run
                                table.sortByColumn(sortorder[i], runs[j-1]+1, runs[j]);
                          }
                        }
                  }
                 // if(getReorderColumns())
                //      reorder();
                  // Reset global variables
                 // runs = null;
                  //sortorder = null;
                  //first = true;
                 // pushOutput(table, 0);
                }

        /*public void initialize(int[] array) {
                for (int index=0; index < array.length; index++)
                  array[index] = index;
          } */

        public void reorder(int [] sortorder) {
//VG - replaced the commented out code with this one. there was a bug with
          //reordering of the column - it did not work....

      //an arrayof the old indices and how we are swapping among them.
          int[] indirection = new int[table.getNumColumns()];
          for (int i = 0; i < indirection.length; i++)
            indirection[i] = i;

          for (int i = 0; i < sortorder.length; i++) {
            //if what's in sortorder[i] and indirection[i] is the same don't do anything
            if (indirection[i] == sortorder[i])continue;

            //if not - swao the ith column with column
            //indexed_what_should_be_in_the_ith_column
            table.swapColumns(i, sortorder[i]);
            //also swapping these indices in indirection
            int temp = indirection[i];
            indirection[i] = indirection[sortorder[i]];
            indirection[sortorder[i]] = temp;

          }

          /*
          for (int i = 0; i < indirection.length; i++)
            indirection[i] = i;

          for (int i = 0; i < sortorder.length; i++) {

            table.swapColumns(i, indirection[sortorder[i]]);

            int tmp = indirection[i];
            indirection[i] = indirection[sortorder[i]];
            indirection[sortorder[i]] = tmp;

          }
      */
      }


}
