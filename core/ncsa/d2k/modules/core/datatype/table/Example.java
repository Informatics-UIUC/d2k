package ncsa.d2k.modules.core.datatype.table;

public interface Example {
    /**
     * Get the ith input double.
     * @param i the input index
     */
    public double getInputDouble(int i);

    /**
     * Get the ith output double.
     * @param i the output index
     */
    public double getOutputDouble(int i);
    public String getInputString(int i);
    public String getOutputString(int i);
    public int getInputInt(int i);
    public int getOutputInt(int i);
    public float getInputFloat(int i);
    public float getOutputFloat(int i);
    public short getInputShort(int i);
    public short getOutputShort(int i);
    public long getInputLong(int i);
    public long getOutputLong(int i);
    public byte getInputByte(int i);
    public byte getOutputByte(int i);
    public Object getInputObject(int i);
    public Object getOutputObject(int i);
    public char getInputChar(int i);
    public char getOutputChar(int i);
    public byte[] getInputBytes(int i);
    public byte[] getOutputBytes(int i);
    public char[] getInputChars(int i);
    public char[] getOutputChars(int i);
    public boolean getInputBoolean(int i);
    public boolean getOutputBoolean(int i);
    public int getNumInputs();
    public int getNumOutputs();
}
