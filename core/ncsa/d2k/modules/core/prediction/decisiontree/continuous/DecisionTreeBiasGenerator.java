package ncsa.d2k.modules.core.prediction.decisiontree.continuous;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;

public class DecisionTreeBiasGenerator extends ComputeModule
{
  int     numBiasDimensions = 9;

  private int     MinDecompositionPopulation = 20;
  public  void    setMinDecompositionPopulation (int value) {       this.MinDecompositionPopulation = value;}
  public  int     getMinDecompositionPopulation ()          {return this.MinDecompositionPopulation;}

  private double  MinErrorReduction = 0.0;
  public  void    setMinErrorReduction (double value) {       this.MinErrorReduction = value;}
  public  double  getMinErrorReduction ()          {return this.MinErrorReduction;}

  public int UseSimpleBooleanSplit = 0;
  public void    setUseSimpleBooleanSplit (int value) {       this.UseSimpleBooleanSplit = value;}
  public int getUseSimpleBooleanSplit ()              {return this.UseSimpleBooleanSplit;}

  public int UseMidPointBasedSplit = 0;
  public void    setUseMidPointBasedSplit (int value) {       this.UseMidPointBasedSplit = value;}
  public int getUseMidPointBasedSplit ()              {return this.UseMidPointBasedSplit;}

  public int UseMeanBasedSplit = 1;
  public void    setUseMeanBasedSplit (int value) {       this.UseMeanBasedSplit = value;}
  public int getUseMeanBasedSplit ()              {return this.UseMeanBasedSplit;}

  public int UsePopulationBasedSplit = 0;
  public void    setUsePopulationBasedSplit (int value) {       this.UsePopulationBasedSplit = value;}
  public int getUsePopulationBasedSplit ()              {return this.UsePopulationBasedSplit;}

  public int SaveNodeExamples = 0;
  public void    setSaveNodeExamples (int value) {       this.SaveNodeExamples = value;}
  public int getSaveNodeExamples ()              {return this.SaveNodeExamples;}

  public int UseMeanNodeModels = 1;
  public void    setUseMeanNodeModels (int value) {       this.UseMeanNodeModels = value;}
  public int getUseMeanNodeModels ()              {return this.UseMeanNodeModels;}

  public int UseLinearNodeModels = 0;
  public void    setUseLinearNodeModels (int value) {       this.UseLinearNodeModels = value;}
  public int getUseLinearNodeModels ()              {return this.UseLinearNodeModels;}

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
    double [] bias      = new double[numBiasDimensions];
    String [] biasNames = new String[numBiasDimensions];

    int errorFunctionIndex;
    String errorFunctionObjectFileName;

    int biasIndex = 0;

    biasNames         [biasIndex] = "MinDecompositionPopulation";
    bias[biasIndex] = MinDecompositionPopulation;
    biasIndex++;
    biasNames         [biasIndex] = "MinErrorReduction";
    bias[biasIndex] = MinErrorReduction;
    biasIndex++;
    biasNames         [biasIndex] = "UseSimpleBooleanSplit";
    bias[biasIndex] = UseSimpleBooleanSplit;
    biasIndex++;
    biasNames         [biasIndex] = "UseMidPointBasedSplit";
    bias[biasIndex] = UseMidPointBasedSplit;
    biasIndex++;
    biasNames         [biasIndex] = "UseMeanBasedSplit";
    bias[biasIndex] = UseMeanBasedSplit;
    biasIndex++;
    biasNames         [biasIndex] = "UsePopulationBasedSplit";
    bias[biasIndex] = UsePopulationBasedSplit;
    biasIndex++;
    biasNames         [biasIndex] = "SaveNodeExamples";
    bias[biasIndex] = SaveNodeExamples;
    biasIndex++;
    biasNames         [biasIndex] = "UseMeanNodeModels";
    bias[biasIndex] = UseMeanNodeModels;
    biasIndex++;
    biasNames         [biasIndex] = "UseLinearNodeModels";
    bias[biasIndex] = UseLinearNodeModels;
    biasIndex++;

    ParameterPointImpl parameterPoint = new ParameterPointImpl();

    parameterPoint.createFromData(biasNames, bias);

    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.decisiontree.continuous.DecisionTreeInducerOpt");
    }
    catch (Exception e) {
      System.out.println("could not find class");
      throw new Exception();
    }

    this.pushOutput(parameterPoint,       0);
    this.pushOutput(functionInducerClass, 1);
  }
}