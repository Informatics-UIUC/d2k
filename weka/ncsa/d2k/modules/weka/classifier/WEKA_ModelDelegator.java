package ncsa.d2k.modules.weka.classifier;

import weka.core.Instances;

public interface WEKA_ModelDelegator {

  public weka.classifiers.Classifier getDelegate();

  public void buildClassifier(Instances instances) throws Exception;

  public void setOptions(String[] options) throws Exception;

  public String toString();

}