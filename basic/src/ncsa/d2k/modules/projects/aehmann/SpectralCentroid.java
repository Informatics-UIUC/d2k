package ncsa.d2k.modules.projects.aehmann;

import ncsa.d2k.core.modules.*;

public class SpectralCentroid
    extends OrderedReentrantModule {

  public String getModuleName() {
    return "SpectralCentroid";
  }

  public String getModuleInfo() {
    return "This module computes the centroid of an array";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Input";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Input. (Double array).";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[D",};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Centroid";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The centroid of the input array. (Double array).";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[D"};
    return types;
  }

  public void doit() {

    Object object1 = (Object) this.pullInput(0);

    if (object1 == null) {
    	this.pushOutput(null, 0);
    	return;
    }
  	
    double[] input = (double []) object1;


    int len = input.length;

    double[] centroid = new double[1];
    double numerator = 0;
    double denominator = 0;

    for (int i = 0; i < len; i++ ) {
    	numerator += i*input[i];
    	denominator += input[i];
    }
    centroid[0] = numerator/denominator;
    this.pushOutput(centroid, 0);

  }
}