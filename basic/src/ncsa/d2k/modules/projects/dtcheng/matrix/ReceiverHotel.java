package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.net.*;
import java.util.Date;
import java.io.*;

import ncsa.d2k.core.modules.*;

public class ReceiverHotel extends InputModule {
  
  public String getModuleInfo() {
    return "ReceiverHotel: this reads from a socket on a specific port "
    + "listed in the property. This tries to set up semi-permanent connections "
    + "If I get this working well, hopefully, I will "
    + "remember to update this description...";
  }
  
  public String getModuleName() {
    return "ReceiverHotel";
  }
  
  // try to do the property thing...
  
  private int PortNumber = 7007;
  public void setPortNumber(int value) {
    this.PortNumber = value;
  }
  public int getPortNumber() {
    return this.PortNumber;
  }
  
  private boolean VerboseStatus = false;
  public void setVerboseStatus(boolean newValue) {
    this.VerboseStatus = newValue;
  }
  public boolean getVerboseStatus() {
    return this.VerboseStatus;
  }
  
  // all the usual things...
  public String getInputName(int i) {
    switch (i) {
    default:
      return "NO SUCH INPUT!";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    default:
      return "No such input";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = {  };
    return types;
  }
  
  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "ReceivedObject";
    default:
      return "NO SUCH OUTPUT!";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "ReceivedObject";
    default:
      return "No such output";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = { "java.lang.Object", };
    return types;
  }
    
  public void doit() throws Exception {

    Object theReceivedObject;
    
    if (VerboseStatus) {
//      System.out.println(" [" + getAlias() + "] attempting to listen on " + PortNumber);
    }
    // start up a listening socket and accept an incoming request
    ServerSocket basicListeningSocket = new ServerSocket(PortNumber);
    if (VerboseStatus) {
//      System.out.println(" [" + getAlias() + "] set up the listener... [" + basicListeningSocket + "] at " + new Date());
    }
    Socket theSocketWeUse = basicListeningSocket.accept();
    if (VerboseStatus) {
      System.out.println("[" + getAlias() + "] accepted a connection from " + theSocketWeUse.getInetAddress() +
          " at " + new Date() );
    }
    
    // start up the input stream
    ObjectInputStream receiverIn = new ObjectInputStream(theSocketWeUse.getInputStream());

    // do an infinite loop reading and pushing out...
    while (true) {
      theReceivedObject = receiverIn.readObject();
      if (VerboseStatus) {
        System.out.println("  [" + getAlias() + "] received object [" + theReceivedObject + "] from " +
            theSocketWeUse.getInetAddress() + " at " + new Date());
      }
      this.pushOutput(theReceivedObject, 0);
//      Thread.sleep(1);
//      Thread.yield();
    }
  }
}

