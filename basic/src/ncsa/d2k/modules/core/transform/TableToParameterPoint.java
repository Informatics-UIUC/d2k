package ncsa.d2k.modules.core.transform;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;

/**
 * <p>Overview: Converts a table to a parameter point. The inputs are assumed 
 * to be the coordinates in parameter space.</p>
 */
public class TableToParameterPoint extends DataPrepModule{

    /**
     * Returns the name of the module that is appropriate for end-user consumption.
     *
     * @return The name of the module.
     */
    public String getModuleName() {
        return "Table to Parameter Point";
    }

    /**
     * Describes the purpose of the module.
     *
     * @return <code>String</code> describing the purpose of the module.
     */
    public String getModuleInfo() {
        return "<p>Overview: Converts a table to a parameter point. The inputs "+
                "are assumed to be the coordinates in parameter space.</p>";
    }

    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     *         the corresponding index.
     */
    public String [] getInputTypes() {
        return new String[]{"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
	}

    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     *         the corresponding index.
     */
    public String [] getOutputTypes() {
        return new String[]{"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    }

    /**
     * Returns a description of the input at the specified index.
     *
     * @param i Index of the input for which a description should be returned.
     * @return <code>String</code> describing the input at the specified index.
     */
    public String getInputInfo(int i) {
        switch (i) {
            case 0:
                return "Input table containing the paramter point.";
            default:
                return "No such input";
        }
    }

    /**
     * Returns the name of the input at the specified index.
     *
     * @param i Index of the input for which a name should be returned.
     * @return <code>String</code> containing the name of the input at the specified index.
     */
    public String getInputName(int i) {
        switch (i) {
            case 0:
                return "Parameter Point Table";
            default:
                return "No such input";
        }
    }

    /**
     * Returns a description of the output at the specified index.
     *
     * @param i Index of the output for which a description should be returned.
     * @return <code>String</code> describing the output at the specified index.
     */
    public String getOutputInfo(int i) {
        switch (i) {
            case 0:
                return "The parameter point found in the table.";
            default:
                return "No such output";
        }
	}

    /**
     * Returns the name of the output at the specified index.
     *
     * @param i Index of the output for which a description should be returned.
     * @return <code>String</code> containing the name of the output at the specified index.
     */
    public String getOutputName(int i) {
        switch (i) {
            case 0:
                return "Paramter Point";
            default:
                return "No such output";
        }
	}

    /**
     * Performs the main work of the module.
     *
     * @throws Exception if a problem occurs while performing the work of the module
     */
    public void doit() throws Exception {
        ExampleTable et = (ExampleTable) this.pullInput(0);
        //ANCA replaced obsolete method
        //int numInputs = et.getNumInputs(0);
        int numInputs = et.getNumInputFeatures();
        double [] parameters = new double [numInputs];
        String [] labels = new String [numInputs];
        for (int i = 0; i < numInputs; i++) {
            parameters[i] = et.getInputDouble(0, i);
            labels[i] = et.getInputName(i);
        }
        ParameterPoint ppi = ParameterPointImpl.getParameterPoint(labels, parameters);
        this.pushOutput(ppi, 0);
    }
}
