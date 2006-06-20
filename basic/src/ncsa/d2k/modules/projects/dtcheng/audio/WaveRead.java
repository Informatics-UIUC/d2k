package ncsa.d2k.modules.projects.dtcheng.audio;

import ncsa.d2k.core.modules.*;
import java.io.*;

public class WaveRead extends InputModule {

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

	private int HeaderSize = 44;

	private int BytesPerSample = 2;

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
		return "WaveRead";
	}

	public String getModuleName() {
		return "WaveRead";
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
		else
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

	int numChannels;

	long SampleRate;

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
		if (Count == 0) {
			return true;
		} else {
			if (Count < NumFrames) {
				return true;
			}
		}
		return false;
	}


	
	byte[] buffer;
	double[] LeftSamples;
	double[] RightSamples;

	public void doit() throws Exception {

		if (Count == 0) {
		AudioFile = (File) this.pullInput(0);

		if (AudioFile == null) {
			this.pushOutput(null, 0);
			this.pushOutput(null, 1);
			return;
		}

		openFile();

		// Read in *.wav header information
		byte[] header = new byte[44];
		int hdrSize = randomAccessFile.read(header);
		
		for (int i = 0; i < 44; i++ ) {
		  System.out.println("i = " + i + " v = " + header[i]);
		}

		if (header[22] == 1)
			numChannels = 1;
		else
			numChannels = 2;
		if (header[34] == 8)
			BytesPerSample = 1;
		else if (header[34] == 16)
			BytesPerSample = 2;
		else
			BytesPerSample = 3;

		SampleRate = (long) ((unsignedByte(header[27]) << 24)
				+ (unsignedByte(header[26]) << 16)
				+ (unsignedByte(header[25]) << 8) + (unsignedByte(header[24])));

		if (DisplayProperties) {
			System.out.println("Path           : " + AudioFile.getPath());
			System.out.println("numChannels    : " + numChannels);
			System.out.println("Sample Rate    : " + SampleRate);
			System.out.println("Bits per Sample: " + header[34]);
		}

		// Read in *.wav sample data

		NumSamples = dataLength / BytesPerSample / numChannels;

		NumFrames = NumSamples / FrameSize;

		buffer = new byte[FrameSize * BytesPerSample * numChannels];
		LeftSamples = new double[FrameSize];
		RightSamples = null;

		if (numChannels == 2)
			RightSamples = new double[FrameSize];

		scalingFactor = 1 << (BytesPerSample * 8 - 1);
		}

		if (Count < NumFrames) {
		
			//System.out.println("Count = " + Count);

			int numRead = randomAccessFile.read(buffer);

			if (numRead != buffer.length) {
				System.out.println("numRead != buffer.length");
				throw new Exception();
			}


			int index = 0;
			switch (BytesPerSample) {
			case 1:
				for (int i = 0; i < FrameSize; i++) {
					value1L = unsignedByte(buffer[index++]);
					LeftSamples[i] = (value1L - 128) / scalingFactor;
					if (numChannels == 2) {
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
					if (numChannels == 2) {
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
					if (numChannels == 2) {
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
			if (Count == NumFrames) {
				closeFile();
				beginExecution();
				return;
			}
				
		}
		
		
		
		

	}
}