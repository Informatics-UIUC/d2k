package ncsa.d2k.modules.core.discovery.pca;

import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class PCAParamSpaceGenerator extends AbstractParamSpaceGenerator
	implements java.io.Serializable{

	int numInputColumns=2;

	/**doit.
	
		pulls in the example table to get the number of input
		columns and then calls the superclass's doit
		*/
	public void doit() throws Exception {
		ExampleTable et=(ExampleTable)pullInput(0);
		numInputColumns=et.getNumInputFeatures();
		space = this.getDefaultSpace();
		this.pushOutput(space, 0);
	}	

	
	/**
	 * Returns a reference to the developer supplied defaults. These are
	 * like factory settings, absolute ranges and definitions that are not
	 * mutable.
	 * @return the factory settings space.
	 */
	protected ParameterSpace getDefaultSpace(){
		int numParams=3;

		String[] names=new String[numParams];
		double[] mins=new double[numParams];
		double[] maxs=new double[numParams];
		double[] defaults=new double[numParams];
		int[] res=new int[numParams];
		int[] types=new int[numParams];

		mins[PrincipalComponentsOpt.NUM_COMPONENTS]=1;
		mins[PrincipalComponentsOpt.USE_CORRELATION_MATRIX]=0;
		mins[PrincipalComponentsOpt.DO_SCALING]=0;
		
		maxs[PrincipalComponentsOpt.NUM_COMPONENTS]=numInputColumns;
		maxs[PrincipalComponentsOpt.USE_CORRELATION_MATRIX]=1;
		maxs[PrincipalComponentsOpt.DO_SCALING]=1;

		
		names[PrincipalComponentsOpt.NUM_COMPONENTS]=
			"Number of Principle Components to Calculate";
		names[PrincipalComponentsOpt.USE_CORRELATION_MATRIX]= 
			"Correlation or Covariance Matrix";
		names[PrincipalComponentsOpt.DO_SCALING]= 
			"Scale to a Common Range";

		res[PrincipalComponentsOpt.NUM_COMPONENTS]=1;
		res[PrincipalComponentsOpt.USE_CORRELATION_MATRIX]=1;
		res[PrincipalComponentsOpt.DO_SCALING]=1;		
		
		defaults[PrincipalComponentsOpt.NUM_COMPONENTS]=2;
		defaults[PrincipalComponentsOpt.USE_CORRELATION_MATRIX]=1;
		defaults[PrincipalComponentsOpt.DO_SCALING]=1;	
		
		types[PrincipalComponentsOpt.NUM_COMPONENTS]=
			ColumnTypes.INTEGER;
		types[PrincipalComponentsOpt.USE_CORRELATION_MATRIX]=
			ColumnTypes.BOOLEAN;
		types[PrincipalComponentsOpt.DO_SCALING]=
			ColumnTypes.BOOLEAN;

		ParameterSpaceImpl space=new ParameterSpaceImpl();
		space.createFromData(names,mins,maxs,defaults,res,types);
		return space;
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "PCA Parameter Space Generator";
	}
	
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return 
			"An example table with the InputColumns selected."+
			" This will be used to initialize the maximum number of "+
			"principle components that can be calculated.";
			default: return "No such input";
		}
	}
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Data Table";
			default: return "NO SUCH INPUT!";
		}
	}	
	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [3];
		pds[PrincipalComponentsOpt.NUM_COMPONENTS] = 
			new PropertyDescription (
			"numComponents",
			"Number of Principal Components",
			"Specifies the number of most significant principal "+
			"components to calculate. The range will be initialized"+
			" to the interval [1,N], where N is the number of "+
			"input features of the input ExampleTable. A maximum value"+
			" greater than the given maximum (N) will cause an error in"+
			" the PCA algorithm.");
		pds[PrincipalComponentsOpt.USE_CORRELATION_MATRIX] = 
			new PropertyDescription (
			"useCorrelationMatrix",
			"Correlation or Covariance",
			"If 0, a correlation matrix will be used, if 1, a covariance"+
			" matrix. Both will produce viable principle components, "+
			"it is up to the optimization evaluation to determine if one"+
			" is superior.");
		pds[PrincipalComponentsOpt.DO_SCALING] = 
			new PropertyDescription (
			"doScalingHere",
			"Scale to a Common Range",
			"If true (1), the input features will be scaled to the interval"+
			" [0,1]. This is normally a good idea if the data is not already"+
			" scaled to a common range, but there might be instances where"+
			" the difference of scale can be retained, such as when"+
			" two features use the same unit of measurement but have"+
			" different ranges." );
		return pds;
	}
	
}


