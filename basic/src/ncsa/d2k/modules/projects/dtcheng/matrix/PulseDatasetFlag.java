package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class PulseDatasetFlag extends ComputeModule {
	
	public String getModuleName() {
		return "PulseDatasetName";
	}
	
	public String getModuleInfo() {
		return "This module is extremely specialized to help automate my map analysis.... true means real data, false means scenario";
	}
	
	public String getInputName(int i) {
		switch (i) {
		case 0:
			return "RegionNumber";
		case 1:
			return "SleepTime";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "RegionNumber";
		case 1:
			return "SleepTime";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String[] getInputTypes() {
		String[] types = { "java.lang.Integer", "java.lang.Long" };
		return types;
	}
	
	public String getOutputName(int i) {
		switch (i) {
		case 0:
			return "RealDataFlag";
		default:
			return "Error!  No such output.  ";
		}
	}
	
	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "RealDataFlag";
		default:
			return "Error!  No such output.  ";
		}
	}
	
	public String[] getOutputTypes() {
		String[] types = { "java.lang.Boolean", };
		return types;
	}
	
	public void doit() throws Exception {
//		int nRegions = 4;
		int nModels = 4;

//		int RegionNumber = ((Integer)this.pullInput(0)).intValue();
		((Integer)this.pullInput(0)).intValue();
			long sleepTime = ((Long)this.pullInput(1)).longValue();

		for (int modelIndex = 0; modelIndex < nModels; modelIndex++) {
			// for the "real" data
				this.pushOutput(new Boolean(true), 0);
				System.gc();
				Thread.sleep(sleepTime);
			// for the "scenario" data
				this.pushOutput(new Boolean(false), 0);
				System.gc();
				Thread.sleep(sleepTime);
		}
		
	}
}









