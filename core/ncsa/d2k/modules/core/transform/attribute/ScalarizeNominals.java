package ncsa.d2k.modules.core.transform.attribute;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * This module examines columns in a <code>MutableTable</code> and, for
 * appropriate columns which contain nominal values, converts these single
 * columns into multiple columns (of <code>boolean</code>s or
 * <code>int</code>s) -- one for each possible value of the attribute.
 * <p>
 * If the input <code>MutableTable</code> implements the
 * <code>ExampleTable</code> interface, only columns marked as inputs and
 * outputs will be converted. Otherwise, all columns containing nominal
 * values will be converted.
 * <p>
 * Through a property of the module, a user can select whether the generated
 * columns are of type <code>boolean</code> or <code>int</code>.
 */
public class ScalarizeNominals extends DataPrepModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

   public String getInputInfo(int index) {
     if (index == 0)
       return "A <i>MutableTable</i> (possibly an <i>ExampleTable</i>).";
     return "NO SUCH INPUT";
   }

   public String getInputName(int index) {
     if (index == 0)
       return "Mutable Table";
     return "NO SUCH INPUT";
   }

   public String[] getInputTypes() {
     return new String[] {
       "ncsa.d2k.modules.core.datatype.table.MutableTable"
     };
   }

   public String getModuleInfo() {
     StringBuffer sb = new StringBuffer("<p>Overview: ");
     sb.append("This module examines columns in a <i>MutableTable</i> and, ");
     sb.append("for appropriate columns which contain nominal values, ");
     sb.append("converts these single columns into multiple columns -- one ");
     sb.append("for each possible value of the attribute.");
     sb.append("</p><p>Detailed Description: ");
     sb.append("If the input <i>MutableTable</i> implements the ");
     sb.append("<i>ExampleTable</i> interface, only columns marked as ");
     sb.append("inputs and outputs will be converted. Otherwise, all ");
     sb.append("columns containing nominal values will be converted. ");
     sb.append("Through a property of the module, the user can select ");
     sb.append("whether the generated columns are integer or boolean.");
     sb.append("</p><p>Data Handling: ");
     sb.append("This module modifies its input data; each relevant nominal ");
     sb.append("column may be replaced with an arbitrary number of new ");
     sb.append("ones. In addition, columns with blank labels are assigned ");
     sb.append("default ones.");
     sb.append("</p>");
     return sb.toString();
   }

   public String getModuleName() {
     return "Scalarize Nominals";
   }

   public String getOutputInfo(int index) {
     if (index == 0) {
       StringBuffer sb = new StringBuffer();
       sb.append("The input <i>MutableTable</i> with appropriate nominal ");
       sb.append("columns transformed.");
       return sb.toString();
     }
     return "NO SUCH OUTPUT";
   }

   public String[] getOutputTypes() {
     return new String[] {
       "ncsa.d2k.modules.core.datatype.table.MutableTable"
     };
   }

   public String getOutputName(int index) {
     if (index == 0)
       return "Scalarized Mutable Table";
     return "NO SUCH OUTPUT";
   }

////////////////////////////////////////////////////////////////////////////////
// properties                                                                 //
////////////////////////////////////////////////////////////////////////////////

   private boolean _newTypeBoolean = true;
   public void setNewTypeBoolean(boolean value) { _newTypeBoolean = value; }
   public boolean getNewTypeBoolean() { return _newTypeBoolean; }

   public PropertyDescription[] getPropertiesDescriptions() {

     PropertyDescription newTypeBooleanDesc = new PropertyDescription(
       "newTypeBoolean",
       "Create new columns as type boolean",
       "Controls whether converted nominal columns will have scalar type " +
       "boolean (true) or type int (false).");

     return new PropertyDescription[] { newTypeBooleanDesc };

   }

