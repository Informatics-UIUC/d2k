package ncsa.d2k.modules.core.datatype.table.examples;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.weighted.*;

/**
 * A WeightedShallowPredictionExample is an Example that keeps a reference to
 * its PredictionTable and its row index.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class WeightedShallowPredictionExample extends ShallowPredictionExample
    implements WeightedPredictionExample {

    public WeightedShallowPredictionExample(PredictionTable wet, int rowIdx) {
        super(wet, rowIdx);
    }

    public double getWeight() {
        return ((WeightedTable)et).getRowWeight(rowIdx);
    }

    public void setWeight(double weight) {
        ((WeightedTable)et).setRowWeight(weight, rowIdx);
    }
}
