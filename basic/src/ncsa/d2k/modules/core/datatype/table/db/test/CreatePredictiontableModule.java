package ncsa.d2k.modules.core.datatype.table.db.test;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CreatePredictiontableModule extends ComputeModule {

  public String getModuleInfo() {
    return "<p>Overview: This module creates a prediction table from an example table.</p>" ;


  }


  public String getModuleName() {
    return "Create Prediction Table Module";
  }


  public String getInputInfo(int parm1) {
    switch(parm1){
      case 0: return "Example Table";

      default: return "No such input";
    }
  }


  public String getInputName(int parm1) {
    switch(parm1){
      case 0: return "Example Table";

      default: return "No such input";
    }

  }



  public String getOutputInfo(int parm1) {
    switch(parm1){
      case 0: return "Prediction Table";

      default: return "No such output";
    }

  }


  public String getOutputName(int parm1) {
    switch(parm1){
       case 0: return "Prediction Table";

       default: return "No such output";
     }

  }





  public String[] getInputTypes() {
    String[] retVal = new String[1];
    retVal[0] = "ncsa.d2k.modules.core.datatype.table.ExampleTable";
    return retVal;
  }


  public String[] getOutputTypes() {
    String[] retVal = new String[1];
   retVal[0] = "ncsa.d2k.modules.core.datatype.table.PredictionTable";
   return retVal;

  }


  public void doit(){
    ExampleTable table = (ExampleTable)this.pullInput(0);

    pushOutput(table.toPredictionTable(), 0);

  }//doit


}