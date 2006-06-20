package ncsa.d2k.modules.projects.dtcheng.datatype;

import java.text.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.inducers.Model;
import ncsa.d2k.modules.projects.dtcheng.inducers.ModelPrintOptions;

public class CombinedModel extends Model implements java.io.Serializable {
	int numModels = 0;

	Model[] models;

	double[] weights;

	double weightSum;

	public CombinedModel(ExampleTable examples, Model model1, Model model2,
			double weight1, double weight2) {
		super(examples);

		this.numModels = 2;
		this.models = new Model[2];
		this.models[0] = model1;
		this.models[1] = model2;
		this.weights = new double[] { weight1, weight2 };
		weightSum = weights[0] + weights[1];
	}

	public CombinedModel(ExampleTable examples, Model model1, Model model2) {
		super(examples);

		this.numModels = 2;
		this.models = new Model[2];
		this.models[0] = model1;
		this.models[1] = model2;
		this.weights = new double[] { 1.0, 1.0 };
		weightSum = weights[0] + weights[1];
	}

	public CombinedModel(ExampleTable examples, Model model1, Model model2,
			Model model3) {
		super(examples);

		this.numModels = 3;
		this.models = new Model[3];
		this.models[0] = model1;
		this.models[1] = model2;
		this.models[2] = model3;

		this.weights = new double[] { 1.0, 1.0, 1.0 };
		weightSum = weights[0] + weights[1] + weights[2];
	}

	public void evaluate(double[] inputs, double[] outputs) throws Exception {

		int numOutputs = outputs.length;
		double[] submodelOutputs = new double[numOutputs];
		double[] combinedOutputs = new double[numOutputs];

		for (int m = 0; m < numModels; m++) {
			this.models[m].evaluate(inputs, submodelOutputs);
			for (int v = 0; v < numOutputs; v++) {
				combinedOutputs[v] += (weights[m] * submodelOutputs[v]);
			}
		}

		for (int v = 0; v < numOutputs; v++) {
			outputs[v] = combinedOutputs[v] / weightSum;
		}

	}

	public double[] evaluate(ExampleTable exampleSet, int e) throws Exception {
		double[] outputs;
		double[] combinedOutputs = new double[getNumOutputs()];

		for (int m = 0; m < numModels; m++) {
			outputs = this.models[m].evaluate(exampleSet, e);
			for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
				combinedOutputs[v] += (weights[m] * outputs[v]);
			}
		}

		for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
			combinedOutputs[v] /= weightSum;
		}

		return combinedOutputs;
	}

	public void evaluate(ExampleTable exampleSet, int e,
			double[] combinedOutputs) throws Exception {
		double[] outputs;

		for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
			combinedOutputs[v] = 0.0;
		}

		for (int m = 0; m < numModels; m++) {
			outputs = this.models[m].evaluate(exampleSet, e);
			for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
				combinedOutputs[v] += (weights[m] * outputs[v]);
			}
		}

		for (int v = 0; v < exampleSet.getNumOutputFeatures(); v++) {
			combinedOutputs[v] /= weightSum;
		}

	}

	DecimalFormat Format = new DecimalFormat();

	public void print(ModelPrintOptions options) throws Exception {

		Format.setMaximumFractionDigits(options.MaximumFractionDigits);

		for (int m = 0; m < numModels; m++) {
			this.models[m].print(options, 0);

			System.out.println();
		}

	}

}