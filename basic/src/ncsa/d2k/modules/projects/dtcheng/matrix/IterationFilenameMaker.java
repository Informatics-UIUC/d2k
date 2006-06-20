package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class IterationFilenameMaker
    extends ComputeModule {

  public String getModuleName() {
    return "IterationFilenameMaker";
  }

  public String getModuleInfo() {
    return "This module kicks out a filename with an iteration number on it " +
		"so that we can store results in a pile of files and not lose them unless we want to.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "IterationNumber";
      case 1:
        return "BaseFileName";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
        return "IterationNumber";
      case 1:
        return "BaseFileName";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long",
        "java.lang.String",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "NewBaseFileName";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "NewBaseFileName";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.String",
    };
    return types;
  }

  public void doit() throws Exception {
      long IterationNumber = ((Long)this.pullInput(0)).longValue();
      String BaseFileName = (String)this.pullInput(1);
      
      String NewBaseFileName = new String(BaseFileName + IterationNumber);

      this.pushOutput(NewBaseFileName, 0);


  }

}
