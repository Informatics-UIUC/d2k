package ncsa.d2k.modules.core.datatype.parameter.impl;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.examples.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.core.datatype.parameter.*;
/**

The ParameterPoint object can extend ExampleImpl.
It will be input to the learning algorithm.
It should use the same column names and column order as the ParameterSpace implementation.
Note: It is important that this be an Example so that additional layers
(optimizing the optimizer etc..) can more easily be implemented.

*/
public class ParameterPointImpl extends ExampleImpl implements ParameterPoint {

	//static final long serialVersionUID = 2508941379956505568L;
	double [] values;
	String [] names;
	int [] res;
	int [] types;

	/**
	 * return a paramter point from the given arrays.
	 * @return
	 */
	static final public ParameterPoint getParameterPoint(String []names, double [] values,
			int [] res, int [] types) {
		int numColumns = names.length;
		ExampleTableImpl eti = new ExampleTableImpl();
		double [] vals = new double [1];
		for (int i = 0 ; i < values.length; i++) {
			vals [0] = values[i];
			DoubleColumn dc = new DoubleColumn(vals);
			dc.setLabel(names[i]);
			eti.addColumn(dc);
		}
		return new ParameterPointImpl(eti);
	 }

	 /**
	  *
	  * @param et
	  */
	ParameterPointImpl(ExampleTableImpl et) {
		super(et, 0);
	}

	/**
	* Get the number of parameters that define the space.
	* @return An int value representing the minimum possible value of the parameter.
	*/
	public int getNumParameters() {
		return names.length;
	}

	/**
	* Get the name of a parameter.
	* @param parameterIndex the index of the parameter of interest.
	* @return A string value representing the name of the parameter.
	*/
	public String getName(int parameterIndex) { return names[parameterIndex]; }

	/**
	* Get the resolution of a parameter.
	* @param parameterIndex the index of the parameter of interest.
	* @return An int value representing the number of intervals between the min and max parameter values.
	*/
	public int getResolution(int parameterIndex) { return res[parameterIndex]; }

	/**
	* Get the value of a parameter.
	* @param parameterIndex the index of the parameter of interest.
	* @return a double value representing the minimum possible value of the parameter.
	*/
	public double getValue(int parameterIndex) { return values[parameterIndex]; }
} /* ParameterPoint */