package ncsa.d2k.modules.core.optimize;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.continuous.*;

public class CreateExample extends ComputeModule {

  public String getModuleInfo() {
    return "CreateExample";
  }
  public String getModuleName() {
    return "CreateExample";
  }

  public String getInputName(int i) {
    switch(i) {
      case  0: return "Control Paramter Point";
      case  1: return "Objective Paramter Point";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case  0: return "Control Paramter Point";
      case  1: return "Output";
      default: return "Objective Paramter Point";
    }
  }
  public String[] getInputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Example";
      default: return "No such output";
    }
  }
  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "Example";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.Example"};
    return types;
  }


  public void doit() {

    ParameterPoint   controlParameterPoint = (ParameterPoint) this.pullInput(0);
    ParameterPoint objectiveParameterPoint = (ParameterPoint) this.pullInput(1);

    int  numInputs =   controlParameterPoint.getNumParameters();
    int numOutputs = objectiveParameterPoint.getNumParameters();

    double []  inputValues = new double[numInputs];
    for (int i = 0; i < numInputs; i++) {
      inputValues[i] = controlParameterPoint.getValue(i);
    }
    double [] outputValues = new double[numOutputs];
    for (int i = 0; i < numOutputs; i++) {
      outputValues[i] = objectiveParameterPoint.getValue(i);
    }

    double [][] data = new double [][] {inputValues, outputValues};

    String [] inputNames = new String[numInputs];
    for (int v = 0; v < numInputs; v++) {
      inputNames[v] = "in" + (v + 1);
    }
    String [] outputNames = new String[numOutputs];
    for (int v = 0; v < numOutputs; v++) {
      outputNames[v] = "out" + (v + 1);
    }

    DoubleExample example = new DoubleExample(data, numInputs, numOutputs, inputNames, outputNames);

    this.pushOutput(example, 0);
  }
}