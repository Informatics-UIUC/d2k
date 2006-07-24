package ncsa.d2k.modules.core.optimize.samplers;

import java.beans.PropertyVetoException;
import java.util.Random;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
/**
 *
 * <p>Title: </p>
 * <p>Description: used to be ncsa.d2k.modules.core.optimize.random.RandomGriddedSampling</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author David Tcheng
 * @version 1.0
 */
public class GriddedSampler extends RandomSampler {



	/** */
	int[] indices = null;

	/**
	 * this is the dimension multiplier, contains the cardinality of that level
	 * of the space.
	 */
	long[] multiplier = null;

	/**
	 * this is the increment, the indexed distance between points, not the
	 * actual euclidean distance.
	 */
	double increment;

	/** random number generator used to compute the delta from the offset. */
	Random random = new Random();



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


	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] descriptions = new PropertyDescription[2];
		descriptions[0] = new PropertyDescription("numSamples",
				"Number of Samples",
				"The number of samples to generate.  ");
		descriptions[1] = new PropertyDescription("trace", "Trace",
				"Report each scored point in parameter space as it becomes available.");
		return descriptions;
	}

	/**
	 * returns the information about the module.
	 *
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "<p>"
				+ "      Overview: This module will generate equally spaced points (within a computed tolerance) in a parameter "
				+ "      space."
				+ "    </p>"
				+ "    <p>"
				+ "      Detailed Description: This module is given a parameter space. It will "
				+ "      generate a number of points in the paramter space equally distanced from "
				+ "      one another, then offset them by a random offset such that any point will not"
				+ "		 exceed the next possible point."
				+ "		 The maximum offset value is actually the the number of "
				+ "      possible values between two points if the points were evenly spaced."
				+ "      For example, imagine you have one parameter to optimize in the space,"
				+ "		 and the resolution for that parameter is 150. Further, say maxIterations is set to"
				+ "		 10. If the spaces were exactly equally space, there would be 15 points with 10 points"
				+ "		 between each value."
				+ "      In this case the tolerance is actually 10. When we produce an exactly equally space point"
				+ "		 in parameter space,"
				+ "		 we offset it by a random value from zero to the tolerance. In this way, we are sure that"
				+ "		 we get a fair distribution of points in the space."
				+ "      The number of parameters generated is defined by the "
				+ "      property maxIterations. The Trace property will turn on debugging output, "
				+ "      displayed when each point is generated." + "    </p>";
	}

	public void beginExecution() {
		super.beginExecution();
		multiplier = null;
	}

	/**
	 * This is the method that computes the next paramamter point and pushes it
	 * out. To do this the N dimension space is reduced to a 1D space. It is
	 * simply a range, starting at the first point on the line and continuing to
	 * the last possible point in the space. This mapping is easy, that is what
	 * the resolution of each parameter in the space provides. Once the next
	 * equally spaced point is selected, we offset the point by a random value
	 * from zero to the distance to the next point on the line. This provides
	 * better coverage than a regularly spaced grid.
	 */
	protected void pushParameterPoint() {

		int numParams = space.getNumParameters();

		// This array will contain the indexes of the increments
		// for each of the parameters. We want our points equidistant
		// throughout the space.
		if (multiplier == null) {

			// Compute the total number of points in the space.
			long integerTotal = 1;
			indices = new int[numParams];
			multiplier = new long[numParams];
			for (int i = numParams - 1; i >= 0; i--) {
				multiplier[i] = integerTotal;
				integerTotal *= space.getNumRegions(i);
			}
			increment = (double) integerTotal / (double) this.getNumSamples();
		}

		// Select the begining of the next interval.
		double current = (int) (increment * pointsPushed);

		// Now select the random offset within that interval
		current += increment * random.nextDouble();

		// Compute the intervals of the indices.
		for (int i = 0; i < numParams; i++) {
			indices[i] = (int) (current / multiplier[i]);
			current = current % multiplier[i];
		}

		// Now we have the indices of the increments, compute the floating point
		// value
		// at that interval.
		double[] point = new double[numParams];
		for (int i = numParams - 1; i >= 0; i--) {
			if (space.getNumRegions(i) == 1)
				point[i] = space.getMinValue(i);
			else {
				point[i] = ((double) indices[i])
						/ (space.getNumRegions(i) - 1.0);
				point[i] *= space.getMaxValue(i) - space.getMinValue(i);
				point[i] += space.getMinValue(i);
			}
		}

		// we have data, construct a paramter point.
		String[] names = new String[space.getNumParameters()];
		for (int i = 0; i < space.getNumParameters(); i++) {
			names[i] = space.getName(i);
		}
		ParameterPointImpl parameterPoint = (ParameterPointImpl) ParameterPointImpl
				.getParameterPoint(names, point);
		if (trace) {
			System.out.println("Equidistant Sampling Pushed point "
					+ pointsPushed + " " + parameterPoint);
		}
		this.pushOutput(parameterPoint, 0);
	}

	/**
	 * Return the human readable name of the module.
	 *
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Gridded Sampler";
	}
}
