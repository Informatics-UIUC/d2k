package ncsa.d2k.modules.core.prediction.instancebased;


import ncsa.d2k.core.modules.ComputeModule;

public class InstanceBasedBiasSpaceGenerator extends ComputeModule {

  private int     P1_NeighborhoodSizeMin = 20;
  public  void    setP1_NeighborhoodSizeMin (int value) {       this.P1_NeighborhoodSizeMin = value;}
  public  int     getP1_NeighborhoodSizeMin ()          {return this.P1_NeighborhoodSizeMin;}
  private int     P1_NeighborhoodSizeMax = 20;
  public  void    setP1_NeighborhoodSizeMax (int value) {       this.P1_NeighborhoodSizeMax = value;}
  public  int     getP1_NeighborhoodSizeMax ()          {return this.P1_NeighborhoodSizeMax;}

  private double  P2_DistanceWeightingPowerMin = 0.0;
  public  void    setP2_DistanceWeightingPowerMin (double value) {       this.P2_DistanceWeightingPowerMin = value;}
  public  double  getP2_DistanceWeightingPowerMin ()          {return this.P2_DistanceWeightingPowerMin;}
  private double  P2_DistanceWeightingPowerMax = 0.0;
  public  void    setP2_DistanceWeightingPowerMax (double value) {       this.P2_DistanceWeightingPowerMax = value;}
  public  double  getP2_DistanceWeightingPowerMax ()          {return this.P2_DistanceWeightingPowerMax;}

  private double  P3_ZeroDistanceValueMin = 1.0E-99;
  public  void    setP3_ZeroDistanceValueMin (double value) {       this.P3_ZeroDistanceValueMin = value;
  if (value == 0.0)
    System.out.println("Error! P3_ZeroDistanceValueMin = 0.0");}
    public  double  getP3_ZeroDistanceValueMin ()          {return this.P3_ZeroDistanceValueMin;}
  private double  P3_ZeroDistanceValueMax = 1.0E-99;
  public  void    setP3_ZeroDistanceValueMax (double value) {       this.P3_ZeroDistanceValueMax = value;
  if (value == 0)
    System.out.println("Error! P3_ZeroDistanceValueMax = 0.0");}
    public  double  getP3_ZeroDistanceValueMax ()          {return this.P3_ZeroDistanceValueMax;}

  public String getModuleInfo() {
    return "InstanceBasedBiasSpaceGenerator";
  }
  public String getModuleName() {
    return "InstanceBasedBiasSpaceGenerator";
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }


  public String getInputInfo(int i) {
    switch (i) {
      default: return "No such input";
    }
  }

  public String getInputName(int i) {
    switch(i) {
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "BiasSpace";
      case 1: return "BiasNames";
      case 2: return "FunctionInducerClass";
    }
    return "";
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0: return "BiasSpace";
      case 1: return "BiasNames";
      case 2: return "FunctionInducerClass";
    }
    return "";
  }

  public String[] getOutputTypes()
    {
    String [] out = {"[[D", "[S", "java.lang.Class"};
    return out;
    }

  public void doit() throws Exception
  {
    int     numBiasDimensions = 3;

    double [][] biasSpaceBounds = new double[2][numBiasDimensions];
    String []   biasNames       = new String[numBiasDimensions];

    int biasIndex = 0;

    biasNames[biasIndex]          = "NeighborhoodSize";
    biasSpaceBounds[0][biasIndex] = P1_NeighborhoodSizeMin;
    biasSpaceBounds[1][biasIndex] = P1_NeighborhoodSizeMax;
    biasIndex++;
    biasNames[biasIndex]          = "DistanceWeightingPower";
    biasSpaceBounds[0][biasIndex] = P2_DistanceWeightingPowerMin;
    biasSpaceBounds[1][biasIndex] = P2_DistanceWeightingPowerMax;
    biasIndex++;
    biasNames[biasIndex]          = "ZeroDistanceValue";
    biasSpaceBounds[0][biasIndex] = P3_ZeroDistanceValueMin;
    biasSpaceBounds[1][biasIndex] = P3_ZeroDistanceValueMax;
    biasIndex++;



    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.projects.dtcheng.InstanceBasedInducer");
    }
    catch (Exception e) {
      System.out.println("could not find class");
      throw new Exception();
    }

    this.pushOutput(biasSpaceBounds,      0);
    this.pushOutput(biasNames,            1);
    this.pushOutput(functionInducerClass, 2);
  }
}