package ncsa.d2k.modules.projects.dtcheng.examples;

import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.core.modules.ComputeModule;

public class GetExampleTableNumInputs extends ComputeModule
  {

  public String getModuleInfo()
    {
    return "GetExampleTableNumInputs";
  }
  public String getModuleName()
    {
    return "GetExampleTableNumInputs";
  }

  public String[] getInputTypes()
    {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }

  public String[] getOutputTypes()
    {
    String[] types = {"java.lang.Integer"};
    return types;
  }

  public String getInputInfo(int i)
    {
    switch (i) {
      case 0: return "ExampleSet";
      default: return "No such input";
    }
  }

  public String getInputName(int i)
    {
    switch(i) {
      case 0:
        return "ExampleSet";
      default: return "No such input";
    }
  }

  public String getOutputInfo(int i)
    {
    switch (i) {
      case 0: return"java.lang.Integer";
      default: return "No such output";
    }
  }

  public String getOutputName(int i)
    {
    switch(i) {
      case 0:
        return "NumInputs";
      default: return "No such output";
    }
  }

  public void doit()
    {
    ExampleTable exampleSet = (ExampleTable) this.pullInput(0);

    this.pushOutput(new Integer(exampleSet.getNumInputFeatures ()), 0);

    }
  }
