package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.ComputeModule;
import java.io.*;
import java.util.Random;

public class StupidReadingCapOld extends ComputeModule {

  public String getModuleInfo() {
    return "StupidReadingCap: this serializes and writes to a file. This is the original stupid one.";
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
  

  public void doit() throws Exception {

    
//    Random RandomNumberGenerator = new Random();
    long magicSleepTime = 5000;
//    long magicBaseExtraTime = 3000;
//    long magicExtraSleepTime = (long)(magicBaseExtraTime * RandomNumberGenerator.nextDouble());
//    long magicHesitateTime = 1000;
    String FileNameToRead = (String) this.pullInput(0);

    String fileLockName = new String(FileNameToRead + ".lock");
    
    File fileToReadFrom = new File(FileNameToRead);
    File fileToLockOn = new File(fileLockName);

    while (true) {
      if (fileToReadFrom.exists() && !fileToLockOn.exists()) {
        /*
         * the "writer" will create the lock before it starts on the real file and will delete the lock after the real file is done...
         */
        FileInputStream streamToReadWith = new FileInputStream(fileToReadFrom);

        ObjectInputStream ininin = new ObjectInputStream(streamToReadWith);

        Object RecoveredObject = ininin.readObject();
        ininin.close();
        fileToReadFrom.delete();
        this.pushOutput(RecoveredObject, 0);
      } else {
//        System.out.println("Reading cap is sleeping because file [" + fileToReadFrom + "] does not exist...");
        Thread.sleep(magicSleepTime);
      }

    }
  }
}

