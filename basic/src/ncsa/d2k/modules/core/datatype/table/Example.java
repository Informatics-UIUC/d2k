package ncsa.d2k.modules.core.datatype.table;

public interface Example extends java.io.Serializable {

	static final long serialVersionUID = 3145394474006927718L;

    /**
     * Get the ith input as a double.
     * @param i the input index
     * @return the ith input as a double
     */
    public double getInputDouble(int i);

    /**
     * Get the oth output as a double.
     * @param o the output index
     * @return the oth output as a double
     */
    public double getOutputDouble(int o);

    /**
     * Get the ith input as a String.
     * @param i the input index
     * @return the ith input as a String
     */
    public String getInputString(int i);

    /**
     * Get the oth output as a String.
     * @param o the output index
     * @return the oth output as a String
     */
    public String getOutputString(int o);

    /**
     * Get the ith input as an int.
     * @param i the input index
     * @return the ith input as an int
     */
    public int getInputInt(int i);

    /**
     * Get the oth output as an int.
     * @param o the output index
     * @return the oth output as an int
     */
    public int getOutputInt(int o);

    /**
     * Get the ith input as a float.
     * @param i the input index
     * @return the ith input as a float
     */
    public float getInputFloat(int i);

    /**
     * Get the oth output as a float.
     * @param o the output index
     * @return the oth output as a float
     */
    public float getOutputFloat(int o);

    /**
     * Get the ith input as a short.
     * @param i the input index
     * @return the ith input as a short
     */
    public short getInputShort(int i);

    /**
     * Get the oth output as a short.
     * @param o the output index
     * @return the oth output as a short
     */
    public short getOutputShort(int o);

    /**
     * Get the ith input as a long.
     * @param i the input index
     * @return the ith input as a long
     */
    public long getInputLong(int i);

    /**
     * Get the oth output as a long.
     * @param o the output index
     * @return the ith output as a long
     */
    public long getOutputLong(int o);

    /**
     * Get the ith input as a byte.
     * @param i the input index
     * @return the ith input as a byte
     */
    public byte getInputByte(int i);

    /**
     * Get the oth output as a byte.
     * @param o the output index
     * @return the oth output as a byte
     */
    public byte getOutputByte(int o);

    /**
     * Get the ith input as an Object.
     * @param i the input index
     * @return the ith input as an Object.
     */
    public Object getInputObject(int i);

    /**
     * Get the oth output as an Object.
     * @param o the output index
     * @return the oth output as an Object
     */
    public Object getOutputObject(int o);

    /**
     * Get the ith input as a char.
     * @param i the input index
     * @return the ith input as a char
     */
    public char getInputChar(int i);

    /**
     * Get the oth output as a char.
     * @param o the output index
     * @return the oth output as a char
     */
    public char getOutputChar(int o);

    /**
     * Get the ith input as chars.
     * @param i the input index
     * @return the ith input as chars
     */
    public char[] getInputChars(int i);

    /**
     * Get the oth output as chars.
     * @param o the output index
     * @return the oth output as chars
     */
    public char[] getOutputChars(int o);

    /**
     * Get the ith input as bytes.
     * @param i the input index
     * @return the ith input as bytes.
     */
    public byte[] getInputBytes(int i);

    /**
     * Get the oth output as bytes.
     * @param o the output index
     * @return the oth output as bytes.
     */
    public byte[] getOutputBytes(int o);

    /**
     * Get the ith input as a boolean.
     * @param i the input index
     * @return the ith input as a boolean
     */
    public boolean getInputBoolean(int i);

    /**
     * Get the oth output as a boolean.
     * @param o the output index
     * @return the oth output as a boolean
     */
    public boolean getOutputBoolean(int o);

    /**
     * Get the number of inputs.
     * @return the number of inputs
     */
    public int getNumInputs();

    /**
     * Get the number of outputs.
     * @return the number of outputs
     */
    public int getNumOutputs();

    /**
     * Get the name of an input.
     * @param i the input index
     * @return the name of the ith input.
     */
    public String getInputName(int i);

    /**
     * Get the name of an output.
     * @param o the output index
     * @return the name of the oth output
     */
    public String getOutputName(int o);

    /**
     * Get the type of the ith input.
     * @param i the input index
     * @return the type of the ith input
     * @see ncsa.d2k.modules.core.datatype.table.ColumnTypes
     */
    public int getInputType(int i);

    /**
     * Get the type of the oth output.
     * @param o the output index
     * @return the type of the oth output
     * @see ncsa.d2k.modules.core.datatype.table.ColumnTypes
     */
    public int getOutputType(int o);

    /**
     * Return true if the ith input is nominal, false otherwise.
     * @param i the input index
     * @return true if the ith input is nominal, false otherwise.
     */
    public boolean isInputNominal(int i);

    /**
     * Return true if the ith output is nominal, false otherwise.
     * @param o the output index
     * @return true if the ith output is nominal, false otherwise.
     */
    public boolean isOutputNominal(int o);

    /**
     * Return true if the ith input is scalar, false otherwise.
     * @param i the input index
     * @return true if the ith input is scalar, false otherwise.
     */
    public boolean isInputScalar(int i);

    /**
     * Return true if the ith output is scalar, false otherwise.
     * @param o the output index
     * @return true if the ith output is scalar, false otherwise.
     */
    public boolean isOutputScalar(int o);
}
