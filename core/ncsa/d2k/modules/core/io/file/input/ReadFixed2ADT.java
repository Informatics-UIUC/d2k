package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.pipes.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.*;
import java.io.*;
import java.util.*;
import ncsa.d2k.modules.core.io.file.*;

/**
 * ReadFixed2VT reads a fixed format file and creates an ADTree.
 * It can also provide metadata in a Table format.
 * The format is specified as follows (position counting starts from 0):
 * <beginingcolumn>-<endcolumn> type  eg. 0-7 int
 * or <column width> type eg. 5 int
 * supported types are: String int float double long short boolean char[] byte[]
 * The fixed format can be specified in the header of the file or in a Vertical
 * Table input which has the following column labels in any order:
 * Label Type Start Stop
 * If the VerticalTable contains a column named Length then it will be used
 * to set the start and stop values overriding the Start Stop columns
 * The fields will have to be ordered and next to each other.
 */

public class ReadFixed2ADT extends ncsa.d2k.infrastructure.modules.InputModule
    implements Serializable
{
    protected boolean _ignoreSyntaxErrors;
    protected String  _comments;
    protected String  _format;
    protected int _typesRow = -1;
    protected int _labelsRow = -1;
    protected double emptyValue = 0;


    /**
       This pair returns the description of the various inputs.
       @return the description of the indexed input.
    */
    public String getInputInfo(int index) {
	switch (index) {
	case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>" +
		    " <Info common=\"_fileName\"> "+
		    "  <Text>input file name </Text>  </Info></D2K>";
	case 1: return "Vertical Table containing type/label data";
	default: return "No such input";
	}

    }

    /**
       This pair returns an array of strings that contains the data types for the inputs.
       @return the data types of all inputs.
    */
    public String[] getInputTypes() {
	String[] types = {"java.lang.String", 
			  "ncsa.d2k.modules.core.datatype.table.Table"};
	return types;

    }

    /**
       This pair returns the description of the outputs.
       @return the description of the indexed output.
    */
    public String getOutputInfo(int index) {
	switch (index) {
	case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"_dataTable\">    <Text>an ADTree  containing summary data from the file </Text>  </Info></D2K>";
	case 1: return " a Table containing metadata about the file";
	case 2: return "Contains row/column info on which fields were blank in"+
		    "the read in file";
	default: return "No such output";
	}

    }

    /**
       This pair returns an array of strings that contains the data types for the outputs.
       @return the data types of all outputs.
    */
    public String[] getOutputTypes() {
	String[] types = {"ncsa.d2k.modules.core.datatype.ADTree",
			  "ncsa.d2k.modules.core.datatype.table.Table",
			  "ncsa.d2k.modules.core.datatype.table.Table"};

	return types;

    }

    /**
       This pair returns the description of the module.
       @return the description of the module.
    */
    public String getModuleInfo() {
	return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>"+
	    "  <Info common=\"\"> " +
	    "  <Text> Implements a line oriented parser with a fixed format. <p>" + " The user can set the format of the data in the columns using the following " +
"  syntax: " +
"  start_column-end_column type " +
"  type can be  int, String , double, float, long, short, boolean, char[], byte[].<p>" +
" </Text>    <Text> </Text>  </Info></D2K>";

    }

    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    /**
       Get the flag for handling of the formating errors .
       @return the value of the flag for handling formating errors
    */
    public boolean getIgnoreSyntaxErrors() {
	return _ignoreSyntaxErrors;
    }

    /**
       Set the flag  for handling of the formating errors .
       @param flag  the value of the flag for handling formating errors
    */
    public void    setIgnoreSyntaxErrors(boolean flag) {
	_ignoreSyntaxErrors = flag;
    }


    /**
       Get the index of the types row.
       @return the index of the types row.
    */
    public int getTypesRow() {
	return _typesRow;
    }

    /**
       Set the index of the types row.
       @param i the new index
    */
    public void setTypesRow(int i) {
	_typesRow = i;
    }

    /**
       Get the index of the labels row.
       @return the index of the labels row
    */
    public int getLabelsRow() {
	return _labelsRow;
    }

    /**
       Set the index of the labels row
       @param i the new index
    */
    public void setLabelsRow(int i) {
	_labelsRow = i;
    }

    public double getEmptyValue() {
    	return emptyValue;
    }

    public void setEmtpyValue(double d) {
    	emptyValue = d;
    }

    public boolean isReady() {
	//type information is not in header and needs to be provided
	// as the second input in the VerticalTable
	if (_labelsRow < 0 && _typesRow < 0 && inputFlags[0]>0 && inputFlags[1]>0)
	    return true;
	//type information is available in the file
	else if (_typesRow>=0 &&inputFlags[0] >0)
		 return true;
	//type information is not available
	return false;
    }





    /**
       PUT YOUR CODE HERE.
    */
    public void doit() throws Exception {

	FixedFormatParser p = null;
	if (_labelsRow < 0 && _typesRow < 0)
	    p=new FixedFormatParser(new File((String)pullInput(0)),(TableImpl)pullInput(1));
	else
	    p =	new FixedFormatParser(new File((String)pullInput(0)),_labelsRow, _typesRow);

	p.ignoreSyntaxErrors(getIgnoreSyntaxErrors());
	ADTree adt = p.parseAD();
	//adt.expand(adt);
	pushOutput(adt, 0);
	ExampleTable vt = (ExampleTable) p.getMetadata();
	pushOutput(vt,1);
        pushOutput(p.getBlanks(), 2);
  


    }

}








