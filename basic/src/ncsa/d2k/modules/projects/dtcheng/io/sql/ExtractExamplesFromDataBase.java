package ncsa.d2k.modules.projects.dtcheng.io.sql;

import java.sql.*;
import java.util.*;

import ncsa.d2k.modules.projects.dtcheng.datatype.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.examples.ContinuousDoubleExampleTable;




public class ExtractExamplesFromDataBase extends ComputeModule {
  
  
  public String getModuleName() {
    
    return "ExtractExamplesFromDataBase";
    
  }
  
  public String getModuleInfo() {
    String s = "<p> Overview: ";
    s += "This module tests a connection to a database. </p>";
    
    return s;
    
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Data Table Name";
      case 1:
        return "Feature Table Name";
      case 2:
        return "Feature Role Column Name";
      case 3:
        return "Where Clause";
      default:
        return "No such input!";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Data Table Name";
      case 1:
        return "Feature Table Name";
      case 2:
        return "Feature Role Column Name";
      case 3:
        return "Where Clause";
      default:
        return "No such input!";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = {
      "java.lang.String",
      "java.lang.String",
      "java.lang.String",
      "java.lang.String"};
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
    
    
    String dataTableName         = (String) this.pullInput(0);
    String featureTableName      = (String) this.pullInput(1);
    String featureRoleColumnName = (String) this.pullInput(2);
    String whereClause           = (String) this.pullInput(3);
    
    
    String username   = "research_tools";
    String port       = "1521";
    String machine    = "research-data.neuropace.com";
    String dbInstance = "research";
    String password   = "whatever";
    String driver     = "oracle.jdbc.driver.OracleDriver";
    String url        = "jdbc:oracle:thin:@" + machine + ":" + port + ":" + dbInstance;
    
    
    System.out.println("creating connection to research DB");
    
    /////////////////////////////////////////////
    // establish connection to the research DB //
    /////////////////////////////////////////////
    
    
    Connection connection = getConnection(url,  driver,  username,  password);
    
    
    
    
    /////////////////////////////////////////////////////////////////////////////
    // define input, output, and group features from the feature control table //
    /////////////////////////////////////////////////////////////////////////////
    
    
    
    int numInputFeatures  = 0;
    int numOutputFeatures = 0;
    int numGroupFeatures  = 0;
    int NumFeatures;
    
    
    Hashtable inputHashtable;
    Hashtable outputHashtable;
    
    
    String[] inputNames = null;
    String[] outputNames = null;
    String groupName = null;
    
    
    String featureResultStrings[];
    Vector featureVector = new Vector();
    
    String featureTableQuery = "SELECT *  FROM " + featureTableName;
    
    System.out.println("issuing feature table query: " + featureTableQuery);
    Statement statement = connection.createStatement();
    ResultSet featureResultSet = statement.executeQuery(featureTableQuery);
    
    ResultSetMetaData featureResultSetMetaData = featureResultSet.getMetaData();
//
//    // count number of rows
//    int featureIndex = 0;
//    while (featureResultSet.next()) {
//      featureIndex++;
//    }
//    statement.close();
//
//
    int numFeatureColumns = featureResultSetMetaData.getColumnCount();
    int featureRoleColumnIndex = -1;
    
    
    
    System.out.println("column names in feature role table:");
    // print feature names
    for (int i = 0; i < numFeatureColumns; i++) {
      String string = featureResultSetMetaData.getColumnName(i+ 1);
      
      System.out.println(string);
      
      if (string.equalsIgnoreCase(featureRoleColumnName)) {
        featureRoleColumnIndex = i;
      }
      
    }
    System.out.println();
    
    
    
    
    
    
    
    
    
//
//    System.out.println("featureIndex = " + featureIndex);
    
    
    //////////////////////////////////////////
    // #1 pass through feature control file //
    //////////////////////////////////////////
    
    featureResultSet = statement.executeQuery(featureTableQuery);
    
    
    while (featureResultSet.next()) {
      
      String name   = featureResultSet.getString(1);
      String role = featureResultSet.getString(featureRoleColumnIndex + 1);
      
      System.out.println("name = " + name);
      System.out.println("role = " + role);
      
      if (role.equalsIgnoreCase("I")) {
        numInputFeatures++;
        continue;
      }
      if (role.equalsIgnoreCase("O")) {
        numOutputFeatures++;
        continue;
      } else
        if (role.equalsIgnoreCase("G")) {
        numGroupFeatures++;
        continue;
        }
      if (role.equalsIgnoreCase("NA")) {
        continue;
      }
      System.out.println("error, feature = "+ name +  "  role = " + role + "  Role code invalid");
    }
    if (numGroupFeatures > 1) {
      
      System.out.println("numGroupFeatures > 1");
    }
    
    
    NumFeatures = numInputFeatures + numOutputFeatures;
    
    System.out.println("numInputFeatures  = " + numInputFeatures);
    System.out.println("numOutputFeatures = " + numOutputFeatures);
    System.out.println("numGroupFeatures  = " + numGroupFeatures);
    
    
    
    
    //////////////////////////////////////////
    // #2 pass through feature control file //
    //////////////////////////////////////////
    
    inputHashtable = new Hashtable();
    outputHashtable = new Hashtable();
    inputNames  = new String[numInputFeatures]; 
    outputNames = new String[numOutputFeatures]; 
    
    featureResultSet = statement.executeQuery(featureTableQuery);
    
    int inputIndex  = 0;
    int outputIndex = 0;
    while (featureResultSet.next()) {
      
      String name   = featureResultSet.getString(1);
      String role = featureResultSet.getString(featureRoleColumnIndex + 1);
      
      System.out.println("name = " + name);
      System.out.println("role = " + role);
      
      if (role.equalsIgnoreCase("I")) {
        inputNames[inputIndex] = name;
        inputHashtable.put(name, new Integer(inputIndex));
        inputIndex++;
      }
      if (role.equalsIgnoreCase("O")) {
        outputNames[outputIndex] = name;
        outputHashtable.put(name, new Integer(outputIndex));
        outputIndex++;
      }
      if (role.equalsIgnoreCase("G")) {
        groupName = name;
      }
    }
    statement.close();
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    System.out.println("creating connection to data table");
    
    
    String resultStrings[];
    Vector vector = new Vector();
    
    String dataTableQuery    = null;
    
    
    
    
    
    if (whereClause.length() == 0)
     dataTableQuery = "SELECT *  FROM " + dataTableName;
    else
     dataTableQuery = "SELECT *  FROM " + dataTableName + " WHERE " + whereClause;
    
    
    System.out.println("issuing data table query: " + dataTableQuery);
    Statement dataStatement = connection.createStatement();
    ResultSet dataResultSet = dataStatement.executeQuery(dataTableQuery);
    
    ResultSetMetaData dataResultSetMetaData = dataResultSet.getMetaData();
    
    // count number of rows
    int exampleIndex = 0;
    while (dataResultSet.next()) {
      exampleIndex++;
    }
    statement.close();
    
    
    int numDataColumns = dataResultSetMetaData.getColumnCount();
    
    
    int[] inputColumnIndices = new int[numInputFeatures];
    int[] outputColumnIndices = new int[numOutputFeatures];
    int   groupColumnIndex = -1;
    
    inputIndex = 0;
    outputIndex = 0;
    
    
    System.out.println();
    System.out.println("column names in data table:");
    // print feature names
    for (int i = 0; i < numDataColumns; i++) {
      String string = dataResultSetMetaData.getColumnName(i+ 1);
      
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    statement = connection.createStatement();
    dataResultSet   = statement.executeQuery(dataTableQuery);
    
    dataResultSetMetaData = dataResultSet.getMetaData();
    
    
    int NumExamples = exampleIndex;
    
    
    System.out.println("NumExamples = " + NumExamples);
    
    double[] data = new double[NumExamples * NumFeatures];
    int[] group = new int[NumExamples];
    
    
    
    ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(data, group, NumExamples, numInputFeatures, numOutputFeatures, inputNames, outputNames);
    
    
    
    // print feature types
    double [] rowValues = new double[numDataColumns];
    boolean [] parseFeature = new boolean[numDataColumns];
    
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
    while (dataResultSet.next()) {
      
      // read row values into a buffer
      for (int i = 0; i < numDataColumns; i++) {
        if (parseFeature[i]) {
          String string = dataResultSet.getString(i + 1);
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
      
      String groupString = dataResultSet.getString(groupColumnIndex + 1);
      
      
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
