package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;

public class RemoveRowsWithMissingValues extends DataPrepModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
    return out;
  }

  public String getInputInfo(int i) {
    return "A Table.";
  }

  public String getInputName(int i) {
    return "Table";
  }

  public String getOutputInfo(int i) {
    return "The input table, with all rows that contain missing values removed.";
  }

  public String getOutputName(int i) {
    return "Table";
  }

  public String getModuleInfo() {
    String s = "<p>Overview: Remove any rows with missing values from the table.";
    s += "<p>Detailed Description: Any rows in <i>Table</i> with missing values ";
    s += "will be removed from <i>Table</i>.";
    s += "<p>Data Handling: This module will remove rows from <i>Table</i>.";
    s += "No additional memory is needed.";
    return s;
  }

  public String getModuleName() {
    return "Remove Rows With Missing Values";
  }

  public void doit() {
    MutableTable mt = (MutableTable)pullInput(0);

    List toRemove = new ArrayList();
    int numCols = mt.getNumColumns();
    int numRows = mt.getNumRows();

    for(int i = 0; i < numCols; i++) {
      for(int j = 0; j < numRows; j++) {
        if(mt.isValueMissing(j, i)) {
          toRemove.add(new Integer(j));
          break;
        }
      }
    }

    int[] idx = new int[toRemove.size()];
    for(int i = 0; i < toRemove.size(); i++)
      idx[i] = ((Integer)toRemove.get(i)).intValue();

    mt.removeRowsByIndex(idx);
    pushOutput(mt,0);
  }
    

}
