package ncsa.d2k.modules.projects.dtcheng.io.read;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;

import ncsa.d2k.modules.projects.dtcheng.datatype.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.projects.dtcheng.examples.ContinuousDoubleExampleTable;

public class ReadSwansonExamples extends InputModule {
  
  //////////////////
  //  PROPERTIES  //
  //////////////////
  
  private String DataPath = "c:/data/swanson/data.tenth.xls";
  
  public void setDataPath(String value) {
    this.DataPath = value;
  }
  
  public String getDataPath() {
    return this.DataPath;
  }
  
  
  private String DescriptionPath = "c:/dat/swanson/description.xls";
  
  public void setDescriptionPath(String value) {
    this.DescriptionPath = value;
  }
  
  public String getDescriptionPath() {
    return this.DescriptionPath;
  }
  
  private boolean YoungInPositiveClass = true;
  public void setYoungInPositiveClass(boolean value) {
    this.YoungInPositiveClass = value;
  }
  public boolean getYoungInPositiveClass() {
    return this.YoungInPositiveClass;
  }
  
  private boolean OldInPositiveClass = true;
  public void setOldInPositiveClass(boolean value) {
    this.OldInPositiveClass = value;
  }
  public boolean getOldInPositiveClass() {
    return this.OldInPositiveClass;
  }
  
  private boolean APBInPositiveClass = true;
  public void setAPBInPositiveClass(boolean value) {
    this.APBInPositiveClass = value;
  }
  public boolean getAPBInPositiveClass() {
    return this.APBInPositiveClass;
  }

  private boolean PPBInPositiveClass = true;
  public void setPPBInPositiveClass(boolean value) {
    this.PPBInPositiveClass = value;
  }
  public boolean getPPBInPositiveClass() {
    return this.PPBInPositiveClass;
  }

  
  private boolean BrainInPositiveClass = true;
  public void setBrainInPositiveClass(boolean value) {
    this.BrainInPositiveClass = value;
  }
  public boolean getBrainInPositiveClass() {
    return this.BrainInPositiveClass;
  }
  
  private boolean ColonInPositiveClass = false;
  public void setColonInPositiveClass(boolean value) {
    this.ColonInPositiveClass = value;
  }
  public boolean getColonInPositiveClass() {
    return this.ColonInPositiveClass;
  }
  
  private boolean LiverInPositiveClass = false;
  public void setLiverInPositiveClass(boolean value) {
    this.LiverInPositiveClass = value;
  }
  public boolean getLiverInPositiveClass() {
    return this.LiverInPositiveClass;
  }
  
  private boolean MuscleInPositiveClass = false;
  public void setMuscleInPositiveClass(boolean value) {
    this.MuscleInPositiveClass = value;
  }
  public boolean getMuscleInPositiveClass() {
    return this.MuscleInPositiveClass;
  }
  
  
  
  public String getModuleName() {
    return "ReadSwansonExamples";
  }
  
