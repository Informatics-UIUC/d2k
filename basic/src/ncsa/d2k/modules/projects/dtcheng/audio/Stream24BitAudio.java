package ncsa.d2k.modules.projects.dtcheng.audio;

import ncsa.d2k.core.modules.*;
import java.io.*;

public class Stream24BitAudio
    extends InputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

    private boolean JumpToEnd = true;
  public void setJumpToEnd(boolean value) {
    this.JumpToEnd = value;
  }

  public boolean getJumpToEnd() {
    return this.JumpToEnd;
  }

  private boolean Stereo = false;
  public void setStereo(boolean value) {
	this.Stereo = value;
  }

  public boolean getStereo() {
	return this.Stereo;
  }

  private boolean Terminate = false;
  public void setTerminate(boolean value) {
	this.Terminate = value;
  }

  public boolean getTerminate() {
	return this.Terminate;
  }

  private int HeaderSize = 724;
  public void setHeaderSize(int value) {
    this.HeaderSize = value;
  }

  public int getHeaderSize() {
    return this.HeaderSize;
  }

  private int BytesPerSample = 3;
  public void setBytesPerSample(int value) {
    this.BytesPerSample = value;
  }

  public int getBytesPerSample() {
    return this.BytesPerSample;
  }

  private int ReportInterval = 30;
  public void setReportInterval(int value) {
    this.ReportInterval = value;
  }

  public int getReportInterval() {
    return this.ReportInterval;
  }

  private int WaitTimeInMilliseconds = 1;
  public void setWaitTimeInMilliseconds(int value) {
    this.WaitTimeInMilliseconds = value;
  }

  public int getWaitTimeInMilliseconds() {
    return this.WaitTimeInMilliseconds;
  }

  private int SamplesPerFrame = 1600;
  public void setSamplesPerFrame(int value) {
    this.SamplesPerFrame = value;
  }

  public int getSamplesPerFrame() {
    return this.SamplesPerFrame;
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
    String[] types = {
        "java.io.File"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Time";
      case 1:
        return "Samples";
      default:
        return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Time";
      case 1:
        return "Samples";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Long", "[D"};
    return types;
  }

  public String getModuleInfo() {
    return "Stream24BitAudio";
  }

  public String getModuleName() {
    return "Stream24BitAudio";
  }

  void wait(int time) throws Exception {
    try {
      synchronized (Thread.currentThread()) {
        Thread.sleep(time);
      }
    }
    catch (Exception e) {
      System.out.println("wait error!!!");
      throw e;
    }
  }

  int unsignedByte(byte value) {
    if (value > 0)
      return value;
    else
      return 256 + value;
  }

  File AudioFile;
  RandomAccessFile randomAccessFile;
  long dataLength;
  int count;
  long lastPosition;
  int bufferSize;
  byte[] buffer;
  int value1;
  int value2;
  int value3;
  double signedValue;
  double scalingFactor;
  int numChannels;

  public void openFile() {
  	
	if (Stereo) {
		numChannels = 2;
	}
	else {
		numChannels = 1;
	}

    setTerminate(false);

    randomAccessFile = null;

    try {
      randomAccessFile = new RandomAccessFile(AudioFile, "r");
    }
    catch (Exception e) {
      System.out.println("couldn't open file: " + AudioFile);
    }

    try {
      dataLength = randomAccessFile.length() - HeaderSize;
    }
    catch (Exception e) {
      System.out.println("couldn't length() file: " + AudioFile);
    }

    dataLength = (dataLength / BytesPerSample) * BytesPerSample;

    count = 0;

    try {
      if (JumpToEnd) {
        randomAccessFile.seek(HeaderSize + dataLength);
        lastPosition = HeaderSize + dataLength;
      }
      else {
        randomAccessFile.seek(HeaderSize);
        lastPosition = HeaderSize;
      }
    }
    catch (Exception e) {
      System.out.println("couldn't seek() file: " + AudioFile);
    }

    bufferSize = BytesPerSample * SamplesPerFrame * numChannels;
    buffer = new byte[bufferSize];

    value1 = -1;
    value2 = -1;
    value3 = -1;
    signedValue = -1;
    scalingFactor = 1 << (BytesPerSample * 8 - 1);
  }

  public void closeFile() {

    setTerminate(false);

    try {
      randomAccessFile.close();
    }
    catch (Exception e) {
      System.out.println("couldn't close file: " + randomAccessFile);
    }

  }

  public void doit() throws Exception {

  	AudioFile = (File) this.pullInput(0);
  	
  	if (AudioFile == null) {
  	    this.pushOutput(null, 0);
  	    this.pushOutput(null, 1);
  		return;
  	}
  	
  	
  	openFile();
    
    

    if (Terminate)
      return;

    //wait(ReportInterval);

    int numAvailable = (int) (randomAccessFile.length() - lastPosition);

    while (numAvailable < bufferSize) {
      numAvailable = (int) (randomAccessFile.length() - lastPosition);
      wait(WaitTimeInMilliseconds);
    }

    Long time = new Long(System.currentTimeMillis());

    double[] samples = new double[SamplesPerFrame];
    int numRead = randomAccessFile.read(buffer);

    if (numRead != buffer.length)
      return;

    lastPosition += numRead;

    int index = 0;
    switch (BytesPerSample) {
      case 2:
        for (int i = 0; i < SamplesPerFrame; i++) {
          value1 = unsignedByte(buffer[index++]);
          value2 = unsignedByte(buffer[index++]);
          if (Stereo) {
			value1 += unsignedByte(buffer[index++]);
			value2 += unsignedByte(buffer[index++]);
			
			value1 /= 2;
			value2 /= 2;
          }
          signedValue = ((int) (((value2 << 8) + value1) << 8)) / 8;
          samples[i] = signedValue / scalingFactor;
        }
        break;
      case 3:
        for (int i = 0; i < SamplesPerFrame; i++) {
          value1 = unsignedByte(buffer[index++]);
          value2 = unsignedByte(buffer[index++]);
          value3 = unsignedByte(buffer[index++]);
		  if (Stereo) {
			value1 += unsignedByte(buffer[index++]);
			value2 += unsignedByte(buffer[index++]);
			value3 += unsignedByte(buffer[index++]);
			
			value1 /= 2;
			value2 /= 2;
			value3 /= 2;
		  }
          signedValue = ((int) (((((value3 << 8) + value2) << 8) + value1) << 8)) / 8;
          samples[i] = signedValue / scalingFactor;
        }
        break;
    }

    count++;

    if (count % ReportInterval == 0)
      System.out.println("count = " + count);

    closeFile();
    
    this.pushOutput(time, 0);
    this.pushOutput(samples, 1);

  }
}
