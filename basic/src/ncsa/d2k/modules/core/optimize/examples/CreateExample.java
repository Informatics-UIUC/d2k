package ncsa.d2k.modules.core.optimize.examples;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl;

import java.util.ArrayList;

public class CreateExample extends ComputeModule {

	public String getModuleName () {
		return "Create Example";
	}

	public String getModuleInfo () {
		return "This module creates an example for function induction given a point in control space paired with a point in objective space.  ";
	}

	public String getInputName (int i) {
		switch (i) {
			case 0:
				return "Control Parameter Point";
			case 1:
				return "Objective Parameter Point";
			default:
				return "No such input";
		}
	}

	public String getInputInfo (int i) {
		switch (i) {
			case 0:
				return "The point in control parameter space that defines the behavior of the module being optimized.  ";
			case 1:
				return "The point in objective parameter space that defines the performance of the module given the control point.  ";
			default:
				return "No such input";
		}
	}

	public String[] getInputTypes () {
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getOutputName (int i) {
		switch (i) {
			case 0:
				return "Example";
			default:
				return "NO SUCH OUTPUT!";
		}
	}

	public String getOutputInfo (int i) {
		switch (i) {
			case 0:
				return "A supervised learning example that is used for guiding the optimization process";
			default:
				return "No such output";
		}
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Example"};
		return types;
	}

	ArrayList parameterPoints;
	ArrayList objectivePoints;

	public void beginExecution () {
		parameterPoints = new ArrayList ();
		objectivePoints = new ArrayList ();
	}

	public boolean isReady () {
		if (this.getFlags()[0] > 0 || this.getFlags()[1] > 0) {
			return true;
		} else if (parameterPoints.size () > 0 && objectivePoints.size () > 0)
			return true;
		return false;
	}

	public void doit () {

		// add any new data at the end of the list.
		if (this.getFlags()[0] > 0)
			parameterPoints.add (this.pullInput (0));
		if (this.getFlags()[1] > 0)
			objectivePoints.add (this.pullInput (1));

		// if we have data to push, push it.
		if (parameterPoints.size () > 0 && objectivePoints.size () > 0) {
			ParameterPoint controlParameterPoint = (ParameterPoint) parameterPoints.remove (0);
			ParameterPoint objectiveParameterPoint = (ParameterPoint) objectivePoints.remove (0);
			int numInputs = controlParameterPoint.getNumParameters ();
			int numOutputs = objectiveParameterPoint.getNumParameters ();

			// Compile the data.
			double[][] data = new double[numInputs + numOutputs][1];
			int index = 0;
			for (int i = 0; i < numInputs; i++, index++) {
				data[index][0] = controlParameterPoint.getValue (i);
			}
			for (int i = 0; i < numOutputs; i++, index++) {
				data[index][0] = objectiveParameterPoint.getValue (i);
			}

			// get the names.
			String[] inputNames = new String[numInputs];
			int[] inputs = new int[numInputs];
			index = 0;
			for (int v = 0; v < numInputs; v++, index++) {

				// tlr change to preserve the real names of the paramters.
				inputNames[v] = controlParameterPoint.getName (v);
				if (inputNames[v] == null)
					inputNames[v] = "in" + (v + 1);
				inputs[v] = index;
			}
			String[] outputNames = new String[numOutputs];
			int[] outputs = new int[numOutputs];
			for (int v = 0; v < numOutputs; v++, index++) {

				// tlr change to preserve the real names of the ovjectives.
				outputNames[v] = objectiveParameterPoint.getName (v);
				if (outputNames[v] == null)
					outputNames[v] = "out" + (v + 1);
				outputs[v] = index;
			}

			// Make the column objects and build the table.
			int totCol = numInputs+numOutputs;
			Column [] cols = new Column [totCol];
			int colIdx = 0;
			for (; colIdx < numInputs ; colIdx++) {
				cols[colIdx] = new DoubleColumn(data[colIdx]);
				cols[colIdx].setLabel(inputNames[colIdx]);
			}
			for (int i = 0; i < numOutputs; i++){
				cols[colIdx] = new DoubleColumn(data[colIdx]);
				cols[colIdx].setLabel(outputNames[i]);
				colIdx++;
			}

			// Create the example table.
			ExampleTableImpl et = new ExampleTableImpl(cols);
			et.setInputFeatures(inputs);
			et.setOutputFeatures(outputs);

			// construct an example, first create a table.
			Example example = new ParameterPointImpl(et);
			example.setIndex (0);
			this.pushOutput (example, 0);
		}
	}
}
