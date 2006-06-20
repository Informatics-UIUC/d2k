package ncsa.d2k.modules.projects.aehmann;

import ncsa.d2k.core.modules.*;

public class Magnitude
    extends ComputeModule {

  public String getModuleName() {
    return "Magnitude";
  }

  public String getModuleInfo() {
    return "This module computes the magnitude, M, of a complex signal, i.e. M = sqrt(real^2 + imaginary^2)";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Real";
      case 1:
        return "Imaginary";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The real portion of the complex input signal.  (Double array).";
      case 1:
        return "The imaginary portion of the complex input signal.  (Double array).";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[D", "[D"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Magnitude";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The magnitude of the complex input. (Double array).";
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
    Object object2 = (Object) this.pullInput(1);

    if (object1 == null) {
    	this.pushOutput(null, 0);
    	return;
    }
  	
    double[] real = (double []) object1;
	double[] imag = (double []) object2;


    int array1NumValues = real.length;
    int array2NumValues = imag.length;


    double[] mag = new double[array1NumValues];

    for (int i = 0; i < array1NumValues; i++ ) {
    	mag[i] = Math.sqrt(real[i]*real[i]+imag[i]*imag[i]);
    }

    this.pushOutput(mag, 0);

  }
}