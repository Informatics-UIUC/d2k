package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class ObjectArrayToStream extends ComputeModule {

  public String getModuleName() {
    return "ObjectArrayToStream";
  }

  public String getModuleInfo() {
    return "This module segments an array of objects and produces an object stream.  ";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "ObjectArray";
    default:
      return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "ObjectArray";
    default:
      return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "[java.lang.Object;" };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Object Stream";
    default:
      return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Object Stream";
    default:
      return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "java.lang.Object" };
    return types;
  }

  boolean InitialExecution;

  int NumObjects;

  int ObjectIndex;

  Object[] ObjectArray;

  public void beginExecution() {
    InitialExecution = true;
    NumObjects = 0;
    ObjectIndex = 0;
    ObjectArray = null;
  }

  public boolean isReady() {
    boolean value = false;

    if (InitialExecution && (this.getFlags()[0] > 0)) {
      value = true;
    } else {
      if (ObjectIndex < NumObjects)
        value = true;
    }
    return value;
  }

  public void doit() {

    if (InitialExecution) {

      InitialExecution = false;

      ObjectArray = (Object[]) this.pullInput(0);

      if (ObjectArray == null) {
        this.pushOutput(null, 0);
        beginExecution();
        return;
      }

      NumObjects = ObjectArray.length;

    } else {

      //if (ObjectIndex < NumObjects) {
      this.pushOutput(ObjectArray[ObjectIndex], 0);
      //}
      ObjectIndex++;

      if (ObjectIndex == NumObjects) {
        this.pushOutput(null, 0);
        beginExecution();
      }

    }
  }
}