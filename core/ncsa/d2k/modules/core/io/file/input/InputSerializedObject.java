package ncsa.d2k.modules.core.io.file.input;

import java.io.*;
import ncsa.d2k.core.modules.*;

/*
* Reads a serialized object from a file.
* @author David Tcheng
 */
public class InputSerializedObject extends InputModule {

    /**
     * put your documentation comment here
     * @return
     */
  /*public String getModuleInfo () {
  return "<html>  <head>      </head>  <body>    Reads a serialized object from a file PROPS-usePropFileName is true if you     don't want it to wait for an input for a filename and instead use the     filename in the properties  </body></html>";
 }*/

    public String getModuleName() {
        return "Read a Serialized Object from a File";
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String getModuleInfo () {
        StringBuffer sb = new StringBuffer("<p>Overview: ");
        sb.append("This module will read a serialized object from a file.");
        return sb.toString();
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getInputTypes () {
        String[] types = {"java.lang.String"};
        return types;
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getOutputTypes () {
        String[] types = {"java.lang.Object"};
        return types;
    }

    /**
     * put your documentation comment here
     * @param i
     * @return
     */
    public String getInputInfo (int i) {
        switch (i) {
            case 0: return "The filename of the serialized Object to read in.";
            default: return "No such input";
        }
    }

    /**
     * put your documentation comment here
     * @param i
     * @return
     */
    public String getInputName (int i) {
        switch(i) {
            case 0:
                return "File Name";
            default: return "NO SUCH INPUT!";
        }
    }

    /**
     * put your documentation comment here
     * @param i
     * @return
     */
    public String getOutputInfo (int i) {
        switch (i) {
            case 0: return "The Object that was read in.";
            default: return "No such output";
        }
    }

    /**
     * put your documentation comment here
     * @param i
     * @return
     */
    public String getOutputName (int i) {
        switch(i) {
            case 0:
                return "Deserialized Object";
            default: return "NO SUCH OUTPUT!";
        }
    }
    ////////////////
    // Properties //
    ////////////////
/*  private String FileName;      // = "ObjectFile.ser";

 /**
  * put your documentation comment here
  * @param value
  */
/*  public void setFileName (String value) {
    this.FileName = value;
  }

  /**
   * put your documentation comment here
   * @return
   */
/*  public String getFileName () {
    return  this.FileName;
  }
  private boolean usePropFileName = false;

  /**
   * put your documentation comment here
   * @param b
   */
/*  public void setUsePropFileName (boolean b) {
    usePropFileName = b;
  }

  /**
   * put your documentation comment here
   * @return
   */
/*  public boolean getUsePropFileName () {
    return  usePropFileName;
  }
  */

 //////////////////
 //isReady
 /////////////////
  /*public boolean isReady () {
    if (usePropFileName) {
      return  true;
    }
    else {
      return  super.isReady();
    }
  }*/

 //////////
 // Doit //
 //////////
    public void doit () throws Exception {
        //    String FileName = null;
        //try {
        //if (!usePropFileName) {
        String FileName = (String)(pullInput(0));
        //}
        FileInputStream file = new FileInputStream(FileName);
        ObjectInputStream in = new ObjectInputStream(file);
        Object object = null;
        //try {
        object = (Object)in.readObject();
        //} catch (java.lang.ClassNotFoundException IOE) {
        //  System.out.println("java.lang.ClassNotFoundException " + IOE);
        // }
        in.close();
        pushOutput(object, 0);
        //} catch (java.io.IOException IOE) {
        //  System.out.println("IOException!!! could not open " + FileName);
        //}
    }
}