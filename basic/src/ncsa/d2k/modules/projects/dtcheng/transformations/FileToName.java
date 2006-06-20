package ncsa.d2k.modules.projects.dtcheng.transformations;

import java.io.*;
import ncsa.d2k.core.modules.*;

public class FileToName extends ComputeModule {


  public String getModuleName() {
    return "FileToName";
  }

  public String getModuleInfo() {
    return "This module gets the file name from a file object and returns it as a string.  ";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "String";
    default:
      return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "String";
    default:
      return "No such output";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "java.io.File" };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "String";
    default:
      return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "String";
    default:
      return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "java.lang.String" };
    return types;
  }

  
  
  
  
  public void doit() throws Exception {


    File file = (File) this.pullInput(0);
    

    if (file == null)  {  
         this.pushOutput(null, 0);
      return;
    }
    

    this.pushOutput(file.getName(), 0);

  }
}