package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.net.*;

public class SenderHotel extends OutputModule {
  
  public String getModuleInfo() {
    return "SenderGolf: this serializes and sends out a socket; for use with "
    + "ReceiverHotel. This will attempt semi-permanent connections.";
  }
  
  public String getModuleName() {
    return "SenderHotel";
  }
  
  // try to do the property thing...
  
  private int PortNumber = 7007;
  
  public void setPortNumber(int value) {
    this.PortNumber = value;
  }
  
  public int getPortNumber() {
    return this.PortNumber;
  }
  
  private String ComputerName = "";
  
  public void setComputerName(String name) {
    this.ComputerName = name;
  }
  
  public String getComputerName() {
    return this.ComputerName;
  }
  
  private long SleepTime = 50;
  
  public void setSleepTime(long time) {
    this.SleepTime = time;
  }
  
  public long getSleepTime() {
    return this.SleepTime;
  }
  
  private long BadConnectionRetries = 5;
  
  public void setBadConnectionRetries(long badtries) {
    this.BadConnectionRetries = badtries;
  }
  
  public long getBadConnectionRetries() {
    return this.BadConnectionRetries;
  }
  
  // all the usual things...
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "ObjectToSend";
    default:
      return "NO SUCH INPUT!";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "ObjectToSend";
    default:
      return "No such input";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.Object", };
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
  
  private boolean weHaveAGoodConnection = false;
  
  public void beginExecution() {
    weHaveAGoodConnection = false;
  }
    
  
  
  private Object[] establishConnection(String ipAddress, int portToTry, long nAttempts, long sleepInterval) throws Exception {
    boolean weHaveAGoodConnection = false;
    Socket outgoingSocket = null;
    
    for (long badRetry = 0; badRetry < nAttempts; badRetry++) {
      try {
        outgoingSocket = new Socket(ipAddress, portToTry);
        //        System.out.println("After creating new socket... about to flip the flag...");
        weHaveAGoodConnection = true;
        //        System.out.println("flipped flag: [" + weHaveAGoodConnection + "]; about to break...");
        break;
      } catch (IOException e){
        System.out.println("  --[" + this.getAlias() + "] failed to connect to [" + ComputerName + "] on port [" + PortNumber + "] current retry = " + badRetry);
        weHaveAGoodConnection = false;
      }
      Thread.sleep(sleepInterval);
    }
    
    return new Object[] {new Boolean(weHaveAGoodConnection) , outgoingSocket};
  }


  private Object[]           connectionPair = null;
  private Socket             outgoingSocket = null;
  private ObjectOutputStream senderOut      = null;
   
  public void doit() throws Exception {

    Object ObjectToSend = this.pullInput(0);

    // try to establish a connection
    if (!weHaveAGoodConnection) {
      
      connectionPair = establishConnection(ComputerName, PortNumber, BadConnectionRetries, SleepTime);
      
      weHaveAGoodConnection = ((Boolean)connectionPair[0]).booleanValue();
      outgoingSocket        =   (Socket)connectionPair[1];
      
      if (weHaveAGoodConnection) {
      senderOut = new ObjectOutputStream(outgoingSocket.getOutputStream());
//      System.out.println("output stream has been set up...");
      } else {
        System.err.println("   [" + this.getAlias() + "] was unable to establish a connection to " + ComputerName +
            " on port " + PortNumber + " after attempt #" + BadConnectionRetries + "; pulling the input and swallowing it...");
        throw new Exception();
      }
      
    }
    
    senderOut.writeObject(ObjectToSend);
    senderOut.flush();
//    System.out.println("      [" + this.getAlias() + "] sent an object to " + ComputerName + " [" + outgoingSocket.getInetAddress() + "]");
    
    
    
  }
}







