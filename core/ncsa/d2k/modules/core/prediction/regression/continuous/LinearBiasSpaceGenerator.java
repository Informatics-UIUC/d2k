package ncsa.d2k.modules.core.prediction.regression.continuous;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;

public class LinearBiasSpaceGenerator extends ComputeModule {

  private int        NumRoundsMin = 0;
  public  void    setNumRoundsMin (int value) {       this.NumRoundsMin = value;}
  public  int     getNumRoundsMin ()          {return this.NumRoundsMin;}
  private int        NumRoundsMax = 0;
  public  void    setNumRoundsMax (int value) {       this.NumRoundsMax = value;}
  public  int     getNumRoundsMax ()          {return this.NumRoundsMax;}

  private int        DirectionMin = 0;
  public  void    setDirectionMin (int value) {       this.DirectionMin = value;}
  public  int     getDirectionMin ()          {return this.DirectionMin;}
  private int        DirectionMax = 0;
  public  void    setDirectionMax (int value) {       this.DirectionMax = value;}
  public  int     getDirectionMax ()          {return this.DirectionMax;}

  private double     MinOutputValueMin = Double.NEGATIVE_INFINITY;
  public  void    setMinOutputValueMin (double value) {       this.MinOutputValueMin = value;}
  public  double  getMinOutputValueMin ()             {return this.MinOutputValueMin;}
  private double     MinOutputValueMax = Double.NEGATIVE_INFINITY;
  public  void    setMinOutputValueMax (double value) {       this.MinOutputValueMax = value;}
  public  double  getMinOutputValueMax ()             {return this.MinOutputValueMax;}

  private double     MaxOutputValueMin = Double.POSITIVE_INFINITY;
  public  void    setMaxOutputValueMin (double value) {       this.MaxOutputValueMin = value;}
  public  double  getMaxOutputValueMin ()             {return this.MaxOutputValueMin;}
  private double     MaxOutputValueMax = Double.POSITIVE_INFINITY;
  public  void    setMaxOutputValueMax (double value) {       this.MaxOutputValueMax = value;}
  public  double  getMaxOutputValueMax ()             {return this.MaxOutputValueMax;}

  public String getModuleName() {
    return "LinearBiasSpaceGenerator";
  }
  public String getModuleInfo() {
    return "LinearBiasSpaceGenerator";
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

    int         numControlParameters = 4;
    double []   minControlValues = new double[numControlParameters];
    double []   maxControlValues = new double[numControlParameters];
    double []   defaults         = new double[numControlParameters];
    int    []   resolutions      = new int[numControlParameters];
    int    []   types            = new int[numControlParameters];
    String []   biasNames        = new String[numControlParameters];

    int biasIndex = 0;

    biasNames       [biasIndex] = "NumRounds";
    minControlValues[biasIndex] = NumRoundsMin;
    maxControlValues[biasIndex] = NumRoundsMax;
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = NumRoundsMax - NumRoundsMin + 1;
    types           [biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;

    biasNames       [biasIndex] = "Direction";
    minControlValues[biasIndex] = DirectionMin;
    maxControlValues[biasIndex] = DirectionMax;
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = DirectionMax - DirectionMin + 1;
    types           [biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;

    biasNames       [biasIndex] = "MinOutputValue";
    minControlValues[biasIndex] = MinOutputValueMin;
    maxControlValues[biasIndex] = MinOutputValueMax;
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = 1000;
    types           [biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;

    biasNames       [biasIndex] = "MaxOutputValue";
    minControlValues[biasIndex] = MaxOutputValueMin;
    maxControlValues[biasIndex] = MaxOutputValueMax;
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = 1000;
    types           [biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;

    ParameterSpaceImpl parameterSpace = new ParameterSpaceImpl();
    parameterSpace.createFromData(biasNames, minControlValues, maxControlValues, defaults, resolutions, types);
    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.regression.continuous.LinearInducerOpt");
    }
    catch (Exception e) {
      System.out.println("could not find class");
      throw new Exception();
    }

    this.pushOutput(parameterSpace,       0);
    this.pushOutput(functionInducerClass, 1);
  }
}