////////////////////////////////////////////////////////////////////////////////
// doit()                                                                     //
////////////////////////////////////////////////////////////////////////////////

   public void doit() throws Exception {

     MutableTable table = (MutableTable)pullInput(0);

     int[] indices;
     int[] origInputs = null, origOutputs = null;

     // columns with blank labels need to be assigned default ones

     for (int i = 0; i < table.getNumColumns(); i++) {
       String s = table.getColumnLabel(i);
       if (s == null || s.length() == 0)
         table.setColumnLabel("column_" + i, i);
     }

     // determine which columns we wish to transform

     boolean tableIsExample = false;

     if (table instanceof ExampleTable) {

       tableIsExample = true;

       ExampleTable et = (ExampleTable)table;

       origInputs  = new int[et.getInputFeatures().length];
       for (int i = 0; i < origInputs.length; i++)
         origInputs[i] = et.getInputFeatures()[i];

       origOutputs = new int[et.getOutputFeatures().length];
       for (int i = 0; i < origOutputs.length; i++)
         origOutputs[i] = et.getOutputFeatures()[i];

       // ensure unique column indices

       HashMap uniqueIndexMap = new HashMap();

       for (int i = 0; i < origInputs.length; i++)
         if (et.isColumnNominal(origInputs[i]))
            uniqueIndexMap.put(new Integer(origInputs[i]), null);

       for (int i = 0; i < origOutputs.length; i++)
         if (et.isColumnNominal(origOutputs[i]))
            uniqueIndexMap.put(new Integer(origOutputs[i]), null);

       // retrieve column indices

       indices = new int[uniqueIndexMap.size()];
       int index = 0;

       Iterator iterator = uniqueIndexMap.keySet().iterator();
       while (iterator.hasNext())
         indices[index++] = ((Integer)iterator.next()).intValue();

       Arrays.sort(indices);

       // we'll be removing all input and output columns, so set these to
       // empty (at least until ExampleTableImpl handles this properly!)
       /*
       et.setInputFeatures(new int[0]);
       et.setOutputFeatures(new int[0]);
       */

     }
     else {

       // simply iterate to find nominal columns

       int numNominalColumns = 0;

       for (int i = 0; i < table.getNumColumns(); i++)
         if (table.isColumnNominal(i))
            numNominalColumns++;

       indices = new int[numNominalColumns];

       int index = 0;
       for (int i = 0; i < table.getNumColumns(); i++)
         if (table.isColumnNominal(i))
            indices[index++] = i;

     }

     // iterate and replace

     int offset = 0; // number of extra columns added to the table. must be
                 // added to column indices in order to keep consistent

     int numRows = table.getNumRows();

     for (int count = 0; count < indices.length; count++) {

       int index = indices[count] + offset;

       // find this column's unique values

       HashMap uniqueValuesMap = new HashMap();
       int uniqueValueCount = 0;

       for (int row = 0; row < numRows; row++) {

         if (table.isValueMissing(row, index))
            continue;
         else if (table.isValueMissing(row, index))
            continue;

         String s = table.getString(row, index);

         if (s == null || s.length() == 0)
            continue;
         if (uniqueValuesMap.containsKey(s))
            continue;

         uniqueValuesMap.put(s, new Integer(uniqueValueCount++));

       }

       if (uniqueValuesMap.size() == 0) { // nothing (or only missing) here
         continue;
       }
       else {

         // first, we'd like our string-to-integer mappings as arrays,
         // for efficiency

         String[] uniqueValues = new String[uniqueValuesMap.size()];
         int[] uniqueValueIndices = new int[uniqueValues.length];

         Iterator iterator = uniqueValuesMap.keySet().iterator();
         int iteratorCount = 0;

         while (iterator.hasNext())
            uniqueValues[iteratorCount++] = (String)iterator.next();

         for (int i = 0; i < uniqueValues.length; i++)
            uniqueValueIndices[i] = ((Integer)uniqueValuesMap.get(
              uniqueValues[i])).intValue();

         // we also want an indirection array so we can act as if these
         // mappings were sorted on the integer value

         int[] indirection = new int[uniqueValueIndices.length];
         for (int i = 0; i < uniqueValueIndices.length; i++)
            indirection[uniqueValueIndices[i]] = i;

         // now create one array for the entire column specifying which
         // unique value is contained in each row. if the value is missing
         // or empty, set to -1.

         int[] match = new int[numRows];
         for (int row = 0; row < numRows; row++) {

            if (table.isValueMissing(row, index) ||
               table.isValueEmpty  (row, index)) {
              match[row] = -1;
              continue;
            }

            String s = table.getString(row, index);

            for (int j = 0; j < uniqueValues.length; j++) {
              if (s.equals(uniqueValues[indirection[j]])) {
                match[row] = j;
                break;
              }
            }

         }

         // !:
         // are we dealing with an ExampleTable? if so, is the old column
         // an input, output, or both?

         boolean isInput = false, isOutput = false;
         if (tableIsExample) {

            ExampleTable et = (ExampleTable)table;

            for (int i = 0; i < origInputs.length; i++) {
              if (origInputs[i] == indices[count]) {
                isInput = true;
                break;
              }
            }

            for (int i = 0; i < origOutputs.length; i++) {
              if (origOutputs[i] == indices[count]) {
                isOutput = true;
                break;
              }
            }

         }

         // remove the old column

         String columnLabel = table.getColumnLabel(index);
         table.removeColumn(index);
         offset--;

         // iterate and create the new columns

         for (int k = 0; k < uniqueValues.length; k++) {

            if (_newTypeBoolean) { // create new columns as type boolean

              boolean[] newColumn = new boolean[numRows];

              for (int row = 0; row < match.length; row++) {
                if (match[row] == k)
                  newColumn[row] = true;
                else
                  newColumn[row] = false;
              }

              table.insertColumn(newColumn, index + k);
              table.setColumnLabel(columnLabel + "=" +
                uniqueValues[indirection[k]], index + k);

            }
            else { // create new columns as type int

              int[] newColumn = new int[numRows];

              for (int row = 0; row < match.length; row++) {
                if (match[row] == k)
                  newColumn[row] = 1;
                else
                  newColumn[row] = 0;
              }

              table.insertColumn(newColumn, index + k);
              table.setColumnLabel(columnLabel + "=" +
                uniqueValues[indirection[k]], index + k);

            }

            offset++;

            // !:
            // we now must add this new column to the list of inputs/outputs
            // if we are dealing with an ExampleTable. this isn't very
            // efficient; maybe we should modify the API to handle this
            if (tableIsExample) {

              ExampleTable et = (ExampleTable)table;

              if (isInput) {

                int[] inputs = et.getInputFeatures();
                int[] newInputs = new int[inputs.length + 1];

                for (int i = 0; i < inputs.length; i++)
                  newInputs[i] = inputs[i];
                newInputs[inputs.length] = index + k;

                Arrays.sort(newInputs);

                et.setInputFeatures(newInputs);

              }

              if (isOutput) {

                int[] outputs = et.getOutputFeatures();
                int[] newOutputs = new int[outputs.length + 1];

                for (int i = 0; i < outputs.length; i++)
                  newOutputs[i] = outputs[i];
                newOutputs[outputs.length] = index + k;

                Arrays.sort(newOutputs);

                et.setOutputFeatures(newOutputs);

              }

            }

         }

       }

     }

     pushOutput(table, 0);

   }

   /*
   private void printInputOutputColumns(ExampleTable et) {

      int[] inputColumns = et.getInputFeatures(),
            outputColumns = et.getOutputFeatures();

      String[] inputNames = et.getInputNames(),
               outputNames = et.getOutputNames();

      System.out.print("actual labels:");
      for (int i = 0; i < et.getNumColumns(); i++)
         System.out.print(" " + i + "(" + et.getColumnLabel(i) + ")");
      System.out.println(" [" + et.getNumColumns() + "]");

      System.out.print("input columns:");
      for (int i = 0; i < inputColumns.length; i++)
         System.out.print(" " + inputColumns[i] + "(" + inputNames[i] + ")");
      System.out.println(" [" + inputColumns.length + "]");

      System.out.print("output columns:");
      for (int i = 0; i < outputColumns.length; i++)
         System.out.print(" " + outputColumns[i] + "(" + outputNames[i] + ")");
      System.out.println(" [" + outputColumns.length + "]");

   }
   */

}
