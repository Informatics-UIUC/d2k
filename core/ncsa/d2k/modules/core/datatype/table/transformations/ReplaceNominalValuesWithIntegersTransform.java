package ncsa.d2k.modules.core.datatype.table.transformations;

import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Encapsulates a (reversible) transformation on a <code>MutableTable</code>
 * that replaces unique nominal column values with unique integers.
 */
public class ReplaceNominalValuesWithIntegersTransform
   implements ReversibleTransformation {

   static final long serialVersionUID = 2772230274372026572L;

   private int[] indirection;
   private HashMap[] nominalToInteger, integerToNominal;

   public ReplaceNominalValuesWithIntegersTransform(MutableTable mt) {

      // how many nominal columns do we have?
      int numNominalColumns = 0, totalColumns = mt.getNumColumns();

      for (int i = 0; i < totalColumns; i++) {
         if (mt.isColumnNominal(i)) {
            numNominalColumns++;
         }
      }

      nominalToInteger = new HashMap[numNominalColumns];
      integerToNominal = new HashMap[numNominalColumns];

      // create the indirection lookup for the nominal columns
      indirection = new int[numNominalColumns];

      int index = 0;
      for (int i = 0; i < totalColumns; i++)
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

      // transform(mt);

   }

   public String toMappingString(MutableTable mt) {

      if (nominalToInteger.length == 0 || integerToNominal.length == 0)
         return "empty transformation: " + super.toString();

      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < indirection.length; i++) {

         sb.append("column '");
         sb.append(mt.getColumnLabel(indirection[i]));
         sb.append("':\n");

         int size = integerToNominal[i].size();
         for (int j = 0; j < size; j++) {
            sb.append(integerToNominal[i].get(new Integer(j)));
            sb.append(" -> ");
            sb.append(j);
            sb.append('\n');
         }

      }

      return sb.toString();

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
