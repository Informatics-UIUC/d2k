package ncsa.d2k.modules.core.transform;

import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Apply a Transformation to a MutableTable.
 */
public class ApplyTransformation extends DataPrepModule {

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.datatype.table.Transformation",
			"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return in;
	}

	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.datatype.table.Transformation",
			"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return out;
	}

	public String getInputInfo(int i) {
          switch(i){
            case 0: return "Trasnformation to apply on  the input Table.";
            case 1: return "MutableTable to apply the Transformation on.";
            default: return "no such input";
          }
	}

	public String getInputName(int i) {
          switch(i){
            case 0: return "Trasnformation";
            case 1: return "Table";
            default: return "no such input";
          }
	}

	public String getOutputInfo(int i) {
        switch(i){
            case 0: return "the Trasnformation which was applied to the input Table.";
            case 1: return "the input Table after the Transformation";
            default: return "no such input";
          }
	}

	public String getOutputName(int i) {
          switch(i){
            case 0: return "Trasnformation";
            case 1: return "Table";
            default: return "no such input";
          }
	}


        public String getModuleName() {
          return "Apply Tranform";
        }
	public String getModuleInfo() {
		return "This module applies a Transformation to a Table." +
                "<P><B>Detailed Description:</b><br>" +
                "The module applies a Transformation to a MutableTable and outputs " +
                "the table transformed and the transformation as well." +
                "<P><B>Data Handling:</b><br>This modules changes the data in the inputs Table</P>";
	}

	public void doit() throws Exception {
		Transformation t = (Transformation)pullInput(0);
		MutableTable mt = (MutableTable)pullInput(1);

		boolean ok = t.transform(mt);
		if(!ok)
			throw new Exception("Transformation failed.");

                //vered - debug
//                System.out.println( ( (mt.getTransformations().get(0)) == null ? "no " : "there are " + "transformations" ));

		pushOutput(t, 0);
		pushOutput(mt, 1);
	}
}

/**
 * QA comments
 * 28-2-03 through this module was not yet handed off to the qa, due to the fact
 *         that the other tranformation creators modules used it in itineraries
 *         i took the liberty to add names and info to inputs and outputs.
 *         vered.
 */