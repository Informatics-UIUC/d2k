package ncsa.d2k.modules.weka.cluster;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============


import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.core.modules.ComputeModule;
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
		switch (parm1) {
			case 0: return "ncsa.d2k.infrastructure.modules.PredictionModelModule: Cluster model built on training set of instances.";
			default: return "No such output";
		}
	}
  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Takes the input instances and uses them to train the input clusterer.     Outputs the trained model.  </body></html>";
	}
  public String[] getInputTypes() {
		String[] types = {"weka.core.Instances","ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}
  public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}
  public String getInputInfo(int parm1) {
		switch (parm1) {
			case 0: return "weka.core.Instances: training set.";
			case 1: return "ncsa.d2k.modules.PredictionModelModule: input model.";
			default: return "No such input";
		}
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_ClusterModelBuilder";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "WEKA Instance Set";
			case 1:
				return "PredictionModelModule";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "PredictionModelModule";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
