package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.projects.dtcheng.primitive.*;


public class MFMSetGarbageCollection extends ComputeModule {

  public String getModuleName() {
    return "MFMSetGarbageCollection";
  }


  public String getModuleInfo() {
    return "This module allows the user to set whether an MFM is garbage " +
	"collected or not. The default is that MFM's are garbage collected when " +
	"no longer needed. If the MFMs are to be permanently stored, they should " +
	"not be garbaged collected long enough to be written to disk. Note that this " +
	"changes an existing object rather than creating a new one, so there could be " +
	"problems if this is not used properly.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "MultiFormatMatrix";
      case 1:
      	return "GarbageCollectMe";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
    case 0:
        return "MultiFormatMatrix";
      case 1:
      	return "GarbageCollectMe";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", "java.lang.Boolean",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "SameMultiFormatMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "SameMultiFormatMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }


  public void doit() throws Exception {

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    boolean GarbageCollectMe = ((Boolean)this.pullInput(1)).booleanValue();

    X.setGarbageCollectionMode(GarbageCollectMe);
    
    this.pushOutput(X, 0);
  }

}


