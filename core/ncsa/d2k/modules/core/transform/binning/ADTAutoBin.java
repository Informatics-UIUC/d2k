package ncsa.d2k.modules.core.transform.binning;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

/**
 * Automatically discretize scalar data for the Naive Bayesian classification
 * model.  This module requires a ParameterPoint to determine the method of binning
 * to be used.
 */
public class ADTAutoBin extends DataPrepModule {

	public String[] getInputTypes() {
		String[] in =
			{
				"ncsa.d2k.modules.core.datatype.ADTree",
				"ncsa.d2k.modules.core.datatype.table.ExampleTable" };
		return in;
	}

	/**
	* Get the name of the input parameter
	* @param i is the index of the input parameter
	* @return Name of the input parameter
	*/
	public String getInputName(int i) {
		switch (i) {
			case 0 :
				return "AD Tree";
			case 1 :
				return "Meta Data Example Table";
			default :
				return "No such input";
		}
	}

	/**
	* Get the data types for the output parameters
	* @return A object of class BinTransform
	*/
	public String[] getOutputTypes() {
		String[] types =
			{ "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform" };
		return types;
	}

	/**
	 * Get input information
	 * @param i is the index of the input parameter
	 * @return A description of the input parameter
	 */
	public String getInputInfo(int i) {
		switch (i) {
			case 0 :
				return "The ADTree containing counts";
			case 1 :
				return "ExampleTable containing the names of the input/output features";
			default :
				return "No such input";
		}
	}

	/**
	 * Get the name of the output parameters
	 * @param i is the index of the output parameter
	 * @return Name of the output parameter
	 */
	public String getOutputName(int i) {
		switch (i) {
			case 0 :
				return "Binning Transformation";
			default :
				return "no such output!";
		}
	}

	/**
	 * Get output information
	 * @param i is the index of the output parameter
	 * @return A description of the output parameter
	 */
	public String getOutputInfo(int i) {
		switch (i) {
			case 0 :
				return "A BinTransform object that contains column_numbers, names and lables";
			default :
				return "No such output";
		}
	}

	public String getModuleName() {
		return " AD Tree Auto Discretization";
	}

	public String getModuleInfo() {
		String s =
			"<p>Overview: Automatically discretize nominal data for the "
				+ "Naive Bayesian classification model using ADTrees. "
				+ "<p>Detailed Description: Given an ADTree and an Example table containing labels and "
				+ "types of the columns, define the bins for each nominal input column, one bin "
				+ "for each unique value in the column."
				+ "<p>Properties: none"
				+ "<p>Data Type Restrictions: This module does not bin numeric data. It does not modify data.";
		return s;
	}


	ADTree adt;
	ExampleTable tbl;

	public void doit() throws Exception {

	    adt = (ADTree) pullInput(0);
	    tbl = (ExampleTable) pullInput(1);
	    
	    int [] inputs = tbl.getInputFeatures(); 
	    if (inputs == null || inputs.length == 0)  
		throw new Exception("Input features are missing. Please select an input feature."); 
	    
	    int [] outputs = tbl.getOutputFeatures(); 
	    if (outputs == null || outputs.length == 0)  
		throw new Exception("Output feature is missing. Please select an output feature."); 
	    if(tbl.isColumnScalar(outputs[0])) 
		throw new Exception("Output feature must be nominal."); 
	    
	    BinDescriptor[] bins = createAutoNominalBins();
	    
	    BinTransform bt = new BinTransform(bins, false);
	    
	    pushOutput(bt, 0);
		//pushOutput(et, 1);
	}

	protected BinDescriptor[] createAutoNominalBins() throws Exception {

		List bins = new ArrayList();
		int[] inputs = tbl.getInputFeatures();

		for (int i = 0; i < inputs.length; i++) {
			boolean isScalar = tbl.isColumnScalar(i);

			//System.out.println("scalar ? " + i + " " + isScalar);
			if (isScalar) {
			    throw new Exception ("ADTrees do not support scalar values");
			}

			// if it is nominal, create a bin for each unique value.
			// attributes indexes in the ADTree start at 1
			else {
				TreeSet vals = adt.getUniqueValuesTreeSet(inputs[i]+1);
				Iterator iter = vals.iterator();
				while (iter.hasNext()) {
					String item = (String) iter.next();
					String[] st = new String[1];
					st[0] = item;
					BinDescriptor bd =
						new TextualBinDescriptor(
							i,
							item,
							st,
							tbl.getColumnLabel(i));
					bins.add(bd);
				}
			}
		}

		BinDescriptor[] bn = new BinDescriptor[bins.size()];
		for (int i = 0; i < bins.size(); i++) {
			bn[i] = (BinDescriptor) bins.get(i);

		}
		return bn;
	}

}

