package ncsa.d2k.modules.core.datatype;

import ncsa.d2k.core.modules.*;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

public class CreateBinTree extends DataPrepModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.datatype.table.transformations.BinTransform",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {"ncsa.d2k.modules.core.datatype.BinTree"};
    return out;
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
      return "";
  }

  public String getInputName(int i) {
    return "";
  }

  public String getOutputName(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  public String getModuleName() {
     return "";
  }

  public void doit() {
    BinTransform bt = (BinTransform)pullInput(0);
    ExampleTable et = (ExampleTable)pullInput(1);

    BinTree tree = bt.createBinTree(et);
    pushOutput(tree, 0);
  }
}
