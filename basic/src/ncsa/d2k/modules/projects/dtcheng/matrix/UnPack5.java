package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.ComputeModule;

public class UnPack5 extends ComputeModule {
  
  public String getModuleInfo() {
    return "UnPack5. Unpacks an object array in order. The opposite of PackUp5.";
  }
  
  public String getModuleName() {
    return "UnPack5";
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.Object" };
    return types;
  }
  
  public String[] getOutputTypes() {
    String[] types = { "java.lang.Object", "java.lang.Object", "java.lang.Object", "java.lang.Object","java.lang.Object", };
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "PackedObjectArray";
    default:
      return "No such input";
    }
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "PackedObjectArray";
    default:
      return "NO SUCH INPUT!";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Object0";
    case 1:
      return "Object1";
    case 2:
      return "Object2";
    case 3:
      return "Object3";
    case 4:
      return "Object4";
    default:
      return "No such output";
    }
  }
  
  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Object0";
    case 1:
      return "Object1";
    case 2:
      return "Object2";
    case 3:
      return "Object3";
    case 4:
      return "Object4";
    default:
      return "NO SUCH OUTPUT!";
    }
  }
  
  public void doit() {
    int nObjects = 5;
    Object[] ThePackage = new Object[nObjects];

    ThePackage = (Object[])this.pullInput(0);
    
    for (int objectIndex = 0; objectIndex < nObjects; objectIndex++) { 
      this.pushOutput(ThePackage[objectIndex],objectIndex);
    }
    
  }
}

