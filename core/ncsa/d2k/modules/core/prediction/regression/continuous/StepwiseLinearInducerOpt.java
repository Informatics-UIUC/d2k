package ncsa.d2k.modules.core.prediction.regression.continuous;

import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

import ncsa.d2k.modules.core.datatype.table.*;

public class StepwiseLinearInducerOpt
    extends FunctionInducerOpt {

  private int NumRounds = 0;
  private int Direction = 0;

  public String getModuleName() {
    return "StepwiseLinearInducerOpt";
  }

  public String getModuleInfo() {
    return "StepwiseLinearInducerOpt";
  }

  public void instantiateBias(ParameterPoint parameterPoint) {

    double useStepwise = parameterPoint.getValue(0);
    Direction = (int) parameterPoint.getValue(1);
    NumRounds = (int) parameterPoint.getValue(2);

    if (useStepwise < 0.5) {
      NumRounds = 0;
      Direction = 0;
    }

  }

  public void instantiateBiasFromProperties() {
    // Nothing to do in this case since properties are reference directly by the algorithm and no other control
    // parameters need be set.  This may not be the case in general so this stub is left open for future development.
  }

  Object[] computeError(ExampleTable examples, boolean[] selectedInputs) throws
      Exception {

    int numExamples = examples.getNumExamples();
    int numInputs = examples.getNumInputFeatures();
    int numOutputs = examples.getNumOutputFeatures();

    int numSelectedInputs = 0;
    int[] selectedInputIndices = new int[numInputs];

    for (int i = 0; i < numInputs; i++) {
      if (selectedInputs[i] == true) {
        selectedInputIndices[numSelectedInputs] = i;
        numSelectedInputs++;
      }
    }

    double[][] weights = new double[numOutputs][numSelectedInputs + 1];

    for (int outputIndex = 0; outputIndex < numOutputs; outputIndex++) {

      //                                                       T  -1 T
      // To evaluate b, the computation being carried out is (X X)  X Y
      //

      if (_Trace)
        System.out.println("Found transpose");

      double[][] firstProductDouble = new double[numSelectedInputs +
          1][numSelectedInputs + 1];

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

      Matrix firstProduct = new Matrix(firstProductDouble,
                                       numSelectedInputs + 1,
                                       numSelectedInputs + 1);

      if (_Trace)
        System.out.println("Found firstproduct");

      boolean exceptionflag = false;

      Matrix firstInv = null;

      if (numSelectedInputs == 0) {
        firstInv = new Matrix(1, 1);
        firstInv.set(0, 0, 1.0 / firstProduct.get(0, 0));
      }
      else {
        try {
          firstInv = firstProduct.inverse();
        }
        catch
            (Exception e) {
          exceptionflag = true;
        }
      }

      if (exceptionflag == true) {
        Object[] returnValues = new Object[2];

        Double errorObject = new Double(Double.MAX_VALUE);

        returnValues[0] = null;
        returnValues[1] = errorObject;

        return returnValues;
      }

      if (_Trace)
        System.out.println("Found inverse");

      if (_Trace)
        System.out.println("Found secondproduct");

      double[][] thirdProductDouble = new double[numSelectedInputs + 1][1];

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

            double v1 = secondProductDouble;
            double v2;
            v2 = examples.getOutputDouble(row, outputIndex);
            thirdProductDouble[x1][x2] += v1 * v2;
          }
        }
      }

      if (_Trace)
        System.out.println("Found thirdproduct");

      double b[] = new double[numSelectedInputs + 1];
      for (int i = 0; i < numSelectedInputs; i++) {
        b[i] = thirdProductDouble[i][0];
        weights[outputIndex][i] = b[i];
        if (_Trace)
          System.out.println("w[" + (i + 1) + "] = " + b[i]);
      }
      b[numSelectedInputs] = thirdProductDouble[numSelectedInputs][0];
      weights[outputIndex][numSelectedInputs] = b[numSelectedInputs];
      if (_Trace)
        System.out.println("offset  = " + b[numSelectedInputs]);
    }

    StepwiseLinearModel model = new StepwiseLinearModel(examples, selectedInputs, weights);

    ErrorFunction errorFunction = new ErrorFunction(ErrorFunction.
        varianceErrorFunctionIndex);
    double error = errorFunction.evaluate(examples, model);

    Object[] returnValues = new Object[2];

    Double errorObject = new Double(error);

    returnValues[0] = model;
    returnValues[1] = errorObject;

    return returnValues;
  }



  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) throws
      Exception {

    int numExamples = examples.getNumExamples();
    int numInputs = examples.getNumInputFeatures();
    int numOutputs = examples.getNumOutputFeatures();

    boolean[] selectedInputs = new boolean[numInputs];

    for (int i = 0; i < numInputs; i++) {
      if (Direction <= 0)
        selectedInputs[i] = true;
      else
        selectedInputs[i] = false;
    }

    StepwiseLinearModel bestModel = null;

    if (Direction != 0) {
      for (int roundIndex = 0; roundIndex < NumRounds; roundIndex++) {

        System.out.println("roundIndex = " + roundIndex);
        System.out.println("NumRounds = " + NumRounds);
        double bestError = Double.POSITIVE_INFINITY;
        int bestV = 0;

        for (int v = 0; v < numInputs; v++) {
          if ((Direction == -1 && selectedInputs[v]) ||
              (Direction == 1 && !selectedInputs[v])) {

            if (Direction == -1)
              selectedInputs[v] = false;
            else
              selectedInputs[v] = true;

            Object[] results = computeError(examples, selectedInputs);

            double error = ( (Double) results[1]).doubleValue();

            if (error < bestError) {
              bestError = error;
              bestV = v;
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

    Object[] results = computeError(examples, selectedInputs);

    bestModel = (StepwiseLinearModel) results[0];

    return (Model) bestModel;
  }
  }