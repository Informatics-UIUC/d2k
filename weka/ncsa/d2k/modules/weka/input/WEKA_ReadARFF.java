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
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * This class reads a file in ARFF format and creates an Instances object for
 * later processing by weka classes.
*/
public class WEKA_ReadARFF extends DataPrepModule {

    //==============
    // Data Members
    //==============

    //index of the class attribute (assumes only one)
    // -1 = last attribute by default
    private int m_classIndex = -1;

    private boolean m_verbose = false;

    private boolean m_hasClass = true;

    //================
    // Constructor(s)
    //================

    //================
    // Public Methods
    //================

    /**
       Describe the input types.
       @return An array containing the datatypes of the inputs.
    */
    public String[] getInputTypes() {
	String []in = {"java.lang.String"};
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
	return "Path and file name of the file to read";
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
	return "WEKA Instances object built from file buffer";
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
	StringBuffer sb = new StringBuffer("This module will read a file oa ARFF ");
	sb.append("format and construct a WEKA Instances object.  The instances object ");
	sb.append("is the primary data structure for holding table information in the WEKA ");
	sb.append("framework. ");
	return sb.toString();
    }

   /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
	return "WEKA_ReadARFF";
    }

    /**
       Read the file and create the Instances.
    */
    public void doit() throws java.lang.Exception {
	String fn = (String)pullInput(0);
        Instances instances = null;
	try {
          Reader r = new BufferedReader(new FileReader(fn));
          instances = new Instances(r);
          if (m_hasClass){
            int ind = getClassIndex();
            if ((ind == -1) || (ind >= instances.numAttributes())){
              instances.setClassIndex(instances.numAttributes() - 1);
            } else {
              instances.setClassIndex(ind);
            }
          }
	} catch (Exception ex) {
	    System.err.println("ERROR in ReadARFF: " + ex.getMessage());
	    throw ex;
	}

        if (getVerbose()){
           System.out.println(instances.toSummaryString());
        }

	System.gc();
	this.pushOutput(instances, 0);
    }

    public int getClassIndex(){
      return m_classIndex;
    }

    public void setClassIndex(int i){
      m_classIndex = i;
    }

    public boolean getVerbose(){
      return m_verbose;
    }

    public void setVerbose(boolean b){
      m_verbose = b;
    }

    public boolean getHasClass(){
      return m_hasClass;
    }

    public void setHasClass(boolean b){
      m_hasClass = b;
    }

}
