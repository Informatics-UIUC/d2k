package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.ComputeModule;
import java.io.*;
import java.net.*;


public class StupidReadingSocketCap extends ComputeModule {
  
  public String getModuleInfo() {
    return "StupidReadingSocketCap: this reads from a socket.";
  }
  
  public String getModuleName() {
    return "StupidReadingSocketCap";
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "PortNumber";
    default:
      return "NO SUCH INPUT!";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "PortNumber";
    default:
      return "No such input";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.Integer" };
    return types;
  }
  
  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "RecoveredObject";
    case 1:
      return "Signature";
    default:
      return "NO SUCH OUTPUT!";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "RecoveredObject";
    case 1:
      return "Signature";
    default:
      return "No such output";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = { "java.lang.Object", "java.lang.Long" };
    return types;
  }
  
  /*  boolean InitialExecution;
   boolean HavePortNumber = false;
   int PortNumberToListenTo -1;
   
   public void beginExecution() {
   InitialExecution = true;
   HavePortNumber = false;
   PortNumberToListenTo = -2;
   }
   
   public boolean isReady() {
   
   //    System.out.println("reading cap being checked for readiness...");
    if (InitialExecution && (this.getFlags()[0] > 0)) {
    //      System.out.println("InitialExecution = [" + InitialExecution + "], flag = [" + this.getFlags()[0] + "]");
     
     int PortNumber = ((Integer)this.pullInput(0)).intValue();
     PortNumberToListenTo = PortNumber;
     
     HavePortNumber = true;
     
     return true;
     
     } else if (HavePortNumber) {
     return true;
     } else {
     return false;
     }
     
     }
     */  
  public void doit() throws Exception {
    
    
    int PortNumber = ((Integer)this.pullInput(0)).intValue();
    
    long magicSleepTime = 1000;
    
    /*    if (InitialExecution) {
     InitialExecution = false;
     }
     */
    Object objectToPush;
    long Signature;
    
    
    ServerSocket basicListeningSocket = null;
    
    try {
      basicListeningSocket = new ServerSocket(PortNumber);
    } catch (IOException e) {
      System.out.println("Could not listen on port: " + PortNumber);
    }
    
    while (true) {
      Socket clientSocket = null;
      
      try {
        clientSocket = basicListeningSocket.accept();
      } catch (IOException e) {
        System.out.println("Accept failed on: " + PortNumber);
      }
      
      ObjectInputStream incomingStream = new ObjectInputStream(clientSocket.getInputStream());
      
      // ok, read the object
      objectToPush = incomingStream.readObject();
      // now for the signature
      Signature = incomingStream.readLong();
      
      incomingStream.close();
      clientSocket.close();
      
      this.pushOutput(objectToPush,0);
      this.pushOutput(new Long(Signature),1);
      
    }
    
  }
}


