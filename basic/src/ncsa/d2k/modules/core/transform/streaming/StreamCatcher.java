package ncsa.d2k.modules.core.transform.streaming;

import java.util.*;

import ncsa.d2k.core.modules.DataPrepModule;


public class StreamCatcher extends DataPrepModule  {

  int objCnt = -1;
  LinkedList streamedObjects = new LinkedList();

  /**
   * This class catches the output of a streaming module, and converts it into
   * a list of objects.  This allows D2KSL to operate with streaming itineraries
   * broken across different items.
   */

  public StreamCatcher() {

  }

  public void beginExecution() {
     streamedObjects.clear();
  }

  /**
   * Return the name of this module.
   * @return the dislay name for this module.
   */
  public String getModuleName() {
    return "Stream Catcher";
  }

  /**
   Return information about the module.
   @return A detailed description of the module.
   */
  public String getModuleInfo () {
      String s = "<p>Overview: ";
      s += "This module takes a stream of objects, one at a time, through its ";
      s += "first input slot.  The second input slot gives the count of how ";
      s += "many objects to expect.  When the expected number of objects have ";
      s += "been pushed through the door, this module pops out a list containing ";
      s += "all of the objects that have been streamed in.  This allows ";
      s += "streaming itineraries to operate correctly across item boundaries ";
      s += "in D2KSL.  (Itineraries of this sort include file loading and tagging ";
      s += "in T2K, which could not be separated into different items if ";
      s += "stream catching were not implemented here.";
      return  s;
  }


  /**
   * Return the name of a specific input.
   * @param parm1 The index of the input.
   * @return The name of the input.
   */
  public String getInputName (int parm1) {
      if (parm1 == 0) {
        return  "An object in the stream.";
      }
      else if (parm1 == 1) {
        return "Stream count.";
      }
      else {
          return  "No such input.";
      }
  }

  /**
   Return a description of a specific input.
   @param i The index of the input
   @return The description of the input
   */
  public String getInputInfo (int parm1) {
      if (parm1 == 0) {
        return  "One object in a stream of many.";
      }
      else if (parm1 == 1) {
        return "The number of objects which are to be streamed.";
      }
      else {
          return  "No such input.";
      }
  }

  /**
   Return a String array containing the datatypes the inputs to this
   module.
   @return The datatypes of the inputs.
   */
  public String[] getInputTypes () {
      String[] in =  {
          "java.lang.Object", "java.lang.Integer"
      };
      return  in;
  }


  /**
   * Return the name of a specific output.
   * @param parm1 The index of the output.
   * @return The name of the output.
   */
  public String getOutputName(int parm1) {
    if (parm1 == 0) {
      return "List of Streamed Objects";
    }
    else {
      return "No such input.";
    }
  }

  /**
   * put your documentation comment here
   * @param parm1
   * @return
   */
  public String getOutputInfo (int parm1) {
      if (parm1 == 0) {
          return  "List";
      }
      else {
          return  "";
      }
  }

  /**
   * put your documentation comment here
   * @return
   */
  public String[] getOutputTypes () {
      String[] out =  {
          "java.util.List"
      };
      return  out;
  }


  /**
   * override isready to fire if *either* output is ready.
   * @return boolean
   */
  public boolean isReady(){
    return ((getFlags()[0] > 0) || (getFlags()[1] > 0));
  }


  public void doit () {

    if (getFlags()[1] > 0) {
      //System.out.println("sc: GONNA pull input intvalue.  val is: "+objCnt);
      objCnt = ( (Integer)this.pullInput(1)).intValue();
      //System.out.println("sc: pulled input intvalue.  val is: "+objCnt);
      return;
    }

    if (getFlags()[0] > 0) {
      //System.out.println("sc: GONNA pull input object.");
      while (this.getFlags()[0] > 0) {
        //System.out.println("sc: pulling another input");
        streamedObjects.add(this.pullInput(0));
        //System.out.println("sc: DONE pulling another input");
      }
    }

    if (streamedObjects.size() == objCnt) {
      //System.out.println("sc: finished loop");
      this.pushOutput(streamedObjects, 0);
      //System.out.println("sc: pushed output -- byebye");
    } else {
      //System.out.println("sc: size not equal to count! size is: "+streamedObjects.size()+
      //                   "count is: "+objCnt);
    }
  }

}
