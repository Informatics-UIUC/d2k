package ncsa.d2k.modules.weka.evaluation;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============


import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.core.modules.ModelSelectorModule;
import ncsa.d2k.core.modules.ModelModule;


public class WEKA_SimpleModelSelector extends ModelSelectorModule {

  //==============
  // Data Members
  //==============

  private ModelModule m_model = null;

  //================
  // Constructor(s)
  //================

  public WEKA_SimpleModelSelector() {
  }

  //================
  // Public methods
  //================

  public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Simply selects the last model it has received for storage.  </body></html>";
	}

  public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

  public String getInputInfo(int parm1) {
		switch (parm1) {
			case 0: return "A PredicitonModelModule.";
			default: return "No such input";
		}
	}

  public String getOutputInfo(int parm1) {
		switch (parm1) {
			default: return "No such output";
		}
	}

  public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

  protected void doit() throws java.lang.Exception {
    try {
      m_model = (ModelModule)this.pullInput(0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_SimpleModelSelector.doit()");
      throw ex;
    }


  }

  public ModelModule getModel() {
    return m_model;
  }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_SimpleModelSelector";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
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
			default: return "NO SUCH OUTPUT!";
		}
	}
}
