package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
/**
 *
 * <p>Title: </p>
 * <p>Description: this module will prepare the output feature of an ExampleTable
 * for the use of an Isvm Module and the likes. The output Table of this module will have
 * only 1 or -1 in the output feature. it replaces the zeros with ones.

 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author vered goren
 * @version 1.0
 */
public class PrepareClassAttributeModule extends ncsa.d2k.core.modules.DataPrepModule {

  protected void doit(){
    ExampleTable et = (ExampleTable) pullInput(0);
    int[] outIdx = et.getOutputFeatures();
    for (int i=0; i<et.getNumRows(); i++){
      for (int j=0; j<outIdx.length; j++){
        if (et.getInt(i, outIdx[j]) == 0)
          et.setInt( -1, i, outIdx[j]);
      }
    }

    pushOutput(et, 0);
  }

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<p>"+
""+
"      An ExampleTable with scalar binary output feature (that contain only zero and one)."+
""+
"    </p>";
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Input Table";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<p>"+
""+
"      Same as the input table with transformed output features  - all "+
""+
"      zeros are replaced with -1."+
""+
"    </p>";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Output Table";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "<p>"+
"      This module prepares the output features of the <i>Input Table</i> "+
"      for the use of SVM modules and the likes."+
"    </p>"+
"    <p>"+

"      The output features should be scalar and binary (1 and 0)"+
"    </p>"+
"    <p>"+
"      the 0 values are then replaces with -1."+
"    </p>"+
"    <p>"+
"      This module changes the content of the <i>Input Table</i>."+
"    </p>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Prepare Output Attribute For SVM";
	}
}
