package ncsa.d2k.modules.core.datatype.table.examples;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.continuous.*;

import ncsa.d2k.core.modules.*;

public class PrintExample extends OutputModule {

  private String Label    = "";
  public  void   setLabel (String value) {       this.Label       = value;}
  public  String getLabel ()             {return this.Label;}

  public String getModuleInfo() {
    return "PrintExample";
  }
  public String getModuleName() {
    return "PrintExample";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0: return "Example Table";
    }
    return "";
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "ncsa.d2k.modules.core.datatype.table.Example";
    }
    return "";
  }
  public String[] getInputTypes() {
    String [] in = {"ncsa.d2k.modules.core.datatype.table.Example"};
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Example Table";
    }
    return "";
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "ncsa.d2k.modules.core.datatype.table.Example";
    }
    return "";
  }
  public String[] getOutputTypes() {
    String [] out = {"ncsa.d2k.modules.core.datatype.table.Example"};
    return out;
  }





  public void doit() {

    Example example = (Example) this.pullInput(0);
    int numInputs = example.getNumInputs();
    int numOutputs = example.getNumOutputs();

    System.out.println(Label + "    input:");
    for (int v = 0; v < numInputs; v++) {
      System.out.println(Label + "    " + example.getInputName(v)  + " = " + example.getInputDouble(v));
    }
    System.out.println(Label + "    output:");
    for (int v = 0; v < numOutputs; v++) {
      System.out.println(Label + "    " + example.getOutputName(v) + " = " + example.getOutputDouble(v));
    }

    this.pushOutput(example, 0);
  }
}