package ncsa.d2k.modules.projects.dtcheng.examples;
import java.util.*;
import java.util.Enumeration;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

public class ExampleTableMinMaxMode
 extends OutputModule {
  
  private String Label = "ExampleTableMinMaxMode:\t";
  public void setLabel(String value) {
    this.Label = value;
  }
  
  public String getLabel() {
    return this.Label;
  }
  public String getModuleName() {
    return "ExampleTableMinMaxMode";
  }
  
  public String getModuleInfo() {
    return "ExampleTableMinMaxMode";
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Example Table";
    }
    return "";
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The Example Table to report on";
    }
    return "";
  }
  
  public String[] getInputTypes() {
    String[] in = {
      "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
      return in;
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Example Table";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The Example Table that was input to the module without any modification";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = {
      "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
      return out;
  }
  
  public void doit() {
    
    ExampleTable exampleSet = (ExampleTable)this.pullInput(0);
    int numExamples = exampleSet.getNumRows();
    int numInputs  = exampleSet.getNumInputFeatures();
    int numOutputs = exampleSet.getNumOutputFeatures();
    System.out.println(Label + " numExamples = " + numExamples);
    
    System.out.println(Label + " numInputs = " + numInputs);
    for (int v = 0; v < numInputs; v++) {
      System.out.println(Label + "  " + (v + 1) + " : " +
       exampleSet.getInputName(v));
    }
    System.out.println(Label + " numOutputs = " + numOutputs);
    for (int v = 0; v < numOutputs; v++) {
      System.out.println(Label + "  " + (v + 1) + " : " +
       exampleSet.getOutputName(v));
    }
    
    double [] inputMins = new double[numInputs];
    double [] inputMaxs = new double[numInputs];
    double [] inputSums = new double[numInputs];
    double [] inputAvgs = new double[numInputs];
    
    Hashtable [] inputValueHashtables = new Hashtable[numInputs];
    double [] inputModes = new double[numInputs];
    
    
    for (int v = 0; v < numInputs; v++) {
      inputMins[v] = Double.POSITIVE_INFINITY;
      inputMaxs[v] = Double.NEGATIVE_INFINITY;
      inputSums[v] = 0;
      inputValueHashtables[v] = new Hashtable();
    }
    
    
    for (int e = 0; e < numExamples; e++) {
      for (int v = 0; v < numInputs; v++) {
        
        
        double value = exampleSet.getInputDouble(e, v);
        
        
        if (value < inputMins[v]) 
          inputMins[v] = value;
        
        if (value > inputMaxs[v]) 
          inputMaxs[v] = value;
        
        
        inputSums[v] += value;
        
        
        int [] count = (int []) inputValueHashtables[v].get(new Double (value));

        if (count == null) {
          count = new int[1];
          count[0] = 1;
          inputValueHashtables[v].put(new Double(value), count);
        }
        else {
          count[0]++;
        }
        
        
      }
    }
    

      for (int v = 0; v < numInputs; v++) {
        
        inputAvgs[v] = inputSums[v] / numExamples;
        

        Enumeration  enumeration = inputValueHashtables[v].keys();
        
        int maxCount      = 0;;
        
        while (enumeration.hasMoreElements()) {
          
          Double key = (Double) enumeration.nextElement();
          
          int count = ((int [])  inputValueHashtables[v].get(key))[0];
          
          if (count > maxCount) {
            maxCount = count;
            inputModes[v] = key.doubleValue();
          }
          
        }
      }
        
        // report results
        
    for (int v = 0; v < numInputs; v++) {
      System.out.println(Label + "  " + (v + 1) + " : " + exampleSet.getInputName(v));
      System.out.println(Label + "    " + "  min  = " + inputMins[v]);
      System.out.println(Label + "    " + "  max  = " + inputMaxs[v]);
      System.out.println(Label + "    " + "  sum  = " + inputSums[v]);
      System.out.println(Label + "    " + "  avg  = " + inputAvgs[v]);
      System.out.println(Label + "    " + "  mode = " + inputModes[v]);
    }
    
    
    
    this.pushOutput(exampleSet, 0);
  }
}
