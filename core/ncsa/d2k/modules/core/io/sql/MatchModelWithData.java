package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: </p>
 * <p>Description: Verify the selected model matches with the selected data
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.*;
import javax.swing.*;

public class MatchModelWithData extends ComputeModule {
  JOptionPane msgBoard = new JOptionPane();
  public MatchModelWithData() {
  }

  public String getOutputInfo (int i) {
    switch(i) {
      case 0: return "An example table";
      case 1: return "An prediction model";
      default: return "No such output";
    }
  }

  public String getInputInfo (int i) {
    switch(i) {
      case 0: return "An example table";
      case 1: return "An prediction model";
      default: return "No such input.";
    }
  }

  public String getModuleInfo () {
    String s = "<p> Overview: ";
    s += "This module determines the compatibility between a model and a data set. </p>";
    s += "<p> Detailed Description: ";
    s += "This module compares the selected model and the selected data set for ";
    s += "compatibility. There are three properties are compared: the output features, ";
    s += "the number and names of input feature. If any of these three properties ";
    s += "is different, the selected model cannot be applied to the selected ";
    s += "data set. ";
    s += "<p> Restrictions: ";
    s += "We currently only support Oracle databases and only support decision ";
    s += "tree models and Naive Bayes models. ";
    return s;
  }

  public String[] getInputTypes () {
    String [] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable","ncsa.d2k.modules.PredictionModelModule"};
    return in;
  }

  public String[] getOutputTypes () {
    String [] out = {"ncsa.d2k.modules.core.datatype.table.ExampleTable","ncsa.d2k.modules.PredictionModelModule"};
    return out;
  }

  /**
   * Return the human readable name of the module.
   * @return the human readable name of the module.
   */
  public String getModuleName() {
    return "MatchModelWithData";
  }

  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
  public String getInputName(int index) {
    switch(index) {
      case 0:
        return "Example Table";
      case 1:
        return "Prediction Model";
      default: return "NO SUCH INPUT!";
    }
  }

  /**
   * Return the human readable name of the indexed output.
   * @param index the index of the output.
   * @return the human readable name of the indexed output.
   */
   public String getOutputName(int index) {
    switch(index) {
      case 0:
        return "Example Table";
      case 1:
        return "Prediction Model";
      default: return "NO SUCH OUTPUT!";
    }
  }

  protected String[] getFieldNameMapping () {
    return null;
  }

  /** compare model's and data's input features and out feature. If anything
   *  does not match, quit the execution.
   */
  public void doit() {
    ExampleTable et = (ExampleTable)pullInput(0);
    PredictionModelModule mdl = (PredictionModelModule)pullInput(1);
    System.out.println("rows in the testing data set is " + et.getNumRows());
    // check the first row in the example table. If the first row contains
    // the strings of data type, the user has not set the property "typeRow"
    // correctly in data loading.
    for (int colIdx = 0; colIdx < et.getNumColumns(); colIdx++) {
      if (et.getString(0,colIdx).equals("double") ||
          et.getString(0,colIdx).equals("string")) {
        JOptionPane.showMessageDialog(msgBoard,
          "The data table has problems. You did not set the property 'typeRow' correctly when loading data.",
          "Information", JOptionPane.INFORMATION_MESSAGE);
        return;
      }
    }
    int etOutColumn = (et.getOutputFeatures())[0];
    String etOutLabel = et.getColumnLabel(etOutColumn).toUpperCase();
    // database does not allow column name containing "-", therefore in the
    // database table, if the column name contains "-", it has been converted
    // to "_". We have to make the convertion here before compare the column names.
    etOutLabel = etOutLabel.replace('-','_');
    // database does not allow column name containing spaces, therefore in the
    // database table, if the column name contains spaces, the spaces must have been
    // squeezed out. We have to make the convertion here before compare the column names.
    etOutLabel = squeezeSpace(etOutLabel);
    String mdlOutLabel = (mdl.getOutputColumnLabels())[0].toUpperCase();
    mdlOutLabel = mdlOutLabel.replace('-','_');
    mdlOutLabel = squeezeSpace(mdlOutLabel);
    // Model does not match to data if output labels are different.
    if (!etOutLabel.equals(mdlOutLabel)) {
      JOptionPane.showMessageDialog(msgBoard,
        "Output features do not match. You cannot use this model.",
        "Information", JOptionPane.INFORMATION_MESSAGE);
      return;
    }
    // Model does not match to data if the number of input features are different.
    else if (et.getNumInputFeatures() != mdl.getInputColumnLabels().length) {
      JOptionPane.showMessageDialog(msgBoard,
        "Input number do not match. You cannot use this model.",
        "Information", JOptionPane.INFORMATION_MESSAGE);
      return;
    }
    // Model does not match to data if the column names for input features are different.
    else {
      for (int col=0; col<et.getInputFeatures().length; col++) {
        int index = (et.getInputFeatures())[col];
        String etInLabel = et.getColumnLabel(index).toUpperCase();
        etInLabel = etInLabel.replace('-','_');
        etInLabel = squeezeSpace(etInLabel);
        String mdlInLabel = (mdl.getInputColumnLabels())[col].toUpperCase();
        mdlInLabel = mdlInLabel.replace('-','_');
        mdlInLabel = squeezeSpace(mdlInLabel);
        if (!etInLabel.equals(mdlInLabel)) {
          System.out.println("et is " + etInLabel);
          System.out.println("mdl is " + mdlInLabel);
          JOptionPane.showMessageDialog(msgBoard,
          "Input features do not match. You cannot use this model.",
          "Information", JOptionPane.INFORMATION_MESSAGE);
          return;
        }
      }
    }
    pushOutput(et, 0);
    pushOutput(mdl,1);
  }

    /**
   * Squeeze out spaces from the string value
   * @param value The string to edit
   * @return The string after spaces are squeezed out
   */
  public String squeezeSpace(String value)
  {
    int j;
    String newStr = "";
    for (j=0; j<value.length();j++)
    {
      if (value.charAt(j)!=' ')
      newStr = newStr + value.charAt(j);
    }
    return(newStr);
  }

}