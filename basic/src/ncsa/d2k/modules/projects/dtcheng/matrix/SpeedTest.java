package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;

//import javax.crypto.j;

import ncsa.d2k.core.modules.*;
//import Jama.Matrix;

public class SpeedTest extends ComputeModule {

	public String getModuleName() {
		return "SpeedTest";
	}

	public String getModuleInfo() {
		return "This module does some silliness to try to do some speed testing.  ";
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "NumElements";
		case 1:
			return "NumIterations";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "NumElements";
		case 1:
			return "NumIterations";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = {
				"java.lang.Integer",
				"java.lang.Integer",
				};
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "Time";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "Time";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = {
				"java.lang.Double",
				};
		return types;
	}

	public void doit() {

		System.out.println("creating data arrays");

		int NumElements = ((Integer) this.pullInput(0)).intValue();
		int NumRepetitions = ((Integer) this.pullInput(1)).intValue();
		//    int NumElements = 10000000;
		double[] x = new double[NumElements];
		double[] y = new double[NumElements];
		double[] z = new double[NumElements];

		for (int i = 0; i < NumElements; i++) {
			x[i] = i;
			y[i] = i;
			z[i] = i;
		}

		System.out.println("starting");

		long NumberOfIterations = NumElements;
//		long NumRepetitions = 10;
				
			System.out.println("Doing empty loop");
			long StartTime = System.currentTimeMillis();

			for (int r = 0; r < NumRepetitions; r++) {
				for (int i = 0; i < NumberOfIterations; i++) {
				}
			}
			long EndTime = System.currentTimeMillis();

			double DurationInSeconds = (double) (EndTime - StartTime) / 1000.0;
			double OperationsPerSecond = NumberOfIterations / DurationInSeconds;

			System.out.println("DurationInSeconds = " + DurationInSeconds);
			System.out.println("OperationsPerSecond = " + OperationsPerSecond);
		
		
//			System.out.println("Doing x[i] = Math.log((y[i] + 1.0) * (z[i] + 1.0))");
			System.out.println("Doing x[i] = x[i] = y[i] * z[i]");
			long StartTime2 = System.currentTimeMillis();

			for (int r = 0; r < NumRepetitions; r++) {
				for (int i = 0; i < NumberOfIterations; i++) {
//					x[i] = Math.log((y[i] + 1.0) * (z[i] + 1.0));
					x[i] = y[i] * z[i];
				}
			}
			double EndTime2 = System.currentTimeMillis();

			double DurationInSeconds2 = (double) (EndTime2 - StartTime2) / 1000.0;
			double OperationsPerSecond2 = NumberOfIterations * NumRepetitions
					/ DurationInSeconds2;

			System.out.println("DurationInSeconds = " + DurationInSeconds2);
			System.out.println("OperationsPerSecond = " + OperationsPerSecond2);
		
		this.pushOutput(new Double(DurationInSeconds2),0);
	}

}