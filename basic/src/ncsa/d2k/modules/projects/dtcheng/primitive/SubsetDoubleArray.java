package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class SubsetDoubleArray
    extends ComputeModule {

  


  private int StartNumber = 1;

  public void setStartNumber(int value) {
    this.StartNumber = value;
  }

  public int getStartNumber() {
    return this.StartNumber;
  }
  
  


  private int EndNumber = 1;

  public void setEndNumber(int value) {
    this.EndNumber = value;
  }

  public int getEndNumber() {
    return this.EndNumber;
  }
  
  
  
  
  
  public String getModuleName() {
    return "SubsetDoubleArray";
  }

  public String getModuleInfo() {
    return "This produce 1d double array which is the subset of the input array.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "[D";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The first  double array.";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[D"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "[D";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "[D";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[D"};
    return types;
  }

  public void doit() {

    double[] InputArray = (double[])this.pullInput(0);
    
    if (InputArray == null) {
        this.pushOutput(null, 0);
        return;
    }

    int InputArrayNumValues  = InputArray.length;
    
    int StartIndex;
    int EndIndex;
    
    if (StartNumber == -1) {
      StartIndex = 0;
    } else {
      StartIndex = StartNumber - 1;
    }
    if (EndNumber == -1) {
      EndIndex = InputArrayNumValues - 1;
    } else {
      EndIndex = EndNumber - 1;
    }
    
    int OutputArrayNumValues = EndIndex - StartIndex + 1;

    double [] OutputArray = new double[OutputArrayNumValues];
    
    //System.arraycopy(InputArray, StartIndex, OutputArray, 0, OutputArrayNumValues);
    int j = 0;
    for (int i = StartIndex; i <= EndIndex; i++) {
      OutputArray[j++] = InputArray[i];
    }

    this.pushOutput(OutputArray, 0);

  }
}