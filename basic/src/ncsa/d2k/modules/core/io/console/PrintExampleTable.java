package ncsa.d2k.modules.core.io.console;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

public class PrintExampleTable
    extends OutputModule {

  int numProperties = 4;

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

    pds[1] = new PropertyDescription(
        "printFeatureNames",
        "Print Feature Names",
        "Report the name and number of each input and output feature");

    pds[2] = new PropertyDescription(
        "printNumExamples",
        "Print Num Examples",
        "Report the number of examples in the example table");

    pds[3] = new PropertyDescription(
        "printExampleValues",
        "Print Example Values",
        "Report the feature values for each example in the example table");

    return pds;
  }





  private String Label = "Print Example Table Label:  ";
  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  private boolean PrintFeatureNames = true;
  public void setPrintFeatureNames(boolean value) {
    this.PrintFeatureNames = value;
  }

  public boolean getPrintFeatureNames() {
    return this.PrintFeatureNames;
  }

  private boolean PrintNumExamples = true;
  public void setPrintNumExamples(boolean value) {
    this.PrintNumExamples = value;
  }

  public boolean getPrintNumExamples() {
    return this.PrintNumExamples;
  }

  private boolean PrintExampleValues = true;
  public void setPrintExampleValues(boolean value) {
    this.PrintExampleValues = value;
  }

  public boolean getPrintExampleValues() {
    return this.PrintExampleValues;
  }

  /**
* Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleName() {
    return "Print Example Table";
  }

  /**
 * Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleInfo() {
    return "<p>Overview: This module reports information in the given example set.</p>";
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
        return "Example Table";
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
        return "The Example Table to report on";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
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
        return "Example Table";
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
        return "The Example Table that was input to the module without any modification";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return out;
  }

  /**
 * Performs the main work of the module.
 */
public void doit() {

    ExampleTable exampleSet = (ExampleTable)this.pullInput(0);
    int numExamples = exampleSet.getNumRows();
    int numInputs  = exampleSet.getNumInputFeatures();
    int numOutputs = exampleSet.getNumOutputFeatures();

    if (PrintNumExamples)
      System.out.println(Label + " numExamples = " + numExamples);

    if (PrintFeatureNames) {
      System.out.println(Label + " numInputs = " + numInputs);
      for (int v = 0; v < numInputs; v++) {
        System.out.println(Label + "  " + (v + 1) + " : " +
                           exampleSet.getInputName(v));
      }
      System.out.println(Label + " numOutputs = " + numOutputs);
      for (int v = 0; v < numOutputs; v++) {
        System.out.println(Label + "  " + (v + 1) + " : " +
                           exampleSet.getOutputName(v));
      }
    }

    if (PrintExampleValues) {
      for (int e = 0; e < numExamples; e++) {
        System.out.println(Label + "  e" + (e + 1));
        System.out.println(Label + "    input:");
        for (int v = 0; v < numInputs; v++) {
          System.out.println(Label + "    " + exampleSet.getInputName(v) +
                             " = " + exampleSet.getInputDouble(e, v));
        }
        System.out.println(Label + "    output:");
        for (int v = 0; v < numOutputs; v++) {
          System.out.println(Label + "    " + exampleSet.getOutputName(v) +
                             " = " + exampleSet.getOutputDouble(e, v));
        }
      }
    }

    this.pushOutput(exampleSet, 0);
  }
}
