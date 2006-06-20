package ncsa.d2k.modules.projects.dtcheng.io.print;

import ncsa.d2k.core.modules.*;

public class Print1DSArray extends OutputModule {

  private boolean CSV = false;

  public void setCSV(boolean value) {
    this.CSV = value;
  }

  public boolean getCSV() {
    return this.CSV;
  }

  private String Label = "Array";

  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  public String getModuleInfo() {
    return "Print1DSArray";
  }

  public String getModuleName() {
    return "Print1DSArray";
  }

  public String[] getInputTypes() {
    String[] types = { "[Ljava.lang.String;" };
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = { "[Ljava.lang.String;" };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "[Ljava.lang.String;";
    default:
      return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "[Ljava.lang.String;";
    default:
      return "No such INput";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "[Ljava.lang.String;";
    default:
      return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "[Ljava.lang.String;";
    default:
      return "No such output";
    }
  }

  public void doit() {

    String[] values = (String[]) this.pullInput(0);

    if (values == null) {
      return;
    } else {

      int dim1Size = values.length;
      for (int i = 0; i < dim1Size; i++) {
        if (CSV) {
          if (i != 0)
            System.out.print(",");
          System.out.print(values[i]);
          if (i + 1 == dim1Size)
            System.out.println();
        } else {
          System.out.println(Label + "[" + (i+1) + "] = " + values[i]);
        }
      }

    }

    this.pushOutput(values, 0);
  }
}