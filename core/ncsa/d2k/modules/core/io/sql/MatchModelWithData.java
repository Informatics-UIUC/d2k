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
    String text = "Verify the selected model is suitable for the testing data.";
    return text;
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
        return "ExampleTable";
      case 1:
        return "PredictionModel";
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
        return "ExampleTable";
      case 1:
        return "PredictionModel";
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
    int etOutColumn = (et.getOutputFeatures())[0];
    String etOutLabel = et.getColumnLabel(etOutColumn);
    String mdlOutLabel = (mdl.getOutputColumnLabels())[0];
    if (!etOutLabel.equals(mdlOutLabel)) {
      JOptionPane.showMessageDialog(msgBoard,
        "Output features do not match. You cannot use this model.",
        "Information", JOptionPane.INFORMATION_MESSAGE);
      return;
    }
    else if (et.getNumInputFeatures() != mdl.getInputColumnLabels().length) {
      JOptionPane.showMessageDialog(msgBoard,
        "Input features do not match. You cannot use this model.",
        "Information", JOptionPane.INFORMATION_MESSAGE);
      return;
    }
    else {
      for (int col=0; col<et.getInputFeatures().length; col++) {
        int index = (et.getInputFeatures())[col];
        if (!et.getColumnLabel(index).equals((mdl.getInputColumnLabels())[col])) {
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

}