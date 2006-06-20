package ncsa.d2k.modules.projects.aehmann;

import ncsa.d2k.core.modules.ComputeModule;


public class fft extends ComputeModule{

  private int           SampleRate = 44100;
  public  void       setSampleRate (int value) {       
  	this.SampleRate = value;
  }

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
    return "fft";
  }
  public String getModuleInfo() {
    return "This computes an inplace fft.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0: return "AudioSamples";
    }
    return "";
  }
  public String[] getInputTypes() {
    String [] in = {"[D"};
    return in;
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "AudioSamples";
    }
    return "";
  }


  public String getOutputName(int i) {
	switch (i) {
	  case 0:
		return "Real";
	  case 1:
		return "Imaginary";
	  default:
		return "No such output";
	}
  }
  
  public String[] getOutputTypes() {
	String[] types = {
		"java.lang.Long", "[D"};
	return types;
  }
  
  public String getOutputInfo(int i) {
	switch (i) {
	  case 0:
		return "Real";
	  case 1:
		return "Imaginary";
	  default:
		return "No such output";
	}
  }


  public void doit() throws Exception {

    double [] audioBuffer = (double []) this.pullInput(0);

    if (audioBuffer == null) {
      this.pushOutput(null, 0);
      return;
    }

	/* compute the next power of 2 length and zero pad for the radix-2 fft */
    int numSamples = audioBuffer.length;
    int numSamplesPow2 = nextPow2(numSamples);
    //double [] audioBufferZP = 



 

    //this.pushOutput(Real, 0);
	//this.pushOutput(Imaginary, 1);

  }
  

  public double[] log2(double data){

		  float e, f;
		  double[] output = new double[2];

		  double result = 0;
		  int exp = 0;

		  while ((result <= 0.5) | (result > 1)){
				  exp++;
				  result = data/java.lang.Math.pow(2, exp);
		  }

		 output[0] = result;
		  output[1] = exp;

		  return output;
  }


  public int nextPow2(int n){

		  if (n<1){
				  n = 1;
				  return n;
		  }
		  else{
				  double[] f = log2(n);
				  return (int)Math.pow(2.0,f[1]);
		  }
  }

}