  public String getModuleInfo() {
    return "ReadSwansonExamples";
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
      case 1:
        return "InputFeatureStrings";
      case 2:
        return "OutputFeatureStrings";
    }
    return "";
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
      case 1:
        return "InputFeatureStrings";
      case 2:
        return "OutputFeatureStrings";
    }
    return "";
  }
  
  public String[] getOutputTypes() {
    String[] out = { "ncsa.d2k.modules.core.datatype.table.ExampleTable", "[[S", "[[S" };
    return out;
  }
  
  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
  public String getInputName(int index) {
    switch (index) {
      default:
        return "NO SUCH INPUT!";
    }
  }
  
  public String getInputInfo(int index) {
    switch (index) {
      default:
        return "No such input";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }
  
  ////////////
  //  MAIN  //
  ////////////
  
  public void doit() throws Exception {
    
    char delimiterChar = (char) '\t';
    
    //////////////////////////
    //  get file dimensions //
    //////////////////////////
    
    int numLines = -1;
    int numColumns = -1;
    {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(DataPath)));
      numLines = 0;
      while ( true) {
        
        String line = bufferedReader.readLine();
        
        if (line == null) {
          break;
        }
        
        if (numLines == 0) {
          numColumns = Utility.parseList(line, delimiterChar).length;
        } else {
          int currentNumColumns =  Utility.parseList(line, delimiterChar).length;
          if (currentNumColumns != numColumns) {
            
            System.out.println("currentNumColumns(" + currentNumColumns + ") != numColumns("+numColumns+"):  "  + line);
          }
        }
        
        numLines++;
      }
      bufferedReader.close();
    }
    
    System.out.println("numLines = " + numLines);
    System.out.println("numColumns = " + numColumns);
    
    System.out.println("numLines = " + numLines);
    System.out.println("numColumns = " + numColumns);
    
      int numExamples = numColumns - 1;
      int numInputFeatures = numLines - 1;
      int numOutputFeatures = 1;
      int numFeatures = numInputFeatures + numOutputFeatures;
      
      
      double[] Data = new double[numExamples * numFeatures];
      
      
      
      String[] inputNames = new String[numInputFeatures];
      String[] outputNames = new String[numOutputFeatures];
      
      outputNames[0] = "class";
      
      
      
      
      //////////////////////////////
      // set input feature values //
      //////////////////////////////
      
      {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(DataPath)));
        
        // skip column names row
        bufferedReader.readLine();
        
        for (int i = 0; i < numInputFeatures; i++) {
          
          String line = bufferedReader.readLine();
          
          String [] fields = Utility.parseList(line, delimiterChar);
          
          inputNames[i] = fields[0];
          
          for (int e = 0; e < numExamples; e++) {
            Data[e * numFeatures + i] = Double.parseDouble(fields[e + 1]);
          }
        }
        bufferedReader.close();
        
      }
      
      
      //////////////////////////////
      // set input output values //
      //////////////////////////////
      
      
      
    
    boolean [] exampleYoung = new boolean[numExamples];
    boolean [] exampleOld = new boolean[numExamples];
    boolean [] exampleAPB = new boolean[numExamples];
    boolean [] examplePPB = new boolean[numExamples];
    boolean [] exampleBrain = new boolean[numExamples];
    boolean [] exampleColon = new boolean[numExamples];
    boolean [] exampleLiver = new boolean[numExamples];
    boolean [] exampleMuscle = new boolean[numExamples];
    
    ///////////////////////////////
    //  get example output masks //
    ///////////////////////////////
    {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(DescriptionPath)));
      bufferedReader.readLine();
        
      for (int e = 0; e < 48; e++) {
        
          
          String line = bufferedReader.readLine();
          
          String [] fields = Utility.parseList(line, delimiterChar);
          
          if (fields[1].equalsIgnoreCase("old")) {
            exampleOld[e] = true;
          } 
          if (fields[1].equalsIgnoreCase("you")) {
            exampleYoung[e] = true;
          } 
          
          if (fields[2].equalsIgnoreCase("APB")) {
            exampleAPB[e] = true;
          } 
          if (fields[2].equalsIgnoreCase("PPB")) {
            examplePPB[e] = true;
          } 
          
          
          if (fields[3].equalsIgnoreCase("brain")) {
            exampleBrain[e] = true;
          } 
          if (fields[3].equalsIgnoreCase("colon")) {
            exampleColon[e] = true;
          } 
          if (fields[3].equalsIgnoreCase("liver")) {
            exampleLiver[e] = true;
          } 
          if (fields[3].equalsIgnoreCase("muscle")) {
            exampleMuscle[e] = true;
          } 
      }
      bufferedReader.close();
    }
    
    for (int e = 0; e < numExamples; e++) {
      if (BrainInPositiveClass && exampleBrain[e]) {
        Data[e * numFeatures + numInputFeatures + 0]  = 1;
      }
      if (ColonInPositiveClass && exampleColon[e]) {
        Data[e * numFeatures + numInputFeatures + 0]  = 1;
      }
      if (LiverInPositiveClass && exampleLiver[e]) {
        Data[e * numFeatures + numInputFeatures + 0]  = 1;
      }
      if (MuscleInPositiveClass && exampleMuscle[e]) {
        Data[e * numFeatures + numInputFeatures + 0]  = 1;
      }
      if (OldInPositiveClass && exampleOld[e]) {
        Data[e * numFeatures + numInputFeatures + 0]  = 1;
      }
      if (YoungInPositiveClass && exampleYoung[e]) {
        Data[e * numFeatures + numInputFeatures + 0]  = 1;
      }
      if (APBInPositiveClass && exampleAPB[e]) {
        Data[e * numFeatures + numInputFeatures + 0]  = 1;
      }
      if (PPBInPositiveClass && examplePPB[e]) {
        Data[e * numFeatures + numInputFeatures + 0]  = 1;
      }
    }
      
      ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(Data, numExamples, numInputFeatures, numOutputFeatures, inputNames, outputNames);
      
      this.pushOutput((ExampleTable) exampleSet, 0);
  }
  
}