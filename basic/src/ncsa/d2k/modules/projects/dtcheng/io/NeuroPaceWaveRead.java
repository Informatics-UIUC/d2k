package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;
import java.io.*;

public class NeuroPaceWaveRead extends InputModule {

  private int ChannelOfInterest = 1;
  private int ChannelOfInterestIndex = 0;

  public void setChannelOfInterest(int value) {
    this.ChannelOfInterest = value;
    this.ChannelOfInterestIndex = value - 1;
  }

  public int getChannelOfInterest() {
    return this.ChannelOfInterest;
  }

  private int FrameSize = 250 * 4;

  public void setFrameSize(int value) {
    this.FrameSize = value;
  }

  public int getFrameSize() {
    return this.FrameSize;
  }

  private int BytesPerSample = 2;

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "File";
    case 1:
      return "Number of Channels";
    default:
      return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "File";
    case 1:
      return "Number of Channels";
    default:
      return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "java.io.File" , "[D"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Sample Stream";
    default:
      return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Sample Stream";
    default:
      return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[D" };
    return types;
  }

  public String getModuleInfo() {
    return "NeuroPaceWaveRead";
  }

  public String getModuleName() {
    return "NeuroPaceWaveRead";
  }


  int unsignedByte(byte value) {
    if (value >= 0)
      return value;

      return 256 + value;
  }

  File sampleFile;

  RandomAccessFile randomAccessFile;

  int dataLength;

  int NumSamples;

  long lastPosition;

  int value1L;

  int value2L;

  int value3L;


  double signedValue;

  double scalingFactor;

  int numChannels;

  long SampleRate;

  public void openFile() {

    randomAccessFile = null;

    try {
      randomAccessFile = new RandomAccessFile(sampleFile, "r");
    } catch (Exception e) {
      System.out.println("couldn't open file: " + sampleFile);
    }

    try {
      dataLength = (int) (randomAccessFile.length());
    } catch (Exception e) {
      System.out.println("couldn't length() file: " + sampleFile);
    }

    value1L = -1;
    value2L = -1;
    signedValue = -1;
  }

  public void closeFile() {

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
    
    if ((Count == 0) && (this.getFlags()[0]>0) && (this.getFlags()[1]>0)) {
      return true;
    }

    if ((Count != 0) && (Count < NumFrames)) {
      return true;
    }

    return false;
  }

  byte[] buffer;

  double[] Samples;

  public void doit() throws Exception {

    if (Count == 0) {
      sampleFile = (File) this.pullInput(0);
      double [] doubleArray = (double []) this.pullInput(1);

      if (sampleFile == null) {
        this.pushOutput(null, 0);
        return;
      }
      
      openFile();
      
      numChannels = (int) doubleArray[0];
      BytesPerSample = 2;

      SampleRate = 250;

      NumSamples = dataLength / BytesPerSample / numChannels;

      NumFrames = NumSamples / FrameSize;

      buffer = new byte[FrameSize * BytesPerSample * numChannels];

      scalingFactor = 1 << (BytesPerSample * 8 - 1);
    }

    if (Count < NumFrames) {

      //System.out.println("Count = " + Count);

      int numRead = randomAccessFile.read(buffer);

      if (numRead != buffer.length) {
        System.out.println("numRead != buffer.length");
        throw new Exception();
      }

      Samples = new double[FrameSize];
      for (int i = 0; i < FrameSize; i++) {
        int index = numChannels * BytesPerSample * i + ChannelOfInterestIndex * BytesPerSample;
        value1L = unsignedByte(buffer[index++]);
        value2L = unsignedByte(buffer[index++]);

        signedValue = (short) ((value2L << 8) + value1L);
        Samples[i] = signedValue / scalingFactor;
      }

      this.pushOutput(Samples, 0);

      Count++;
      
      if (Count == NumFrames) {

        this.pushOutput(null, 0);
        
        closeFile();
        beginExecution();
        return;
      }

    }

  }
}