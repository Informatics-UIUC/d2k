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
 *    Adpated after Discretize.java
 *    Copyright (C) 1999 Eibe Frank,Len Trigg
 *    By Anca Suvaiala
 */



package ncsa.d2k.modules.core.transform.filters;

import java.util.ArrayList;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.transformations.BinTransform;
import ncsa.d2k.modules.core.datatype.table.util.TableUtilities;
import ncsa.d2k.modules.core.transform.binning.BinDescriptor;
import ncsa.d2k.modules.core.transform.binning.NumericBinDescriptor;

/**
 * A filter that discretizes a range of numeric attributes in
 * the dataset into nominal attributes. Discretization is by
 * Fayyad & Irani's MDL method (the default).<p>
 *
 * Valid filter-specific options are: <p>
  *
 * -D <br>
 * Make binary nominal attributes. <p>
 *
 * -E <br>
 * Use better encoding of split point for MDL. <p>
 *
 * -K <br>
 * Use Kononeko's MDL criterion. <p>
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Adapted for D2k by Anca Suvaiala
 */
public class EntropyBinning extends DataPrepModule {

    /** Store the current cutpoints */
  protected double [][] m_CutPoints = null;

  /** Output binary attributes for discretized attributes. */
  protected boolean m_MakeBinary = false;

  /** Use better encoding of split point for MDL. */
  protected boolean m_UseBetterEncoding = false;

  /** Use Kononenko's MDL criterion instead of Fayyad et al.'s */
  protected boolean m_UseKononenko = false;

   /** Constructor - initialises the filter */
  public EntropyBinning() {

    m_CutPoints = null;
  
  }

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return in;
	}

	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.datatype.table.Transformation"};
			
		return out;
	}

	public String getInputInfo(int i) {
		return "ExampleTable containing data that will be binned";
	}

	public String getInputName(int i) {
		return "Example Table";
	}

	public String getOutputInfo(int i) {
		return "Bin transformation that can be applied to the table";
	}

	public String getOutputName(int i) {
		return "Bin Transformation";
	}

	public String getModuleInfo() {
		String s =
			"<p>Overview: Using Entropy calculate cut points to be used in binning data"
			+ "<p>Detailed Description: This module detemines the best cut points for discretizing"
			+ " a range of numeric attributes in the dataset into nominal attributes. Discretization is"
			+ " by  Fayyad & Irani's MDL method (the default). The results is a binning transformation "
			+ " that can be applied on the table to discretize the numeric attributes into nominal ones."
 			+ "<p>Data Type Restrictions: This module can work only on numeric data. All nominal "
			+ " values should be discretized using ReplaceNominalsWithInts transformation before using this module."
			+ "<p>Data Handling: Data is sorted by attributes as the cutPoints are computed.";
			
		return s;
		
	}

	public void doit() throws Exception {
		ExampleTable mt = (ExampleTable)pullInput(0);
                //selectedColumns has to come from a user interface
                int [] selectedColumns = mt.getInputFeatures();
                BinDescriptor[] binDescr = buildBins(mt,selectedColumns);
                BinTransform bt = new BinTransform(mt,binDescr, false);

		pushOutput(bt, 0);
		
	}



