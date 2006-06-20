package ncsa.d2k.modules.projects.dtcheng.generators;

import java.io.*;
import ncsa.d2k.core.modules.*;

public class GenerateFile extends InputModule {

  private String PathString = "defaultStringValue";

  public void setPathString(String value) {
    this.PathString = value;
  }

  public String getPathString() {
    return this.PathString;
  }

  public String getModuleInfo() {
    return "This module outputs a file dirived from the PathString property.";
  }

  public String getModuleName() {
    return "GenerateFile";
  }

  public String getInputName(int i) {
    switch (i) {
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    default:
      return "NO SUCH INPUT";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "OutputString";
    default:
      return "NO SUCH OUTPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "java.io.File" };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "A string corresponding to the PathString property.  ";
    default:
      return "No such output";
    }
  }

  public void doit() {
    this.pushOutput(new File(PathString), 0);
  }
}