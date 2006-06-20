package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.ComputeModule;
import java.io.*;

public class StupidWritingCap extends ComputeModule {
  
  public String getModuleInfo() {
    return "StupidWritingCap: this serializes and writes to a file.";
  }
  
  public String getModuleName() {
    return "StupidWritingCap";
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "ObjectToRecord";
    case 1:
      return "FileName";
    default:
      return "NO SUCH INPUT!";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "ObjectToRecord";
    case 1:
      return "FileName";
    default:
      return "No such input";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.Object", "java.lang.String" };
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

    long magicSleepTime = 10;
    Object ObjectToRecord = this.pullInput(0);
    String FileNameToRecord = (String)this.pullInput(1);

    String fileLockName = new String(FileNameToRecord + ".lock");

    File fileToWriteTo = new File(FileNameToRecord);
    File fileToLockOn = new File(fileLockName);
    
    while (fileToWriteTo.exists()) {
//      System.out.println("Writing Cap is sleeping because file [" + fileToWriteTo + "] currently exists...");
      Thread.sleep(magicSleepTime); 
    }

    // create the lock file BEFORE we start on the real thing...
    fileToLockOn.createNewFile();

    // do the real thing
    FileOutputStream streamToWriteWith = new FileOutputStream(fileToWriteTo);
    ObjectOutputStream outoutout = new ObjectOutputStream(streamToWriteWith);
    
    outoutout.writeObject(ObjectToRecord);
    outoutout.flush();
    outoutout.close();
    
    // done with the real thing, delete the lock file
    fileToLockOn.delete();
    
  }
}







