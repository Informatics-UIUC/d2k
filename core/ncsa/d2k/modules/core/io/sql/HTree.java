package ncsa.d2k.modules.core.io.sql;
 /**
 * <p>Title: HTree </p>
 * <p>Description: Calculate a Cube based on HTree algorithm </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: NCSA ALG </p>
 * @author Dora Cai
 * @version 1.0
 */
import ncsa.d2k.core.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import oracle.sql.*;
import oracle.jdbc.driver.*;

public class HTree extends ComputeModule
       implements java.io.Serializable {
  JOptionPane msgBoard = new JOptionPane();
  ConnectionWrapper cw;
  Connection con;
  String tableName;
  String cubeTableName;
  String[] fieldNames;
  String whereClause;
  double support = 0.2;
  int maxRuleSize = 5;
  int ruleSize;
  int totalRow;
  double cutOff;
  static String NOTHING = "";
  static String DELIMITER = "\t";
  static String ELN = "\n";
  static String NA = "NOVAL";
  // String for the list of column names seperated by ","
  String columnStr;

  // ArrayList of Column for columns
  ArrayList columnList;

  // ArrayList of Head for base header table.
  ArrayList baseHeadTbl;

  // ArrayList of Node for Htree.
  ArrayList hTree;

  // ArrayList of ArrayList for local head tables
  ArrayList localHeadTbls;

  // ArrayList of LocalHead for localHeadTbls
  ArrayList aLocalHeadTbl;

  // integer array for prefix list
  int[] prefixList;

  // class for items in ArrayList columnList
  public class Column {
    String columnName;
    int uniqCnt;
    int startIdx;
    String dataType;
  }
  // class for items in ArrayList baseHeadTbl
  public class Head {
    String value;
    // int headIdx;
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
  //class for items in ArrayList hTree
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
      default: return "No such input";
    }
  }

  public String getModuleInfo () {
    String text = "Compute a cube for all possible combinations for the selected table";
    return text;
  }

  /** this property is the min acceptable support score. */
  public void setMinimumSupport (double i) {
    support = i;
  }
  public double getMinimumSupport () {
    return support;
  }
  /** this property is the maximum acceptable rule size. */
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
                     "java.lang.String"};
    return in;
  }

  public String[] getOutputTypes () {
    String [] in =  {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
                     "java.lang.String"};
    return in;
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
    columnList = new ArrayList();
    baseHeadTbl = new ArrayList();
    hTree = new ArrayList();

    totalRow = getRowCount();
    cutOff = totalRow * support;

    setColumnList();
    sortColumnList();

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
    this.pushOutput(cw, 0);
    this.pushOutput(cubeTableName, 1);
  }

  /** get the count of rows from the database table */
  protected int getRowCount() {
    try {
      con = cw.getConnection();
      Statement cntStmt;
      String sb = new String("select count(1) from " + tableName);
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

  /** create columnList.
   */
  protected void setColumnList() {
    Column aColumn;
    for (int fieldIdx=0; fieldIdx<fieldNames.length; fieldIdx++) {
      try {
        con = cw.getConnection();
        Statement uniqStmt;
        String sb = new String("select count(distinct " + fieldNames[fieldIdx] + ") from " + tableName);
        uniqStmt = con.createStatement();
        ResultSet uniqSet = uniqStmt.executeQuery(sb);
        uniqSet.next();
        int uniqCount = uniqSet.getInt(1);
        uniqStmt.close();
        con = cw.getConnection();
        Statement typeStmt;
        sb = new String("select data_type from all_tab_columns where table_name=upper('" +
             tableName + "') and column_name=upper('" + fieldNames[fieldIdx] + "')");
        typeStmt = con.createStatement();
        ResultSet typeSet = typeStmt.executeQuery(sb);
        typeSet.next();
        String dataType;
        if (typeSet.getString(1).equals("NUMBER"))
          dataType = "NUMBER";
        else
          dataType = "STRING";
        typeSet.close();
        aColumn = new Column();
        aColumn.columnName = fieldNames[fieldIdx];
        aColumn.uniqCnt = uniqCount;
        aColumn.startIdx = -1;
        aColumn.dataType = dataType;
        columnList.add((Object) aColumn);
      }
      catch (Exception e) {
	  JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
        System.out.println("Error occoured in setColumnList.");
      }
    }
  }

  /** order the columns based on cardinality */
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
    // set the starting index in baseHeadTbl for each column
    int startIdx = 1; // first record is for root
    for (int colIdx=0; colIdx<columnList.size(); colIdx++) {
      ((Column)columnList.get(colIdx)).startIdx = startIdx;
      startIdx = startIdx + ((Column)columnList.get(colIdx)).uniqCnt + 1; // one extra item for value NA
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
      while (colIdx < columnList.size()) {
        String colName = ((Column)columnList.get(colIdx)).columnName;
        String valueQry = new String("select distinct " + colName + " from ");
        if (((Column)columnList.get(colIdx)).dataType.equals("NUMBER"))
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
    catch (Exception e){
      JOptionPane.showMessageDialog(msgBoard,
                e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
      System.out.println("Error occoured in initBaseHeadTbl.");
    }
  }

  /** Build a Htree.
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
      int itemIdx;
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
          // tableSet's data starts from index 1
          aValue = tableSet.getString(colIdx+1);
          if (aValue==null) { // it is null, replace it with value of NA
            aValue = NA;
            System.out.println("null value is converted to " + NA);
          }
          else if (aValue.equals(" ") || aValue.equals("")) { // it is a blank string, replace it with value of NA
            aValue = NA;
            System.out.println("blank value is converted to " + NA);
          }
          // Searching baseHeadTble to find the record with this value.
          // To speed up the search, first find the starting index and ending index
          // in baseHeadTbl for this column.
          startIdx = ((Column)columnList.get(colIdx)).startIdx;
          endIdx = startIdx + ((Column)columnList.get(colIdx)).uniqCnt; // don't subtract 1, because need to add one extra item for NA
          itemIdx = getIndex(startIdx, endIdx, aValue);
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

  protected int getIndex(int first, int last, String val) {
    // use binary search to find the index of the value
    while ((last - first) > 1) {
      if (val.compareTo(((Head)baseHeadTbl.get((first+last)/2)).value) > 0) {
          first = ((first+last)/2);
      }
      else if (val.compareTo(((Head)baseHeadTbl.get((first+last)/2)).value) < 0) {
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
                "Can't find the item in the baseHeadTbl", "Error",
                JOptionPane.ERROR_MESSAGE);
        System.out.println("Error occoured in getIndex.");
        return (-1); // not found the item
    }
  }

  /** update baseHeadTbl and insert a new node into hTree */
  protected int updLinks(int hdIdx, int pIdx) {
    int treeIdx = 0;
    int firstNodeIdx;
    boolean isFirst;
    int hdCnt = ((Head)baseHeadTbl.get(hdIdx)).valueCnt+1;
    ((Head)baseHeadTbl.get(hdIdx)).valueCnt = hdCnt;
    // If the index of first-node = -1, then create a new record in hTree,
    // and update the index of first-node and the index of current-node.
    firstNodeIdx = ((Head)baseHeadTbl.get(hdIdx)).firstNode;
    treeIdx = hTree.size();
    if (firstNodeIdx == -1) {
      createNode(hdIdx, pIdx);
      ((Head)baseHeadTbl.get(hdIdx)).firstNode = treeIdx; // index for first-node
      ((Head)baseHeadTbl.get(hdIdx)).currNode = treeIdx; // index for current-node
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
        if (((Node)hTree.get(firstNodeIdx)).parentIdx == pIdx){
          int nodeCnt = ((Node)hTree.get(firstNodeIdx)).nodeCnt+1;
          ((Node)hTree.get(firstNodeIdx)).nodeCnt = nodeCnt;
          return(firstNodeIdx);
        }
        else { // parent is different, follow same-value-node to search
          firstNodeIdx = ((Node)hTree.get(firstNodeIdx)).sameValueIdx;
        }
      }
      // no same parent found
      if (firstNodeIdx == -1) {
        createNode(hdIdx, pIdx);
        // update index of current node in baseHeadTbl
        ((Head)baseHeadTbl.get(hdIdx)).currNode = treeIdx;
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

  /**  update the first child index. 3 parameters: hTree index, baseHeadTbl index, parent's hTree index) */
  protected void updFirstChild(int treeIdx, int parentIdx) {
    int firstChildIdx;
    int currNodeIdx;
    /* update index of first-child in hTree */
    // find the firstChildIdx from hTree for the current parent node
    firstChildIdx = ((Node)hTree.get(parentIdx)).firstChildIdx;
    // if parent has no first-child, update the index of first-child for the parent node
    if (firstChildIdx == -1) {
      ((Node)hTree.get(parentIdx)).firstChildIdx = treeIdx;
    }
  }

  /** Find the first node in baseHeadTbl, then follow the same value link in
  * hTree to update the Same Value index for the immediate previous node of same value
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

  /** Create a new node in HTree
   *
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
            tableQry = tableQry + columnResult.getString(1) + " VARCHAR2(11)";
          }
          else if (columnResult.getString(2).equals("DATE")) {
            tableQry = tableQry + columnResult.getString(1) + " VARCHAR2(11)";
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
    int columnOrder;
    String insertStr = NOTHING;

    for (int headIdx=baseHeadTbl.size()-1; headIdx >= 0; headIdx--) {
      columnOrder = ((Head)baseHeadTbl.get(headIdx)).columnOrder;
      if (columnOrder < 0) break;
      if (((Head)baseHeadTbl.get(headIdx)).valueCnt >= cutOff) {
        insertStr = "insert into " + cubeTableName + " (" +
                  ((Column)columnList.get(columnOrder)).columnName + ", set_size, cnt)";
        insertStr = insertStr + " values ('" + ((Head)baseHeadTbl.get(headIdx)).value + "', 1, ";
        insertStr = insertStr + ((Head)baseHeadTbl.get(headIdx)).valueCnt + ")";

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

  /** Create an ArrayList to held all local header tables
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

  /** Computer every node in the HTree, starting from level 2 to max level
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
          // param1: index in baseHeadTbl.
          // param2: index of the column, also same as the level of hTree
          // param3: which localHeadTbl to go. 0 for 2-item set , 1 for 3-item set, ..., etc
          computeItemSet(headIdx, colIdx, 0);
        }
        headIdx ++;
      }
    }
  }

  /** compute the localHeadTbl against a value in the level with hTreeLevel.
   *  The hTreeLevel is equivalent to the column index, because one column goes to one level in the tree.
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
        int startIdx = ((Column)columnList.get(colIdx)).startIdx;
        int endIdx = startIdx + ((Column)columnList.get(colIdx)).uniqCnt + 1;
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

  /** reset counts in HTree for the nodes they are in columns < columnIdx
   */
  protected void resetHTreeCnt(int columnIdx) {
    int headIdx;
    for (int nodeIdx = 0; nodeIdx < hTree.size(); nodeIdx++) {
      headIdx = ((Node)hTree.get(nodeIdx)).headIdx;
      if (((Head)baseHeadTbl.get(headIdx)).columnOrder < columnIdx) {
        ((Node)hTree.get(nodeIdx)).nodeCnt = 0;
      }
    }
  }

  /** Initialized the local header table
   */
  protected void initLocalHeadTbl (int localTblIdx) {
    ArrayList aLocalTbl = (ArrayList)localHeadTbls.get(localTblIdx);
    // wipe out the previous data
    for (int itemIdx = 0; itemIdx < aLocalTbl.size(); itemIdx++) {
      ((LocalHead)aLocalTbl.get(itemIdx)).value = NOTHING;
      ((LocalHead)aLocalTbl.get(itemIdx)).headIdx = -1;
      ((LocalHead)aLocalTbl.get(itemIdx)).valueCnt = 0;
      ((LocalHead)aLocalTbl.get(itemIdx)).columnOrder = -1;
    }
    // find the smallest columnIndex in prefixList
    int minCol = 1000;
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
    int newHead = 1;  // baseHeadTbl[0] is for root
    int itemIdx = 0;
    while (newHead < startIdx) {
      String value = ((Head)baseHeadTbl.get(newHead)).value;
      int columnOrder = ((Head)baseHeadTbl.get(newHead)).columnOrder;
      ((LocalHead)aLocalTbl.get(itemIdx)).value = value;
      ((LocalHead)aLocalTbl.get(itemIdx)).headIdx = newHead;
      ((LocalHead)aLocalTbl.get(itemIdx)).columnOrder = columnOrder;
      newHead++;
      itemIdx++;
    }
    //printLocalHeadTbl(aLocalTbl);  // for debugging
  }

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
      newHeadIdx = ((LocalHead)localHeads.get(localIdx)).headIdx;
      if (newHeadIdx == -1) {
        break; // reach the end
      }
      if (((LocalHead)localHeads.get(localIdx)).valueCnt >= cutOff) {
        newColIdx = ((Head)baseHeadTbl.get(newHeadIdx)).columnOrder;
        newColName = ((Column)columnList.get(newColIdx)).columnName;
        queryStr = insertStr1 + ", " + newColName + ", set_size, cnt) " + insertStr2 + ", '" +
                 ((LocalHead)localHeads.get(localIdx)).value + "', " +
                 (localTblsIdx + 2) + ", " +     //localTbls[0] for 2-item set, [1] for 3-item set
                 ((LocalHead)localHeads.get(localIdx)).valueCnt + ")";
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
    System.out.println("Htree: ");
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
