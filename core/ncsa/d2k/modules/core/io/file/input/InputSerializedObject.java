/*
 * InputSerializedObject.java
 * Reads a serialized object from a file.
 * @author David Tcheng
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */

package ncsa.d2k.modules.core.io.file.input;

import  java.io.*;
import  ncsa.d2k.infrastructure.modules.*;

/*
 * Reads a serialized object from a file.
 * @author David Tcheng
 */
public class InputSerializedObject extends InputModule
    implements HasNames, HasProperties, java.io.Serializable {

  /**
   * put your documentation comment here
   * @return
   */
  public String getModuleInfo () {
    StringBuffer sb = new StringBuffer("Reads a serialized object from a file PROPS-usePropFileName"
        + " is true if you don't want it to wait for an input for a filename and instead"
        + " use the filename in the properties");
    return  sb.toString();
  }

  /**
   * put your documentation comment here
   * @return
   */
  public String getModuleName () {
    return  "Reads a serialized object from a file.";
  }

  /**
   * put your documentation comment here
   * @return
   */
  public String[] getInputTypes () {
    String[] in =  {
      "java.lang.String"
    };
    return  in;
  }

  /**
   * put your documentation comment here
   * @return
   */
  public String[] getOutputTypes () {
    String[] out =  {
      "java.lang.Object"
    };
    return  out;
  }

  /**
   * put your documentation comment here
   * @param i
   * @return
   */
  public String getInputInfo (int i) {
    if (i == 0)
      return  "The filename to read in";
    return  "no such input!";
  }

  /**
   * put your documentation comment here
   * @param i
   * @return
   */
  public String getInputName (int i) {
    return  "no such input!";
  }

  /**
   * put your documentation comment here
   * @param i
   * @return
   */
  public String getOutputInfo (int i) {
    if (i == 0)
      return  "SerializedObject";
    return  "no such output!";
  }

  /**
   * put your documentation comment here
   * @param i
   * @return
   */
  public String getOutputName (int i) {
    if (i == 0)
      return  "SerializedObject";
    return  "no such output!";
  }
  ////////////////
  // Properties //
  ////////////////
  private String FileName;      // = "ObjectFile.ser";

  /**
   * put your documentation comment here
   * @param value
   */
  public void setFileName (String value) {
    this.FileName = value;
  }

  /**
   * put your documentation comment here
   * @return
   */
  public String getFileName () {
    return  this.FileName;
  }
  private boolean usePropFileName = false;

  /**
   * put your documentation comment here
   * @param b
   */
  public void setUsePropFileName (boolean b) {
    usePropFileName = b;
  }

  /**
   * put your documentation comment here
   * @return
   */
  public boolean getUsePropFileName () {
    return  usePropFileName;
  }

  //////////////////
  //isReady
  /////////////////
  public boolean isReady () {
    if (usePropFileName) {
      return  true;
    }
    else {
      return  super.isReady();
    }
  }

  //////////
  // Doit //
  //////////
  public void doit () {
    try {
      if (!usePropFileName) {
        FileName = (String)(pullInput(0));
      }
      FileInputStream file = new FileInputStream(FileName);
      ObjectInputStream in = new ObjectInputStream(file);
      Object object = null;
      try {
        object = (Object)in.readObject();
      } catch (java.lang.ClassNotFoundException IOE) {
        System.out.println("java.lang.ClassNotFoundException " + IOE);
      }
      in.close();
      pushOutput(object, 0);
    } catch (java.io.IOException IOE) {
      System.out.println("IOException!!! could not open " + FileName);
    }
  }
}



