package ncsa.d2k.modules.core.datatype.table.continuous;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.continuous.*;

public class DoubleExample implements Example, java.io.Serializable
  {
  int numInputs;
  int numOutputs;
  String [] inputNames;
  String [] outputNames;
  double [][] data;


  public DoubleExample()
    {
    }

  public DoubleExample(double [][] data)
    {
    this.data        = data;
    }

  public DoubleExample(double [][] data, int numInputs, int numOutputs, String [] inputNames, String [] outputNames)
    {
    this.data        = data;
    this.numInputs   = numInputs;
    this.numOutputs  = numOutputs;

    this.inputNames  = inputNames;
    this.outputNames = outputNames;
    }

  public DoubleExample(ExampleTable exampleSet, int exampleIndex)
    {
    this.numInputs   = exampleSet.getNumInputFeatures();
    this.numOutputs  = exampleSet.getNumOutputFeatures();

    this.inputNames  = exampleSet.getInputNames();
    this.outputNames = exampleSet.getOutputNames();

    data = new double[2][];
    double [] inputData  = new double[numInputs];
    double [] outputData = new double[numOutputs];
    data[0] = inputData;
    data[1] = outputData;

    for (int i = 0; i < numInputs; i++)
      {
      inputData[i] = (double) exampleSet.getInputDouble(exampleIndex, i);
      }
    for (int i = 0; i < numOutputs; i++)
      {
      outputData[i] = (double) exampleSet.getOutputDouble(exampleIndex, i);
      }

    }

  public double getInputDouble(int i) {
    return data[0][i];
    }

  public double getOutputDouble(int i) {
    return data[1][i];
    }

  public void setInputDouble(int i, double value)
    {
    data[0][i] = value;
    }

  public void setOutputDouble(int i, double value)
    {
    data[1][i] = value;
    }

  public Example copy()
    {
    DoubleExample exampleCopy = new DoubleExample();
    exampleCopy.data = (double [][]) this.data.clone();
    exampleCopy.numInputs  = this.numInputs;
    exampleCopy.numOutputs = this.numOutputs;
    exampleCopy.inputNames  = (String []) this.inputNames.clone();
    exampleCopy.outputNames = (String []) this.outputNames.clone();

    return exampleCopy;
    }
  public boolean isInputNominal(int i) {
    return false;
  }

  public boolean isOutputNominal(int i) {
    return false;
  }

  public boolean isInputScalar(int i) {
    return true;
  }

  public boolean isOutputScalar(int i) {
    return true;
  }





  public int getInputType(int i) {
    return ColumnTypes.FLOAT;
  }

  public int getOutputType(int i) {
    return ColumnTypes.FLOAT;
  }

  public String getInputString(int i) {
    return Double.toString(this.getInputDouble(i));
  }

  public String getOutputString(int i) {
    return Double.toString(this.getOutputDouble(i));
  }


  public int getInputInt(int i) {
    return (int) this.getInputDouble(i);
  }

  public int getOutputInt(int i) {
    return (int) this.getOutputDouble(i);
  }

  public float getInputFloat(int i) {
    return (float) this.getInputDouble(i);
  }

  public float getOutputFloat(int i) {
    return (float) this.getOutputDouble(i);
  }

  public short getInputShort(int i) {
    return (short) this.getInputDouble(i);
  }

  public short getOutputShort(int i) {
    return (short) this.getOutputDouble(i);
  }

  public long getInputLong(int i) {
    return (long) this.getInputDouble(i);
  }

  public long getOutputLong(int i) {
    return (long) this.getOutputDouble(i);
  }

  public byte getInputByte(int i) {
    return (byte) this.getInputDouble(i);
  }

  public byte getOutputByte(int i) {
    return (byte) this.getOutputDouble(i);
  }

  public Object getInputObject(int i) {
    return (Object) new Double(this.getInputDouble(i));
  }

  public Object getOutputObject(int i) {
    return (Object) new Double(this.getOutputDouble(i));
  }

  public char getInputChar(int i) {
    return (char) this.getInputDouble(i);
  }

  public char getOutputChar(int i) {
    return (char) this.getOutputDouble(i);
  }

  public byte[] getInputBytes(int i) {
    byte [] bytes = new byte[1];
    bytes[0] = (byte) this.getInputDouble(i);
    return bytes;
  }

  public byte[] getOutputBytes(int i) {
    byte [] bytes = new byte[1];
    bytes[0] = (byte) this.getOutputDouble(i);
    return bytes;
  }

  public char[] getInputChars(int i) {
    char [] chars = new char[1];
    chars[0] = (char) this.getInputDouble(i);
    return chars;
  }

  public char[] getOutputChars(int i) {
    char [] chars = new char[1];
    chars[0] = (char) this.getOutputDouble(i);
    return chars;
  }

  public boolean getInputBoolean(int i) {
    if (this.getInputDouble(i) < 0.5)
      return false;
    else
      return true;
  }

  public boolean getOutputBoolean(int i) {
    if (this.getOutputDouble(i) < 0.5)
      return false;
    else
      return true;
  }


  public int getNumInputs() {
    return numInputs;
  }
  public int getNumOutputs() {
    return numOutputs;
  }

/*
  public void setNumInputs(int i) {
    numInputs = i;
    }
  public void setNumOutputs(int i) {
    numOutputs = i;
    }
*/

  public String getInputName(int i) {
    return inputNames[i];
  }
  public String getOutputName(int i) {
    return outputNames[i];
  }

  public ContinuousExample shallowCopy() throws Exception {
    return (ContinuousExample) this.clone();
  }

  public ContinuousExample deepCopy() throws Exception {
    Exception e = new Exception();
    System.out.println("Error!  Can not deep copy ContinuousExample.  ");
    throw e;
    //return null;
  }



  public void setInput(int i, double value) {
    this.setInputDouble(i, value);
  }

  public void setOutput(int i, double value) {
    this.setOutputDouble(i, value);
  }

  public void deleteInputs(boolean [] deleteFeatures) {
    System.out.println("!!! deleteInputs not defined");
  }


  public Object getObject(int row, int column) {
    return null;
  }


  }
