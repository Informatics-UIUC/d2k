package ncsa.d2k.modules.weka.input;

//==============
// Java Imports
//==============

import java.io.*;
import java.util.*;

//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import weka.core.*;

public class WEKA_InstancesToExampleTable extends DataPrepModule {

  //==============
  // Data Members
  //==============

  private boolean m_verbose = false;

  static String STRING_TYPE = "String";
  static String FLOAT_TYPE = "float";
  static String DOUBLE_TYPE = "double";
  static String INT_TYPE = "int";
  static String BOOLEAN_TYPE = "boolean";
  static String CHAR_TYPE = "char[]";
  static String BYTE_TYPE = "byte[]";
  static String LONG_TYPE = "long";
  static String SHORT_TYPE = "short";

  //================
  // Constructor(s)
  //================

  public WEKA_InstancesToExampleTable() {
  }

    //================
    // Public Methods
    //================

    /**
       Describe the input types.
       @return An array containing the datatypes of the inputs.
    */
    public String[] getInputTypes() {
	String []in = {"weka.core.Instances"};
	return in;
    }

    /**
       Describe the output types.
       @return An array containing the datatypes of the outputs.
    */
    public String[] getOutputTypes() {
	String []out = {"ncsa.d2k.util.datatype.ExampleTable"};
	return out;
    }

    /**
       Describe the inputs.
       @param i the index of the input to describe
       @return A description of the selected input.
    */
    public String getInputInfo(int i) {
	return "WEKA Instances object";
    }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
	if(i == 0) {
	    return "WEKA Instances";
	} else {
	    return "No such input!";
	}
    }

    /**
       Describe the outputs.
       @param i The index of the output to describe
       @return A description of the selected output.
    */
    public String getOutputInfo(int i) {
	return "A D2K ExampleTable";
    }

   /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
	if(i == 0) {
	    return "D2K ExampleTable";
	} else {
	    return "No such output!";
	}
    }

    /**
       Describe the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
	StringBuffer sb = new StringBuffer("This module will read in a WEKA Instances object ");
	sb.append("and construct a D2K Example Table. ");
	return sb.toString();
    }

   /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
	return "WEKA_InstancesToExampleTable";
    }

    public String getWEKAType(Attribute att) {
      if (att.isNumeric()){
        return DOUBLE_TYPE;
      } else {
        return STRING_TYPE;
      }
    }


    /**
       Read the file and create the Instances.
    */
    public void doit() throws java.lang.Exception {
      try{
        Instances instances = (Instances)pullInput(0);
        int numatts = instances.numAttributes();
        int numinsts = instances.numInstances();

	// now create the columns
	//SimpleColumn []tableColumns = new SimpleColumn[m_maxRowLength];
        Vector tableColumns = new Vector();
	for(int i = 0; i < numatts; i++) {
	    tableColumns.addElement(createColumn(getWEKAType(instances.attribute(i)), numinsts));
        }

	// now populate the columns
	for(int row = 0; row < numinsts; row++) {
          for(int col = 0; col < numatts; col++){
            Attribute att = instances.attribute(col);
            if (att.isNumeric()){
		((SimpleColumn)tableColumns.elementAt(col)).setString(Double.toString(instances.instance(row).value(col)), row);
            } else {
		((SimpleColumn)tableColumns.elementAt(col)).setString(att.value((int)instances.instance(row).value(col)), row);
            }
          }
        }

	// set the labels if given
	for(int i = 0; i < numatts; i++){
          ((SimpleColumn)tableColumns.elementAt(i)).setLabel(instances.attribute(i).name());
        }

        SimpleColumn[] simps = new SimpleColumn[0];
        VerticalTable verticaltable = new VerticalTable((SimpleColumn[])tableColumns.toArray(simps));
        ExampleTable exampletable = new ExampleTable((Table)verticaltable);

        int classind = instances.classIndex();
        int[] ins = null;
        if ((classind > -1) && (classind < numatts)){
          ins = new int[numatts - 1];
        } else {
          ins = new int[numatts];
        }
        int j = 0;
        for (int i = 0;i < numatts; i++){
          if (i != classind){
            ins[j] = i;
            j++;
          }
        }
        exampletable.setInputFeatures(ins);

        if (getVerbose()){
          exampletable.print();
        }
        System.gc();
	this.pushOutput(exampletable, 0);
      } catch (Exception ex) {
          ex.printStackTrace();
	  System.err.println("ERROR in WEKA_InstancesToVerticalTable: " + ex);
	  throw ex;
	}
    }

	/**
		Create a column given the type and size.
		@param type the type of column to create
		@param size the initial size of the column
		@return a new, empty column
	*/
	protected SimpleColumn createColumn(String type, int size) {
		if(type.equals(STRING_TYPE))
			return new StringColumn(size);
		else if(type.equals(FLOAT_TYPE))
			return new FloatColumn(size);
		else if(type.equals(DOUBLE_TYPE))
			return new DoubleColumn(size);
		else if(type.equals(INT_TYPE))
			return new IntColumn(size);
		else if(type.equals(BOOLEAN_TYPE))
			return new BooleanColumn(size);
		else if(type.equals(CHAR_TYPE))
			return new CharArrayColumn(size);
		else if(type.equals(BYTE_TYPE))
			return new ByteArrayColumn(size);
		else if(type.equals(LONG_TYPE))
			return new LongColumn(size);
		else if(type.equals(SHORT_TYPE))
			return new ShortColumn(size);
		else
			return new ByteArrayColumn(size);
	}

    public boolean getVerbose(){
      return m_verbose;
    }

    public void setVerbose(boolean b){
      m_verbose = b;
    }

}