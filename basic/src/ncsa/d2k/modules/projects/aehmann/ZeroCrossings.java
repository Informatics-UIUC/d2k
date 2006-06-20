package ncsa.d2k.modules.projects.aehmann;

import ncsa.d2k.core.modules.*;

public class ZeroCrossings
    extends OrderedReentrantModule {

  public String getModuleName() {
    return "ZeroCrossings";
  }

  public String getModuleInfo() {
    return "This module counts the number of zero-crossings in an array";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "[D";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "double array.";
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
        return "[D";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "[D";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[D"};
    return types;
  }
  
  double signum(double in) {
  	double out;
  	if (in >= 0.0)
  		out = 1.0;
  	else
  		out = -1.0;
  	return out;
  }
  
  public void doit() {

    Object object1 = (Object) this.pullInput(0);
    double[] tdzc = new double[1];
    
    if (object1 == null) {
    	this.pushOutput(null, 0);
    	return;
    }
    
  	
    double[] input = (double []) object1;
    int len = input.length;
    
    for (int i = 1; i < len; i++) {
    	tdzc[0] += Math.abs( signum(input[i]) - signum(input[i-1]) ); 
    }
    tdzc[0] = tdzc[0]/2.0;

    this.pushOutput(tdzc, 0);
    
  }
}