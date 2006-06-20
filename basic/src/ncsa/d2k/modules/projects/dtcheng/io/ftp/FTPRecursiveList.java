package ncsa.d2k.modules.projects.dtcheng.io.ftp;

import java.io.*;
import ncsa.d2k.core.modules.*;

public class FTPRecursiveList extends InputModule {
  
  //int StartDataFileIndex = 7485;
  int StartDataFileIndex = 3036;
  
  //String ReverseFlag = "";
  String ReverseFlag = "r";
  
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
  
  void readLine(BufferedReader bufferedReader) throws Exception {
    
    while (true) {
      
      if (bufferedReader.ready()) {
        
        String string = bufferedReader.readLine();
        
        System.out.println("string = " + string);
        return;
      }
      
      wait(1);
    }
  }
  
  
  void readLinesUntil(BufferedReader bufferedReader, String keyString) throws Exception {
    
    while (true) {
      
      if (bufferedReader.ready()) {
        
        String string = bufferedReader.readLine();
        
        System.out.println("readLinesUntil skipping line = " + string);
        
        if (string.startsWith(keyString)) {
          return;
        }
        
      }
      
      wait(1);
    }
  }
  
  
  int MaxNumFilesInDirectory = 1000;
  String [] CurrentDirectoryFileNames = new String[MaxNumFilesInDirectory];
  int CurrentDirectoryFileIndex = 0;
  String [] CurrentDataFileNames = new String[MaxNumFilesInDirectory];
  int CurrentDataFileIndex = 0;
  
  
  
