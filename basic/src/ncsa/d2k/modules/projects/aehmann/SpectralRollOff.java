package ncsa.d2k.modules.projects.aehmann;

import ncsa.d2k.core.modules.*;

public class SpectralRollOff
    extends OrderedReentrantModule {

  public String getModuleName() {
    return "SpectralRollOff";
  }

  public String getModuleInfo() {
    return "This module computes the rolloff point of the input array.  The rolloff point is defined as the point below which" +
    		"85% of the signal's magnitude distribution resides.";
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
        return "Rolloff";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Rolloff point. (Double array).";
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

    double[] rolloff = new double[1];
    double tot_energy = 0;
    double partial_energy = 0;
    int rolloffpoint = 0;

    for (int i = 0; i < len; i++ ) {
    	tot_energy += input[i];
    }
    
    for (int R = 0; R < len; R++) {
    	partial_energy += input[R];
    	if (partial_energy >= 0.85*tot_energy) {
    		rolloffpoint = R;
    		break;
    	}	
    }
    
    rolloff[0] = (double)rolloffpoint;
    this.pushOutput(rolloff, 0);
  }
}