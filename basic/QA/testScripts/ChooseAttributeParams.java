package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.Table;
//import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 ChooseAttributes.java  (previously ChooseFields)

 Allows the user to choose which columns of a table are inputs and outputs.
 Then assigns them in an ExampleTable.

 @author Peter Groves, c/o David Clutter c/o ANCA
 */
public class ChooseAttributeParams extends DataPrepModule  {

  /**
   Return a description of the function of this module.
   @return A description of this module.
   */
  public String getModuleInfo() {
    String info = "<p>Overview: ";
    info += "This module allows the user to choose which columns of a table are inputs and outputs.";
    info += "</p><p>Detailed Description: ";
    info += "This module outputs an <i>Example Table</i> with the input and output features assigned. ";
    info += "Inputs and outputs do not have to be selected, nor do they have to be mutually exclusive. ";
    info += "</p><p>Data Handling: ";
    info += "This module does not modify the data in the table. It only sets the input and output features.";
    return info;
  }

  /**
   Return the name of this module.
   @return The name of this module.
   */
  public String getModuleName() {
    return "Choose Attributes By Parameters";
  }

  /**
   Return a String array containing the datatypes the inputs to this
   module.
   @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
    return types;
  }

  /**
   Return a String array containing the datatypes of the outputs of this
   module.
   @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }

  /**
   Return a description of a specific input.
   @param i The index of the input
   @return The description of the input
   */
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "The Table to choose inputs and outputs from.";
      default: return "No such input";
    }
  }

  /**
   Return the name of a specific input.
   @param i The index of the input.
   @return The name of the input
   */
  public String getInputName(int i) {
    switch(i) {
      case 0:
        return "Table";
      default: return "No such input";
    }
  }

  /**
   Return the description of a specific output.
   @param i The index of the output.
   @return The description of the output.
   */
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "The Example Table with input and output features assigned.";
      default: return "No such output";
    }
  }

  /**
   Return the name of a specific output.
   @param i The index of the output.
   @return The name of the output
   */
  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "Example Table";
      default: return "No such output";
    }
  }


    public PropertyDescription[] getPropertiesDescriptions() { 
	return new PropertyDescription[0]; // so that "windowName" property 
	// is invisible 
    }


 
    /**
     Called when inputs arrive
     */
    public void doit() {
	Table tbl = (Table)pullInput(0);
	ExampleTable etbl = tbl.toExampleTable();
	int numColumns = tbl.getNumColumns();
	int [] outputs = new int[1];
	outputs[0] = numColumns-1;
	int [] inputs = new int [numColumns-1];
	for (int i=0; i < numColumns-2; i ++)
	    inputs[i] =i;
	etbl.setOutputFeatures(outputs);
	etbl.setInputFeatures(inputs);
	pushOutput(etbl,0);
    }

}
