package ncsa.d2k.modules.core.prediction.decisiontree.continuous;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;

public class DecisionTreeBiasSpaceGenerator extends ComputeModule {

  private int     MinDecompositionPopulationMin = 20;
  public  void    setMinDecompositionPopulationMin (int value) {       this.MinDecompositionPopulationMin = value;}
  public  int     getMinDecompositionPopulationMin ()          {return this.MinDecompositionPopulationMin;}

  private int     MinDecompositionPopulationMax = 20;
  public  void    setMinDecompositionPopulationMax (int value) {       this.MinDecompositionPopulationMax = value;}
  public  int     getMinDecompositionPopulationMax ()          {return this.MinDecompositionPopulationMax;}

  private double  MinErrorReductionMin = 0.0;
  public  void    setMinErrorReductionMin (double value) {       this.MinErrorReductionMin = value;}
  public  double  getMinErrorReductionMin ()          {return this.MinErrorReductionMin;}

  private double  MinErrorReductionMax = 0.0;
  public  void    setMinErrorReductionMax (double value) {       this.MinErrorReductionMax = value;}
  public  double  getMinErrorReductionMax ()          {return this.MinErrorReductionMax;}


  public int UseSimpleBooleanSplitMin = 0;
  public void    setUseSimpleBooleanSplitMin (int value) {       this.UseSimpleBooleanSplitMin = value;}
  public int getUseSimpleBooleanSplitMin ()              {return this.UseSimpleBooleanSplitMin;}
  public int UseSimpleBooleanSplitMax = 0;
  public void    setUseSimpleBooleanSplitMax (int value) {       this.UseSimpleBooleanSplitMax = value;}
  public int getUseSimpleBooleanSplitMax ()              {return this.UseSimpleBooleanSplitMax;}

  public int UseMidPointBasedSplitMin = 0;
  public void    setUseMidPointBasedSplitMin (int value) {       this.UseMidPointBasedSplitMin = value;}
  public int getUseMidPointBasedSplitMin ()              {return this.UseMidPointBasedSplitMin;}
  public int UseMidPointBasedSplitMax = 0;
  public void    setUseMidPointBasedSplitMax (int value) {       this.UseMidPointBasedSplitMax = value;}
  public int getUseMidPointBasedSplitMax ()              {return this.UseMidPointBasedSplitMax;}

  public int UseMeanBasedSplitMin = 1;
  public void    setUseMeanBasedSplitMin (int value) {       this.UseMeanBasedSplitMin = value;}
  public int getUseMeanBasedSplitMin ()              {return this.UseMeanBasedSplitMin;}
  public int UseMeanBasedSplitMax = 1;
  public void    setUseMeanBasedSplitMax (int value) {       this.UseMeanBasedSplitMax = value;}
  public int getUseMeanBasedSplitMax ()              {return this.UseMeanBasedSplitMax;}

  public int UsePopulationBasedSplitMin = 0;
  public void    setUsePopulationBasedSplitMin (int value) {       this.UsePopulationBasedSplitMin = value;}
  public int getUsePopulationBasedSplitMin ()              {return this.UsePopulationBasedSplitMin;}
  public int UsePopulationBasedSplitMax = 0;
  public void    setUsePopulationBasedSplitMax (int value) {       this.UsePopulationBasedSplitMax = value;}
  public int getUsePopulationBasedSplitMax ()              {return this.UsePopulationBasedSplitMax;}

  public int SaveNodeExamplesMin = 0;
  public void    setSaveNodeExamplesMin (int value) {       this.SaveNodeExamplesMin = value;}
  public int getSaveNodeExamplesMin ()              {return this.SaveNodeExamplesMin;}
  public int SaveNodeExamplesMax = 0;
  public void    setSaveNodeExamplesMax (int value) {       this.SaveNodeExamplesMax = value;}
  public int getSaveNodeExamplesMax ()              {return this.SaveNodeExamplesMax;}

  public int UseMeanNodeModelsMin = 1;
  public void    setUseMeanNodeModelsMin (int value) {       this.UseMeanNodeModelsMin = value;}
  public int getUseMeanNodeModelsMin ()              {return this.UseMeanNodeModelsMin;}
  public int UseMeanNodeModelsMax = 1;
  public void    setUseMeanNodeModelsMax (int value) {       this.UseMeanNodeModelsMax = value;}
  public int getUseMeanNodeModelsMax ()              {return this.UseMeanNodeModelsMax;}

