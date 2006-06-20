package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.Utility;

public class ConvertByteArrayToDoubleArray extends OutputModule {


	  private int Offset = 0;
	  public void setOffset(int value) {
	    this.Offset = value;
	  }


	  public int getOffset() {
	    return this.Offset;
	  }

	  private int NumValues = 1;
	  public void setNumValues(int value) {
	    this.NumValues = value;
	  }


	  public int getNumValues() {
	    return this.NumValues;
	  }


	public String getModuleInfo() {
		return "ConvertByteArrayToDoubleArray";
	}

	public String getModuleName() {
		return "ConvertByteArrayToDoubleArray";
	}

	public String[] getInputTypes() {
		String[] types = { "[B" };
		return types;
	}

	public String[] getOutputTypes() {
		String[] types = { "[D" };
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "ByteArray";
		default:
			return "No such input";
		}
	}

	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "ByteArray";
		default:
			return "NO SUCH INPUT!";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "DoubleArray";
		default:
			return "No such output";
		}
	}

	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "DoubleArray";
		default:
			return "NO SUCH OUTPUT!";
		}
	}

	public void doit() {

		Object object = (Object) this.pullInput(0);
		if (object == null)
			return;

		byte[] array = (byte[]) object;
		
		String string = new String(array);
		
	    String[] elements = Utility.parseCSVList(string);
	    
	    double[] doubleArray = new double[NumValues];
	    
	    for (int i = 0; i < NumValues; i++) {
	    	doubleArray[i] = (Double.valueOf(elements[i + Offset])).doubleValue();
	    }

		this.pushOutput(doubleArray, 0);

	}
}