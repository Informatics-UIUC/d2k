package ncsa.d2k.modules.projects.dtcheng.streams;


import ncsa.d2k.modules.projects.dtcheng.primitive.Utility;
import ncsa.d2k.core.modules.*;
import java.util.*;

public class FlatenStream extends InputModule {
  
  private int ApplicationDepth = 1;
  public void setApplicationDepth(int value) {
    this.ApplicationDepth = value;
  }
  public int getApplicationDepth() {
    return this.ApplicationDepth;
  }
  
  public String getModuleName() {
    return "FlatenStream";
  }
  
  public String getModuleInfo() {
    return "FlatenStream";
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "StreamObject";
    }
    return "";
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "StreamObject";
    }
    return "";
  }
  
  public String[] getInputTypes() {
    String[] out = { "java.lang.Object" };
    return out;
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "StreamObject";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "StreamObject";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = { "java.lang.Object" };
    return out;
  }
  int NestingLevel;
  public void beginExecution() {
    NestingLevel = 0;
  }
  
  
  Class streamStartMarkerClass = (new StreamStartMarker()).getClass();
  Class streamEndMarkerClass = (new StreamEndMarker()).getClass();
  Vector Vector = new Vector(1, 1);
  public void doit() throws Exception {
    
    Object object = this.pullInput(0);
    
    if (streamStartMarkerClass.isInstance(object)) {
      
      NestingLevel++;
      
      if (NestingLevel <= ApplicationDepth) {
        this.pushOutput(object, 0);
      }
      return;
    }
    
    if (streamEndMarkerClass.isInstance(object)) {
      
      if (NestingLevel <= ApplicationDepth) {
        this.pushOutput(object, 0);
      }
      
      NestingLevel--;
      
      return;
    }
    
    
    this.pushOutput(object, 0);
    
  }
}