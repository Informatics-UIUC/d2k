/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    InfoGainAttributeEval.java
 *    Copyright (C) 1999 Mark Hall
 *
 */

package ncsa.d2k.modules.core.transform.filters.evaluation;

//import  java.io.*;
import java.util.Enumeration;
import java.util.Vector;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.datatype.table.transformations.ReplaceNominalsWithIntsTransform;
import ncsa.d2k.modules.core.datatype.table.util.TableUtilities;
import ncsa.d2k.modules.core.transform.binning.BinDescriptor;
import ncsa.d2k.modules.core.transform.filters.*;


/**
 * Class for Evaluating attributes individually by measuring information gain
 * with respect to the class.
 *
 * Valid options are:<p>
 *
 * -M <br>
 * Treat missing values as a seperate value. <br>
 *
 * -B <br>
 * Just binarize numeric attributes instead of properly discretizing them. <br>
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class InfoGainAttributeEval
  extends AttributeEvaluator {
  //implements OptionHandler {

  /** Treat missing values as a seperate value */
  private boolean m_missing_merge;

  /** Just binarize numeric attributes */
  private boolean m_Binarize;

  /** The info gain for each attribute */
  private double[] m_InfoGains;

  
  /**
   * Constructor
   */
  public InfoGainAttributeEval () {
    resetOptions();
  }

  /**
   * Returns an enumeration describing the available options.
   * @return an enumeration of all the available options.
   **/
  public Enumeration listOptions () {
    Vector newVector = new Vector(2);
    newVector.addElement(new Option("\ttreat missing values as a seperate "
				    + "value.", "M", 0, "-M"));
    newVector.addElement(new Option("\tjust binarize numeric attributes instead\n "
				    +"\tof properly discretizing them.", "B", 0,
				    "-B"));
    return  newVector.elements();
  }


  /**
   * Parses a given list of options. <p>
   *
   * Valid options are:<p>
   *
   * -M <br>
   * Treat missing values as a seperate value. <br>
   *
   * -B <br>
   * Just binarize numeric attributes instead of properly discretizing them. <br>
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   *
   **/
  public void setOptions (String[] options)
    throws Exception {

    resetOptions();
    setMissingMerge(!(Utils.getFlag('M', options)));
    setBinarizeNumericAttributes(Utils.getFlag('B', options));
  }


  /**
   * Gets the current settings of WrapperSubsetEval.
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[2];
    int current = 0;

    if (!getMissingMerge()) {
      options[current++] = "-M";
    }
    if (getBinarizeNumericAttributes()) {
      options[current++] = "-B";
    }

    while (current < options.length) {
      options[current++] = "";
    }

    return  options;
  }

 

  /**
   * Binarize numeric attributes.
   *
   * @param b true=binarize numeric attributes
   */
  public void setBinarizeNumericAttributes (boolean b) {
    m_Binarize = b;
  }


  /**
   * get whether numeric attributes are just being binarized.
   *
   * @return true if missing values are being distributed.
   */
  public boolean getBinarizeNumericAttributes () {
    return  m_Binarize;
  }

 

  /**
   * distribute the counts for missing values across observed values
   *
   * @param b true=distribute missing values.
   */
  public void setMissingMerge (boolean b) {
    m_missing_merge = b;
  }


  /**
   * get whether missing values are being distributed or not
   *
   * @return true if missing values are being distributed.
   */
  public boolean getMissingMerge () {
    return  m_missing_merge;
  }


  /**
   * Initializes an information gain attribute evaluator.
   * Discretizes all attributes that are numeric.
   *
   * @param data set of instances serving as training data
   * @exception Exception if the evaluator has not been
   * generated successfully
   */
  public void buildEvaluator (Table data)
    throws Exception {

    //if (data.checkForStringAttributes()) {
    //  throw  new UnsupportedAttributeTypeException("Can't handle string attributes!");
    //}

    int[] outputFeatures = ((ExampleTable) data).getOutputFeatures();

    if (outputFeatures == null || outputFeatures.length == 0) 
    	throw new Exception ( " Class attribute must be specified in ChooseAttributes ");
    
	int classIndex = outputFeatures[0];
    int numAttributes = data.getNumColumns();
    if (data.isColumnScalar(classIndex)) {
      throw  new Exception("Class must be nominal!");
    }
    int numInstances = data.getNumRows();


    //selectedColumns has to come from a user interface ore other params
    int [] selectedColumns = new int[data.getNumColumns()-1];

    if (!m_Binarize) {
      EntropyBinning m_disTransform = new EntropyBinning();
      m_disTransform.setUseBetterEncoding(true);
      for (int i =0; i < data.getNumColumns()-1; i ++) {
               selectedColumns[i] = i;
      }
      BinDescriptor[] binDescr = m_disTransform.buildBins((ExampleTable)data,selectedColumns);
      BinTransform bt = new BinTransform(data,binDescr, false);
      bt.transform((ExampleTable)data);
    } else {
      NumericToBinaryTransform binTransform = new NumericToBinaryTransform(null);
      binTransform.transform((ExampleTable)data);
    }
       // transform nominal binned attributes to numeric ones
    ReplaceNominalsWithIntsTransform tr =
     new ReplaceNominalsWithIntsTransform((ExampleTable)data);
     tr.transform((MutableTable)data);
    String[] tempStr = TableUtilities.uniqueValues(data,classIndex);
    int numClasses = tempStr.length;



    // Reserve space and initialize counters
    double[][][] counts = new double[data.getNumColumns()][][];
    int []attributeCounts = new int[numAttributes-1];
    for (int k = 0; k < data.getNumColumns(); k++) {
      if (k != classIndex) {
        tempStr = TableUtilities.uniqueValues(data,k);
    	int numValues = tempStr.length;
        attributeCounts[k] = numValues;
	counts[k] = new double[numValues + 1][numClasses + 1];
      }
    }


        // Initialize counters
    double[] temp = new double[numClasses + 1];
    for (int k = 0; k < numInstances; k++) {
       // Instance inst = data.instance(k);
      if (data.isValueMissing(k,classIndex)) {
	temp[numClasses] += 1;
        //temp[numClasses] += inst.weight();
      } else {
	temp[data.getInt(k,classIndex)] += 1;
        //temp[(int)inst.classValue()] += inst.weight();
      }
    }
    for (int k = 0; k < counts.length; k++) {
      if (k != classIndex) {
	for (int i = 0; i < temp.length; i++) {
	  counts[k][0][i] = temp[i];
	}
      }
    }

    // Get counts
    for (int k = 0; k < numInstances; k++) {
     // Instance inst = data.instance(k);
      for (int i = 0; i < numAttributes; i++) {
	if (i != classIndex) {
	  if (data.isValueMissing(k,i) || data.isValueMissing(k,classIndex)) {
	    if (!data.isValueMissing(k,i)) {
	      counts[i][data.getInt(k,i)][numClasses] +=1;
	      counts[i][0][numClasses] -= 1;
	    } else if (!data.isValueMissing(k,classIndex)) {
                counts[i][attributeCounts[i]][data.getInt(k,classIndex)] += 1;
	      counts[i][0][data.getInt(k,classIndex)] -= 1;
	    } else {
	      counts[i][attributeCounts[i]][numClasses] += 1;
	      counts[i][0][numClasses] -= 1;
	    }
	  } else {
	    counts[i][data.getInt(k,i)][data.getInt(k,classIndex)] += 1;
	    counts[i][0][data.getInt(k,classIndex)] -= 1;
	  }
	}
      }
    }

    // distribute missing counts if required
    if (m_missing_merge) {

      for (int k = 0; k < data.getNumColumns(); k++) {
	if (k != classIndex) {
	  int numValues = attributeCounts[k];

	  // Compute marginals
	  double[] rowSums = new double[numValues];
	  double[] columnSums = new double[numClasses];
	  double sum = 0;
	  for (int i = 0; i < numValues; i++) {
	    for (int j = 0; j < numClasses; j++) {
	      rowSums[i] += counts[k][i][j];
	      columnSums[j] += counts[k][i][j];
	    }
	    sum += rowSums[i];
	  }

	  if (Utils.gr(sum, 0)) {
	    double[][] additions = new double[numValues][numClasses];

	    // Compute what needs to be added to each row
	    for (int i = 0; i < numValues; i++) {
	      for (int j = 0; j  < numClasses; j++) {
		additions[i][j] = (rowSums[i] / sum) * counts[k][numValues][j];
	      }
	    }

	    // Compute what needs to be added to each column
	    for (int i = 0; i < numClasses; i++) {
	      for (int j = 0; j  < numValues; j++) {
		additions[j][i] += (columnSums[i] / sum) *
		  counts[k][j][numClasses];
	      }
	    }

	    // Compute what needs to be added to each cell
	    for (int i = 0; i < numClasses; i++) {
	      for (int j = 0; j  < numValues; j++) {
		additions[j][i] += (counts[k][j][i] / sum) *
		  counts[k][numValues][numClasses];
	      }
	    }

	    // Make new contingency table
	    double[][] newTable = new double[numValues][numClasses];
	    for (int i = 0; i < numValues; i++) {
	      for (int j = 0; j < numClasses; j++) {
		newTable[i][j] = counts[k][i][j] + additions[i][j];
	      }
	    }
	    counts[k] = newTable;
	  }
	}
      }
    }

    // Compute info gains
    m_InfoGains = new double[numAttributes];
    for (int i = 0; i < numAttributes; i++) {
      if (i != classIndex) {
	m_InfoGains[i] =
	  (ContingencyTables.entropyOverColumns(counts[i])
	   - ContingencyTables.entropyConditionedOnRows(counts[i]));
      }
    }
  }

  /**
   * Reset options to their default values
   */
  protected void resetOptions () {
    m_InfoGains = null;
    m_missing_merge = true;
    m_Binarize = false;
  }


  /**
   * evaluates an individual attribute by measuring the amount
   * of information gained about the class given the attribute.
   *
   * @param attribute the index of the attribute to be evaluated
   * @exception Exception if the attribute could not be evaluated
   */
  public double evaluateAttribute (int attribute)
    throws Exception {

    return m_InfoGains[attribute];
  }

  /**
   * Describe the attribute evaluator
   * @return a description of the attribute evaluator as a string
   */
  public String toString () {
    StringBuffer text = new StringBuffer();

    if (m_InfoGains == null) {
      text.append("Information Gain attribute evaluator has not been built");
    }
    else {
      text.append("\tInformation Gain Ranking Filter");
      if (!m_missing_merge) {
	text.append("\n\tMissing values treated as seperate");
      }
      if (m_Binarize) {
	text.append("\n\tNumeric attributes are just binarized");
      }
    }

    text.append("\n");
    return  text.toString();
  }


  // ============
  // Test method.
  // ============
  /**
   * Main method for testing this class.
   *
   * @param argv the options
   */
  public static void main (String[] args) {
    try {
      System.out.println(AttributeSelection.
			 SelectAttributes(new InfoGainAttributeEval(), args));
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }
}


