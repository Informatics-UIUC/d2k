package ncsa.d2k.modules.core.datatype.table.examples;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.util.*;

/**
 * An implementation of an Example.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class ExampleImpl extends TableImpl implements Example {
	static final long serialVersionUID = 8436788752105562461L;

    private int[] inputs;
    private int[] outputs;

    public ExampleImpl(ExampleTable et, int e) {
        // for each input and output, make a column
        int[] ins = et.getInputFeatures();
        int[] outs = et.getOutputFeatures();
        inputs = new int[ins.length];
        outputs = new int[outputs.length];

        int colidx = 0;
        columns = new Column[ins.length+outs.length];
        for(int i = 0; i < ins.length; i++) {
            int typ = et.getInputType(i);
            Column c = ColumnUtilities.createColumn(typ, 1);
            c.setLabel(et.getInputName(i));
            c.setIsNominal(et.isInputNominal(i));
            // set the datum
            // switch on typ
            switch(typ) {
                case(ColumnTypes.BOOLEAN):
                    c.setBoolean(et.getInputBoolean(i, e), 0);
                    break;
                case(ColumnTypes.BYTE):
                    c.setByte(et.getInputByte(i, e), 0);
                    break;
                case(ColumnTypes.BYTE_ARRAY):
                    c.setBytes(et.getInputBytes(i, e), 0);
                    break;
                case(ColumnTypes.CHAR):
                    c.setChar(et.getInputChar(i, e), 0);
                    break;
                case(ColumnTypes.CHAR_ARRAY):
                    c.setChars(et.getInputChars(i, e), 0);
                    break;
                case(ColumnTypes.DOUBLE):
                    c.setDouble(et.getInputDouble(i, e), 0);
                    break;
                case(ColumnTypes.FLOAT):
                    c.setFloat(et.getInputFloat(i, e), 0);
                    break;
                case(ColumnTypes.INTEGER):
                    c.setInt(et.getInputInt(i, e), 0);
                    break;
                case(ColumnTypes.LONG):
                    c.setLong(et.getInputLong(i, e), 0);
                    break;
                case(ColumnTypes.OBJECT):
                    c.setObject(et.getInputObject(i, e), 0);
                    break;
                case(ColumnTypes.SHORT):
                    c.setShort(et.getInputShort(i, e), 0);
                    break;
                default:
                    c.setString(et.getInputString(i, e), 0);
                    break;
            }
            columns[colidx] = c;
            inputs[i] = colidx;
            colidx++;
        }
        for(int i = 0; i < outs.length; i++) {
            int typ = et.getOutputType(i);
            Column c = ColumnUtilities.createColumn(typ, 1);
            c.setLabel(et.getOutputName(i));
            c.setIsNominal(et.isOutputNominal(i));
            // set the datum
            // switch on typ
            switch(typ) {
                case(ColumnTypes.BOOLEAN):
                    c.setBoolean(et.getOutputBoolean(i, e), 0);
                    break;
                case(ColumnTypes.BYTE):
                    c.setByte(et.getOutputByte(i, e), 0);
                    break;
                case(ColumnTypes.BYTE_ARRAY):
                    c.setBytes(et.getOutputBytes(i, e), 0);
                    break;
                case(ColumnTypes.CHAR):
                    c.setChar(et.getOutputChar(i, e), 0);
                    break;
                case(ColumnTypes.CHAR_ARRAY):
                    c.setChars(et.getOutputChars(i, e), 0);
                    break;
                case(ColumnTypes.DOUBLE):
                    c.setDouble(et.getOutputDouble(i, e), 0);
                    break;
                case(ColumnTypes.FLOAT):
                    c.setFloat(et.getOutputFloat(i, e), 0);
                    break;
                case(ColumnTypes.INTEGER):
                    c.setInt(et.getOutputInt(i, e), 0);
                    break;
                case(ColumnTypes.LONG):
                    c.setLong(et.getOutputLong(i, e), 0);
                    break;
                case(ColumnTypes.OBJECT):
                    c.setObject(et.getOutputObject(i, e), 0);
                    break;
                case(ColumnTypes.SHORT):
                    c.setShort(et.getOutputShort(i, e), 0);
                    break;
                default:
                    c.setString(et.getOutputString(i, e), 0);
                    break;
            }
            columns[colidx] = c;
            outputs[i] = colidx;
            colidx++;
        }
    }

    /**
     * Get the ith input as a double.
     * @param i the input index
     * @return the ith input as a double
     */
    public double getInputDouble(int i) {
        return getDouble(0, inputs[i]);
    }

    /**
     * Get the oth output as a double.
     * @param o the output index
     * @return the oth output as a double
     */
    public double getOutputDouble(int o) {
        return getDouble(0, outputs[o]);
    }

    /**
     * Get the ith input as a String.
     * @param i the input index
     * @return the ith input as a String
     */
    public String getInputString(int i) {
        return getString(0, inputs[i]);
    }

    /**
     * Get the oth output as a String.
     * @param o the output index
     * @return the oth output as a String
     */
    public String getOutputString(int o) {
        return getString(0, outputs[o]);
    }

    /**
     * Get the ith input as an int.
     * @param i the input index
     * @return the ith input as an int
     */
    public int getInputInt(int i) {
        return getInt(0, inputs[i]);
    }

    /**
     * Get the oth output as an int.
     * @param o the output index
     * @return the oth output as an int
     */
    public int getOutputInt(int o) {
        return getInt(0, outputs[o]);
    }

    /**
     * Get the ith input as a float.
     * @param i the input index
     * @return the ith input as a float
     */
    public float getInputFloat(int i) {
        return getFloat(0, inputs[i]);
    }

    /**
     * Get the oth output as a float.
     * @param o the output index
     * @return the oth output as a float
     */
    public float getOutputFloat(int o) {
        return getFloat(0, outputs[o]);
    }

    /**
     * Get the ith input as a short.
     * @param i the input index
     * @return the ith input as a short
     */
    public short getInputShort(int i) {
        return getShort(0, inputs[i]);
    }

    /**
     * Get the oth output as a short.
     * @param o the output index
     * @return the oth output as a short
     */
    public short getOutputShort(int o) {
        return getShort(0, outputs[o]);
    }

    /**
     * Get the ith input as a long.
     * @param i the input index
     * @return the ith input as a long
     */
    public long getInputLong(int i) {
        return getLong(0, inputs[i]);
    }

    /**
     * Get the oth output as a long.
     * @param o the output index
     * @return the ith output as a long
     */
    public long getOutputLong(int o) {
        return getLong(0, outputs[o]);
    }

    /**
     * Get the ith input as a byte.
     * @param i the input index
     * @return the ith input as a byte
     */
    public byte getInputByte(int i) {
        return getByte(0, inputs[i]);
    }

    /**
     * Get the oth output as a byte.
     * @param o the output index
     * @return the oth output as a byte
     */
    public byte getOutputByte(int o) {
        return getByte(0, outputs[o]);
    }

    /**
     * Get the ith input as an Object.
     * @param i the input index
     * @return the ith input as an Object.
     */
    public Object getInputObject(int i) {
        return getObject(0, inputs[i]);
    }

    /**
     * Get the oth output as an Object.
     * @param o the output index
     * @return the oth output as an Object
     */
    public Object getOutputObject(int o) {
        return getObject(0, outputs[o]);
    }

    /**
     * Get the ith input as a char.
     * @param i the input index
     * @return the ith input as a char
     */
    public char getInputChar(int i) {
        return getChar(0,  inputs[i]);
    }

    /**
     * Get the oth output as a char.
     * @param o the output index
     * @return the oth output as a char
     */
    public char getOutputChar(int o) {
        return getChar(0, outputs[o]);
    }

    /**
     * Get the ith input as chars.
     * @param i the input index
     * @return the ith input as chars
     */
    public char[] getInputChars(int i) {
        return getChars(0,  inputs[i]);
    }

    /**
     * Get the oth output as chars.
     * @param o the output index
     * @return the oth output as chars
     */
    public char[] getOutputChars(int o) {
        return getChars(0, outputs[o]);
    }

    /**
     * Get the ith input as bytes.
     * @param i the input index
     * @return the ith input as bytes.
     */
    public byte[] getInputBytes(int i) {
        return getBytes(0, inputs[i]);
    }

    /**
     * Get the oth output as bytes.
     * @param o the output index
     * @return the oth output as bytes.
     */
    public byte[] getOutputBytes(int o) {
        return getBytes(0, outputs[o]);
    }

    /**
     * Get the ith input as a boolean.
     * @param i the input index
     * @return the ith input as a boolean
     */
    public boolean getInputBoolean(int i) {
        return getBoolean(0, inputs[i]);
    }

    /**
     * Get the oth output as a boolean.
     * @param o the output index
     * @return the oth output as a boolean
     */
    public boolean getOutputBoolean(int o) {
        return getBoolean(0, outputs[o]);
    }

    /**
     * Get the number of inputs.
     * @return the number of inputs
     */
    public int getNumInputs() {
        return inputs.length;
    }

    /**
     * Get the number of outputs.
     * @return the number of outputs
     */
    public int getNumOutputs() {
        return outputs.length;
    }

    /**
     * Get the name of an input.
     * @param i the input index
     * @return the name of the ith input.
     */
    public String getInputName(int i) {
        return getColumnLabel(inputs[i]);
    }

    /**
     * Get the name of an output.
     * @param o the output index
     * @return the name of the oth output
     */
    public String getOutputName(int o) {
        return getColumnLabel(outputs[o]);
    }

    /**
     * Get the type of the ith input.
     * @param i the input index
     * @return the type of the ith input
     * @see ncsa.d2k.modules.core.datatype.table.ColumnTypes
     */
    public int getInputType(int i) {
        return getColumnType(inputs[i]);
    }

    /**
     * Get the type of the oth output.
     * @param o the output index
     * @return the type of the oth output
     * @see ncsa.d2k.modules.core.datatype.table.ColumnTypes
     */
    public int getOutputType(int o) {
        return getColumnType(outputs[o]);
    }

    /**
     * Return true if the ith input is nominal, false otherwise.
     * @param i the input index
     * @return true if the ith input is nominal, false otherwise.
     */
    public boolean isInputNominal(int i) {
        return isColumnNominal(inputs[i]);
    }

    /**
     * Return true if the ith output is nominal, false otherwise.
     * @param o the output index
     * @return true if the ith output is nominal, false otherwise.
     */
    public boolean isOutputNominal(int o) {
        return isColumnNominal(outputs[o]);
    }

    /**
     * Return true if the ith input is scalar, false otherwise.
     * @param i the input index
     * @return true if the ith input is scalar, false otherwise.
     */
    public boolean isInputScalar(int i) {
        return isColumnScalar(inputs[i]);
    }

    /**
     * Return true if the ith output is scalar, false otherwise.
     * @param o the output index
     * @return true if the ith output is scalar, false otherwise.
     */
    public boolean isOutputScalar(int o) {
        return isColumnScalar(outputs[o]);
    }
}
