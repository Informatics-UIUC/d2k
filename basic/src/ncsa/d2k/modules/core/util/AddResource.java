package ncsa.d2k.modules.core.util;

import ncsa.d2k.core.modules.*;

/**
 * a base class for modules that generate an object and then register it with
* the resource manager.
* the prepObj method must be overriden. the other methods are optional for overriding.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author vered
 * @version 1.0
 */
public abstract class AddResource  extends DataPrepModule{
  public AddResource() {
  }

  public String[] getInputTypes(){
    String[] types = {};
    return types;
  }

  public String getInputName(int index){
    switch(index){
        default: return "no such input";
    }
  }


  public String getInputInfo(int index){
    switch(index){
        default: return "no such input";
    }
  }


  public String[] getOutputTypes(){
    String[] types = {"java.lang.Object"};
    return types;
  }

  public String getOutputName(int index){
    switch(index){
      case 0: return "Resource";
        default: return "no such output";
    }
  }


  public String getOutputInfo(int index){
    switch(index){
      case 0: return "The resource prepared/loaded by this module.";
        default: return "no such output";
    }
  }

public String getModuleName(){return "Add Resource Module";}
  public String getModuleInfo(){return "This module adds an object as a resource, " +
        "it registers the object as a resource with the Resource Manager identified " +
  "by the String <i>Resource ID</i>.";
}

  private String rscID;
  public void setRscID(String id){rscID = id;}
  public String getRscID(){return rscID;}

  public PropertyDescription[] getPropertiesDescriptions()
{
       PropertyDescription[] pds = new PropertyDescription[1];
       pds[0] = new PropertyDescription("rscID", "Resource ID",
                                        "The resource ID for the object prepared/loaded by this module.");
       return pds;
}


  protected void doit(){

    Object obj = prepObject();
    if(obj != null){
      this.setResource(rscID, obj);
      pushOutput(obj, 0);
    }else System.out.println("Could not initialize the resource");
  }

  abstract protected Object prepObject();



}
