//package  ncsa.util.table;
package ncsa.d2k.modules.core.datatype.table;

import java.io.*;
import java.util.*;

/**
 This class provides transparent access to the training data only. The testSets
 field of the TrainTest table is used to reference only the test data, yet
 the getter methods look exactly the same as they do for any other table.
 */
public final class TrainTableImpl extends ExampleTableImpl implements TrainTable {
    protected ExampleTable original = null;

	TrainTableImpl(int i) {
		super(i);
	}

    /**
	 * Create a new TrainTable
     * @param ttt the ExampleTable from which this table is derived
     */
    TrainTableImpl (ExampleTableImpl ttt) {
        super(ttt);
        original = ttt;
        //this.trainSet = ttt.trainSet;
        //this.trainSet = new int[ttt.trainSet.length];
        //System.arraycopy(ttt.trainSet, 0, trainSet, 0, ttt.trainSet.length);
    }

    /**
     Copy method. Return an exact copy of this column.  A deep copy
     is attempted, but if it fails a new column will be created,
     initialized with the same data as this column.
     @return A new Column with a copy of the contents of this column.
     */
    public Table copy () {
       	TrainTableImpl vt;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte buf[] = baos.toByteArray();
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            vt = (TrainTableImpl)ois.readObject();
            ois.close();
            return  vt;
        } catch (Exception e) {
            vt = new TrainTableImpl(getNumColumns());
            vt.setKeyColumn(getKeyColumn());
            for (int i = 0; i < getNumColumns(); i++)
                vt.setColumn(getColumn(i).copy(), i);
            vt.setLabel(getLabel());
            vt.setComment(getComment());
            vt.setType(getType());
			vt.setInputFeatures(getInputFeatures());
			vt.setOutputFeatures(getOutputFeatures());
			vt.transformations = (ArrayList)transformations.clone();
			vt.setTestingSet(getTestingSet());
			vt.setTrainingSet(getTrainingSet());
			vt.original = original;
            return  vt;
        }
    }

    /**
     * Get the example table from which this table was derived.
	 * @return the example table from which this table was derived
     */
    public ExampleTable getExampleTable () {
        return  original;
    }

    /**
	 * Get an int value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the int at (row, column)
     */
    public int getInt (int row, int column) {
        return  columns[column].getInt(this.trainSet[row]);
    }

    /**
	 * Get a short value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the short at (row, column)
     */
    public short getShort (int row, int column) {
        return  columns[column].getShort(this.trainSet[row]);
    }

    /**
	 * Get a long value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the long at (row, column)
     */
    public long getLong (int row, int column) {
        return  columns[column].getLong(this.trainSet[row]);
    }

    /**
	 * Get a float value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the float at (row, column)
     */
    public float getFloat (int row, int column) {
        return  columns[column].getFloat(this.trainSet[row]);
    }

    /**
	 * Get a double value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the double at (row, column)
     */
    public double getDouble (int row, int column) {
        return  columns[column].getDouble(this.trainSet[row]);
    }

    /**
	 * Get a String from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the String at (row, column)
     */
    public String getString (int row, int column) {
        return  columns[column].getString(this.trainSet[row]);
    }

    /**
	 * Get an array of bytes from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of bytes
     */
    public byte[] getBytes (int row, int column) {
        return  columns[column].getBytes(this.trainSet[row]);
    }

    /**
	 * Get an array of chars from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the value at (row, column) as an array of chars
     */
    public char[] getChars (int row, int column) {
        return  columns[column].getChars(this.trainSet[row]);
    }

    /**
	 * Get a boolean value from the table.
     * @param row the row of the table
     * @param column the column of the table
     * @return the boolean value at (row, column)
     */
    public boolean getBoolean (int row, int column) {
        return  columns[column].getBoolean(this.trainSet[row]);
    }

    /**
     get the number of entries in the train set.
     @return the size of the train set
     */
    public int getNumRows () {
        return  this.trainSet.length;
    }
}
