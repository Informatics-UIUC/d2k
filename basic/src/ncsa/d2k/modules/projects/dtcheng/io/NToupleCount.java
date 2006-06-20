package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;
import java.util.*;

public class NToupleCount extends InputModule {

  private int NumFeaturesConsidered = 1;

  public void setNumFeaturesConsidered(int value) {
    this.NumFeaturesConsidered = value;
  }

  public int getNumFeaturesConsidered() {
    return this.NumFeaturesConsidered;
  }

  private int Feature1ColumnNumber = 1;

  public void setFeature1ColumnNumber(int value) {
    this.Feature1ColumnNumber = value;
  }

  public int getFeature1ColumnNumber() {
    return this.Feature1ColumnNumber;
  }

  private int Feature2ColumnNumber = 1;

  public void setFeature2ColumnNumber(int value) {
    this.Feature2ColumnNumber = value;
  }

  public int getFeature2ColumnNumber() {
    return this.Feature2ColumnNumber;
  }


  private String Operator = "MathSUM";

  public void setOperator(String value) {
    this.Operator = value;
  }

  public String getOperator() {
    return this.Operator;
  }


  public String getModuleName() {
    return "NToupleCount";
  }

  public String getModuleInfo() {
    return "The operator can be MathSUM or LogicOR";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    case 1:
      return "NominalCountHashtable";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    case 1:
      return "NominalCountHashtable";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] out = { "[Ljava.lang.String;", "[Ljava.util.Hashtable;" };
    return out;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "NToupleCountArray";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "NToupleCountArray";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = { "[[I" };
    return out;
  }

  boolean InitialExecution;

  int OperatorIndex;
  final int MathSUMOperterIndex = 0;
  final int LogicOROperterIndex = 1;

    boolean[] FeatureConsidered = null;


  int[] ConsideredFeatureIndices;

  Hashtable[] NominalIndexCountHashtable = null;

  Object NToupleCountArray;

  public void beginExecution() {

    InitialExecution = true;


    if (Operator.equalsIgnoreCase("MathSum")) {
      OperatorIndex = MathSUMOperterIndex;
    }
    else if (Operator.equalsIgnoreCase("LogicOR")) {
      OperatorIndex = LogicOROperterIndex;
    }
    else {
      System.out.println("Error!  Invalid Operator");
    }
    
    
    
    switch (NumFeaturesConsidered) {
    case 1: {
      ConsideredFeatureIndices = new int[1];
      ConsideredFeatureIndices[0] = Feature1ColumnNumber - 1;
      break;
    }
    case 2: {
      ConsideredFeatureIndices = new int[2];
      ConsideredFeatureIndices[0] = Feature1ColumnNumber - 1;
      ConsideredFeatureIndices[1] = Feature2ColumnNumber - 1;
      break;
    }
    }

    NominalIndexCountHashtable = null;
    NToupleCountArray = null;

  }

  public boolean isReady() {

    boolean value = false;

    if (InitialExecution && (this.getFlags()[1] > 0)) {
      value = true;
    } else {
      if (!InitialExecution && (this.getFlags()[0] > 0)) {
        value = true;
      }
    }
    return value;
  }

  public void doit() throws Exception {

    if (InitialExecution) {
      InitialExecution = false;
      NominalIndexCountHashtable = (Hashtable[]) this.pullInput(1);

      switch (NumFeaturesConsidered) {
      case 1: {
        int Size1 = NominalIndexCountHashtable[ConsideredFeatureIndices[0]].size();
        NToupleCountArray = new int[Size1];
        break;
      }
      case 2: {
        int Size1 = NominalIndexCountHashtable[ConsideredFeatureIndices[0]].size();
        int Size2 = NominalIndexCountHashtable[ConsideredFeatureIndices[1]].size();
        NToupleCountArray = new int[Size1][Size2];
        break;
      }
      }
      return;
    }

    String[] CurrentRecord = (String[]) this.pullInput(0);

    if (CurrentRecord == null) {
      this.pushOutput(NToupleCountArray, 0);
      beginExecution();
      return;
    }

    // update NToupleCounts

    switch (NumFeaturesConsidered) {
    case 1: {
      String Name1;
      Name1 = CurrentRecord[ConsideredFeatureIndices[0]];
      int Index1 = ((int[]) NominalIndexCountHashtable[ConsideredFeatureIndices[0]].get(Name1))[0];
      switch (OperatorIndex) {
      case MathSUMOperterIndex:
        ((int[]) NToupleCountArray)[Index1]++;
        break;
      case LogicOROperterIndex:
        if (((int[]) NToupleCountArray)[Index1] == 0)
          ((int[]) NToupleCountArray)[Index1]++;
        break;
      }
      break;
    }
    case 2: {
      String Name1;
      String Name2;
      Name1 = CurrentRecord[ConsideredFeatureIndices[0]];
      Name2 = CurrentRecord[ConsideredFeatureIndices[1]];
      int Index1 = ((int[]) NominalIndexCountHashtable[ConsideredFeatureIndices[0]].get(Name1))[0];
      int Index2 = ((int[]) NominalIndexCountHashtable[ConsideredFeatureIndices[1]].get(Name2))[0];
      switch (OperatorIndex) {
      case MathSUMOperterIndex:
        ((int[][]) NToupleCountArray)[Index1][Index2]++;
        break;
      case LogicOROperterIndex:
        if (((int[][]) NToupleCountArray)[Index1][Index2] == 0)
          ((int[][]) NToupleCountArray)[Index1][Index2]++;
        break;
      }
      
      break;
    }
    }

  }
}