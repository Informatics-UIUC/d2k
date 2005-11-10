package ncsa.d2k.modules.core.datatype.table;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * Convert a table from one format to another.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ConvertTable extends ComputeModule {
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
    if(i == 0)
      return "Original Table";
    return "TableFactory";
  }

  public String getOutputInfo(int i) {
    return "Copied Table";
  }

  public String getModuleInfo() {
    return "Convert a table from one format to another, using a TableFactory.";
  }

  public void doit() throws Exception {
    Table orig = (Table)pullInput(0);
    TableFactory factory = (TableFactory)pullInput(1);

    Sparse sparseOrig = (Sparse)orig;
    /*for(int i = 0; i < orig.getNumColumns(); i++) {
      System.out.println(sparse.getColumnNumEntries(i));
    }
    if(true)
      return;*/

    int numColumns = orig.getNumColumns();
    int numRows = orig.getNumRows();

    MutableTable newTable = (MutableTable)factory.createTable(numColumns);
    for(int i = 0; i < numColumns; i++) {
      //System.out.println("converting column: "+i);
      int type = orig.getColumnType(i);
      Column newcol = factory.createColumn(type);
      newcol.setNumRows(sparseOrig.getColumnNumEntries(i));
      newcol.setLabel(orig.getColumnLabel(i));
      newcol.setComment(orig.getColumnComment(i));
      newTable.addColumn(newcol);

      switch(type) {
        case ColumnTypes.FLOAT:
          for(int j = 0; j < numRows; j++) {
            newcol.setFloat(orig.getFloat(j, i), j);
          }
          break;
        case ColumnTypes.DOUBLE:
          for(int j = 0; j < numRows; j++) {
            newcol.setDouble(orig.getDouble(j, i), j);
          }
          break;
        case ColumnTypes.SHORT:
          for(int j = 0; j < numRows; j++) {
            newcol.setShort(orig.getShort(j, i), j);
          }
          break;
        case ColumnTypes.STRING:
          for(int j = 0; j < numRows; j++) {
            newcol.setString(orig.getString(j, i), j);
          }
          break;
        case ColumnTypes.LONG:
          for(int j = 0; j < numRows; j++) {
            newcol.setLong(orig.getLong(j, i), j);
          }
          break;
        case ColumnTypes.BYTE:
          for(int j = 0; j < numRows; j++) {
            newcol.setByte(orig.getByte(j, i), j);
          }
          break;
        case ColumnTypes.CHAR:
          for(int j = 0; j < numRows; j++) {
            newcol.setChar(orig.getChar(j, i), j);
          }
          break;
        default:
          for(int j = 0; j < numRows; j++) {
            newcol.setObject(orig.getObject(j, i), j);
          }
          break;
      }

      //((MutableTable)orig).removeColumn(0);
    }

    newTable.setLabel(orig.getLabel());
    newTable.setComment(orig.getComment());

    if(orig instanceof ExampleTable) {
      int[] inputs = ((ExampleTable)orig).getInputFeatures();
      int[] outputs = ((ExampleTable)orig).getOutputFeatures();

      ExampleTable newExampleTable = newTable.toExampleTable();
      newExampleTable.setInputFeatures(inputs);
      newExampleTable.setOutputFeatures(outputs);
      pushOutput(newExampleTable, 0);
    }
    else {
      pushOutput(newTable, 0);
    }
  }
}
