package ncsa.d2k.modules.core.datatype.parameter;



import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.core.modules.PropertyDescription;

public class TestParamSpaceImpl extends ncsa.d2k.core.modules.DataPrepModule {

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<p>      This is the <i>ParameterSpace</i> implementation that will be tested.    </p>";
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Space to Test";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.parameter.ParameterSpace"};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<p>      This is the minimum point in parameter space.    </p>";
			case 1: return "<p>      This is the max point in parameter space.    </p>";
			case 2: return "<p>      This is the default point in parameter space.    </p>";
			case 3: return "<p>      This is the space resulting from the addition of a new space.    </p>";
			case 4: return "<p>      This is the space that was added to the original.    </p>";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Minimum Point";
			case 1:
				return "Maximum Point";
			case 2:
				return "Default Point";
			case 3:
				return "Appended Space";
			case 4:
				return "Added Space";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint","ncsa.d2k.modules.core.datatype.parameter.ParameterPoint","ncsa.d2k.modules.core.datatype.parameter.ParameterPoint","ncsa.d2k.modules.core.datatype.parameter.ParameterSpace","ncsa.d2k.modules.core.datatype.parameter.ParameterSpace"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "<p>      Overview: This module will add a space to the given parameter space,       using the"+
			" methods of the parameter space passed in. It will pass out the       min point, the max point"+
			" and the default point, the space that was added       and the space resulting from the addition."+
			"    </p>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Test ParameterSpace";
	}

	private void printParameterSpace(ParameterSpace ps, String name) {
		System.out.println ("-------- "+name+" --------");
		System.out.println ("parameter name, min value, max value, default value, res, type");

		// get the subspace indices.
		int nsubs = ps.getNumSubspaces();
		int [] spacesizes = new int [nsubs];
		for (int i = 0 ; i < nsubs ; i++) spacesizes[i] = ps.getSubspaceNumParameters(i);

		int subspaceIndex = 0;
		int subspaceCount = spacesizes[subspaceIndex];
		for (int i = 0 ; i < ps.getNumParameters(); i++) {
			if (subspaceCount == 0) {
				subspaceIndex++;
				subspaceCount = spacesizes[subspaceIndex];
			}
			System.out.println(subspaceIndex+":"+ps.getName(i)+","+ps.getMinValue(i)+","+ps.getMaxValue(i)
			+","+ps.getDefaultValue(i)+","+ps.getResolution(i)+","+ps.getType(i));
			subspaceCount--;
		}
	}
	public void doit() {
		ParameterSpace input = (ParameterSpace) this.pullInput(0);
		this.printParameterSpace(input, "original space");
		final String names[] = {"lift", "pitch", "roll", "descent"};
		final double min[] = {2.0D, 0.0D, 100.0D, 0};
		final double max[] = {10.0D, 1.275D, 200.0D, 1};
		final double def[] = {7.2D, .565D, 150.0D, 1};
		final int res[] = {7, 8, 9, 1};
		final int types[] = {ColumnTypes.FLOAT, ColumnTypes.FLOAT, ColumnTypes.INTEGER, ColumnTypes.BOOLEAN};
		ParameterSpace psi =  (ParameterSpace) new ParameterSpaceImpl();
		psi.createFromData(names, min, max, def, res, types);
		this.printParameterSpace(psi, "space to add");
		ParameterSpace newSpace = input.joinSubspaces(input, psi);
		this.printParameterSpace(newSpace, "joined space");

		this.pushOutput(newSpace.getMinParameterPoint(), 0);
		this.pushOutput(newSpace.getMaxParameterPoint(), 1);
		this.pushOutput(newSpace.getDefaultParameterPoint(),2);
		this.pushOutput(newSpace, 3);
		this.pushOutput(psi, 4);
	}
}
