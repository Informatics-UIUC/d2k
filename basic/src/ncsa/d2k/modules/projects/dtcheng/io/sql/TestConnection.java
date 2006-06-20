package ncsa.d2k.modules.projects.dtcheng.io.sql;

import java.sql.*;
import java.util.*;

import ncsa.d2k.core.modules.*;




public class TestConnection extends ComputeModule {
  
  public String getModuleInfo() {
    String s = "<p> Overview: ";
    s += "This module tests a connection to a database. </p>";
    
    return s;
    
  }
  
  public String getModuleName() {
    
    return "TestConnection";
    
  }
  
  public String[] getInputTypes() {
    
    String[] out = {};
    
    return out;
  }
  
  
  public String[] getOutputTypes() {
    
    String[] out = {"ncsa.d2k.modules.core.io.sql.DBConnection" };
    
    return out;
    
  }
  
  
  
  public String getInputInfo(int i) {
    switch(i) {
      
      
      default:
        
        return "No such output!";
        
    }
  }
  
  
  
  public String getInputName(int i) {
    
    switch(i) {
      default:
        
        return "No such intput!";
        
    }
    
  }
  
  
  
  public String getOutputInfo(int i) {
    
    switch(i) {
      
      case(0):
        
        return "A connection to the database.";
        
      default:
        
        return "No such output!";
        
    }
    
  }
  
  
  
  public String getOutputName(int i) {
    
    switch(i) {
      
      case(0):
        
        return "Database Connection";
        
      default:
        
        return "No such output!";
        
    }
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
  
  
  
  
  int numInputFeatures = 0;
  
  
  public void doit() throws Exception {
    
    
    int exampleIndex = 0;
    
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
    query = "SELECT *  FROM dm_coarse_day";
    
    System.out.println("issuing query: " + query);
    
    
    Statement statement = connection.createStatement();
    ResultSet resultSet   = statement.executeQuery(query);
    
    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
    
    int numColumns = resultSetMetaData.getColumnCount();
    
    
    // print feature names
    for (int i = 0; i < numColumns; i++) {
      if (i != 0)
        System.out.print(",");
      System.out.print(resultSetMetaData.getColumnName(i+ 1));
    }
    System.out.println();
    
    // print feature types
    for (int i = 0; i < numColumns; i++) {
      if (i != 0)
        System.out.print(",");
      if (i == 1)
        System.out.print("String");
      else
        System.out.print("Double");
    }
    System.out.println();
    
    while (resultSet.next()) {
      for (int i = 0; i < numColumns; i++) {
        String string = resultSet.getString(i + 1);
        
        System.out.print(string);
        if (i != numColumns - 1)
          System.out.print(",");
      }
      System.out.println();
      
      exampleIndex++;
    }
    statement.close();
    
    System.out.println("exampleIndex = " + exampleIndex);
    
  }
  
}






/* all features:
PATIENT_ID
DAY
SZ_RATE
BASE_SZ_RATE
OBS_EXP_RATE_DIFF
RX_RATE
DET_RATE
LOCKOUT_RATE
NUM_RX_ENABLED
T1B1_ENABLED
T1B2_ENABLED
T1B1_PW
T1B1_AMP_CAT
T1B1_AMP_AN
T1B1_CNT
T1B1_DUR
T1B1_FREQ
T1B2_PW
T1B2_CHG
T1B2_AMP_CAT
T1B2_AMP_AN
T1B2_CNT
T1B2_DUR
T1B2_FREQ
FOC_HEMI_LEFT
FOC_HEMI_RIGHT
FOC_HEMI_BILATERAL
FOC_HEMI_INTER
FOC_GEN_TEMP
FOC_GEN_FRONT
FOC_GEN_PARI
FOC_GEN_OCCIP
FOC_GEN_HIPPO
FOC_GEN_INTER
FOC_GEN_PARASAG
FOC_GEN_SUBTEMP
NUM_FOCI
LEAD_HEMI_LEFT
LEAD_HEMI_RIGHT
LEAD_HEMI_BILATERAL
LEAD_HEMI_INTER
LEAD_GEN_TEMP
LEAD_GEN_FRONT
LEAD_GEN_PARI
LEAD_GEN_OCCIP
LEAD_GEN_HIPPO
LEAD_GEN_INTER
LEAD_GEN_PARASAG
LEAD_GEN_SUBTEMP
NUM_LEADS
SURGERY
 */