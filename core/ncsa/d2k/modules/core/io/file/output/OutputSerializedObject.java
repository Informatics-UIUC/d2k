package ncsa.d2k.modules.core.io.file.output;

import java.io.*;
import ncsa.d2k.core.modules.*;

/**
OutputSerializedObject.java
Writes a serialized object to a file.
@author David Tcheng
*/
public class OutputSerializedObject extends OutputModule {

/*    public String getModuleInfo() {
  return "<html>  <head>      </head>  <body>    Writes a serialized object to a file. PROPS-usePropFileName is true if you     don't want it to wait for an input for a filename and instead use the     filename in the properties  </body></html>";
 }
    */

    public String getModuleInfo() {
        StringBuffer sb = new StringBuffer("<p>Overview: ");
        sb.append("Write a serializable object to a file. ");
        sb.append("The Object must implement the java.io.Serializable interface.");
        return sb.toString();
    }

    public String getModuleName() {
        //return "Writes a serialized object to a file.";
        return "Write a Serializable Object to a File";
    }

    public String[] getInputTypes() {
        String[] types = {"java.lang.Object", "java.lang.String"};
        return types;
    }

    public String[] getOutputTypes() {
        String[] types = {};
        return types;
    }

    public String getInputInfo(int i) {
        switch (i) {
            case 0: return "The Object to Serialize.";
            case 1: return "The filename to write to.";
            default: return "No such input";
        }
    }

    public String getInputName(int i) {
        switch(i) {
            case 0:
                return "Object to Serialize";
            case 1:
                return "File Name";
            default: return "NO SUCH INPUT!";
        }
    }

    public String getOutputInfo(int i) {
        switch (i) {
            default: return "No such output";
        }
    }

    public String getOutputName(int i) {
        switch(i) {
            default: return "NO SUCH OUTPUT!";
        }
    }

    ////////////////
    // Properties //
    ////////////////

/*  private String     FileName;// = "ObjectFile.ser";
  public  void    setFileName (String value) {       this.FileName = value;}
  public  String  getFileName ()             {return this.FileName; }

  private boolean usePropFileName=false;
  public void setUsePropFileName(boolean b) {usePropFileName=b;}
  public boolean getUsePropFileName()	{return usePropFileName;}

    //////////////////
    //isReady
    /////////////////
  public boolean isReady(){
   if(usePropFileName){
    if(inputFlags[0]>0){
     return true;
    }
    else
     return false;
   }
   else{
    return super.isReady();
   }
  }
    */


    //////////
    // Doit //
    //////////

    public void doit() throws Exception {
        Object object = pullInput(0);
        String FileName = (String)pullInput(1);
        //if(!usePropFileName){
        //	FileName=(String)(pullInput(1));
        //}
        //try
        // {
        FileOutputStream file = new FileOutputStream(FileName);
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(object);
        out.flush();
        out.close();
        //  }
        //catch (java.io.IOException IOE)
        // {
        // System.out.println("IOException");
        // }
    }
}