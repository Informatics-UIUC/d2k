package ncsa.d2k.modules.core.discovery.cluster;

/**
 * <p>Title: ClusterModel</p>
 * <p>Description: The state in this module represents model information
 * for a clustering of a dataset.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searmith
 * @version 1.0
 */

//==============
// Java Imports
//==============

import java.io.*;
import java.util.*;

//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.util.*;

public class ClusterModel
    extends ModelModule
    implements Table, Serializable {

  //==============
  // Data Members
  //==============

  private Table _table = null;
  private ArrayList _clusters = null;
  private TableCluster _root = null;

  //================
  // Constructor(s)
  //================

  public ClusterModel(ArrayList clusters) {
    _clusters = clusters;
  }

  public ClusterModel(ArrayList clusters, TableCluster root) {
    _clusters = clusters;
    _root = root;
  }

  public ClusterModel(Table tab, ArrayList clusters, TableCluster root) {
    _table = tab;
    _clusters = clusters;
    _root = root;
  }

  //================
  // Public Methods
  //================

  /**
   * Returns the table of cluster entities for this model.
   * @return Table of cluster entities for this model.
   */
  public Table getTable(){
    return _table;
  }

  /**
   * Does this model contain a table.
   * @return boolean
   */
  public boolean hasTable(){
    return _table != null;
  }

  /**
   * Does this model have a root node (cluster tree).
   * @return boolean
   */
  public boolean hasRoot(){
    return _root != null;
  }

  /**
   * Get an ArrayList of the clusters in this model.
   * @return ArrayList
   */
  public ArrayList getClusters(){
    return _clusters;
  }

  /**
   * Get the root node of the cluster tree.
   * @return TableCluster
   */
  public TableCluster getRoot(){
    return _root;
  }

  //========================
  // D2K Abstract Overrides

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Cluster Model";
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    return "No such input";
  }

  /**
     Return the name of a specific input.
     @param i The index of the input.
     @return The name of the input
   */
  public String getInputName(int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] in = null;
    return in;
  }

  /** Return information about the module.
      @return A detailed description of the module.
  */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module encapsulates a model for a hierarchical, bottom-up clustering.";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "The <i>Cluster Model</i> in this module contains the original table of examples that were clustered, ";
    s += "a tree of <i>Table Cluster</i> objects that was built during the bottom-up ";
    s += "clustering process, and a list of the clusters that were created.  The list of created ";
    s += "clusters is a subset of the clusters from the cluster tree, and represents a \"cut\" in ";
    s += "the tree.";
    s += "</p>";
    s += "<p>The <i>Cluster Model</i> implements the Table interface.  It delegates all table interface ";
    s += "functions to the contained table.  As a result, it can be passed to modules that take ";
    s += "a table as input, such as <i>TableViewer</i>.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "The model scalability is limited principally by the size of the example table it contains ";
    s += "and the memory limitations that follow from that.";
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
      return "The Cluster Model object.";
    } else {
      return "No such output.";
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
      return "No such output";
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
     Return an array of the property variables, labels, and descriptions.
     @return The editable properties of the module - none in this case
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    // hide properties that the user shouldn't udpate
    return new PropertyDescription[0];
  }

  protected void doit() throws java.lang.Exception {
    this.pushOutput(this, 0);
  }

  //=================================
  // Interface Implementation: Table
  //=================================

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public Object getObject(int row, int column) {
    if (_table == null) {
      System.out.println("ERROR: " +
			 getAlias() +
			 ".getObject(...) --  Model does not contain table data and will return null");
      return null;
    } else {
      return _table.getObject(row, column);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public int getInt(int row, int column) {
    if (_table == null) {
      System.out.println("ERROR: " +
		         getAlias() +
			 ".getInt(...) -- Model does not contain table data and will return Integer.MIN_VALUE");
      return Integer.MIN_VALUE;
    } else {
      return _table.getInt(row, column);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public short getShort(int row, int column) {
    if (_table == null) {
      System.out.println("ERROR: " +
		         getAlias() +
			 ".getShort(...) -- Model does not contain table data and will return Short.MIN_VALUE");
      return Short.MIN_VALUE;
    } else {
      return _table.getShort(row, column);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public float getFloat(int row, int column) {
    if (_table == null) {
      System.out.println("ERROR: " +
		         getAlias() +
			 ".getFloat(...) -- Model does not contain table data and will return Float.MIN_VALUE");
      return Float.MIN_VALUE;
    } else {
      return _table.getFloat(row, column);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public double getDouble(int row, int column) {
    if (_table == null) {
      System.out.println("ERROR: " +
		         getAlias() +
			 ".getDouble(...) -- Model does not contain table data and will return Double.MIN_VALUE");
      return Double.MIN_VALUE;
    } else {
      return _table.getDouble(row, column);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public long getLong(int row, int column) {
    if (_table == null) {
      System.out.println("ERROR: " +
		         getAlias() +
			 ".getLong(...) -- Model does not contain table data and will return Long.MIN_VALUE");
      return Long.MIN_VALUE;
    } else {
      return _table.getLong(row, column);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public String getString(int row, int column) {
    if (_table == null) {
      System.out.println("ERROR: " +
		         getAlias() +
			 ".getString(...) -- Model does not contain table data and will return \"\"");
      return "";
    } else {
      return _table.getString(row, column);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public byte[] getBytes(int row, int column) {
    if (_table == null) {
      System.out.println("ERROR: " +
		         getAlias() +
			 ".getBytes(...) -- Model does not contain table data and will return null");
      return null;
    } else {
      return _table.getBytes(row, column);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public boolean getBoolean(int row, int column) {
    if (_table == null) {
      System.out.println("ERROR: " +
		         getAlias() +
			 ".getBoolean(...) -- Model does not contain table data and will return false");
      return false;
    } else {
      return _table.getBoolean(row, column);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public char[] getChars(int row, int column) {
    if (_table == null) {
      System.out.println("ERROR: " +
		         getAlias() +
			 ".getChars(...) -- Model does not contain table data and will return null");
      return null;
    } else {
      return _table.getChars(row, column);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public byte getByte(int row, int column) {
    if (_table == null) {
      System.out.println("ERROR: " +
		         getAlias() +
			 ".getByte(...) -- Model does not contain table data and will return Byte.MIN_VALUE");
      return Byte.MIN_VALUE;
    } else {
      return _table.getByte(row, column);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public char getChar(int row, int column) {
    if (_table == null) {
      System.out.println("ERROR: " +
		         getAlias() +
			 ".getChar(...) -- Model does not contain table data and will return Character.MIN_VALUE");
      return Character.MIN_VALUE;
    } else {
      return _table.getChar(row, column);
    }
  }

  //////////////////////////////////////
  //// Accessing Table Metadata

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public int getKeyColumn() {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getKeyColumn(...) -- Model does not contain table data -- will return Integer.MIN_VALUE");
      return Integer.MIN_VALUE;
    } else {
      return _table.getKeyColumn();
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public void setKeyColumn(int position) {
    if (_table == null) {
      System.out.println(
          "ERROR: " + getAlias() + ".getKeyColumn(...) -- Model does not contain table data.");
    } else {
      _table.setKeyColumn(position);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public String getColumnLabel(int position) {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getColumnLabel(...) -- Model does not contain table data -- will return null");
      return null;
    } else {
      return _table.getColumnLabel(position);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public String getColumnComment(int position) {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getColumnComment(...) -- Model does not contain table data -- will return null");
      return null;
    } else {
      return _table.getColumnComment(position);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public String getLabel() {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getLabel() -- Model does not contain table data -- will return null");
      return null;
    } else {
      return _table.getLabel();
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public void setLabel(String labl) {
    if (_table == null) {
      System.out.println(
          "ERROR: " + getAlias() + ".setLabel(...) -- Model does not contain table data.");
    } else {
      _table.setLabel(labl);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public String getComment() {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getComment() -- Model does not contain table data -- will return null");
      return null;
    } else {
      return _table.getComment();
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public void setComment(String comment) {
    if (_table == null) {
      System.out.println(
          "ERROR: " + getAlias() + ".setComment(...) -- Model does not contain table data.");
    } else {
      _table.setComment(comment);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public int getNumRows() {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getNumRows() -- Model does not contain table data -- will return Integer.MIN_VALUE");
      return Integer.MIN_VALUE;
    } else {
      return _table.getNumRows();
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public int getNumEntries() {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getNumEntries() -- Model does not contain table data -- will return Integer.MIN_VALUE");
      return Integer.MIN_VALUE;
    } else {
      return _table.getNumEntries();
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public int getNumColumns() {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getNumColumns() -- Model does not contain table data -- will return Integer.MIN_VALUE");
      return Integer.MIN_VALUE;
    } else {
      return _table.getNumColumns();
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public void getRow(Object buffer, int position) {
    if (_table == null) {
      System.out.println(
          "ERROR: " + getAlias() + ".getRow(...) -- Model does not contain table data.");
    } else {
      _table.getRow(buffer, position);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public void getColumn(Object buffer, int position) {
    if (_table == null) {
      System.out.println(
          "ERROR: " + getAlias() + ".getColumn(...) -- Model does not contain table data.");
    } else {
      _table.getColumn(buffer, position);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public Table getSubset(int start, int len) {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getSubset(...) -- Model does not contain table data -- will return null");
      return null;
    } else {
      return _table.getSubset(start, len);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public Table copy() {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".copy() -- Model does not contain table data -- will return null");
      return null;
    } else {
      return _table.copy();
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public TableFactory getTableFactory() {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getTableFactory() -- Model does not contain table data -- will return null");
      return null;
    } else {
      return _table.getTableFactory();
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public boolean isColumnNominal(int position) {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".isColumnNominal(...) -- Model does not contain table data -- will return false");
      return false;
    } else {
      return _table.isColumnNominal(position);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public boolean isColumnScalar(int position) {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".isColumnScalar(...) -- Model does not contain table data -- will return false");
      return false;
    } else {
      return _table.isColumnScalar(position);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public void setColumnIsNominal(boolean value, int position) {
    if (_table == null) {
      System.out.println(
          "ERROR: " + getAlias() + ".setColumnIsNominal(...) -- Model does not contain table data.");
    } else {
      _table.setColumnIsNominal(value, position);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public void setColumnIsScalar(boolean value, int position) {
    if (_table == null) {
      System.out.println(
          "ERROR: " + getAlias() + ".setColumnIsScalar(...) -- Model does not contain table data.");
    } else {
      _table.setColumnIsScalar(value, position);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public boolean isColumnNumeric(int position) {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".isColumnNumeric(...) -- Model does not contain table data and will return false");
      return false;
    } else {
      return _table.isColumnNumeric(position);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public int getColumnType(int position) {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getColumnType(...) -- Model does not contain table data and will return Integer.MIN_VALUE");
      return Integer.MIN_VALUE;
    } else {
      return _table.getColumnType(position);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public ExampleTable toExampleTable() {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".toExampleTable() -- Model does not contain table data and will return null");
      return null;
    } else {
      return _table.toExampleTable();
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public boolean isValueMissing(int row, int col) {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".isValueMissing(...) -- Model does not contain table data and will return false");
      return false;
    } else {
      return _table.isValueMissing(row, col);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public boolean isValueEmpty(int row, int col) {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".isValueEmpty(...) -- Model does not contain table data and will return false");
      return false;
    } else {
      return _table.isValueEmpty(row, col);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public Table getSubsetByReference(int start, int len) {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getSubsetByReference(...) -- Model does not contain table data and will return null");
      return null;
    } else {
      return _table.getSubsetByReference(start, len);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public Table getSubsetByReference(int[] rows) {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getSubsetByReference(...) -- Model does not contain table data and will return null");
      return null;
    } else {
      return _table.getSubsetByReference(rows);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public Table getSubset(int[] rows) {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".getSubset(...) -- Model does not contain table data and will return null");
      return null;
    } else {
      return _table.getSubset(rows);
    }
  }

  /**
   * See Table in ncsa.d2k.modules.core.datatype.table
   */
  public boolean hasMissingValues() {
    if (_table == null) {
      System.out.println("ERROR: " + getAlias() + ".hasMissingValues() -- Model does not contain table data and will return false");
      return false;
    } else {
      return _table.hasMissingValues();
    }
  }

}
// Start QA Comments
// 4/11/03 - Ruth started QA;   Updates to error messages to use getAlias() rather than
//           hardcoded name.   Updated to ModuleInfo to add spaces & remove some tech
//           terms.  Added getPropertiesDescriptions() as no editable properties.
//           Changed getOutputNames() to getOutputName() so output port labeled correctly.
//           Ready for basic.
// End QA Comments
