package ncsa.d2k.modules.projects.dtcheng.inducers;

import java.beans.PropertyVetoException;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;

public class NaiveBayesInducer extends NaiveBayesInducerOpt {
  
  
  private int     NumRounds = 0;
  public  void    setNumRounds (int value) {       this.NumRounds = value;}
  public  int     getNumRounds ()          {return this.NumRounds;}

  private int     Direction = 0;
  public  void    setDirection (int value) {       this.Direction = value;}
  public  int     getDirection ()          {return this.Direction;}
  
  int numBiasDimensions = 2;
  
  public PropertyDescription[] getPropertiesDescriptions() {
    
    PropertyDescription[] pds = new PropertyDescription[numBiasDimensions];
    
    int i = 0;
    
    pds[i++] = new PropertyDescription(
     "NumRounds",
     "NumRounds controls the number of features added or removed",
     "NumRounds controls the number of features added or removed");
    
    pds[i++] = new PropertyDescription(
     "Direction",
     "Direction can be -1, 0, or 1",
     "Direction can be -1, 0, or 1");
    
    
    
    return pds;
  }
  
  
  public String getModuleName() {
    return "Naive Bayes Inducer";
  }
  
  public String getInputName(int i) {
    switch(i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "Error!  No such input.";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "Error!  No such input.";
    }
  }
  public String[] getInputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
       "ncsa.d2k.modules.core.prediction.ErrorFunction"
    };
    return types;
  }
  
  public String getOutputName(int i) {
    switch(i) {
      case  0: return "Model";
      default: return "Error!  No such output.";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Model";
      default: return "Error!  No such output.";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.model.Model"};
    return types;
  }
  
  public void doit() throws Exception {

    Object InputObject1 = this.pullInput(0);
    Object InputObject2 = this.pullInput(1);

    if ((InputObject1 == null) || (InputObject2 == null)) {
      this.pushOutput(null, 0);
      return;
    }

    ExampleTable exampleSet = (ExampleTable) InputObject1;
    ErrorFunction errorFunction = (ErrorFunction) InputObject2;

    Model model = null;

    model = generateModel(exampleSet, errorFunction);

    this.pushOutput(model, 0);
  }

}
