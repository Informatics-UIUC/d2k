package ncsa.d2k.modules.core.prediction.mean.continuous;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import java.text.*;

public class MeanModel extends Model implements java.io.Serializable
  {
  double [] meanOutputValues;


  // this is a dummy input since mean model does not have any; added only for consistancy
  public double [] Evaluate(double [] inputs) {

    double [] outputs = new double[meanOutputValues.length];
    for (int f = 0; f < meanOutputValues.length; f++) {
      outputs[f] = meanOutputValues[f];
      }
    return outputs;
    }

  public double [] Evaluate(ExampleTable exampleSet, int e) {
    double [] outputs = new double[exampleSet.getNumOutputFeatures()];
    for (int f = 0; f < meanOutputValues.length; f++) {
      outputs[f] = meanOutputValues[f];
      }
    return outputs;
    }

  public void Evaluate(ExampleTable exampleSet, int e, double [] outputs) {
    for (int f = 0; f < meanOutputValues.length; f++) {
      outputs[f] = meanOutputValues[f];
      }
    }

  public void Instantiate(int numInputs, int numOutputs, String [] inputNames, String [] outputNames, double [] meanOutputValues) {
    this.numInputs = numInputs;
    this.numOutputs = numOutputs;
    this.inputNames = inputNames;
    this.outputNames = outputNames;
    this.meanOutputValues = (double []) meanOutputValues.clone();
    }

  DecimalFormat Format = new DecimalFormat();
  public void print(ModelPrintOptions options) {

    Format.setMaximumFractionDigits(options.MaximumFractionDigits);

    for (int i = 0; i < numOutputs; i++) {
      if (i > 0)
        System.out.print("  ");
      System.out.print(outputNames[i] + " = " + Format.format(this.meanOutputValues[i]));
    }
    //System.out.println();
  }

  }
