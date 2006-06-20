package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.net.*;

public class SenderFoxtrot extends OutputModule {
  
  public String getModuleInfo() {
    return "SenderFoxtrot: this serializes and sends out a socket; for use with "
    + "ReceiverFoxtrot. ID number should not be zero (zero means \"die\" for the receiver).";
  }
  
  public String getModuleName() {
    return "SenderFoxtrot";
  }
  
  // try to do the property thing...
  
  private int PortNumber = 7007;
  
  public void setPortNumber(int value) {
    this.PortNumber = value;
  }
  
  public int getPortNumber() {
    return this.PortNumber;
  }
  
  private long SendingIDNumber = -1;
  
  public void setObjectIDNumber(long id) {
    this.SendingIDNumber = id;
  }
  
  public long getObjectIDNumber() {
    return this.SendingIDNumber;
  }
  
  private String ComputerName = "";
  
  public void setComputerName(String name) {
    this.ComputerName = name;
  }
  
  public String getComputerName() {
    return this.ComputerName;
  }
  
  private long SleepTime = 5;
  
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
  
  public void doit() throws Exception {
    
    Object ObjectToSend = null;
    boolean weHaveAGoodConnection = false;
    boolean weHaveSentTheObject = false;
    
    //////
    // the kill signal
    //////
    if (SendingIDNumber == 0) {
      // pull in the input even though it doesn't matter. just in case the pipe would stay full.
      this.pullInput(0);
      System.out.println("SendingIDNumber == 0; Kill Signal for Receivers... to [" + ComputerName + "] on port [" + PortNumber + "]");
      
      Socket outgoingSocket = null;
      ObjectOutputStream outgoingStreamSender = null;
      
      ObjectToSend = new Long(0);
      
      // try to establish a connection
      
      weHaveAGoodConnection = false;
      for (long badRetry = 0; badRetry < BadConnectionRetries; badRetry++) {
        try {
          outgoingSocket = new Socket(ComputerName, PortNumber);
//          System.out.println("After creating new socket... about to flip the flag...");
          weHaveAGoodConnection = true;
//          System.out.println("flipped flag: [" + weHaveAGoodConnection + "]; about to break...");
          break;
        } catch (IOException e){
          System.out.println("  --failed to connect to [" + ComputerName + "] on port [" + PortNumber + "] current retry = " + badRetry);
          weHaveAGoodConnection = false;
        }
        Thread.sleep(SleepTime);
      }
      
      if (weHaveAGoodConnection) {
//        System.out.println("   !!! actually sending the kill signal !!!");
        outgoingStreamSender = new ObjectOutputStream(outgoingSocket.getOutputStream());
        
        outgoingStreamSender.writeLong(SendingIDNumber);
        outgoingStreamSender.flush();
        
        Thread.sleep(SleepTime);
        
        outgoingStreamSender.close();
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
      //    String ComputerName = (String)this.pullInput(1);
      //    int PortNumber = ((Integer)this.pullInput(2)).intValue();
      //    long Signature = ((Long)this.pullInput(3)).longValue();
      
      Socket outgoingSocket = null;
      ObjectOutputStream outgoingStreamSender = null;
      ObjectInputStream incomingStreamSender = null;
      boolean sendObjectFlag;
      
      System.out.println("  trying: Computer = [" + ComputerName + "]; Port [" + PortNumber + "]; ID#[" + SendingIDNumber + "]; Object ["
          + ObjectToSend + "]");
      
      for (long nAttempts = 0; nAttempts < GoodConnectionRetries; nAttempts++) {
        // sleep a little to give the next server a chance to start up
        //      System.out.println("starting loop: taking short nap...");
        Thread.sleep(SleepTime);
        // try to establish a connection
        weHaveAGoodConnection = false;
        for (long badRetry = 0; badRetry < BadConnectionRetries; badRetry++) {
          //      System.out.println("requesting connection...");
          try {
            outgoingSocket = new Socket(ComputerName, PortNumber);
//            System.out.println("After creating new socket... about to flip the flag...");
            weHaveAGoodConnection = true;
//            System.out.println("flipped flag: [" + weHaveAGoodConnection + "]; about to break...");
            break;
          } catch (IOException e){
            System.out.println("  --failed to connect to [" + ComputerName + "] on port [" + PortNumber + "] current retry = " + badRetry);
            weHaveAGoodConnection = false;
            weHaveSentTheObject = false;
          }
          
          Thread.sleep(SleepTime);
        }
        
        if (weHaveAGoodConnection) {
          
          outgoingStreamSender = new ObjectOutputStream(outgoingSocket.getOutputStream());
          incomingStreamSender = new ObjectInputStream(outgoingSocket.getInputStream());
          //      System.out.println("connection, in, and out streams are set up...");
          // write out the ID number
          outgoingStreamSender.writeLong(SendingIDNumber);
          outgoingStreamSender.flush();
          //      System.out.println("sent ID# [" + SendingIDNumber + "]");
          
          // consider the response, respond appropriately
          sendObjectFlag = incomingStreamSender.readBoolean();
          
          //      System.out.println("received response [" + sendObjectFlag + "]");
          if (sendObjectFlag) {
            // write out the object
            outgoingStreamSender.writeObject(ObjectToSend);
            outgoingStreamSender.flush();
            //        System.out.println("sent object, now cleaning up...");
            
            // clean up and exit
            outgoingStreamSender.close();
            incomingStreamSender.close();
            outgoingSocket.close();
            
            System.out.println("----> successfully sent [" + ObjectToSend + "] to ["
                + ComputerName + "]/[" + PortNumber + "] ID[" + SendingIDNumber + "] on attempt " + nAttempts );
            weHaveSentTheObject = true;
            break;
          } else {
            // connected to wrong receiver. break connection and try again.
            // hopefully, we will get a different receiver in the ring...
            
            //        System.out.println("wrong receiver... cleaning up...");
            
            // clean up, but do not exit
            outgoingStreamSender.flush();
            outgoingStreamSender.close();
            incomingStreamSender.close();
            outgoingSocket.close();
            
            System.out.println("  have not found correct receiver on [" + ComputerName + "]/[" + PortNumber + "] as of attempt #" + nAttempts);
            weHaveSentTheObject = false;
          }
        } else {
          //// no connection established...
          System.out.println("Unable to successfully connect to IP [" + ComputerName + "] after " + BadConnectionRetries
              + " attempts; Object [" + ObjectToSend + "]; ID[" + SendingIDNumber + "] not sent. \n" +
              		"Module finishing execution... Expect something to (informally) hang...");
          break;
        }
      }
      if (!weHaveSentTheObject) {
        System.out.println(" ----> we have failed to send [" + ObjectToSend + "]/[" + SendingIDNumber + "]\n       to ["
            + ComputerName + "]/[" + PortNumber + "]; # of attempts = " + GoodConnectionRetries
            + "\n       Module exiting quietly. Expect something to (informally) hang.");
      }
    }
    
  }
}

