package ncsa.d2k.modules.core.prediction.decisiontree.continuous;

import ncsa.d2k.core.modules.ComputeModule;

public class DecisionTreeBiasSpaceGenerator extends ComputeModule
  {
  int     numBiasDimensions = 9;

  /*
  private int     ErrorFunctionIndexMin = 0;
  public  void    setErrorFunctionIndexMin (int value) {       this.ErrorFunctionIndexMin = value;}
  public  int     getErrorFunctionIndexMin ()          {return this.ErrorFunctionIndexMin;}

  private int     ErrorFunctionIndexMax = 0;
  public  void    setErrorFunctionIndexMax (int value) {       this.ErrorFunctionIndexMax = value;}
  public  int     getErrorFunctionIndexMax ()          {return this.ErrorFunctionIndexMax;}
  */

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




  public String getModuleInfo()
    {
    return "DecisionTreeBiasSpaceGenerator";
    }
  public String getModuleName()
    {
    return "DecisionTreeBiasSpaceGenerator";
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
    double [][] biasSpaceBounds = new double[2][numBiasDimensions];
    String []   biasNames       = new String[numBiasDimensions];

    int errorFunctionIndex;
    String errorFunctionObjectFileName;

    /*
    errorFunctionIndex = ((ErrorFunction []) pullInput(0))[0];
    errorFunctionObjectFileName = ((String []) pullInput(1))[0];
    */

    int biasIndex = 0;

    /*
    biasNames         [biasIndex] = "ErrorFunctionIndex";
    //biasSpaceBounds[0][biasIndex] = ErrorFunctionIndexMin;
    //biasSpaceBounds[1][biasIndex] = ErrorFunctionIndexMax;
    biasSpaceBounds[0][biasIndex] = errorFunctionIndex;
    biasSpaceBounds[1][biasIndex] = errorFunctionIndex;
    biasIndex++;
    */
    biasNames         [biasIndex] = "MinDecompositionPopulation";
    biasSpaceBounds[0][biasIndex] = MinDecompositionPopulationMin;
    biasSpaceBounds[1][biasIndex] = MinDecompositionPopulationMax;
    biasIndex++;
    biasNames         [biasIndex] = "MinErrorReduction";
    biasSpaceBounds[0][biasIndex] = MinErrorReductionMin;
    biasSpaceBounds[1][biasIndex] = MinErrorReductionMax;
    biasIndex++;
    biasNames         [biasIndex] = "UseSimpleBooleanSplit";
    biasSpaceBounds[0][biasIndex] = UseSimpleBooleanSplitMin;
    biasSpaceBounds[1][biasIndex] = UseSimpleBooleanSplitMax;
    biasIndex++;
    biasNames         [biasIndex] = "UseMidPointBasedSplit";
    biasSpaceBounds[0][biasIndex] = UseMidPointBasedSplitMin;
    biasSpaceBounds[1][biasIndex] = UseMidPointBasedSplitMax;
    biasIndex++;
    biasNames         [biasIndex] = "UseMeanBasedSplit";
    biasSpaceBounds[0][biasIndex] = UseMeanBasedSplitMin;
    biasSpaceBounds[1][biasIndex] = UseMeanBasedSplitMax;
    biasIndex++;
    biasNames         [biasIndex] = "UsePopulationBasedSplit";
    biasSpaceBounds[0][biasIndex] = UsePopulationBasedSplitMin;
    biasSpaceBounds[1][biasIndex] = UsePopulationBasedSplitMax;
    biasIndex++;
    biasNames         [biasIndex] = "SaveNodeExamples";
    biasSpaceBounds[0][biasIndex] = SaveNodeExamplesMin;
    biasSpaceBounds[1][biasIndex] = SaveNodeExamplesMax;
    biasIndex++;
    biasNames         [biasIndex] = "UseMeanNodeModels";
    biasSpaceBounds[0][biasIndex] = UseMeanNodeModelsMin;
    biasSpaceBounds[1][biasIndex] = UseMeanNodeModelsMax;
    biasIndex++;
    biasNames         [biasIndex] = "UseLinearNodeModels";
    biasSpaceBounds[0][biasIndex] = UseLinearNodeModelsMin;
    biasSpaceBounds[1][biasIndex] = UseLinearNodeModelsMax;
    biasIndex++;


    Class functionInducerClass = null;
    try
      {
      functionInducerClass = Class.forName("ncsa.d2k.modules.core.prediction.decisiontree.continuous.DecisionTreeInducer");
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
