package ncsa.d2k.modules.projects.dtcheng.generators;

import ncsa.d2k.core.modules.*;

public class GenerateDoubleArrayStream extends ComputeModule {

	private int MaxNumUnfinishedProblems = 2;

	public void setMaxNumUnfinishedProblems(int value) {
		this.MaxNumUnfinishedProblems = value;
	}

	public int getMaxNumUnfinishedProblems() {
		return this.MaxNumUnfinishedProblems;
	}

	private int FrameSize = 44100;

	public void setFrameSize(int value) {
		this.FrameSize = value;
	}

	public int getFrameSize() {
		return this.FrameSize;
	}

	private int NumTimes = 10;

	public void setNumTimes(int value) {
		this.NumTimes = value;
	}

	public int getNumTimes() {
		return this.NumTimes;
	}

	public String getModuleName() {
		return "GenerateDoubleArrayStream";
	}

	public String getModuleInfo() {
		return "GenerateDoubleArrayStream";
	}

	public String getInputName(int i) {
		switch (i) {
		}
		return "";
	}

	public String[] getInputTypes() {
		String[] types = { "java.lang.Object" };
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "Trigger";
		default:
			return "No such input";
		}
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "Time";
		}
		return "";
	}

	public String[] getOutputTypes() {
		String[] types = { "[D", };
		return types;
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "Time";
		default:
			return "No such output";
		}
	}

	boolean firstTime = true;
	long Count = 0;
	int  NumUnfinishedProblems = 0;

	public void beginExecution() {
		firstTime = true;
		Count = 0;
		NumUnfinishedProblems = 0;
	}

	/*
	 public boolean isReady() {
	 if (Count == 0) {
	 return true;
	 }
	 else {
	 if (Count < NumTimes) {
	 return true;
	 }
	 }
	 return false;
	 }

	 */


	public void doit() throws Exception {

		Object object = this.pullInput(0);
		
		// ignore initial trigger when keeping track of the number unfinished problems
		if (!firstTime) {
			NumUnfinishedProblems--;
		}

		while ((Count < NumTimes) && (NumUnfinishedProblems < MaxNumUnfinishedProblems)) {

			double[] data = new double[FrameSize];

			for (int i = 0; i < FrameSize; i++) {
				data[i] = Count * 1000000 + i;
			}

			this.pushOutput(data, 0);
			NumUnfinishedProblems++;
			Count++;
			
			if (Count == NumTimes) {
				this.pushOutput(null, 0);
			}

		}
		firstTime = false;
	}
}