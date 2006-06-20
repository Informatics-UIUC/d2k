package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;

public class KillThisItinerary extends InputModule {
  
  public String getModuleInfo() {
    return "KillThisItinerary: listens on a port for " +
    "a true Boolean. At which point, " +
    "it sends the message along to whatever " +
    "other computers are on its list and then dies." +
    "<p>" +
    "The computer lists and port lists should be comma separated with" +
    "no spaces. The elements in the lists should be in the same order. " +
    "This assumes you were smart enough to do so...";
  }
  
  public String getModuleName() {
    return "KillThisItinerary";
  }
  
  // try to do the property thing...
  
  private int MyPortNumber = 7777;
  
  public void setMyPortNumber(int value) {
    this.MyPortNumber = value;
  }
  
  public int getMyPortNumber() {
    return this.MyPortNumber;
  }
  
  private String NextComputer = new String("");
  
  public void setNextComputer(String next) {
    this.NextComputer = next;
  }
  
  public String getNextComputer() {
    return this.NextComputer;
  }
  
  private String NextPort = new String("");
  
  public void setNextPort(String ports) {
    this.NextPort = ports;
  }
  
  public String getNextPort() {
    return this.NextPort;
  }
//  private long SleepInterval = 10;
//  
//  public void setSleepInterval(long interval) {
//    this.SleepInterval = interval;
//  }
//  
//  public long getSleepInterval() {
//    return this.SleepInterval;
//  }
  
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
    String[] types = { , };
    return types;
  }
  
  private boolean reallyKeepListening = true;
  //  private SimplePigeonHoles incomingCache = null;
  private SingleServerThread actualReceiver = null;
  
  public void beginExecution() {
    //    System.out.println("starting beginExecution()");
    
    // set up the computer and port lists...
    
    // search through the computer list
    int nComputers = 0;
    int computerCommaIndex = 0;
    int oldComputerCommaIndex = 0;
    int portCommaIndex = 0;
    int oldPortCommaIndex = 0;
    
    String[] computerList;
    int[] portList;
    
    while (true) {
      //      System.out.println("starting loop; numberOfComputers = " + numberOfComputers);
      computerCommaIndex = NextComputer.indexOf(',',computerCommaIndex + 1);
      //      System.out.println("   commaIndex = " + commaIndex);
      nComputers++;
      
      if (computerCommaIndex == -1) {
        break;
      }
    }
    
    if (nComputers == 1 && NextComputer.length() == 0) {
      nComputers = 0;
    }
    
    System.out.println("nComputers on our kill list = " + nComputers);
    
    if (nComputers == 0) {
      computerList = new String[0];
      portList = new int[0];
    } else {
      computerList = new String[nComputers];
      portList = new int[nComputers];
      
      // re-initialize the commaIndex
      computerCommaIndex = 0;
      oldComputerCommaIndex = -1;
      portCommaIndex = 0;
      oldPortCommaIndex = -1;
      
      for (int computerIndex = 0; computerIndex < nComputers; computerIndex++) {
  //      System.out.println("we're in the for loop");
        computerCommaIndex = NextComputer.indexOf(',', oldComputerCommaIndex + 1);
        portCommaIndex = NextPort.indexOf(',', oldPortCommaIndex + 1);
        if (computerCommaIndex != -1) {
          //          System.out.println("the string we're pulling out is ["
          //              + NextComputer.substring(oldCommaIndex + 1,commaIndex) + "]");
          computerList[computerIndex] = new String(NextComputer.substring(oldComputerCommaIndex + 1,computerCommaIndex));
        } else {
          //          System.out.println("the string we're pulling out is ["
          //              + NextComputer.substring(oldCommaIndex + 1) + "]");
          computerList[computerIndex] = new String(NextComputer.substring(oldComputerCommaIndex + 1));
        }
        
        if (portCommaIndex != -1) {
          portList[computerIndex] = Integer.parseInt(NextPort.substring(oldPortCommaIndex + 1,portCommaIndex));
        } else {
          portList[computerIndex] = Integer.parseInt(NextPort.substring(oldPortCommaIndex + 1));
        }
        oldComputerCommaIndex = computerCommaIndex;
        oldPortCommaIndex = portCommaIndex;
      }
      
    }
    
    for (int theIndex = 0; theIndex < nComputers; theIndex++) {
      System.out.println("Computer #" + theIndex + " is [" + computerList[theIndex] + "] on port [" + portList[theIndex] + "]");
    }
    
    System.out.println();
    
    KillServerThread killListener = new KillServerThread(MyPortNumber, computerList, portList);
    killListener.start();
    
    
    //    System.out.println("ending beginExecution()");
  } // end beginExecution()
  
//  public void endExecution() {
    // need to stop the server...
    // System.out.println("trying an endExecution()");
    //      actualReceiver.forceShutDown();
//  }
  
  public boolean isReady() {
    return true;
  }
  
  public void doit() {
  }
}

