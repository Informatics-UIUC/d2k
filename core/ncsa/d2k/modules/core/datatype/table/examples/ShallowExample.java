package ncsa.d2k.modules.core.datatype.table.examples;

import ncsa.d2k.modules.core.datatype.table.*;

public class ShallowExample implements Example {
    protected ExampleTable et;
    protected int rowIdx;

    public ShallowExample(ExampleTable tbl, int row) {
        et = tbl;
        rowIdx = row;
    }

    /**
     * Get the ith input as a double.
     * @param i the input index
     * @return the ith input as a double
     */
    public double getInputDouble(int i) {
        return et.getInputDouble(rowIdx, i);
    }

    /**
     * Get the oth output as a double.
     * @param o the output index
     * @return the oth output as a double
     */
    public double getOutputDouble(int o) {
        return et.getOutputDouble(rowIdx, o);
    }

    /**
     * Get the ith input as a String.
     * @param i the input index
     * @return the ith input as a String
     */
    public String getInputString(int i) {
        return et.getInputString(rowIdx, i);
    }

    /**
     * Get the oth output as a String.
     * @param o the output index
     * @return the oth output as a String
     */
    public String getOutputString(int o) {
        return et.getOutputString(rowIdx, o);
    }

    /**
     * Get the ith input as an int.
     * @param i the input index
     * @return the ith input as an int
     */
    public int getInputInt(int i) {
        return et.getInputInt(rowIdx, i);
    }

    /**
     * Get the oth output as an int.
     * @param o the output index
     * @return the oth output as an int
     */
    public int getOutputInt(int o) {
        return et.getOutputInt(rowIdx, o);
    }

    /**
     * Get the ith input as a float.
     * @param i the input index
     * @return the ith input as a float
     */
    public float getInputFloat(int i) {
        return et.getInputFloat(rowIdx, i);
    }

    /**
     * Get the oth output as a float.
     * @param o the output index
     * @return the oth output as a float
     */
    public float getOutputFloat(int o) {
        return et.getOutputFloat(rowIdx, o);
    }

    /**
     * Get the ith input as a short.
     * @param i the input index
     * @return the ith input as a short
     */
    public short getInputShort(int i) {
        return et.getInputShort(rowIdx, i);
    }

    /**
     * Get the oth output as a short.
     * @param o the output index
     * @return the oth output as a short
     */
    public short getOutputShort(int o) {
        return et.getOutputShort(rowIdx, o);
    }

    /**
     * Get the ith input as a long.
     * @param i the input index
     * @return the ith input as a long
     */
    public long getInputLong(int i) {
        return et.getInputLong(rowIdx, i);
    }

    /**
     * Get the oth output as a long.
     * @param o the output index
     * @return the ith output as a long
     */
    public long getOutputLong(int o) {
        return et.getOutputLong(rowIdx, o);
    }

    /**
     * Get the ith input as a byte.
     * @param i the input index
     * @return the ith input as a byte
     */
    public byte getInputByte(int i) {
        return et.getInputByte(rowIdx, i);
    }

    /**
     * Get the oth output as a byte.
     * @param o the output index
     * @return the oth output as a byte
     */
    public byte getOutputByte(int o) {
        return et.getOutputByte(rowIdx, o);
    }

    /**
     * Get the ith input as an Object.
     * @param i the input index
     * @return the ith input as an Object.
     */
    public Object getInputObject(int i) {
        return et.getInputObject(rowIdx, i);
    }

    /**
     * Get the oth output as an Object.
     * @param o the output index
     * @return the oth output as an Object
     */
    public Object getOutputObject(int o) {
        return et.getOutputObject(rowIdx, o);
    }

    /**
     * Get the ith input as a char.
     * @param i the input index
     * @return the ith input as a char
     */
    public char getInputChar(int i) {
        return et.getInputChar(rowIdx, i);
    }

    /**
     * Get the oth output as a char.
     * @param o the output index
     * @return the oth output as a char
     */
    public char getOutputChar(int o) {
        return et.getOutputChar(rowIdx, o);
    }

    /**
     * Get the ith input as chars.
     * @param i the input index
     * @return the ith input as chars
     */
    public char[] getInputChars(int i) {
        return et.getInputChars(rowIdx, i);
    }

    /**
     * Get the oth output as chars.
     * @param o the output index
     * @return the oth output as chars
     */
    public char[] getOutputChars(int o) {
        return et.getOutputChars(rowIdx, o);
    }

    /**
     * Get the ith input as bytes.
     * @param i the input index
     * @return the ith input as bytes.
     */
    public byte[] getInputBytes(int i) {
        return et.getInputBytes(rowIdx, i);
    }

    /**
     * Get the oth output as bytes.
     * @param o the output index
     * @return the oth output as bytes.
     */
    public byte[] getOutputBytes(int o) {
        return et.getOutputBytes(rowIdx, o);
    }

    /**
     * Get the ith input as a boolean.
     * @param i the input index
     * @return the ith input as a boolean
     */
    public boolean getInputBoolean(int i) {
        return et.getInputBoolean(rowIdx, i);
    }

    /**
     * Get the oth output as a boolean.
     * @param o the output index
     * @return the oth output as a boolean
     */
    public boolean getOutputBoolean(int o) {
        return et.getOutputBoolean(rowIdx, o);
    }

    /**
     * Get the number of inputs.
     * @return the number of inputs
     */
    public int getNumInputs() {
        return et.getNumInputs();
    }

    /**
     * Get the number of outputs.
     * @return the number of outputs
     */
    public int getNumOutputs() {
        return et.getNumOutputs();
    }

    /**
     * Get the name of an input.
     * @param i the input index
     * @return the name of the ith input.
     */
    public String getInputName(int i) {
        return et.getInputName(i);
    }

    /**
     * Get the name of an output.
     * @param o the output index
     * @return the name of the oth output
     */
    public String getOutputName(int o) {
        return et.getOutputName(o);
    }

    /**
     * Get the type of the ith input.
     * @param i the input index
     * @return the type of the ith input
     * @see ncsa.d2k.modules.core.datatype.table.ColumnTypes
     */
    public int getInputType(int i) {
        return et.getInputType(i);
    }

    /**
     * Get the type of the oth output.
     * @param o the output index
     * @return the type of the oth output
     * @see ncsa.d2k.modules.core.datatype.table.ColumnTypes
     */
    public int getOutputType(int o) {
        return et.getOutputType(o);
    }

    /**
     * Return true if the ith input is nominal, false otherwise.
     * @param i the input index
     * @return true if the ith input is nominal, false otherwise.
     */
    public boolean isInputNominal(int i) {
        return et.isInputNominal(i);
    }

    /**
     * Return true if the ith output is nominal, false otherwise.
     * @param o the output index
     * @return true if the ith output is nominal, false otherwise.
     */
    public boolean isOutputNominal(int o) {
        return et.isOutputNominal(o);
    }

    /**
     * Return true if the ith input is scalar, false otherwise.
     * @param i the input index
     * @return true if the ith input is scalar, false otherwise.
     */
    public boolean isInputScalar(int i) {
        return et.isInputScalar(i);
    }

    /**
     * Return true if the ith output is scalar, false otherwise.
     * @param o the output index
     * @return true if the ith output is scalar, false otherwise.
     */
    public boolean isOutputScalar(int o) {
        return et.isOutputScalar(o);
    }
}
