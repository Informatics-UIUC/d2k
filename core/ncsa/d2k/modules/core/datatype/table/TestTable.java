//package  ncsa.util.table;
package ncsa.d2k.modules.core.datatype.table;


/**
 This is a table which provides access to only the rows which are part of
 the test dataset. This is done is such a way as to be transparent to the
 accessing objects. Used to test the effectiveness of the model. From the
 constructor it is also evident that the input columns are all first, and
 the output columns last.
 */
public interface TestTable extends PredictionTable {
}



