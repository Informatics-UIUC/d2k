package ncsa.d2k.modules.projects.dtcheng.io.read;

import ncsa.d2k.core.modules.InputModule;
import ncsa.d2k.modules.projects.dtcheng.streams.*;

import java.io.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class StreamDirectory extends InputModule {

  public String getModuleName() {
    return "StreamDirectory";
  }

  public String getModuleInfo() {
    return "StreamDirectory";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "Directory";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] types = { "java.io.File" };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "Directory";
    default:
      return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "File Stream";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] types = { "java.io.File" };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "File Stream";
    default:
      return "No such output";
    }
  }

  Class streamMarkerClass = (new StreamMarker()).getClass();
  public void doit() throws Exception {
    Object object = this.pullInput(0);
    if (streamMarkerClass.isInstance(object)) {
      this.pushOutput(object, 0);
      return;
    }
    
    File rootDirectoryFile = (File) object;
    File[] directoryFiles = rootDirectoryFile.listFiles();

    this.pushOutput(new StreamStartMarker(), 0);
    int numFiles = directoryFiles.length;
    for (int i = 0; i < numFiles; i++) {
      this.pushOutput(directoryFiles[i], 0);
    }
    this.pushOutput(new StreamEndMarker(), 0);
    
  }
}