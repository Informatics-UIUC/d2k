package ncsa.d2k.modules.core.prediction.regression.continuous;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

import ncsa.d2k.modules.core.datatype.table.*;
public class LinearInducer extends FunctionInducer
  {
  private int        NumRounds = 0;
  public  void    setNumRounds (int value) {       this.NumRounds = value;}
  public  int     getNumRounds ()          {return this.NumRounds;}

  private int     Direction = 0;
  public  void    setDirection (int value) {       this.Direction = value;}
  public  int     getDirection ()             {return this.Direction;}

  private double     MinOutputValue = 0.0;
  public  void    setMinOutputValue (double value) {       this.MinOutputValue = value;}
  public  double  getMinOutputValue ()             {return this.MinOutputValue;}

  private double     MaxOutputValue = 1.0;
  public  void    setMaxOutputValue (double value) {       this.MaxOutputValue = value;}
  public  double  getMaxOutputValue ()             {return this.MaxOutputValue;}


  public String getModuleInfo()
    {
    return "<html>  <head>      </head>  <body>    LinearInducer  </body></html>";
    }
  public String getModuleName()
    {
    return "LinearInducer";
    }

  public void instantiateBias(double [] bias)
    {
    NumRounds       = (int) bias[0];
    Direction       = (int) bias[1];
    MinOutputValue  =       bias[2];
    MaxOutputValue  =       bias[3];
    }

  Object [] computeError(ExampleTable examples, boolean [] selectedInputs) throws Exception
    {

    int numExamples = examples.getNumExamples();
    int numInputs   = examples.getNumInputFeatures();
    int numOutputs  = examples.getNumOutputFeatures();


    int        numSelectedInputs    = 0;
    int     [] selectedInputIndices = new int[numInputs];

    for (int i = 0; i < numInputs; i++)
      {
      if (selectedInputs[i] == true)
        {
        selectedInputIndices[numSelectedInputs] = i;
        numSelectedInputs++;
        }
      }



    double [][] weights = new double[numOutputs][numSelectedInputs + 1];

    for (int outputIndex = 0; outputIndex < numOutputs; outputIndex++)
      {

      /*
      double x[][]   = new double[numExamples][numSelectedInputs + 1];
      double y[]     = new double[numExamples];

      for (int e = 0; e < numExamples; e++)
        {
        for (int i = 0; i < numSelectedInputs; i++)
          {
          x[e][i] = examples.getInputDouble(e, selectedInputIndices[i]);
          }
        x[e][numSelectedInputs] = 1.0;

        y[e] = examples.getOutputDouble(e, outputIndex);
        }
      */

      //                                                       T  -1 T
      // To evaluate b, the computation being carried out is (X X)  X Y
      //

      //Matrix xmat       = new Matrix(x, numExamples, numSelectedInputs + 1);
      //Matrix ymat       = new Matrix(y, numExamples);
      //Matrix xtranspose = null;
      //Matrix xtranspose = new Matrix(numSelectedInputs + 1, numExamples);
      //xtranspose = xmat.transpose();

      if (_Trace)
        System.out.println("Found transpose");

      double [][] firstProductDouble = new double[numSelectedInputs + 1][numSelectedInputs + 1];

      for (int x1 = 0; x1 < numSelectedInputs + 1; x1++) {
        for (int x2 = 0; x2 < numSelectedInputs + 1; x2++) {
          firstProductDouble[x1][x2] = 0;
          for (int row = 0; row < numExamples; row++) {
            double v1;
            if (x1 == numSelectedInputs)
              v1 = 1.0;
            else
              v1 = examples.getInputDouble(row, selectedInputIndices[x1]);
            double v2;
            if (x2 == numSelectedInputs)
              v2 = 1.0;
            else
              v2 = examples.getInputDouble(row, selectedInputIndices[x2]);

            firstProductDouble[x1][x2] += v1 * v2;
          }
        }
      }

      //Matrix firstProduct = null;
      //Matrix firstProduct = new Matrix(numSelectedInputs + 1, numSelectedInputs + 1);
      //firstProduct = xtranspose.times(xmat);

      Matrix firstProduct = new Matrix(firstProductDouble, numSelectedInputs + 1, numSelectedInputs + 1);

      /*

      for (int i1 = 0; i1 < numSelectedInputs + 1; i1++) {
        for (int i2 = 0; i2 < numSelectedInputs + 1; i2++) {
          System.out.println("i1 = " + i1 + "  i2 = " + i2 + "  v = " + firstProduct.get(i1, i2) +
          "(" + firstProductDouble[i1][i2] + ")");
        }
      }
      */

      if (_Trace)
        System.out.println("Found firstproduct");

      boolean exceptionflag = false;

      Matrix firstInv = null;
      //Matrix firstInv = new Matrix(numSelectedInputs + 1, numSelectedInputs + 1);
      if (numSelectedInputs == 0)
        {
        firstInv = new Matrix(1, 1);
        firstInv.set(0, 0, 1.0 / firstProduct.get(0, 0));
        }
      else
        {
        try
          {
          firstInv = firstProduct.inverse();
          }
        catch
          (Exception e)
          {
          exceptionflag = true;
          }
        }

        if (exceptionflag == true) {
          Object [] returnValues = new Object[2];

          Double errorObject = new Double(Double.MAX_VALUE);

          returnValues[0] = null;
          returnValues[1] = errorObject;

          return returnValues;
        }

      if (_Trace)
        System.out.println("Found inverse");

      /*
      for (int i1 = 0; i1 < numSelectedInputs + 1; i1++) {
        for (int i2 = 0; i2 < numSelectedInputs + 1; i2++) {
          System.out.println("i1 = " + i1 + "  i2 = " + i2 + "  v = " + firstInv.get(i1, i2) +
          "(" + firstProductDouble[i1][i2] + ")");
        }
      }
      */

      //Matrix secondProduct;
      //Matrix secondProduct = new Matrix(numSelectedInputs + 1, numExamples);
      //secondProduct = firstInv.times(xtranspose);

      //System.out.println("secondProduct.getRowDimension() = " + secondProduct.getRowDimension());
      //System.out.println("secondProduct.getColumnDimension() = " + secondProduct.getColumnDimension());

      if (_Trace)
        System.out.println("Found secondproduct");


      /*
      double [][] secondProductDouble = new double[numSelectedInputs + 1][numExamples];


      for (int x1 = 0; x1 < numSelectedInputs + 1; x1++) {
        for (int x2 = 0; x2 < numExamples; x2++) {
          secondProductDouble[x1][x2] = 0;
          for (int row = 0; row < numSelectedInputs + 1; row++) {
            double v1;
            v1 = firstInv.get(row, x1);
            double v2;new
            if (row == numSelectedInputs)
              v2 = 1.0;
            else
              v2 = examples.getInputDouble(x2, selectedInputIndices[row]);

            secondProductDouble[x1][x2] += v1 * v2;
          }
        }
      }
      */
/*
      for (int i1 = 0; i1 < numSelectedInputs + 1; i1++) {
        for (int i2 = 0; i2 < 100; i2++) {
          System.out.println("i1 = " + i1 + "  i2 = " + i2 + "  v = " + secondProduct.get(i1, i2) +
          "(" + secondProductDouble[i1][i2] + ")");
        }
      }
*/




      //Matrix thirdProduct;
      //Matrix thirdProduct = new Matrix(numSelectedInputs + 1, 1);
      //thirdProduct = secondProduct.times(ymat);



      double [][] thirdProductDouble = new double[numSelectedInputs + 1][1];


      for (int x1 = 0; x1 < numSelectedInputs + 1; x1++) {
        for (int x2 = 0; x2 < 1; x2++) {
          thirdProductDouble[x1][x2] = 0;
          for (int row = 0; row < numExamples; row++) {
            double secondProductDouble = 0.0;

            for (int row2 = 0; row2 < numSelectedInputs + 1; row2++) {
              double v1;
              v1 = firstInv.get(row2, x1);
              double v2;
              if (row2 == numSelectedInputs)
                v2 = 1.0;
              else
                v2 = examples.getInputDouble(row, selectedInputIndices[row2]);

              secondProductDouble += v1 * v2;
            }


            //v1 = secondProductDouble[x1][row];
            double v1 = secondProductDouble;
            double v2;
            v2 = examples.getOutputDouble(row, outputIndex);
            thirdProductDouble[x1][x2] += v1 * v2;
          }
        }
      }


      /*
      for (int i1 = 0; i1 < numSelectedInputs + 1; i1++) {
          System.out.println("i1 = " + i1 + "  i2 = " + 0 + "  v = " + thirdProduct.get(i1, 0) +
          "(" + thirdProductDouble[i1][0] + ")");
      }
      */












      if (_Trace)
        System.out.println("Found thirdproduct");

      /*
      System.out.println("thirdProduct.getRowDimension() = " + thirdProduct.getRowDimension());
      System.out.println("thirdProduct.getColumnDimension() = " + thirdProduct.getColumnDimension());

      for (int i1 = 0; i1 < numSelectedInputs + 1; i1++) {
          System.out.println("i1 = " + i1 + "  thirdProduct = " + thirdProduct.get(i1, 0) /*+
          "(" + firstProductDouble[i1][i2] + ")");
      }
      */


      double b[] = new double[numSelectedInputs + 1];
      for (int i = 0; i < numSelectedInputs; i++)
        {
        b[i] = thirdProductDouble[i][0];
        weights[outputIndex][i] = b[i];
        if (_Trace)
          System.out.println("w[" + (i + 1) +"] = " + b[i]);
        }
      b[numSelectedInputs] = thirdProductDouble[numSelectedInputs][0];
      weights[outputIndex][numSelectedInputs] = b[numSelectedInputs];
      if (_Trace)
        System.out.println("offset  = " + b[numSelectedInputs]);
      }

    LinearModel model = new LinearModel();
    model.Instantiate(examples.getNumInputFeatures(), examples.getNumOutputFeatures(), examples.getInputNames(), examples.getOutputNames(),
                      selectedInputs, weights,
                      MinOutputValue, MaxOutputValue);

    ErrorFunction errorFunction = new ErrorFunction(ErrorFunction.varianceErrorFunctionIndex);
    double error = errorFunction.evaluate(examples, model);

    //System.out.println("linear error = " + error);

    Object [] returnValues = new Object[2];

    Double errorObject = new Double(error);

    returnValues[0] = model;
    returnValues[1] = errorObject;

    return returnValues;
    }






  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) throws Exception
    {
    int numExamples = examples.getNumExamples();
    int numInputs   = examples.getNumInputFeatures();
    int numOutputs  = examples.getNumOutputFeatures();


    boolean [] selectedInputs = new boolean[numInputs];

    for (int i = 0; i < numInputs; i++)
      {
      if (Direction <= 0)
        selectedInputs[i] = true;
      else
        selectedInputs[i] = false;
      }

    LinearModel bestModel = null;


    if (Direction != 0)
      {
      for (int roundIndex = 0; roundIndex < NumRounds; roundIndex++)
        {
        double bestError = Double.POSITIVE_INFINITY;
        int    bestV = 0;

        for (int v = 0; v < numInputs; v++)
          {
          if ((Direction == -1 && selectedInputs[v]) || (Direction == 1 && !selectedInputs[v]))
            {

            if (Direction == -1)
              selectedInputs[v] = false;
            else
              selectedInputs[v] = true;

            Object [] results = computeError(examples, selectedInputs);

            double error = ((Double) results[1]).doubleValue();

            if (error < bestError)
              {
              bestError = error;
              bestV     = v;
              }

            if (Direction == -1)
              selectedInputs[v] = true;
            else
              selectedInputs[v] = false;
            }
          }

        if (Direction == -1)
          selectedInputs[bestV] = false;
        else
          selectedInputs[bestV] = true;

        }
      }

    Object [] results = computeError(examples, selectedInputs);

    bestModel = (LinearModel) results[0];

    return (Model) bestModel;
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
