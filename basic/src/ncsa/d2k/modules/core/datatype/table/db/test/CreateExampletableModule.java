package ncsa.d2k.modules.core.datatype.table.db.test;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.core.datatype.table.Table;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CreateExampletableModule extends ComputeModule {

  public String getModuleInfo() {
    return "<p>Overview: This module creates an example table from a table. " +
        "The output port should be linked to a module that selects the output " +
        "and input features.</p>";

  }


  public String getModuleName() {
    return "Create Example Table Module";
  }


  public String getInputInfo(int parm1) {
    switch(parm1){
      case 0: return "Table";

      default: return "No such input";
    }
  }


  public String getInputName(int parm1) {
    switch(parm1){
      case 0: return "Table";

      default: return "No such input";
    }

  }



  public String getOutputInfo(int parm1) {
    switch(parm1){
      case 0: return "Example Table";

      default: return "No such output";
    }

  }


  public String getOutputName(int parm1) {
    switch(parm1){
       case 0: return "Example Table";

       default: return "No such output";
     }

  }





  public String[] getInputTypes() {
    String[] retVal = new String[1];
    retVal[0] = "ncsa.d2k.modules.core.datatype.table.Table";
    return retVal;
  }


  public String[] getOutputTypes() {
    String[] retVal = new String[1];
   retVal[0] = "ncsa.d2k.modules.core.datatype.table.ExampleTable";
   return retVal;

  }


  public void doit(){
    Table table = (Table)this.pullInput(0);

    pushOutput(table.toExampleTable(), 0);

  }//doit


}