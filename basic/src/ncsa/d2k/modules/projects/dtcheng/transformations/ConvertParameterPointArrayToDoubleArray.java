package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;

public class ConvertParameterPointArrayToDoubleArray extends ComputeModule {

  public String getModuleName() {
    return "ConvertParameterPointArrayToDoubleArray";
  }
  public String getModuleInfo() {
    return "This module prints a 1D array of parameter points.  ";
  }

  public String getInputName(int i) {
    switch(i) {
      case 0:  return "Parameter Point";
      default: return "Error!  No such input.  ";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Parameter Point";
      default: return "Error!  No such input.  ";
    }
  }
  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:  return "Parameter Point";
      default: return "Error!  No such output.  ";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Parameter Point";
      default: return "Error!  No such output.  ";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"[[D"};
    return types;
  }

  public void doit() {
	ParameterPoint [] parameterPoints = (ParameterPoint []) this.pullInput(0);
	
	int numPoints = parameterPoints.length;
    int dim1Size = parameterPoints[0].getNumParameters();
    
    double [][] doubleArray = new double[numPoints][1];

	for (int pointIndex = 0; pointIndex < numPoints; pointIndex++) {
		doubleArray[pointIndex][0] = parameterPoints[pointIndex].getValue(0);
      }
	

    this.pushOutput(doubleArray, 0);
  }
}