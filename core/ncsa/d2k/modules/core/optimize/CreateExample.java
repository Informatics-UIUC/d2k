package ncsa.d2k.modules.core.optimize;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.continuous.*;

public class CreateExample extends ComputeModule {

  public String getModuleName() {
    return "Create Example";
  }

  public String getModuleInfo() {
    return "This module creates an example for function induction given a point in control space paired with a point in objective space.  ";
  }

  public String getInputName(int i) {
    switch(i) {
      case  0: return "Control Parameter Point";
      case  1: return "Objective Parameter Point";
      default: return "No such input";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case  0: return "The point in control parameter space that defines the behavior of the module being optimized.  ";
      case  1: return "The point in objective parameter space that defines the performance of the module given the control point.  ";
      default: return "No such input";
    }
  }
  public String[] getInputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "Example";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "A supervised learning example that is used for guiding the optimization process";
      default: return "No such output";
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