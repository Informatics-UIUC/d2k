package ncsa.d2k.modules.core.datatype.table.sparse;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;

/**
 * put your documentation comment here
 */
public class SparseTableFactoryInjector extends InputModule {

    static final long serialVersionUID = 1L;

    //================
    // Constructor(s)
    //================
    public SparseTableFactoryInjector() {
    }

    //================
    // Public Methods
    //================
    //========================
    // D2K Abstract Overrides
    public String getInputInfo (int parm1) {
            return  "";

    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getInputTypes () {
        String[] in =  null;
        return  in;
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String getModuleInfo () {
        return  "";
    }

    /**
     * put your documentation comment here
     * @param parm1
     * @return
     */
    public String getOutputInfo (int parm1) {
        return  "ncsa.d2k.modules.core.datatype.table.TableFactory";
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getOutputTypes () {
        String[] out =  {
            "ncsa.d2k.modules.core.datatype.table.TableFactory"
        };
        return  out;
    }

    public String getInputName(int i) {
      return null;
    }

    public String getOutputName(int i) {
      if (i == 0) {
        return "Table Factory";
      }
      return null;
    }

    /**
     *
     */
    protected void doit() throws java.lang.Exception {
      try {
        this.pushOutput(new ncsa.d2k.modules.core.datatype.table.sparse.SparseTableFactory(), 0);
      }
      catch (Exception ex) {
        ex.printStackTrace();
        System.out.println(ex.getMessage());
        System.out.println("ERROR: SparseTableFactoryInjector.doit()");
        throw ex;
      }
    }
}



