package ncsa.d2k.modules.core.prediction.regression.continuous;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;

public class LinearBiasGenerator extends ComputeModule {
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
  public String getModuleInfo() {
    return "DecisionTreeBiasGenerator";
  }
  public String getModuleName() {
    return "DecisionTreeBiasGenerator";
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

    int     numBiasDimensions = 4;
    double [] bias      = new double[numBiasDimensions];
    String [] biasNames = new String[numBiasDimensions];

    int errorFunctionIndex;
    String errorFunctionObjectFileName;

    int biasIndex = 0;

    biasNames         [biasIndex] = "NumRounds";
    bias[biasIndex] = NumRounds;
    biasIndex++;
    biasNames         [biasIndex] = "Direction";
    bias[biasIndex] = Direction;
    biasIndex++;
    biasNames         [biasIndex] = "MinOutputValue";
    bias[biasIndex] = MinOutputValue;
    biasIndex++;
    biasNames         [biasIndex] = "MaxOutputValue";
    bias[biasIndex] = MaxOutputValue;
    biasIndex++;

    ParameterPointImpl parameterPoint = new ParameterPointImpl();

    parameterPoint.createFromData(biasNames, bias);

    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.regression.continuous.LinearInducerOpt");
    }
    catch (Exception e) {
      System.out.println("could not find class");
      throw new Exception();
    }

    this.pushOutput(parameterPoint,       0);
    this.pushOutput(functionInducerClass, 1);
  }

}