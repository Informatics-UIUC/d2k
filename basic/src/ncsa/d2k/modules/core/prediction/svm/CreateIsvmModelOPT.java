package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;

/**
 * Produces an initialized IsvmModel. This modules generates a new IsvmModel and
 * initializes it according to the properties, as they are set by the input
 * parameter point. see CreateIsvmModel or IsvmParamSpaceGenerator for
 * information about the properties.
 * 
 * @author vered goren
 */
public class CreateIsvmModelOPT extends CreateIsvmModel {

    /**
     * Returns a description of the input at the specified index.
     *
     * @param index Index of the input for which a description should be
     *              returned.
     * @return <code>String</code> describing the input at the specified index.
     */
    public String getInputInfo(int index) {
        switch (index) {
            case 0:
                return "Controll point in the parameter space";
            default:
                return "No such input";
        }
	}

    /**
     * Returns the name of the input at the specified index.
     *
     * @param index Index of the input for which a name should be returned.
     * @return <code>String</code> containing the name of the input at the
     *         specified index.
     */
    public String getInputName(int index) {
        switch (index) {
            case 0:
                return "Parameter Point";
            default:
                return "No such input";
        }
	}

    /**
     * Returns an array of <code>String</code> objects each containing the fully
     * qualified Java data type of the input at the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully
     *         qualified Java data type of the input at the corresponding index.
     */
    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
        return in;
    }

    /**
     * Describes the purpose of the module.
     *
     * @return <code>String</code> describing the purpose of the module.
     */
    public String getModuleInfo() {
        return "<p>Overview: Produces an initialized IsvmModel.</p>"
                + "<p>Detailed Description: This module generates a new IsvmModel and initializes it "
                + "according to the properties, as they are set by the input parameter point."
                + "See CreateIsvmModel or IsvmParamSpaceGenerator for information about the "
                + "properties.</p>";

    }

    /**
     * Returns the name of the module that is appropriate for end-user
     * consumption.
     *
     * @return The name of the module.
     */
    public String getModuleName() {
        return "Create Isvm Model Optimized";
    }


    /**
     * Called by the D2K Infrastructure before the itinerary begins to execute.
     */
    public void beginExecution() {
    }

    /**
     * Called by the D2K Infrastructure after the itinerary completes execution.
     */
    public void endExecution() {
        super.endExecution();
    }

    /**
     * Performs the main work of the module.
     *
     * @throws Exception if a problem occurs while performing the work of the
     *                   module
     */
    protected void doit() throws Exception {
        ParameterPoint pp = (ParameterPoint) pullInput(0);

        IsvmModel d2k_model = new IsvmModel(super.numAttributes);
        d2k_model.setNu(pp.getValue(IsvmParamSpaceGenerator.NU));

        d2k_model.init();
        pushOutput(d2k_model, 0);

    }

    /**
     * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
     * objects for each property of the module.
     *
     * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
     *         objects.
     */
    public PropertyDescription[] getPropertiesDescriptions() {
        PropertyDescription[] pds = new PropertyDescription[1];
        pds[0] = super.getPropertiesDescriptions()[1];
        return pds;
    }

}
