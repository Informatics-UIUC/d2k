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
 *    GainRatioAttributeEval.java
 *    Copyright (C) 1999 Mark Hall
 *    Adapted to work with the D2k Table by Anca Suvaiala.
 */


package ncsa.d2k.modules.core.transform.filters.evaluation;

import java.util.Enumeration;
import java.util.Vector;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.transformations.BinTransform;
import ncsa.d2k.modules.core.datatype.table.transformations.ReplaceNominalsWithIntsTransform;
import ncsa.d2k.modules.core.datatype.table.util.TableUtilities;
import ncsa.d2k.modules.core.transform.binning.BinDescriptor;
import ncsa.d2k.modules.core.transform.filters.*;

/**
 * Class for Evaluating attributes individually by measuring gain ratio
 * with respect to the class. <p>
 *
 * Valid options are:<p>
 *
 * -M <br>
 * Treat missing values as a seperate value. <br>
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class GainRatioAttributeEval
  extends AttributeEvaluator
  implements OptionHandler
{

  /** The training instances */
  private ExampleTable m_trainTable;

  /** The class index */
  private int m_classIndex;

  /** The number of attributes */
  private int m_numAttribs;

  /** The number of instances */
  private int m_numInstances;

  /** The number of classes */
  private int m_numClasses;

  /** Merge missing values */
  private boolean m_missing_merge;

  

  /**
   * Constructor
   */
  public GainRatioAttributeEval () {
    resetOptions();
  }


  /**
   * Returns an enumeration describing the available options.
   * @return an enumeration of all the available options.
   **/
  public Enumeration listOptions () {
    Vector newVector = new Vector(1);
    newVector.addElement(new Option("\ttreat missing values as a seperate "
				    + "value.", "M", 0, "-M"));
    return  newVector.elements();
  }


  /**
   * Parses a given list of options. Valid options are:<p>
   *
   * -M <br>
   * Treat missing values as a seperate value. <p>
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   **/
  public void setOptions (String[] options)
    throws Exception
  {
    resetOptions();
    setMissingMerge(!(Utils.getFlag('M', options)));
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
   * Gets the current settings of WrapperSubsetEval.
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[1];
    int current = 0;

    if (!getMissingMerge()) {
      options[current++] = "-M";
    }

    while (current < options.length) {
      options[current++] = "";
    }

    return  options;
  }


  /**
   * Initializes a gain ratio attribute evaluator.
   * Discretizes all attributes that are numeric.
   *
   * @param data set of instances serving as training data
   * @exception Exception if the evaluator has not been
   * generated successfully
   */
  public void buildEvaluator (Table data)
    throws Exception
  {
    //TODO check this later
    /*for (int i =0; i < data.getNumColumns() -1; i ++)
    if (data.isColumnNominal(i)) {
      System.err.println("Can't handle string attributes!");
      return;
    } */

    m_trainTable = (ExampleTable)data;
    int [] outputFeatures = m_trainTable.getOutputFeatures();
    if (outputFeatures == null || outputFeatures.length == 0) 
    	throw new Exception ( " Class attribute must be specified in ChooseAttributes ");
    
    m_classIndex = m_trainTable.getOutputFeatures()[0];
    m_numAttribs = m_trainTable.getNumColumns();
    m_numInstances = m_trainTable.getNumRows();

    if (m_trainTable.isColumnScalar(m_classIndex)) {
      throw  new Exception("Class must be nominal!");
    }

    // discretize numeric attributes
    //selectedColumns has to come from a user interface ore other params
    int [] selectedColumns = new int[m_numAttribs-1];
    for (int i =0; i < m_numAttribs-1; i ++)
          selectedColumns[i] = i;
     //bin numeric attributes     
    EntropyBinning disTransform = new EntropyBinning();
    disTransform.setUseBetterEncoding(true);
    BinDescriptor[] binDescr =
      disTransform.buildBins((ExampleTable)m_trainTable,selectedColumns);
    BinTransform bt = new BinTransform(m_trainTable,binDescr, false);
    bt.transform(m_trainTable);
    // transform nominal binned attributes to numeric ones
    ReplaceNominalsWithIntsTransform tr =
     new ReplaceNominalsWithIntsTransform((ExampleTable)m_trainTable);
  	tr.transform((MutableTable)m_trainTable);
    String [] uniques = TableUtilities.uniqueValues(m_trainTable,m_classIndex);
    m_numClasses = uniques.length;
  }


  /**
   * reset options to default values
   */
  protected void resetOptions () {
    m_trainTable = null;
    m_missing_merge = true;
  }


  /**
   * evaluates an individual attribute by measuring the gain ratio
   * of the class given the attribute.
   *
   * @param attribute the index of the attribute to be evaluated
   * @exception Exception if the attribute could not be evaluated
   */
  public double evaluateAttribute (int attribute)
    throws Exception
  {
    int i, j, ii, jj;
    int nnj, nni, ni, nj;
    double sum = 0.0;

    // TODO numOFValues of an attribute

    String[] attributeCounts = TableUtilities.uniqueValues(m_trainTable,attribute);
    int numAttributes = attributeCounts.length;
    ni = numAttributes + 1;
    nj = m_numClasses + 1;
    double[] sumi, sumj;
    //Instance inst;
    double temp = 0.0;
    sumi = new double[ni];
    sumj = new double[nj];
    double[][] counts = new double[ni][nj];
    sumi = new double[ni];
    sumj = new double[nj];

    for (i = 0; i < ni; i++) {
      sumi[i] = 0.0;

      for (j = 0; j < nj; j++) {
        sumj[j] = 0.0;
        counts[i][j] = 0.0;
      }
    }

    // Fill the contingency table
    for (i = 0; i < m_numInstances; i++) {

      if (m_trainTable.isValueMissing(i,attribute)) {
        ii = ni - 1;
      }
      else {
        ii = m_trainTable.getInt(i,attribute);
      }

      if (m_trainTable.isValueMissing(i,m_classIndex)) {
        jj = nj - 1;
      }
      else {
        jj = m_trainTable.getInt(i,m_classIndex);
      }

      counts[ii][jj]++;
    }

    // get the row totals
    for (i = 0; i < ni; i++) {
      sumi[i] = 0.0;

      for (j = 0; j < nj; j++) {
        sumi[i] += counts[i][j];
        sum += counts[i][j];
      }
    }

    // get the column totals
    for (j = 0; j < nj; j++) {
      sumj[j] = 0.0;

      for (i = 0; i < ni; i++) {
        sumj[j] += counts[i][j];
      }
    }

    // distribute missing counts
    if (m_missing_merge &&
	(sumi[ni-1] < m_numInstances) &&
	(sumj[nj-1] < m_numInstances)) {
      double[] i_copy = new double[sumi.length];
      double[] j_copy = new double[sumj.length];
      double[][] counts_copy = new double[sumi.length][sumj.length];

      for (i = 0; i < ni; i++) {
        System.arraycopy(counts[i], 0, counts_copy[i], 0, sumj.length);
      }

      System.arraycopy(sumi, 0, i_copy, 0, sumi.length);
      System.arraycopy(sumj, 0, j_copy, 0, sumj.length);
      double total_missing = (sumi[ni - 1] + sumj[nj - 1] -
			      counts[ni - 1][nj - 1]);

      // do the missing i's
      if (sumi[ni - 1] > 0.0) {
        for (j = 0; j < nj - 1; j++) {
          if (counts[ni - 1][j] > 0.0) {
            for (i = 0; i < ni - 1; i++) {
              temp = ((i_copy[i]/(sum - i_copy[ni - 1]))*counts[ni - 1][j]);
              counts[i][j] += temp;
              sumi[i] += temp;
            }

            counts[ni - 1][j] = 0.0;
          }
        }
      }

      sumi[ni - 1] = 0.0;

      // do the missing j's
      if (sumj[nj - 1] > 0.0) {
        for (i = 0; i < ni - 1; i++) {
          if (counts[i][nj - 1] > 0.0) {
            for (j = 0; j < nj - 1; j++) {
              temp = ((j_copy[j]/(sum - j_copy[nj - 1]))*counts[i][nj - 1]);
              counts[i][j] += temp;
              sumj[j] += temp;
            }

            counts[i][nj - 1] = 0.0;
          }
        }
      }

      sumj[nj - 1] = 0.0;

      // do the both missing
      if (counts[ni - 1][nj - 1] > 0.0  && total_missing != sum) {
        for (i = 0; i < ni - 1; i++) {
          for (j = 0; j < nj - 1; j++) {
            temp = (counts_copy[i][j]/(sum - total_missing)) *
	      counts_copy[ni - 1][nj - 1];
            counts[i][j] += temp;
            sumi[i] += temp;
            sumj[j] += temp;
          }
        }

        counts[ni - 1][nj - 1] = 0.0;
      }
    }

    return  ContingencyTables.gainRatio(counts);
  }


  /**
   * Return a description of the evaluator
   * @return description as a string
   */
  public String toString () {
    StringBuffer text = new StringBuffer();

    if (m_trainTable == null) {
      text.append("\tGain Ratio evaluator has not been built");
    }
    else {
      text.append("\tGain Ratio feature evaluator");

      if (!m_missing_merge) {
        text.append("\n\tMissing values treated as seperate");
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
   * @param args the options
   * -t training file
   */
  public static void main (String[] args) {
    try {
      System.out.println(AttributeSelection.
			 SelectAttributes(new GainRatioAttributeEval(), args));
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }

}

