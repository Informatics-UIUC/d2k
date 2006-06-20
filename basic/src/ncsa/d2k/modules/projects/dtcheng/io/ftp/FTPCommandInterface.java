package ncsa.d2k.modules.projects.dtcheng.io.ftp;

import java.io.*;
import ncsa.d2k.core.modules.*;

public class FTPCommandInterface extends InputModule {
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

  private int numLinesToSkip = 1;

  public int getNumLinesToSkip() {
    return this.numLinesToSkip;
  }

  public void setNumLinesToSkip(int value) {
    this.numLinesToSkip = value;
  }

  int waitTime = 100;

  public String getModuleInfo() {
    return "FTPCommandInterface";
  }

  public String getModuleName() {
    return "FTPCommandInterface";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "MachineURL";
    case 1:
      return "Command";
    case 2:
      return "File 1";
    case 3:
      return "File 2";
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "MachineURL";
    case 1:
      return "Command";
    case 2:
      return "File 1";
    case 3:
      return "File 2";
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "java.lang.String", "java.lang.String", "java.io.File", "java.io.File" };
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

  void ProcessInputStream(InputStream inputStream) throws Exception {
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
        break;

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

        System.out.print(new String(buffer, 0, numBytes));

        numBytesAvailable = numBytesAvailable - numBytes;
      }
    }
  }

  int MaxNumFileNames = 10000;

  String[] FileNames = new String[MaxNumFileNames];

  int NumFileNames = 0;

  void getFileNames() {
    NumFileNames = 0;

    int index = 0;

    // skip first n lines

    for (int i = 0; i < numLinesToSkip; i++) {
      while (true) {
        if (OutputBuffer[index] == LineFeedByte)
          break;
        index++;
      }
      index++;
    }

    // read file names
    while (true) {
      // skip header
      index += 71;
      if (index >= OutputBufferIndex)
        break;

      int lastIndex = index;
      while (true) {
        if (OutputBuffer[index] == LineFeedByte)
          break;
        index++;
      }
      index++;
      int length = index - lastIndex - 2;

      FileNames[NumFileNames] = new String(OutputBuffer, lastIndex, length);
      NumFileNames++;

      if (index >= OutputBufferIndex)
        break;

    }
  }

  public void doit() throws Exception {

    String machineName = (String) this.pullInput(0);
    String command = (String) this.pullInput(1);
    File file1 = (File) this.pullInput(2);
    File file2 = (File) this.pullInput(3);

    if (file1 == null) {

      this.pushOutput(null, 0);
      return;
    }

    String file1Name = file1.getName();
    String file2Name = file2.getName();

    String directory1Name = file1.getParent();
    String directory2Name = file2.getParent().substring(3).replace('\\', '/');

    if (true) {
      System.out.println("file1Name = " + file1Name);
      System.out.println("file2Name = " + file2Name);

      System.out.println("directory1Name = " + directory1Name);
      System.out.println("directory2Name = " + directory2Name);
    }

    String localFileName1 = file1.toString();
    String localFileName2 = file2.toString();

    if (false) {
      System.out.println("localFileName1 = " + localFileName1);
      System.out.println("localFileName2 = " + localFileName2);
    }

    String remoteFileName1 = file1.toString();
    remoteFileName1 = remoteFileName1.substring(3);
    remoteFileName1 = remoteFileName1.replace('\\', '/');
    //remoteFileName1 = remoteFileName1.replaceAll("!", "\\!");
    //remoteFileName1 = remoteFileName1.replaceAll("\\[", "");
    //remoteFileName1 = remoteFileName1.replaceAll("\\]", "");
    //remoteFileName1 = remoteFileName1.replaceAll("\\{", "\\{");
    //remoteFileName1 = remoteFileName1.replaceAll("\\}", "\\}");

    String remoteFileName2 = file2.toString();
    remoteFileName2 = remoteFileName2.substring(3);
    remoteFileName2 = remoteFileName2.replace('\\', '/');
    //remoteFileName1 = remoteFileName2.replaceAll("!", "\\!");
    //remoteFileName2 = remoteFileName2.replaceAll("\\[", "");
    //remoteFileName2 = remoteFileName2.replaceAll("\\]", "");
    //remoteFileName2 = remoteFileName2.replaceAll("\\{", "\\{");
    //remoteFileName2 = remoteFileName2.replaceAll("\\}", "\\}");

    if (false) {
      System.out.println("remoteFileName1 = " + remoteFileName1);
      System.out.println("remoteFileName2 = " + remoteFileName2);
    }

    boolean UseCDCommands = false;
    int numArgs = -1;
    String arg1 = "";
    String arg2 = "";

    if (command.equalsIgnoreCase("put")) {
      numArgs = 2;
      arg1 = file1Name;
      arg2 = file2Name;
      UseCDCommands = true;
    }

    if (command.equalsIgnoreCase("get")) {
      numArgs = 2;
      arg1 = remoteFileName1;
      arg2 = localFileName2;
      UseCDCommands = true;
    }

    if (command.equalsIgnoreCase("delete")) {
      numArgs = 1;
      arg1 = remoteFileName1;
      UseCDCommands = true;
    }

    if (command.equalsIgnoreCase("mkdir") || command.equalsIgnoreCase("rmdir")) {
      numArgs = 1;
      arg1 = remoteFileName1;
      UseCDCommands = false;
    }

    String[] InputStreamLines = null;

    InputStreamLines = new String[999];
    int numCommandLines = 0;

    InputStreamLines[numCommandLines++] = "open " + machineName;
    InputStreamLines[numCommandLines++] = user;
    if (UseCDCommands) {
      InputStreamLines[numCommandLines++] = "lcd " + "\"" + directory1Name + "\"";
      InputStreamLines[numCommandLines++] = "cd " + "\"" + directory2Name + "\"";
    }
    InputStreamLines[numCommandLines++] = command;
    switch (numArgs) {
    case 1:
      InputStreamLines[numCommandLines++] = "\"" + arg1 + "\"";
      break;
    case 2:
      InputStreamLines[numCommandLines++] = "\"" + arg1 + "\"";
      InputStreamLines[numCommandLines++] = "\"" + arg2 + "\"";
      break;
    }

    InputStreamLines[numCommandLines++] = "quit";

    Runtime runtime = Runtime.getRuntime();
    Process process = null;
    try {
      process = runtime.exec(FTPCommandString);
    } catch (Exception e) {
      System.out.println("exec error!!!");
    }

    OutputStream outputStream = process.getOutputStream();
    InputStream inputStream = process.getInputStream();
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

      ProcessInputStream(inputStream);
      IgnoreInputStream(errorStream);
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
      int exitValue = -1;
      try {
        exitValue = process.exitValue();
      } catch (Exception e) {
      }
      if (exitValue != -1)
        break;
      wait(waitTime);
      ProcessInputStream(inputStream);
      IgnoreInputStream(errorStream);
    }

    /////////////////////////////////
    // process any remaning output //
    /////////////////////////////////

    ProcessInputStream(inputStream);
    IgnoreInputStream(errorStream);

    if (false) {
      getFileNames();
      String[] result = new String[NumFileNames];

      System.arraycopy(FileNames, 0, (Object) result, 0, NumFileNames);
    }

    this.pushOutput(null, 0);
  }

}