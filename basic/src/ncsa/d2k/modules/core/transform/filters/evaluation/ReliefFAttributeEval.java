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
 *    ReliefFAttributeEval.java
 *    Copyright (C) 1999 Mark Hall
 *    Adapted to work with the D2k Table by Anca Suvaiala.
 */


package ncsa.d2k.modules.core.transform.filters.evaluation;


import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.util.TableUtilities;
import ncsa.d2k.modules.core.transform.filters.Option;
import ncsa.d2k.modules.core.transform.filters.Utils;

//import  weka.core.*;

/**
 * Class for Evaluating attributes individually using ReliefF. <p>
 *
 * For more information see: <p>
 *
 * Kira, K. and Rendell, L. A. (1992). A practical approach to feature
 * selection. In D. Sleeman and P. Edwards, editors, <i>Proceedings of
 * the International Conference on Machine Learning,</i> pages 249-256.
 * Morgan Kaufmann. <p>
 *
 * Kononenko, I. (1994). Estimating attributes: analysis and extensions of
 * Relief. In De Raedt, L. and Bergadano, F., editors, <i> Machine Learning:
 * ECML-94, </i> pages 171-182. Springer Verlag. <p>
 *
 * Marko Robnik Sikonja, Igor Kononenko: An adaptation of Relief for attribute
 * estimation on regression. In D.Fisher (ed.): <i> Machine Learning,
 * Proceedings of 14th International Conference on Machine Learning ICML'97,
 * </i> Nashville, TN, 1997. <p>
 *
 *
 * Valid options are:
 *
 * -M <number of instances> <br>
 * Specify the number of instances to sample when estimating attributes. <br>
 * If not specified then all instances will be used. <p>
 *
 * -D <seed> <br>
 * Seed for randomly sampling instances. <p>
 *
 * -K <number of neighbours> <br>
 * Number of nearest neighbours to use for estimating attributes. <br>
 * (Default is 10). <p>
 *
 * -W <br>
 * Weight nearest neighbours by distance. <p>
 *
 * -A <sigma> <br>
 * Specify sigma value (used in an exp function to control how quickly <br>
 * weights decrease for more distant instances). Use in conjunction with <br>
 * -W. Sensible values = 1/5 to 1/10 the number of nearest neighbours. <br>
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class ReliefFAttributeEval
  extends AttributeEvaluator
    //implements OptionHandler
{

  /** The training instances */
  private Table m_trainTable;

  /** The class index */
  private int m_classIndex;

  /** The number of attributes */
  private int m_numAttribs;

  /** The number of instances */
  private int m_numRows;

  /** Numeric class */
  private boolean m_numericClass;

  /** The number of classes if class is nominal */
  private int m_numClasses;

  /**
   * Used to hold the probability of a different class val given nearest
   * instances (numeric class)
   */
  private double m_ndc;

  /**
   * Used to hold the prob of different value of an attribute given
   * nearest instances (numeric class case)
   */
  private double[] m_nda;

  /**
   * Used to hold the prob of a different class val and different att
   * val given nearest instances (numeric class case)
   */
  private double[] m_ndcda;

  /** Holds the weights that relief assigns to attributes */
  private double[] m_weights;

  /** Prior class probabilities (discrete class case) */
  private double[] m_classProbs;

  /**
   * The number of instances to sample when estimating attributes
   * default == -1, use all instances
   */
  private int m_sampleM;

  /** The number of nearest hits/misses */
  private int m_Knn;

  /** k nearest scores + instance indexes for n classes */
  private double[][][] m_karray;

  /** Upper bound for numeric attributes */
  private double[] m_maxArray;

  /** Lower bound for numeric attributes */
  private double[] m_minArray;

  /** Keep track of the farthest instance for each class */
  private double[] m_worst;

  /** Index in the m_karray of the farthest instance for each class */
  private int[] m_index;

  /** Number of nearest neighbours stored of each class */
  private int[] m_stored;

  /** Random number seed used for sampling instances */
  private int m_seed;

  /**
   *  used to (optionally) weight nearest neighbours by their distance
   *  from the instance in question. Each entry holds
   *  exp(-((rank(r_i, i_j)/sigma)^2)) where rank(r_i,i_j) is the rank of
   *  instance i_j in a sequence of instances ordered by the distance
   *  from r_i. sigma is a user defined parameter, default=20
   **/
  private double[] m_weightsByRank;
  private int m_sigma;

  /** Weight by distance rather than equal weights */
  private boolean m_weightByDistance;

  /**
   * Returns a string describing this attribute evaluator
   * @return a description of the evaluator suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "ReliefFAttributeEval :\n\nEvaluates the worth of an attribute by "
      +"repeatedly sampling an instance and considering the value of the "
      +"given attribute for the nearest instance of the same and different "
      +"class. Can operate on both discrete and continuous class data.\n";
  }

  /**
   * Constructor
   */
  public ReliefFAttributeEval () {
    resetOptions();
  }


  /**
   * Returns an enumeration describing the available options.
   * @return an enumeration of all the available options.
   **/
  public Enumeration listOptions () {
    Vector newVector = new Vector(4);
    newVector
      .addElement(new Option("\tSpecify the number of instances to\n"
			     + "\tsample when estimating attributes.\n"
			     + "\tIf not specified, then all instances\n"
			     + "\twill be used.", "M", 1
			     , "-M <num instances>"));
    newVector.
      addElement(new Option("\tSeed for randomly sampling instances.\n"
			    + "\t(Default = 1)", "D", 1
			    , "-D <seed>"));
    newVector.
      addElement(new Option("\tNumber of nearest neighbours (k) used\n"
			    + "\tto estimate attribute relevances\n"
			    + "\t(Default = 10).", "K", 1
			    , "-K <number of neighbours>"));
    newVector.
      addElement(new Option("\tWeight nearest neighbours by distance\n", "W"
			    , 0, "-W"));
    newVector.
      addElement(new Option("\tSpecify sigma value (used in an exp\n"
			    + "\tfunction to control how quickly\n"
			    + "\tweights for more distant instances\n"
			    + "\tdecrease. Use in conjunction with -W.\n"
			    + "\tSensible value=1/5 to 1/10 of the\n"
			    + "\tnumber of nearest neighbours.\n"
			    + "\t(Default = 2)", "A", 1, "-A <num>"));
    return  newVector.elements();
  }


  /**
   * Parses a given list of options.
   *
   * Valid options are: <p>
   *
   * -M <number of instances> <br>
   * Specify the number of instances to sample when estimating attributes. <br>
   * If not specified then all instances will be used. <p>
   *
   * -D <seed> <br>
   * Seed for randomly sampling instances. <p>
   *
   * -K <number of neighbours> <br>
   * Number of nearest neighbours to use for estimating attributes. <br>
   * (Default is 10). <p>
   *
   * -W <br>
   * Weight nearest neighbours by distance. <p>
   *
   * -A <sigma> <br>
   * Specify sigma value (used in an exp function to control how quickly <br>
   * weights decrease for more distant instances). Use in conjunction with <br>
   * -W. Sensible values = 1/5 to 1/10 the number of nearest neighbours. <br>
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   *
   **/
  public void setOptions (String[] options)
    throws Exception
  {
    String optionString;
    resetOptions();
    setWeightByDistance(Utils.getFlag('W', options));
    optionString = Utils.getOption('M', options);

    if (optionString.length() != 0) {
      setSampleSize(Integer.parseInt(optionString));
    }

    optionString = Utils.getOption('D', options);

    if (optionString.length() != 0) {
      setSeed(Integer.parseInt(optionString));
    }

    optionString = Utils.getOption('K', options);

    if (optionString.length() != 0) {
      setNumNeighbours(Integer.parseInt(optionString));
    }

    optionString = Utils.getOption('A', options);

    if (optionString.length() != 0) {
      setWeightByDistance(true); // turn on weighting by distance
      setSigma(Integer.parseInt(optionString));
    }
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String sigmaTipText() {
    return "Set influence of nearest neighbours. Used in an exp function to "
      +"control how quickly weights decrease for more distant instances. "
      +"Use in conjunction with weightByDistance. Sensible values = 1/5 to "
      +"1/10 the number of nearest neighbours.";
  }

  /**
   * Sets the sigma value.
   *
   * @param s the value of sigma (> 0)
   * @exception Exception if s is not positive
   */
  public void setSigma (int s)
    throws Exception
  {
    if (s <= 0) {
      throw  new Exception("value of sigma must be > 0!");
    }

    m_sigma = s;
  }


  /**
   * Get the value of sigma.
   *
   * @return the sigma value.
   */
  public int getSigma () {
    return  m_sigma;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String numNeighboursTipText() {
    return "Number of nearest neighbours for attribute estimation.";
  }

  /**
   * Set the number of nearest neighbours
   *
   * @param n the number of nearest neighbours.
   */
  public void setNumNeighbours (int n) {
    m_Knn = n;
  }


  /**
   * Get the number of nearest neighbours
   *
   * @return the number of nearest neighbours
   */
  public int getNumNeighbours () {
    return  m_Knn;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String seedTipText() {
    return "Random seed for sampling instances.";
  }

  /**
   * Set the random number seed for randomly sampling instances.
   *
   * @param s the random number seed.
   */
  public void setSeed (int s) {
    m_seed = s;
  }


  /**
   * Get the seed used for randomly sampling instances.
   *
   * @return the random number seed.
   */
  public int getSeed () {
    return  m_seed;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String sampleSizeTipText() {
    return "Number of instances to sample. Default (-1) indicates that all "
      +"instances will be used for attribute estimation.";
  }

  /**
   * Set the number of instances to sample for attribute estimation
   *
   * @param s the number of instances to sample.
   */
  public void setSampleSize (int s) {
    m_sampleM = s;
  }


  /**
   * Get the number of instances used for estimating attributes
   *
   * @return the number of instances.
   */
  public int getSampleSize () {
    return  m_sampleM;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String weightByDistanceTipText() {
    return "Weight nearest neighbours by their distance.";
  }

  /**
   * Set the nearest neighbour weighting method
   *
   * @param b true nearest neighbours are to be weighted by distance.
   */
  public void setWeightByDistance (boolean b) {
    m_weightByDistance = b;
  }


  /**
   * Get whether nearest neighbours are being weighted by distance
   *
   * @return m_weightByDiffernce
   */
  public boolean getWeightByDistance () {
    return  m_weightByDistance;
  }


  /**
   * Gets the current settings of ReliefFAttributeEval.
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String[] getOptions () {
    String[] options = new String[9];
    int current = 0;

    if (getWeightByDistance()) {
      options[current++] = "-W";
    }

    options[current++] = "-M";
    options[current++] = "" + getSampleSize();
    options[current++] = "-D";
    options[current++] = "" + getSeed();
    options[current++] = "-K";
    options[current++] = "" + getNumNeighbours();
    options[current++] = "-A";
    options[current++] = "" + getSigma();

    while (current < options.length) {
      options[current++] = "";
    }

    return  options;
  }


  /**
   * Return a description of the ReliefF attribute evaluator.
   *
   * @return a description of the evaluator as a String.
   */
  public String toString () {
    StringBuffer text = new StringBuffer();

    if (m_trainTable == null) {
      text.append("ReliefF feature evaluator has not been built yet\n");
    }
    else {
      text.append("\tReliefF Ranking Filter");
      text.append("\n\tTable sampled: ");

      if (m_sampleM == -1) {
        text.append("all\n");
      }
      else {
        text.append(m_sampleM + "\n");
      }

      text.append("\tNumber of nearest neighbours (k): " + m_Knn + "\n");

      if (m_weightByDistance) {
        text.append("\tExponentially decreasing (with distance) "
		    + "influence for\n"
		    + "\tnearest neighbours. Sigma: "
		    + m_sigma + "\n");
      }
      else {
        text.append("\tEqual influence nearest neighbours\n");
      }
    }

    return  text.toString();
  }


  /**
   * Initializes a ReliefF attribute evaluator.
   *
   * @param data set of instances serving as training data
   * @exception Exception if the evaluator has not been
   * generated successfully
   */
  public void buildEvaluator (Table data)
    throws Exception
  {
    int z, totalRows;
    Random r = new Random(m_seed);
  
    int [] inputFeatures = ((ExampleTable)data).getInputFeatures();
	int [] outputFeatures = ((ExampleTable)data).getOutputFeatures();

	if (outputFeatures == null || outputFeatures.length == 0) 
		throw new Exception ( " Class attribute must be specified in ChooseAttributes ");
	
    m_trainTable = data;
    m_classIndex = outputFeatures[0];
    m_numAttribs = m_trainTable.getNumColumns();
    m_numRows = m_trainTable.getNumRows();

	// determine whether class is numeric or nominal
    if (m_trainTable.isColumnScalar(m_classIndex)) {
      m_numericClass = true;
    }
    else {
      m_numericClass = false;
    }

    HashMap classIndices = null;
    HashMap uniqueClassValues = null;
    String [] classNames = null;
    //get number of classes
    if (!m_numericClass) {
      uniqueClassValues = TableUtilities.uniqueValuesWithCounts(m_trainTable,m_classIndex);
      m_numClasses  =uniqueClassValues.size();
      classIndices = new HashMap(m_numClasses) ;
      classNames = new String [m_numClasses];
      Iterator it = uniqueClassValues.keySet().iterator();
      int i = 0;
      String name;
      while(it.hasNext()) {
      	name = (String) it.next();
        classIndices.put(name, new Integer(i));
      	classNames[i] = name;
      	i++;
      }
    }
    else {
      m_ndc = 0;
      m_numClasses = 1;
      m_nda = new double[m_numAttribs];
      m_ndcda = new double[m_numAttribs];
    }

    // set up the rank based weights
    if (m_weightByDistance) {
			m_weightsByRank = new double[m_Knn];
			for (int i = 0; i < m_Knn; i++) {
				m_weightsByRank[i] =
					Math.exp(-((i/(double)m_sigma)*(i/(double)m_sigma)));
			}
    }

    // the final attribute weights
    m_weights = new double[m_numAttribs];
    // num classes (1 for numeric class) knn neighbours,
    // and 0 = distance, 1 = instance index
    m_karray = new double[m_numClasses][m_Knn][2];

    // determine class probabilities
    if (!m_numericClass) {
      m_classProbs = new double[m_numClasses];
      
      //for (int i = 0; i < m_numRows; i++) {
       // m_classProbs[(int)m_trainTable.getDouble(i,m_classIndex)]++;
      
      //}

      //for (int i = 0; i < m_numClasses; i++) {
       // m_classProbs[i] /= m_numRows;
      //}
    	for (int i = 0; i < m_numClasses; i++) {
    		
    	m_classProbs[i] =
    			(((Integer)uniqueClassValues.get(classNames[i])).doubleValue())/m_numRows;
    	//System.out.println("d2k: class probability for i " + m_classProbs[i]);
    	}
    }
    

    m_worst = new double[m_numClasses];
    m_index = new int[m_numClasses];
    m_stored = new int[m_numClasses];
    m_minArray = new double[m_numAttribs];
    m_maxArray = new double[m_numAttribs];
    boolean [] isNumeric = new boolean[m_numAttribs];
    for (int i = 0; i < m_numAttribs; i++) {
      m_minArray[i] = m_maxArray[i] = Double.NaN;
      isNumeric [i] = m_trainTable.isColumnScalar(i);
    }


    double  [] temp =  new double[m_numAttribs];
    boolean [] isMissing = new boolean[m_numAttribs];
   // int m_numColumns = m_trainTable.getNumColumns();
    
    //determine min/max for all numeric columns
    for (int i = 0; i < m_numRows; i++) {
    	for (int j = 0; j < m_numAttribs; j++) {
	       isMissing[j] = m_trainTable.isValueMissing(i,j);
	       if (isNumeric[j]) temp[j]=m_trainTable.getDouble(i,j); //TODO check if set to NAN is needed here
    	}
		updateMinMax(temp,isNumeric,isMissing);
    }

    if ((m_sampleM > m_numRows) || (m_sampleM < 0)) {
      totalRows = m_numRows;
    }
    else {
      totalRows = m_sampleM;
    }

    // process each instance, updating attribute weights
    for (int i = 0; i < totalRows; i++) {
      if (totalRows == m_numRows) {
        z = i;
      }
      else {
        z = r.nextInt()%m_numRows;
      }

      if (z < 0) {
        z *= -1;
      }

      if (!(m_trainTable.isValueMissing(z,m_classIndex))) {
			  // first clear the knn and worst index stuff for the classes
			  for (int j = 0; j < m_numClasses; j++) {
					  m_index[j] = m_stored[j] = 0;
			      for (int k = 0; k < m_Knn; k++) {
						  m_karray[j][k][0] = m_karray[j][k][1] = 0;
						}
				}
      findKHitMiss(z,classIndices);

     // System.out.println("d2k weights for z " + z + " numeric " + m_numericClass);
     //System.out.println(" m_stored " + m_stored[0] + " " + m_stored[1] + " " + m_stored[2]);
     //int cl = ((Integer)classIndices.get(m_trainTable.getString(z,m_classIndex))).intValue();
    // System.out.println("D2K CL is " +  cl);
     
      if (m_numericClass) {
			    updateWeightsNumericClass(z);
        } else {
				  updateWeightsDiscreteClass(z,classIndices);
        }
      // if ( z == 0) {
        //for (int g =0; g < m_weights.length; g ++)
        //	System.out.println("d2k " + m_weights[g]);
       //}
      }
    }
    
    
    
    //System.out.println(m_weights[i]);
    
    // now scale weights by 1/m_numRows (nominal class) or
    // calculate weights numeric class
    // System.out.println("num inst:"+m_numRows+" r_ndc:"+r_ndc);

    for (int i = 0; i < m_numAttribs; i++) {if (i != m_classIndex) {
      if (m_numericClass) {
        m_weights[i] = m_ndcda[i]/m_ndc -
					  ((m_nda[i] - m_ndcda[i])/((double)totalRows - m_ndc));
      } else {
        m_weights[i] *= (1.0/(double)totalRows);
      }
        
    }

    }
  }


  /**
   * Evaluates an individual attribute using ReliefF's instance based approach.
   * The actual work is done by buildEvaluator which evaluates all features.
   *
   * @param attribute the index of the attribute to be evaluated
   * @exception Exception if the attribute could not be evaluated
   */
  public double evaluateAttribute (int attribute)
    throws Exception
  {
    return  m_weights[attribute];
  }


  /**
   * Reset options to their default values
   */
  protected void resetOptions () {
    m_trainTable = null;
    m_sampleM = -1;
    m_Knn = 10;
    m_sigma = 2;
    m_weightByDistance = false;
    m_seed = 1;
  }


  /**
   * Normalizes a given value of a numeric attribute.
   *
   * @param x the value to be normalized
   * @param i the attribute's index
   */
  private double norm (double x, int i) {
    if (Double.isNaN(m_minArray[i]) ||	Utils.eq(m_maxArray[i], m_minArray[i])) {
      return  0;
    }
    else {
      return  (x - m_minArray[i])/(m_maxArray[i] - m_minArray[i]);
    }
  }


  /**
   * Updates the minimum and maximum values for all the attributes
   * based on a new instance.
   *
   * @param instance the new instance
   */
  private void updateMinMax (double [] instance, boolean[] isNumeric, boolean [] isMissing) {
    //    for (int j = 0; j < m_numAttribs; j++) {
    try {
      for (int j = 0; j < instance.length; j++) {
		  	if (isNumeric[j] && (!isMissing[j])) {
				  if (Double.isNaN(m_minArray[j])) {
							m_minArray[j] = instance[j];
							m_maxArray[j] = instance[j];
				  }
					else {
						if (instance[j] < m_minArray[j]) {
							m_minArray[j] = instance[j];
						}
						else {
							if (instance[j] > m_maxArray[j]) {
								m_maxArray[j] = instance[j];
							}
						}
					}
			  }
      }
    } catch (Exception ex) {
      System.err.println(ex);
      ex.printStackTrace();
    }


  }

  /**
   * Computes the difference between two given attribute
   * values.
   */
  private double difference(int index, int ln1, int ln2, int col1, int col2) {

           
			boolean mis1 =  m_trainTable.isValueMissing(ln1,col1);
		    boolean mis2 =  m_trainTable.isValueMissing(ln2,col2);

		    // If attribute is nominal
		    //TODO change here when ReplaceNominalWithInts is phased out
		    if (m_trainTable.isColumnNominal(index)){
		    	String val1 =  m_trainTable.getString(ln1,col1);
		    	String val2 =  m_trainTable.getString(ln2,col2);
		    	
		    	if (mis1 || mis2 ) {
		    		String[] uniquevalues = TableUtilities.uniqueValues(m_trainTable,index);
		    		int numValues = uniquevalues.length;
		    		return (1.0 - (1.0/((double)numValues)));
		    	} else if (!val1.equals( val2)) {
		    		return 1;
		    	} else {
		    		return 0;
		    	}
		    } else if (m_trainTable.isColumnScalar(index)) {
		    	double val1 =  m_trainTable.getDouble(ln1,col1);
		    	double val2 =  m_trainTable.getDouble(ln2,col2);
		    		    	
		    	// If attribute is numeric
		    	if (mis1 || mis2) {
		    		if(mis1 && mis2 ) {
		    			return 1;
		    		} else {
		    			double diff;
		    			if (mis2) {
		    				diff = norm(val1, index);
		    			} else {
		    				diff = norm(val2, index);
		    			}
		    			if (diff < 0.5) {
		    				diff = 1.0 - diff;
		    			}
		    			return diff;
		    		}
		    	} else {
		    		return Math.abs(norm(val1, index) - norm(val2, index));
		    	}
		    }
		    //default:
		    else
		    	return 0;
  }


  /**
   * Calculates the distance between two instances
   *
   * @param test the first instance
   * @param train the second instance
   * @return the distance between the two given instances, between 0 and 1
   */
  private double distance(int first, int second) {

    double distance = 0;
    int firstI, secondI;
	int numAttribs = m_trainTable.getNumColumns();

    for (int p1 = 0, p2 = 0; p1 < numAttribs || p2 < numAttribs;) {
      if (p1 >= numAttribs) {
		  	firstI = numAttribs;
      } else {
			firstI = p1;
      }
      if (p2 >= numAttribs) {
		  	secondI = numAttribs;
      } else {
			  secondI = p2;
      }
      if (firstI == m_classIndex) {
		  	p1++; continue;
      }
      if (secondI == m_classIndex) {
			  p2++; continue;
      }

      double diff;
     
      if (firstI == secondI) {
      	diff = difference(firstI,first,second,p1,p2);
      	p1++; p2++;
      } else if (firstI > secondI) {
      	diff = difference(secondI,0, second, 0, p2);
      	p2++;
      } else {
      	diff = difference(firstI, first, 0, p1, 0);
      	p1++;
      }
      //      distance += diff * diff;
      distance += diff;
    }

    //    return Math.sqrt(distance / m_NumAttributesUsed);
    return distance;
  }


  /**
   * update attribute weights given an instance when the class is numeric
   *
   * @param instNum the index of the instance to use when updating weights
   */
  private void updateWeightsNumericClass (int instNum) {
    int i, j;
    double temp,temp2;
    int[] tempSorted = null;
    double[] tempDist = null;
    double distNorm = 1.0;
    int firstI, secondI;

    double [] inst = new double[m_numAttribs];
    //m_trainTable.getRow(inst,instNum);
    
       for (int k = 0; k< m_numAttribs; k++) 
      		inst[k]=m_trainTable.getDouble(instNum,k);
    
   
    // sort nearest neighbours and set up normalization variable
    if (m_weightByDistance) {
      tempDist = new double[m_stored[0]];

      for (j = 0, distNorm = 0; j < m_stored[0]; j++) {
	// copy the distances
	tempDist[j] = m_karray[0][j][0];
	// sum normalizer
	distNorm += m_weightsByRank[j];
      }

      tempSorted = Utils.sort(tempDist);
    }

		for (i = 0; i < m_stored[0]; i++) {
      // P diff prediction (class) given nearest instances
//		  mis1 = m_trainTable.isValueMissing(instNum, m_classIndex);

		  if (m_weightByDistance) {
		//		mis2 = m_trainTable.isValueMissing(((int)m_karray[0][tempSorted[i]][1],m_classIndex);
//			  temp = difference(m_classIndex,inst[m_classIndex],
	//		  m_trainTable.getInt((int)m_karray[0][tempSorted[i]][1],m_classIndex), mis1, mis2);
			  temp = difference(m_classIndex,instNum,
				       (int)m_karray[0][tempSorted[i]][1],m_classIndex, m_classIndex);
		  	temp *= (m_weightsByRank[i]/distNorm);
      }
      else {
	//			mis2 = m_trainTable.isValueMissing((int)m_karray[0][i][1],m_classIndex);
				//temp = difference(m_classIndex, inst[m_classIndex],
				//		  m_trainTable.getInt((int)m_karray[0][i][1],m_classIndex), mis1,mis2);
			  temp = difference(m_classIndex,instNum,
				       (int)m_karray[0][i][1],m_classIndex, m_classIndex);
				temp *= (1.0/(double)m_stored[0]); // equal influence
      }

      m_ndc += temp;

      int cmpNum;
			double [] cmp = new double[m_numAttribs];
      if (m_weightByDistance) {
				//m_trainTable.getRow(cmp,(int)m_karray[0][tempSorted[i]][1]);
      		for (int k = 0; k< m_numAttribs; k++) 
      			cmp[k]=m_trainTable.getDouble((int)m_karray[0][tempSorted[i]][1],k);
				cmpNum = (int)m_karray[0][tempSorted[i]][1];
      }
      else {
			  //m_trainTable.getRow(cmp,(int)m_karray[0][i][1]);
      	for (int k = 0; k< m_numAttribs; k++) 
      		cmp[k]=m_trainTable.getDouble((int)m_karray[0][i][1],k);
      	
      	cmpNum = (int)m_karray[0][i][1];
      }
      double temp_diffP_diffA_givNearest =
					difference(m_classIndex, instNum, cmpNum, m_classIndex,m_classIndex);
      // now the attributes
      for (int p1 = 0, p2 = 0;p1 < inst.length || p2 < cmp.length;) {
			  if (p1 >= inst.length) {
				  firstI = m_trainTable.getNumColumns();
		  	} else {
		  	  firstI = p1;
			  }
		  	if (p2 >= cmp.length) {
		  	  secondI = m_trainTable.getNumColumns();
		  	} else {
		  	  secondI = p2;
			  }
		  	if (firstI == m_classIndex) {
		  	  p1++; continue;
			  }
		  	if (secondI == m_classIndex) {
		  	  p2++; continue;
		  	}
			temp = 0.0;
			temp2 = 0.0;

		if (firstI == secondI) {
	    j = firstI;
	    temp = difference(j, instNum, cmpNum ,p1,p2);
		  p1++;p2++;
	  } else if (firstI > secondI) {
	    j = secondI;
		  temp = difference(j, 0, cmpNum, 0, p2);
	    p2++;
		} else {
		  j = firstI;
	    temp = difference(j, instNum, 0, p1, 0);
	    p1++;
	  }

		temp2 = temp_diffP_diffA_givNearest * temp;
	// P of different prediction and different att value given
	// nearest instances
		if (m_weightByDistance) {
	    temp2 *= (m_weightsByRank[i]/distNorm);
	  }
	  else {
	    temp2 *= (1.0/(double)m_stored[0]); // equal influence
	  }

		m_ndcda[j] += temp2;

	// P of different attribute val given nearest instances
	if (m_weightByDistance) {
	  temp *= (m_weightsByRank[i]/distNorm);
	}
	else {
	  temp *= (1.0/(double)m_stored[0]); // equal influence
	}

	m_nda[j] += temp;
      }
    }
  }


  /**
   * update attribute weights given an instance when the class is discrete
   *
   * @param instNum the index of the instance to use when updating weights
   */
  private void updateWeightsDiscreteClass (int instNum, HashMap classIndices) {
    int i, j, k;
    int cl;
    double cc = m_numRows;
    double temp, temp_diff, w_norm = 1.0;
    double[] tempDistClass;
    int[] tempSortedClass = null;
    double distNormClass = 1.0;
    double[] tempDistAtt;
    int[][] tempSortedAtt = null;
    double[] distNormAtt = null;
    int firstI, secondI;

    //TODO change here when ReplaceNominalWithInts is phased out
    // store the indexes (sparse instances) of non-zero elements
    
    //double [] inst = new double[m_numAttribs];
    // 	m_trainTable.getRow(inst,instNum);	
    //for (int l = 0; l< m_numAttribs; l++) 
     //inst[l]=m_trainTable.getDouble(instNum,l);
   
    // get the class of this instance
    cl = ((Integer)classIndices.get(m_trainTable.getString(instNum,m_classIndex))).intValue();

    
 //  if (instNum < 1) 
   // 	System.out.println("cl " + cl + " m_weightByDistance " + m_weightByDistance);
    
    // sort nearest neighbours and set up normalization variables
    if (m_weightByDistance) {
      // do class (hits) first
      // sort the distances
      tempDistClass = new double[m_stored[cl]];

      for (j = 0, distNormClass = 0; j < m_stored[cl]; j++) {
      	// copy the distances
      	tempDistClass[j] = m_karray[cl][j][0];
      	// sum normalizer
      	distNormClass += m_weightsByRank[j];
      }
      
      tempSortedClass = Utils.sort(tempDistClass);
      // do misses (other classes)
      tempSortedAtt = new int[m_numClasses][1];
      distNormAtt = new double[m_numClasses];
      
      for (k = 0; k < m_numClasses; k++) {
      	if (k != cl) // already done cl
      	{
      		// sort the distances
      		tempDistAtt = new double[m_stored[k]];
      		
      		for (j = 0, distNormAtt[k] = 0; j < m_stored[k]; j++) {
      			// copy the distances
      			tempDistAtt[j] = m_karray[k][j][0];
      			// sum normalizer
      			distNormAtt[k] += m_weightsByRank[j];
      		}
      		
      		tempSortedAtt[k] = Utils.sort(tempDistAtt);
      	}
      }
    }

    if (m_numClasses > 2) {
      // the amount of probability space left after removing the
      // probability of this instance's class value
      w_norm = (1.0 - m_classProbs[cl]);
    }

    //if (instNum < 1) 
    //	System.out.println("w_norm " + w_norm);
    
    // do the k nearest hits of the same class
    for (j = 0, temp_diff = 0.0; j < m_stored[cl]; j++) {
    	//double [] cmp =  new double[m_numAttribs];
    	int cmpNum = -1;
    	if (m_weightByDistance) {
    		// m_trainTable.getRow(cmp,(int)m_karray[cl][tempSortedClass[j]][1]);
    		//for (int l = 0; l< m_numAttribs; l++) 
    			//cmp[l]=m_trainTable.getDouble((int)m_karray[cl][tempSortedClass[j]][1],l);
    		cmpNum = (int)m_karray[cl][tempSortedClass[j]][1];
    	}
    	else {
    		//m_trainTable.getRow(cmp,p,(int)m_karray[cl][j][1]);
    		//for (int l = 0; l< m_numAttribs; l++) 
    			//cmp[l]=m_trainTable.getDouble((int)m_karray[cl][j][1],l);
    		cmpNum = (int)m_karray[cl][j][1];
    	}
    	
//    	if (instNum < 1) {
  //  		System.out.println("karray cl j " + m_karray[cl][j][1] + " " + cl + " " + j );
    //		System.out.println(" cmp " );
   		
   	//	for (int g =0; g < m_numAttribs; g ++)
    //			System.out.print(m_trainTable.getString((int)m_karray[cl][j][1],g)+ " ");
    //		System.out.println("" );
   	//}
    	
    	for (int p1 = 0, p2 = 0;	p1 < m_numAttribs || p2 < m_numAttribs;) {
    		if (p1 >= m_numAttribs) {
    			firstI = m_trainTable.getNumColumns();
    		} else {
    			firstI = p1;
    		}
    		if (p2 >= m_numAttribs) {
    			secondI = m_trainTable.getNumColumns();
    		} else {
    			secondI = p2;
    		}
    		if (firstI == m_classIndex) {
    			p1++; continue;
    		}
    		if (secondI == m_classIndex) {
    			p2++; continue;
    		}
    		if (firstI == secondI) {
    			i = firstI;
    			temp_diff = difference(i, instNum, cmpNum, p1,p2);
    			p1++;p2++;
    		} else if (firstI > secondI) {
    			i = secondI;
    			temp_diff = difference(i, 0, cmpNum, 0, p2);
    			p2++;
    		} else {
    			i = firstI;
    			temp_diff = difference(i, instNum, 0, p1, 0);
    			p1++;
    		}
    		//if(instNum < 1)
    		//	System.out.println("d2k temp_diff " + temp_diff + " for j, p1, p2 " + j + " " + p1 + " " + p2 );
    		if (m_weightByDistance) {
    			temp_diff *=
    				(m_weightsByRank[j]/distNormClass);
    		} else {
    			if (m_stored[cl] > 0) {
    				temp_diff /= (double)m_stored[cl];
    			}
    		}
    		m_weights[i] -= temp_diff;
    		//if (instNum < 1)
    		 //System.out.println(" i  m _weights[i] " + i + " " + m_weights[i]);
    	}
    }
    
    
    // now do k nearest misses from each of the other classes
    temp_diff = 0.0;
    
    for (k = 0; k < m_numClasses; k++) {
    //	if (instNum < 1) System.out.println("d2k k cl " + k + " " + cl + " " + m_numAttribs);
    	if (k != cl) // already done cl
    	{
    //		if (instNum < 1) System.out.println("d2k k m_stored[k] " + k + " " + m_stored[k]);
    		for (j = 0, temp = 0.0; j < m_stored[k]; j++) {
    //			if (instNum < 1) System.out.println("d2k j k m_stored[k] " + j + " " + k + " " + m_stored[k]);
    			//double [] cmp = new double[m_numAttribs];
    			int cmpNum;
    			if (m_weightByDistance) {
    				//m_trainTable.getRow(cmp,(int)m_karray[k][tempSortedAtt[k][j]][1]);
    				//for (int l = 0; l< m_numAttribs; l++) 
    					//cmp[l]=m_trainTable.getDouble((int)m_karray[k][tempSortedAtt[k][j]][1],l);
    				
    				cmpNum = (int)m_karray[k][tempSortedAtt[k][j]][1];
    			}
    			else {
    				//m_trainTable.getRow(cmp,(int)m_karray[k][j][1]);
    				//for (int l = 0; l< m_numAttribs; l++) 
    				//	cmp[l]=m_trainTable.getDouble((int)m_karray[k][j][1],l);
    				cmpNum = (int)m_karray[k][j][1];
    			}
    			for (int p1 = 0, p2 = 0;
    			p1 < m_numAttribs || p2 < m_numAttribs;) {
    //				if (instNum < 1) System.out.println("d2k p1 p2  " + p1 + " " + p2);
    				if (p1 >= m_numAttribs) {
    					firstI = m_trainTable.getNumColumns();
    				} else {
    					firstI = p1;
    				}
    				if (p2 >= m_numAttribs) {
    					secondI = m_trainTable.getNumColumns();
    				} else {
    					secondI = p2;
    				}
    				if (firstI == m_classIndex) {
    					p1++; continue;
    				}
    				if (secondI == m_classIndex) {
    					p2++; continue;
    				}
    				if (firstI == secondI) {
    					i = firstI;
    					temp_diff = difference(i, instNum, cmpNum,p1,p2);
    					p1++;p2++;
    				} else if (firstI > secondI) {
    					i = secondI;
    					temp_diff = difference(i, 0, cmpNum, 0,p2);
    					p2++;
    				} else {
    					i = firstI;
    					temp_diff = difference(i, instNum, 0, p1, 0);
    					p1++;
    				}
    				
    				if (m_weightByDistance) {
    					temp_diff *=
    						(m_weightsByRank[j]/distNormAtt[k]);
    				}
    				else {
    					if (m_stored[k] > 0) {
    						temp_diff /= (double)m_stored[k];
    					}
    				}
    				
    //				if (instNum < 1)
    //					System.out.println("d2k temp_diff " + temp_diff + " for j, p1, p2 " + j + " " + p1 + " " + p2 );
    				
    				if (m_numClasses > 2) {
    					m_weights[i] += ((m_classProbs[k]/w_norm)*temp_diff);
    				} else {
    					m_weights[i] += temp_diff;
    				}
    			}
    		}
    	}
    }
  }


  /**
   * Find the K nearest instances to supplied instance if the class is numeric,
   * or the K nearest Hits (same class) and Misses (K from each of the other
   * classes) if the class is discrete.
   *
   * @param instNum the index of the instance to find nearest neighbours of
   */
private void findKHitMiss (int instNum, HashMap classIndices) {
    int i, j;
    int cl;
    double ww;
    double temp_diff = 0.0;

	int thisInst = instNum;

    for (i = 0; i < m_numRows; i++) {
			if (i != instNum) {
			  temp_diff = distance(i, thisInst);

			  // class of this training instance or 0 if numeric
			  if (m_numericClass) {
			  	cl = 0;
			  }
			  else { 
			  	//TODO change this when ReplaceNominalsWithInts is phased out
			  	cl = ((Integer)classIndices.get(m_trainTable.getString(i,m_classIndex))).intValue();
			  	//System.out.println("D2K CL for instNum is " + instNum + " " +  cl);
			  	//cl = (int)m_trainTable.getInt(i,m_classIndex);
			  }
			//  if(i == 0) {
			 // 	System.out.println("d2k temp_diff  i thisInst " + temp_diff + " " + i + " " + thisInst);
			 // 	System.out.println("class index " + m_classIndex);
			 // 	System.out.println("d2k cl instNum " + cl + " " + instNum + " " + m_numericClass);
			 // }
			  
			  // add this diff to the list for the class of this instance
			  if (m_stored[cl] < m_Knn) {
			  	m_karray[cl][m_stored[cl]][0] = temp_diff;
			  	m_karray[cl][m_stored[cl]][1] = i;
			  	m_stored[cl]++;
			  	
			  	
			  	// note the worst diff for this class
			  	for (j = 0, ww = -1.0; j < m_stored[cl]; j++) {
			  		if (m_karray[cl][j][0] > ww) {
			  			ww = m_karray[cl][j][0];
			  			m_index[cl] = j;
			  		}
			  	}
			  	
			  	m_worst[cl] = ww;
			  }
			  else
			  	/* if we already have stored knn for this class then check to
			  	see if this instance is better than the worst */
				{
			  		if (temp_diff < m_karray[cl][m_index[cl]][0]) {
			  			m_karray[cl][m_index[cl]][0] = temp_diff;
			  			m_karray[cl][m_index[cl]][1] = i;
			  			
			  			for (j = 0, ww = -1.0; j < m_stored[cl]; j++) {
			  				if (m_karray[cl][j][0] > ww) {
			  					ww = m_karray[cl][j][0];
			  					m_index[cl] = j;
			  				}
			  			}
			  			
			  			m_worst[cl] = ww;
			  		}
			  	}
			}
    }

    
    	//for (int y =0 ; y < m_stored.length; y ++)
    	//	System.out.println("d2k y m_stored " + y + " " + m_stored[y]);
}



}

