package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.net.*;
import java.io.*;
//import ncsa.d2k.modules.projects.dtcheng.matrix.*;

public class MultiServerThread extends Thread {
  
  private Socket socketToUse = null;
  private SimplePigeonHoles cacheToUse = null;
  private long magicSleepTime;
  
  public MultiServerThread(Socket socketToUse,
      SimplePigeonHoles cacheToUse, long magicSleepTime) {
    this.socketToUse = socketToUse;
    this.cacheToUse = cacheToUse;
    this.magicSleepTime = magicSleepTime;
  }
  
  public synchronized void run() {
    boolean objectStoredSuccessfully = false;
    try {
      // open up the input/output streams
      ObjectInputStream receiverIn =
        new ObjectInputStream(socketToUse.getInputStream());
      ObjectOutputStream receiverOut =
        new ObjectOutputStream(socketToUse.getOutputStream());
      
      // talk to each other
      if (cacheToUse.areOpenings()) {
        // say, send her over
        receiverOut.writeBoolean(true);
        receiverOut.flush();
        try {
          // read the object and try to put it in a pigeon hole. of course,
          // all the openings may have been filled in the meantime by other
          // threads. so, we will get a boolean back saying whether we were
          // successful or not. based on that boolean, we will say whether to
          // try again later...
          objectStoredSuccessfully = cacheToUse.storeObject(receiverIn.readObject());
          
          if (objectStoredSuccessfully) {
            receiverOut.writeBoolean(true);
            receiverOut.flush();
            
          } else {
            receiverOut.writeBoolean(false);
            receiverOut.flush();
          }
          
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
      try {
        Thread.sleep(magicSleepTime);
      } catch (InterruptedException f) {
        f.printStackTrace();
      }
      socketToUse.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}