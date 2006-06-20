package ncsa.d2k.modules.projects.dtcheng.io.sql;

import java.sql.*;
import java.util.*;

import ncsa.d2k.modules.projects.dtcheng.datatype.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.examples.ContinuousDoubleExampleTable;




public class ExtractExampleTableFromDB extends ComputeModule {
  
  
  
  private String TableName = "dm_coarse_day";
  
  public void setTableName(String value) {
    this.TableName = value;
  }
  
  public String getTableName() {
    return this.TableName;
  }
  
  
  
  
  public String getModuleName() {
    
    return "ExtractExampleTableFromDB";
    
  }
  
  public String getModuleInfo() {
    String s = "<p> Overview: ";
    s += "This module tests a connection to a database. </p>";
    
    return s;
    
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Input Feature Name Table";
      case 1:
        return "Output Feature Name Table";
      case 2:
        return "Group Feature Name Table";
      default:
        return "No such input!";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Input Feature Name Table";
      case 1:
        return "Output Feature Name Table";
      case 2:
        return "Group Feature Name Table";
      default:
        return "No such input!";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
      "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
      return types;
  }
  
  
  
  public String getOutputName(int i) {
    
    switch(i) {
      
      case 0:
        
        return "Example Table";
        
      default:
        
        return "No such output!";
        
    }
  }
  
  
  
