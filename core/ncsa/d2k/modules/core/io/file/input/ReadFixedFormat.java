package ncsa.d2k.modules.core.io.file.input;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.pipes.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.io.*;
import java.util.*;
import ncsa.d2k.modules.core.io.file.*;

/**

 */

public class ReadFixedFormat extends ncsa.d2k.core.modules.InputModule
    
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
			case 0: return "    input file name   ";
			case 1: return "Table containing type/label data";
			default: return "No such input";
		}
	}

    /**
       This pair returns an array of strings that contains the data types for the inputs.
       @return the data types of all inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"java.lang.String","ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

    /**
       This pair returns the description of the outputs.
       @return the description of the indexed output.
    */
    public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "      a table containing data from the file   ";
			case 1: return "Contains row/column info on which fields were blank inthe read in file";
			default: return "No such output";
		}
	}

    /**
       This pair returns an array of strings that contains the data types for the outputs.
       @return the data types of all outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl","ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

    /**
       This pair returns the description of the module.
       @return the description of the module.
    */
    public String getModuleInfo() {
		return "<paragraph>  <head>  </head>  <body>    <p>          </p>  </body></paragraph>";
	}

    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    /**
       Get the flag for handling of the formating errors .
       @return the value of the flag for handling formating errors

    public boolean getIgnoreSyntaxErrors() {
	return _ignoreSyntaxErrors;
    }

    /**
       Set the flag  for handling of the formating errors .
       @param flag  the value of the flag for handling formating errors

    public void    setIgnoreSyntaxErrors(boolean flag) {
	_ignoreSyntaxErrors = flag;
    }


    /**
       Get the index of the types row.
       @return the index of the types row.

    public int getTypesRow() {
	return _typesRow;
    }

    /**
       Set the index of the types row.
       @param i the new index

    public void setTypesRow(int i) {
	_typesRow = i;
    }

    /**
       Get the index of the labels row.
       @return the index of the labels row

    public int getLabelsRow() {
	return _labelsRow;
    }

    /**
       Set the index of the labels row
       @param i the new index

    public void setLabelsRow(int i) {
	_labelsRow = i;
    }

    public double getEmptyValue() {
    	return emptyValue;
    }

    public void setEmtpyValue(double d) {
    	emptyValue = d;
    }
*/
    public boolean isReady() {
		return super.isReady();
    }



    /**
       PUT YOUR CODE HERE.
    */
    public void doit() throws Exception {

	FixedFormatParser p = null;
	p=new FixedFormatParser(new File((String)pullInput(0)),(TableImpl)pullInput(1)/*, emptyValue*/);

	pushOutput(p.parse(), 0);
	pushOutput(p.getBlanks(), 1);
	//VerticalTable vt = (VerticalTable) p.parse();
	//vt.print();
	//pushOutput(vt,0);

    }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "ReadFixedFormat";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "_fileName";
			case 1:
				return "input1";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "_dataTable";
			case 1:
				return "output1";
			default: return "NO SUCH OUTPUT!";
		}
	}
}








