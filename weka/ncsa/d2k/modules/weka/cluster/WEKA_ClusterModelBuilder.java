package ncsa.d2k.modules.weka.cluster;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;
import weka.clusterers.*;
import weka.core.Instances;
import ncsa.d2k.modules.weka.cluster.WEKA_ClusterModelDelegator;

public class WEKA_ClusterModelBuilder extends ComputeModule {
  //==============
  // Data Members
  //==============

  //================
  // Constructor(s)
  //================

  public WEKA_ClusterModelBuilder() {
  }

  //================
  // Public Methods
  //================

  protected void doit() throws java.lang.Exception {
    try {
      Instances instances = (Instances)pullInput(0);
      PredictionModelModule pmm = (PredictionModelModule)pullInput(1);
      ((WEKA_ClusterModelDelegator)pmm).buildClusterer(instances);
      System.out.println((WEKA_ClusterModelDelegator)pmm);
      pushOutput(pmm, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_ClusterModelBuilder");
      throw ex;
    }
  }

  public String getOutputInfo(int parm1) {
    return "ncsa.d2k.infrastructure.modules.PredictionModelModule: Cluster model built on training set of instances.";
  }
  public String getModuleInfo() {
    return "Takes the input instances and uses them to train the input clusterer.  Outputs the trained model.";
  }
  public String[] getInputTypes() {
    String []in = {"weka.core.Instances","ncsa.d2k.infrastructure.modules.PredictionModelModule"};
    return in;
  }
  public String[] getOutputTypes() {
    String[] out = {"ncsa.d2k.infrastructure.modules.PredictionModelModule"};
    return out;
  }
  public String getInputInfo(int parm1) {
    if (parm1 == 1) {
      return "weka.core.Instances: training set.";
    } else if (parm1 == 0) {
      return "ncsa.d2k.infrastructure.modules.PredictionModelModule: input model.";
    } else {
      return "No such input.";
    }

  }

}