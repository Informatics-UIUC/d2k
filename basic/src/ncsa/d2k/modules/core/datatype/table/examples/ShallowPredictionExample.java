package ncsa.d2k.modules.core.datatype.table.examples;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * A ShallowPredictionExample is a PredictionExample that keeps a reference
 * to the table and the row it represents.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class ShallowPredictionExample extends ShallowExample
        implements PredictionExample {

    public ShallowPredictionExample(PredictionTable pt, int row) {
        super(pt, row);
    }

    public int getNumPredictions() {
        return ((PredictionTable)et).getPredictionSet().length;
    }

    public void setDoublePrediction(double pred, int p) {
        ((PredictionTable)et).setDoublePrediction(pred, rowIdx, p);
    }
    public double getDoublePrediction(int p) {
        return ((PredictionTable)et).getDoublePrediction(rowIdx, p);
    }

    public void setIntPrediction(int pred, int p) {
        ((PredictionTable)et).setIntPrediction(pred, rowIdx, p);
    }
    public int getIntPrediction(int p) {
        return ((PredictionTable)et).getIntPrediction(rowIdx, p);
    }

    public void setFloatPrediction(float pred, int p) {
        ((PredictionTable)et).setFloatPrediction(pred, rowIdx, p);
    }
    public float getFloatPrediction(int p) {
        return ((PredictionTable)et).getIntPrediction(rowIdx, p);
    }

    public void setShortPrediction(short pred, int p) {
        ((PredictionTable)et).setShortPrediction(pred, rowIdx, p);
    }
    public short getShortPrediction(int p) {
        return ((PredictionTable)et).getShortPrediction(rowIdx, p);
    }

    public void setLongPrediction(long pred, int p) {
        ((PredictionTable)et).setLongPrediction(pred, rowIdx, p);
    }
    public long getLongPrediction(int p) {
        return ((PredictionTable)et).getLongPrediction(rowIdx, p);
    }

    public void setStringPrediction(String pred, int p) {
        ((PredictionTable)et).setStringPrediction(pred, rowIdx, p);
    }
    public String getStringPrediction(int p) {
        return ((PredictionTable)et).getStringPrediction(rowIdx, p);
    }

    public void setCharsPrediction(char[] pred, int p) {
        ((PredictionTable)et).setCharsPrediction(pred, rowIdx, p);
    }
    public char[] getCharsPrediction(int p) {
        return ((PredictionTable)et).getCharsPrediction(rowIdx, p);
    }

    public void setCharPrediction(char pred, int p) {
        ((PredictionTable)et).setCharPrediction(pred, rowIdx, p);
    }
    public char getCharPrediction(int p) {
        return ((PredictionTable)et).getCharPrediction(rowIdx, p);
    }

    public void setBytesPrediction(byte[] pred, int p) {
        ((PredictionTable)et).setBytesPrediction(pred, rowIdx, p);
    }
    public byte[] getBytesPrediction(int p) {
        return ((PredictionTable)et).getBytesPrediction(rowIdx, p);
    }

    public void setBytePrediction(byte pred, int p) {
        ((PredictionTable)et).setBytePrediction(pred, rowIdx, p);
    }
    public byte getBytePrediction(int p) {
        return ((PredictionTable)et).getBytePrediction(rowIdx, p);
    }

    public void setBooleanPrediction(boolean pred, int p) {
        ((PredictionTable)et).setBooleanPrediction(pred, rowIdx, p);
    }
    public boolean getBooleanPrediction(int p) {
        return ((PredictionTable)et).getBooleanPrediction(rowIdx, p);
    }

    public void setObjectPrediction(Object pred, int p) {
        ((PredictionTable)et).setObjectPrediction(pred, rowIdx, p);
    }
    public Object getObjectPrediction(int p) {
        return ((PredictionTable)et).getObjectPrediction(rowIdx, p);
    }
}
