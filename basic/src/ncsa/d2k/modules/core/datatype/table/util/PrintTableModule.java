package ncsa.d2k.modules.core.datatype.table.util;

import ncsa.d2k.core.modules.OutputModule;
import ncsa.d2k.modules.core.datatype.table.Table;

public class PrintTableModule extends OutputModule {

	public void printTabular(Table tbl) {
		for (int j = 0; j < tbl.getNumColumns(); j++) {
			System.out.print(tbl.getColumnLabel(j) + ", ");
		}
		System.out.println();
		for (int i = 0; i < tbl.getNumRows(); i++) {
			for (int j = 0; j < tbl.getNumColumns(); j++) {
				System.out.print(tbl.getString(i, j) + ", ");
			}
			System.out.println();
		}
	}

	public void printByColumn(Table tbl) {
		for (int j = 0; j < tbl.getNumColumns(); j++) {
			System.out.print(tbl.getColumnLabel(j) + ":\t");
			for (int i = 0; i < tbl.getNumRows(); i++) {

				System.out.print(tbl.getString(i, j) + ", ");
			}
			System.out.println();
		}
	}

	@Override
	protected void doit() throws Exception {
		Table tbl = (Table) pullInput(0);
		printByColumn(tbl);

	}

	public String getInputName(int index) {
		switch (index) {
		case 0:
			return "Table";
		default:
			return "No such input";
		}

	}

	public String getOutputName(int index) {
		switch (index) {
		case 0:
			return "Table";
		default:
			return "No such input";
		}

	}

	public String getInputInfo(int index) {
		switch (index) {
		case 0:
			return "Table to be printed to stdout";
		default:
			return "No such input";
		}

	}

	public String[] getInputTypes() {
		String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };
		return types;
	}

	public String getModuleName() {
		return "Print Table";

	}

	public String getModuleInfo() {
		return "This module prints the input table to stdout in a tabular fashion";

	}

	public String getOutputInfo(int index) {
		switch (index) {
		case 0:
			return "The input Table";
		default:
			return "No such output";
		}

	}

	public String[] getOutputTypes() {
		String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };
		return types;
	}

}
