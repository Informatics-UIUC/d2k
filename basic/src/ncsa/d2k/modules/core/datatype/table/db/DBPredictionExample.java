package ncsa.d2k.modules.core.datatype.table.db;

import ncsa.d2k.modules.core.datatype.table.PredictionExample;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.io.sql.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author vered goren
 * @version 1.0
 *
 * this is an extension of DBExample which wraps a MutableExample in it for
 * representation of the prediction part.
 *
 * setPrediction methods were implemented in a very premitive way, the only one
 * available at the time of coding this. [8-26-03] relying on the implementation of
 * the prediction columns in a LocalDBPredictionTable, it is known to be
 * represented by a MutableTable. hence the careless casting in these methods.
 *
 * @todo: define APIs for Row and its inherited and implementing classes.
 * are all examples to be fast examples? is there a need in a (really) mutable
 * example ?
 *
 */

public class DBPredictionExample extends DBExample implements PredictionExample {

  protected MutableExample predictionExample;
  protected int[] indirection;
  protected boolean[] prediction;


  public DBPredictionExample(DBDataSource _dataSource,  DBConnection _dbConnection,
                             MutableExample mEx, DBTable _table) {
    super(_dataSource, _dbConnection, _table);
    predictionExample = mEx;


  }

  public int getNumPredictions() {

    return predictionExample.getTable().getNumColumns();

  }


  public void setDoublePrediction(double pred, int p) {
   ((MutableTable)predictionExample.getTable()).setDouble(pred, index, p);
  }

  public double getDoublePrediction(int p) {
    return predictionExample.getDouble(p);
  }

  public void setIntPrediction(int pred, int p) {
       ((MutableTable)predictionExample.getTable()).setInt(pred, index, p);
  }

  public int getIntPrediction(int p) {
        return predictionExample.getInt(p);
  }

  public void setFloatPrediction(float pred, int p) {
       ((MutableTable)predictionExample.getTable()).setFloat(pred, index, p);
  }

  public float getFloatPrediction(int p) {
    return predictionExample.getFloat(p);
  }

  public void setShortPrediction(short pred, int p) {
   ((MutableTable)predictionExample.getTable()).setShort(pred, index, p);
  }

  public short getShortPrediction(int p) {
       return predictionExample.getShort(p);
  }

  public void setLongPrediction(long pred, int p) {
   ((MutableTable)predictionExample.getTable()).setLong(pred, index, p);
  }

  public long getLongPrediction(int p) {
    return predictionExample.getLong(p);
  }

  public void setStringPrediction(String pred, int p) {
   ((MutableTable)predictionExample.getTable()).setString(pred, index, p);
  }

  public String getStringPrediction(int p) {
      return predictionExample.getString(p);
  }

  public void setCharsPrediction(char[] pred, int p) {
   ((MutableTable)predictionExample.getTable()).setChars(pred, index, p);
  }
  public char[] getCharsPrediction(int p) {
        return predictionExample.getChars(p);
  }
  public void setCharPrediction(char pred, int p) {
    ((MutableTable)predictionExample.getTable()).setChar(pred, index, p);
  }

  public char getCharPrediction(int p) {
        return predictionExample.getChar(p);
  }

  public void setBytesPrediction(byte[] pred, int p) {
((MutableTable)predictionExample.getTable()).setBytes(pred, index, p);
  }
  public byte[] getBytesPrediction(int p) {
        return predictionExample.getBytes(p);
  }
  public void setBytePrediction(byte pred, int p) {
((MutableTable)predictionExample.getTable()).setByte(pred, index, p);
  }

  public byte getBytePrediction(int p) {
        return predictionExample.getByte(p);
  }

  public void setBooleanPrediction(boolean pred, int p) {
((MutableTable)predictionExample.getTable()).setBoolean(pred, index, p);
  }

  public boolean getBooleanPrediction(int p) {
        return predictionExample.getBoolean(p);
  }

  public void setObjectPrediction(Object pred, int p) {
((MutableTable)predictionExample.getTable()).setObject(pred, index, p);
  }

  public Object getObjectPrediction(int p) {
        return predictionExample.getObject(p);
  }




  public void setIndex(int i) {
    predictionExample.setIndex(i);
    index = subset[i];
  }

  public double getDouble(int i) {
    if (prediction[i])
              return predictionExample.getDouble(indirection[i]);
    else
        return (double)dataSource.getNumericData(index, indirection[i]);

  }
  public String getString(int i) {
    if (prediction[i])
           return predictionExample.getString(indirection[i]);
   else
     return dataSource.getTextData(index, indirection[i]);

  }
  public int getInt(int i) {
    if (prediction[i])
           return predictionExample.getInt(indirection[i]);
   else
     return (int)dataSource.getNumericData(index, indirection[i]);

  }
  public float getFloat(int i) {
    if (prediction[i])
            return predictionExample.getFloat(indirection[i]);
  else
      return (float)dataSource.getNumericData(index, indirection[i]);

  }

  public short getShort(int i) {
    if (prediction[i])
          return predictionExample.getShort(indirection[i]);
    else
      return (short)dataSource.getNumericData(index, indirection[i]);

  }


  public long getLong(int i) {
    if (prediction[i])
          return predictionExample.getLong(indirection[i]);
    else
     return (long)dataSource.getNumericData(index, indirection[i]);

  }


  public byte getByte(int i) {
    if (prediction[i])
          return predictionExample.getByte(indirection[i]);
    else
     return dataSource.getTextData(index, indirection[i]).getBytes()[0];

  }


  public Object getObject(int i) {
    if (prediction[i])
          return predictionExample.getObject(indirection[i]);
    else
     return (Object)dataSource.getObjectData(index, indirection[i]).toString();

  }


  public char getChar(int i) {
    if (prediction[i])
       return predictionExample.getChar(indirection[i]);
 else
  return dataSource.getTextData(index, indirection[i]).toCharArray()[0];

  }
  public char[] getChars(int i) {
    if (prediction[i])
          return predictionExample.getChars(indirection[i]);
    else
     return dataSource.getTextData(index, indirection[i]).toCharArray();

  }
  public byte[] getBytes(int i) {
    if (prediction[i])
          return predictionExample.getBytes(indirection[i]);
    else
     return dataSource.getTextData(index, indirection[i]).getBytes();

  }
  public boolean getBoolean(int i) {
    if (prediction[i])
          return predictionExample.getBoolean(indirection[i]);
    else
     return new Boolean(dataSource.getTextData(index, indirection[i])).booleanValue();

  }


}