package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.streams.*;
import ncsa.d2k.core.modules.*;
import java.util.*;
import ncsa.d2k.modules.projects.dtcheng.examples.OldContinuousByteExampleTable;

public class FeatureVectorStreamsToExampleTable extends InputModule {
  
  public String getModuleName() {
    return "FeatureVectorStreamsToExampleTable";
  }
  
  public String getModuleInfo() {
    return "FeatureVectorStreamsToExampleTable";
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumClasses";
      case 1:
        return "ClassNames";
      case 2:
        return "InputFeatureNames";
      case 3:
        return "ClassNumExamples";
      case 4:
        return "InputFeatureVector";
    }
    return "";
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NumClasses";
      case 1:
        return "ClassNames";
      case 2:
        return "InputFeatureNames";
      case 3:
        return "ClassNumExamples";
      case 4:
        return "InputFeatureVector";
    }
    return "";
  }
  
  public String[] getInputTypes() {
    String[] out = { "java.lang.Integer",  "java.lang.Object", "[Ljava.lang.String;", "java.lang.Integer", "java.lang.Object" };
    return out;
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = { "ncsa.d2k.modules.core.datatype.table.ExampleTable" };
    return out;
  }
  
  boolean InitialExecution;
  
  int PhaseIndex;
  int NestingLevel;
  int ClassIndex;
  int ExampleIndex;
  String [] InputFeatureNames;
  public void beginExecution() {
    NestingLevel = 0;
    ClassIndex = 0;
    PhaseIndex = 0;
  }
  
  
  Class streamStartMarkerClass = (new StreamStartMarker()).getClass();
  Class streamEndMarkerClass = (new StreamEndMarker()).getClass();
  
  
  public boolean isReady() {
    
    boolean value = false;
    
    switch (PhaseIndex) {
      case 0:
        value = (this.getFlags()[0] > 0);
        break;
      case 1:
        value = (this.getFlags()[1] >= NumClasses);
        break;
      case 2:
        value = (this.getFlags()[2] > 0);
        break;
      case 3:
        value = (this.getFlags()[3]  >= NumClasses);
        break;
      case 4:
        value = (this.getFlags()[4]  > 0);
        break;
      case 5:
        value = true;
    }
    
    return value;
  }
  
  int NumClasses;
  String [] OutputFeatureNames;
  int [] ClassNumExamples;
  int TotalNumExamples;
  int NumInputFeatures;
  int NumOutputFeatures;
  int NumFeatures;
  byte[] Data;
  public void doit() throws Exception {
    
    switch (PhaseIndex) {
      
      case 0:
        NumClasses =  ((Integer) this.pullInput(0)).intValue();
        NumOutputFeatures = NumClasses;
        PhaseIndex++;
        break;
        
        
      case 1:
        OutputFeatureNames = new String[NumOutputFeatures];
        for (int i = 0; i < NumClasses; i++) {
          OutputFeatureNames[i] =  (String) this.pullInput(1);
        }
        PhaseIndex++;
        break;
        
        
      case 2:
        InputFeatureNames = (String []) this.pullInput(2);
        NumInputFeatures = InputFeatureNames.length;
        PhaseIndex++;
        break;
        
        
      case 3:
        ClassNumExamples = new int[NumClasses];
        TotalNumExamples = 0;
        for (int i = 0; i < NumClasses; i++) {
          ClassNumExamples[i] = ((Integer) this.pullInput(3)).intValue();
          TotalNumExamples += ClassNumExamples[i];
        }
        
        NumFeatures = NumInputFeatures + NumOutputFeatures;
        Data = new byte[TotalNumExamples * NumFeatures];
        
        PhaseIndex++;
        break;
        
        
      case 4:
        Object object = this.pullInput(4);
        
        if (streamStartMarkerClass.isInstance(object)) {
          
          NestingLevel++;
          
          if (NestingLevel == 1) {
            ClassIndex = 0;
            ExampleIndex = 0;
          }
          
          return;
        }
        
        if (streamEndMarkerClass.isInstance(object)) {
          
          if (NestingLevel == 2) {
            ClassIndex++;
            if (ClassIndex == NumClasses) {
              PhaseIndex++;
            }
            NestingLevel--;
            return;
          }
          
        }
        
        if (NestingLevel == 2) {
          
          double [] inputFeatures = (double []) object;
          
          for (int i = 0; i < NumInputFeatures; i++) {
            Data[ExampleIndex * NumFeatures + i] = (byte) inputFeatures[i];
          }
          
          // set output features
          Data[ExampleIndex * NumFeatures + NumInputFeatures + ClassIndex] = (byte) 1.0;
          
          ExampleIndex++;
          
        }
        break;
        
      case 5:
        OldContinuousByteExampleTable exampleSet = new OldContinuousByteExampleTable(Data, TotalNumExamples, NumInputFeatures, NumOutputFeatures, InputFeatureNames, OutputFeatureNames);
        
        this.pushOutput((ExampleTable) exampleSet, 0);
        PhaseIndex = 0;
        break;
    }
    
    
  }
}