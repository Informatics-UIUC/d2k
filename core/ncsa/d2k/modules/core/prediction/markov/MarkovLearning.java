//package working.click;
//package opie;
package ncsa.d2k.modules.core.prediction.markov;

//import ncsa.d2k.dtcheng.classes.*;
//import ncsa.d2k.dtcheng.general.*;
//import ncsa.d2k.dtcheng.io.*;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;


import java.io.*;
import java.util.*;
import java.text.*;



public class MarkovLearning extends ncsa.d2k.infrastructure.modules.ComputeModule implements Serializable
  {

  //////////////////////
  //  INITIALIZATION  //
  //////////////////////

  public void beginExecution()
    {
    }

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private boolean Trace;//    = true;
  public  void    setTrace (boolean value) {       this.Trace = value;}
  public  boolean getTrace ()              {return this.Trace;}


  ////////////////////
  //  INFO METHODS  //
  ////////////////////

  public String getInputInfo (int index)
    {
    String[] inputDescriptions = {"ExampleSetVerticalTable",
		"LearningBiasVerticalTable"};
    return inputDescriptions[index];
    }

  public String getOutputInfo (int index)
    {
    String[] outputDescriptions = {"MarkovModel"};
    return outputDescriptions[index];
    }

  public String getModuleInfo()
    {
    String text = this.getClass().getName();
    return text;
    }


  ////////////////////////
  //  TYPE DEFINITIONS  //
  ////////////////////////

  public String[] getInputTypes()
    {
    String[] temp = {"ncsa.d2k.util.datatype.ExampleTable",
                     "ncsa.d2k.util.datatype.VerticalTable"};
    return temp;
    }

  public String[] getOutputTypes()
    {
    String[] temp = {"ncsa.d2k.modules.core.prediction.markov.MarkovModel"};
    return temp;
    }



/**********************************************************************************************************************************/
/**********************************************************************************************************************************/
/**********************************************************************************************************************************/
/**********************************************************************************************************************************/
/**********************************************************************************************************************************/
/**********************************************************************************************************************************/

  ////////////
  //  MAIN  //
  ////////////



  //VerticalTable cached_transformed_examples_vertical_table;

  public void doit()
    {
    // pull inputs //
    ExampleTable examples_vertical_table = (ExampleTable) this.pullInput(0);
    VerticalTable bias_vertical_table     = (VerticalTable) this.pullInput(1);

    // extract bias parameters */
    int order = (int) bias_vertical_table.getDouble(0, 0);

    if (Trace)
      System.out.println("order = " + order);

    // analyze example vertical table to determine number of examples and input and output features //
    int    numExamples          = examples_vertical_table.getNumRows();
    int    numFeatures          = examples_vertical_table.getNumColumns();
    int    numInputFeatures     = 0;
    int    numOutputFeatures    = 0;
    int [] inputFeatureIndices  = examples_vertical_table.getInputFeatures();//new int[numFeatures];
    int [] outputFeatureIndices = examples_vertical_table.getOutputFeatures();//new int[numFeatures];
	numInputFeatures = inputFeatureIndices.length;
	numOutputFeatures = outputFeatureIndices.length;
    /*for (int f = 0; f < numFeatures; f++)
      {
      Column column = (Column) examples_vertical_table.getColumn(f);
      if (column.getComment().equals("input"))
        {
        inputFeatureIndices[numInputFeatures] = f;
        numInputFeatures++;
        }
      if (column.getComment().equals("output"))
        {
        outputFeatureIndices[numOutputFeatures] = f;
        numOutputFeatures++;
        }
      }
	*/

    if (Trace)
      {
      System.out.println("numExamples        = " + numExamples);
      System.out.println("numFeatures        = " + numFeatures);
      System.out.println("numInputFeatures  = " + numInputFeatures);
      System.out.println("numOutputFeatures = " + numOutputFeatures);
      }


    //////////////////////////////////////////////////
    // find unique values for each nominal variable //
    //////////////////////////////////////////////////

    // find min max and span of each input and output feature

    // initialize input mins and maxs //
    int[] inputValueMins = new int[numInputFeatures];
    int[] inputValueMaxs = new int[numInputFeatures];
    for (int f = 0; f < numInputFeatures; f++)
      {
      inputValueMins[f] = Integer.MAX_VALUE;
      inputValueMaxs[f] = Integer.MIN_VALUE;
      }

    // initialize output mins and maxs //
    int[] outputValueMins = new int[numOutputFeatures];
    int[] outputValueMaxs = new int[numOutputFeatures];
    for (int f = 0; f < numOutputFeatures; f++)
      {
      outputValueMins[f] = Integer.MAX_VALUE;
      outputValueMaxs[f] = Integer.MIN_VALUE;
      }

    // find input mins and maxs //
    for (int f = 0; f < numInputFeatures; f++)
      {
      for (int e = 0; e < numExamples; e++)
        {
        int v = examples_vertical_table.getInt(e, inputFeatureIndices[f]);
        if (v < inputValueMins[f])
          inputValueMins[f] = v;
        if (v > inputValueMaxs[f])
          inputValueMaxs[f] = v;
        }
      }

    // find output mins and maxs //
    for (int f = 0; f < numOutputFeatures; f++)
      {
      for (int e = 0; e < numExamples; e++)
        {
        int v = examples_vertical_table.getInt(e, outputFeatureIndices[f]);
        if (v < outputValueMins[f])
          outputValueMins[f] = v;
        if (v > outputValueMaxs[f])
          outputValueMaxs[f] = v;
        }
      }

    // find input feature spans //
    int [] inputValueSpans = new int[numInputFeatures];
    for (int f = 0; f < numInputFeatures; f++)
      {
      inputValueSpans[f] = (inputValueMaxs[f] - inputValueMins[f]) + 1;
      }

    // find output feature spans //
    int[] outputValueSpans = new int[numOutputFeatures];
    for (int f = 0; f < numOutputFeatures; f++)
      {
      outputValueSpans[f] = (outputValueMaxs[f] - outputValueMins[f]) + 1;
      }

    if (Trace)
      {
      for (int f = 0; f < numInputFeatures; f++)
        {
        System.out.println("inputValueSpans[" + f + "] = " + inputValueSpans[f]);
        System.out.println("inputValueMins [" + f + "] = " + inputValueMins[f]);
        System.out.println("inputValueMaxs [" + f + "] = " + inputValueMaxs[f]);
        }

      for (int f = 0; f < numOutputFeatures; f++)
        {
        System.out.println("outputValueSpans[" + f + "] = " + outputValueSpans[f]);
        System.out.println("outputValueMins [" + f + "] = " + outputValueMins[f]);
        System.out.println("outputValueMaxs [" + f + "] = " + outputValueMaxs[f]);
        }
      }

    // calculate value distributions
    int[][] inputValueDistributions = new int[numInputFeatures][];
    for (int f = 0; f < numInputFeatures; f++)
      {
      inputValueDistributions[f] = new int[inputValueSpans[f]];
      }

    int[][] outputValueDistributions = new int[numOutputFeatures][];
    for (int f = 0; f < numOutputFeatures; f++)
      {
      outputValueDistributions[f] = new int[outputValueSpans[f]];
      }


    // compute input and output feature distributions

    for (int f = 0; f < numInputFeatures; f++)
      {
      for (int e = 0; e < numExamples; e++)
        {
        int v = examples_vertical_table.getInt(e, inputFeatureIndices[f]);
        inputValueDistributions[f][v - inputValueMins[f]]++;
        }
      }

    for (int f = 0; f < numOutputFeatures; f++)
      {
      for (int e = 0; e < numExamples; e++)
        {
        int v = examples_vertical_table.getInt(e, outputFeatureIndices[f]);
        outputValueDistributions[f][v - outputValueMins[f]]++;
        }
      }

    // print distributions

    if (Trace)
      {
      for (int f = 0; f < numInputFeatures; f++)
        {
        for (int v = inputValueMins[f]; v <= inputValueMaxs[f]; v++)
          {
          int count = inputValueDistributions[f][v - inputValueMins[f]];
          if (count > 0)
            {
            System.out.println("inputValueDistributions[" + f + "][" + v + "] = " + count);
            }
          }
        }

      for (int f = 0; f < numOutputFeatures; f++)
        {
        for (int v = outputValueMins[f]; v <= outputValueMaxs[f]; v++)
          {
          int count = outputValueDistributions[f][v - outputValueMins[f]];
          if (count > 0)
            {
            System.out.println("outputValueDistributions[" + f + "][" + v + "] = " + count);
            }
          }
        }
      }


    /*********************************************************************************/
    /* count number of unique values for each input feature and create index mapping */
    /*********************************************************************************/

    int[]   numUniqueInputValues = new int[numInputFeatures];
    int[][] inputValueIndex      = new int[numInputFeatures][];
    int[][] inputIndexValue      = new int[numInputFeatures][];
    for (int f = 0; f < numInputFeatures; f++)
      {
      inputValueIndex[f] = new int[inputValueSpans[f]];
      inputIndexValue[f] = new int[inputValueSpans[f]];
      for (int i = 0; i < inputValueSpans[f]; i++)
        {
        inputValueIndex[f][i] = -1;
        }
      for (int v = inputValueMins[f]; v <= inputValueMaxs[f]; v++)
        {
        int count = inputValueDistributions[f][v - inputValueMins[f]];
        if (count > 0)
          {
          inputValueIndex[f][v - inputValueMins[f]] = numUniqueInputValues[f];
	  inputIndexValue[f][numUniqueInputValues[f]] = v;
          numUniqueInputValues[f]++;
          }
        }
      }

    /**********************************************************************************/
    /* count number of unique values for each output feature and create index mapping */
    /**********************************************************************************/

    int[]   numUniqueOutputValues = new int[numOutputFeatures];
    int[][] outputValueIndex      = new int[numOutputFeatures][];
    int[][] outputIndexValue      = new int[numOutputFeatures][];
    for (int f = 0; f < numOutputFeatures; f++)
      {
      outputValueIndex[f] = new int[outputValueSpans[f]];
      outputIndexValue[f] = new int[outputValueSpans[f]];
      for (int i = 0; i < outputValueSpans[f]; i++)
        {
        outputValueIndex[f][i] = -1;
        }
      for (int v = outputValueMins[f]; v <= outputValueMaxs[f]; v++)
        {
        int count = outputValueDistributions[f][v - outputValueMins[f]];
        if (count > 0)
          {
          outputValueIndex[f][v - outputValueMins[f]] = numUniqueOutputValues[f];
	  outputIndexValue[f][numUniqueOutputValues[f]] = v;
          numUniqueOutputValues[f]++;
          }
        }
      }

    if (Trace)
      {
      for (int f = 0; f < numInputFeatures; f++)
        {
        System.out.println("numUniqueInputValues[" + f + "] = " + numUniqueInputValues[f]);
        }

      for (int f = 0; f < numOutputFeatures; f++)
        {
        System.out.println("numUniqueOutputValues[" + f + "] = " + numUniqueOutputValues[f]);
        }
      }


    /************************************/
    /* determine size of instance space */
    /************************************/

    int inputSpaceSize  = 1;
    int outputSpaceSize = 1;

    for (int f = numInputFeatures - order; f < numInputFeatures; f++)
      {
      inputSpaceSize *= numUniqueInputValues[f];
      }

    for (int f = 0; f < numOutputFeatures; f++)
      {
      outputSpaceSize *= numUniqueOutputValues[f];
      }

    int spaceSize = inputSpaceSize * outputSpaceSize;

    if (Trace)
      {
      System.out.println("order           = " + order);

      System.out.println("inputSpaceSize  = " +  inputSpaceSize);
      System.out.println("outputSpaceSize = " + outputSpaceSize);
      System.out.println("spaceSize       = " + spaceSize);
      }




    // create markov count matrix based on order size

    int[] markovCounts = new int[spaceSize];


    for (int e = 0; e < numExamples; e++)
      {

      int inputIndex = 0;

      for (int f = numInputFeatures - order; f < numInputFeatures; f++)
        {

        inputIndex *= numUniqueInputValues[f];

        int v = examples_vertical_table.getInt(e, inputFeatureIndices[f]);

        inputIndex += (inputValueIndex[f][v - inputValueMins[f]]);

        }

      int outputIndex = 0;
      for (int f = 0; f < numOutputFeatures; f++)
        {
        outputIndex *= numUniqueOutputValues[f];

        int v = examples_vertical_table.getInt(e, outputFeatureIndices[f]);

        outputIndex += (outputValueIndex[f][v - outputValueMins[f]]);

        }

      int combinedIndex = inputIndex * outputSpaceSize + outputIndex;
      //System.out.println("combindedIndex = " + combindedIndex);

      markovCounts[combinedIndex]++;
      }


    MarkovModel model = getMarkovModel();

    model.order                    = order;
	ArrayList transforms = examples_vertical_table.getTransformations();
	if(transforms.size() > 0)
		model.dataTransformer = (MarkovDataTransform)transforms.get(0);

	for(int i = 0; i < outputValueMins.length; i++)
		System.out.println("outputValueMins: "+outputValueMins[i]);
	for(int i = 0; i < outputValueMaxs.length; i++)
		System.out.println("outputValueMaxs: "+outputValueMaxs[i]);

	for(int i = 0; i < inputValueMins.length; i++)
		System.out.println("inputValueMins "+inputValueMins[i]);
	for(int i = 0; i < inputValueMaxs.length; i++)
		System.out.println("inputValueMaxs: "+inputValueMaxs[i]);

    model.name                     = "Markov";
    model.numExamples              = numExamples;
    model.numFeatures              = numFeatures;
    model.numInputFeatures         = numInputFeatures;
    model.numOutputFeatures        = numOutputFeatures;
    model.inputFeatureIndices      = inputFeatureIndices;
    model.outputFeatureIndices     = outputFeatureIndices;
    model.inputValueMins           = inputValueMins;
    model.inputValueMaxs           = inputValueMaxs;
    model.outputValueMins          = outputValueMins;
    model.outputValueMaxs          = outputValueMaxs;
    model.inputValueSpans          = inputValueSpans;
    model.outputValueSpans         = outputValueSpans;
    model.inputValueDistributions  = inputValueDistributions;
    model.outputValueDistributions = outputValueDistributions;
    model.numUniqueInputValues     = numUniqueInputValues;
    model.inputValueIndex          = inputValueIndex;
    model.inputIndexValue          = inputIndexValue;
    model.numUniqueOutputValues    = numUniqueOutputValues;
    model.outputValueIndex         = outputValueIndex;
    model.outputIndexValue         = outputIndexValue;
    model.inputSpaceSize           = inputSpaceSize;
    model.outputSpaceSize          = outputSpaceSize;
    model.spaceSize                = spaceSize;
    model.markovCounts             = markovCounts;

    this.pushOutput(model, 0);
    }

	protected MarkovModel getMarkovModel() {
		return new MarkovModel();
	}

  }
