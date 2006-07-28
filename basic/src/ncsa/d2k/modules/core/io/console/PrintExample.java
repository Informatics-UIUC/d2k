package ncsa.d2k.modules.core.io.console;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

public class PrintExample
    extends OutputModule {

  int numProperties = 1;

  /**
 * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects for each property of the module.
 *
 * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects.
 */
public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numProperties];

    pds[0] = new PropertyDescription(
        "label",
        "Label",
        "The label printed as a prefix to each report line");

    return pds;
  }




  private String Label = "";
  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  /**
* Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleName() {
    return "Print Example";
  }

  /**
 * Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleInfo() {
    return "<p>Overview: This module reports the values of the features of the given example.</p>";
  }

  /**
 * Returns the name of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the input at the specified index.
 */
public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Example";
    }
    return "";
  }

  /**
 * Returns a description of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a description should be returned.
 *
 * @return <code>String</code> describing the input at the specified index.
 */
public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ncsa.d2k.modules.core.datatype.table.Example";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.table.Example"};
    return in;
  }

  /**
 * Returns the name of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the output at the specified index.
 */
public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Example";
    }
    return "";
  }

  /**
 * Returns a description of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a description should be returned.
 *
 * @return <code>String</code> describing the output at the specified index.
 */
public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The Example that was input to the module without any modification";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.table.Example"};
    return out;
  }

  /**
 * Performs the main work of the module.
 */
public void doit() {

    Example example = (Example)this.pullInput(0);
    int numInputs = ((ExampleTable)example.getTable()).getNumInputFeatures();
    int numOutputs = ((ExampleTable)example.getTable()).getNumOutputFeatures();

    System.out.println(Label + "    input:");
    for (int v = 0; v < numInputs; v++) {
      System.out.println(Label + "    " + ((ExampleTable)example.getTable()).getInputName(v) + " = " +
                         example.getInputDouble(v));
    }
    System.out.println(Label + "    output:");
    for (int v = 0; v < numOutputs; v++) {
      System.out.println(Label + "    " + ((ExampleTable)example.getTable()).getOutputName(v) + " = " +
                         example.getOutputDouble(v));
    }

    this.pushOutput(example, 0);
  }
}
