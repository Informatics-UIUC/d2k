package ncsa.d2k.modules.projects.dtcheng.io.print;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;

public class PrintParameterPointArray extends OutputModule {

  private boolean    Disable = false;
  public  void    setDisable (boolean value)              {       this.Disable = value;}
  public  boolean getDisable()                            {return this.Disable;}

  private String Label    = "label = ";
  public  void   setLabel (String value) {       this.Label       = value;}
  public  String getLabel ()             {return this.Label;}

  public String getModuleName() {
    return "PrintParameterPointArray";
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
    String[] types = {"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    return types;
  }

  public void doit() {
	ParameterPoint [] parameterPoints = (ParameterPoint []) this.pullInput(0);
	
	int numPoints = parameterPoints.length;
    int dim1Size = parameterPoints[0].getNumParameters();

    if (!Disable) {
		for (int pointIndex = 0; pointIndex < numPoints; pointIndex++) {
		  System.out.println("pointIndex = " + pointIndex);
		  for (int parameterIndex = 0; parameterIndex < dim1Size; parameterIndex++) {
        	System.out.println(Label + "[" + parameterIndex + "] = " + parameterPoints[pointIndex].getValue(parameterIndex));
          }
		}
    }

    this.pushOutput(parameterPoints, 0);
  }
}