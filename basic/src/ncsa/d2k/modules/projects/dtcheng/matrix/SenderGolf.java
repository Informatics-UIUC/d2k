package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.net.*;

public class SenderGolf extends OutputModule {
  
  public String getModuleInfo() {
    return "SenderGolf: this serializes and sends out a socket; for use with "
    + "ReceiverGolf.";
  }
  
  public String getModuleName() {
    return "SenderGolf";
  }
  
  // try to do the property thing...
  
  private int PortNumber = 7007;
  
  public void setPortNumber(int value) {
    this.PortNumber = value;
  }
  
  public int getPortNumber() {
    return this.PortNumber;
  }
  
  private boolean SendKillSignal = false;
  
  public void setSendKillSignal(boolean killSignal) {
    this.SendKillSignal = killSignal;
  }
  
  public boolean getSendKillSignal() {
    return this.SendKillSignal;
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
  
  private long GoodConnectionRetries = 5;
  
  public void setGoodConnectionRetries(long goodtries) {
    this.GoodConnectionRetries = goodtries;
  }
  
  public long getGoodConnectionRetries() {
    return this.GoodConnectionRetries;
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
  
  public void doit() throws Exception {
    
    Socket outgoingSocket = null;
    ObjectOutputStream senderOut = null;
    ObjectInputStream senderIn = null;
    
    Object ObjectToSend = null;
    boolean weHaveAGoodConnection = false;
    boolean sendObjectFlag;
    boolean weHaveSentTheObject = false;
    
    //////
    // the kill signal
    //////
    if (SendKillSignal) {
      // pull in the input even though it doesn't matter. just in case the pipe would stay full.
      this.pullInput(0);
      System.out.println("Sending Kill Signal to [" + ComputerName + "] on port [" + PortNumber + "]");
      
      // try to establish a connection
      
      Object[] connectionPair = establishConnection(ComputerName, PortNumber, BadConnectionRetries, SleepTime);
      weHaveAGoodConnection = ((Boolean)connectionPair[0]).booleanValue();
      
      if (weHaveAGoodConnection) {
        outgoingSocket = (Socket)connectionPair[1];
        //        System.out.println("   !!! actually sending the kill signal !!!");
        senderOut = new ObjectOutputStream(outgoingSocket.getOutputStream());
        
        // send the keepListening = false to the other end...
        senderOut.writeBoolean(false);
        senderOut.flush();
        
        Thread.sleep(SleepTime);
        
        senderOut.close();
        outgoingSocket.close();
      } else {
        System.out.println("Unable to successfully connect to IP [" + ComputerName + "] after " + BadConnectionRetries
            + " attempts;\nkill signal not sent. Module finishing execution... Expect something to (informally) hang.");
      }
    } else {
      //////
      // sending something real
      //////
      ObjectToSend = this.pullInput(0);
      System.out.println("  trying: Computer = [" + ComputerName + "]; Port [" + PortNumber + "]; Object ["
          + ObjectToSend + "]");
      
      for (int goodTry = 0; goodTry < GoodConnectionRetries; goodTry++) {
        // try to establish a connection
        Object[] connectionPair = establishConnection(ComputerName, PortNumber, BadConnectionRetries, SleepTime);
        weHaveAGoodConnection = ((Boolean)connectionPair[0]).booleanValue();
        
        // set up the streams and send the goods...
        if (weHaveAGoodConnection) {
          outgoingSocket = (Socket)connectionPair[1];
          senderOut = new ObjectOutputStream(outgoingSocket.getOutputStream());
          senderIn = new ObjectInputStream(outgoingSocket.getInputStream());
          
          senderOut.writeBoolean(true); // send the keepListening = true to the other end.
          senderOut.flush();
          sendObjectFlag = senderIn.readBoolean();
          if (sendObjectFlag) {
            senderOut.writeObject(ObjectToSend); // send the object...
            senderOut.flush();
            weHaveSentTheObject = true;
            // shut everything down.
            senderIn.close();
            senderOut.close();
            outgoingSocket.close();
            break;
          } else {
            weHaveSentTheObject = false;
            senderIn.close();
            senderOut.close();
            outgoingSocket.close();
            System.out.println("    --Good try #" + goodTry + " connected, but the cache was full.");
            Thread.sleep(SleepTime);
          }
        }
      }
      if (!weHaveSentTheObject) {
        System.out.println("Unable to successfully connect (with an available cache) to IP [" + ComputerName
            + ";\nObject [" + ObjectToSend + "] not sent. Module finishing execution... Expect something to (informally) hang.");
      }
    } // end else; that is, we are sending something real...
    
  }
}

