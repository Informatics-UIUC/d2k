package ncsa.d2k.modules.weka.cluster;

import weka.core.Instances;

public interface WEKA_ClusterModelDelegator {

  public weka.clusterers.Clusterer getDelegate();

  public void buildClusterer(Instances instances) throws Exception;

  public void setOptions(String[] options) throws Exception;

  public String toString();

}