package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

/**
 * <p>Title: CopyTable</p>
 * <p>Description: Make an exact copy of a Table.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author David Clutter
 * @version 1.0
 */
public class CopyTable extends DataPrepModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {"ncsa.d2k.modules.core.datatype.table.Table"};
    return out;
  }

  public String getInputInfo(int i) {
    return "A Table to copy.";
  }

  public String getInputName(int i) {
    return "Table";
  }

  public String getOutputInfo(int i) {
    return "An exact copy of the Table.";
  }

  public String getOutputName(int i) {
    return "Table Copy";
  }

  public String getModuleName() {
    return "Make a Copy of a Table";
  }

  public String getModuleInfo() {
    String s = "<p>Overview: Make an exact copy of a Table."+
      "<p>Detailed Description: Make a copy of a Table by calling its copy "+
      "method.  A deep copy of the Table is created."+
      "<p>Data Handling: No data is modified by this module."+
      "<p>Scalability: An exact copy of the Table is made, so there must "+
      "be enough available memory to hold two copies of the Table.";
    return s;
  }

  public void doit() throws Exception {
   Table t = (Table)pullInput(0);
	Table cpy = t.copy();
	pushOutput(cpy, 0);
  }
}
