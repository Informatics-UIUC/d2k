package ncsa.d2k.modules.core.discovery.cluster.util;

//==============
// Java Imports
//==============
import java.util.*;

//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.*;
import ncsa.d2k.modules.core.discovery.cluster.hac.*;


public class AddClusterAssignmentsToTable
    extends DataPrepModule {

  //============
  // Properties
  //============
  private boolean _tableOnly = true;
  public boolean getTableOnly() {return _tableOnly;}
  public void setTableOnly(boolean b){_tableOnly = b;}

  private boolean _verbose = false;
  public boolean getVerbose() {return _verbose;}
  public void setVerbose(boolean b){_verbose = b;}

  //=======================
  // D2K Abstract Override

  /**
   * Return array of property descriptors for this module.
   * @return array
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[2];

    descriptions[0] = new PropertyDescription("tableOnly",
        "Output Table Only",
        "Choose whther to output the ClusterModel (which contains the table) or just a table. " +
        "Default is \"True\".");

    descriptions[1] = new PropertyDescription("verbose",
        "Verbose Ouput",
        "Do you want verbose output to the console.");

    return descriptions;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
  */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module takes as input a ClusterModel and adds a column to the ";
    s += "end of the table that contains a cluster label (an int value) for each ";
    s += "row.";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "ClusterModels usually contain the tables that were the original input ";
    s += "to the clustering algorithm.  An additional column is added to this ";
    s += "table to with the cluster assignments for each row.  The new column type is ";
    s += "int.  The user can specify via the <i>tabelOnly</i> property whether the ";
    s += "ClusterModel is returned (containing the modified table) or whether just ";
    s += "the table is returned.  ClusterModels implement the Table interface and delegate ";
    s += "all calls to that interface to the contained table.";
    s += "</p>";

    s += "<p>Data Type Restrictions: ";
    s += "The input ClusterModel must contain a table and this table must be mutable.";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "This module will modify (as described above) the table that is contained in ";
    s += "the ClusterModel that was input.  If the table is an example table then the ";
    s += "new column is added to the output features.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "The module does not create a new table but adds a column to the existing table. ";
    s += "The cost of this operation will vary depending on the table implementation type. ";
    s += "For in memory table implementations it will use a memory cost the size of one int ";
    s += "column. ";
    s += "</p>";
    return s;
  }

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Add Cluster Column";
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.discovery.cluster.ClusterModel"};
    return types;
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.table.Table"};
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
        return "This is the ClusterModel that will have it's table modified.";
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
        return "ClusterModel";
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
        return "This is the modified table.";
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
        return "Table";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  /**
     Perform the work of the module.
   */
  public void doit() throws Exception {
    try {

      ClusterModel cm = (ClusterModel)this.pullInput(0);

      if (!cm.hasTable()){
        throw new Exception("The input model does not contain a table.");
      }

      if (!(cm.getTable() instanceof MutableTable)){
        throw new Exception("The input model does not contain a mutable table.");
      }

      MutableTable itable = (MutableTable)cm.getTable();

      //remove pre-existing cluster column
      if (itable.getColumnLabel(itable.getNumColumns() - 1).equals(HAC._CLUSTER_COLUMN_LABEL)) {
        itable.removeColumn(itable.getNumColumns() - 1);
      }
      itable.addColumn(new int[itable.getNumRows()]);
      itable.setColumnLabel(HAC._CLUSTER_COLUMN_LABEL, itable.getNumColumns() - 1);
      if (itable instanceof ExampleTable){
        int[] outs = ((ExampleTable)itable).getOutputFeatures();
        int[] newouts = new int[outs.length + 1];
        System.arraycopy(outs,0,newouts,0,outs.length);
        newouts[newouts.length-1] = newouts.length-1;
        ((ExampleTable)itable).setOutputFeatures(newouts);
      }
      ArrayList resultClusters = cm.getClusters();
      for (int i = 0, n = resultClusters.size(); i < n; i++) {
        TableCluster tc = (TableCluster) resultClusters.get(i);
        int[] rows = tc.getMemberIndices();
        int col = itable.getNumColumns() - 1;
        for (int j = 0, m = rows.length; j < m; j++) {
          itable.setInt(i, rows[j], col);
        }
        if (getVerbose()){
          System.out.println("AddClusterAssignmentsToTable: Cluster " + tc.getClusterLabel() + " containing " +
                             tc.getSize() + " elements.");
        }
      }

      if (this.getTableOnly()){
        this.pushOutput(itable, 0);
      } else {
        this.pushOutput(cm, 0);
      }

    }  catch (Exception ex) {
    ex.printStackTrace();
    System.out.println(ex.getMessage());
    System.out.println("EXCEPTION: AddClusterAssignmentsToTable.doit()");
    throw ex;
  }

  }
}
