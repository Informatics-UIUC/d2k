package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.modules.projects.dtcheng.streams.*;
import ncsa.d2k.core.modules.*;

public class ByteArrayToStringStream extends ComputeModule {
  
  private int EOLNumBytes = 1;
  
  public void setEOLNumBytes(int value) {
    this.EOLNumBytes = value;
  }
  
  public int getEOLNumBytes() {
    return this.EOLNumBytes;
  }
  
  private byte EOLByte1 = 10;
  
  public void setEOLByte1(byte value) {
    this.EOLByte1 = value;
  }
  
  public byte getEOLByte1() {
    return this.EOLByte1;
  }
  
  private byte EOLByte2 = 10;
  
  public void setEOLByte2(byte value) {
    this.EOLByte2 = value;
  }
  
  public byte getEOLByte2() {
    return this.EOLByte2;
  }
  
  public String getModuleInfo() {
    return "ByteArrayToStringStream";
  }
  
  public String getModuleName() {
    return "ByteArrayToStringStream";
  }
  
  public String[] getInputTypes() {
    String[] types = { "[B" };
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Byte Array";
      default:
        return "No such input";
    }
  }
  
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Byte Array";
      default:
        return "No such input";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = { "java.lang.String" };
    return types;
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "String";
      default:
        return "No such output";
    }
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "String";
      default:
        return "No such output";
    }
  }
  
  StreamMarker streamMarker = new StreamMarker();
  Class streamMarkerClass = streamMarker.getClass();
  public void doit() {
    
    Object object = this.pullInput(0);
    if (streamMarkerClass.isInstance(object)) {
      this.pushOutput(object, 0);
      return;
    }
    
    byte [] byteArray = (byte [])object;
    
    int byteArrayLenth = byteArray.length;
    int startIndex = 0;
    int endIndex = 0;
    boolean inWord = false;
    int numWords = 0;
    
      this.pushOutput(new StreamStartMarker(), 0);
    for (int i = 0; i < byteArrayLenth; i++) {
      
      boolean endOfWord = false;
      if (i == byteArrayLenth - 1) {
        endOfWord = true;
      }
      if (byteArray[i] == (byte) ' ') {
        endOfWord = true;
      }
      if ((EOLNumBytes == 1) && (byteArray[i] == EOLByte1)) {
        endOfWord = true;
      }
      if ((EOLNumBytes == 2) && ((byteArray[i] == EOLByte1) || (byteArray[i] == EOLByte2))) {
        endOfWord = true;
      }
      
      if (!endOfWord && !inWord) {
        startIndex = i;
        inWord = true;
      }
      
      if (inWord && endOfWord) {
        this.pushOutput( new String(byteArray, startIndex, i - startIndex), 0);
        inWord = false;
        numWords++;
      }
      
    }
      this.pushOutput(new StreamEndMarker(), 0);
  }
}