package ncsa.d2k.modules.core.datatype.table;

import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * Convert a table from one format to another.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ConvertTable
    extends ComputeModule {
  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.datatype.table.Table",
        "ncsa.d2k.modules.core.datatype.table.TableFactory"
    };
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.datatype.table.Table"
    };
  }

  public String getInputInfo(int i) {
    if (i == 0) {
      return "Original Table";
    }
    return "TableFactory";
  }

  public String getOutputInfo(int i) {
    return "Copied Table";
  }

  public String getModuleInfo() {
    return "Convert a table from one format to another, using a TableFactory.";
  }

  public void doit() throws Exception {
    Table orig = (Table) pullInput(0);
    TableFactory factory = (TableFactory) pullInput(1);

    /*Sparse sparseOrig = (Sparse)orig;
         for(int i = 0; i < orig.getNumColumns(); i++) {
      System.out.println(sparse.getColumnNumEntries(i));
         }
         if(true)
      return;*/

    int numColumns = orig.getNumColumns();
    int numRows = orig.getNumRows();

    MutableTable newTable = (MutableTable) factory.createTable();
    //newTable.setNumRows(numRows);

    for (int i = 0; i < numColumns; i++) {
      //System.out.println("converting column: "+i);
      int type = orig.getColumnType(i);
      Column newcol = factory.createColumn(type);
      //newcol.setNumRows(sparseOrig.getColumnNumEntries(i));
      newcol.setNumRows(numRows);
      newcol.setLabel(orig.getColumnLabel(i));
      newcol.setComment(orig.getColumnComment(i));

      switch (type) {
        case ColumnTypes.FLOAT:
          if(orig instanceof Sparse) {
            int[] rows = ((Sparse)orig).getColumnIndices(i);
            for (int j = 0; j < rows.length; j++) {
              newcol.setFloat(orig.getFloat(rows[j], i), rows[j]);
            }
          }
          else {
            for (int j = 0; j < numRows; j++) {
              newcol.setFloat(orig.getFloat(j, i), j);
            }
          }
          break;
        case ColumnTypes.DOUBLE:
          if(orig instanceof Sparse) {
            int[] rows = ((Sparse)orig).getColumnIndices(i);
            for (int j = 0; j < rows.length; j++) {
              newcol.setDouble(orig.getDouble(rows[j], i), rows[j]);
            }
          }
          else {
            for (int j = 0; j < numRows; j++) {
              newcol.setDouble(orig.getDouble(j, i), j);
            }
          }
          break;
        case ColumnTypes.SHORT:
          if(orig instanceof Sparse) {
            int[] rows = ((Sparse)orig).getColumnIndices(i);
            for (int j = 0; j < rows.length; j++) {
              newcol.setShort(orig.getShort(rows[j], i), rows[j]);
            }
          }
          else {
            for (int j = 0; j < numRows; j++) {
              newcol.setShort(orig.getShort(j, i), j);
            }
          }
          break;
        case ColumnTypes.STRING:
          if(orig instanceof Sparse) {
            int[] rows = ( (Sparse) orig).getColumnIndices(i);
            for (int j = 0; j < rows.length; j++) {
              newcol.setString(orig.getString(rows[j], i), rows[j]);
            }
          }
          else {
            for (int j = 0; j < numRows; j++) {
              newcol.setString(orig.getString(j, i), j);
            }
          }
          break;
        case ColumnTypes.LONG:
          if(orig instanceof Sparse) {
            int[] rows = ( (Sparse) orig).getColumnIndices(i);
            for (int j = 0; j < rows.length; j++) {
              newcol.setLong(orig.getLong(rows[j], i), rows[j]);
            }
          }
          else {
            for (int j = 0; j < numRows; j++) {
              newcol.setLong(orig.getLong(j, i), j);
            }
          }
          break;
        case ColumnTypes.BYTE:
          if(orig instanceof Sparse) {
            int[] rows = ( (Sparse) orig).getColumnIndices(i);
            for (int j = 0; j < rows.length; j++) {
              newcol.setByte(orig.getByte(rows[j], i), rows[j]);
            }
          }
          else {
            for (int j = 0; j < numRows; j++) {
              newcol.setByte(orig.getByte(j, i), j);
            }
          }
          break;
        case ColumnTypes.CHAR:
          if(orig instanceof Sparse) {
            int[] rows = ( (Sparse) orig).getColumnIndices(i);
            for (int j = 0; j < rows.length; j++) {
              newcol.setChar(orig.getChar(rows[j], i), rows[j]);
            }
          }
          else {
            for (int j = 0; j < numRows; j++) {
              newcol.setChar(orig.getChar(j, i), j);
            }
          }
          break;
        default:
          if(orig instanceof Sparse) {
            int[] rows = ( (Sparse) orig).getColumnIndices(i);
            for (int j = 0; j < rows.length; j++) {
              newcol.setObject(orig.getObject(rows[j], i), rows[j]);
            }
          }
          else {
            for (int j = 0; j < numRows; j++) {
              newcol.setObject(orig.getObject(j, i), j);
            }
          }
          break;
      }
      newTable.addColumn(newcol);
    }

    newTable.setLabel(orig.getLabel());
    newTable.setComment(orig.getComment());

    // missing values preservation
    for(int i = 0; i < orig.getNumRows(); i++) {
      for(int j = 0; j < orig.getNumColumns(); j++) {
        if(orig.isValueEmpty(i, j)) {
          newTable.setValueToEmpty(true, i, j);
        }
        if(orig.isValueMissing(i, j)) {
          newTable.setValueToMissing(true, i, j);
        }
      }
    }

    //System.out.println("pushing converted table.");

    if (orig instanceof ExampleTable) {
      int[] inputs = ( (ExampleTable) orig).getInputFeatures();
      int[] newinputs = new int[inputs.length];
      System.arraycopy(inputs, 0, newinputs, 0, inputs.length);
      int[] outputs = ( (ExampleTable) orig).getOutputFeatures();
      int[] newoutputs = new int[outputs.length];
      System.arraycopy(outputs, 0, newoutputs, 0, outputs.length);

      ExampleTable newExampleTable = newTable.toExampleTable();
      newExampleTable.setInputFeatures(newinputs);
      newExampleTable.setOutputFeatures(newoutputs);

      if(orig instanceof PredictionTable) {
        PredictionTable newPredTable = newExampleTable.toPredictionTable();
        int[] preds = ( (PredictionTable) orig).getPredictionSet();
        int[] newpreds = new int[preds.length];
        System.arraycopy(preds, 0, newpreds, 0, preds.length);
        pushOutput(newPredTable, 0);
      }
      else {
        pushOutput(newExampleTable, 0);
      }
    }
    else {
      pushOutput(newTable, 0);
    }
  }
}
