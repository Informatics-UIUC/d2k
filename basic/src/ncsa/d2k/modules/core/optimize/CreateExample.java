package ncsa.d2k.modules.core.optimize;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.random.UniformSampling;

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

	// Compile the data.
	double [][] data = new double [numInputs+numOutputs][1];
	int index = 0;
	for (int i = 0 ; i < numInputs ; i++, index++) {
		data[index][0] = controlParameterPoint.getValue(i);
	}
	for (int i = 0 ; i < numOutputs ; i++, index++) {
		data[index][0] = objectiveParameterPoint.getValue(i);
	}

	// get the names.
    String [] inputNames = new String[numInputs];
	int [] inputs = new int [numInputs];
	index = 0;
    for (int v = 0; v < numInputs; v++, index++) {
      inputNames[v] = "in" + (v + 1);
	  inputs[v] = index;
    }
    String [] outputNames = new String[numOutputs];
	int [] outputs = new int [numOutputs];
	for (int v = 0; v < numOutputs; v++, index++) {
      outputNames[v] = "out" + (v + 1);
	  outputs[v] = index;
	}

	// construct an example, first create a table.
	Example example = UniformSampling.getTable(data, inputNames, outputNames, inputs, outputs, 1).getExample(0);
    this.pushOutput(example, 0);
  }
}