  public String getOutputInfo(int i) {
    
    switch(i) {
      
      case 0:
        
        return "Example Table";
        
      default:
        
        return "No such output!";
        
    }
    
  }
  
  
  public String[] getOutputTypes() {
    
    String[] out = {"ncsa.d2k.modules.core.datatype.table.ExampleTable" };
    
    return out;
    
  }
  
  
  
  
  
  
  
  
  public Connection getConnection
  (String url, String driver, String username, String password) {
    
    Connection connection = null;
    
    try {
      System.out.println("Entering getConnection");
      if (connection == null) {
        Class classNm = Class.forName(driver);
        DriverManager.registerDriver((Driver) Class.forName(driver).newInstance());
        
        // make the connection
        if (username == null) {
          connection = DriverManager.getConnection( url );
        } else {
          System.out.println("URL is "+url);
          connection = DriverManager.getConnection( url, username, password );
        }
      }
      System.out.println("Successful Connection");
      return connection;
    } catch (Exception e){
      System.out.println(
      "Cannot connect to the database. Please check the user " +
      "name, password, server machine, " +
      "and database instance you have entered.");
      System.out.println("Error occurred when connecting to a database.");
      return null;
    }
  }
  
  
  
  
  
  
  public void doit() throws Exception {
    
    
    ncsa.d2k.modules.core.datatype.table.Table inputFeatureTable  = (ncsa.d2k.modules.core.datatype.table.Table) this.pullInput(0);
    ncsa.d2k.modules.core.datatype.table.Table outputFeatureTable = (ncsa.d2k.modules.core.datatype.table.Table) this.pullInput(1);
    ncsa.d2k.modules.core.datatype.table.Table groupFeatureTable = (ncsa.d2k.modules.core.datatype.table.Table) this.pullInput(2);
    
    
    String username   = "research_tools";
    String port       = "1521";
    String machine    = "research-data.neuropace.com";
    String dbInstance = "research";
    String password   = "whatever";
    String driver     = "oracle.jdbc.driver.OracleDriver";
    String url        = "jdbc:oracle:thin:@" + machine + ":" + port + ":" + dbInstance;
    
    
    System.out.println("creating connection");
    
    
    Connection connection = getConnection(url,  driver,  username,  password);
    
    
    String resultStrings[];
    Vector vector = new Vector();
    
    String query;
    query = "SELECT *  FROM " + TableName;
    
    System.out.println("issuing query: " + query);
    
    
    Statement statement = connection.createStatement();
    ResultSet resultSet   = statement.executeQuery(query);
    
    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
    
    // count number of rows
    int exampleIndex = 0;
    while (resultSet.next()) {
      exampleIndex++;
    }
    statement.close();
    
    
    
    statement = connection.createStatement();
    resultSet   = statement.executeQuery(query);
    
    resultSetMetaData = resultSet.getMetaData();
    
    
    int NumExamples = exampleIndex;
    
    
    System.out.println("NumExamples = " + NumExamples);
    
    
    
    int numColumns = resultSetMetaData.getColumnCount();
    
    
    int numInputFeaturesRows  = inputFeatureTable.getNumRows();
    int numOutputFeaturesRows = outputFeatureTable.getNumRows();
    int numGroupFeaturesRows = groupFeatureTable.getNumRows();
    
    
//    int inputIndex = 0;
//    int outputIndex = 0;
//    for (int i = 0; i < numInputFeaturesRows; i++) {
//      String name = inputFeatureTable.getString(i, 0);
//        inputIndex++;
//    }
//    for (int i = 0; i < numOutputFeaturesRows; i++) {
//      String name = outputFeatureTable.getString(i, 0);
//        outputIndex++;
//    }
    
    int numInputFeatures = numInputFeaturesRows;
    int numOutputFeatures = numOutputFeaturesRows;
    
    
    
    
    int NumFeatures = numInputFeatures + numOutputFeatures;
    
    double[] data = new double[NumExamples * NumFeatures];
    int[] group = new int[NumExamples];
    
    
    
    String[] inputNames = new String[numInputFeatures];
    String[] outputNames = new String[numOutputFeatures];
    String groupName;
    
    Hashtable inputHashtable = new Hashtable();
    Hashtable outputHashtable = new Hashtable();
    
    int inputIndex = 0;
    int outputIndex = 0;
    for (int i = 0; i < numInputFeaturesRows; i++) {
      String name = inputFeatureTable.getString(i, 0);
      inputNames[inputIndex] = name;
      inputHashtable.put(name, new Integer(inputIndex));
      inputIndex++;
    }
    for (int i = 0; i < numOutputFeaturesRows; i++) {
      String name = outputFeatureTable.getString(i, 0);
      outputNames[outputIndex] = name;
      outputHashtable.put(name, new Integer(outputIndex));
      outputIndex++;
    }
    groupName = groupFeatureTable.getString(0, 0);
    
    
    int[] inputColumnIndices = new int[numInputFeatures];
    int[] outputColumnIndices = new int[numOutputFeatures];
    int   groupColumnIndex = -1;
    
    inputIndex = 0;
    outputIndex = 0;
    
    
    System.out.println();
    System.out.println("column names:");
    // print feature names
    for (int i = 0; i < numColumns; i++) {
      String string = resultSetMetaData.getColumnName(i+ 1);
      
      System.out.println(string);
      
      Integer integerInputIndex = (Integer) inputHashtable.get(string);
      if (integerInputIndex != null) {
        inputColumnIndices[inputIndex] = i;
        inputIndex++;
      }
      Integer integerOutputIndex = (Integer) outputHashtable.get(string);
      
      if (integerOutputIndex != null) {
        outputColumnIndices[outputIndex] = i;
        outputIndex++;
      }
      
      
      if (string.equalsIgnoreCase(groupName)) {
        groupColumnIndex = i;
      }
      
    }
    System.out.println();
    
    ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(data, group, NumExamples, numInputFeatures, numOutputFeatures, inputNames, outputNames);
    
    
    
    // print feature types
    double [] rowValues = new double[numColumns];
    boolean [] parseFeature = new boolean[numColumns];
    
    for (int i = 0; i < numInputFeatures; i++) {
      parseFeature[inputColumnIndices[i]] = true;
    }
    for (int i = 0; i < numOutputFeatures; i++) {
      parseFeature[outputColumnIndices[i]] = true;
    }
    parseFeature[groupColumnIndex] = true;
    
    
    
    Hashtable ht = new Hashtable();
    int groupIndex = 0;
    
    
    exampleIndex = 0;
    int numGroups = 0;
    while (resultSet.next()) {
      
      // read row values into a buffer
      for (int i = 0; i < numColumns; i++) {
        if (parseFeature[i]) {
          String string = resultSet.getString(i + 1);
          try {
            rowValues[i] = Double.parseDouble(string);
          } catch (Exception e) {};
        }
      }
      
      for (int i = 0; i < numInputFeatures; i++) {
        exampleSet.setInput(exampleIndex, i, rowValues[inputColumnIndices[i]]);
      }
      for (int i = 0; i < numOutputFeatures; i++) {
        exampleSet.setOutput(exampleIndex, i, rowValues[outputColumnIndices[i]]);
      }
      
      String groupString = resultSet.getString(groupColumnIndex + 1);
      
      
      if (ht.containsKey(groupString)) {
        groupIndex = ((int []) ht.get(groupString))[0];
      } else {
        groupIndex = numGroups;
        int [] index ={numGroups++};
        ht.put(groupString, index);
      }
      
      exampleSet.setGroup(exampleIndex, groupIndex);
      
      exampleIndex++;
    }
    statement.close();
    
    exampleSet.setNumGroups(numGroups);
    
    
    
    this.pushOutput((ExampleTable) exampleSet, 0);
    
  }
  
}
