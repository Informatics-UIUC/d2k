package ncsa.d2k.modules.core.datatype.table;

public interface WeightedPredictionExample extends PredictionExample {

    public double getWeight();
    public void setWeight(double weight);
}
