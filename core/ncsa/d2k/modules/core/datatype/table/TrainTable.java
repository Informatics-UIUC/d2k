//package  ncsa.util.table;
package ncsa.d2k.modules.core.datatype.table;


/**
 This class provides transparent access to the training data only. The testSets
 field of the TrainTest table is used to reference only the test data, yet
 the getter methods look exactly the same as they do for any other table.
 */
public interface TrainTable extends ExampleTable {

    /**
     * Get the example table from which this table was derived.
	 * @return the example table from which this table was derived
     */
    public ExampleTable getExampleTable ();
}



