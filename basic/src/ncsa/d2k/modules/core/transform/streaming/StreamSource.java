package ncsa.d2k.modules.core.transform.streaming;

import java.util.*;

import ncsa.d2k.core.modules.DataPrepModule;


public class StreamSource extends DataPrepModule  {

  /**
   * This class takes a list input, and converts it to a stream of objects.
   * Used to allow cross-item streaming in D2KSL.
   */

  List theList = null;
  int listIndex = 0;


  public StreamSource() {

  }

  /**
   * Return the name of this module.
   * @return the dislay name for this module.
   */
  public String getModuleName() {
    return "Stream Source";
  }

  /**
   Return information about the module.
   @return A detailed description of the module.
   */
  public String getModuleInfo () {
      String s = "<p>Overview: ";
      s += "This module takes a list of objects and pushes them out as single ";
      s += "objects in a stream.  It also outputs the number of objects that ";
      s += "will be pushed out in total.  It is the other end of a couple with ";
      s += "StreamCatcher.  ";
      return  s;
  }


  /**
   * Return the name of a specific input.
   * @param parm1 The index of the input.
   * @return The name of the input.
   */
  public String getInputName (int parm1) {
      if (parm1 == 0) {
        return  "A list of objects to stream.";
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
        return  "A list of objects to stream.";
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
          "java.util.List"
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
      return "Object.";
    }
    else if (parm1 == 1) {
      return "Object count.";
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
        return  "Object in the stream";
      }
      else if (parm1 == 1) {
        return "Number of objects in the stream.";
      }
      else {
          return  "";
      }
  }


  public boolean isReady () {
    return (super.isReady() || (theList != null));
  }


  /**
   * put your documentation comment here
   * @return
   */
  public String[] getOutputTypes () {
      String[] out =  {
          "java.lang.Object", "java.lang.Integer"
      };
      return  out;
  }

  public void doit () {
    System.out.println("starting streamsource.");
    if (theList == null) {
      System.out.println("streamsource list init.");
      theList = (List)this.pullInput(0);
      this.pushOutput(new Integer(theList.size()), 1);
      System.out.println("streamsource PUSHED list size: "+theList.size());
    }
    if (this.listIndex < theList.size()) {
      System.out.println("streamsource pushing output number: "+listIndex);
      this.pushOutput(theList.get(listIndex++),0);
    } else {
      System.out.println("streamsource NULLIFYIN.");
      theList = null;
    }

  }

}
