package ncsa.d2k.modules.core.prediction.naivebayes;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.transform.binning.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * CreateNBModel simply creates a NaiveBayesModel.
 * @author David Clutter
 */
public class CreateNBModel
    extends ModelProducerModule {

  /**
      Return a description of the function of this module.
      @return A description of this module.
   */
  public String getModuleInfo() {
    return "<p>Overview: Generates a NaiveBayesModel from the given BinTree.  " +
        "The Naive Bayes Model performs all necessary calculations."+
        "<p>Detailed Description: Given a BinTree object that contains counts for "+
        "each discrete item in the training data set, this module creates a "+
        "Naive Bayesian learning model.  This method is based on Bayes's rule "+
        "for conditional probablility.  It \"naively\" assumes independence of "+
        "the input features."+
        "<p>Data Type Restrictions: This model can only use nominal data as the inputs "+
        "and can only classify one nominal output.  The binning procedure will "+
        "discretize any scalar inputs in the training data, but the output data "+
        "is not binned and should be nominal."+
        "<p>Data Handling: The input data is neither modified nor destroyed."+
        "<p>Scalability: The module utilizes the counts in the BinTree, and "+
        "as such does not perform any significant computations.";
  }

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Create Naive Bayes Model";
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.transform.binning.BinTree",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
    return types;
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The BinTree which contains counts.";
      case 1:
        return "The ExampleTable with the data in it.";
      default:
        return "No such input";
    }
  }

  /**
     Return the name of a specific input.
     @param i The index of the input.
     @return The name of the input
   */
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "BinTree";
      case 1:
        return "Example Table";
      default:
        return "NO SUCH INPUT!";
    }
  }

  /**
     Return the description of a specific output.
     @param i The index of the output.
     @return The description of the output.
   */
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "A Naive Bayes Model module.";
      default:
        return "No such output";
    }
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Naive Bayes Model";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  /**
   Create the model and push it out.
   */
  public void doit() throws Exception {
    BinTree bins = (BinTree) pullInput(0);
    ExampleTable et = (ExampleTable) pullInput(1);
    int [] outputs = et.getOutputFeatures();
    if (outputs == null || outputs.length == 0) 
	throw new Exception("Output feature is missing. Please select an output feature.");
    if(et.isColumnScalar(outputs[0]))
	throw new Exception("Output feature must be nominal.");
    ModelModule mdl = new NaiveBayesModel(bins, et);
    pushOutput(mdl, 0);
  }
}