package ncsa.d2k.modules.core.io.file.output;

import java.io.*;
import ncsa.d2k.infrastructure.modules.*;

/**
OutputSerializedObject.java
Writes a serialized object to a file.
@author David Tcheng
*/
public class OutputSerializedObject extends OutputModule implements HasNames, HasProperties, java.io.Serializable 
  {

    public String getModuleInfo() 
      {
      StringBuffer sb = new StringBuffer("Writes a serialized object to a file. PROPS-usePropFileName"
	  					+" is true if you don't want it to wait for an input for a filename and instead"+
						" use the filename in the properties");
      return sb.toString();
      }

    public String getModuleName() 
      { 
      return "Writes a serialized object to a file.";
      }

    public String[] getInputTypes() 
      {
      String [] in = {"java.lang.Object", 
	  					"java.lang.String"};
      return in;
      }

    public String[] getOutputTypes() 
      {
      String [] out = {};
      return out;
      }

    public String getInputInfo(int i) 
      {
      if(i == 0)
        return "The Object to Serialize.";
	  if (i==1)
		  return "The filename to write to";	
			
      return "no such input!";
      }

    public String getInputName(int i) 
      {
      if(i ==0)
        return "SerializedObject";
      return "no such input!";
      }

    public String getOutputInfo(int i) 
      {
      return "no such output";
      }

    public String getOutputName(int i) 
      {
      return "no such output.";
      }



  ////////////////
  // Properties //
  ////////////////

  private String     FileName;// = "ObjectFile.ser";
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


  //////////
  // Doit //
  //////////

  public void doit() 
    {
    Object object = pullInput(0); 
	if(!usePropFileName){
		FileName=(String)(pullInput(1));
	}
    try 
      {
      FileOutputStream   file = new FileOutputStream(FileName);
      ObjectOutputStream out = new ObjectOutputStream(file);
      out.writeObject(object);
      out.flush();
      out.close();
      }
    catch (java.io.IOException IOE) 
      {
      System.out.println("IOException");
      }
    }
  }
