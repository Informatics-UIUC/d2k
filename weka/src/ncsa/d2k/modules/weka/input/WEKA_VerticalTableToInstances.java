package ncsa.d2k.modules.weka.input;
//==============
// Java Imports
//==============


import java.io.*;
import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import weka.core.*;

/**
 * This module takes a vertical table as input and outputs a WEKA Instances object.
*/

public class WEKA_VerticalTableToInstances extends DataPrepModule {

  //==============
  // Data Members
  //==============

  private boolean m_verbose = false;

  //================
  // Constructor(s)
  //================

  public WEKA_VerticalTableToInstances() {
  }

    //================
    // Public Methods
    //================

    /**
       Describe the input types.
       @return An array containing the datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

    /**
       Describe the output types.
       @return An array containing the datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {"weka.core.Instances"};
		return types;
	}

    /**
       Describe the inputs.
       @param i the index of the input to describe
       @return A description of the selected input.
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "A D2K Vertical Table";
			default: return "No such input";
		}
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
	if(i == 0) {
	    return "WEKA Instances object";
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
		switch (i) {
			case 0: return "WEKA Instances object built from D2K Vertical Table";
			default: return "No such output";
		}
	}

   /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
	if(i == 0) {
	    return "Instances";
	} else {
	    return "No such output!";
	}
    }

    /**
       Describe the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module will read in a D2K Vertical Table and construct a WEKA     Instances object. The instances object is the primary data structure for     holding table information in the WEKA framework.  </body></html>";
	}

   /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
	return "WEKA_VerticalTableToInstances";
    }

    /**
       Read the file and create the Instances.
    */
    public void doit() throws java.lang.Exception {
        try{
	  /*Vertical*/Table vt = (/*Vertical*/Table)pullInput(0);
          int numatts = vt.getNumColumns();
          FastVector atts = new FastVector(numatts);
          for (int i = 0; i < numatts; i++){
            //Object attType = vt.getColumn(i).getType();
            int attType = vt.getColumnType(i);
            /*if ((attType instanceof Short) || (attType instanceof Float) || (attType instanceof Double)
              || (attType instanceof Integer) || (attType instanceof Long)){
              */
             if( (attType == ColumnTypes.SHORT) || (attType == ColumnTypes.FLOAT) || (attType == ColumnTypes.DOUBLE)
                || (attType == ColumnTypes.INTEGER) || (attType == ColumnTypes.LONG)){
              atts.addElement(new Attribute(vt.getColumnLabel(i)));
            } else {
            //assume the column is of a string type (VT's don't have explicitly nominal values)
              atts.addElement(new Attribute(vt.getColumnLabel(i), null));
            }
          }
          Instances instances = new Instances("Vertical Table", atts, 1000);
          long numinstances = vt.getNumRows();
          for (int i = 0; i < numinstances; i++){
            Instance inst = new Instance(numatts);
            inst.setDataset(instances);
            for (int j = 0; j < numatts; j++){
              if (((Attribute)atts.elementAt(j)).isString()){
                try{
                  inst.setValue((Attribute)atts.elementAt(j), vt.getString(i, j));
                } catch (Exception e){
                  //string hasn't been added yet (first time encoutered) so add it then try again
                  ((Attribute)atts.elementAt(j)).addStringValue(vt.getString(i, j));
                  inst.setValue((Attribute)atts.elementAt(j), vt.getString(i, j));
                }
              } else {
                inst.setValue((Attribute)atts.elementAt(j),vt.getDouble(i,j));
              }
            }
            instances.add(inst);
          }
          if (vt instanceof ncsa.d2k.modules.core.datatype.table.ExampleTable) {
            ExampleTable et = (ExampleTable)vt;
            int numout = et.getNumOutputFeatures();
            if (numout > 1) {
              System.out.println("The input example table has more than one output feature, none will be set i the instances.");
            } else if (numout == 1){
              int[] outs = et.getOutputFeatures();
              instances.setClassIndex(outs[0]);
              System.out.println("Class is set for column: " + outs[0]);
            } else {
              System.out.println("No class set.");
            }
          }
          if (getVerbose()){
            System.out.println(instances.toSummaryString());
          }
          System.gc();
	  this.pushOutput(instances, 0);
	} catch (Exception ex) {
            ex.printStackTrace();
	    System.err.println("ERROR in WEKA_VerticalTableToInstances: " + ex);
            throw ex;
	}
    }


    public boolean getVerbose(){
      return m_verbose;
    }

    public void setVerbose(boolean b){
      m_verbose = b;
    }

}
