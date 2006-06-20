package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class NeuralNetParamSpaceGenerator extends
		AbstractParamSpaceGenerator {
	
	int numBiasDimensions = 13;


	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "Parameter Space";
		case 1:
			return "Function Inducer Class";
		}
		return "";
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "Control Parameter Space for Neural Net Inducer";
		case 1:
			return "Neural Net Function Inducer Class";
		}
		return "";
	}

	public String[] getOutputTypes() {
		String[] out = {
				"ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
				"java.lang.Class" };
		return out;
	}

	/**
	 * Returns a reference to the developer supplied defaults. These are
	 * like factory settings, absolute ranges and definitions that are not
	 * mutable.
	 * @return the factory settings space.
	 */
	protected ParameterSpace getDefaultSpace() {

		int numControlParameters = numBiasDimensions;
		double[] minControlValues = new double[numControlParameters];
		double[] maxControlValues = new double[numControlParameters];
		double[] defaults = new double[numControlParameters];
		int[] resolutions = new int[numControlParameters];
		int[] types = new int[numControlParameters];
		String[] biasNames = new String[numControlParameters];

		int biasIndex = 0;

		biasNames[biasIndex] = "NumHiddenLayers";
		minControlValues[biasIndex] = 1;
		maxControlValues[biasIndex] = 4;
		defaults[biasIndex] = 1;
        resolutions[biasIndex] = (int) maxControlValues[biasIndex] - (int) minControlValues[biasIndex] + 1;
        types[biasIndex] = ColumnTypes.INTEGER;
		biasIndex++;

		biasNames[biasIndex] = "NumHiddensPerLayer";
		minControlValues[biasIndex] = 1;
		maxControlValues[biasIndex] = 100;
		defaults[biasIndex] = 20;
		resolutions[biasIndex] = (int) maxControlValues[biasIndex]
				- (int) minControlValues[biasIndex] + 1;
		types[biasIndex] = ColumnTypes.INTEGER;
		biasIndex++;

		biasNames[biasIndex] = "RandLimit";
		minControlValues[biasIndex] = 0.000001;
		maxControlValues[biasIndex] = 0.1;
		defaults[biasIndex] = 0.001;
		resolutions[biasIndex] = 10;
		types[biasIndex] = ColumnTypes.DOUBLE;
		biasIndex++;

		biasNames[biasIndex] = "Seed";
		minControlValues[biasIndex] = 1;
		maxControlValues[biasIndex] = 999999999;
		defaults[biasIndex] = 123;
		resolutions[biasIndex] = 10;
		types[biasIndex] = ColumnTypes.INTEGER;
		biasIndex++;

		biasNames[biasIndex] = "Epochs";
		minControlValues[biasIndex] = 1;
		maxControlValues[biasIndex] = 999999999;
		defaults[biasIndex] = 999999999;
		resolutions[biasIndex] = 1;
		types[biasIndex] = ColumnTypes.INTEGER;
		biasIndex++;

		biasNames[biasIndex] = "LearningRate";
		minControlValues[biasIndex] = 0.0;
		maxControlValues[biasIndex] = 1.0;
		defaults[biasIndex] = 0.1;
		resolutions[biasIndex] = 10;
		types[biasIndex] = ColumnTypes.DOUBLE;
		biasIndex++;

		biasNames[biasIndex] = "Momentum";
		minControlValues[biasIndex] = 0.0;
		maxControlValues[biasIndex] = 1.0;
		defaults[biasIndex] = 0.1;
		resolutions[biasIndex] = 10;
		types[biasIndex] = ColumnTypes.DOUBLE;

		biasIndex++;
		biasNames[biasIndex] = "IncrementalWeightUpdates";
		minControlValues[biasIndex] = 0;
		maxControlValues[biasIndex] = 1;
		defaults[biasIndex] = 0;
		resolutions[biasIndex] = 1;
		types[biasIndex] = ColumnTypes.BOOLEAN;

		biasIndex++;
		biasNames[biasIndex] = "CalculateErrors";
		minControlValues[biasIndex] = 0;
		maxControlValues[biasIndex] = 1;
		defaults[biasIndex] = 1;
		resolutions[biasIndex] = 1;
		types[biasIndex] = ColumnTypes.BOOLEAN;
		biasIndex++;

		biasNames[biasIndex] = "ErrorThreshold";
		minControlValues[biasIndex] = 0.0;
		maxControlValues[biasIndex] = 1.0;
		defaults[biasIndex] = 0.0;
		resolutions[biasIndex] = 1;
		types[biasIndex] = ColumnTypes.DOUBLE;
		biasIndex++;

		biasNames[biasIndex] = "ErrorCheckNumEpochs";
		minControlValues[biasIndex] = 1;
		maxControlValues[biasIndex] = 999999999;
		defaults[biasIndex] = 10;
		resolutions[biasIndex] = 10;
		types[biasIndex] = ColumnTypes.INTEGER;
		biasIndex++;

		biasNames[biasIndex] = "MaxNumWeightUpdates";
		minControlValues[biasIndex] = 1;
		maxControlValues[biasIndex] = 999999999;
		defaults[biasIndex]    = 1000000;
		resolutions[biasIndex] = 1;
		types[biasIndex] = ColumnTypes.INTEGER;
		biasIndex++;

		biasNames[biasIndex] = "MaxCPUTime";
		minControlValues[biasIndex] = 0.0;
		maxControlValues[biasIndex] = 999999999;
		defaults[biasIndex] = 999999999;
		resolutions[biasIndex] = 10;
		types[biasIndex] = ColumnTypes.DOUBLE;
		biasIndex++;
		
		ParameterSpace psi = new ParameterSpaceImpl();
		psi.createFromData(biasNames, minControlValues, maxControlValues,
				defaults, resolutions, types);
		return psi;

	}

	/**
	 * REturn a name more appriate to the module.
	 * @return a name
	 */
	public String getModuleName() {
		return "Neural Net Param Space Generator";
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */

	public PropertyDescription[] getPropertiesDescriptions() {

		PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

		int i = 0;
		pds[i++] = new PropertyDescription(
		    "numHiddenLayers",
				"NumHiddenLayers",
				"NumHiddenLayers");

		pds[i++] = new PropertyDescription(
		    "numHiddensPerLayer",
				"NumHiddensPerLayer",
				"NumHiddensPerLayer");

		pds[i++] = new PropertyDescription(
				"randLimit",
				"RandLimit",
				"RandLimit");

		pds[i++] = new PropertyDescription(
				"seed",
				"Seed",
				"Seed");

		pds[i++] = new PropertyDescription(
				"epochs",
				"Epochs",
				"Epochs");

		pds[i++] = new PropertyDescription(
				"learningRate",
				"LearningRate",
				"LearningRate");

		pds[i++] = new PropertyDescription(
				"momentum",
				"Momentum",
				"Momentum");

		pds[i++] = new PropertyDescription(
				"incrementalWeightUpdates",
				"IncrementalWeightUpdates",
				"IncrementalWeightUpdates");

		pds[i++] = new PropertyDescription(
				"calculateErrors",
				"CalculateErrors",
				"CalculateErrors");

		pds[i++] = new PropertyDescription(
				"errorThreshold",
				"ErrorThreshold",
				"ErrorThreshold");

		pds[i++] = new PropertyDescription(
				"errorCheckNumEpochs",
				"ErrorCheckNumEpochs",
				"ErrorCheckNumEpochs");

		pds[i++] = new PropertyDescription(
				"maxNumWeightUpdates",
				"MaxNumWeightUpdates",
				"MaxNumWeightUpdates");
		
		pds[i++] = new PropertyDescription(
			"maxCPUTime",
			"MaxCPUTime",
			"MaxCPUTime");

		return pds;
	}

	/**
	 * All we have to do here is push the parameter space and function inducer class.
	 */
	public void doit() throws Exception {

		Class functionInducerClass = null;
		try {
			functionInducerClass = Class.forName("ncsa.d2k.modules.projects.dtcheng.inducers.NeuralNetInducerOpt");
		} catch (Exception e) {
			//System.out.println("could not find class");
			//throw new Exception();
			throw new Exception(getAlias()
					+ ": could not find class NeuralNetInducerOpt ");
		}

		if (space == null)
			space = this.getDefaultSpace();
		this.pushOutput(space, 0);
		this.pushOutput(functionInducerClass, 1);
	}
}