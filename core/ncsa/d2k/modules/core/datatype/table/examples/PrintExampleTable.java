package ncsa.d2k.modules.core.datatype.table.examples;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

public class PrintExampleTable extends OutputModule {

  private boolean PrintFeatureNames          = true;
  public  void    setPrintFeatureNames       (boolean value) {       this.PrintFeatureNames       = value;}
  public  boolean getPrintFeatureNames       ()              {return this.PrintFeatureNames;}

  private boolean PrintNumExamples          = true;
  public  void    setPrintNumExamples       (boolean value) {       this.PrintNumExamples       = value;}
  public  boolean getPrintNumExamples       ()              {return this.PrintNumExamples;}

  private boolean PrintExampleValues          = true;
  public  void    setPrintExampleValues       (boolean value) {       this.PrintExampleValues       = value;}
  public  boolean getPrintExampleValues       ()              {return this.PrintExampleValues;}

  private String Label    = "";
  public  void   setLabel (String value) {       this.Label       = value;}
  public  String getLabel ()             {return this.Label;}

  public String getModuleName() {
    return "PrintExampleTable";
  }
  public String getModuleInfo() {
    return "PrintExampleTable";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0: return "ExampleTable";
    }
    return "";
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "ExampleTable";
    }
    return "";
  }
  public String[] getInputTypes() {
    String [] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0: return "ExampleTable";
    }
    return "";
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "ExampleTable";
    }
    return "";
  }
  public String[] getOutputTypes() {
    String [] out = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return out;
  }


  public void doit() {

    ExampleTable exampleSet = (ExampleTable) this.pullInput(0);
    int numExamples = exampleSet.getNumExamples();
    int numInputs = exampleSet.getNumInputs(0);
    int numOutputs = exampleSet.getNumOutputs(0);


    if (PrintNumExamples)
      System.out.println(Label + " numExamples = " + numExamples);

    if (PrintFeatureNames) {
      System.out.println(Label + " numInputs = " + numInputs);
      for (int v = 0; v < numInputs; v++) {
        System.out.println(Label + "  " + (v + 1) + " : " + exampleSet.getInputName(v));
      }
      System.out.println(Label + " numOutputs = " + numOutputs);
      for (int v = 0; v < numOutputs; v++) {
        System.out.println(Label + "  " + (v + 1) + " : " + exampleSet.getOutputName(v));
      }
    }

    if (PrintExampleValues) {
      for (int e = 0; e < numExamples; e++) {
        System.out.println(Label + "  e" + (e + 1));
        System.out.println(Label + "    input:");
        for (int v = 0; v < numInputs; v++) {
          System.out.println(Label + "    " + exampleSet.getInputName(v)  + " = " + exampleSet.getInputDouble(e, v));
        }
        System.out.println(Label + "    output:");
        for (int v = 0; v < numOutputs; v++) {
          System.out.println(Label + "    " + exampleSet.getOutputName(v) + " = " + exampleSet.getOutputDouble(e, v));
        }
      }
    }

    this.pushOutput(exampleSet, 0);
  }
}