package ncsa.d2k.modules.core.prediction.decisiontree.continuous;

import ncsa.d2k.modules.core.transform.sort.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.prediction.regression.continuous.*;
import ncsa.d2k.modules.core.prediction.mean.continuous.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.beans.PropertyVetoException;


public class DecisionTreeInducer extends DecisionTreeInducerOpt {

    /*
    public void    setUseSimpleBooleanSplit (boolean value) {this.UseSimpleBooleanSplit = value;}
    public boolean getUseSimpleBooleanSplit ()              {return this.UseSimpleBooleanSplit;}


    public void    setPrintEvolvingModels (boolean value) { this.PrintEvolvingModels  = value;}
    public boolean getPrintEvolvingModels ()              {return this.PrintEvolvingModels;}
    */

  private int MinDecompositionPopulation = 20;
  public void setMinDecompositionPopulation(int value) throws PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.MinDecompositionPopulation = value;
  }

  public int getMinDecompositionPopulation() {
    return this.MinDecompositionPopulation;
  }

  private double MinErrorReduction = 0.0;
  public void setMinErrorReduction(double value) {
    this.MinErrorReduction = value;
  }

  public double getMinErrorReduction() {
    return this.MinErrorReduction;
  }

  public boolean UseOneHalfSplit = false;
  public void setUseOneHalfSplit(boolean value) {
    this.UseOneHalfSplit = value;
  }

  public boolean getUseOneHalfSplit() {
    return this.UseOneHalfSplit;
  }

  public boolean UseMidPointBasedSplit = false;
  public void setUseMidPointBasedSplit(boolean value) {
    this.UseMidPointBasedSplit = value;
  }

  public boolean getUseMidPointBasedSplit() {
    return this.UseMidPointBasedSplit;
  }

  public boolean UseMeanBasedSplit = true;
  public void setUseMeanBasedSplit(boolean value) {
    this.UseMeanBasedSplit = value;
  }

  public boolean getUseMeanBasedSplit() {
    return this.UseMeanBasedSplit;
  }

  public boolean UsePopulationBasedSplit = false;
  public void setUsePopulationBasedSplit(boolean value) {
    this.UsePopulationBasedSplit = value;
  }

  public boolean getUsePopulationBasedSplit() {
    return this.UsePopulationBasedSplit;
  }

  public boolean SaveNodeExamples = false;
  public void setSaveNodeExamples(boolean value) {
    this.SaveNodeExamples = value;
  }

  public boolean getSaveNodeExamples() {
    return this.SaveNodeExamples;
  }

  public boolean UseMeanNodeModels = true;
  public void setUseMeanNodeModels(boolean value) {
    this.UseMeanNodeModels = value;
    this.UseLinearNodeModels = !value;
  }

  public boolean getUseMeanNodeModels() {
    return this.UseMeanNodeModels;
  }

  public boolean UseLinearNodeModels = false;
  public void setUseLinearNodeModels(boolean value) {
    this.UseLinearNodeModels = value;
    this.UseMeanNodeModels = !value;
  }

  public boolean getUseLinearNodeModels() {
    return this.UseLinearNodeModels;
  }

  int numBiasDimensions = 9;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];

    pds[0] = new PropertyDescription(
        "minDecompositionPopulation",
        "Minimum examples per leaf",
        "Prevents the generation of splits that result in leaf nodes with " +
        "less than the specified number of examples.  ");

    pds[1] = new PropertyDescription(
        "minErrorReduction",
        "Minimum split error reduction",
        "The units of this error reduction are relative to the error function passed to the " +
        "decision tree inducer.  " +
        "A split will not occur unless the error is reduced by at least the amount specified.");

    pds[2] = new PropertyDescription(
        "useOneHalfSplit",
        "Generate splits only at 1/2",
        "This works fine for boolean and continuous values.  " +
        "If used as the sole decomposition strategy, it forces the system to only split on a variable once.  ");

    pds[3] = new PropertyDescription(
        "useMeanBasedSplit",
        "Generate mean splits",
        "The mean of each attribute value is calculated in the given node and used to generate " +
        "splits for that node.  One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

    pds[4] = new PropertyDescription(
        "useMidPointBasedSplit",
        "Generate midpoint splits",
        "The min and max values of each attribute at each node in the tree are used to generate splits for that node.  " +
        "The split occurs at the midpoint between the min and max values.  " +
        "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

    pds[5] = new PropertyDescription(
        "usePopulationBasedSplit",
        "Generate median splits",
        "The median of each attribute value is calculated in the given node and used to generate " +
        "splits for that node.  This requires sorting of all the examples in a node and therefore " +
        "scales at n log n in time complexity.  " +
        "One or more split methods (mean, midpoint, and median) can be use simultaneously.  ");

    pds[6] = new PropertyDescription(
        "saveNodeExamples",
        "Save examples at each node",
        "In order to compute and print statistics of the node you must save the examples at the node " +
        "which increases the space and time complexity of the algorithm by a linear factor.  ");

    pds[7] = new PropertyDescription(
        "useMeanNodeModels",
        "Use the mean averaging for models",
        "This results in a simple decision tree with constant functions at the leaves.  " +
        "UseMeanNodeModels and UseLinearNodeModels are mutually exclusive.  ");

    pds[8] = new PropertyDescription(
        "useLinearNodeModels",
        "Use multiple regression for models",
        "This results in a decision tree with linear functions of the input attributes at the leaves.  " +
        "UseLinearNodeModels and UseMeanNodeModels are mutually exclusive.  ");

    return pds;
  }


  public String getModuleName() {
    return "Decision Tree Inducer";
  }

  public String getModuleInfo() {

    String s = "";
    s += "<p>";
    s += "Overview: ";
    s += "This module builds a decision tree from an example table. </p>";
    s += "<p>Detailed Description: ";
    s += "The module implements a decision tree learning algorithm for continuous features. ";

    s += "For inducing functions within a node, one of two different learning algorithms can be used. ";
    s += "<ul>";
    s += "<li><i>Use the mean averaging for models</i>:  Simple averaging of output values.  </li>";
    s += "<li><i>Use multiple regression for models</i>:  Using the best 1-d linear function using a single input features.  </li>";
    s += "</ul>";

    s += "It allows for multiple decomposition strategies to be used simultaneously. ";

    s += "<ul>";
    s += "<li><i>Generate splits only at 1/2</i>:  Generate splits only at 0.5.  Works well when inputs are scaled from 0.0 to 1.0.</li>";
    s += "<li><i>Generate mean splits</i>:  Generate splits at the mean input feature value within the node.  </li>";
    s += "<li><i>Generate midpoint splits</i>:  Generate splits at the midpoint between the min an max input feature value within the node.  </li>";
    s += "<li><i>Generate median splits</i>:  Generate splits at the median input feature value within the node which requires n log n sorting.  </li> ";
    s += "</ul>";

    s += "Determining whether to split a node is controled by a couple parameters ";
    s += "<i>Minimum examples per leaf</i> and <i>Minimum split error reduction</i>.  ";
    s += "A split is not considered if it results in a nodes with less than <i>Minimum examples per leaf</i> examples in it.  ";
    s += "A split is not considered if the resubstitution based error weighted by population ";
    s += "does improve by at least <i>Minimum split error reduction</i>.  ";

    s += "<p>";
    s += "Restrictions: ";
    s += "This module will only classify examples with ";
    s += "continuous outputs.  </p>";

    s += "<p>";
    s += "<p>Data Handling: This module does not modify the input data. </p>";

    s += "<p>";
    s += "<p>Scalability: This module can efficiently a data set that can be stored in memory.  ";
    s += "The ultimate limit is how much virtual memory java can access. </p> ";

    return s;
  }

  public String getInputName(int i) {
    switch(i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "Error!  No such input.";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "Error!  No such input.";
    }
  }
  public String[] getInputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
      "ncsa.d2k.modules.core.prediction.ErrorFunction"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case  0: return "Model";
      default: return "Error!  No such output.";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Model";
      default: return "Error!  No such output.";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.model.Model"};
    return types;
  }





  public void beginExecution() {
  }


  public void instantiateBiasFromProperties() {
    // Nothing to do in this case since properties are reference directly by the algorithm and no other control
    // parameters need be set.  This may not be the case in general so this stub is left open for future development.
  }


  public void doit() throws Exception {

    ExampleTable  exampleSet      = (ExampleTable)  this.pullInput(0);
    ErrorFunction errorFunction   = (ErrorFunction) this.pullInput(1);

    instantiateBiasFromProperties();

    Model model = null;

    model = generateModel(exampleSet, errorFunction);

    this.pushOutput(model, 0);
  }


}
