package ncsa.d2k.modules.weka.classifier;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============


import ncsa.d2k.core.modules.*;
import weka.core.*;
import weka.classifiers.*;

public class WEKA_ID3ModelProducer extends ModelProducerModule {

  //==============
  // Data Members
  //==============

  //================
  // Constructor(s)
  //================

  public WEKA_ID3ModelProducer() {
  }

  //================
  // Public Methods
  //================

  protected void doit() throws java.lang.Exception {
    try {
      WEKA_ID3Model m_model = new WEKA_ID3Model();
      pushOutput(m_model,0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_ID3ModelProducer.doit()");
      throw ex;
    }
  }

  public String getInputInfo(int parm1) {
		switch (parm1) {
			default: return "No such input";
		}
	}

  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Generates an ID3 tree. NOTE: This classifier will accept only nominal     attributes, a nominal class, and no missing values.  </body></html>";
	}

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			case 0: return "ncsa.d2k.infrastructure.modules.PredictionModelModule: WEKA_ID3Model";
			default: return "No such output";
		}
	}

  public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

  public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_ID3ModelProducer";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
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
				return "PredictionModelModule (ID3)";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
