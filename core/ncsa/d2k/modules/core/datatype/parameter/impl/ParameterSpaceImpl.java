package ncsa.d2k.modules.core.datatype.parameter.impl;

import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;

public class ParameterSpaceImpl extends ExampleTableImpl implements ParameterSpace {

	/** the resolution of each property. */
	int [] res;

	/** the number of spaces. */
	int numSpaces = 0;

	/** counts of the number of paramters in each subspace. */
	int [] parameterCount = null;

	public ParameterSpaceImpl () {
		this(0);
	}
	public ParameterSpaceImpl (int numColumns) {
		super(numColumns);
	}
	public ParameterSpace createFromTable(Table table) {
		return null;
	}

	/**
	 * Instantiate a ParameterSpace from primative data types.
	 * @param names the names of the paramters.
	 * @param minValues the minimum parameter values.
	 * @param maxValues the maximum parameter values.
	 * @param defaultValues the default parameter settings.
	 * @param resolutions the parameter resolutions in terms of number of intervals.
	 * @param types the type as an integer as defined in ColumnTypes.
	 * @return a ParameterSpace.
	 */
	public ParameterSpace createFromData(String [] names,
										 double [] minValues,
										 double [] maxValues,
										 double [] defaultValues,
										 int    [] resolutions,
										 int    [] types){

		// Given the data create a paramter space object and return it.
		int numColumns = names.length;
		ParameterSpaceImpl spi = this;
		Column [] columns = new Column[numColumns];
		Column addMe = null;

		// Init all the columns of the paramter space.
		for (int i = 0; i < numColumns; i++) {
			switch (types[i]) {
				case ColumnTypes.DOUBLE:
				case ColumnTypes.FLOAT:
					double [] dvals = new double[3];
					dvals[0] = minValues[i];
					dvals[1] = maxValues[i];
					dvals[2] = defaultValues[i];
					addMe = new DoubleColumn(dvals);
					break;
				case ColumnTypes.INTEGER:
				case ColumnTypes.LONG:
					int [] ivals = new int[3];
					ivals[0] = (int)minValues[i];
					ivals[1] = (int)maxValues[i];
					ivals[2] = (int)defaultValues[i];
					addMe = new IntColumn(ivals);
					break;
				case ColumnTypes.BOOLEAN:
					boolean [] bvals = new boolean[3];
					bvals[0] = minValues[i] == 0 ? false : true;
					bvals[1] = maxValues[i] == 0 ? false : true;
					bvals[2] = defaultValues[i] == 0 ? false : true;
					addMe = new BooleanColumn(bvals);
					break;
			}
			addMe.setLabel(names[i]);
			spi.addColumn(addMe);
		}
		this.res = resolutions;
		return spi;
	}

	/**
	 * Add a new subspace to the parameter space.
	 * @param newcols the number of paramters in the new space.
	 */
	private void addSubspace(int newcols) {
		this.numSpaces++;
		int [] newcounts = new int[this.numSpaces];
		System.arraycopy(this.parameterCount, 0, newcounts, 0, this.numSpaces-1);
		this.parameterCount = newcounts;
		this.parameterCount [this.numSpaces-1] = newcols;
	}

	/**
	 * Get the number of parameters that define the space.
	 * @return an int value representing the minimum possible value of the parameter.
	 */
	public int getNumParameters() {
		return this.getNumColumns();
	}

	/**
	 * Get the name of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a string value representing the name of the parameter.
	 */
	public String getName(int parameterIndex) {
		return columns[parameterIndex].getLabel();
	}

	/**
	 * Get the parameter index of that corresponds to the given name.
	 * @return an integer representing the index of the parameters.
	 */
	public int getParameterIndex(String name) {
		for (int i = 0 ; i < this.getNumColumns(); i++) {
			if (this.getColumnLabel(i).equals(name)) return i;
		}
		return -1;
	}

