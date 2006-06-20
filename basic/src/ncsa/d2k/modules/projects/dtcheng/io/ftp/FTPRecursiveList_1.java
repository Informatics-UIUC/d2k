package ncsa.d2k.modules.projects.dtcheng.io.ftp;

import java.io.*;
import ncsa.d2k.core.modules.*;

public class FTPRecursiveList_1 extends InputModule {
  
  private boolean Trace = false;

  public void setTrace(boolean value) {
    this.Trace = value;
  }

  public boolean getTrace() {
    return this.Trace;
  }

  private String FTPCommandString = "C:\\Program Files\\NCSA\\Kerberos 5\\ftp.exe";

  public void setFTPCommandString(String value) {
    this.FTPCommandString = value;
  }

  public String getFTPCommandString() {
    return this.FTPCommandString;
  }

  private String user = "dtcheng";

  public String getUser() {
    return this.user;
  }

  public void setUser(String value) {
    this.user = value;
  }

  public String getModuleInfo() {
    return "FTPRecursiveList";
  }

  public String getModuleName() {
    return "FTPRecursiveList";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "MachineURL";
    case 1:
      return "User Name";
    case 2:
      return "Remote Directory Name";
    case 3:
      return "Local Directory Name";
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "MachineURL";
    case 1:
      return "User Name";
    case 2:
      return "Remote Directory Name";
    case 3:
      return "Local Directory Name";
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "null trigger";
    default:
      return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "null trigger";
    default:
      return "NO SUCH OUTPUT";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "java.lang.Object" };
    return types;
  }

  byte LineFeedByte = (byte) 10;

  void wait(int time) {
    try {
      synchronized (Thread.currentThread()) {
        Thread.sleep(time);
      }
    } catch (Exception e) {
      System.out.println("wait error!!!");
    }
  }

  int MaxBufferSize = 1000000;

  byte[] OutputBuffer = new byte[MaxBufferSize];

  int OutputBufferIndex = 0;

  public void beginExecution() {
    OutputBufferIndex = 0;
  }

  int MaxLineLength = 999;
  void GetNames(InputStream inputStream) throws Exception {
    
    byte [] lineBuffer = new byte[MaxLineLength];
    int lineBufferIndex = 0;
    int numLines = 0;
    
    int numBytes = 0;
    
    while (true) {
      
      int numBytesAvailable = 0;
      try {
        numBytesAvailable = inputStream.available();
      } catch (Exception e) {
        System.out.println("inputStream.available() error!!!");
        throw e;
      }

      if (numBytesAvailable == 0)
        continue;

      try {
        
        int value = inputStream.read();
        if (value == -1) {
          System.out.println("value == -1");
          throw new Exception();
        }
        if (Trace) {
          System.out.print((char) value);
        }
        
        lineBuffer[lineBufferIndex++] =  (byte) value;
        
        if (value == 12) {
          numLines++;
          String lineString = new String(lineBuffer, 0, lineBufferIndex++);
          lineBufferIndex = 0;
          if (lineString.startsWith("226 Transfer complete."))
          break;
        }
      } catch (Exception e) {
        System.out.println("inputStream.read() error!!!");
        throw e;
      }

    }
  }
  
  void getFileNames(BufferedReader bufferedReader) throws Exception {
    int count = 0;

      
      if (bufferedReader.ready()) {
       
        String string = bufferedReader.readLine();
        count++;
        
        System.out.println("string = " + string);
        System.out.println("count = " + count);
      
      }
      else {
      //wait(1);
      }
  }
  
  void ProcessInputStream(InputStream inputStream) throws Exception {
    
    int numBytes = 0;
    
    OutputBufferIndex = 0;

    while (true) {
      
      int numBytesAvailable = 0;
      try {
        numBytesAvailable = inputStream.available();
      } catch (Exception e) {
        System.out.println("inputStream.available() error!!!");
        throw e;
      }

      if (numBytesAvailable == 0)
        continue;

      try {
        numBytes = inputStream.read(OutputBuffer, OutputBufferIndex, numBytesAvailable);
        if (numBytes != numBytesAvailable) {
          System.out.println("numBytes != numBytesAvailable");
          throw new Exception();
        }
      } catch (Exception e) {
        System.out.println("inputStream.read() error!!!");
        throw e;
      }

      if (Trace) {
        System.out.print(new String(OutputBuffer, OutputBufferIndex, numBytesAvailable));
      }

      OutputBufferIndex += numBytesAvailable;
      
    }
  }

  void IgnoreInputStream(InputStream inputStream) throws Exception {
    byte[] buffer = new byte[1000];
    int numBytes = 0;

    while (true) {
      int numBytesAvailable = 0;
      try {
        numBytesAvailable = inputStream.available();
      } catch (Exception e) {
        System.out.println("inputStream.available() error!!!");
      }

      if (numBytesAvailable == 0)
        break;

      while (numBytesAvailable > 0) {
        try {
          numBytes = inputStream.read(buffer, 0, 1);
        } catch (Exception e) {
          System.out.println("inputStream.read() error!!!");
          throw e;
        }

        System.out.print("ignoring " + new String(buffer, 0, numBytes));

        numBytesAvailable = numBytesAvailable - numBytes;
      }
    }
  }

  private int numLinesToSkipForParsingDirectoryListing = 1;

  int waitTime = 100;

  int MaxNumFileNames = 10000;

  String[] FileNames = new String[MaxNumFileNames];

  int NumFileNames = 0;

    int MaxNumCommands = 999;
  public void doit() throws Exception {

    String machineName = (String) this.pullInput(0);
    String userName = (String) this.pullInput(1);
    String remoteDirectoryName = (String) this.pullInput(2);
    String localDirectoryName = (String) this.pullInput(3);

    String[] InputStreamLines = null;
    InputStreamLines = new String[MaxNumCommands];
    int numCommandLines = 0;

    InputStreamLines[numCommandLines++] = "verbose";
    InputStreamLines[numCommandLines++] = "open " + machineName;
    InputStreamLines[numCommandLines++] = user;
      InputStreamLines[numCommandLines++] = "lcd " + "\"" + localDirectoryName + "\"";
      InputStreamLines[numCommandLines++] = "cd " + "\"" + remoteDirectoryName + "\"";

      InputStreamLines[numCommandLines++] = "ls *";
      InputStreamLines[numCommandLines++] = "quit";


    Runtime runtime = Runtime.getRuntime();
    Process process = null;
    try {
      process = runtime.exec(FTPCommandString);
    } catch (Exception e) {
      System.out.println("exec error!!!");
    }

    OutputStream outputStream = process.getOutputStream();
    //InputStream inputStream = ;
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream())); 
    InputStream errorStream = process.getErrorStream();

    byte[] lineFeed = { LineFeedByte };

    for (int i = 0; i < numCommandLines; i++) {
      
      byte[] values = InputStreamLines[i].getBytes();
      
      if (Trace)
        System.out.println("command = " + InputStreamLines[i]);
      
      for (int j = 0; j < values.length; j++) {
        try {
          //wait(waitTime);
          outputStream.write(values[j]);
          outputStream.flush();
        } catch (Exception e) {
          System.out.println("write error!!!");
        }
      }

      try {
        outputStream.write(lineFeed);
        outputStream.flush();
      } catch (Exception e) {
        System.out.println("write error!!!");
        throw e;
      }

        //getFileNames(bufferedReader);
        /*
      if (InputStreamLines[i].equals("ls"))
        getFileNames(bufferedReader);
      else {
        if (bufferedReader.ready()) {
        String string = bufferedReader.readLine();
        System.out.println("string = " + string);
        }
        }
         */
      
      //IgnoreInputStream(errorStream);
    }

    while (true) {
      getFileNames(bufferedReader);
      if (false) {
        break;
      }
      wait(1);
    }
    
    
    ////////////////////////////////
    // close process input stream //
    ////////////////////////////////
    try {
      outputStream.close();
    } catch (Exception e) {
      System.out.println("close error!!!");
    }

    /////////////////////////////
    // wait for process to end //
    /////////////////////////////
    while (true) {
      getFileNames(bufferedReader);
      int exitValue = -1;
      try {
        exitValue = process.exitValue();
      } catch (Exception e) {
      }
      if (exitValue != -1)
        break;
      wait(waitTime);
      IgnoreInputStream(errorStream);
    }

    /////////////////////////////////
    // process any remaning output //
    /////////////////////////////////

    IgnoreInputStream(errorStream);
    
    this.pushOutput(null, 0);
  }

}