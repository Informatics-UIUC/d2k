package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;

public class IndexArrayToStringArray extends OutputModule {

  public String getModuleInfo() {
    return "IndexArrayToStringArray";
  }

  public String getModuleName() {
    return "IndexArrayToStringArray";
  }

  public String[] getInputTypes() {
    String[] types = { "[I", "[Ljava.lang.String;"};
    return types;
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "IndexArray";
    case 1:
      return "AllStringArray";
    default:
      return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "[I";
    case 1:
      return "[Ljava.lang.String;";
    default:
      return "No such input";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[Ljava.lang.String;" };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    default:
      return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    default:
      return "No such output";
    }
  }

  public void doit() {
    int[] IndicesArray = (int[]) this.pullInput(0);
    String[] AllStrings = (String[]) this.pullInput(1);
    int size = IndicesArray.length;
    String [] Result = new String[size];

      for (int i = 0; i < size; i++) {
        Result[i] = AllStrings[IndicesArray[i]];
      }

    this.pushOutput(Result, 0);
  }
}