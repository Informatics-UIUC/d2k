package ncsa.d2k.modules.projects.dtcheng.io.print;


import ncsa.d2k.core.modules.*;

public class Print3ObjectInOrder extends OutputModule {
  
  private String Label1    = "";
  public  void   setLabel1(String value) {       this.Label1       = value;}
  public  String getLabel1()             {return this.Label1;}
  private String Label2    = "";
  public  void   setLabel2(String value) {       this.Label2       = value;}
  public  String getLabel2()             {return this.Label2;}
  private String Label3    = "";
  public  void   setLabel3(String value) {       this.Label3       = value;}
  public  String getLabel3()             {return this.Label3;}
  
  public String getModuleInfo() {
    return "Print3ObjectInOrder";
  }
  public String getModuleName() {
    return "Print3ObjectInOrder";
  }
  
  public String[] getInputTypes() {
    String[] types = {
      "java.lang.Object",
      "java.lang.Object",
      "java.lang.Object",
    };
    return types;
  }
  
  public String[] getOutputTypes() {
    String[] types = {};
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Object1";
      case 1: return "Object2";
      case 2: return "Object3";
      default: return "No such input";
    }
  }
  
  public String getInputName(int i) {
    switch(i) {
      case 0: return "Object1";
      case 1: return "Object2";
      case 2: return "Object3";
      default: return "No such input";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      default: return "No such output";
    }
  }
  
  public String getOutputName(int i) {
    switch(i) {
      default: return "NO SUCH OUTPUT!";
    }
  }
  
  public void doit() {
    
    Object object1 = (Object) this.pullInput(0);
    Object object2 = (Object) this.pullInput(1);
    Object object3 = (Object) this.pullInput(2);
    
    System.out.println(Label1);
    System.out.println(object1);
    
    System.out.println(Label2);
    System.out.println(object2);
    
    System.out.println(Label3);
    System.out.println(object3);
  }
}