	/**
	 * Get the minimum value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a double value representing the minimum possible value of the parameter.
	 */
	public double getMinValue(int parameterIndex) {
		return this.getDouble(0, parameterIndex);
	}

	/**
	 * Get the maximum value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a double value representing the minimum possible value of the parameter.
	 */
	public double getMaxValue(int parameterIndex) {
		return this.getDouble(1, parameterIndex);
	}

	/**
	 * Get the default value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a double value representing the minimum possible value of the parameter.
	 */
	public double getDefaultValue(int parameterIndex) {
		return this.getDouble(2, parameterIndex);
	}

	/**
	 * Set the minimum value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 */
	public void setMinValue(int parameterIndex, double value){
		this.setDouble(value, 0, parameterIndex);
	}

	/**
	 * Set the maximum value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @param value the value of the parameter of interest.
	 */
	public void setMaxValue(int parameterIndex, double value){
		this.setDouble(value, 1, parameterIndex);
	}

	/**
	 * Set the default value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @param value the value of the parameter of interest.
	 */
	public void setDefaultValue(int parameterIndex, double value){
		this.setDouble(value, 2, parameterIndex);
	}

	/**
	 * Get the minimum values of all parameters returned as a ParamterPoint.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return A ParamterPoint representing the minimum possible values of all parameters.
	 */
	public ParameterPoint getMinParameterPoint(){
		return null;
	}

	/**
	 * Get the maximum values of all parameters returned as a ParamterPoint.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return A ParamterPoint representing the maximum possible values of all parameters.
	 */
	public ParameterPoint getMaxParameterPoint(){
		return null;
	}

	/**
	 * Get the default values of all parameters returned as a ParamterPoint.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return A ParamterPoint representing the default values of all parameters.
	 */
	public ParameterPoint getDefaultParameterPoint(){
		return null;
	}

	/**
	 * Get the resolution of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a int value representing the number of intervals between the min and max parameter values.
	 */
	public int getResolution(int parameterIndex){
		return res[parameterIndex];
	}

	/**
	 * Get the type of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a int value representing the type of the parameter value as defined in ColumnTypes.
	 */
	public int getType(int parameterIndex) {
		return columns[parameterIndex].getType();
	}

	/**
	 * Get the number of subspaces that defines the space.
	 * @return a int value representing the number subspaces that define the space.
	 */
	public int getNumSubspaces() {
		return this.numSpaces;
	}

	/**
	 * Get the number of parameters in each subspace.
	 * @return a int array of values the number of parameters defining each subspace.
	 */
	public int [] getSubspaceNumParameters() {
		return this.parameterCount;
	}

	/**
	 * Get the subspace index of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a int value representing the subpace index number of parameter.
	 */
	public int getSubspaceIndex(int parameterIndex) {
		for (int i = 0, counter = 0 ; i < this.parameterCount.length; i++) {
			counter += parameterCount[i];
			if (counter > parameterIndex) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get the subspace parameter index of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a int value representing the subpace index number of parameter.
	 */
	public int getSubspaceParameterIndex(int parameterIndex) {return -1; }

	/**
	 * Get a subspace from the space.
	 * @param subspaceIndex the index of the subspace of interest.
	 * @return a ParameterSpace which defines the indicated subspace.
	 */
	public ParameterSpace getSubspace(int subspaceIndex){ return null; }

	/**
	 * Join two ParameterSpaces to produce a single parameter space.
	 * @param firstSpace the first of the two ParameterSpaces to join.
	 * @param secondSpace the second of the two ParameterSpaces to join.
	 * @return a ParameterSpace which defines the indicated subspace.
	 */
	public ParameterSpace joinSubspaces(ParameterSpace firstSpace, ParameterSpace secondSpace) { return null; }

	/**
	 * Split a ParameterSpace into two parameter spaces.
	 * @return an array of two ParameterSpaces which define the two subspaces, the first being the head and the second being the tail.
	 */
	public ParameterSpace [] splitSubspaces(ParameterSpace space) { return null; }
}