package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.net.*;

public class ReceiverFoxtrot extends InputModule {

  public String getModuleInfo() {
    return "ReceiverFoxtrot: this reads from a socket. For use with SenderFoxtrot. The "
    + "port number and object ID number are specified in the "
    + "properties. If I get this working well, hopefully, I will "
    + "remember to update this description... <p> This is meant to "
    + "be used in token ring fashion.";
  }
  
  public String getModuleName() {
    return "ReceiverFoxtrot";
  }
  
  // try to do the property thing...
  
  private int PortNumber = 7007;
  
  public void setPortNumber(int value) {
    this.PortNumber = value;
  }
  
  public int getPortNumber() {
    return this.PortNumber;
  }
  
  private long DesiredIDNumber = -1;
  
  public void setDesiredIDNumber(long id) {
    this.DesiredIDNumber = id;
  }
  
  public long getDesiredIDNumber() {
    return this.DesiredIDNumber;
  }
  
  // all the usual things...
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "StartListeningFlag";
    default:
      return "NO SUCH INPUT!";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "StartListeningFlag";
    default:
      return "No such input";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.Boolean" };
    return types;
  }
  
  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "ReceivedObject";
    case 1:
      return "ListeningFlag";
    default:
      return "NO SUCH OUTPUT!";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "ReceivedObject";
    case 1:
      return "ListeningFlag";
    default:
      return "No such output";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = { "java.lang.Object", "java.lang.Boolean" };
    return types;
  }
  
  public void doit() throws Exception {
    
    boolean StartListeningFlag = ((Boolean) (this.pullInput(0))).booleanValue();
    
    System.out.println("   -The listed port number is [" + PortNumber + "]; while the Desired Object ID # is [" + DesiredIDNumber
        + "] and the StartListeningFlag is <" + StartListeningFlag + ">");
    
    if (StartListeningFlag) {
      
      Object objectWeAreReceiving;
      long statedIDNumber;
      
      ServerSocket basicListeningSocket = null;
  
      // turns out that i don't really want this exception caught, because
      // then it will lead to a null pointer exception when we try to accept...

      //      try {
        basicListeningSocket = new ServerSocket(PortNumber);
//      } catch (IOException e) {
//        System.out.println("Could not listen on port: " + PortNumber);
//      }
      
      Socket clientSocket = null;
      
//      try {
//        System.out.println("Starting to listen and trying to accept...");
        clientSocket = basicListeningSocket.accept();
//      } catch (IOException e) {
//        System.out.println("Accept failed on: " + PortNumber);
//      }
      
//      System.out.println("we seem to have gotten a socket...");
      
      ObjectInputStream incomingStreamReceiver = new ObjectInputStream(clientSocket.getInputStream());
      ObjectOutputStream outgoingStreamReceiver = new ObjectOutputStream(clientSocket.getOutputStream());

//      System.out.println("In and Out Streams are set up...");
      
      // ask for the ID number...
      statedIDNumber = incomingStreamReceiver.readLong();
      
//      System.out.println("received ID number [" + statedIDNumber + "]");
      
      if (statedIDNumber == 0) {
        System.out.println("Received \"die! die! die!\" signal [id = zero]; complying...");
        
        // shut everything down...
        incomingStreamReceiver.close();
        outgoingStreamReceiver.close();
        clientSocket.close();
        basicListeningSocket.close();
        
      } else if (statedIDNumber == DesiredIDNumber) {
//        System.out.println("ID numbers matched... (stated [" + statedIDNumber + "] == [" + DesiredIDNumber + "] DesiredIDNumber)");
        
        // write back and request the object
        outgoingStreamReceiver.writeBoolean(true);
        outgoingStreamReceiver.flush();

//        System.out.println("sent the true back...");
        // ok, read the object
        objectWeAreReceiving = incomingStreamReceiver.readObject();
        
//        System.out.println("received the object, now shutting down...");
        // shut everything down...
        
        incomingStreamReceiver.close();
        outgoingStreamReceiver.close();
        clientSocket.close();
        basicListeningSocket.close();
        
        // push out the newly read object
        this.pushOutput(objectWeAreReceiving, 0);
        
        // pass along the "can receive token"
        this.pushOutput(new Boolean(true), 1);
        
      } else {
//        System.out.println("ID numbers failed to match... (stated [" + statedIDNumber + "] != [" + DesiredIDNumber + "] DesiredIDNumber)");

        // write back and request the object
        outgoingStreamReceiver.writeBoolean(false);
        outgoingStreamReceiver.flush();

        // shut everything down...
        incomingStreamReceiver.close();
        outgoingStreamReceiver.close();
        clientSocket.close();
        basicListeningSocket.close();
        
        // pass along the "can receive token"
        this.pushOutput(new Boolean(true), 1);
        
      }
      
    } else {
      System.out.println("Received \"die! die! die!\" signal [a false flag]; complying...");
    }
    
  }
}

