//package ncsa.d2k.modules.projects.gpape;
package ncsa.d2k.modules.core.datatype.table.transformations;

import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

public class ReplaceNominalValuesWithIntegersTransform implements ReversibleTransformation {
	static final long serialVersionUID = 2772230274372026572L;

   /*public String getModuleInfo() {
      return "Replaces values in nominal columns of a MutableTable with unique integers.";
   }

   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
      return i;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "A MutableTable with nominal columns.";
      else
         return "This module has no such input.";
   }

   public String[] getOutputTypes() {
      String[] o = {"ncsa.d2k.modules.core.datatype.table.MutableTable",
                    "ncsa.d2k.modules.projects.gpape.ReplaceNominalValuesWithIntegers"};
      return o;
   }

   public String getOutputInfo(int index) {
      if (index == 0)
         return "The transformed MutableTable";
      else if (index == 1)
         return "This module, with the appropriate transformation logic.";
      else
         return "This module has no such output.";
   }
   */


   private int[] indirection;
   private HashMap nominalToInteger[], integerToNominal[];

   //public void doit() {

   //   MutableTable mt = (MutableTable)pullInput(0);
   public ReplaceNominalValuesWithIntegersTransform(MutableTable mt) {

      // how many nominal columns do we have?
      int numNominalColumns = 0, totalColumns = mt.getNumColumns();
      for (int i = 0; i < totalColumns; i++)
         if (mt.isColumnNominal(i))
            numNominalColumns++;

      nominalToInteger = new HashMap[numNominalColumns];
      integerToNominal = new HashMap[numNominalColumns];

      // create the indirection lookup for the nominal columns
      indirection = new int[numNominalColumns];

      int index = 0;
      for (int i = 0; i < numNominalColumns; i++)
         if (mt.isColumnNominal(i))
            indirection[index++] = i;

      // replace the columns
      int numRows = mt.getNumRows(), numItems;
      String item;
      for (int i = 0; i < indirection.length; i++) {

         nominalToInteger[i] = new HashMap();
         integerToNominal[i] = new HashMap();

         int col = indirection[i];

         numItems = 0;
         for (int j = 0; j < numRows; j++) {

            item = mt.getString(j, col);
            if (!nominalToInteger[i].containsKey(item)) {
               nominalToInteger[i].put(item, new Integer(numItems));
               integerToNominal[i].put(new Integer(numItems), item);
               numItems++;
            }

         }

      }

      transform(mt);

      //if (mt instanceof ExampleTable)
      //  ((ExampleTable)output).addTransformation(this);

      // mt = null; // really garbage collect mt?

      //pushOutput(output, 0);
      //pushOutput(this, 1);
		mt.addTransformation(this);
   }

   public boolean transform(MutableTable mt) {

      //MutableTable mt = (MutableTable)t.copy();

      int numRows = mt.getNumRows();
      String item, label;
      for (int i = 0; i < indirection.length; i++) {

         int[] intColumn = new int[numRows];

         int col = indirection[i];

         for (int j = 0; j < numRows; j++) {
            item = (String)mt.getString(j, col);
            intColumn[j] = ((Integer)nominalToInteger[i].get(item)).intValue();
         }

         label = mt.getColumnLabel(col);
         mt.setColumn(intColumn, col);
         mt.setColumnLabel(label, col);

      }

      //return mt;
		return true;
   }

   public boolean untransform(MutableTable mt) {

      //MutableTable mt = (MutableTable)t.copy();

      int numRows = mt.getNumRows();
      Integer item;
      String label;
      for (int i = 0; i < indirection.length; i++) {

         String[] stringColumn = new String[numRows];

         int col = indirection[i];

         for (int j = 0; j < numRows; j++) {
            item = new Integer(mt.getInt(j, col));
            stringColumn[j] = (String)integerToNominal[i].get(item);
         }

         label = mt.getColumnLabel(col);
         mt.setColumn(stringColumn, col);
         mt.setColumnLabel(label, col);

      }

      //return mt;
		return true;
   }

}
