package ncsa.d2k.modules.projects.dtcheng.streams;

import ncsa.d2k.core.modules.ComputeModule;
import java.util.Vector;

public class CountObjects extends ComputeModule {
  
  public String getModuleName() {
    return "CountObjects";
  }

  public String getModuleInfo() {
    return "This module counts the number of non-null objects that pass through.  "
        + "The module input is passed to the output without modification.   "
        + "When it encounters a null input, it prints out the count and then resets the counter.  "
        + "The Label property is a string printed before the count is printed to distinguish it from other module output. "
        + "The ReportEndExecutionCount property specifies whether or not the current count will be printed at the end of the itineary execution.";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "InputObject";
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "Any non-null object to be counted.  ";
    default:
      return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "java.lang.Object" };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "OutputObject";
    default:
      return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "The object read from the module input.  ";
    default:
      return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "java.lang.Integer" };
    return types;
  }

  int MaxNestingDepth = 99;
  int []  Counts;
  int NestingLevel;

  public void beginExecution() {
    Counts = new int[MaxNestingDepth];
    NestingLevel = 0;
  }
  


  Class streamMarkerClass = (new StreamMarker()).getClass();
  Class streamStartMarkerClass = (new StreamStartMarker()).getClass();
  Class streamEndMarkerClass = (new StreamEndMarker()).getClass();
  
  
  public void doit() {
    
    Object object = (Object) this.pullInput(0);
    
    if (streamStartMarkerClass.isInstance(object)) {
      NestingLevel++;
      return;
    }
    if (streamEndMarkerClass.isInstance(object)) {
      this.pushOutput(new Integer(Counts[NestingLevel]), 0);
      Counts[NestingLevel] = 0;
      NestingLevel--;
      Counts[NestingLevel]++;
      return;
    }
    
    Counts[NestingLevel]++;
    
  }
}