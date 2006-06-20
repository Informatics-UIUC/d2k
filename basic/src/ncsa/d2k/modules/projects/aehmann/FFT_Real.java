package ncsa.d2k.modules.projects.aehmann;

import ncsa.d2k.core.modules.*;


public class FFT_Real extends ComputeModule {

	private boolean Inverse = false;
	public void setInverse(boolean value) {
		this.Inverse = value;
	}

	public boolean getInverse() {
		return this.Inverse;
	}

	////////////////////
	//  INFO METHODS  //
	////////////////////

	public String getModuleName() {
		return "FFT_Real";
	}
	public String getModuleInfo() {
		return "This module computes an in-place radix-2 FFT of a real (noncomplex) input signal, e.g. an audio signal.  Arrays that are not a power-of-2 " +
				"length are automatically zero-padded to the next appropriate length.  The output is split into two separate portions," +
				"the first corresponding to the real portion, the second corresponding to the imaginary portion.  Since the input is a" +
				"real signal, only half of the symmetric spectrum is outputted, i.e. the spectrum over the frequency range of 0 to pi radians";
	}

	public String getInputName(int i) {
		switch (i) {
			case 0 :
				return "Input Signal";
		}
		return "";
	}
	public String[] getInputTypes() {
		String[] in = { "[D" };
		return in;
	}
	public String getInputInfo(int i) {
		switch (i) {
			case 0 :
				return "Strictly real input signal (Double array)";
		}
		return "";
	}

	public String getOutputName(int i) {
		switch (i) {
			case 0 :
				return "Real Part";
			case 1 :
				return "Imaginary Part";
			default :
				return "No such output";
		}
	}

	public String[] getOutputTypes() {
		String[] types = { "[D", "[D" };
		return types;
	}

	public String getOutputInfo(int i) {
		switch (i) {
			case 0 :
				return "Real part of spectrum. (Double array).";
			case 1 :
				return "Imaginary part of spectrum. (Double array).";
			default :
				return "No such output";
		}
	}

	void SWAP(double[] array, int a, int b) {
		double temp = array[a];
		array[a] = array[b];
		array[b] = temp;
	}

	public void doit() throws Exception {

		//define SWAP(a,b) tempr=(a);(a)=(b);(b)=tempr
		//void four1(float data[], unsigned long nn, int isign)
		

	    Object object1 = (Object) this.pullInput(0);

	    if (object1 == null) {
	    	this.pushOutput(null, 0);
	    	this.pushOutput(null, 1);
	    	return;
	    }
	  	
	    double[] data_real = (double []) object1;
		
		int isign;
		if (Inverse) {
			isign = -1;
		}
		else {
			isign = 1;
		}

		double dnn = Math.log((double) data_real.length) / Math.log(2.0);
		int log2nn = (int)Math.ceil(dnn);
		int nn = 2 << (log2nn - 3);
		int length = (int)Math.pow(2.0,(double)log2nn);
		nn = length;

		if (false) {
		System.out.println("isign =" + isign);
		System.out.println("nn = " + nn);
		System.out.println("data.length = " + data_real.length);
		System.out.println("dnn = " + dnn);
		System.out.println("log2nn = " + log2nn);
		System.out.println("length " + length);
		}
		
//		Zero-Pad data arrays to a power-of-2 length 
		double[] data = new double[2*length];
		for (int k=0; k< length; k++ ) {
			if (k < data_real.length) {
				data[2*k] = data_real[k];
				data[2*k+1] = 0.0;
			}
			else {
				data[2*k] = 0.0;
				data[2*k+1] = 0.0;
			}
		}

		int n, mmax, m, j, istep, i;
		double wtemp, wr, wpr, wpi, wi, theta;
		//Double precision for the trigonometric recurrences.
		double tempr, tempi;
		n = nn << 1;
		j = 1;
		for (i = 1;
			i < n;
			i += 2) { //This is the bit-reversal section of the routine.
			if (j > i) {
				SWAP(data, j-1, i-1); //Exchange the two complex numbers.
				SWAP(data, j, i);
			}
			m = nn;
			while (m >= 2 && j > m) {
				j -= m;
				m >>= 1;
			}
			j += m;
		}
		//Here begins the Danielson-Lanczos section of the routine.
		mmax = 2;
		while (n > mmax) { //Outer loop executed log2 nn times.
			istep = mmax << 1;
			theta = isign * (6.28318530717959 / mmax);
			//Initialize the trigonometric recurrence.
			wtemp = Math.sin(0.5 * theta);
			wpr = -2.0 * wtemp * wtemp;

			wpi = Math.sin(theta);
			wr = 1.0;
			wi = 0.0;
			for (m = 1;
				m < mmax;
				m += 2) { //Here are the two nested inner loops.
				for (i = m; i <= n; i += istep) {
					j = i + mmax; //This is the Danielson-Lanczos formula:
					tempr = wr * data[j-1] - wi * data[j];
					tempi = wr * data[j] + wi * data[j-1];
					data[j-1] = data[i-1] - tempr;
					data[j] = data[i] - tempi;
					data[i-1] += tempr;
					data[i] += tempi;
				}
				wr = (wtemp = wr) * wpr - wi * wpi + wr;  //Trigonometric recurrence.
				wi = wi * wpr + wtemp * wpi + wi;
			}
			mmax = istep;
		}
		
		double [] real      = new double[length/2+1];
		double [] imaginary = new double[length/2+1];
		for (i = 0; i < length/2; i++) {
			real[i] = data[i * 2];
			imaginary[i] = -data[i * 2 + 1];
		}
		
		this.pushOutput(real,0);
		this.pushOutput(imaginary, 1);
	}

}
