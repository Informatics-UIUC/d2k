package ncsa.d2k.modules.core.prediction.decisiontree.continuous;

import ncsa.d2k.modules.core.datatype.table.*;

public class DecisionTreePartition implements java.io.Serializable
  {
  ExampleTable exampleSet1;
  ExampleTable exampleSet2;

  public DecisionTreePartition(ExampleTable exampleSet1, ExampleTable exampleSet2)
    {
    this.exampleSet1 = exampleSet1;
    this.exampleSet2 = exampleSet2;
    }

  }
