// package ncsa.d2k.modules.core.datatype.table.newsparse_rowlist;
package ncsa.d2k.modules.core.datatype.table.sparse2;

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


    /**
     *
     */
    protected void doit() throws java.lang.Exception {
      try {
        this.pushOutput(new ncsa.d2k.modules.core.datatype.table.sparse2.SparseTableFactory(), 0);
      }
      catch (Exception ex) {
        ex.printStackTrace();
        System.out.println(ex.getMessage());
        System.out.println("ERROR: SparseTableFactoryInjector.doit()");
        throw ex;
      }
    }
}



