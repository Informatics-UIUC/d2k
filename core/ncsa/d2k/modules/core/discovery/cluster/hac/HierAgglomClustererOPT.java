package ncsa.d2k.modules.core.discovery.cluster.hac;

//==============
// Java Imports
//==============
//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 *
 * <p>Title: </p>
 * <p>Description: HierAgglomClustererOPT</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 *
 * TODO: change distance method to accomodate sparse matrices
 */

public class HierAgglomClustererOPT
    extends OrderedReentrantModule {

  //==============
  // Data Members
  //==============

  //============
  // Properties
  //============

  protected boolean _verbose = false;
  public boolean getVerbose() {
    return _verbose;
  }

  public void setVerbose(boolean b) {
    _verbose = b;
  }

  protected boolean _mvCheck = true;
  public boolean getCheckMissingValues() {
    return _mvCheck;
  }

  public void setCheckMissingValues(boolean b) {
    _mvCheck = b;
  }

  //================
  // Constructor(s)
  //================
  public HierAgglomClustererOPT() {
  }

  //================
  // Public Methods
  //================

  //========================
  // D2K Abstract Overrides

  /**
   * Return array of property descriptors for this module.
   * @return array
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[2];

    descriptions[0] = new PropertyDescription("checkMissingValues",
        "Check Missing Values",
        "Perform a check for missing values on the table inputs (or not).");

    descriptions[1] = new PropertyDescription("verbose",
        "Verbose Ouput",
        "Do you want verbose output to the console.");

    return descriptions;
  }


  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Hier. Agglom. Clusterer";
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Control parameters";
    } else if (parm1 == 1) {
      return "Table of examples to be clustered";
    } else {
      return "";
    }
  }

  /**
     Return the name of a specific input.
     @param i The index of the input.
     @return The name of the input
   */
  public String getInputName(int parm1) {
    if (parm1 == 0) {
      return "ParameterPoint";
    } else if (parm1 == 1) {
      return "Table";
    } else {
      return "";
    }
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "Perform a bottom-up, hierarchical clustering of the examples in the input ";
    s += "table.";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "A ClusterModel is created for the clustering performed by this module.  This model ";
    s += "contains the unchanged table of original example (the one input), a tree of TableCluster ";
    s += "objects, and an ArrayList of the clusters formed.";
    s += "</p>";
    s += "<p>If <i>auto clustering</i> is activated then a <i>threshold</i> value is used to determine ";
    s += "a distance cutoff value.  The <i>threshold</i> represents a certain percentage of the approximate ";
    s += "max distance of all example pairs.  When the next two most similar clusters have distance > this value ";
    s += "the clsutering is stopped (in actuality the clustering continues all the way to the root of the ";
    s += "cluster tree so that the complete tree can be placed in the model).  The clusters at the cutoff (also ";
    s += "known as the \"cut\") are saved to the model separately.  If <i>auto clustering</i> is NOT activated ";
    s += "then clustering is halted when the number of clusters is equal to the <i>number of clusters</i> ";
    s += "property.";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The table is unchanged by this module, however, it is included in the ";
    s += "output ClusterModel.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "The algoruthm is quadratic in time complexity and therefore will run impossibly ";
    s += "long for large numbers of examples.  It is recommended to use one of the sampling ";
    s += "cluster methods (i.e. KMeans) for very large datasets.  An array of N squared doubles ";
    s += "is created twice requiring sufficient heap resources for the size of N (number of examples).";
    s += "</p>";
    return s;
  }

  /**
     Return the description of a specific output.
     @param i The index of the output.
     @return The description of the output.
   */
  public String getOutputInfo(int parm1) {
    if (parm1 == 0) {
      return "Cluster Model";
    } else {
      return "";
    }
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputName(int parm1) {
    if (parm1 == 0) {
      return "Cluster Model";
    } else {
      return "";
    }
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.discovery.cluster.ClusterModel"};
    return out;
  }

  /**
     Code to execute before doit.
   */
  public void beginExecution() {
    System.out.println("Beginning execution: " + this);
  }

  /**
     Code to execute at end of itinerary run.
   */
  public void endExecution() {
    super.endExecution();
  }

  /**
   Perform the work of the module.
   @throws Exception
   */
  protected void doit() throws Exception {
    ParameterPoint pp = (ParameterPoint) pullInput(0);
    HAC hac = new HAC( (int) pp.getValue(0), (int) pp.getValue(1),
                      (int) pp.getValue(2), (int) pp.getValue(3), getVerbose(),
                      this.getCheckMissingValues());
    this.pushOutput(hac.buildModel( (Table)this.pullInput(1)), 0);
  }

}
