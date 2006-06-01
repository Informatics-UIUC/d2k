package ncsa.d2k.modules.core.control;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;

/**
 * Created by IntelliJ IDEA.
 * User: clutter
 * Date: May 5, 2006
 * Time: 1:42:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class Branch extends DataPrepModule {
    private boolean useFirstOutput = true;
    public void setUseFirstOutput(boolean b) {
        useFirstOutput = b;
    }
    public boolean getUseFirstOutput() {
        return useFirstOutput;
    }

    /**
     * put your documentation comment here
     * @return
     */
    public PropertyDescription[] getPropertiesDescriptions () {
        PropertyDescription[] desc = new PropertyDescription[1];
        desc[0] = new PropertyDescription("useFirstOutput",
                "Use the first output",
                "Set this to true to use the first output.  Set this to false "+
                "to use the second output.");
        return  desc;
    }

    public String[] getInputTypes() {
        return new String[] {
            "java.lang.Object"
        };
    }

    public String[] getOutputTypes() {
        return new String[] {
            "java.lang.Object",
            "java.lang.Object"
        } ;
    }

    public String getInputInfo(int i) {
        return "Object";
    }

    public String getOutputInfo(int i) {
        return "Object";
    }

    public String getInputName(int i) {
      return "Object";
    }

    public String getOutputName(int i) {
      return "Object";
    }

    public String getModuleInfo() {
        return "Simple branch.  Push data out the first output pipe if <i>useFirstOutput</i> is"+
                " true; push data out the second output pipe if <i>useFirstOutput</i> is "+
                "false.";
    }

    public void doit() throws Exception {
        Object o = pullInput(0);

        if(useFirstOutput)
            pushOutput(o, 0);
        else
            pushOutput(o, 1);
    }
}
