package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class TwoIndexFilenameMaker
    extends ComputeModule {

  public String getModuleName() {
    return "TwoIndexFilenameMaker";
  }

  public String getModuleInfo() {
    return "This module kicks out filenames with all combinations of two indices " +
		"so that we can store results in a pile of files and not lose them unless we want to.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "BaseFileName";
      case 1:
        return "StartFirst";
      case 2:
        return "EndFirst";
      case 3:
        return "StartSecond";
      case 4:
        return "EndSecond";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
        return "BaseFileName";
      case 1:
        return "StartFirst";
      case 2:
        return "EndFirst";
      case 3:
        return "StartSecond";
      case 4:
        return "EndSecond";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.String",
		"java.lang.Integer",
		"java.lang.Integer",
		"java.lang.Integer",
		"java.lang.Integer",
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
      String BaseFileName = (String)this.pullInput(0);
      int StartFirst = ((Integer)this.pullInput(1)).intValue();
      int EndFirst = ((Integer)this.pullInput(2)).intValue();
      int StartSecond = ((Integer)this.pullInput(3)).intValue();
      int EndSecond = ((Integer)this.pullInput(4)).intValue();
      
      String newFileName = new String();
      // notice the end condition...
      for (int firstIndex = StartFirst; firstIndex <= EndFirst; firstIndex++) {
      	for (int secondIndex = StartSecond; secondIndex <= EndSecond; secondIndex++) {
      		newFileName = new String(BaseFileName + "_" + firstIndex + "_" + secondIndex);

      		this.pushOutput(newFileName, 0);
      	}
      }
  }
}
