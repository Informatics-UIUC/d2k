package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;
import java.util.*;

public class CreateOneWayMapping extends InputModule {

  private int FeatureToConsiderNumber1 = 1;

  public void setFeatureToConsiderNumber1(int value) {
    this.FeatureToConsiderNumber1 = value;
  }

  public int getFeatureToConsiderNumber1() {
    return this.FeatureToConsiderNumber1;
  }

  private int FeatureToConsiderNumber2 = 2;

  public void setFeatureToConsiderNumber2(int value) {
    this.FeatureToConsiderNumber2 = value;
  }

  public int getFeatureToConsiderNumber2() {
    return this.FeatureToConsiderNumber2;
  }


  public String getModuleName() {
    return "CreateOneWayMapping";
  }

  public String getModuleInfo() {
    return "CreateOneWayMapping";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] out = { "[Ljava.lang.String;" };
    return out;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "OneWayMappingHashtable";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "OneWayMappingHashtable";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = { "java.util.Hashtable" };
    return out;
  }

  Hashtable OneWayMappingHashtable = null;

  public void beginExecution() {

    OneWayMappingHashtable = new Hashtable();

  }

  public void doit() throws Exception {

    String[] StringArray = (String[]) this.pullInput(0);

    if (StringArray == null) {

      this.pushOutput(OneWayMappingHashtable, 0);

      beginExecution();

      return;
    }

    String Key1 = StringArray[FeatureToConsiderNumber1 - 1];
    String Key2 = StringArray[FeatureToConsiderNumber2 - 1];

    if (!OneWayMappingHashtable.containsKey(Key1)) {
      OneWayMappingHashtable.put(Key1, Key2);
    }

  }

}