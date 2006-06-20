package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.core.modules.*;

public class StringArrayToFeatureVector extends OutputModule {

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
    return "StringArrayToFeatureVector";
  }

  public String getModuleName() {
    return "StringArrayToFeatureVector";
  }

  public String[] getInputTypes() {
    String[] types = { "[Ljava.lang.String;", "[Ljava.lang.String;" };
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = { "[D" };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "[Ljava.lang.String;";
    case 1:
      return "[Ljava.lang.String;";
    default:
      return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    case 1:
      return "AllStringsArray";
    default:
      return "No such INput";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "[D";
    default:
      return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Feature Vector";
    default:
      return "No such output";
    }
  }

  public void doit() {

    String[] Values = (String[]) this.pullInput(0);
    String[] AllValues = (String[]) this.pullInput(1);

    if ((Values == null) || (Values == null)) {
      this.pushOutput(null, 0);
      return;
    } else {

      int ValuesSize = Values.length;
      int AllValuesSize = AllValues.length;
      double[] Result = new double[AllValuesSize];
      for (int i = 0; i < ValuesSize; i++) {
        for (int j = 0; j < AllValuesSize; j++) {
          if (Values[i].equals(AllValues[j])) {
            Result[j] = 1.0;
          }
        }
      }

      this.pushOutput(Result, 0);
    }

  }
}