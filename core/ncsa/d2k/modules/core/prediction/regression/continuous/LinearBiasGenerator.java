package ncsa.d2k.modules.core.prediction.regression.continuous;


import ncsa.d2k.core.modules.ComputeModule;

public class LinearBiasGenerator extends ComputeModule
  {
  private int        NumRounds = 0;
  public  void    setNumRounds (int value) {       this.NumRounds = value;}
  public  int     getNumRounds ()          {return this.NumRounds;}

  private int        Direction = 0;
  public  void    setDirection (int value) {       this.Direction = value;}
  public  int     getDirection ()          {return this.Direction;}

  private double     MinOutputValue = Double.NEGATIVE_INFINITY;
  public  void    setMinOutputValue (double value) {       this.MinOutputValue = value;}
  public  double  getMinOutputValue ()             {return this.MinOutputValue;}

  private double     MaxOutputValue = Double.POSITIVE_INFINITY;
  public  void    setMaxOutputValue (double value) {       this.MaxOutputValue = value;}
  public  double  getMaxOutputValue ()             {return this.MaxOutputValue;}


  public String getModuleInfo()
    {
    return "<html>  <head>      </head>  <body>    LinearBiasGenerator  </body></html>";
  }
  public String getModuleName()
    {
    return "LinearBiasGenerator";
  }

  public String[] getInputTypes()
    {
    String [] in = {};
    return in;
    }

  public String[] getOutputTypes()
    {
    String [] out = {"[D",
                     "[S",
                     "[Ljava.lang.Class;"};
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
      case 0: return "Bias";
      case 1: return "BiasNames";
      case 2: return "FunctionInducerClass";
      }
    return "";
    }

  public String getOutputName(int i)
    {
    switch (i)
      {
      case 0: return "Bias";
      case 1: return "BiasNames";
      case 2: return "FunctionInducerClass";
      }
    return "";
    }

  public void doit() throws Exception
    {
    int numBiasDimensions = 4;

    double [] bias      = new double[numBiasDimensions];
    String [] biasNames = new String[numBiasDimensions];

    int biasIndex = 0;

    biasNames[biasIndex]          = "NumRounds";
    bias[biasIndex] = NumRounds;
    biasIndex++;
    biasNames[biasIndex]          = "Direction";
    bias[biasIndex] = Direction;
    biasIndex++;
    biasNames[biasIndex]          = "MinOutputValue";
    bias[biasIndex] = MinOutputValue;
    biasIndex++;
    biasNames[biasIndex]          = "MaxOutputValue";
    bias[biasIndex] = MaxOutputValue;
    biasIndex++;

    Class [] functionInducerClass = new Class[1];
    try
      {
      functionInducerClass[0] = Class.forName("ncsa.d2k.modules.core.prediction.regression.continuous.LinearInducer");
    }
    catch (Exception e)
    {
      System.out.println("could not find class");
      throw new Exception();
    }


    this.pushOutput(bias,                 0);
    this.pushOutput(biasNames,            1);
    this.pushOutput(functionInducerClass, 2);
    }
  }
