//package working.click;
//package opie;
package ncsa.d2k.modules.core.prediction.markov;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

// should be renamed RoteMemory or TABLE LOOKUP or LITERAL KEY or EXACT_MATCH or EPISODIC ...
public class MarkovModel extends ModelModule implements java.io.Serializable
  {
  public String   name;

  public MarkovDataTransform dataTransformer;

  public int     order;

  public int     numExamples;

  public int     numFeatures;

  public int     numInputFeatures;      // number of example input  features (not necessarily the number used in model)
  public int     numOutputFeatures;     // number of example output features (must be 1!)

  public int[]   inputFeatureIndices;
  public int[]   outputFeatureIndices;

  public int[]   inputValueMins;
  public int[]   inputValueMaxs;
  public int[]   outputValueMins;
  public int[]   outputValueMaxs;

  public int[]   inputValueSpans;
  public int[]   outputValueSpans;

  public int     outputSpaceSize;
  public int     inputSpaceSize;
  public int     spaceSize;

  public int[][] inputValueDistributions;
  public int[][] outputValueDistributions;

  public int[]   numUniqueInputValues;
  public int[][] inputValueIndex;
  public int[][] inputIndexValue;

  public int[]   numUniqueOutputValues;
  public int[][] outputValueIndex;
  public int[][] outputIndexValue;

  public int[]   markovCounts;


	public int getOrder() {
		return order;
	}

	public int getNumExamples() {
		return numExamples;
	}

	public int getNumOutputFeatures() {
		return numOutputFeatures;
	}

	public int getOutputSpaceSize() {
		return outputSpaceSize;
	}

	public int getNumInputFeatures() {
		return numInputFeatures;
	}


  public String getModuleInfo()
    {
    StringBuffer sb = new StringBuffer("MarkovModelModule");
    return sb.toString();
    }

  public String getModuleName()
    {
    return "MarkovModel";
    }

  public String[] getInputTypes()
    {
    String [] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    //String [] in = {"java.lang.Object"};
    return in;
    }

  public String[] getOutputTypes()
    {
    //String [] out = {"java.lang.Object"};
	String [] out = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return out;
    }

  public String getInputInfo(int i)
    {
    if (i == 0)
      return "A Table.";
    return "no such input!";
    }

  public String getInputName(int i)
    {
    if (i ==0)
      return "table";
    return "no such input!";
    }

  public String getOutputInfo(int i)
    {
    if (i == 0)
      {
      StringBuffer sb = new StringBuffer("The test table.  This table ");
      return sb.toString();
      }
    return "no such output";
    }

  public String getOutputName(int i)
    {
    if (i == 0)
      return "testingTable";
    return "no such output.";
    }

  public void doit()
    {
		ExampleTable et = (ExampleTable)pullInput(0);

		Column[] predCols = new Column[outputSpaceSize*2];
		int numRows = et.getNumRows();
		int[] outputMapping = getOutputMapping();

		for(int i = 0; i < outputSpaceSize; i++) {
			predCols[2*i] = new DoubleColumn(numRows);
			predCols[2*i].setLabel("Probability"+i);
			predCols[(2*i)+1] = new StringColumn(numRows);
			predCols[(2*i)+1].setLabel("OutputValue"+i);
		}

		// for each row, make a prediction
		for(int i = 0; i < numRows; i++) {
			int [] toPredict = new int[numInputFeatures];
			for(int j = 0; j < numInputFeatures; j++) {
				toPredict[j] = dataTransformer.stringToInt(et.getString(i, j));
			}
			double [] preds = (double[])predict(toPredict);

			for(int k = 0; k < preds.length; k++) {
				predCols[k*2].setDouble(preds[k], i);
				predCols[(k*2)+1].setString(dataTransformer.intToString(
					outputMapping[k]), i);
			}
		}

		for(int i = 0; i < predCols.length; i++)
			et.addColumn(predCols[i]);
		pushOutput(et, 0);
    }

  public Object predict(Object objectInput)
    {
    int [] input = (int []) objectInput;

    /***************************************/
    /* calculate markcovCounts array index */
    /***************************************/

    int     inputSpaceIndex   = 0;
    boolean inputIndexFailure = false;

    // Note: the model order determines the number of features used

    for (int f = this.numInputFeatures - this.order; f < this.numInputFeatures; f++)
      {

      inputSpaceIndex *= this.numUniqueInputValues[f];

      int v = input[f];

      if (v < this.inputValueMins[f] || v > this.inputValueMaxs[f])
        {
        inputIndexFailure = true;
        break;
        }

      int subIndex = this.inputValueIndex[f][v - this.inputValueMins[f]];

      if (subIndex < 0)
        {
        inputIndexFailure = true;
        break;
        }

      inputSpaceIndex += subIndex;

      }

    int inputIndexAdditive = inputSpaceIndex * outputSpaceSize;


    // not so efficient to allocate each time
    double outputSpaceProbs[] = new double[outputSpaceSize]; // init to 0.0

    // sum the counts in output space //
    if (!inputIndexFailure)
      {
      int countSum = 0;

      for (int outputSpaceIndex = 0; outputSpaceIndex < outputSpaceSize; outputSpaceIndex++)
        {
        countSum += this.markovCounts[inputIndexAdditive + outputSpaceIndex];
        }

      for (int outputSpaceIndex = 0; outputSpaceIndex < outputSpaceSize; outputSpaceIndex++)
        {
        int count = this.markovCounts[inputIndexAdditive + outputSpaceIndex];

	double countOffset = (1.0 / (double) outputSpaceSize);
        double prob = ((double) count + countOffset) / (double) (countSum + 1.0);

        outputSpaceProbs[outputSpaceIndex] = prob;
        }
      }

    return outputSpaceProbs;
    }

  public int [] getOutputMapping()
    {
    int [] mapping = new int[this.numUniqueOutputValues[0]];

    for (int i = 0; i < this.numUniqueOutputValues[0]; i++)
      {
      mapping[i] = this.outputIndexValue[0][i];
      }
    return mapping;
    }
  }

