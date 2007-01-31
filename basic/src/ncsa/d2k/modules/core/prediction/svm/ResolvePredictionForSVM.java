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
public class ResolvePredictionForSVM
    extends DataPrepModule {
  public ResolvePredictionForSVM() {
    super();
  }

  private String removeThis = "class=";
  public String getRemoveThis(){return removeThis;}
  public void setRemoveThis(String str){removeThis = str;}

  public final static String FINAL_CLASS = "Final Class";

    public final static String PREDICTIONS = " Predictions";
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[]  pds = new PropertyDescription[1];
    pds[0] =new PropertyDescription("removeThis", "Output Column Label Prefix",
       "This string is a prefix shared by all output columns. "
       + "This prefix is to be removed from the column label when setting " +
       "it as the target class in the final prediction column" );
    return pds;
  }
  /**
   * Subclasses should override this method to perform their desired function.
   *
   * @throws Exception we have no idea what may happen during execution.
   * @todo Implement this ncsa.d2k.core.modules.ExecModule method
   */
  protected void doit() throws Exception {
    PredictionTable pt = (PredictionTable) pullInput(0);
    int[] idx = pt.getPredictionSet();
    if(idx.length == 1){
      pushOutput(pt, 0);
      return;
    }
    TableFactory factory = pt.getTableFactory();


    String[] labels = new String[idx.length];
    int[] outputs = pt.getOutputFeatures();
    String finalLabel = this.removeThis + PREDICTIONS;

    for(int i=0; i<outputs.length; i++){
      String temp = pt.getColumnLabel(outputs[i]);
      if(temp.startsWith(this.removeThis)){

        temp = temp.substring(this.removeThis.length() + 1);
      }
      labels[i] = temp;
    }

    Column finalPred = factory.createColumn(ColumnTypes.STRING);

    finalPred.setLabel(finalLabel);
    int[] testSet = pt.getTestingSet();
    int total = pt.getTrainingSet().length + pt.getTestingSet().length;
    finalPred.addRows(total);
    finalPred.setMissingValues(new boolean[total]);


    for(int i=0; i<pt.getNumRows(); i++){
      int theIndex = -1;
      double bigger = Double.NEGATIVE_INFINITY;
      for(int j=0; j<idx.length; j++){
        double val = pt.getDoublePrediction(i, j);
        if(val > bigger){
          theIndex = j;
          bigger = val;
        }
      }//for j
      if (theIndex != -1) {
        finalPred.setString(labels[theIndex], testSet[i]);
      }
    }//for i
    pt.addColumn(finalPred);
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
      case 0: return "Prediction Table to select the predicted value for each recotd. " ;

        default: return "No such input";
    }
  }

  public String getInputName(int index) {
   switch(index){
     case 0: return "Prediciton Table";
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
    String[] types = {"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
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
    return "<P><B>General Overview:</b><br>" +
        "This module selects the predicted class for each record according to the assigned probabilities.</p>" +
"<P><B>Detailed Description:</b><br>" +
        "For each record of the input Table, this module selects the column's label" +
        " from a mongst the prediction columns, of the one that has the highest value." +
        " The module expects the prediction columns to be double columns. The selected value " +
        "is added as a String in an additional String Column (that is neither input nor outpu nor prediction column).</p>";
  }

  public String getModuleName() {
    return "Resolve Prediction For SVM";
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
        return "The input Prediction Table with an additional column withthe final prediction";
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
