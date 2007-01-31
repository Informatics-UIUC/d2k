package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PreparePredictionTableForSVM
    extends DataPrepModule {
  public PreparePredictionTableForSVM() {
    super();
  }

  /**
   * Subclasses should override this method to perform their desired function.
   *
   * @throws Exception we have no idea what may happen during execution.
   * @todo Implement this ncsa.d2k.core.modules.ExecModule method
   */
  protected void doit() throws Exception {
    ExampleTable et = (ExampleTable) pullInput(0);
    if(et instanceof PredictionTable){
      pushOutput(et, 0);
      return;
    }
    PredictionTable pt = et.toPredictionTable();

      pushOutput(pt, 0);

  }

  /**
   * This method will return a text description of the the input indexed by the
   * value passed in.
   *
   * @param index the index of the input we want the description of.
   * @return a text description of the indexed input.
   * @todo Implement this ncsa.d2k.core.modules.Module method
   */
  public String getInputInfo(int index) {
    switch(index){
      case 0: return "Example Table to turn into a Prediction Table.";
        default: return "No such input";
    }
  }

  public String getInputName(int index) {
   switch(index){
     case 0: return "Example Table";
       default: return "No such input";
   }
 }

  /**
   * This method returns an array of strings containing Java data types of the
   * input.
   *
   * @return the <b>fully qualified</b> names of the java objects taken as
   *   input.
   * @todo Implement this ncsa.d2k.core.modules.Module method
   */
  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }

  /**
   * This method returns a Java String that describes the purpose of the
   * module, what it does and what it needs.
   *
   * @return a text description of the modules function
   * @todo Implement this ncsa.d2k.core.modules.Module method
   */
  public String getModuleInfo() {
    return "This module converts the input example table into a prediction table." +
        " Then for each prediction column it replaces the prediction column with " +
        "a new double column. This is done in order to have double values in the prediction columns, " +
        "while the output features are expected to be integer columns. This is a special case for SVM prediciton models.";
  }

  public String getModuleName() {
    return "Prepare Prediction Table for SVM Model";
  }

  /**
   * This method returns a text description of the one of the outputs of the
   * module.
   *
   * @param outputIndex the index of the output we want the description of.
   * @return a text description of the indexed output.
   * @todo Implement this ncsa.d2k.core.modules.Module method
   */
  public String getOutputInfo(int outputIndex) {
    switch (outputIndex) {
      case 0:
        return "Prediction Table based on the input Example Table.";
      default:
        return "No such output";
    }

  }

  public String getOutputName(int outputIndex) {
    switch (outputIndex) {
      case 0:
        return "Prediction Table";
      default:
        return "No such output";
    }
  }

  /**
   * This method returns an array of strings containing Java data types of the
   * outputs.
   *
   * @return the <b>fully qualified</b> names of the java objects returned as
   *   results.
   * @todo Implement this ncsa.d2k.core.modules.Module method
   */
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
     return types;
   }

}
