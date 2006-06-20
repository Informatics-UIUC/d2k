package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.util.Date;

import ncsa.d2k.core.modules.*;

public class TestComparisonTime extends ComputeModule {
  
  
  private long NumberOfComparisons = 10;
  public void setNumberOfComparisons(long value) {
    this.NumberOfComparisons = value;
  }
  public long getNumberOfComparisons() {
    return this.NumberOfComparisons;
  }
  
  public String getModuleInfo() {
    return "TestComparisonTime";
  }
  
  public String getModuleName() {
    return "TestComparisonTime";
  }
  
  public String[] getInputTypes() {
    String[] types = { , };
    return types;
  }
  
  public String[] getOutputTypes() {
    String[] types = { , };
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    default:
      return "No such input";
    }
  }
  
  public String getInputName(int i) {
    switch (i) {
    default:
      return "No such input";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    default:
      return "No such output";
    }
  }
  
  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Object";
    default:
      return "NO SUCH OUTPUT!";
    }
  }
  
  public void doit() {
    boolean flagToFlip = true;
    long endTime = Long.MIN_VALUE;
    long startTime = System.currentTimeMillis();
    System.out.println("Starting at " + new Date() + " or in milliseconds: " + startTime);
    
    for (long compIndex = 0; compIndex < NumberOfComparisons; compIndex++) {
      if (flagToFlip) {
        flagToFlip = !flagToFlip;
      } else {
        flagToFlip = !flagToFlip;
      }
    }
    
    
    endTime = System.currentTimeMillis();
    System.out.println("Done with " + NumberOfComparisons + " comparisons and negations at " +
        new Date() + " or in milliseconds: " + endTime);
    System.out.println("  DeltaTime = " + (endTime - startTime)
        + "; rate = " + (NumberOfComparisons/(endTime - startTime)));
  
  }
}











