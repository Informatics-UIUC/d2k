package ncsa.d2k.modules.core.prediction.instancebased;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.ComputeModule;

public class InstanceBasedInducerOpt extends FunctionInducer
  {
  //int NumBiasParameters = 3;

  private int        P1_NeighborhoodSize = 20;
  public  void    setP1_NeighborhoodSize (int value) {       this.P1_NeighborhoodSize = value;}
  public  int     getP1_NeighborhoodSize ()          {return this.P1_NeighborhoodSize;}

  private double     P2_DistanceWeightingPower = 0.0;
  public  void    setP2_DistanceWeightingPower (double value) {    this.P2_DistanceWeightingPower = value;}
  public  double  getP2_DistanceWeightingPower ()          {return this.P2_DistanceWeightingPower;}

  private double     P3_ZeroDistanceValue = 1E-9;
  public  void    setP3_ZeroDistanceValue (double value) {    this.P3_ZeroDistanceValue = value;}
  public  double  getP3_ZeroDistanceValue ()          {return this.P3_ZeroDistanceValue;}

  public String getModuleInfo()
    {
    return "<html>  <head>      </head>  <body>    InstanceBasedInducerOpt  </body></html>";
    }
  public String getModuleName()
    {
    return "InstanceBasedInducerOpt";
    }

   public void instantiateBias(double [] bias)
    {
    P1_NeighborhoodSize       = (int) bias[0];
    P2_DistanceWeightingPower =       bias[1];
    P3_ZeroDistanceValue      =       bias[2];
    }

   public Model generateModel(ExampleTable examples, ErrorFunction errorFunction)
    {




    int numExamples = examples.getNumExamples();
    int numInputs   = examples.getNumInputFeatures();
    double [] inputMins   = new double[numInputs];
    double [] inputMaxs   = new double[numInputs];
    double [] inputRanges = new double[numInputs];

    for (int v = 0; v < numInputs; v++)
      {
      inputMins[v] = Double.POSITIVE_INFINITY;
      inputMaxs[v] = Double.NEGATIVE_INFINITY;
      }


    for (int e = 0; e < numExamples; e++)
      {
      for (int v = 0; v < numInputs; v++)
        {
        double value = examples.getInputDouble(e, v);
        if (value < inputMins[v])
          inputMins[v] = value;
        if (value > inputMaxs[v])
          inputMaxs[v] = value;
        }
      }

    for (int v = 0; v < numInputs; v++)
      {
      inputRanges[v] = inputMaxs[v] - inputMins[v];
      }





    InstanceBasedModel model = new InstanceBasedModel(examples,
                      inputRanges,
                      P1_NeighborhoodSize,
                      P2_DistanceWeightingPower,
                      P3_ZeroDistanceValue,
                      examples);

    return (Model) model;
    }


  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
  public String getInputName(int index) {
    switch(index) {
      case 0:
        return "input0";
      case 1:
        return "input1";
      default: return "NO SUCH INPUT!";
    }
  }

  /**
   * Return the human readable name of the indexed output.
   * @param index the index of the output.
   * @return the human readable name of the indexed output.
   */
  public String getOutputName(int index) {
    switch(index) {
      case 0:
        return "output0";
      default: return "NO SUCH OUTPUT!";
    }
  }
  }
