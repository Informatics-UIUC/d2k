package ncsa.d2k.modules.core.datatype.conversion;


import java.io.Serializable;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.HashLookupTable;

public class VTtoHashLookupTable extends DataPrepModule
    {

   public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module converts a Table into a HashLookupTable.  </body></html>";
	}

   public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.Table"};
		return types;
	}

   public String getInputInfo(int index) {
		switch (index) {
			case 0: return "A Table to be converted to a HashLookupTable.";
			default: return "No such input";
		}
	}

   public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.HashLookupTable"};
		return types;
	}

   public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "A HashLookupTable built from the Table input.";
			default: return "No such output";
		}
	}

   public void doit() {

      Table v = (Table)pullInput(0);
      HashLookupTable t = new HashLookupTable();

      Object[] keys;
      int keys_l = v.getNumColumns() - 1;

      for (int i = 0; i < v.getNumRows(); i++) {

         keys = new Object[keys_l];

         for (int j = 0; j < keys_l; j++)
            keys[j] = v.getObject(i, j);

         t.put(keys, v.getObject(i, keys_l));

      }

      pushOutput(t, 0);

   }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "VTtoHashLookupTable";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
