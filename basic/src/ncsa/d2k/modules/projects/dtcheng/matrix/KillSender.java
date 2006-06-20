package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.net.*;

public class KillSender extends OutputModule {
  
  public String getModuleInfo() {
    return "KillSender: sends the initial kill signal and then shuts itself down.";
  }
  
  public String getModuleName() {
    return "KillSender";
  }
  
  // try to do the property thing...
  
  private int PortNumber = 7777;
  
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
  
  // all the usual things...
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "TripWire";
    default:
      return "NO SUCH INPUT!";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "TripWire";
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
        System.out.println("  --failed to connect to [" + ComputerName + "] on port [" + PortNumber + "] current retry = " + badRetry);
        weHaveAGoodConnection = false;
      }
      Thread.sleep(sleepInterval);
    }
    
    return new Object[] {new Boolean(weHaveAGoodConnection) , outgoingSocket};
  }
  
  private int exitCode = 37;
  
  public void doit() throws Exception {
    
    Socket outgoingSocket = null;
    ObjectOutputStream senderOut = null;
    
    this.pullInput(0);

    long badConnectionRetries = 1;
    long sleepTime = 1;

    if (!ComputerName.equals("")) {
      Object[] connectionPair = establishConnection(ComputerName, PortNumber, badConnectionRetries, sleepTime);
      outgoingSocket = (Socket)connectionPair[1];
      senderOut = new ObjectOutputStream(outgoingSocket.getOutputStream());
      senderOut.writeBoolean(true);
      senderOut.flush();
      Thread.sleep(sleepTime);
      senderOut.close();
      outgoingSocket.close();
    }
    // shut down this JVM
    System.out.println("[" + this.getAlias() + "] is shutting down the JVM... this thread's hashcode is ["
        + this.hashCode() + "] at millisecond time = " + System.currentTimeMillis());
    System.exit(exitCode);

  }
}

