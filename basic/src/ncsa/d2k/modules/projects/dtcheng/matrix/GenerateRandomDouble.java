package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
//import Jama.Matrix;
import java.util.Random;

public class GenerateRandomDouble
    extends ComputeModule {

  public String getModuleName() {
    return "GenerateRandomDouble";
  }

  public String getModuleInfo() {
    return "This module generates a (uniform) random Double.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Seed";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Seed";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "RandomDouble";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "RandomDouble";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Double"};
    return types;
  }

  public void doit() throws Exception {

    long Seed = ((Long)this.pullInput(0)).longValue();

// prepare the random number generator
    Random RandomNumberGenerator = new Random(Seed);

    double randomDoubleTemp = RandomNumberGenerator.nextDouble();

    this.pushOutput(new Double(randomDoubleTemp), 0);
  }

}
