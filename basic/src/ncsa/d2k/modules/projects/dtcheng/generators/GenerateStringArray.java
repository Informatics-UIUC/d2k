package ncsa.d2k.modules.projects.dtcheng.generators;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.Utility;

public class GenerateStringArray extends InputModule {

  private String CSVStringData = "one,two,three";

  public void setCSVStringData(String value) {
    this.CSVStringData = value;
  }

  public String getCSVStringData() {
    return this.CSVStringData;
  }

  public String getModuleInfo() {
    return "This module outputs a single string array which comes from the CSVStringData property.";
  }

  public String getModuleName() {
    return "GenerateStringArray";
  }

  public String getInputName(int i) {
    switch (i) {
    default:
      return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    default:
      return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "OutputString";
    default:
      return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[Ljava.lang.String;" };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "A string corresponding to the StringData property.  ";
    default:
      return "No such output";
    }
  }

  public void doit() {
    

    String[] StringArray = Utility.parseCSVList(CSVStringData);

    this.pushOutput(StringArray, 0);
  }
}