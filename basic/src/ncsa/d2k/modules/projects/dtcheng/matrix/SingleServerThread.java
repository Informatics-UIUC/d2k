package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.net.*;
import java.util.Date;
import java.io.*;
//import ncsa.d2k.modules.projects.dtcheng.matrix.*;

public class SingleServerThread extends Thread {
  
  private int portToListenTo = -1; 
  private SimplePigeonHoles cacheToUse = null;
  private boolean keepListening = true;
  private ServerSocket basicListeningSocket = null;
  private Socket socketToUseAfterAcceptance = null;
  
  //  private Socket socketToUse = null;
  //  private long magicSleepTime;
  
  
  // will need: port number to listen to, a cache
  public SingleServerThread(int portToListenTo, SimplePigeonHoles cacheToUse) {
    this.portToListenTo = portToListenTo;
    this.cacheToUse = cacheToUse;
    //    this.magicSleepTime = magicSleepTime;
    //    this.keepListening = keepListening;
  }
  
  //  public void setListeningFlag(boolean newFlagValue) {
  //    this.keepListening = newFlagValue;
  //  }
  
  public boolean stillListening() {
    return keepListening;
  }

  public void forceShutDown() {
    this.keepListening = false;
    try {
      socketToUseAfterAcceptance.close();
    } catch (IOException acceptanceClosureFailure) {
      System.err.println("Problem trying to close the after-connected socket...");
      acceptanceClosureFailure.printStackTrace();
    }
    if (!basicListeningSocket.isClosed()) {
      try {
        basicListeningSocket.close();
      } catch (IOException forceClosedFailure) {
        System.err.println("Problem forcing the server socket closed...");
        forceClosedFailure.printStackTrace();
      }
    }
  }

  public void run() {
    
    // set up the server socket...
    
    try {
      basicListeningSocket = new ServerSocket(portToListenTo);
    } catch (IOException socketFailure){
      System.err.println("Could not listen on port: " + portToListenTo);
      socketFailure.printStackTrace();
    }
    
    while (keepListening) {
      // let's see if putting a yield() in helps anything...
      yield();
      // grab the real socket
      try {
        socketToUseAfterAcceptance = basicListeningSocket.accept();
      } catch (IOException listeningProblem) {
        System.err.println("Had some sort of problem starting a new " +
        "server...");
        listeningProblem.printStackTrace();
      }
      
      // get the show on the road...
      
      //      boolean objectStoredSuccessfully = false;
      try {
        // open up the input/output streams
        ObjectInputStream receiverIn =
          new ObjectInputStream(socketToUseAfterAcceptance.getInputStream());
        ObjectOutputStream receiverOut =
          new ObjectOutputStream(socketToUseAfterAcceptance.getOutputStream());
        
        // consider if this is a kill signal...
        keepListening = receiverIn.readBoolean();
        
        if (!keepListening) {
          // if we get the kill signal, just shut everything down.
          receiverOut.close();
          receiverIn.close();
          socketToUseAfterAcceptance.close();
        } else {
          // talk to each other
          if (cacheToUse.areOpenings()) {
            // say, send her over
            receiverOut.writeBoolean(true);
            receiverOut.flush();
            try {
              // read the object and try to put it in a pigeon hole. at the
              // moment, we will assume that nothing else is accessing these
              // pigeonholes. that is, nothing will cause the pigeonholes to
              // be filled between inquiring if there is space and when we want
              // to store something.
              //objectStoredSuccessfully = cacheToUse.storeObject(receiverIn.readObject());
              cacheToUse.storeObject(receiverIn.readObject());
              System.out.println("successfully received an object on port " + socketToUseAfterAcceptance + " at " + new Date());
              
              //              if (objectStoredSuccessfully) {
              //                receiverOut.writeBoolean(true);
              //                receiverOut.flush();
              //                
              //              } else {
              //                receiverOut.writeBoolean(false);
              //                receiverOut.flush();
              //              }
              
            } catch (ClassNotFoundException receivingObject) {
              System.err.println("Trouble with unknown class types...");
            }
          } else {
            // say, try again later
            receiverOut.writeBoolean(false);
            receiverOut.flush();
          }
          
          //    close the connection
          receiverIn.close();
          receiverOut.close();
          //          try {
          //            Thread.sleep(magicSleepTime);
          //          } catch (InterruptedException f) {
          //            f.printStackTrace();
          socketToUseAfterAcceptance.close();
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      basicListeningSocket.close();
    } catch (IOException closingServerFailure) {
      System.err.println("Trouble closing the listening server...");
      closingServerFailure.printStackTrace();
    }
  }
}