package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.infrastructure.modules.*;
import java.sql.*;


/**
   TrainDataTable.java   
*/
public class TrainDataTable extends ncsa.d2k.infrastructure.modules.InputModule {
    
    /**
       This method returns the description of the various inputs.
       @return the description of the indexed input.
    */
    public String getInputInfo(int index) {
	
	switch (index) {
	case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Connection Wrapper\">    <Text>This is the data source </Text>  </Info></D2K>";
	case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Selected Fields\">    <Text>Selected fields from the table </Text>  </Info></D2K>";
	case 2: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Selected Fields\">    <Text>Selected table from the database that contains the train data </Text>  </Info></D2K>";
	case 3: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Fields with Delimiters\">    <Text>Selected fields with its delimiters. </Text>  </Info></D2K>";
	default: return "No such input";
	}
	
    }
    
    /**
       This method returns an array of strings that contains the data types for the inputs.
       @return the data types of all inputs.
    */
    public String[] getInputTypes () {
	
	String [] types =  {
	    "ncsa.d2k.modules.sql.ConnectionWrapper", //The data source, connection wrapper
	    "[Ljava.lang.String;",                    //selected fields
	    "java.lang.String",                       //selected table
	    "[[Ljava.lang.String;",                   // Fields with delimiters
	}; 
	return types;
	
    }
    
    /**
       This method returns the description of the outputs.
       @return the description of the indexed output.
    */
    public String getOutputInfo (int index) {
	
	switch (index) {
	case 0: return  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Number of Rows\">    <Text>This is the number of rows in the train data.  </Text>  </Info></D2K>";
	case 1: return  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Number of Columns\">    <Text>This is the number of columns in the train data.  </Text>  </Info></D2K>";
	case 2: return  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Number of Classes\">    <Text>This is the number of classes in the train data.  </Text>  </Info></D2K>";
	case 3: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Result Set\">    <Text>This Result Set contains the train data itself. </Text>  </Info></D2K>";
	case 4: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Fields with Delimiters\">    <Text>Selected fields with its corresponding delimiters. </Text>  </Info></D2K>";
	default: return "No such output";
	    
	}
    }	
    
    /**
       This method returns an array of strings that contains the data types for the outputs.
       @return the data types of all outputs.
    */
    public String[] getOutputTypes () {	    

	String [] types =  {
	    "java.lang.Integer", // the number of rows in the train data
	    "java.lang.Integer", // the number of columns in the train data
	    "java.lang.Integer", // the number of classes in the train data
	    "java.sql.ResultSet", // train data itself 
	    "[[Ljava.lang.String;" // Fields and corresponding delimiters
	};
	return types;

    }
    
    /**
       This method returns the description of the module.
       @return the description of the module.
    */
    public String getModuleInfo () {
	
	return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"\">    <Text>This will obtain the train data from the database in a form of a result set. It will also return the number of rows, columns, and classes there are in the train data.  </Text>  </Info></D2K>";
	
    }
    
    /**
       PUT YOUR CODE HERE.
    */
    public void doit () throws Exception {

	System.out.println("TRAIN DATA TABLE");
	
	ConnectionWrapper connwrap = (ConnectionWrapper) this.pullInput(0);
	Connection conn = connwrap.getConnection();
	String[] fieldArray = (String[]) this.pullInput(1);
	String[][] temp = (String[][]) this.pullInput(3);
	//Object temp = this.pullInput(3);
	//System.out.println(temp.toString());
	//String[] temp1 = (String[])temp;

	StringBuffer fieldList = new StringBuffer(fieldArray[0]);
	
	for(int i=1; i<fieldArray.length; i++)
	    fieldList.append(", " + fieldArray[i]);
	//System.out.println("fieldList: " + fieldList);

	String tableList = (String) this.pullInput(2);
	

	/* resultset of query will be used to obtain the number of classes in the last field of the table */
	// fieldArray.length -- number of selected fields 
	int lastfield = fieldArray.length-1;
        String query = " SELECT " + fieldArray[lastfield].toString()+ " FROM " + tableList;
	System.out.println("Query :" + query);
	
	/* resultset of query1 will be training data itself */
	// Obtain all the selected fields from table
	String query1 = "SELECT " + fieldList.toString()+" FROM " + tableList;
	System.out.println("Query 1: " + query1);


	String metaquery = "SELECT " + fieldList.toString() +  " FROM " + tableList;
 
	// Get the number of rows
	// Assume that the number of rows for each field is the same
	// fieldArray[0] -- the first selected field
	String query2 = "SELECT COUNT (" + fieldArray[0].toString() + ") FROM " + tableList;
	System.out.println("Query 2: " + query2);

	Statement stmt = conn.createStatement();

	//	ResultSet rset = stmt.executeQuery(query1);  // training data

	/*while (rset.next()) {
	    for (int i=1; i <= 5; i++) {
		Object obj = rset.getObject(i);
		System.out.println(obj.toString());
	    }
	    }*/
	
	ResultSet metarset = stmt.executeQuery(metaquery);
       	ResultSetMetaData meta = metarset.getMetaData();

	Integer columns = new Integer(meta.getColumnCount());
	System.out.println("# of columns: " + columns);

 	ResultSet rset2 = stmt.executeQuery(query2);
	rset2.next(); 
	Integer rows = new Integer (rset2.getInt(1));
	System.out.println("# of rows: " + rows);
		
	// Number of Classes
	int num_of_classes = 0;
	String stringtemp = null;
	String newtemp = null;

	ResultSet rset3 = stmt.executeQuery(query);
	
	rset3.next();
	Object obj = rset3.getObject(1);
	stringtemp = obj.toString();
	num_of_classes++;

	while(rset3.next()) {
	    Object obj2 = rset3.getObject(1);
	    //System.out.println(obj.toString());
	    newtemp = obj2.toString();
	    //System.out.println(obj2.toString());

	    if((stringtemp.compareTo(newtemp)) != 0 ) {
		num_of_classes++;
		stringtemp = newtemp;
	    }
	}
	
	System.out.println("Number of classes in train data: " + num_of_classes);
	Integer classes = new Integer (num_of_classes);

	ResultSet rset = stmt.executeQuery(query1);  // training data

	this.pushOutput(rows,0);
	this.pushOutput(columns,1);
	this.pushOutput(classes,2);
	this.pushOutput(rset,3);
	this.pushOutput(temp,4);
    }
    
} /* TestDataTable */

    
    
    
