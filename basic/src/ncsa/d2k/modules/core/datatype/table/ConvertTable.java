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

    int numColumns = orig.getNumColumns();
    int numRows = orig.getNumRows();

    MutableTable newTable = (MutableTable)factory.createTable(numColumns);
    for(int i = 0; i < numColumns; i++) {
      Column newcol = factory.createColumn(orig.getColumnType(i));
      newcol.setNumRows(numRows);
      newcol.setLabel(orig.getColumnLabel(i));
      newcol.setComment(orig.getColumnComment(i));
      newTable.setColumn(newcol, i);

      // using Object here as lowest common denominator
      for(int j = 0; j < numRows; j++) {
        newcol.setObject(orig.getObject(j, i), j);
      }
    }

    newTable.setLabel(orig.getLabel());
    newTable.setComment(orig.getComment());

    // should take care of example table, prediction table, etc
    pushOutput(newTable, 0);
  }
}
