package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;

public class NeuralNetInducerOpt extends FunctionInducerOpt
  {

   int        NumHiddenLayers = 1;
   int        NumHiddensPerLayer = 10;
   double     RandLimit = 0.001;
   int        Seed = 123;
   int        Epochs = 999999999;
   double     LearningRate = 0.1;
   double     Momentum = 0.0;
   boolean    IncrementalWeightUpdates = false;
   boolean    CalculateErrors = true;
   double     ErrorThreshold = 0.0;
   int        ErrorCheckNumEpochs = 10;
   long       MaxNumWeightUpdates = 10000000;
   double     MaxCPUTime = 999999999;

   public void setControlParameters(ParameterPoint parameterPoint) {

     int i = 0;
     NumHiddenLayers = (int) parameterPoint.getValue(i++);
     NumHiddensPerLayer = (int) parameterPoint.getValue(i++);
     RandLimit = parameterPoint.getValue(i++);
     Seed = (int) parameterPoint.getValue(i++);
     Epochs = (int) parameterPoint.getValue(i++);
     LearningRate = parameterPoint.getValue(i++);
     Momentum = parameterPoint.getValue(i++);
     IncrementalWeightUpdates = false;
     if (parameterPoint.getValue(i++) > 0.5) IncrementalWeightUpdates = true;
     CalculateErrors = false;
     if (parameterPoint.getValue(i++) > 0.5) CalculateErrors = true;
     ErrorThreshold = parameterPoint.getValue(i++);
     ErrorCheckNumEpochs = (int) parameterPoint.getValue(i++);
     MaxNumWeightUpdates = (long) parameterPoint.getValue(i++);
     MaxCPUTime = parameterPoint.getValue(i++);
     
   }
   
  public String getModuleInfo()
    {
		return "Neural Net Inducer Decision Optimizable";
	}
  public String getModuleName()
    {
		return "Neural Net Inducer Decision Optimizable";
	}


  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction)
    {

    int numExamples = examples.getNumRows();
    int numInputs   = examples.getNumInputFeatures();
    int numOutputs  = examples.getNumOutputFeatures();

    if (false)
      {
      System.out.println("numExamples = " + numExamples);
      System.out.println("numInputs   = " + numInputs);
      System.out.println("numOutputs  = " + numOutputs);
      }

    BackPropNN inducer = new BackPropNN();

    inducer.createStandardNet(numInputs, numOutputs, NumHiddenLayers, NumHiddensPerLayer, RandLimit, Seed);

    inducer.train(examples, Epochs, LearningRate, Momentum,
           IncrementalWeightUpdates, CalculateErrors, ErrorThreshold,
           ErrorCheckNumEpochs, MaxNumWeightUpdates, MaxCPUTime);

    NeuralNetModel model = new NeuralNetModel(examples, inducer);

    return (Model) model;
    }


	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			case 1:
				return "input1";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
  }
