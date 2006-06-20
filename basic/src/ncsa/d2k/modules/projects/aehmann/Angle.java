package ncsa.d2k.modules.projects.aehmann;

import ncsa.d2k.core.modules.ComputeModule;

public class Angle
    extends ComputeModule {

  public String getModuleName() {
    return "Angle";
  }

  public String getModuleInfo() {
    return "This module computes the phase angle (theta), in radians, of a complex signal, i.e. theta = atan2(imaginary/real).";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Real part";
      case 1:
        return "Imaginary part";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The real portion of the complex input signal.  (Double array)";
      case 1:
        return "The imaginary portion of the complex input signal.  (Double array)";
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
        return "Angle";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Phase angle, in radians, of the complex input. (Double array)";
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

    double[] real = (double[])this.pullInput(0);
    double[] imag = (double[])this.pullInput(1);

    int array1NumValues = real.length;
    int array2NumValues = imag.length;


    double[] arg = new double[array1NumValues];

    for (int i = 0; i < array1NumValues; i++ ) {
    	arg[i] = Math.atan2(imag[i],real[i]);
    }

    this.pushOutput(arg, 0);

  }
}