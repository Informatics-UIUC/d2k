package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.ComputeModule;
import java.io.*;
import java.util.Random;

public class StupidReadingCap extends ComputeModule {
  
  public String getModuleInfo() {
    return "StupidReadingCap: this serializes and writes to a file.";
  }
  
  public String getModuleName() {
    return "StupidReadingCap";
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "FileName";
    default:
      return "NO SUCH INPUT!";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "FileName";
    default:
      return "No such input";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.String" };
    return types;
  }
  
  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "RecoveredObject";
    default:
      return "NO SUCH OUTPUT!";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "RecoveredObject";
    default:
      return "No such output";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = { "java.lang.Object" };
    return types;
  }
  
  boolean InitialExecution;
  boolean HaveFileName = false;
  String FileNameToCheckOn = new String();
  
  public void beginExecution() {
    InitialExecution = true;
    HaveFileName = false;
    FileNameToCheckOn = new String();
  }
  
  public boolean isReady() {
    
//    System.out.println("reading cap being checked for readiness...");
    if (InitialExecution && (this.getFlags()[0] > 0)) {
//      System.out.println("InitialExecution = [" + InitialExecution + "], flag = [" + this.getFlags()[0] + "]");

      String FileNameToRead = (String) this.pullInput(0);
      FileNameToCheckOn = new String(FileNameToRead);

      HaveFileName = true;

      return true;

    } else if (HaveFileName) {
      return true;
    } else {
      return false;
    }
    
  }
  
  public void doit() throws Exception {

    long magicSleepTime = 1000;
    
    if (InitialExecution) {
      InitialExecution = false;
    }

    
    File fileToReadFrom = new File(FileNameToCheckOn);
    String fileLockName = new String(FileNameToCheckOn + ".lock");
    File fileToLockOn = new File(fileLockName);
    
    /*
     * the "writer" will create the lock before it starts on the real file and will delete the lock after the real file is done...
     */
    
    if (fileToReadFrom.exists() && !fileToLockOn.exists()) {
      
      FileInputStream streamToReadWith = new FileInputStream(fileToReadFrom);
      
      ObjectInputStream ininin = new ObjectInputStream(streamToReadWith);
      
      Object RecoveredObject = ininin.readObject();
      ininin.close();
      fileToReadFrom.delete();
      this.pushOutput(RecoveredObject, 0);

    } else {
      Thread.sleep(magicSleepTime);
      // do nothing and wait till next time...
    }
    
  }
}


