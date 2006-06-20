package ncsa.d2k.modules.projects.dtcheng.io.read;

import ncsa.d2k.core.modules.*;
import java.io.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.projects.dtcheng.streams.*;

public class ReadWholeFileFromInput extends InputModule {

  private int readBufferSize = 10000000;

  public void setReadBufferSize(int value) {
    this.readBufferSize = value;
  }

  public int getReadBufferSize() {
    return this.readBufferSize;
  }

  private int maxNumBytesToRead = -1;

  public void setMaxNumBytesToRead(int value) {
    this.maxNumBytesToRead = value;
  }

  public int getMaxNumBytesToRead() {
    return this.maxNumBytesToRead;
  }

  public String getModuleName() {
    return "ReadWholeFileFromInput";
  }

  public String getModuleInfo() {
    return "This module reads the whole with one read and outputs the byte array.  ";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "File";
    default:
      return "No such input.";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "File";
    default:
      return "No such input.";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "java.io.File" };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "ByteArray";
    default:
      return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "A byte array representing the file.";
    default:
      return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[B" };
    return types;
  }
  
  StreamMarker streamMarker = new StreamMarker();
  Class streamMarkerClass = streamMarker.getClass();
    
  public void doit() throws Exception {
    
    Object object = this.pullInput(0);
    if (streamMarkerClass.isInstance(object)) {
      this.pushOutput(object, 0);
      return;
    }
    
    
    File file = (File) object;

    RandomAccessFile input = null;

    int fileLength = (int) file.length();
    //System.out.println("fileLength = " + fileLength);

    if ((maxNumBytesToRead > 0) && (maxNumBytesToRead < fileLength))
      fileLength = maxNumBytesToRead;

    byte[] buffer = new byte[fileLength];

    try {
      input = new RandomAccessFile(file, "r");
    } catch (Exception e) {
      System.out.println("user.dir: " + System.getProperty("user.dir"));
      System.out.println("Error!!! could not open " + file);
    }

    int numToRead = readBufferSize;
    int fillPtr = 0;

    while (true) {

      int numLeft = fileLength - fillPtr;

      if (numLeft < numToRead)
        numToRead = numLeft;

      int numRead = input.read(buffer, fillPtr, (int) numToRead);

      if (numRead <= 0)
        break;

      fillPtr += numRead;
    }

    input.close();
    this.pushOutput(buffer, 0);
    return;
  }

}