package ncsa.d2k.modules.core.prediction.regression.continuous;

//!!!import ncsa.d2k.modules.projects.aydt.prediction.regression.LRModel;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;

public class LinearModel extends Model implements java.io.Serializable
  {
  int     numSelectedInputs;
  int     [] selectedIndices;
  double  [][] weights;
  double  minOutputValue;
  double  maxOutputValue;


  public double [] Evaluate(ExampleTable examples, int e)
    {
    double [] outputs = new double[numOutputs];
    for (int o = 0; o < weights.length; o++)
      {
      double sum = weights[o][numSelectedInputs];
      for (int i = 0; i < numSelectedInputs; i++)
        {
        sum += weights[o][i] * examples.getInputDouble(e, selectedIndices[i]);
        }
      if (sum > maxOutputValue)
        sum = maxOutputValue;
      if (sum < minOutputValue)
        sum = minOutputValue;
      outputs[o] = sum;
      }
    return outputs;
    }

  public void Instantiate(int numInputs, int numOutputs, String [] inputNames, String [] outputNames,
                          boolean [] selectedInputs, double [][] weights,
                          double minOutputValue, double maxOutputValue)
    {

    int    numSelectedInputs = 0;
    int [] selectedInputIndices = new int[numInputs];

    for (int i = 0; i < numInputs; i++)
      {
      if (selectedInputs[i] == true)
        {
        selectedInputIndices[numSelectedInputs] = i;
        numSelectedInputs++;
        }
      }


    this.numInputs         = numInputs;
    this.numOutputs        = numOutputs;
    this.inputNames        = inputNames;
    this.outputNames       = outputNames;
    this.numSelectedInputs = numSelectedInputs;
    this.selectedIndices   = selectedInputIndices;
    this.weights           = (double [][]) weights.clone();
    this.minOutputValue    = minOutputValue;
    this.maxOutputValue    = maxOutputValue;
    }

  public void print(ModelPrintOptions printOptions) throws Exception
    {
    System.out.println("Linear Model");
    System.out.println("minOutputValue          = " + minOutputValue);
    System.out.println("maxOutputValue          = " + maxOutputValue);
    for (int o = 0; o < numOutputs; o++) {
      System.out.println(outputNames[o] + " = " );
      for (int i = 0; i < numSelectedInputs; i++) {
        System.out.println(weights[o][i] + " * " + inputNames[selectedIndices[i]] + " + ");
        }
      System.out.println(weights[o][numSelectedInputs]);
      }
    }

  // Added so that this model can be accessible to classes outside the package and
  // in particular can be output in PMML.  - Ruth
  /*
  public LRModel returnLRModel(int idx) throws Exception {
    LRModel lr = new LRModel();

    lr.setOutputFeatureName( outputNames[idx] );
    lr.setNumRegressors( numSelectedInputs );

    for ( int i = 0; i < numSelectedInputs; i++ ) {
      lr.setInputFeatureName( i, inputNames[selectedIndices[i]] );
      lr.setCoefficient( i, weights[idx][i] );
    }

    lr.setIntercept( weights[idx][numSelectedInputs] );

    return lr;
    }
    */
  }
