package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.ComputeModule;
import java.io.*;
import java.net.*;

public class StupidWritingSocketCap extends ComputeModule {
  
  public String getModuleInfo() {
    return "StupidWritingSocketCap: this serializes and sends out a socket.";
  }
  
  public String getModuleName() {
    return "StupidWritingSocketCap";
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "ObjectToSend";
    case 1:
      return "ComputerName";
    case 2:
      return "PortNumber";
    case 3:
      return "Signature";
    default:
      return "NO SUCH INPUT!";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "ObjectToSend";
    case 1:
      return "ComputerName";
    case 2:
      return "PortNumber";
    case 3:
      return "Signature: a unique identifier for where it's supposed to go...";
    default:
      return "No such input";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.Object", "java.lang.String", "java.lang.Integer", "java.lang.Long" };
    return types;
  }
  
  public String getOutputName(int i) {
    switch (i) {
    default:
      return "NO SUCH OUTPUT!";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    default:
      return "No such output";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = {};
    return types;
  }
  
  public void doit() throws Exception {
    
    long magicSleepTime = 500;
    Object ObjectToRecord = this.pullInput(0);
    String ComputerName = (String)this.pullInput(1);
    int PortNumber = ((Integer)this.pullInput(2)).intValue();
    long Signature = ((Long)this.pullInput(3)).longValue();
    
    Socket outgoingSocket = null;
    ObjectOutputStream outgoingStream = null;
    
    // just try it with no debugging...

    // sleep a little to give the server a chance to start up
    Thread.sleep(magicSleepTime); 
    
    outgoingSocket = new Socket(ComputerName,PortNumber);
    outgoingStream = new ObjectOutputStream(outgoingSocket.getOutputStream());
    
    // write out the object
    outgoingStream.writeObject(ObjectToRecord);
    // write out the signature
    outgoingStream.writeLong(Signature);

    // clean up and exit
    
    outgoingStream.flush();
    outgoingStream.close();
    outgoingSocket.close();
    
  }
}







