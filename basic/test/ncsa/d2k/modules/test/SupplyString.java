package ncsa.d2k.modules.test;

import ncsa.d2k.core.modules.InputModule;
import ncsa.d2k.core.modules.PropertyDescription;

public class SupplyString extends InputModule {

  private String string = "";
  
  public void setString(String s) {
    string = s;
  }
  public String getString() {
    return string;
  }

  public String[] getInputTypes() {
    return null;
  }

  public String[] getOutputTypes() {
    return new String[] {
      "java.lang.String"
    };
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "The string to push";
  }
  
  public String getOutputName(int i) {
	    return "String";
  }

  public String getModuleInfo() {
    return "Push out the string";
  }

  public PropertyDescription [] getPropertiesDescriptions () {
      PropertyDescription [] pds = new PropertyDescription [1];
      pds[0] = new PropertyDescription ("string", "The string to push",
                                        "The string to push to the output");
      return pds;
   }
  
  public void doit() throws Exception {
    pushOutput(getString(), 0);
  }
}
