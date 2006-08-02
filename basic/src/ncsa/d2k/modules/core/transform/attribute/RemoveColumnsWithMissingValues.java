package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;

/**
 * <p>Overview: Remove any columns with missing values from the table.
 * <p>Detailed Description: Any columns in <i>Table</i> with missing values
 * will be removed from <i>Table</i>.
 * <p>Data Handling: This module will remove columns from <i>Table</i>.
 * No additional memory is needed.
 */
public class RemoveColumnsWithMissingValues extends DataPrepModule {

    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     *         the corresponding index.
     */
    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
        return in;
    }

    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     *         the corresponding index.
     */
    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
        return out;
    }

    /**
     * Returns a description of the input at the specified index.
     *
     * @param i Index of the input for which a description should be returned.
     * @return <code>String</code> describing the input at the specified index.
     */
    public String getInputInfo(int i) {
        return "A Table.";
    }

    /**
     * Returns the name of the input at the specified index.
     *
     * @param i Index of the input for which a name should be returned.
     * @return <code>String</code> containing the name of the input at the specified index.
     */
    public String getInputName(int i) {
        return "Table";
    }

    /**
     * Returns a description of the output at the specified index.
     *
     * @param i Index of the output for which a description should be returned.
     * @return <code>String</code> describing the output at the specified index.
     */
    public String getOutputInfo(int i) {
        return "The input table, with all columns that contain missing values removed.";
    }

    /**
     * Returns the name of the output at the specified index.
     *
     * @param i Index of the output for which a description should be returned.
     * @return <code>String</code> containing the name of the output at the specified index.
     */
    public String getOutputName(int i) {
        return "Table";
    }

    /**
     * Describes the purpose of the module.
     *
     * @return <code>String</code> describing the purpose of the module.
     */
    public String getModuleInfo() {
        String s = "<p>Overview: Remove any columns with missing values from the table.";
        s += "<p>Detailed Description: Any columns in <i>Table</i> with missing values ";
        s += "will be removed from <i>Table</i>.";
        s += "<p>Data Handling: This module will remove columns from <i>Table</i>.";
        s += "No additional memory is needed.";
        return s;
    }

    /**
     * Returns the name of the module that is appropriate for end-user consumption.
     *
     * @return The name of the module.
     */
    public String getModuleName() {
        return "Remove Columns With Missing Values";
    }

    /**
     * Performs the main work of the module.
     *
     * @throws Exception if a problem occurs while performing the work of the module
     */
    public void doit() throws Exception {
        MutableTable mt = (MutableTable) pullInput(0);

        List toRemove = new ArrayList();
        int numCols = mt.getNumColumns();
        int numRows = mt.getNumRows();

        for (int i = 0; i < numCols; i++) {
            for (int j = 0; j < numRows; j++) {
                if (mt.isValueMissing(j, i)) {
                    toRemove.add(new Integer(i));
                    break;
                }
            }
        }

        for (int i = 0; i < toRemove.size(); i++) {
            int idx = ((Integer) toRemove.get(i)).intValue();
            mt.removeColumn(idx - i);
        }

        pushOutput(mt, 0);

    }

}
