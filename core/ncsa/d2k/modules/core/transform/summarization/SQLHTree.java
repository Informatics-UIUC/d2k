package ncsa.d2k.modules.core.transform.summarization;
 /**
 * <p>Title: SQLHTree </p>
 * <p>Description: Calculate a Cube from a database table based on HTree algorithm </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */
import ncsa.d2k.core.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.modules.core.io.sql.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import oracle.sql.*;
import oracle.jdbc.driver.*;

public class SQLHTree extends ComputeModule
       implements java.io.Serializable {
  JOptionPane msgBoard = new JOptionPane();
  ConnectionWrapper cw;
  Connection con;
  String tableName;
  String cubeTableName;
  String[] fieldNames;
  String whereClause;
  BinDescriptor[] bins;
  double support = 0.2;
  int maxRuleSize = 5;
  // maximum number of columns that can be included for analysis
  int maxColumn = 25;
  // allowed maximum ratio of the number of uniq values over the total number of rows
  // for each column. Binning is required if it is over.
  double maxUniqRatio = 0.3;
  int ruleSize;
  int totalRow;
  double cutOff;
  static String NOTHING = "";
  static String DELIMITER = "\t";
  static String ELN = "\n";
  static String NA = "ZZZZZ";
  // String for the list of column names seperated by ","
  String columnStr;

  // ArrayList of Column for columns
  ArrayList columnList;

  // ArrayList of Head for base header table.
  ArrayList baseHeadTbl;

  // ArrayList of Node for hTree.
  ArrayList hTree;

  // ArrayList of ArrayList for local head tables
  ArrayList localHeadTbls;

  // ArrayList of LocalHead for localHeadTbls
  ArrayList aLocalHeadTbl;

  // integer array for prefix list
  int[] prefixList;

  // class for items in ArrayList columnList
  public class Column {
    String columnName; // column name
    int uniqCnt; // the count of unique values in the column
    int startIdx; // the starting index in the baseHeadTbl
    String dataType; // the data type of the column
    boolean isBinned; // is the column binned?
  }
  // class for items in ArrayList baseHeadTbl
  public class Head {
    String value;
    int valueCnt;
    int columnOrder;
    int firstNode;
    int currNode;
  }
  //class for items in ArrayList LocalHeadTbl
  public class LocalHead extends Head {
    String value;
    int headIdx;
    int valueCnt;
    int columnOrder;
  }
  //class for items in ArrayList HTree
  public class Node {
    int headIdx;
    int nodeCnt;
    int parentIdx;
    int firstChildIdx;
    int sameValueIdx;
  }

  public void CalulateCount() {
  }

  public String getOutputInfo (int i) {
    switch(i) {
      case 0: return "JDBC data source to make database connection.";
      case 1: return "The name of the table that stores data statistics.";
      default: return "No such output";
    }
  }

  public String getInputInfo (int i) {
    switch(i) {
      case 0: return "JDBC data source to make database connection.";
      case 1: return "Field Names";
      case 2: return "Table Name";
      case 3: return "Where Clause";
      case 4: return "Bin Transform";
      default: return "No such input";
    }
  }

  public String getModuleInfo () {
    String s = "<p> Overview: ";
      s += "This module computes a cube table from a selected database table. </p>";
      s += "<p> Detailed Description: ";
      s += "This module first makes a connection to a database, retrieves the ";
      s += "data from a specified database table for the specified columns, ";
      s += "where clause, and the bin definition, and then compute a data cube. ";
      s += "You can control the cube construction by two parameters: 'maximum ";
      s += "rule size' and 'minimum support'. The 'maximum rule size' is the ";
      s += "maximum number of items in a rule. To avoid the exponential computation, ";
      s += "only up to 5 items in a rule is allowed. The 'minimum support' is used ";
      s += "to control the usefulness of discovered rules. A support of 2% ";
      s += "for a rule means that 2% of data under analysis support this rule. ";
      s += "By setting 'minimum support' lower, you would get more trivial rules. ";
      s += "However, by setting 'minimum support' higher, you might loose some ";
      s += "important rules. ";
      s += "<p> Restrictions: ";
      s += "We currently only support Oracle database. Since the intensive ";
      s += "computation in constructing data cube, the memory and CPU requirment ";
      s += "may substantially increase when more fields and more unique values ";
      s += "are included. We suggest you only choose the data set that has less ";
      s += "than 25 fields for analysis. ";
      return s;
  }

    /**
     * Get the name of the input parameter
     * @param i is the index of the input parameter
     * @return Name of the input parameter
     */
    public String getInputName (int i) {
        switch (i) {
            case 0:
                return "Database Connection";
            case 1:
                return "Selected Fields";
            case 2:
                return "Selected Table";
            case 3:
                return "Query Condition";
            case 4:
                return "Bin Transform";
            default:
                return  "No such input";
        }
    }

    /**
     * Get the name of the output parameters
     * @param i is the index of the output parameter
     * @return Name of the output parameter
     */
    public String getOutputName (int i) {
        switch (i) {
            case 0:
                return "Database Connection";
            case 1:
                return "Cube Table";
            default:
                return  "no such output!";
        }
    }

  // this property is the min acceptable support score.
  public void setSupport (double i) {
    support = i;
  }
  public double getSupport () {
    return support;
  }

  // this property is the maximum acceptable rule size.
  public void setMaxRuleSize (int yy) {
    maxRuleSize = yy;
  }
  public int getMaxRuleSize () {
    return maxRuleSize;
  }

  public String[] getInputTypes () {
    String [] in =  {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
                     "[Ljava.lang.String;",
                     "java.lang.String",
                     "java.lang.String",
                     "ncsa.d2k.modules.core.datatype.table.transforms.BinTransform"};
    return in;
  }

  public String[] getOutputTypes () {
    String [] in =  {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
                     "java.lang.String"};
    return in;
  }

  public PropertyDescription [] getPropertiesDescriptions () {
    PropertyDescription [] pds = new PropertyDescription [2];
    pds[0] = new PropertyDescription ("maxRuleSize", "Maximum Rule Size", "The maximum number of components in each rule is restricted by Maximum Rule Size.");
    pds[1] = new PropertyDescription ("support", "Minimum Support", "If the occurrence ratio of a rule is below Minimum Support, it is pruned.");
    return pds;
  }

  protected String[] getFieldNameMapping () {
    return null;
  }

  public void doit() {
    cw = (ConnectionWrapper)pullInput(0);
    fieldNames = (String[])pullInput(1);
    tableName = (String)pullInput(2);
    cubeTableName = tableName + "_CUBE";
    whereClause = (String)pullInput(3);
    BinTransform btf = (BinTransform)pullInput(4);
    bins = btf.getBinDescriptors();
    columnList = new ArrayList();
    baseHeadTbl = new ArrayList();
    hTree = new ArrayList();

    totalRow = getRowCount(tableName);
    if (totalRow > 0) {
      cutOff = totalRow * support;
      if (setColumnList()) {
        sortColumnList();
        // to avoid exponential computation, the maxRuleSize is only allowed up to 5
        if (maxRuleSize > 5) {
          maxRuleSize = 5;
        }
        // if number of selected columns <= maxruleSize, ruleSize = # of selected column - 1,
        // otherwise, ruleSize = maxRuleSize
        if (columnList.size()>=maxRuleSize) {
          ruleSize = maxRuleSize;
        }
        else {
          ruleSize = columnList.size();
        }

        initBaseHeadTbl();
        buildHTree();
        //printColumnList();  // for debugging
        //printBaseHeadTbl();  // for debugging
        //printHTree();  // for debugging

        createCubeTable();
        saveBaseHeadTbl();
        createLocalHeadTbls();
        prefixList = new int[ruleSize];
        initPrefixList();
        computeTree();
        if (getCubeRowCount(cubeTableName) > 0) {
          this.pushOutput(cw, 0);
          this.pushOutput(cubeTableName, 1);
        }
        else {
          JOptionPane.showMessageDialog(msgBoard,
          "There is no rule discovered in the data set. You may like to adjust " +
          "Minimum Support in SQLHTree module, and run again.", "Error",
          JOptionPane.ERROR_MESSAGE);
          System.out.println("There is no data in the data set.");
        }
      }
    }
    else {// totalRow <= 0
      JOptionPane.showMessageDialog(msgBoard,
                "There is no data in the data set.", "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("There is no data in the data set.");
    }
  }

  /** get the count of rows from the database table
   *  @return the number of rows in the table.
   */
  protected int getRowCount(String table) {
    try {
      con = cw.getConnection();
      Statement cntStmt;
      String sb = new String("select count(*) from " + table);
      cntStmt = con.createStatement ();
      ResultSet tableSet = cntStmt.executeQuery(sb);
      tableSet.next();
      int rowCount = tableSet.getInt(1);
      cntStmt.close();
      return(rowCount);
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in getRowCount.");
      return(0);
    }
  }

  /** get the count of rows from the database table
   *  @return the number of rows in the table.
   */
  protected int getCubeRowCount(String table) {
    try {
      con = cw.getConnection();
      Statement cntStmt;
      // we are only interested in the rules that are 2-items and up
      String sb = new String("select count(*) from " + table +
                  " where set_size > 1");
      cntStmt = con.createStatement ();
      ResultSet tableSet = cntStmt.executeQuery(sb);
      tableSet.next();
      int rowCount = tableSet.getInt(1);
      cntStmt.close();
      return(rowCount);
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in getCubeRowCount.");
      return(0);
    }
  }
  /** create the object columnList that keeps the information for each column:
   *  column name, the count of uniq values, the starting index in head table,
   *  the data type, binned or not.
   */
  protected boolean setColumnList() {
    Column aColumn;
    // if a user has selected more than maxColumn, return false and quit the program
    if (fieldNames.length > maxColumn) {
      JOptionPane.showMessageDialog(msgBoard,
                "You have selected more than " + maxColumn + " fields for analysis. " +
                "You should only choose the fields " +
                "you are interested.", "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Too many fields have been selected.");
      return false;
    }
    for (int fieldIdx=0; fieldIdx<fieldNames.length; fieldIdx++) {
      try {
        // get the unique count
        int uniqCount = getUniqCount(fieldIdx);
        // quit if something is wrong
        if (uniqCount == 0) {
          return (false);
        }
        // get the data type
        String dataType;
        if (isColumnNumeric(fieldIdx))
          dataType = "NUMBER";
        else
          dataType = "STRING";
        aColumn = new Column();
        aColumn.columnName = fieldNames[fieldIdx];
        aColumn.uniqCnt = uniqCount;
        aColumn.startIdx = -1;
        aColumn.dataType = dataType;
        aColumn.isBinned = isBinnedColumn(fieldIdx);
        columnList.add((Object) aColumn);
      }
      catch (Exception e) {
          JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
        System.out.println("Error occoured in setColumnList.");
        return false;
      }
    }
    return true;
  }

  /** Get the number of unique values in the column
   *  @param col the column to get the count
   *  @return count of the unique values
  */
  protected int getUniqCount(int col) {
    int uniqCount = 0;
    // check whether this is a binned column
    for (int binIdx = 0; binIdx < bins.length; binIdx++) {
      if (bins[binIdx].column_number == col)
        uniqCount ++;
    }
    if (uniqCount > 0)
      return (uniqCount);
    // this is not a binned column
    else {
      try {
        con = cw.getConnection();
        Statement uniqStmt;
        String sb = new String("select count(distinct " + fieldNames[col] + ") from " + tableName);
        uniqStmt = con.createStatement();
        ResultSet uniqSet = uniqStmt.executeQuery(sb);
        uniqSet.next();
        uniqCount = uniqSet.getInt(1);
        uniqStmt.close();
        // if uniqCount/totalRow > maxUniqRatio, inform users to binning the column
        if ((double)uniqCount/(double)totalRow <= maxUniqRatio)
          return (uniqCount);
        else {
          JOptionPane.showMessageDialog(msgBoard,
            "There are too many unique values in the column " +
            fieldNames[col] + ". In order to discover interesting rules, " +
            "you should group the values in the column.", "Error",
            JOptionPane.ERROR_MESSAGE);
          System.out.println("Column " + fieldNames[col] + " need to be binned.");
          return (0);
        }
      }
      catch (Exception e) {
        JOptionPane.showMessageDialog(msgBoard,
          e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
        System.out.println("Error occoured in isColumnNumeric.");
        return (0);
      }
    }
  }

  /** Determine the data type of the column
   *  @param col the column to determine the data type
   *  @return true if the column's data type is numeric, false otherwise.
   */
  protected boolean isColumnNumeric(int col) {
    DatabaseMetaData metadata = null;
    try {
      con = cw.getConnection();
      metadata = con.getMetaData();
      String[] names = {"TABLE"};
      ResultSet tableNames = metadata.getTables(null,"%",tableName,names);
      while (tableNames.next()) {
        ResultSet columns = metadata.getColumns(null,"%",tableNames.getString("TABLE_NAME"),"%");
        while (columns.next()) {
          String columnName = columns.getString("COLUMN_NAME");
          String dataType = columns.getString("TYPE_NAME");
          if (fieldNames[col].equals(columnName)) {
            if (dataType.equals("NUMBER") ||
                dataType.equals("INTEGER") ||
                dataType.equals("FLOAT") ||
                dataType.equals("NUMERIC")) {
              return true;
            }
            else {
              return false;
            }
          }
        }
        return false;
      }
      return false;
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in isColumnNumeric.");
      return false;
    }
  }

  /** determine whether the column needs binning
   *  @param col the column to check
   *  @return true if the column has been binned, false otherwise
   */
  protected boolean isBinnedColumn(int col) {
    for (int binIdx = 0; binIdx < bins.length; binIdx++) {
      if (bins[binIdx].column_number == col)
        return (true);
    }
    return (false);
  }

  /** order the columns based on cardinality. This will put the column with
   *  less unique values to the higher level in the HTree. This is important for
   *  the efficiency.
   */
  protected void sortColumnList() {
    for (int i=0; i<columnList.size(); i++) {
      for (int j=columnList.size()-1; j>i; j--) {
        if (((Column) columnList.get(j-1)).uniqCnt >
          ((Column) columnList.get(j)).uniqCnt) {
          Object tmpObj = columnList.get(j-1);
          columnList.set(j-1, columnList.get(j));
          columnList.set(j, tmpObj);
        }
      }
    }
    // set the starting index in baseHeadTbl for each column to speed up search
    int startIdx = 1; // first record is for root
    for (int colIdx=0; colIdx<columnList.size(); colIdx++) {
      Column aColumn = (Column)columnList.get(colIdx);
      aColumn.startIdx = startIdx;
      startIdx = startIdx + aColumn.uniqCnt + 1; // one extra item for value NA
    }
  }

  /** Initialize the base header table
   */
  protected void initBaseHeadTbl() {
    Head aHead;
    // The record for root node
    aHead = new Head();
    aHead.value = "ROOT";
    aHead.valueCnt = 0;
    aHead.columnOrder = -1;
    aHead.firstNode = 0;
    aHead.currNode = 0;
    baseHeadTbl.add((Object) aHead);
    try {
      con = cw.getConnection();
      int colIdx = 0;
      boolean binnedColumn = false;
      while (colIdx < columnList.size()) {
        Column aColumn = (Column)columnList.get(colIdx);
        String colName = aColumn.columnName;
        // if this column is a binned column, get the unique value from bins
        if (aColumn.isBinned) {
          for (int binIdx = 0; binIdx < bins.length; binIdx++) {
            if (bins[binIdx].label.equals(colName)) {
              aHead = new Head();
              aHead.value = bins[binIdx].name;
              aHead.columnOrder = colIdx;
              aHead.firstNode = -1;
              aHead.currNode = -1;
              baseHeadTbl.add((Object) aHead);
            }
          }
          // add an extra item for value NA
          aHead = new Head();
          aHead.value = NA;
          aHead.valueCnt = 0;
          aHead.firstNode = -1;
          aHead.currNode = -1;
          baseHeadTbl.add((Object) aHead);
          colIdx++;
        }
        else { // this column is not binned, get the unique value from the database table
          String valueQry = new String("select distinct " + colName + " from ");
          if (aColumn.dataType.equals("NUMBER"))
            // conver all numeric value to string before order it
            valueQry = valueQry + tableName + " where " + colName + " is not null order by (to_char(" + colName + "))";
          else
            valueQry = valueQry + tableName + " where " + colName + " is not null order by " + colName;
          Statement valueStmt = con.createStatement();
          ResultSet valueSet = valueStmt.executeQuery(valueQry);
          while (valueSet.next()) {
            aHead = new Head();
            aHead.value = valueSet.getString(1);
            aHead.columnOrder = colIdx;
            aHead.firstNode = -1;
            aHead.currNode = -1;
            baseHeadTbl.add((Object) aHead);
          }
          valueStmt.close();
          // add an extra item for value NA
          aHead = new Head();
          aHead.value = NA;
          aHead.valueCnt = 0;
          aHead.firstNode = -1;
          aHead.currNode = -1;
          baseHeadTbl.add((Object) aHead);
          colIdx++;
        }
      }
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in initBaseHeadTbl.");
    }
  }

  /** Build a HTree.
  */
  protected void buildHTree() {
    // The record for root
    Node aNode;
    aNode = new Node();
    aNode.headIdx = 0;
    aNode.nodeCnt = 0;
    aNode.parentIdx = 0;
    aNode.firstChildIdx = -1;
    aNode.sameValueIdx = -1;
    hTree.add((Object) aNode);
    try {
      con = cw.getConnection();
      Statement treeStmt;
      String sb = new String("select ");
      String aValue;
      columnStr=NOTHING;
      int startIdx;
      int endIdx;
      int itemIdx = 0;
      int parentIdx;
      int newParentIdx = -1;
      int newCnt;
      int lastIdx;
      // data must be retrieved by the column order
      for (int colIdx = 0; colIdx < columnList.size(); colIdx++) {
        if (columnStr.equals(NOTHING))
          columnStr = columnStr + ((Column)columnList.get(colIdx)).columnName;
        else // add comma between column names
          columnStr = columnStr + ", " + ((Column)columnList.get(colIdx)).columnName;
      }
      sb = sb + columnStr + " from " + tableName;
      treeStmt = con.createStatement ();
      ResultSet tableSet = treeStmt.executeQuery(sb);
      while (tableSet.next()) {
        parentIdx = 0;
        for (int colIdx = 0; colIdx < columnList.size(); colIdx++) {
          Column aColumn = (Column)columnList.get(colIdx);
          // tableSet's data starts from index 1
          aValue = tableSet.getString(colIdx+1);
          startIdx = aColumn.startIdx;
          endIdx = startIdx + aColumn.uniqCnt; // don't subtract 1, because need to add one extra item for NA
          if (aValue==null) { // it is null, replace it with value of NA
            aValue = NA;
            //System.out.println("null value is converted to " + NA);
          }
          else if (aValue.equals(" ") || aValue.equals("")) { // it is a blank string, replace it with value of NA
            aValue = NA;
            //System.out.println("blank value is converted to " + NA);
          }
          if (!aColumn.isBinned) {
            // Searching baseHeadTble to find the record with this value.
            // To speed up the search, first find the starting index and ending index
            // in baseHeadTbl for this column.
            itemIdx = getIndex(startIdx, endIdx, aValue, false);
          }
          else if (aColumn.isBinned && aValue.equals(NA)) {
            itemIdx = endIdx; // NA value is always the last item for the column
          }
          else if (aColumn.isBinned && aColumn.dataType.equals("STRING")) {
            aValue = binValue(colIdx, aValue);
            itemIdx = getIndex(startIdx, endIdx, aValue, true);
          }
          else if (aColumn.isBinned && aColumn.dataType.equals("NUMBER")) {
            aValue = binValue(colIdx, Double.valueOf(aValue).doubleValue());
            itemIdx = getIndex(startIdx, endIdx, aValue, true);
          }
          if (itemIdx >= 0) {
            newParentIdx = updLinks(itemIdx, parentIdx);
          }
          parentIdx = newParentIdx;
        }
      }
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
        System.out.println("Error occoured in buildHTree.");
    }
  }

  /** get a binned value for a string value
   *  @param col the column to get the value
   *  @param val the original string value
   *  @return a binned value for the original value
   */
  protected String binValue(int col, String val) {
    String colName = ((Column)columnList.get(col)).columnName;
    for (int binIdx = 0; binIdx < bins.length; binIdx++) {
      if (bins[binIdx].label.equals(colName)) {
        if (bins[binIdx].eval(val)) {
          return (bins[binIdx].name);
        }
      }
    }
    return (val);
  }

  /** get a binned value for a numeric value
   *  @param col the column to get the value
   *  @param val the original numeric value
   *  @return a binned string value for the original numeric value
   */
  protected String binValue(int col, double val) {
    String colName = ((Column)columnList.get(col)).columnName;
    for (int binIdx = 0; binIdx < bins.length; binIdx++) {
      if (bins[binIdx].label.equals(colName)) {
        if (bins[binIdx].eval(val)) {
          return (bins[binIdx].name);
        }
      }
    }
    return (Double.toString(val));
  }

  /** find the index in base head table for a giving string value
   *  @param first the starting index in baseHeadTbl for the column
   *  @param last the ending index in baseHeadTbl for the column
   *  @param val the string value
   *  @param isBinned the flag for binning
   *  @return the index in baseHeadTbl for this string value
   */
  protected int getIndex(int first, int last, String val, boolean isBinned) {
    Head aHead;
    // use binary search to find the value's index for non binned column
    if (!isBinned) {
      while ((last - first) > 1) {
        aHead = (Head)baseHeadTbl.get((first+last)/2);
        if (val.compareTo(aHead.value) > 0) {
          first = ((first+last)/2);
        }
        else if (val.compareTo(aHead.value) < 0) {
          last = ((first+last)/2);
        }
        else { // match the value
          return ((first+last)/2);
        }
      }
      // when the binary search has narrowed done to two items, check both
      if (val.compareTo(((Head)baseHeadTbl.get(first)).value)==0) {
        return(first);
      }
      else if (val.compareTo(((Head)baseHeadTbl.get(last)).value)==0) {
        return(last);
      }
      else { // should never get to this point
        JOptionPane.showMessageDialog(msgBoard,
                "Can't find the item in the baseHeadTbl for " + val, "Error",
                JOptionPane.ERROR_MESSAGE);
        System.out.println("Error occoured in getIndex 1.");
        return (-1); // not found the item
      }
    }
    else {// use sequencial search to find the value's index for the binned column
      for (int i=first; i<=last; i++) {
        aHead = (Head)baseHeadTbl.get(i);
        if (val.equals(aHead.value))
          return(i);
      }
      // should never get to this point
      JOptionPane.showMessageDialog(msgBoard,
                "Can't find the item in the baseHeadTbl for " + val, "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in getIndex 2.");
      return (-1); // not found the item
    }
  }

  /** update baseHeadTbl and insert a new node into hTree
   *  @param hdIdx current node's index in baseHeadTbl
   *  @param pIdx parent node's index in baseHeadTbl
   *  @return index in hTree
   */
  protected int updLinks(int hdIdx, int pIdx) {
    int treeIdx = 0;
    int firstNodeIdx;
    boolean isFirst;
    Head aHead = (Head)baseHeadTbl.get(hdIdx);
    aHead.valueCnt++;
    // If the index of first-node = -1, then create a new record in hTree,
    // and update the index of first-node and the index of current-node.
    firstNodeIdx = aHead.firstNode;
    treeIdx = hTree.size();
    if (firstNodeIdx == -1) {
      createNode(hdIdx, pIdx);
      aHead.firstNode = treeIdx; // index for first-node
      aHead.currNode = treeIdx; // index for current-node
      updFirstChild(treeIdx, pIdx);
      updSameValue(treeIdx, hdIdx);
      return(treeIdx);
    }
    // If the index of first-node >= 0, then follow the index of first-node
    // to hTree. Check the index of parent-node for every node following the
    // chain of same-value-node. If parent is same, update count in hTree. If no
    // same parent found, create a new record in hTree.
    else if (firstNodeIdx >= 0) {
      while (firstNodeIdx >= 0) {
        // parent is same
        Node aNode = (Node)hTree.get(firstNodeIdx);
        if (aNode.parentIdx == pIdx){
          aNode.nodeCnt ++;
          return(firstNodeIdx);
        }
        else { // parent is different, follow same-value-node to search
          firstNodeIdx = aNode.sameValueIdx;
        }
      }
      // no same parent found
      if (firstNodeIdx == -1) {
        createNode(hdIdx, pIdx);
        // update index of current node in baseHeadTbl
        aHead.currNode = treeIdx;
        updFirstChild(treeIdx, pIdx);
        updSameValue(treeIdx, hdIdx);
        return(treeIdx);
      }
      System.out.println("Error 1 in calculate parent index");
      return (-1); // should never reach here
    }
    System.out.println("Error 2 in calculate parent index");
    return (-1); // should never reach here
  }

  /**  update the first child index.
   *   @param treeIdx the current node's index in hTree.
   *   @param parentIdx the parent node's index in hTree.
   */
  protected void updFirstChild(int treeIdx, int parentIdx) {
    int firstChildIdx;
    int currNodeIdx;
    /* update index of first-child in hTree */
    // find the firstChildIdx from hTree for the current parent node
    Node aNode = (Node)hTree.get(parentIdx);
    // if parent has no first-child, update the index of first-child for the parent node
    if (aNode.firstChildIdx == -1) {
      aNode.firstChildIdx = treeIdx;
    }
  }

  /** Find the first node in baseHeadTbl, then follow the same value link in
  *   hTree to update the Same Value index for the immediate previous node of same value
  *   @param treeIdx the current node's index in hTree
  *   @param headIdx the current node's index in baseHeadTbl
  */
  protected void updSameValue(int treeIdx, int headIdx) {
    int firstNodeIdx;
    int sameValIdx;
    firstNodeIdx = ((Head)baseHeadTbl.get(headIdx)).firstNode;
    sameValIdx = ((Node)hTree.get(firstNodeIdx)).sameValueIdx;
    while (sameValIdx != -1) {
      if (firstNodeIdx != treeIdx) {
        firstNodeIdx = ((Node)hTree.get(firstNodeIdx)).sameValueIdx;
        sameValIdx = ((Node)hTree.get(firstNodeIdx)).sameValueIdx;
      }
      else break;
    }
    if (sameValIdx == -1 && firstNodeIdx != treeIdx) {
      ((Node)hTree.get(firstNodeIdx)).sameValueIdx = treeIdx;
    }
  }

  /** Create a new node in hTree
   *  @param headIdx the current node's index in baseHeadTbl
   *  @param parentIdx the parent node's index in baseHeadTbl
   */
  protected void createNode(int headIdx, int parentIdx) {
    Node aNode = new Node();
    aNode.headIdx = headIdx;
    aNode.nodeCnt = 1;
    aNode.parentIdx = parentIdx;
    aNode.firstChildIdx = -1;
    aNode.sameValueIdx = -1;
    hTree.add((Object) aNode);
  }

  /** create a database table to save the cube data
   */
  protected void createCubeTable() {
    try {
      con = cw.getConnection();
      Statement countStmt;
      Statement tableStmt;
      Statement columnStmt;
      Statement alterStmt;
      String sb;
      String tableQry;
      String columnQry;
      ResultSet result;
      ResultSet tableResult;
      ResultSet columnResult;
      // if the cube table already exists, drop the table first
      sb = new String("select count(1) from all_tables where table_name = '" + cubeTableName.toUpperCase() + "'");
      countStmt = con.createStatement();
      ResultSet tableSet = countStmt.executeQuery(sb);
      tableSet.next();
      int rowCount = tableSet.getInt(1);
      countStmt.close();

      if (rowCount > 0) {
        sb = new String("drop table " + cubeTableName);
        countStmt = con.createStatement();
        result = countStmt.executeQuery(sb);
        countStmt.close();
      }

      // Create an empty cube table. The table has the selected columns + count column
      // Convert all numeric data types to character data type
      tableQry = new String("create table " + cubeTableName + "(");
      columnQry = new String("select column_name, data_type, data_length from all_tab_columns ");
      columnQry = columnQry + "where table_name = '" + tableName + "' order by column_id";
      columnStmt = con.createStatement();
      columnResult = columnStmt.executeQuery(columnQry);
      int colNumber = 0;
      while (columnResult.next()) {
        if (isChosenColumn(columnResult.getString(1))) {
          if (colNumber > 0) {
            tableQry = tableQry + ",";
          }
          colNumber ++;
          if (columnResult.getString(2).equals("VARCHAR2")) {
          tableQry = tableQry + columnResult.getString(1) + " VARCHAR2(" +
                     columnResult.getString(3) + ")";
          }
          else if (columnResult.getString(2).equals("NUMBER")) {
            tableQry = tableQry + columnResult.getString(1) + " VARCHAR2(30)";
          }
          else if (columnResult.getString(2).equals("DATE")) {
            tableQry = tableQry + columnResult.getString(1) + " VARCHAR2(30)";
          }
        }
      }
      tableQry = tableQry + ")";

      tableStmt = con.createStatement();
      result = tableStmt.executeQuery(tableQry);
      tableStmt.close();
      // add two columns to the cube table, one column for saving the size of the item set, another for saving counts
      sb = new String("alter table " + cubeTableName + " add (set_size number, cnt number)");
      alterStmt = con.createStatement();
      result = alterStmt.executeQuery(sb);
      alterStmt.close();
      // insert a record to the cube table. This record is the most aggregated record. There is
      // no value for all columns except the column cnt which is the total number of rows in the
      // data set.
      sb = new String ("insert into " + cubeTableName + " (cnt) values (" + totalRow + ")");
      alterStmt = con.createStatement();
      result = alterStmt.executeQuery(sb);
      alterStmt.close();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
        e.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
        System.out.println("Error occoured in createCubeTable.");
    }
  }

  protected boolean isChosenColumn(String columnName) {
    boolean match = false;
    for (int colIdx = 0; colIdx < columnList.size(); colIdx++) {
      if (((Column) columnList.get(colIdx)).columnName.equals(columnName)) {
        match = true;
        break;
      }
    }
    return(match);
  }

  /** Save the base header table
   */
  protected void saveBaseHeadTbl() {
    Column aColumn;
    Head aHead;
    int columnOrder;
    String insertStr = NOTHING;

    // baseHeadTbl[0] is root
    for (int headIdx=baseHeadTbl.size()-1; headIdx > 0; headIdx--) {
      aHead = (Head)baseHeadTbl.get(headIdx);
      columnOrder = aHead.columnOrder;
      aColumn = (Column)columnList.get(columnOrder);
      if (columnOrder < 0) break;
      if (aHead.valueCnt >= cutOff) {
        insertStr = "insert into " + cubeTableName + " (" +
                  aColumn.columnName + ", set_size, cnt)";
        insertStr = insertStr + " values ('" + aHead.value + "', 1, ";
        insertStr = insertStr + aHead.valueCnt + ")";
        try {
          if (insertStr.indexOf(NA)<0) { // if any column value contains NA, not save it
            con = cw.getConnection();
            Statement stmt;
            String sb;
            ResultSet result;
            stmt = con.createStatement();
            result = stmt.executeQuery(insertStr);
            stmt.close();
          }
        }
        catch (Exception e){
          JOptionPane.showMessageDialog(msgBoard,
            e.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occoured in saveBaseHeadTbl.");
        }
      }
    }
  }

  /** Create an ArrayList to hold all local header tables
   *  Each localHeadTbl for an item set: [0] for 2-item set, [1] for 3-item set, ..., etc
   */
  protected void createLocalHeadTbls () {
    int maxUniq = 0;
    localHeadTbls = new ArrayList();
    // max size for each localHeadTbl is total uniq count except last column
    for (int colIdx = 0; colIdx < columnList.size()-1; colIdx++) {
      maxUniq = maxUniq + ((Column) columnList.get(colIdx)).uniqCnt + 1; // one extra for value NA
    }
    for (int ruleIdx = 0; ruleIdx < ruleSize; ruleIdx ++) {
      ArrayList aLocalHeadTbl = new ArrayList();
      for (int localIdx = 0; localIdx < maxUniq; localIdx++) {
        LocalHead aLocalHead = new LocalHead();
        aLocalHead.value = NOTHING;
        aLocalHead.headIdx = -1;
        aLocalHead.valueCnt = 0;
        aLocalHead.columnOrder = -1;
        aLocalHeadTbl.add(aLocalHead);
      }
      localHeadTbls.add(aLocalHeadTbl);
    }
  }

  /** Initialize the prefix list
   *  For 2-item set, the prefix is 1 item, and for 3-item set, the prefix is 2 items, etc ...
   */
  protected void initPrefixList() {
    for (int prefixIdx = 0; prefixIdx < prefixList.length; prefixIdx ++) {
      prefixList[prefixIdx] = -1;
    }
  }

  /** Computer every node in the hTree, starting from level 2 to max level
   */
  protected void computeTree() {
    // start from level 2
    // get column value from baseHeadTbl in the range startIdx - endIdx
    int colIdx;
    for (colIdx = 1; colIdx < columnList.size(); colIdx++) {
      int startIdx = ((Column)columnList.get(colIdx)).startIdx;
      int endIdx = startIdx + ((Column)columnList.get(colIdx)).uniqCnt + 1;
      int headIdx = startIdx;
      while (headIdx < endIdx) {
        if (((Head)baseHeadTbl.get(headIdx)).valueCnt >= cutOff) {
          computeItemSet(headIdx, colIdx, 0);
        }
        headIdx ++;
      }
    }
  }

  /** compute the localHeadTbl against a value in the level with hTreeLevel.
   *  The hTreeLevel is equivalent to the column index, because one column goes to one level in the tree.
   *  @param headIdx the index in baseHeadTbl
   *  @param hTreeLevel the index of the column, also the level of hTree
   *  @param localHeadTblsIdx the index of localHeadTblsIdx. This determine which localHeadTbl to go.
   *         0 for 2-item set, 1 for 3-item set, ..., etc
   */
  protected void computeItemSet(int headIdx, int hTreeLevel, int localHeadTblsIdx) {
    int firstNode;
    int newNode;
    int parentTreeIdx;
    int parentHeadIdx;
    int nodeCnt;
    ArrayList localHeadTbl = null;

    resetHTreeCnt(hTreeLevel);
    updPrefixList(headIdx, localHeadTblsIdx);
    initLocalHeadTbl(localHeadTblsIdx);
    firstNode = ((Head)baseHeadTbl.get(headIdx)).firstNode;
    while (firstNode != -1) {
      // get the count for the current node
      nodeCnt = ((Node)hTree.get(firstNode)).nodeCnt;
      // find the parent index
      parentTreeIdx = ((Node)hTree.get(firstNode)).parentIdx;
      while (parentTreeIdx > 0) {  // 0 is the root
        // find the index in the baseHeadTbl for the parent node
        parentHeadIdx = ((Node)hTree.get(parentTreeIdx)).headIdx;
        //Update localHeadTbls.
        localHeadTbl = (ArrayList) localHeadTbls.get(localHeadTblsIdx);
        for (int localIdx=0; localIdx < localHeadTbl.size(); localIdx++) {
          if (((LocalHead)localHeadTbl.get(localIdx)).headIdx == parentHeadIdx) {
            ((LocalHead)localHeadTbl.get(localIdx)).valueCnt =
              ((LocalHead)localHeadTbl.get(localIdx)).valueCnt + nodeCnt;
              break;
          }
        }
        // propagate the current count to the parent node in hTree
        ((Node)hTree.get(parentTreeIdx)).nodeCnt =
          ((Node)hTree.get(parentTreeIdx)).nodeCnt + nodeCnt;
        // go to the grand parents
        parentTreeIdx = ((Node)hTree.get(parentTreeIdx)).parentIdx;
      }
      //following the sameValue link to go to the next node
      firstNode = ((Node)hTree.get(firstNode)).sameValueIdx;
    }
    //printLocalHeadTbl(localHeadTbl); // for debugging
    //printHTree(); // for debugging

    saveLocalHeadTbl(localHeadTblsIdx);

    if (hTreeLevel > 1 && localHeadTblsIdx < (ruleSize-2)) {
      for (int colIdx = 1; colIdx < (hTreeLevel); colIdx++) {
        Column aColumn = (Column)columnList.get(colIdx);
        int startIdx = aColumn.startIdx;
        int endIdx = startIdx + aColumn.uniqCnt + 1;
        int newHead = startIdx;
        while (newHead < endIdx) {
          if (((Head)baseHeadTbl.get(newHead)).valueCnt >= cutOff) {
            computeItemSet (newHead, colIdx, localHeadTblsIdx+1);
          }
          newHead++;
        }
        // reset the last valid item in prefixList because the recursion on this level should stop.
        // The prefixList should be ready for recursion of one level up.
        for (int prefixIdx = prefixList.length-1; prefixIdx >= 0; prefixIdx--) {
          if (prefixList[prefixIdx] >= 0) {
            prefixList[prefixIdx] = -1;
            break;
          }
        }
      }
    }
  }

  /** reset counts in hTree for the nodes which are in columns less than columnIdx
   *  @param columnIdx the index of the column
   */
  protected void resetHTreeCnt(int columnIdx) {
    int headIdx;
    for (int nodeIdx = 0; nodeIdx < hTree.size(); nodeIdx++) {
      Node aNode = (Node)hTree.get(nodeIdx);
      headIdx = aNode.headIdx;
      if (((Head)baseHeadTbl.get(headIdx)).columnOrder < columnIdx) {
        aNode.nodeCnt = 0;
      }
    }
  }

  /** Initialized the local header table
   *  @param localTblIdx the index in localHeadTbls. This is the local header
   *         table that needs to reinitialize
   */
  protected void initLocalHeadTbl (int localTblIdx) {
    ArrayList aLocalTbl = (ArrayList)localHeadTbls.get(localTblIdx);
    // wipe out the previous data
    for (int itemIdx = 0; itemIdx < aLocalTbl.size(); itemIdx++) {
      LocalHead aLocalHead = (LocalHead) aLocalTbl.get(itemIdx);
      aLocalHead.value = NOTHING;
      aLocalHead.headIdx = -1;
      aLocalHead.valueCnt = 0;
      aLocalHead.columnOrder = -1;
    }
    // find the smallest columnIndex in prefixList
    int minCol = Integer.MAX_VALUE;
    for (int prefixIdx = 0; prefixIdx < prefixList.length; prefixIdx++) {
      if (prefixList[prefixIdx] >= 0) {
        int columnIndex = ((Head)baseHeadTbl.get(prefixList[prefixIdx])).columnOrder;
        if (columnIndex < minCol) {
          minCol = columnIndex;
        }
      }
    }
    // enter the current data
    int startIdx = ((Column)columnList.get(minCol)).startIdx;
    int newHeadIdx = 1;  // baseHeadTbl[0] is for root
    int itemIdx = 0;
    while (newHeadIdx < startIdx) {
      Head aHead = (Head)baseHeadTbl.get(newHeadIdx);
      LocalHead aLocalHead = (LocalHead)aLocalTbl.get(itemIdx);
      aLocalHead.value = aHead.value;
      aLocalHead.headIdx = newHeadIdx;
      aLocalHead.columnOrder = aHead.columnOrder;
      newHeadIdx++;
      itemIdx++;
    }
    //printLocalHeadTbl(aLocalTbl);  // for debugging
  }

  /** add the index of the head node to the prefix list after the computation
   *  of a level is completed
   *  @param newHeadIdx the new head node index to add
   *  @param newLocalTblIdx the index in localHeadTbls. This index is equal to
   *         the index in prefixList.
   */
  protected void updPrefixList (int newHeadIdx, int newLocalTblIdx) {
    if (newLocalTblIdx == 0) {
      initPrefixList();
    }
    prefixList[newLocalTblIdx] = newHeadIdx;
    //printPrefixList();  // for debugging
  }

  protected void saveLocalHeadTbl(int localTblsIdx) {
    String insertStr1 = NOTHING;
    String insertStr2 = NOTHING;
    String queryStr = NOTHING;
    String columnName;
    String newColName;
    int columnOrder;
    String columnValue;
    int newHeadIdx;
    int newColIdx;

    insertStr1 = "insert into " + cubeTableName + " (";
    insertStr2 = "values (";
    for (int prefixIdx=0; prefixIdx<prefixList.length; prefixIdx++) {
      if (prefixList[prefixIdx] >= 0) {
        columnValue = ((Head)baseHeadTbl.get(prefixList[prefixIdx])).value;
        columnOrder = ((Head)baseHeadTbl.get(prefixList[prefixIdx])).columnOrder;
        columnName = ((Column)columnList.get(columnOrder)).columnName;
        if (prefixIdx>0) {
          // add "," to seperate column names
          insertStr1 = insertStr1 + ", ";
          insertStr2 = insertStr2 + ", ";
        }
        insertStr1 = insertStr1 + columnName;
        insertStr2 = insertStr2 + "'" + columnValue + "'";
      }
      else break;
    }
    ArrayList localHeads = (ArrayList) localHeadTbls.get(localTblsIdx);
    for (int localIdx=0; localIdx < localHeads.size(); localIdx++) {
      LocalHead aLocalHead = (LocalHead)localHeads.get(localIdx);
      newHeadIdx = aLocalHead.headIdx;
      if (newHeadIdx == -1) {
        break; // reach the end
      }
      if (aLocalHead.valueCnt >= cutOff) {
        newColIdx = ((Head)baseHeadTbl.get(newHeadIdx)).columnOrder;
        newColName = ((Column)columnList.get(newColIdx)).columnName;
        queryStr = insertStr1 + ", " + newColName + ", set_size, cnt) " + insertStr2 + ", '" +
                 aLocalHead.value + "', " +
                 (localTblsIdx + 2) + ", " +     //localTbls[0] for 2-item set, [1] for 3-item set
                 aLocalHead.valueCnt + ")";
        try {
          if (queryStr.indexOf(NA)<0 && queryStr.indexOf("''")<0) { // if any column value contains NA, not save it
            con = cw.getConnection();
            Statement stmt;
            String sb;
            ResultSet result;
            stmt = con.createStatement();
            result = stmt.executeQuery(queryStr);
            stmt.close();
          }
        }
        catch (Exception e){
          JOptionPane.showMessageDialog(msgBoard,
            e.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
            System.out.println("Error occoured in saveBaseHeadTbl.");
        }
      }
    }
  }

  protected void printHTree() {
    System.out.println("hTree: ");
    for (int i=0; i<hTree.size(); i++) {
          System.out.print(i + " item in hTree is " + ((Node)hTree.get(i)).headIdx + ", ");
          System.out.print(((Node)hTree.get(i)).nodeCnt + ", ");
          System.out.print(((Node)hTree.get(i)).parentIdx + ", ");
          System.out.print(((Node)hTree.get(i)).firstChildIdx + ", ");
          System.out.println(((Node)hTree.get(i)).sameValueIdx);
    }
  }

  protected void printBaseHeadTbl() {
    System.out.println("base head table: ");
    for (int i=0; i<baseHeadTbl.size(); i++) {
          System.out.print("The head " + i + " is " + ((Head)baseHeadTbl.get(i)).value + ", ");
          System.out.print(((Head)baseHeadTbl.get(i)).valueCnt + ", ");
          System.out.print(((Head)baseHeadTbl.get(i)).columnOrder + ", ");
          System.out.print(((Head)baseHeadTbl.get(i)).firstNode + ", ");
          System.out.println(((Head)baseHeadTbl.get(i)).currNode);
    }
  }

  protected void printColumnList() {
    System.out.println("column list: ");
    for (int i=0; i<columnList.size(); i++) {
      System.out.println("The i " + i + " column is " + ((Column)columnList.get(i)).columnName);
      System.out.println("             " + ((Column)columnList.get(i)).uniqCnt);
      System.out.println("             " + ((Column)columnList.get(i)).startIdx);
      System.out.println("             " + ((Column)columnList.get(i)).dataType);
      System.out.println("             " + ((Column)columnList.get(i)).isBinned);
    }
  }

  protected void printLocalHeadTbl(ArrayList localHeadTable) {
      System.out.println("local head table: ");
      for (int i = 0; i<localHeadTable.size(); i++) {
        System.out.println(i + " item: " + ((LocalHead)localHeadTable.get(i)).value +
          ", " + ((LocalHead)localHeadTable.get(i)).headIdx +
          ", " + ((LocalHead)localHeadTable.get(i)).valueCnt +
          ", " + ((LocalHead)localHeadTable.get(i)).columnOrder);
      }
  }

  protected void printPrefixList () {
    System.out.println("prefixList is: ");
    for (int i = 0; i < prefixList.length; i++) {
      System.out.print(prefixList[i] + ", ");
    }
    System.out.println("...");
  }
}

