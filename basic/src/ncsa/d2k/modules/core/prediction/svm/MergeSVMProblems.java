package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import libsvm.*;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author vered goren
 * @version 1.0
 *
 * this module supports the incremental training of support vectors machine.
 * its inputs are the svm model from previous iteration and a new data set in the
 * format of an svm problem.
 * it then merges the support vectors of the svm model with the svm problem
 * into a new svm problem, which is the output of this module.
 * In the first iteration we expect this module to receive only the new data
 * set and it is output as is.
 */
public class MergeSVMProblems extends ncsa.d2k.core.modules.DataPrepModule {

private boolean first = true;

  public boolean isReady(){
    return ((getInputPipeSize(0) > 0) &&( first || getInputPipeSize(1) > 0));
  }

  protected void doit(){
    if(first){
      first = false;
      svm_problem prob = (svm_problem) pullInput(0);
      pushOutput(prob, 0);
    }
    else{
      svm_problem prob = (svm_problem) pullInput(0);
      svm_model model = (svm_model) pullInput(1);
  //    svm_node[][] SV = (svm_node[][]) pullInput(2);
 //     Integer numAtts = (Integer) pullInput(2);
      svm_problem merged = merge(prob, model);
      pushOutput(merged, 0);
    }

  }//doit

  /**
   * copies the array of support vectors, so that the model from iteration i
   * won't be changed in iteration i+1.
   * @param original - Support Vectors of input Model.
   * @return - exact copy of the support vectors.
   */
  private svm_node[][] copy(svm_node[][] original){
    svm_node[][] copyOf = new svm_node[original.length][];
    for (int i=0; i<original.length; i++){
      copyOf[i] = new svm_node[original[i].length];
      for(int j=0; j<original[i].length; j++){
        copyOf[i][j] = new svm_node();
        copyOf[i][j].index = original[i][j].index;
        copyOf[i][j].value = original[i][j].value;
      }
    }
    return copyOf;
  }

  /**
   * merges <code>newData</codE> with the support vectors of <codE>oldModel</code>.
   * @param newData new training data set.
   * @param oldModel support vectors machine model from previous iteration
   * @return svm_problem consisting of <code>newData</code> and the vectors of
   * <codE>oldModel</code>
   */
  private svm_problem merge(svm_problem newData, svm_model oldModel){
    svm_problem merged = new svm_problem();

    //allocate the arrays.

    svm_node[][] sv = copy(svm.svm_get_support_vectors(oldModel)); //the support vectors.

    merged.l = newData.l + sv.length;
    merged.x = new svm_node[merged.l][];
    merged.y = new double[merged.l];

    //copy data from newData
    for (int i=0; i<newData.l; i++){
      merged.x[i] = newData.x[i];
      merged.y[i] = newData.y[i];
    }

//getting the labels for the vectors.
    int[] labels = new int[sv.length];
    svm.svm_get_labels(oldModel, labels);
    //copy nodes from oldModel's vectors and its labels
    for (int j=0, i=newData.l; i<merged.l && j < sv.length; i++, j++){
      merged.x[i] = sv[j];
      merged.y[i] =  labels[j];
    }
    return merged;

  }

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<p>"+
""+
"      New data set, to be merged with the Support Vectors of <i>SVM Model</i>"+
""+
"    </p>";
			case 1: return "<p>"+
""+
"      The Support Vectors of this model are to be merged with <i>Input SVM "+
""+
"      Problem</i>"+
""+
"    </p>";
 //                   case 2: return "<P>The Support Vectors of <i>SVM Model</i>";

                 /*   case 2: return "<P>Number of attributes in the data.</P>";*/
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
				return "Input SVM Problem";
			case 1:
				return "SVM Model";
   //                     case 2: return "Support Vectors";
             /*           case 2: return "Number of Attributes";*/
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {"libsvm.svm_problem","libsvm.svm_model"};
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
"      Merged training data."+
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
				return "Merged SVM Problem";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"libsvm.svm_problem"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "<p>"+
"      Merges <i>Input SVM Problem</i> with the Support of Vectors of <i>SVM "+
"      Model</i> into one <i>Merged SVM Problem</i>."+
"    </p>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Merge SVM Problems";
	}
}
