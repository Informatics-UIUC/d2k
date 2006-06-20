package ncsa.d2k.modules.projects.dtcheng.io.print;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;

public class PrintParameterPoint extends OutputModule {

  private boolean    Disable = false;
  public  void    setDisable (boolean value)              {       this.Disable = value;}
  public  boolean getDisable()                            {return this.Disable;}

  private String Label    = "label = ";
  public  void   setLabel (String value) {       this.Label       = value;}
  public  String getLabel ()             {return this.Label;}

  public String getModuleName() {
    return "PrintParameterPoint";
  }
  public String getModuleInfo() {
    return "This module prints a 1D double array.  ";
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
	ParameterPoint parameterPoint = (ParameterPoint) this.pullInput(0);
    int dim1Size = parameterPoint.getNumParameters();

    if (!Disable) {
      for (int d1 = 0; d1 < dim1Size; d1++) {
        System.out.println(Label + "[" + d1 + "] = " + parameterPoint.getValue(d1));
      }
    }

    this.pushOutput(parameterPoint, 0);
  }
}