package ncsa.d2k.modules.core.prediction.mean.continuous;

import ncsa.d2k.core.modules.ComputeModule;

public class MeanBiasSpaceGenerator extends ComputeModule
  {


  public String getModuleInfo()
    {
    return "MeanBiasSpaceGenerator";
    }
  public String getModuleName()
    {
    return "MeanBiasSpaceGenerator";
    }

  public String[] getInputTypes()
    {
    String [] in = {};
    return in;
    }

  public String[] getOutputTypes()
    {
    String [] out = {"[[D",
                     "[S",
                     "java.lang.Class"};
    return out;
    }

  public String getInputInfo(int i)
    {
    return "";
    }

  public String getInputName(int i)
    {
    return "";
    }

  public String getOutputInfo(int i)
    {
    switch (i)
      {
      case 0: return "BiasSpace";
      case 1: return "BiasNames";
      case 2: return "FunctionInducerClass";
      }
    return "";
    }

  public String getOutputName(int i)
    {
    switch (i)
      {
      case 0: return "BiasSpace";
      case 1: return "BiasNames";
      case 2: return "FunctionInducerClass";
      }
    return "";
    }

  public void doit() throws Exception
    {
    int     numBiasDimensions = 0;
    String []   biasNames       = new String[numBiasDimensions];
    double [][] biasSpaceBounds = new double[2][numBiasDimensions];

    Class functionInducerClass = null;
    try
      {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.mean.continuous.MeanInducer");
      }
      catch (Exception e)
      {
        System.out.println("could not find class");
        throw new Exception();
      }


    this.pushOutput(biasSpaceBounds,      0);
    this.pushOutput(biasNames,            1);
    this.pushOutput(functionInducerClass, 2);
    }
  }