/**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {

    String [] options = new String [12];
    int current = 0;

    if (getMakeBinary()) {
      options[current++] = "-D";
    }
    if (getUseBetterEncoding()) {
      options[current++] = "-E";
    }
    if (getUseKononenko()) {
      options[current++] = "-K";
    }
  
   
    while (current < options.length) {
      options[current++] = "";
    }
    return options;
  }


 

  

  /**
   * Gets whether binary attributes should be made for discretized ones.
   *
   * @return true if attributes will be binarized
   */
  public boolean getMakeBinary() {

    return m_MakeBinary;
  }

  /**
   * Sets whether binary attributes should be made for discretized ones.
   *
   * @param makeBinary if binary attributes are to be made
   */
  public void setMakeBinary(boolean makeBinary) {

    m_MakeBinary = makeBinary;
  }

  
  /**
   * Gets whether Kononenko's MDL criterion is to be used.
   *
   * @return true if Kononenko's criterion will be used.
   */
  public boolean getUseKononenko() {

    return m_UseKononenko;
  }

  /**
   * Sets whether Kononenko's MDL criterion is to be used.
   *
   * @param useKon true if Kononenko's one is to be used
   */
  public void setUseKononenko(boolean useKon) {

    m_UseKononenko = useKon;
  }

 

  /**
   * Gets whether better encoding is to be used for MDL.
   *
   * @return true if the better MDL encoding will be used
   */
  public boolean getUseBetterEncoding() {

    return m_UseBetterEncoding;
  }

  /**
   * Sets whether better encoding is to be used for MDL.
   *
   * @param useBetterEncoding true if better encoding to be used.
   */
  public void setUseBetterEncoding(boolean useBetterEncoding) {

    m_UseBetterEncoding = useBetterEncoding;
  }

  
 
 
  /**
   * Gets the cut points for an attribute
   *
   * @param the index (from 0) of the attribute to get the cut points of
   * @return an array containing the cutpoints (or null if the
   * attribute requested isn't being Discretized
   */
  public double [] getCutPoints(int attributeIndex) {

    if (m_CutPoints == null) {
      return null;
    }
    return m_CutPoints[attributeIndex];
  }

  /** Generate the cutpoints for each attribute */
  protected void calculateCutPoints(ExampleTable mt,int[] selectedColumns) {

    ExampleTable copy = null;

    int j;
    m_CutPoints = new double [selectedColumns.length] [];

    // Compute class counts.
    String[] classCounts =
      TableUtilities.uniqueValues(mt,mt.getOutputFeatures()[0]);
    int numClasses = classCounts.length;

    for(int i = selectedColumns.length -1; i >= 0; i--) {
      j = selectedColumns[i];
      if ((mt.isColumnScalar(j))) {
       //System.out.println("calculating cut points for column "  + j);
	// Use copy to preserve order
	if (copy == null) {
	  copy =(ExampleTable) mt.copy();
	}
	calculateCutPointsByMDL(j, copy, numClasses);
      }
    }
  }

  /**
   * Set cutpoints for a single attribute using MDL.
   *
   * @param index the index of the attribute to set cutpoints for
   */
  protected void calculateCutPointsByMDL(int index,
					 ExampleTable data, int numClasses) {

    // Sort instances
    data.sortByColumn(index);

    // Find first instances that's missing
    int firstMissing = data.getNumRows();
    for (int i = 0; i < data.getNumRows(); i++) {
      if (data.isValueMissing(i,index)) {
        firstMissing = i;
        break;
      }
    }

   // System.out.println("index " + index + "firstMissing " + firstMissing);

    m_CutPoints[index] = cutPointsForSubset(data, index, 0, firstMissing, numClasses);
   // if (m_CutPoints[index]!=null){
    //System.out.println("column " + index + " m_cutPoints are");
    // for (int i =0; i < m_CutPoints[index].length; i++)
     //System.out.print(m_CutPoints[index][i]+ ",");
     //System.out.println(" ");
    //}
    //else
    //System.out.println("column " + index + " m_cutPoints are null");


  }

  /** Test using Kononenko's MDL criterion. */
  private boolean KononenkosMDL(double[] priorCounts,
				double[][] bestCounts,
				double numInstances,
				int numCutPoints) {

    double distPrior, instPrior, distAfter = 0, sum, instAfter = 0;
    double before, after;
    int numClassesTotal;

    // Number of classes occuring in the set
    numClassesTotal = 0;
    for (int i = 0; i < priorCounts.length; i++) {
      if (priorCounts[i] > 0) {
	numClassesTotal++;
      }
    }

    // Encode distribution prior to split
    distPrior = SpecialFunctions.log2Binomial(numInstances
					      + numClassesTotal - 1,
					      numClassesTotal - 1);

    // Encode instances prior to split.
    instPrior = SpecialFunctions.log2Multinomial(numInstances,
						 priorCounts);

    before = instPrior + distPrior;

    // Encode distributions and instances after split.
    for (int i = 0; i < bestCounts.length; i++) {
      sum = Utils.sum(bestCounts[i]);
      distAfter += SpecialFunctions.log2Binomial(sum + numClassesTotal - 1,
						 numClassesTotal - 1);
      instAfter += SpecialFunctions.log2Multinomial(sum,
						    bestCounts[i]);
    }

    // Coding cost after split
    after = Utils.log2(numCutPoints) + distAfter + instAfter;

    // Check if split is to be accepted
    return (Utils.gr(before, after));
  }


  /** Test using Fayyad and Irani's MDL criterion. */
  private boolean FayyadAndIranisMDL(double[] priorCounts,
				     double[][] bestCounts,
				     double numInstances,
				     int numCutPoints) {

    double priorEntropy, entropy, gain;
    double entropyLeft, entropyRight, delta;
    int numClassesTotal, numClassesRight, numClassesLeft;
  //  System.out.println("Entered Fayyad: numInstances " + numInstances + " numCutPoints " + numCutPoints);
    //System.out.println("uses Fayyad ");
    /*for (int i =0; i < priorCounts.length; i++)
     System.out.print(priorCounts[i] + " ");
    System.out.println("");
    System.out.println("bestCounts 0 ");
    for (int i =0; i < bestCounts[0].length; i++)
    	System.out.print(bestCounts[0][i] + " ");
    System.out.println("");
    System.out.println("bestCounts 1 ");
    for (int i =0; i < bestCounts[1].length; i++)
    	System.out.print(bestCounts[1][i] + " ");
    System.out.println("");
    */
    // Compute entropy before split.
    priorEntropy = ContingencyTables.entropy(priorCounts);

    // Compute entropy after split.
    entropy = ContingencyTables.entropyConditionedOnRows(bestCounts);

    // Compute information gain.
    gain = priorEntropy - entropy;

    // Number of classes occuring in the set
    numClassesTotal = 0;
    for (int i = 0; i < priorCounts.length; i++) {
      if (priorCounts[i] > 0) {
	numClassesTotal++;
      }
    }

    // Number of classes occuring in the left subset
    numClassesLeft = 0;
    for (int i = 0; i < bestCounts[0].length; i++) {
      if (bestCounts[0][i] > 0) {
	numClassesLeft++;
      }
    }

    // Number of classes occuring in the right subset
    numClassesRight = 0;
    for (int i = 0; i < bestCounts[1].length; i++) {
      if (bestCounts[1][i] > 0) {
	numClassesRight++;
      }
    }

    // Entropy of the left and the right subsets
    entropyLeft = ContingencyTables.entropy(bestCounts[0]);
    entropyRight = ContingencyTables.entropy(bestCounts[1]);

    // Compute terms for MDL formula
    delta = Utils.log2(Math.pow(3, numClassesTotal) - 2) -
      (((double) numClassesTotal * priorEntropy) -
       (numClassesRight * entropyRight) -
       (numClassesLeft * entropyLeft));

   // System.out.println("Fayad  gain " + gain + " delta " + delta);
    // Check if split is to be accepted
    return (gain > (Utils.log2(numCutPoints) + delta) / (double)numInstances);
  }


  /** Selects cutpoints for sorted subset. */
  private double[] cutPointsForSubset(ExampleTable instances, int attIndex,
				      int first, int lastPlusOne, int numClasses) {

  	
    double[][] counts, bestCounts;
    double[] priorCounts, left, right, cutPoints;
    double currentCutPoint = -Double.MAX_VALUE, bestCutPoint = -1,
      currentEntropy, bestEntropy, priorEntropy, gain;
    int bestIndex = -1, numInstances = 0, numCutPoints = 0;

    // Compute number of instances in set
    if ((lastPlusOne - first) < 2) {
      return null;
    }

    //System.out.println("cutPointsForSubset: index " + attIndex);
    counts = new double[2][numClasses];
    int classIndex = instances.getOutputFeatures()[0];
    for (int i = first; i < lastPlusOne; i++) {
    //BEFORE    numInstances += instances.instance(i).weight();
     // counts[1][(int)instances.instance(i).classValue()] +=
    //	instances.instance(i).weight();
      numInstances += 1;
      counts[1][(int)instances.getInt(i,classIndex)] += 1;
    }

    // Save prior counts
    priorCounts = new double[numClasses];
    System.arraycopy(counts[1], 0, priorCounts, 0,numClasses);

    // Entropy of the full set
    priorEntropy = ContingencyTables.entropy(priorCounts);
    bestEntropy = priorEntropy;

    // Find best entropy.
    bestCounts = new double[2][numClasses];
    for (int i = first; i < (lastPlusOne - 1); i++) {
      //BEFORE counts[0][(int)instances.instance(i).classValue()] +=
	//instances.instance(i).weight();
      //counts[1][(int)instances.instance(i).classValue()] -=
	//instances.instance(i).weight();

        counts[0][(int)instances.getInt(i,classIndex)] += 1;
        counts[1][(int)instances.getInt(i,classIndex)] -= 1;
      if (instances.getDouble(i,attIndex)<
		   instances.getDouble(i+1,attIndex)) {
     // 	System.out.println("i attIndex " + i + " " + attIndex);
      //	System.out.println("instances " + instances.getDouble(i, attIndex) + " " + instances.getDouble(i+1,attIndex));
      	currentCutPoint = (instances.getDouble(i,attIndex) + instances.getDouble(i+1,attIndex))/2.0;
	
	currentEntropy = ContingencyTables.entropyConditionedOnRows(counts);
	
	if (currentEntropy < bestEntropy) {
	  bestCutPoint = currentCutPoint;
	  bestEntropy = currentEntropy;
	  bestIndex = i;
	
	  System.arraycopy(counts[0], 0,  bestCounts[0], 0, numClasses);
	  System.arraycopy(counts[1], 0,  bestCounts[1], 0, numClasses);
	}
	numCutPoints++;
      }
    }
    

    // Use worse encoding?
    if (!m_UseBetterEncoding) {
      numCutPoints = (lastPlusOne - first) - 1;
     // System.out.println("CutPointsForSubset: inside if numCutPoints " + numCutPoints);
    }

    // Checks if gain is zero
    gain = priorEntropy - bestEntropy;
    if (Utils.eq(gain, 0)) {
     // System.out.println("no gain for column " + attIndex);
     // System.out.println("first - lastPlusOne " + first + "-" + lastPlusOne);
      return null;
    }

    // Check if split is to be accepted
    if ((m_UseKononenko && KononenkosMDL(priorCounts, bestCounts,
					 numInstances, numCutPoints)) ||
	(!m_UseKononenko && FayyadAndIranisMDL(priorCounts, bestCounts,
					       numInstances, numCutPoints))) {

      // Select split points for the left and right subsets
      left = cutPointsForSubset(instances, attIndex, first, bestIndex + 1,numClasses);
      right = cutPointsForSubset(instances, attIndex,
				 bestIndex + 1, lastPlusOne, numClasses);
/*      System.out.println("CutPointsForSubset: attIndex " + attIndex +" left " + left + " right " + right);
      if ( left != null ) {
      	System.out.println(" left ");
      	for (int i=0 ; i <left.length; i ++)
      		System.out.print(left[i] + " ");
      	System.out.println("");
      }
      
      if ( right != null ) {
      	System.out.println(" right ");
      	for (int i=0 ; i <right.length; i ++)
      		System.out.print(right[i] + " ");
      	System.out.println("");
      }
  */    
      // Merge cutpoints and return them
      if ((left == null) && (right) == null) {
      	cutPoints = new double[1];
      	cutPoints[0] = bestCutPoint;
      } else if (right == null) {
	cutPoints = new double[left.length + 1];
	System.arraycopy(left, 0, cutPoints, 0, left.length);
	cutPoints[left.length] = bestCutPoint;
      } else if (left == null) {
	cutPoints = new double[1 + right.length];
	cutPoints[0] = bestCutPoint;
	System.arraycopy(right, 0, cutPoints, 1, right.length);
      } else {
	cutPoints = new double[left.length + right.length + 1];
	System.arraycopy(left, 0, cutPoints, 0, left.length);
	cutPoints[left.length] = bestCutPoint;
	System.arraycopy(right, 0, cutPoints, left.length + 1, right.length);
      }

      return cutPoints;
    } else
      return null;
  }

  /**
   * Sets the bin descriptors. Computes the cutpoints and
   * builds the corresponding BinDescriptors
   */

 public BinDescriptor[] buildBins(ExampleTable mt, int[] selectedColumns) {

    int len = selectedColumns.length;
    

  //  mt.setClassIndex(mt.getNumColumns()-1); // get this out latter - for testing only
  
    if (mt.getOutputFeatures()[0] < 0) {
      System.out.println("Cannot use class-based discretization: "
					 + "no class assigned to the dataset");
       return null;
    }
    if (!mt.isColumnNominal(mt.getOutputFeatures()[0])) {
      System.out.println("Supervised discretization not possible:"
					      + " class is not nominal!");
      return null;
    }

    if (m_CutPoints == null)
        calculateCutPoints(mt,selectedColumns);


    ArrayList binDescriptors = new ArrayList();
    int classIndex = mt.getOutputFeatures()[0];
    for(int i = 0; i < mt.getNumColumns(); i++) {
      if ((mt.isColumnScalar(i))) {
        if (m_CutPoints[i]==null) { // supervized discretization failed  - use one bin
            binDescriptors.add(new NumericBinDescriptor(i,"'(-inf,-inf)'",
            -Double.MAX_VALUE,Double.MAX_VALUE, mt.getColumnLabel(i)));
        } else	if (!m_MakeBinary) {
          //System.out.println("column discretize " + i + " " +mt.getColumnLabel(i) + m_CutPoints[i]);
           for(int j = 0; j <= m_CutPoints[i].length; j++) {
	      if (j == 0) {
		binDescriptors.add(new NumericBinDescriptor(i,"'(-inf-"
			+ Utils.doubleToString(m_CutPoints[i][j], 6) + "]'",
                        -Double.MAX_VALUE, m_CutPoints[i][j],mt.getColumnLabel(i)));
	      } else if (j == m_CutPoints[i].length) {
		binDescriptors.add(new NumericBinDescriptor(i,"'("
			+ Utils.doubleToString(m_CutPoints[i][j - 1], 6)
                        + "-inf)'",m_CutPoints[i][j-1],Double.MAX_VALUE,
                          mt.getColumnLabel(i)));
	      } else {
		binDescriptors.add(new NumericBinDescriptor(i,"'("
			+ Utils.doubleToString(m_CutPoints[i][j - 1], 6) + "-"
			+ Utils.doubleToString(m_CutPoints[i][j], 6) + "]'",
                        m_CutPoints[i][j-1], m_CutPoints[i][j],
                          mt.getColumnLabel(i)));
	  	    }
          }
	} else {
	         for(int j = 0; j < m_CutPoints[i].length; j++) {
	      binDescriptors.add(new NumericBinDescriptor(i,"'(-inf-"
		      + Utils.doubleToString(m_CutPoints[i][j], 6) + "]'",
                        Double.MIN_VALUE, m_CutPoints[i][j],mt.getColumnLabel(i)));
	      binDescriptors.add(new NumericBinDescriptor(i,"'("
		      + Utils.doubleToString(m_CutPoints[i][j], 6) + "-inf)'",
                      m_CutPoints[i][j],Double.MAX_VALUE,mt.getColumnLabel(i)));
	    }
	}
      }
    }

   len = binDescriptors.size();
    BinDescriptor[] bd = new BinDescriptor[len];
    Object[] descriptorArray = binDescriptors.toArray();
    for (int i = 0; i < len ; i ++)
      bd[i] = (BinDescriptor) descriptorArray[i];
    return bd;
  }


