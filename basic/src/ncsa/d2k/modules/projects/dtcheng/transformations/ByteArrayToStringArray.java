package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.core.modules.*;

public class ByteArrayToStringArray extends ComputeModule {

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
    return "ByteArrayToStringArray";
  }

  public String getModuleName() {
    return "ByteArrayToStringArray";
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
      return "NO SUCH INPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[S" };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "String Array";
    default:
      return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Strign Array";
    default:
      return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {

    Object object = (Object) this.pullInput(0);

    if (object == null) {
      this.pushOutput(null, 0);
      return;
    }

    byte[] byteArray = (byte[]) object;

    int byteArrayLenth = byteArray.length;

    // count number of lines
    int count = 0;
    for (int i = 0; i < byteArrayLenth; i++) {

      switch (EOLNumBytes) {
      case 1:
        if (byteArray[i] == EOLByte1) {
          count++;
        }
        break;
      case 2:
        if ((i < byteArrayLenth - 1) && (byteArray[i] == EOLByte1) && (byteArray[i + 1] == EOLByte2)) {
          count++;
          i++;
        }
        break;
      }

    }

    String[] stringArray = new String[count];

    // extract strings number of lines
    count = 0;
    int startIndex = 0;
    for (int i = 0; i < byteArrayLenth; i++) {

      switch (EOLNumBytes) {
      case 1:
        if (byteArray[i] == EOLByte1) {
          stringArray[count] = new String(byteArray, startIndex, i - startIndex - 1);
          count++;
          startIndex = i + 1;
        }
        break;
      case 2:
        if ((i < byteArrayLenth - 1) && (byteArray[i] == EOLByte1) && (byteArray[i + 1] == EOLByte2)) {
          stringArray[count] = new String(byteArray, startIndex, i - startIndex - 1);
          count++;
          startIndex = i + 2;
          i++;
        }
        break;
      }

    }

    this.pushOutput(stringArray, 0);

  }
}