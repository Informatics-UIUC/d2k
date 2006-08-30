package ncsa.d2k.modules.core.discovery.pca;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
	Provides functionality for the PCA module to be used
	with an optimizer.

	@author pgroves 06/05/03

*/

public class PrincipalComponentsOpt extends PrincipalComponents{

	/**to access individual parameters in the ParameterPoint*/
	public static int NUM_COMPONENTS=0;
	
	/**to access individual parameters in the ParameterPoint*/
	public static int USE_CORRELATION_MATRIX=1;
	
	/**to access individual parameters in the ParameterPoint*/
	public static int DO_SCALING=2;

	
	public void doit() throws Exception{
		if(verbose){
			  System.out.println(this.getAlias()+": Firing");
		}
		ExampleTable et=(ExampleTable)pullInput(0);
		ParameterPoint params=(ParameterPoint)pullInput(1);
		

		//set the d2k props (of the superclass) to the input parameters
		if(params.getValue(DO_SCALING)>.5){
			 this.doScalingHere=true;
		}else{
			 this.doScalingHere=false;
		}
		if(params.getValue(USE_CORRELATION_MATRIX)>.5){
			 this.useCorrelationMatrix=true;
		}else{
			 this.useCorrelationMatrix=false;
		}
		this.numComponents=(int)params.getValue(NUM_COMPONENTS);
		
		computeLoadings(et);
		
		pushOutput(loadings, 0);
		pushOutput(et, 1);
		pushOutput(eigTable, 2);

	}	
   public String getModuleName() {
		return "PCA Opt";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "An example table with the InputColumns selected";
			case 1: return 
				"A parameter point that conforms to the space created by"+
				" PCAParamSpaceGenerator for the input data set. The properties"+
				" <i>Scale to a Common Range, Number of Principle Components, and"+
				" Correlation or Covariance</i> will be set to the values contain"+
				"ed in this ParameterPoint.";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Data Table";
			case 1:
				return "Parameters";
			default: return "NO SUCH INPUT!";
		}
	}	 
}
