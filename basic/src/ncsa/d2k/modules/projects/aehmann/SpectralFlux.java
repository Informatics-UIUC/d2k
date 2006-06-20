package ncsa.d2k.modules.projects.aehmann;

import ncsa.d2k.core.modules.*;

public class SpectralFlux
    extends OrderedReentrantModule {

  public String getModuleName() {
    return "SpectralFlux";
  }

  public String getModuleInfo() {
    return "This module computes the flux of an array stream.  The flux is defined as the absolutely value of the difference between" +
    		"two successive arrays.  Prior to the difference calculation, the arrays are normalized.";
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
        return "Input of streaming arrays. (Double array).";
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
        return "Flux";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The flux of successive arrays. (Double array).";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[D"};
    return types;
  }
  
  

  int startcase;
	public void beginExecution() {
		startcase = 1;
	}
  
  double[] old_frame;
  
  public void doit() {

    Object object1 = (Object) this.pullInput(0);

    if (object1 == null) {
    	this.pushOutput(null, 0);
    	beginExecution();
    	return;
    }
  	
    double[] input = (double []) object1;
    int len = input.length;
    double[] flux = new double[1];
	flux[0] = 0.0;	
    double norm_factor = 0;
    double[] new_frame = new double[len];
    
    for (int i = 0; i < len; i++) {
    	norm_factor += input[i]*input[i];
    }
    norm_factor = Math.sqrt(norm_factor);

    for (int i = 0; i < len; i++) {
    	new_frame[i] = input[i] / norm_factor;
    }
    
    if (startcase == 1) {
    	old_frame = new_frame;
    	startcase = 0;
    }
    
    for (int i = 0; i < len; i++) {
    	flux[0] += (new_frame[i]-old_frame[i])*(new_frame[i]-old_frame[i]);
    }
    

    old_frame = new_frame;
    this.pushOutput(flux, 0);
    
  }
}