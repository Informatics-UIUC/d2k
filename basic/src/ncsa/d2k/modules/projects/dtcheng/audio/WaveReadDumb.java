package ncsa.d2k.modules.projects.dtcheng.audio;

import ncsa.d2k.core.modules.*;
import java.io.*;

public class WaveReadDumb extends InputModule {

  private int NumChannels = 2;

  public void setNumChannels(int value) {
    this.NumChannels = value;
  }

  public int getNumChannels() {
    return this.NumChannels;
  }


  private int HeaderSize = 44;

  public void setHeaderSize(int value) {
    this.HeaderSize = value;
  }

  public int getHeaderSize() {
    return this.HeaderSize;
  }

  private int BytesPerSample = 2;

  public void setBytesPerSample(int value) {
    this.BytesPerSample = value;
  }

  public int getBytesPerSample() {
    return this.BytesPerSample;
  }

  private int SampleRate = 44100;

  public void setSampleRate(int value) {
    this.SampleRate = value;
  }

  public int getSampleRate() {
    return this.SampleRate;
  }

  private int FrameSize = 44100 * 10;

  public void setFrameSize(int value) {
    this.FrameSize = value;
  }

  public int getFrameSize() {
    return this.FrameSize;
  }

  private boolean DisplayProperties = false;

  public void setDisplayProperties(boolean value) {
    this.DisplayProperties = value;
  }

  public boolean getDisplayProperties() {
    return this.DisplayProperties;
  }

  private boolean Terminate = false;

  public void setTerminate(boolean value) {
    this.Terminate = value;
  }

  public boolean getTerminate() {
    return this.Terminate;
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "Trigger";
    default:
      return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "File Name";
    default:
      return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "java.io.File" };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Left Channel";
    case 1:
      return "Right Channel";
    default:
      return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Left Channel";
    case 1:
      return "Right Channel";
    default:
      return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[D", "[D" };
    return types;
  }

  public String getModuleInfo() {
    return "WaveReadDumb";
  }

  public String getModuleName() {
    return "WaveReadDumb";
  }

  void wait(int time) throws Exception {
    try {
      synchronized (Thread.currentThread()) {
        Thread.sleep(time);
      }
    } catch (Exception e) {
      System.out.println("wait error!!!");
      throw e;
    }
  }

  int unsignedByte(byte value) {
    if (value >= 0)
      return value;
    
      return 256 + value;
  }

  File AudioFile;

  RandomAccessFile randomAccessFile;

  int dataLength;

  int NumSamples;

  long lastPosition;

  int value1L;

  int value2L;

  int value3L;

  int value1R;

  int value2R;

  int value3R;

  double signedValue;

  double scalingFactor;

  public void openFile() {

    setTerminate(false);

    randomAccessFile = null;

    try {
      randomAccessFile = new RandomAccessFile(AudioFile, "r");
    } catch (Exception e) {
      System.out.println("couldn't open file: " + AudioFile);
    }

    try {
      dataLength = (int) (randomAccessFile.length() - HeaderSize);
    } catch (Exception e) {
      System.out.println("couldn't length() file: " + AudioFile);
    }

    value1L = -1;
    value2L = -1;
    value3L = -1;
    value1R = -1;
    value2R = -1;
    value3R = -1;
    signedValue = -1;
  }

  public void closeFile() {

    setTerminate(false);

    try {
      randomAccessFile.close();
    } catch (Exception e) {
      System.out.println("couldn't close file: " + randomAccessFile);
    }

  }

  int Count;

  public void beginExecution() {
    Count = 0;
  }

  int NumFrames;

  public boolean isReady() {

    if ((Count == 0) && (this.getFlags()[0]>0)) {
      return true;
    }
    if ((Count != 0) && (Count < NumFrames)) {
      return true;
    }
    
    return false;
  }

  byte[] buffer;

  double[] LeftSamples;

  double[] RightSamples;

  public void doit() throws Exception {

    if (Count == 0) {
      AudioFile = (File) this.pullInput(0);

      if (AudioFile == null  ||  Terminate) {
        this.pushOutput(null, 0);
        this.pushOutput(null, 1);
        return;
      }

      openFile();

      // Read in *.wav header information
      byte[] header = new byte[HeaderSize];

      randomAccessFile.read(header);


      // Read in *.wav sample data

      NumSamples = dataLength / BytesPerSample / NumChannels;

      NumFrames = NumSamples / FrameSize;

      if (DisplayProperties) {
        System.out.println("Path           : " + AudioFile.getPath());
        System.out.println("NumChannels    : " + NumChannels);
        System.out.println("Sample Rate    : " + SampleRate);
        System.out.println("BytesPerSample : " + BytesPerSample);
        System.out.println("NumSamples     : " + NumSamples);
        System.out.println("NumFrames      : " + NumFrames);
      }

      buffer = new byte[FrameSize * BytesPerSample * NumChannels];

      scalingFactor = 1 << (BytesPerSample * 8 - 1);
    }

    if (Count < NumFrames) {

      //System.out.println("Count = " + Count);

      int numRead = randomAccessFile.read(buffer);

      if (numRead != buffer.length) {
        System.out.println("numRead != buffer.length");
        throw new Exception();
      }
      
      // allocate fresh memory for samples to be pushed
      LeftSamples = new double[FrameSize];
      RightSamples = null;

      if (NumChannels == 2)
        RightSamples = new double[FrameSize];

      
      
      int index = 0;
      switch (BytesPerSample) {
      case 1:
        for (int i = 0; i < FrameSize; i++) {
          value1L = unsignedByte(buffer[index++]);
          LeftSamples[i] = (value1L - 128) / scalingFactor;
          if (NumChannels == 2) {
            value1R = unsignedByte(buffer[index++]);
            signedValue = (value1R - 128) / scalingFactor;
            RightSamples[i] = signedValue / scalingFactor;
          }
        }
        break;
      case 2:
        for (int i = 0; i < FrameSize; i++) {
          value1L = unsignedByte(buffer[index++]);
          value2L = unsignedByte(buffer[index++]);
          signedValue = (short) ((value2L << 8) + value1L);
          LeftSamples[i] = signedValue / scalingFactor;
          if (NumChannels == 2) {
            value1R = unsignedByte(buffer[index++]);
            value2R = unsignedByte(buffer[index++]);
            signedValue = (short) ((value2R << 8) + value1R);
            RightSamples[i] = signedValue / scalingFactor;
          }
        }
        break;
      case 3:
        for (int i = 0; i < FrameSize; i++) {
          value1L = unsignedByte(buffer[index++]);
          value2L = unsignedByte(buffer[index++]);
          value3L = unsignedByte(buffer[index++]);
          signedValue = ((int) (((((value3L << 8) + value2L) << 8) + value1L) << 8)) / 8;
          LeftSamples[i] = signedValue / scalingFactor;
          if (NumChannels == 2) {
            value1R = unsignedByte(buffer[index++]);
            value2R = unsignedByte(buffer[index++]);
            value3R = unsignedByte(buffer[index++]);
            signedValue = ((int) (((((value3R << 8) + value2R) << 8) + value1R) << 8)) / 8;
            RightSamples[i] = signedValue / scalingFactor;
          }
        }
        break;
      }

      this.pushOutput(LeftSamples, 0);
      this.pushOutput(RightSamples, 1);

      Count++;
      if (Count == NumFrames || Terminate) {
        closeFile();
        beginExecution();
        return;
      }

    }

  }
}