package ncsa.d2k.modules.core.prediction.instancebased;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;

public class InstanceBasedBiasGenerator extends ComputeModule {

  private int     NeighborhoodSize = 20;
  public  void    setNeighborhoodSize (int value) {       this.NeighborhoodSize = value;}
  public  int     getNeighborhoodSize ()          {return this.NeighborhoodSize;}

  private double  DistanceWeightingPower = 0.0;
  public  void    setDistanceWeightingPower (double value) {       this.DistanceWeightingPower = value;}
  public  double  getDistanceWeightingPower ()          {return this.DistanceWeightingPower;}

  private double  ZeroDistanceValue = 1.0E-99;
  public  void    setZeroDistanceValue (double value) {       this.ZeroDistanceValue = value;
  if (value == 0.0)
    System.out.println("Error! ZeroDistanceValue = 0.0");}
    public  double  getZeroDistanceValue ()          {return this.ZeroDistanceValue;}


  public String getModuleInfo() {
    return "InstanceBasedBiasGenerator";
  }
  public String getModuleName() {
    return "InstanceBasedBiasGenerator";
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
      case 0: return "Control Parameters";
      case 1: return "Function Inducer Class";
      default: return "Error!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Control Parameters";
      case 1: return "Function Inducer Class";
      default: return "Error!";
    }
  }
  public String[] getOutputTypes() {
    String [] out = {
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
      "java.lang.Class"
    };
    return out;
  }


  public void doit() throws Exception {

    int     numBiasDimensions = 3;
    double [] bias      = new double[numBiasDimensions];
    String [] biasNames = new String[numBiasDimensions];

    int errorFunctionIndex;
    String errorFunctionObjectFileName;

    int biasIndex = 0;

    biasNames         [biasIndex] = "NeighborhoodSize";
    bias[biasIndex] = NeighborhoodSize;
    biasIndex++;
    biasNames         [biasIndex] = "DistanceWeightingPower";
    bias[biasIndex] = DistanceWeightingPower;
    biasIndex++;
    biasNames         [biasIndex] = "ZeroDistanceValue";
    bias[biasIndex] = ZeroDistanceValue;
    biasIndex++;

    ParameterPointImpl parameterPoint = new ParameterPointImpl();

    parameterPoint.createFromData(biasNames, bias);

    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.instancebased.InstanceBasedInducerOpt");
    }
    catch (Exception e) {
      System.out.println("could not find class");
      throw new Exception();
    }

    this.pushOutput(parameterPoint,       0);
    this.pushOutput(functionInducerClass, 1);
  }
}
