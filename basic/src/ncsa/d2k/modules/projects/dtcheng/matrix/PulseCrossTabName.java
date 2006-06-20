package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

public class PulseCrossTabName extends ComputeModule {
	
	public String getModuleName() {
		return "PulseCrossTabName";
	}
	
	public String getModuleInfo() {
		return "This module is extremely specialized to help automate my map analysis....";
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
			return "FullDatasetName";
		default:
			return "Error!  No such output.  ";
		}
	}
	
	public String getOutputInfo(int i) {
		switch (i) {
		case 0:
			return "FullDatasetName";
		default:
			return "Error!  No such output.  ";
		}
	}
	
	public String[] getOutputTypes() {
		String[] types = { "java.lang.String", };
		return types;
	}
	
	public void doit() throws Exception {
		int RegionNumber = ((Integer)this.pullInput(0)).intValue();
		long sleepTime = ((Long)this.pullInput(1)).longValue();

//		int nRegions = 4;
		int nModels = 4;

		for (int modelIndex = 0; modelIndex < nModels; modelIndex++) {
				this.pushOutput(new String("c:\\temp\\regions_scratch\\d2k_out\\Xtab_" + RegionNumber + "_" + modelIndex + ".tex"), 0);
				System.gc();
				Thread.sleep(sleepTime);
				this.pushOutput(new String("c:\\temp\\regions_scratch\\d2k_out\\scenario_Xtab_" + RegionNumber + "_" + modelIndex + ".tex"), 0);
				System.gc();
				Thread.sleep(sleepTime);
		}
		
	}
}