  public int UseLinearNodeModelsMin = 0;
  public void    setUseLinearNodeModelsMin (int value) {       this.UseLinearNodeModelsMin = value;}
  public int getUseLinearNodeModelsMin ()              {return this.UseLinearNodeModelsMin;}
  public int UseLinearNodeModelsMax = 0;
  public void    setUseLinearNodeModelsMax (int value) {       this.UseLinearNodeModelsMax = value;}
  public int getUseLinearNodeModelsMax ()              {return this.UseLinearNodeModelsMax;}

  public String getModuleName() {
    return "DecisionTreeBiasSpaceGenerator";
  }
  public String getModuleInfo() {
    return "DecisionTreeBiasSpaceGenerator";
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

    int         numControlParameters = 9;
    double []   minControlValues = new double[numControlParameters];
    double []   maxControlValues = new double[numControlParameters];
    double []   defaults         = new double[numControlParameters];
    int    []   resolutions      = new int[numControlParameters];
    int    []   types            = new int[numControlParameters];
    String []   biasNames        = new String[numControlParameters];

    int biasIndex = 0;

    biasNames       [biasIndex] = "MinDecompositionPopulation";
    minControlValues[biasIndex] = MinDecompositionPopulationMin;
    maxControlValues[biasIndex] = MinDecompositionPopulationMax;
    defaults        [biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions     [biasIndex] = MinDecompositionPopulationMax - MinDecompositionPopulationMin + 1;
    types           [biasIndex] = ColumnTypes.INTEGER;
    biasIndex++;

    biasNames         [biasIndex] = "MinErrorReduction";
    minControlValues[biasIndex] = MinErrorReductionMin;
    maxControlValues[biasIndex] = MinErrorReductionMax;
    defaults[biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions[biasIndex] = 100;
    types[biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;

    biasNames         [biasIndex] = "UseSimpleBooleanSplit";
    minControlValues[biasIndex] = UseSimpleBooleanSplitMin;
    maxControlValues[biasIndex] = UseSimpleBooleanSplitMax;
    defaults[biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions[biasIndex] = 2;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;

    biasNames         [biasIndex] = "UseMidPointBasedSplit";
    minControlValues[biasIndex] = UseMidPointBasedSplitMin;
    maxControlValues[biasIndex] = UseMidPointBasedSplitMax;
    defaults[biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions[biasIndex] = 2;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;

    biasNames         [biasIndex] = "UseMeanBasedSplit";
    minControlValues[biasIndex] = UseMeanBasedSplitMin;
    maxControlValues[biasIndex] = UseMeanBasedSplitMax;
    defaults[biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions[biasIndex] = 2;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;

    biasNames         [biasIndex] = "UsePopulationBasedSplit";
    minControlValues[biasIndex] = UsePopulationBasedSplitMin;
    maxControlValues[biasIndex] = UsePopulationBasedSplitMax;
    defaults[biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions[biasIndex] = 2;
    types[biasIndex] = ColumnTypes.BOOLEAN;

    biasIndex++;
    biasNames         [biasIndex] = "SaveNodeExamples";
    minControlValues[biasIndex] = SaveNodeExamplesMin;
    maxControlValues[biasIndex] = SaveNodeExamplesMax;
    defaults[biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions[biasIndex] = 2;
    types[biasIndex] = ColumnTypes.BOOLEAN;

    biasIndex++;
    biasNames         [biasIndex] = "UseMeanNodeModels";
    minControlValues[biasIndex] = UseMeanNodeModelsMin;
    maxControlValues[biasIndex] = UseMeanNodeModelsMax;
    defaults[biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions[biasIndex] = 2;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;

    biasNames         [biasIndex] = "UseLinearNodeModels";
    minControlValues[biasIndex] = UseLinearNodeModelsMin;
    maxControlValues[biasIndex] = UseLinearNodeModelsMax;
    defaults[biasIndex] = (minControlValues[biasIndex] + maxControlValues[biasIndex]) / 2.0;
    resolutions[biasIndex] = 2;
    types[biasIndex] = ColumnTypes.BOOLEAN;
    biasIndex++;

    ParameterSpaceImpl parameterSpace = new ParameterSpaceImpl();
    parameterSpace.createFromData(biasNames, minControlValues, maxControlValues, defaults, resolutions, types);
    Class functionInducerClass = null;
    try {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.decisiontree.continuous.DecisionTreeInducerOpt");
    }
    catch (Exception e) {
      System.out.println("could not find class");
      throw new Exception();
    }

    this.pushOutput(parameterSpace,       0);
    this.pushOutput(functionInducerClass, 1);
  }
}