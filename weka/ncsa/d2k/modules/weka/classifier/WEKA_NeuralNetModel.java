
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package ncsa.d2k.modules.weka.classifier;

import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.*;

import weka.classifiers.neural.*;
import weka.classifiers.Classifier;
import weka.core.Instances;


public class WEKA_NeuralNetModel extends PredictionModelModule implements WEKA_ModelDelegator {

  //==============
  // Data Members
  //==============

  NeuralNetwork m_modelDelegate = null;

  //================
  // Constructor(s)
  //================

  public WEKA_NeuralNetModel() {
    m_modelDelegate = new NeuralNetwork();
  }

  //==================
  // Public Functions
  //==================


  /**
   * Return a description of the function of this module.
   * @return A description of this module.
   */
  public String getModuleInfo() {
    StringBuffer sb = new StringBuffer("Makes predictions based on the");
    sb.append(" data it was created with. ");
    return sb.toString();
  }

  /**
   * Return the name of this module.
   * @return The name of this module.
   */
  public String getModuleName() {
    return "WEKA_NeuralNetModel";
  }

  /**
   * Return a String array containing the datatypes the inputs to this
   * module.
   * @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return in;
  }

  /**
   * Return a String array containing the datatypes of the outputs
   * of this module.
   * @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] out = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return out;
  }

  /**
   * Return a description of a specific input.
   * @param i The index of the input
   * @return The description of the input
   */
  public String getInputInfo(int i) {
    StringBuffer sb = new StringBuffer("Table of data to predict.  This ");
    sb.append("must have the same input variables as the training data!");
    return sb.toString();
  }

  /**
   * Return the name of a specific input.
   * @param i The index of the input.
   * @return The name of the input
   */
  public String getInputName(int i) {
    return "NewData";
  }

  /**
   * Return the description of a specific output.
   * @param i The index of the output.
   * @return The description of the output.
   */
  public String getOutputInfo(int i) {
    return "Data to predict, with the predictions in the last column.";
  }

  /**
   * Return the name of a specific output.
   * @param i The index of the output.
   * @return The name of the output
   */
  public String getOutputName(int i) {
    return "predictions";
  }

  /**
   * Pull in the data to predict, and do the prediction.  A new Column
   * is added to the table to hold the predictions.
   */
   public void doit() {
     /*
		VerticalTable vt = (VerticalTable)pullInput(0);
		ExampleTable result;

		if(vt instanceof ExampleTable)
			result = (ExampleTable)predict(vt);
		else
			result = (ExampleTable)predict(new ExampleTable(vt));

		pushOutput(result, 0);
  */
  }


  /**
   * Predict the classes based on the attributes.
   */
  public PredictionTable predict(ExampleTable src) {
    /*
		ExampleTable vt = (ExampleTable)src;
		return vt;
  */
    return null;
  }


  //===============================================
  // Interface Implementation: WEKA_ModelDelegator
  //===============================================

  public Classifier getDelegate() {
    return (Classifier)m_modelDelegate;
  }

  public void buildClassifier(Instances instances) throws Exception{
    getDelegate().buildClassifier(instances);
  }

  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception{
    m_modelDelegate.setOptions(options);
  }

  /**
   * Returns a description of the classifier.
   */
  public String toString() {
    return m_modelDelegate.toString();
  }

}