package ncsa.d2k.modules.core.optimize.samplers;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.Random;
import java.util.ArrayList;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;

public class RandomSampler extends ComputeModule {

	/** these are the paramter points to test. */
	protected int pointsPushed = 0;

	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] descriptions = new PropertyDescription[4];
		descriptions[0] = new PropertyDescription("numSamples", "Number of Samples",
				"Number of points in parameter space to sample.  ");
		descriptions[1] = new PropertyDescription("seed", "Random Number Seed",
				"This integer is use to seed the random number generator which is used to select points in parameter space.");
		descriptions[2] = new PropertyDescription("trace", "Trace",
				"Report each scored point in parameter space as it becomes available.");
		descriptions[3] = new PropertyDescription(
				"useGridOfRegions",
				"Use Grid Of Regions",
				"If this parameter is true, it will use the number regions defined in the paramter space to define the boundary points that are sampled.  " +
				"If this parameter is false, then sampling occurs by randomly selecting points between the min and max values.  ");

		return descriptions;
	}

	private int numSamples = 100;

	public void setNumSamples(int value) throws PropertyVetoException {
		if (value < 1) {
			throw new PropertyVetoException(" < 1", null);
		}
		this.numSamples = value;
	}

	public int getNumSamples() {
		return this.numSamples;
	}

	private int seed = 1;

	public void setSeed(int value) {
		this.seed = value;
	}

	public int getSeed() {
		return this.seed;
	}

	protected boolean trace = false;

	public void setTrace(boolean value) {
		this.trace = value;
	}

	public boolean getTrace() {
		return this.trace;
	}

	private boolean useGridOfRegions = false;

	public void setUseGridOfRegions(boolean value) {
		this.useGridOfRegions = value;
	}

	public boolean getUseGridOfRegions() {
		return this.useGridOfRegions;
	}

	public String getModuleName() {
		return "Random Sampler";
	}

	public String getModuleInfo() {
		return "<p>      Overview: Generate random points in a space defined by a parameter space       input"
				+ " until we push a user defined maximum number of points, we we       converge to a user defined"
				+ " optima.    </p>    <p>      Detailed Description: This module will produce <i>Maximum Number"
				+ " of       Iterations</i> points in parameter space, unless it converges before       generating"
				+ " that many points. It will produce only one point per       invocation, unless it has already"
				+ " produced all the points it is going to       and it is just waiting for scored points to come"
				+ " back. This module will       not wait for a scored point to come back before producing the"
				+ " next one,       it will produce as many poiints as it can, and it will remain enabled     "
				+ "  until all those points are produced, or it has converged. The module       converges if a"
				+ " score exceeds the property named <i>Objective Threashhold</i>. The Random Seed can be set to"
				+ " a positive value to cause this module to       produce the same points, given the same inputs,"
				+ " on multiple runs. <i>      Trace</i> and <i>Verbose Output</i> properties can be set to produce"
				+ " a       little or a lot of console output respectively. If <i>UseRegions</i>"
				+ " is not set, the number of regions value from the parameter space object will be ignored. We"
				+ " can minimize the objective score by setting       the <i>Minimize Objective Score</i> property" + " to true.    </p>";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "Control Parameter Space";
		default:
			return "NO SUCH INPUT!";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "The Control Parameter Space to search";
		default:
			return "No such input";
		}
	}

	public String[] getInputTypes() {
		String[] types = { "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace" };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "Parameter Point";
		case 1:
			return "Number Points";
		default:
			return "NO SUCH OUTPUT!";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "The next Parameter Point selected for evaluation";
		case 1:
			return "This is the number of parameter points to produce";
		default:
			return "No such output";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint", "java.lang.Integer" };
		return types;
	}

	private Random randomNumberGenerator = null;

	/**
	 * Init the standard fields
	 */
	public void beginExecution() {
		pointsPushed = -1;
		randomNumberGenerator = new Random(seed);
	}

	/**
	 * There are two states, searching a space, and waiting for a space. When we are
	 * waiting, we have no space to search, all previous spaces have been searched. When
	 * we receive another paramter space, we will search it. While searching, we have
	 * a space and we are pushing points in that space. We will push <code>maxIteration</code>
	 * points, one periteration. When we have pushed all of them, we are done.
	 * @return true if we are ready to execute.
	 */
	public boolean isReady() {
		if (this.pointsPushed == -1 && this.getInputPipeSize(0) == 0) {
			return false;
		} else {
			return true;
		}
	}

	ParameterSpace space;

	/**
	 * We do one of two things, we either store the newly aquired space input, and reset the
	 * pointsPushed value, or we push another point
	 */
	public void doit() {
		if (this.pointsPushed == -1) {
			this.pointsPushed = 0;
			space = (ParameterSpace) this.pullInput(0);
			this.pushOutput(new Integer(this.numSamples), 1);
		} else {
			this.pushParameterPoint();
			this.pointsPushed++;
			if (this.pointsPushed == numSamples) {

				// we have traversed this space, start on the next one.
				this.pointsPushed = -1;
				space = null;
			}
		}
	}

	/**
	 * Push another paramter point, and update the accounting.
	 */
	protected void pushParameterPoint() {
		int numParams = space.getNumParameters();
		double[] point = new double[numParams];

		// Create one point in parameter space.
		for (int i = 0; i < numParams; i++) {
			double range = space.getMaxValue(i) - space.getMinValue(i);
			if (useGridOfRegions) {
				int numRegions = space.getNumRegions(i);

				// This would be an error on the users part, resolution should never be zero.
				double increment;
				if (numRegions <= 0) {
					increment = 0;
					numRegions = 1;
				} else {
					increment = range / (double) numRegions;
				}
				point[i] = space.getMinValue(i) + increment * randomNumberGenerator.nextInt(numRegions + 1);
			} else {
				switch (space.getType(i)) {
				case ColumnTypes.DOUBLE:
					point[i] = space.getMinValue(i) + range * randomNumberGenerator.nextDouble();
					break;
				case ColumnTypes.FLOAT:
					point[i] = space.getMinValue(i) + range * randomNumberGenerator.nextFloat();
					break;
				case ColumnTypes.INTEGER:
					if ((int) range == 0) {
						point[i] = space.getMinValue(i);
					} else {
						point[i] = space.getMinValue(i) + randomNumberGenerator.nextInt((int) (range + 1));
					}
					break;
				case ColumnTypes.BOOLEAN:
					if ((int) range == 0) {
						point[i] = space.getMinValue(i);
					} else {
						point[i] = space.getMinValue(i) + randomNumberGenerator.nextInt((int) (range + 1));
					}
					break;
				}
			}
		}

		// we have data, construct a paramter point.
		String[] names = new String[space.getNumParameters()];
		for (int i = 0; i < space.getNumParameters(); i++) {
			names[i] = space.getName(i);
		}
		ParameterPointImpl parameterPoint = (ParameterPointImpl) ParameterPointImpl.getParameterPoint(names, point);
		if (trace) {
			System.out.println("RandomSampling: Pushed point " + pointsPushed + " " + parameterPoint);

		}
		this.pushOutput(parameterPoint, 0);
	}

}