/*  public boolean transform(ExampleTable mt) {

    int numAttributes  = mt.getNumColumns() -1;
      double [] instance = new double[mt.getNumColumns()];


      for (int k =0; k < mt.getNumEntries(); k ++) {
        mt.getRow(instance, k);

        int index = 0;
             double [] vals = new double [outputFormatPeek().numAttributes()];

    // Copy and convert the values
    for(int i = 0; i < numAttributes; i++) {
      if (m_DiscretizeCols.isInRange(i) &&
	  mt.isColumnNumeric(i)) {
	int j;
	double currentVal = instance[i];
	if (m_CutPoints[i] == null) {
	  if (mt.isValueMissing(k,i)) {
	    vals[index] = (mt.getScalarMissingValue(i)).doubleValue();
	  } else {
	    vals[index] = 0;
	  }
	  index++;
	} else {
	  if (!m_MakeBinary) {
	    if (mt.isValueMissing(k,i)) {
	      vals[index] =(mt.getScalarMissingValue(i)).doubleValue();
	    } else {
	      for (j = 0; j < m_CutPoints[i].length; j++) {
		if (currentVal <= m_CutPoints[i][j]) {
		  break;
		}
	      }
              vals[index] = j;
	    }
	    index++;
	  } else {
	    for (j = 0; j < m_CutPoints[i].length; j++) {
	      if (mt.isValueMissing(k,i)) {
                vals[index] = (mt.getScalarMissingValue(i)).doubleValue();
	      } else if (currentVal <= m_CutPoints[i][j]) {
                vals[index] = 0;
	      } else {
                vals[index] = 1;
	      }
	      index++;
	    }
	  }
	}
      } else {
        vals[index] = instance[i];
	index++;
      }
    }

*/
    /*Instance inst = null;
    if (instance instanceof SparseInstance) {
      inst = new SparseInstance(instance.weight(), vals);
    } else {
      inst = new Instance(instance.weight(), vals);
    }
    copyStringValues(inst, false, instance.dataset(), getInputStringIndex(),
                     getOutputFormat(), getOutputStringIndex());
    inst.setDataset(getOutputFormat());
      */
  //    }
//  }

 public PropertyDescription[] getPropertiesDescriptions() {
 	PropertyDescription[] pds = new PropertyDescription[3];
 	pds[0] =
 		new PropertyDescription(
 				"m_MakeBinary",
				"Binary Discretization",
				"Makes resulting attributes binary.");
 	pds[1] =
 		new PropertyDescription(
 				"m_UseKononenko",
				"Use Kononenko's MDL",
				"Use Kononenko's MDL criterion. If set to false uses the Fayyad & Irani criterion..");
 	pds[2] =
 		new PropertyDescription(
 				"m_useBetterEncoding",
				"Use Better Encoding",
				"Uses a more efficient split point encoding.");
 	return pds;
 }
}








