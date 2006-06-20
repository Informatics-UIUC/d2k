package ncsa.d2k.modules.projects.dtcheng.generators;

import ncsa.d2k.core.modules.ComputeModule;

public class GenerateInteger
    extends ComputeModule {


  private int Value = 1;

  public void setValue(int value) {
    this.Value = value;
  }

  public int getValue() {
    return this.Value;
  }
  
  public String getModuleInfo() {
    return "CreateInteger";
  }

  public String getModuleName() {
    return "CreateInteger";
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Integer"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Integer";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Integer";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void readInputs () {
  }
  

  public void executeModule () {
    Integer x = new Integer(Value);
    this.pushOutput(x, 0);
  }
  
  // generic form for celerity compatibility
  
  public void doit() throws Exception {
    
    readInputs();
    
    executeModule();
    
  }
}