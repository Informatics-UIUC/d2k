package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;

public class ReceiverGolfOne extends InputModule {
  
  public String getModuleInfo() {
    return "ReceiverGolfOne: this reads from a socket on a specific port "
    + "listed in the property. "
    + "If I get this working well, hopefully, I will "
    + "remember to update this description... Everything is in the doit() for this one";
  }
  
  public String getModuleName() {
    return "ReceiverGolfOne";
  }
  
  // try to do the property thing...
  
  private int PortNumber = 7007;
  
  public void setPortNumber(int value) {
    this.PortNumber = value;
  }
  
  public int getPortNumber() {
    return this.PortNumber;
  }
  
  private int CacheSize = 10;
  
  public void setCacheSize(int size) {
    this.CacheSize = size;
  }
  
  public int getCacheSize() {
    return this.CacheSize;
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
  
  private boolean reallyKeepListening = true;
  private SimplePigeonHoles incomingCache = null;
  private SingleServerThread actualReceiver = null;
  
  public void beginExecution() {
//    System.out.println("starting beginExecution()");
    
//    System.out.println("ending beginExecution()");
  } // end beginExecution()
  
  public void endExecution() {
    // need to stop the server...
//    System.out.println("trying an endExecution()");
//      actualReceiver.forceShutDown();
  }
  
  public boolean isReady() {
//    System.out.println("check me out: i'm doing isReady()");
    //    System.out.println("ignoring server, checking cache, returning "
    //        + incomingCache.areRoosting());
    //    return incomingCache.areRoosting();
    return true;
  }
  
  public void doit() {
    //    System.out.println("nift-o-rama: inside the doit(), considering emptying the cache...");
    
    // set up the cache
    try {
      incomingCache = new SimplePigeonHoles(CacheSize);
    } catch (Exception pigeonFailure) {
      System.err.println("Problem creating pigeon hole cache.");
      pigeonFailure.printStackTrace();
    }
    actualReceiver = new SingleServerThread(PortNumber, incomingCache);
    
    actualReceiver.start();
    
    while (true) {
      Thread.yield();
//		Thread.sleep(1);      
      if (incomingCache.areRoosting()) {
        Object[] currentCacheDump = incomingCache.getContents();
        
        for (int holeIndex = 0; holeIndex < currentCacheDump.length; holeIndex++) {
          this.pushOutput(currentCacheDump[holeIndex],0);
        }
      }
    }
  }
}

