package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.ComputeModule;

public class PackUp4 extends ComputeModule {
  
  public String getModuleInfo() {
    return "PackUp4. Arranges the inputs into an " + "object array in the same order that they came in.";
  }
  
  public String getModuleName() {
    return "PackUp4";
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.Object", "java.lang.Object", "java.lang.Object", "java.lang.Object", };
    return types;
  }
  
  public String[] getOutputTypes() {
    String[] types = { "java.lang.Object" };
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "Object0";
    case 1:
      return "Object1";
    case 2:
      return "Object2";
    case 3:
      return "Object3";
    default:
      return "No such input";
    }
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "Object0";
    case 1:
      return "Object1";
    case 2:
      return "Object2";
    case 3:
      return "Object3";
    default:
      return "NO SUCH INPUT!";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "PackedObjectArray.";
    default:
      return "No such output";
    }
  }
  
  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "PackedObjectArray.";
    default:
      return "NO SUCH OUTPUT!";
    }
  }
  
  public void doit() {
    int nObjects = 4;
    Object[] ThePackage = new Object[nObjects];
    
    for (int objectIndex = 0; objectIndex < nObjects; objectIndex++) { 
      ThePackage[objectIndex] = this.pullInput(objectIndex);
    }
    this.pushOutput(ThePackage,0);
  }
}

