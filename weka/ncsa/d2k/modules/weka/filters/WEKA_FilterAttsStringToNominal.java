package ncsa.d2k.modules.weka.filters;
//==============
// Java Imports
//==============

import java.io.*;
import java.util.*;

//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;
import weka.core.*;
import weka.filters.StringToNominalFilter;

public class WEKA_FilterAttsStringToNominal extends DataPrepModule {


  //==============
  // Data Members
  //==============

  private boolean m_verbose = false;

  //================
  // Constructor(s)
  //================

  public WEKA_FilterAttsStringToNominal() {
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
	String []out = {"weka.core.Instances"};
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
	return "WEKA Instances object where some or all String attributes have been converted to nominals.";
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
	StringBuffer sb = new StringBuffer("This module will read in a WEKA Instances object ");
	sb.append("and construct a WEKA Instances object where some or all of the String attributes have been converted to nominal. The instances object ");
	sb.append("is the primary data structure for holding table information in the WEKA ");
	sb.append("framework. ");
	return sb.toString();
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
	  Instances in = (Instances)pullInput(0);
          Instances newData = null;
          int numatts = in.numAttributes();
          for (int i = 0; i < numatts; i++) {
            if (in.attribute(i).isString()){
              StringToNominalFilter filter = new StringToNominalFilter();
              filter.setAttributeIndex(i); //must come before setInputFormat (this type of dependency is bad)
              filter.setInputFormat(in);
              int numinsts = in.numInstances();
              for (int j = 0; j < numinsts; j++) {
                filter.input(in.instance(j));
              }
              filter.batchFinished();
              newData = filter.getOutputFormat();
              Instance processed;
              while ((processed = filter.output()) != null) {
                newData.add(processed);
              }
              in = newData;
            }
          }

          if (getVerbose()){
            System.out.println(newData.toSummaryString());
          }
          System.gc();
	  this.pushOutput(newData, 0);
	} catch (Exception ex) {
            ex.printStackTrace();
	    System.err.println("ERROR in WEKA_FilterAttsStringToNominal: " + ex);
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