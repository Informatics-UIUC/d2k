package ncsa.d2k.modules.projects.dtcheng.streams;


import ncsa.d2k.modules.projects.dtcheng.primitive.Utility;
import ncsa.d2k.core.modules.*;
import java.util.*;

public class RandomStreamSplit extends InputModule {
  
  private int ApplicationDepth = 1;
  public void setApplicationDepth(int value) {
    this.ApplicationDepth = value;
  }
  public int getApplicationDepth() {
    return this.ApplicationDepth;
  }
  private double Fraction = 0.9;
  public void setFraction(double value) {
    this.Fraction = value;
  }
  public double getFraction() {
    return this.Fraction;
  }
  
  public String getModuleName() {
    return "RandomStreamSplit";
  }
  
  public String getModuleInfo() {
    return "RandomStreamSplit";
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
    String[] out = { "java.lang.Object", "java.lang.Object" };
    return out;
  }
  
  boolean InitialExecution;
  
  int NestingLevel;
  int ObjectCount;
  public void beginExecution() {
    NestingLevel = 0;
    ObjectCount = 0;
  }
  
  
  Class streamStartMarkerClass = (new StreamStartMarker()).getClass();
  Class streamEndMarkerClass = (new StreamEndMarker()).getClass();
  Vector Vector = new Vector(1, 1);
  public void doit() throws Exception {
    
    Object object = this.pullInput(0);
    
    if (streamStartMarkerClass.isInstance(object)) {
      
      NestingLevel++;
      
      if (NestingLevel == ApplicationDepth) {
        Vector.clear();
        ObjectCount = 0;
      }
      this.pushOutput(object, 0);
      this.pushOutput(object, 1);
      return;
    }
    
    if (streamEndMarkerClass.isInstance(object)) {
      
      if (NestingLevel == ApplicationDepth) {
        
        int firstPartitionSize = (int) (ObjectCount * Fraction);
        int secondPartitionSize = ObjectCount - firstPartitionSize;
        int [] RandomizedIndices = Utility.randomIntArray(123, ObjectCount);
        
        for (int e = 0; e < ObjectCount; e++) {
          if (e < firstPartitionSize) {
            this.pushOutput(Vector.elementAt(RandomizedIndices[e]), 0);
          } else {
            this.pushOutput(Vector.elementAt(RandomizedIndices[e]), 1);
          }
        }
      }
      this.pushOutput(object, 0);
      this.pushOutput(object, 1);
      
      NestingLevel--;

      return;
    }
    
    
    if (NestingLevel == ApplicationDepth) {
      Vector.add(object);
      ObjectCount++;
    } else
      //pass object through
    {
      this.pushOutput(object, 0);
    }
    
  }
}