  void readFileNames(BufferedReader bufferedReader) throws Exception {
    int lineCount = 0;
    
    CurrentDirectoryFileIndex = 0;
    CurrentDataFileIndex = 0;
    while(true) {
      
      if (bufferedReader.ready()) {
        
        String fileName = bufferedReader.readLine();
        
        if (fileName.startsWith("226 Transfer complete.")) {
          return;
        }
        
        lineCount++;
        
        if (lineCount > 2) {
          
          if (fileName.endsWith("/")) {
            CurrentDirectoryFileNames[CurrentDirectoryFileIndex++] = fileName;
          } else {
            
            CurrentDataFileNames[CurrentDataFileIndex++] = fileName;
          }
        }
        
        
      } else {
        
      
      wait(1);
      }
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
  
  int MaxStackSize = 10000;
  public void doit() throws Exception {
    
    String machineName = (String) this.pullInput(0);
    String userName = (String) this.pullInput(1);
    String remoteRootDirectoryName = (String) this.pullInput(2);
    String localRootDirectoryName = (String) this.pullInput(3);
    
    
    
    Runtime runtime = Runtime.getRuntime();
    Process process = null;
    try {
      process = runtime.exec(FTPCommandString);
    } catch (Exception e) {
      System.out.println("exec error!!!");
    }
    ;
    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    //InputStream inputStream = ;
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    InputStream errorStream = process.getErrorStream();
    
    
    
    bufferedWriter.write("verbose");
    bufferedWriter.newLine();
    bufferedWriter.flush();
    //readLinesUntil(bufferedReader, "Verbos");
    bufferedWriter.write("open " + machineName);
    bufferedWriter.newLine();
    bufferedWriter.flush();
    readLinesUntil(bufferedReader, "Connected to turmoil.ncsa.uiuc.edu.");
    bufferedWriter.write(user);
    bufferedWriter.newLine();
    bufferedWriter.flush();
    readLinesUntil(bufferedReader, "230 User " + user + " logged in.");
    bufferedWriter.write("cd " + "\"" + remoteRootDirectoryName + "\"");
    bufferedWriter.newLine();
    bufferedWriter.flush();
    readLinesUntil(bufferedReader, "250 DiskXtender CWD command successful.");
    
    
    int MaxNumFiles = 100000;
    String [] allDataFiles = new String[MaxNumFiles];
    int dataFileIndex = 0;
    String [] allDirectoryFiles = new String[MaxNumFiles];
    int directoryFileIndex = 0;
    
    // add slash if necessary
    if (!remoteRootDirectoryName.endsWith("/"))
      remoteRootDirectoryName += "/";
    
    localRootDirectoryName.replace('\\', '/');
    if (!localRootDirectoryName.endsWith("/"))
      localRootDirectoryName += "/";
    
    
    // initialize FIFO
    
    String [] searchTreeLIFO = new String[MaxStackSize];
    
    int searchTreeLIFOReadIndex = 0;
    int searchTreeLIFOWriteIndex = 0;
    
    searchTreeLIFO[searchTreeLIFOWriteIndex++] = remoteRootDirectoryName;
    allDirectoryFiles[directoryFileIndex++] = remoteRootDirectoryName;
    
    
    while (searchTreeLIFOReadIndex < searchTreeLIFOWriteIndex) {
      
      String currentDirectoryName = searchTreeLIFO[searchTreeLIFOReadIndex];
      searchTreeLIFOReadIndex++;
      
      String localDirectoryName = localRootDirectoryName + currentDirectoryName.substring(remoteRootDirectoryName.length());
      
      System.out.println("localDirectoryName: " + localDirectoryName);
      
      // create local directory
      File localDirectory = new File(localDirectoryName);
      
      if (!localDirectory.isDirectory())
        localDirectory.mkdir();
      
      bufferedWriter.write("lcd " + "\"" + localDirectoryName + "\"");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      //readLinesUntil(bufferedReader, "Local directory ");
      
      bufferedWriter.write("cd " + "\"" + currentDirectoryName + "\"");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      
      readLinesUntil(bufferedReader, "250 DiskXtender CWD command successful.");
      
      
      bufferedWriter.write("ls -1F" + ReverseFlag);
      bufferedWriter.newLine();
      bufferedWriter.flush();
      
      readFileNames(bufferedReader);
      
      System.out.println("CurrentDirectoryFileIndex: " + CurrentDirectoryFileIndex);
      for (int i = 0; i < CurrentDirectoryFileIndex; i++) {
        String newDirectoryFileName = currentDirectoryName + CurrentDirectoryFileNames[i];
        System.out.println("Directory File: " + newDirectoryFileName);
        allDirectoryFiles[directoryFileIndex++] = newDirectoryFileName;
        searchTreeLIFO[searchTreeLIFOWriteIndex++] = newDirectoryFileName;
        
      }
      
      System.out.println("CurrentDataFileIndex: " + CurrentDataFileIndex);
      
      for (int i = 0; i < CurrentDataFileIndex; i++) {
        
        String newDataFileName = currentDirectoryName + CurrentDataFileNames[i];
        System.out.println("Data File: " + newDataFileName);
        allDataFiles[dataFileIndex++] = newDataFileName;
        
        if (dataFileIndex >= StartDataFileIndex) {
          
          
          if (true /*CurrentDataFileNames[i].indexOf("[#]") == -1*/) {
            
            //String command = "quote stage 0 " + "\"" + CurrentDataFileNames[i] + "\"";
            String command = "get " + "\"" + CurrentDataFileNames[i] + "\"";
            
            
            System.out.println(command);
            
            bufferedWriter.write(command);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            
            //readLine(bufferedReader);
            readLinesUntil(bufferedReader, "226 Transfer complete.");
            IgnoreInputStream(errorStream);
          }

        }
      }
      
      System.out.println("directoryFileIndex: " + directoryFileIndex);
      System.out.println("dataFileIndex:      " + dataFileIndex);
      System.out.println("searchTreeLIFOWriteIndex: " + searchTreeLIFOWriteIndex);
      System.out.println("searchTreeLIFOReadIndex:  " + searchTreeLIFOReadIndex);
      
      
      IgnoreInputStream(errorStream);
    }
    
    
    
    
    bufferedWriter.close();
    bufferedReader.close();
    
    
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
      IgnoreInputStream(errorStream);
    }
    
    
    this.pushOutput(null, 0);
  }
  
}