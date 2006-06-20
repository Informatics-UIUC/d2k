package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.net.*;
import java.io.*;
//import ncsa.d2k.modules.projects.dtcheng.matrix.*;

public class KillServerThread extends Thread {
  
  private int exitCode = 37;
  private int portToListenTo = -1;
  private String[] computerList;
  private int[] portList;
//  long magicSleepTime = -1;
  
  private boolean dieNow = false;
  private ServerSocket basicListeningSocket = null;
  private Socket socketToUseAfterAcceptance = null;
  
  private Socket killSenderSocket = null;
  
  //  private Socket socketToUse = null;
  //  private long magicSleepTime;
  
  
  // will need: port number to listen to, a cache
  public KillServerThread(int portToListenTo, String[] computerList, int[] portList) {
    this.portToListenTo = portToListenTo;
    this.computerList = computerList;
    this.portList = portList;
//    this.magicSleepTime = magicSleepTime;
  }
  
  //  public void setListeningFlag(boolean newFlagValue) {
  //    this.keepListening = newFlagValue;
  //  }
  
  public boolean stillListening() {
    return dieNow;
  }
  
  public void run() {
    
    // set up the server socket...
    if (computerList.length != portList.length) {
      System.err.println("KillServerThread Error: computerList.length [" + computerList.length
          + "] != [" + portList.length + "portList.length; expect trouble");
    }
    
    try {
      basicListeningSocket = new ServerSocket(portToListenTo);
    } catch (IOException socketFailure){
      System.err.println("Could not listen on port: " + portToListenTo);
      socketFailure.printStackTrace();
    }
    
    while (true) {
      // let's see if putting a yield() in helps anything...
      yield();
      // grab the real socket
      try {
        socketToUseAfterAcceptance = basicListeningSocket.accept();
      } catch (IOException listeningProblem) {
        System.err.println("Had some sort of problem starting a new " +
        "server... ");
        listeningProblem.printStackTrace();
      }
      
      // get the show on the road...
      
      try {
        // open up the input/output streams
        ObjectInputStream killIn =
          new ObjectInputStream(socketToUseAfterAcceptance.getInputStream());
        //        ObjectOutputStream receiverOut =
        //          new ObjectOutputStream(socketToUseAfterAcceptance.getOutputStream());
        
        // consider if this is a kill signal...
        dieNow = killIn.readBoolean();
        
        if (dieNow) {
          // if we get the kill signal...
          // shut the connection down.
          killIn.close();
          socketToUseAfterAcceptance.close();
          
          // pass along the destructive message
          for (int computerIndex = 0; computerIndex < computerList.length; computerIndex++) {
            killSenderSocket = new Socket(computerList[computerIndex],portList[computerIndex]);
            
            ObjectOutputStream killOut =
              new ObjectOutputStream(killSenderSocket.getOutputStream());
            killOut.writeBoolean(true);
            killOut.close();
            killSenderSocket.close();
          }
          
          // shut down this JVM
          System.out.println("shutting down the JVM... this thread's hashcode is [" + this.hashCode() + "] at millisecond time = " + System.currentTimeMillis());
          System.exit(exitCode);
          
        } else {
          // send something to std err saying that somebody tried to kill me...
          System.err.println("   ---Somebody tried to connect " +
              "to " + socketToUseAfterAcceptance + ", \n      but " +
          "didn't send a true. We're ignoring it and resetting the listener.");
          
          // close the connection and start over...
          killIn.close();
          socketToUseAfterAcceptance.close();
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}