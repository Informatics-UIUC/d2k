package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author vered goren
 * @version 1.0
 *
 * receives a model  and a data set and
 * updates the model with the new data. outputs the incremented model.
 *
 * @todo after this module is done, move to a suitable package
 * ncsa.d2k.modules.core.prediction
 *
 * @todo revise of the output model. should it be a deep copy of the model?
 * problem is when this module changes the output model in the next iteration
 * while the model may be used by other modules..... right now this module
 * makes a deep copy of the input model before updating it.
 */

public  class IncrementingModule extends ModelProducerModule {


protected UpdateableModelModule model;


  public String[] getInputTypes() {
    String[] arr = new String[2];
    arr[0] = "ncsa.d2k.modules.projects.vered.svm.UpdateableModelModule";
    arr[1] = "ncsa.d2k.modules.core.datatype.table.ExampleTable";

    return arr;
  }

  public String[] getOutputTypes() {
    String[] arr = new String[1];
   arr[0] = "ncsa.d2k.modules.projects.vered.svm.UpdateableModelModule";

   return arr;

  }

  private boolean deepCopy = true;
  public void setDeepCopy(boolean bl){deepCopy = bl;}
  public boolean getDeepCopy(){return deepCopy ;}

  public PropertyDescription[] getPropertiesDescriptions(){
     PropertyDescription[] pds = new PropertyDescription[1];
     pds[0] = new PropertyDescription("deepCopy", "Create Deep Copy of Input Model",
                                      "Set this property to true if you wish the model to be copied before retraining.");
     return pds;
   }




  protected void doit() throws java.lang.Exception {
     ExampleTable tbl = (ExampleTable) pullInput(1);
     model = ((UpdateableModelModule) pullInput(0));
     if(deepCopy)
       model = model.copy();
    model.update(tbl);

    pushOutput(model, 0);
  }

  public String getModuleInfo() {
  return "<p>Overview: This module receives an updateable model " +
      "and a data set and retrains the model.</p>" +
      "<p>Detailed Description: The module receives an <i>" +
      "Updateable Model</i> and an <i>Example Table</i> as inputs. " +
      "The module creates a deep copy of the model and then updates the model " +
      "with the input table. Thus the older version of the model can be used " +
      "by other modules in an itinerary without being affected by the retraining." +
      "After the retraining the model " +
      "is being output.</p>";

  /*"If the input model is to be used by other modules in the itinerary then you should " +
   "set 'Create Deep Copy' property to true, in order to not distort the correctness " +
   "of the models, generated during runtime." */
  }

  public String getModuleName() {
   return "Incrementing Module";
   }



  public String getInputInfo(int parm1) {
    switch(parm1){
      case 0:
        return "An updateable model to be retrained incrementally";
      case 1: return "Training Data";
      default: return "no such input";
    }
  }

  public String getInputName(int parm1) {
   switch(parm1){
     case 0:
       return "Updateable Model";
     case 1: return "Example Table";
     default: return "no such input";
   }
 }


  public String getOutputInfo(int parm1) {
    switch(parm1){
     case 0:
       return "An updateable model after retraining.";

     default: return "no such output";
   }

  }

  public String getOutputName(int parm1) {
   switch(parm1){
    case 0:
      return "Updateable Model";

    default: return "no such output";
  }

 }


}