package ncsa.d2k.modules.core.prediction.naivebayes;

import ncsa.d2k.core.modules.*;

public class PrepareForVis extends DataPrepModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {"ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
    return out;
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  public void doit() {
    NaiveBayesModel nbm = (NaiveBayesModel)pullInput(0);
    nbm.setupForVis();
    pushOutput(nbm, 0);
  }
}