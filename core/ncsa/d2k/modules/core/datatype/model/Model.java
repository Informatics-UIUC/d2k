package ncsa.d2k.modules.core.datatype.model;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.*;
public class Model extends PredictionModelModule /*, FunctionDouble1DArrayToDouble1DArray */ implements java.io.Serializable
  {


  protected Model(ExampleTable examples) {
    super(examples);
  }

  //public int numInputs;
  //public int numOutputs;
  //public String [] inputNames;
  //public String [] outputNames;



  //double [][][] examples;

  /*
  public double [] Evaluate(double [] inputs) throws Exception
    {
    DoubleExample [] examples = new DoubleExample[1];
    double [][] data = new double[2][];
    double [] outputs = new double[1];
    data[0] = inputs;
    data[1] = outputs;

    DoubleExample    example = new DoubleExample(data, numInputs, numOutputs, inputNames, outputNames);
    examples[0] = example;
    DoubleExampleSet exampleSet = new DoubleExampleSet(examples);


    return Evaluate(exampleSet, 0);
    }
  */

  public double [] Evaluate(ExampleTable exampleSet, int e) throws Exception
    {
    System.out.println("must override this method");
    throw new Exception();
    }

  public void Evaluate(ExampleTable exampleSet, int e, double [] outputs) throws Exception
    {
    int numOutputs = exampleSet.getNumOutputFeatures();
    double [] internalOutputs = Evaluate(exampleSet, e);
    for (int i = 0; i < numOutputs; i++)
      {
      outputs[i] = internalOutputs[i];
      }
    }

  public void print(ModelPrintOptions options) throws Exception {
    System.out.println("must override this method");
    throw new Exception();
    }

  public int getNumInputs()
    {
    return getInputColumnLabels().length;
    }

  public String[] getInputFeatureNames()
    {
    return getInputColumnLabels();
    }

  public String getInputFeatureName(int i)
    {
    return getInputName(i);
    }

  public int getNumOutputs()
    {
    return getOutputColumnLabels().length;
    }

  public String[] getOutputFeatureNames()
    {
    return getOutputColumnLabels();
    }

  public String getOutputFeatureName( int i)
    {
    return getOutputName(i);
    }







    	/**
	 *	Describe the function of this module.
	 *	@return the description of this module.
	 */
	public String getModuleInfo() {
		return "Makes predictions on a set of examples.";
	}

	/**
	 *	Get the name of this module.
	 *	@return the name of this module.
	 */
	public String getModuleName() {
		return "PredModel";
	}

	/**
	 *	The input is an ExampleTable.
	 *	@return the input types
	 */
	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return in;
	}

	/**
	 *	The input is an ExampleTable.
	 *	@param i the index of the input
	 *	@return the description of the input
	 */
	public String getInputInfo(int i) {
		return "A set of examples to make predicitons on.";
	}

	/**
	 *	Get the name of the input.
	 *	@param i the index of the input
	 *	@return the name of the input
	 */
	public String getInputName(int i) {
		return "TrainingSet";
	}

	/**
	 *	The output is a PredictionTable.
	 *	@return the output types
	 */
	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
		return out;
	}

	/**
	 *	Describe the output.
	 *	@param i the index of the output
	 *	@return the description of the output
	 */
	public String getOutputInfo(int i) {
		String out = "The input set of examples, with extra columns of "
			+"predicitions added to the end.";
		return out;
	}

	/**
	 *	Get the name of the output.
	 *	@param i the index of the output
	 *	@return the name of the output
	 */
	public String getOutputName(int i) {
		return "PredictionSet";
	}

	/**
	 *	Pull in the ExampleTable, pass it to the predict() method,
	 *	and push out the PredictionTable returned by predict();
	 */
	public void doit() throws Exception {
		ExampleTable et = (ExampleTable)pullInput(0);
		pushOutput(predict(et), 0);
	}

	/**
	 * Predict the outcomes given a set of examples.  The return value
	 * is a PredictionTable, which is the same as the input set with
	 * extra column(s) of predictions added to the end.
	 * @param value a table with a set of examples to predict on
	 * @return the input table, with extra columns for predictions
	 */
	public PredictionTable predict (Table table) {
          return null;
	}

        public void makePredictions(PredictionTable pt) {

        }

	/**
	 * Get the size of the training set that built this model.
	 * @return the size of the training set used to build this model
	 */
	public int getTrainingSetSize() {
		return 0;
	}

	/**
	 * Get the labels of the input columns.
	 * @return the labels of the input columns
	 */
	public String[] getInputColumnLabels() {
		return null;
	}

	/**
	 * Get the labels of the output columns.
	 * @return the labels of the output columns
	 */
	public String[] getOutputColumnLabels() {
		return null;
	}

	/**
	 * Get the indicies of the input columns in the training table.
	 * @return the indicies of the input columns in the training table
	 */
	public int[] getInputFeatureIndicies() {
		return null;
	}

	/**
	 * Get the indices of the output columns in the training table.
	 * @return the indices of the output columns in the training table
	 */
	public int[] getOutputFeatureIndices() {
		return null;
	}

	/**
	 * Get the data types of the input columns in the training table.  Will
	 * be Numeric or Text.
	 * @return the datatypes of the input columns in the training table
	 */
	public int[] getInputFeatureTypes() {
		return null;
	}

	/**
	 * Get the data types of the output columns in the training table.  Will
	 * be Numeric or Text.
	 * @return the data types of the output columns in the training table
	 */
	public int[] getOutputFeatureTypes() {
		return null;
	}





  }
