package ncsa.d2k.modules.core.discovery.pca;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.datatype.table.*;
/**

<p><b>Overview</b>: Given the outputs of the PCA module, transforms
 the inputs of a data set into M principal components, where M is 
 the number of loadings calculated by the PCA module.

</p><p><b>Detailed Description</b>:
 Principal Component Analysis maps a data set from an N dimensional
 space to an M dimensional space where M is often less than N. This 
module takes the definition of the new space, as well as a data set
 that has been normalized to some common range, and maps the data set
 into the new dimensions. This is a done by a simple weighted sum over
 the input features of the original data set, where the weights are
 the loadings (or weights) produced by the PCA. This therefore produces
 a new ExampleTable, where N the input columns have been replaced by 
the M principal components.

</p><p><b>Data Type Restrictions</b>:
Only numeric input features are supported. If non-numeric input
 features are present, the algorithm will fail. If the inputs
 come directly from the PCA module, the data will necessarily be of
 the correct type.

</p><p><b>Data Handling</b>:
 A copy of the input ExampleTable will be made, with the input columns
 replaced with the new features. The columns from the original table
 that are not replaced are copied by reference.

</p><p><b>Scalability</b>:
 The algorithm scales linearly in terms of both the number of examples 
and number of principle components.
	
	@author pgroves
	**/

public class ApplyWeightedSum extends ComputeModule 
	implements java.io.Serializable{

	//////////////////////
	//d2k Props
	////////////////////
	boolean debug=true;	
	
	String summationColumnPrefix="PCA-";	
	/////////////////////////
	/// other fields
	////////////////////////


	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}
	public void endExecution(){
		super.endExecution();
	}
	public void beginExecution(){
		super.beginExecution();
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(debug){
			 System.out.println(this.getAlias()+": Firing.");
		}
		Table weights=(Table)pullInput(0);
		ExampleTable data=(ExampleTable)pullInput(1);

		int[] transformCols=data.getInputFeatures();

		WeightedSumTransform wst=
			new WeightedSumTransform(transformCols, weights);
			
		wst.setNewFeatureLabelPrefix(summationColumnPrefix);

		wst.transform(data);
		
		/* i don't know if the transformation should be added, as it
		is not reversible, but i'm adding it for now*/
			
		data.addTransformation(wst);
		
		if(wst.transform(data)){
		
			/* i don't know if the transformation should be added, as it
			is not reversible, but i'm adding it for now*/
			
			data.addTransformation(wst);

			pushOutput(data, 0);
		}
		if(debug){
			 System.out.println(this.getAlias()+": Done.");
		}

	}
		
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 
		"<p><b>Overview</b>: Given the outputs of the PCA module, transforms"+
		" the inputs of a data set into M principal components, where M is "+
		" the number of components "+
		"calculated by the PCA module."+ 

		"</p><p><b>Detailed Description</b>:"+
		" Principal Component Analysis maps a data set from an N dimensional"+
		" space to an M dimensional space where M is often less than N. This "+
		"module takes the definition of the new space, as well as a data set"+
		" that has been normalized to some common range, and maps the data set"+
		" into the new dimensions. This is a done by a simple weighted sum over"+
		" the input features of the original data set, where the weights are"+
		" the loadings (or weights) produced by the PCA. The N input columns"+
		" are replaced by the M principal components."+

		"</p><p><b>Data Type Restrictions</b>:"+
		"Only numeric input features are supported. If non-numeric input"+
		" features are present, the algorithm will fail. If the inputs"+
		" come directly from the PCA module, the data will necessarily be of"+
		" the correct type. If numeric column types other than double are"+
		" part of the input feature set, there will be a potential loss of"+
		" precision when the new features are created, as they will be of the"+
		" same type as the original features."+
		
		"</p><p><b>Data Handling</b>:"+
		" The table is modified in place with the input columns"+
		" replaced with the new features. The other columns from the original"+
		" table are not affected. "+

		"</p><p><b>Scalability</b>:"+	
		" The algorithm scales linearly in terms of both the number of examples "+
		"and number of principle components.";
	}
	
   	public String getModuleName() {
		return "Create Weighted Features";
	}
	public String[] getInputTypes(){
		String[] types = {
			 "ncsa.d2k.modules.core.datatype.table.Table",
			 "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return 
				"A Table with the number of columns equal to the number of "+
				"input features of the data table.  Each row hold the coefficients"+
				" for a new feature to construct.";
			case 1: return 
				"The data that will have new input features constructed.";
			default: return "no such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Summation Weights";
			case 1:
				return "Data";
			default: return "no such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			 "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return 
				"The transformed ExampleTable where the input features have "+
				"been replaced by weighted sums of the original input"+
				" features";
			default: return "no such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Data with New Features";
			default: return "no such output";
		}
	}		

    public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[2];
      pds[1] = new PropertyDescription("debug","verbose",
        "Will print firing info to the console");
      pds[0] = new PropertyDescription("summationColumnPrefix",
        "New Feature Label Prefix",
        "The new features (columns) will be named this, followed by"+
		  " an index number.");
      return pds;
    }	
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}
	public String getSummationColumnPrefix(){
		return summationColumnPrefix;
	}
	public void setSummationColumnPrefix(String s){
		summationColumnPrefix=s;
	}
}
			
					

			

								
	
