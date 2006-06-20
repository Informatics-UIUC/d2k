package ncsa.d2k.modules.projects.dtcheng.streams;

import ncsa.d2k.core.modules.ComputeModule;

public class ObjectStreamToArray extends ComputeModule {

  private int NumObjectsToCombine = 10;

  public void setNumObjectsToCombine(int value) {
    this.NumObjectsToCombine = value;
  }

  public int getNumObjectsToCombine() {
    return this.NumObjectsToCombine;
  }

  public String getModuleName() {
    return "ObjectStreamToArray";
  }

  public String getModuleInfo() {
    return "This modules combines a stream of objects into an object array.  "
        + "The output of the combined array is triggered by a null input or when the internal count equals NumObjectsToCombine.  ";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "Object";
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "Object";
    default:
      return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "java.lang.Object" };
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = { "[Ljava.lang.Object;" };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Object Array";
    default:
      return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Object Array";
    default:
      return "No such output";
    }
  }

  int Count = 0;
  int MaxCount = 1;
  Object[] Array;
  public void beginExecution() {
    Count = 0;
    MaxCount = 1;
    Array = new Object[MaxCount];
  }

  public void expandArray() {
    MaxCount = MaxCount * 2;
    Object[] newArray = new Object[MaxCount];
    for (int i = 0; i < Count; i++) {
      newArray[i] = Array[i];
    }
    Array = newArray;
  }

  public void outputArray() {
    Object[] ArrayCopy = new Object[Count];
    for (int i = 0; i < Count; i++) {
      ArrayCopy[i] = Array[i];
    }
    this.pushOutput(ArrayCopy, 0);
    beginExecution();
  }

  public void doit() {
    Object object = (Object) this.pullInput(0);
    if (object == null) {
      outputArray();
      return;
    }
    if (Count == MaxCount) {
      expandArray();
    }
    Array[Count++] = object;

    if (Count == NumObjectsToCombine) {
      outputArray();
      return;
    }
  }
}