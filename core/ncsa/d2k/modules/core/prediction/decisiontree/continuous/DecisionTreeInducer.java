package ncsa.d2k.modules.core.prediction.decisiontree.continuous;
import ncsa.d2k.modules.core.transform.sort.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.prediction.regression.continuous.*;
import ncsa.d2k.modules.core.prediction.mean.continuous.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
public class DecisionTreeInducer extends DecisionTreeInducerOpt {

  public void    setUseMeanNodeModels (boolean value) {       UseMeanNodeModels = value;}
  public boolean getUseMeanNodeModels ()              {return this.UseMeanNodeModels;}

  public void    setUseLinearNodeModels (boolean value) {       this.UseLinearNodeModels = value;}
  public boolean getUseLinearNodeModels ()              {return this.UseLinearNodeModels;}

  public void    setUseSimpleBooleanSplit (boolean value) {       this.UseSimpleBooleanSplit = value;}
  public boolean getUseSimpleBooleanSplit ()              {return this.UseSimpleBooleanSplit;}

  public void    setUseMidPointBasedSplit (boolean value) {       this.UseMidPointBasedSplit = value;}
  public boolean getUseMidPointBasedSplit ()              {return this.UseMidPointBasedSplit;}

  public void    setUseMeanBasedSplit (boolean value) {       this.UseMeanBasedSplit = value;}
  public boolean getUseMeanBasedSplit ()              {return this.UseMeanBasedSplit;}

  public void    setUsePopulationBasedSplit (boolean value) {       this.UsePopulationBasedSplit = value;}
  public boolean getUsePopulationBasedSplit ()              {return this.UsePopulationBasedSplit;}

  public void    setSaveNodeExamples (boolean value) {       this.SaveNodeExamples = value;}
  public boolean getSaveNodeExamples ()              {return this.SaveNodeExamples;}

  public  void    setMinDecompositionPopulation (int value) {       this.MinDecompositionPopulation = value;}
  public  int     getMinDecompositionPopulation ()          {return this.MinDecompositionPopulation;}

  public  void    setMinErrorReduction (double value) {       this.MinErrorReduction = value;}
  public  double  getMinErrorReduction ()          {return this.MinErrorReduction;}

  public void    setPrintEvolvingModels (boolean value) {       this.PrintEvolvingModels       = value;}
  public boolean getPrintEvolvingModels ()              {return this.PrintEvolvingModels;}

  public String getModuleName() {
    return "DecisionTreeInducer";
  }
  public String getModuleInfo() {
    return "DecisionTreeInducer";
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





  public void beginExecution() {
  }


  public void instantiateBiasFromProperties() {
    // Nothing to do in this case since properties are reference directly by the algorithm and no other control
    // parameters need be set.  This may not be the case in general so this stub is left open for future development.
  }


  public void doit() throws Exception {

    ExampleTable  exampleSet      = (ExampleTable)  this.pullInput(0);
    ErrorFunction errorFunction   = (ErrorFunction) this.pullInput(1);

    instantiateBiasFromProperties();

    Model model = null;

    model = generateModel(exampleSet, errorFunction);

    this.pushOutput(model, 0);
  }


}