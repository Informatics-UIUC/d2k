package ncsa.d2k.modules.core.datatype.table.examples;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.weighted.*;

/**
 * A WeightedShallowExample is an Example from a WeightedExampleTable that
 * keeps a reference to the Table and to the row index.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class WeightedShallowExample extends ShallowExample {

    public WeightedShallowExample(ExampleTable wet, int rowIdx) {
        super(wet, rowIdx);
    }

    public double getWeight() {
        return ((WeightedTable)et).getRowWeight(rowIdx);
    }

    public void setWeight(double weight) {
        ((WeightedTable)et).setRowWeight(weight, rowIdx);
    }
}
