package ncsa.d2k.modules.core.io.file.input;


import java.io.*;
import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.ADTree;

/**
        Reads a delimited format file and creates an ADTree 
	@author Anca Suvaiala
*/
public class ReadDelimited2ADT extends ReadDelimitedFormat {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Reads a delimited format file and creates an ADTree  </body></html>";
	}
    
    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleName() {
		return "ReadDelimited2ADT";
	}
    
    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"java.lang.String"};
		return types;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.ADTree","ncsa.d2k.core.datatype.table.ExampleTable"};
		return types;
	}
    
    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "The name of the file to read.";
			default: return "No such input";
		}
	}
    
    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The ADTree";
			case 1: return "ExampleTable containing metadata";
			default: return "No such output";
		}
	}
    
    /**
       Return the name of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputName(int i) {
		switch(i) {
			case 0:
				return "fileName";
			default: return "NO SUCH INPUT!";
		}
	}
    
    
    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "TheADTree";
			case 1:
				return "ExampleTable";
			default: return "NO SUCH OUTPUT!";
		}
	}
    
    int numCols=0 ;
    int numRows =0;
    /**
       Create the table.
    */
    public void doit() throws Exception {
	// get our delimiter set
	String fileName = (String)pullInput(0);
	File file = new File (fileName);
	delimiterOne = super.findDelimiter (file);
	if (delimiterOne == (byte) '=') {
	    throw new Exception ("No single character delimiter could be identified.");
	}
	
	if(typesRow >= 0)
	    hasTypes = true;
	if(labelsRow >= 0)
	    hasLabels = true;
	
	if(file.exists()) {
	    pushOutput(readSDFile2ADT(file), 0);
	    pushOutput(getMetadata(),1);
	}
	
	else
	    System.out.println("File did not exist.");
	
	
    }
    
    /**
       Read a file and create an ADTree from the file.  Returns null
       if any errors occur.
       @param f the File to read
       @return an ADTree  containing summary counts from the file, or null
       if any errors occur
    */
    private ADTree readSDFile2ADT(File f) {
	
	ArrayList rowPtrs = new ArrayList();
	int currentRow = 0;
	ADTree adt;
	try {
	    BufferedReader reader = new BufferedReader(new FileReader(f));
	    String line;
	    
	    // read the file in one row at a time

	    line = reader.readLine();
	    ArrayList thisRow = createSDRow(line);
	    int len = thisRow.size();
	    numCols = len;
	    adt = new ADTree(len);
	    while( line  != null) {
		thisRow = createSDRow(line);
		len = thisRow.size();
		if(currentRow == typesRow) {
		    typesList = thisRow;
		    
		} else if(currentRow == labelsRow) {
		    labelsList = thisRow;
		    for (int i =0; i < len; i++) {
			adt.setLabel(i+1,new String((char[])thisRow.get(i)));
		    }
		} else
		    adt.countLine(adt,thisRow);
		
		currentRow++;
		line = reader.readLine();
	    }
	}
	catch(IOException e) {
	    e.printStackTrace();
	    return null;
	}
	numRows = currentRow-1;
	return adt;
	}

    
    
    /*
      returns an ExampleTable that has no data, only information
      about the columns
    */
    
    public ExampleTable getMetadata() {
	

  	// now create the table.
  	Column[] cols = new Column[numCols];
  	for(int i = 0; i < cols.length; i++) {
  	    if(typesList != null) {
  		String type = new String((char[])typesList.get(i));
  		cols[i] = createColumn(type, numRows);
  	    }
  	    else
  		cols[i] = new StringColumn(numRows);
	    
  	    if(labelsList != null)
  		cols[i].setLabel(new String((char[])labelsList.get(i)));
  	    else
  		cols[i].setLabel(Integer.toString(i));
  	}
	
	DefaultTableFactory  dtf = DefaultTableFactory.getInstance();  
	Table table = dtf.createTable(cols);
  	return dtf.createExampleTable(table);
	
    }




}



