package ncsa.d2k.modules.projects.dtcheng.io.print;


import ncsa.d2k.core.modules.*;

public class Print2ObjectInOrder extends OutputModule {
  
  private String Label1    = "";
  public  void   setLabel1(String value) {       this.Label1       = value;}
  public  String getLabel1()             {return this.Label1;}
  private String Label2    = "";
  public  void   setLabel2(String value) {       this.Label2       = value;}
  public  String getLabel2()             {return this.Label2;}
  
  public String getModuleInfo() {
    return "Print2ObjectInOrder";
  }
  public String getModuleName() {
    return "Print2ObjectInOrder";
  }
  
  public String[] getInputTypes() {
    String[] types = {"java.lang.Object", "java.lang.Object"};
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
      default: return "No such input";
    }
  }
  
  public String getInputName(int i) {
    switch(i) {
      case 0: return "Object1";
      case 1: return "Object2";
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
    
    System.out.println(Label1);
    System.out.println(object1);
    
    System.out.println(Label2);
    System.out.println(object2);
  }
}