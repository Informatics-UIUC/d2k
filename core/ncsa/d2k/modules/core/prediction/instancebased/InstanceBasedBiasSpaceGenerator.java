package ncsa.d2k.modules.core.prediction.instancebased;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;

public class InstanceBasedBiasSpaceGenerator extends ComputeModule {

  private int     NeighborhoodSizeMin = 20;
  public  void    setNeighborhoodSizeMin (int value) {       this.NeighborhoodSizeMin = value;}
  public  int     getNeighborhoodSizeMin ()          {return this.NeighborhoodSizeMin;}
  private int     NeighborhoodSizeMax = 20;
  public  void    setNeighborhoodSizeMax (int value) {       this.NeighborhoodSizeMax = value;}
  public  int     getNeighborhoodSizeMax ()          {return this.NeighborhoodSizeMax;}

  private double  DistanceWeightingPowerMin = 0.0;
  public  void    setDistanceWeightingPowerMin (double value) {       this.DistanceWeightingPowerMin = value;}
  public  double  getDistanceWeightingPowerMin ()          {return this.DistanceWeightingPowerMin;}
  private double  DistanceWeightingPowerMax = 0.0;
  public  void    setDistanceWeightingPowerMax (double value) {       this.DistanceWeightingPowerMax = value;}
  public  double  getDistanceWeightingPowerMax ()          {return this.DistanceWeightingPowerMax;}

  private double  ZeroDistanceValueMin = 1.0E-99;
  public  void    setZeroDistanceValueMin (double value) {       this.ZeroDistanceValueMin = value;
  if (value == 0.0)
    System.out.println("Error! ZeroDistanceValueMin = 0.0");}
    public  double  getZeroDistanceValueMin ()          {return this.ZeroDistanceValueMin;}
  private double  ZeroDistanceValueMax = 1.0E-99;
  public  void    setZeroDistanceValueMax (double value) {       this.ZeroDistanceValueMax = value;
  if (value == 0)
    System.out.println("Error! ZeroDistanceValueMax = 0.0");}
    public  double  getZeroDistanceValueMax ()          {return this.ZeroDistanceValueMax;}

  public String getModuleName() {
    return "InstanceBasedBiasSpaceGenerator";
  }
  public String getModuleInfo() {
    return "InstanceBasedBiasSpaceGenerator";
  }

  public String getInputName(int i) {
    return "";
  }
  public String getInputInfo(int i) {
    return "";
  }
  public String[] getInputTypes() {
    String [] in = {};
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Control Parameter Space";
      case 1: return "Function Inducer Class";
    }
    return "";
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Control Parameter Space";
      case 1: return "Function Inducer Class";
    }
    return "";
  }
  public String[] getOutputTypes() {
    String [] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
      "java.lang.Class"};
    return out;
  }

  public void doit() throws Exception {

    int         numControlParameters = 3;
    double []   minControlValues = new double[numControlParameters];
    double []   maxControlValues = new double[numControlParameters];
    double []   defaults         = new double[numControlParameters];
    int    []   resolutions      = new int[numControlParameters];
    int    []   types            = new int[numControlParameters];
    String []   biasNames        = new String[numControlParameters];

    int biasIndex = 0;

    biasNames       [biasIndex] = "NeighborhoodSize";
    minControlValues[biasIndex] = NeighborhoodSizeMin;
    maxControlValues[biasIndex] = NeighborhoodSizeMax;
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = NeighborhoodSizeMax - NeighborhoodSizeMin + 1;
    types           [biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;

    biasNames       [biasIndex] = "DistanceWeightingPower";
    minControlValues[biasIndex] = DistanceWeightingPowerMin;
    maxControlValues[biasIndex] = DistanceWeightingPowerMax;
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = 1000;
    types           [biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;

    biasNames       [biasIndex] = "ZeroDistanceValue";
    minControlValues[biasIndex] = ZeroDistanceValueMin;
    maxControlValues[biasIndex] = ZeroDistanceValueMax;
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = 1000;
    types           [biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;

    ParameterSpaceImpl parameterSpace = new ParameterSpaceImpl();
    parameterSpace.createFromData(biasNames, minControlValues, maxControlValues, defaults, resolutions, types);
    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.instancebased.InstanceBasedInducerOpt");
    }
    catch (Exception e) {
      System.out.println("could not find class");
      throw new Exception();
    }

    this.pushOutput(parameterSpace,       0);
    this.pushOutput(functionInducerClass, 1);
  }
}