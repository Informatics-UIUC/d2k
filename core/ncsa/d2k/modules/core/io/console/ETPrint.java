package ncsa.d2k.modules.core.io.console;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class ETPrint extends ncsa.d2k.infrastructure.modules.DataPrepModule{

	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "";
			default: return "No such input";
		}

	}

	public String[] getInputTypes () {
		String [] types =  {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getOutputInfo (int index) {
		switch (index) {
			default: return "No such output";
		}
	}

	public String[] getOutputTypes () {
		String [] types =  { };
		return types;
	}

	public String getModuleInfo () {
		return "";
	}
	public void doit () throws Exception {

		ExampleTable et=(ExampleTable)pullInput(0);

		System.out.println("Example Table Properties");

		System.out.println("Input Columns:");

		if(et.getInputFeatures()!=null){
			int[] ins=et.getInputFeatures();
			for(int i=0; i<ins.length; i++){
				System.out.println("   "+i+" - "+ins[i]);
			}
		}else{
			System.out.println("    null");
		}

		System.out.println("Output Columns:");
		if(et.getOutputFeatures()!=null){
			int[] outs=et.getOutputFeatures();
			for(int i=0; i<outs.length; i++){
				System.out.println("   "+i+" - "+outs[i]);
			}
		}else{
			System.out.println("    null");
		}

		/*System.out.println("Training Examples");
		for(int i=0; i<et.getNumTrainExamples(); i++){
				System.out.println("   "+i+" - "+et.getTrainInputDouble(i, 0));
		}
		System.out.println("Testing Examples");

		for(int i=0; i<et.getNumTestExamples(); i++){

				System.out.println("   "+i+" - "+et.getTestInputDouble(i, 0));
		}
		*/

	}
}
