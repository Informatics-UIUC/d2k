package ncsa.d2k.modules.projects.dtcheng.io.read;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;

public class ReadNumberAfterString extends InputModule {

  private boolean CSV = false;

  public void setCSV(boolean value) {
    this.CSV = value;
  }

  public boolean getCSV() {
    return this.CSV;
  }

  private String SearchString = "MyParameter=";

  public void setSearchString(String value) {
    this.SearchString = value;
  }

  public String getSearchString() {
    return this.SearchString;
  }

  public String getModuleInfo() {
    return "ReadNumberAfterString";
  }

  public String getModuleName() {
    return "ReadNumberAfterString";
  }

  public String[] getInputTypes() {
    String[] types = { "[S" };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "Array of Strings";
    default:
      return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "Array of Strings";
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[D" };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Number in array";
    default:
      return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Number in array";
    default:
      return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {

    String[] strings = (String[]) this.pullInput(0);

    if (strings == null) {
      this.pushOutput(null, 0);
      return;
    }

    int dim1Size = strings.length;
    int searchStringLength = SearchString.length();
    double number = Double.NaN;
    for (int i = 0; i < dim1Size; i++) {
      String string = strings[i];
      int index = string.indexOf(SearchString);
      if (index != -1) {
        number = Double.parseDouble(string.substring(index + searchStringLength));
        break;
      }
    }

    double[] output = new double[] { number };

    this.pushOutput(output, 0);
  }
}