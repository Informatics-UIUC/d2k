package ncsa.d2k.modules.core.datatype;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.core.modules.*;

/**
 * <p>Title: CopyTable</p>
 * <p>Description: Make an exact copy of a Table.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author David Clutter
 * @version 1.0
 */
public class CloneBinTransform extends DataPrepModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.datatype.table.transformations.BinTransform",
      "java.lang.Integer"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {"ncsa.d2k.modules.core.datatype.table.transformations.BinTransform"};
    return out;
  }

  public String getInputInfo(int i) {
    if(i == 0)
    return "A BinTransform to clone.";
    else
      return "The number of times to clone the BinTransform.";
  }

  public String getInputName(int i) {
    if(i ==0)
    return "BinTransform to Clone";
    else
      return "N";
  }

  public String getOutputInfo(int i) {
    return "A clone of the input.";
  }

  public String getOutputName(int i) {
    return "BinTransform Clone";
  }

  public String getModuleName() {
    return "Make a clone of a BinTransform.";
  }

  public String getModuleInfo() {
    String s = "<p>Overview: Make <i>N</i> clones of a BinTransform object."+
      "<p>Detailed Description: Make <i>N</i> clones of a BinTransform object by calling "+
      "the clone method."+
      "<p>Data Handling: No data is modified by this module."+
      "<p>Scalability: <i>N</i> exact copies of the <i>BinTransform to Clone</i> are made, "+
      "so there must be enough available memory to hold the original and all the copies.";
    return s;
  }

  public void doit() throws Exception {
    BinTransform bt = (BinTransform)pullInput(0);
    Integer ii = (Integer)pullInput(1);
    for(int i = 0; i < ii.intValue(); i++) {
      BinTransform clne = ( (BinTransform) bt.clone());
      pushOutput(clne, 0);
    }
  }
}