package ncsa.d2k.modules.weka.classifier;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;

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
    return "";
  }

  public String getModuleInfo() {
	StringBuffer sb = new StringBuffer("Generates an ID3 tree. NOTE: This classifier will accept only nominal attributes, a nominal class, and no missing values.");
	return sb.toString();
  }

  public String getOutputInfo(int parm1) {
    return "ncsa.d2k.infrastructure.modules.PredictionModelModule: WEKA_ID3Model";
  }

  public String[] getInputTypes() {
    return null;
  }

  public String[] getOutputTypes() {
    String[] out = {"ncsa.d2k.infrastructure.modules.PredictionModelModule"};
    return out;
  }
}