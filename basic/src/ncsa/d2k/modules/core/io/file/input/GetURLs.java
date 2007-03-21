package  ncsa.d2k.modules.core.io.file.input;


//==============
// Java Imports
//==============
import  java.io.*;
import  java.util.*;
//===============
// Other Imports
//===============
import  ncsa.d2k.core.modules.*;
import  ncsa.d2k.modules.t2k.util.*;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;


/**
 * put your documentation comment here
 */
public class GetURLs extends InputModule
        implements CommonPropertyLabels {
    //==============
    // Data Members
    //==============
    //Options
    private int _docsProcessed = 0;
    private int _urlsPushedCount = 0;
    private long _start = 0;
  //  private ArrayList _names = null;
   // private HashSet _extensions = null;
  //  private String _dirName = null;




    private boolean _verbose = false;

    /**
     * put your documentation comment here
     * @return
     */
    public boolean getVerbose () {
        return  _verbose;
    }

    /**
     * put your documentation comment here
     * @param b
     */
    public void setVerbose (boolean b) {
        _verbose = b;
    }
    private boolean _recurseSubDirectories = false;

    /**
     * put your documentation comment here
     * @return
     */
    public boolean getRecurseSubDirectories () {
        return  _recurseSubDirectories;
    }

    /**
     * put your documentation comment here
     * @param b
     */
    public void setRecurseSubDirectories (boolean b) {
        _recurseSubDirectories = b;
    }
    private String _ext = "txt";

    /**
     * put your documentation comment here
     * @return
     */
    public String getExtensions () {
        return  _ext;
    }

    protected Set extenssions = new HashSet();

    /**
     * put your documentation comment here
     * @param dn
     */
    public void setExtensions (String dn) {
        _ext = dn;
        String[] temp = _ext.split(",", 0);
        extenssions = new HashSet();
        for(int i=0; i<temp.length; i++){
          extenssions.add(temp[i].trim());
        }
    }

    //================
    // Constructor(s)
    //================
    public GetURLs () {
    }

    //================
    // Public Methods
    //================
    //========================
    // D2K Abstract Overrides

    /**
     * Return the name of this module.
     * @return the dislay name for this module.
     */
    public String getModuleName() {
      return "Get URLs";
    }


    /**
     * Return the name of a specific input.
     * @param parm1 The index of the input.
     * @return The name of the input.
     */
    public String getInputName (int parm1) {
      switch(parm1){
        case 0: return "Data Object Proxy";
        default:
            return  "No such input.";
        }
    }

    /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
     */
    public String getInputInfo (int parm1) {
      switch(parm1){
        case 0: return "Data Object Proxy to retrieve children URL from the URL it is pointing to.";
     default:
         return  "No such input.";
     }

    }

    /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
     */
    public String[] getInputTypes () {
        String[] in =  {
            "ncsa.d2k.modules.core.io.proxy.DataObjectProxy"
        };
        return  in;
    }


    /**
     Return information about the module.
     @return A detailed description of the module.
     */
    public String getModuleInfo () {
        String s = "<p>Overview: ";
        s += "This module reads URLs residing underneith the URL that is pointed to by the input Data Object Proxy.";
        s += "</p>";
        return  s;
    }

    //======================
    // Property Information
    //======================
    /**
     * Return a list of the property descriptions.  The order of descriptions
     * matches the order of presentation of the properties to the user.
     * @return a list of the property descriptions.
     */
    public PropertyDescription[] getPropertiesDescriptions () {
        PropertyDescription[] pds = new PropertyDescription[4];
        pds[0] = new PropertyDescription("depth", "Depth",
                "Depth of recursing when retrieving URLs.");
        pds[1] = new PropertyDescription("extensions", "File Extensions to Filter On",
                "A comma delimited list of file extensions that will be used to fiter those files chosen for output.");
        pds[2] = new PropertyDescription("verbose", VERBOSE_prop_label, VERBOSE_prop_label);
                pds[3] = new PropertyDescription("debug", this.DEBUG_prop_label, this.DEBUG_prop_desc);

        return  pds;
    }

    /**
     * Return the name of a specific output.
     * @param parm1 The index of the output.
     * @return The name of the output.
     */
    public String getOutputName(int parm1) {

      switch (parm1) {
        case OUT_URL:
          return "URLs";
        case OUT_COUNT:
          return "Integer  Count";

      default:
        return "No such output";
    }
    }

    /**
     * put your documentation comment here
     * @param parm1
     * @return
     */
    public String getOutputInfo(int parm1) {
      switch (parm1) {
        case OUT_URL:
          return
              "Children URLs of the parent URL the input Data Object Proxy it pointing to.";
        case OUT_COUNT:
          return "Count of total URLs as Integer";
        default:
          return "No such output";
      }
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getOutputTypes () {
        String[] out =  new String[2];
        out[OUT_URL] =             "java.net.URL";
       out[OUT_COUNT] =  "java.lang.Integer";

        return  out;
    }

    /**
     * put your documentation comment here
     */
    public void beginExecution () {
        urlsVector = null;
        _docsProcessed = 0;
        _urlsPushedCount = 0;
        _start = System.currentTimeMillis();
   //     extenssions = new HashSet();
    }

    /**
     * put your documentation comment here
     */
    public void endExecution () {
        super.endExecution();
        long end = System.currentTimeMillis();
        if (getVerbose()) {
            System.out.println(this.getAlias() + ": END EXEC -- URLs Processed: "
                    + _docsProcessed + " and URLs pushed out: " + this._urlsPushedCount +
                   " in " + (end - _start)/1000 + " seconds\n");
        }
        _docsProcessed = 0;
        _urlsPushedCount = 0;
//        extenssions = null;
        urlsVector = null;

    }

    /**
     * put your documentation comment here
     * @return
     */
    public boolean isReady () {
        if ((super.isReady()) || (this.urlsVector != null)) {
            return  true;
        }
        else {
            return  false;
        }
    }


protected Vector urlsVector= null;
    public static final int OUT_URL = 0;
    public static final int OUT_COUNT = 1;
    /**
     * put your documentation comment here
     * @exception java.lang.Exception
     */
    protected void doit () throws java.lang.Exception {

      if(urlsVector == null && this.getInputPipeSize(0)>0){

       DataObjectProxy dop = (DataObjectProxy) pullInput(0);
       urlsVector = dop.getChildrenURLs(depth);


     }


      if(urlsVector != null && urlsVector.size() > 0){



         Object obj =urlsVector.remove(urlsVector.size()-1);

         int index = obj.toString().lastIndexOf(".");
         if(index != -1){
           String tmp = obj.toString().substring(index );
           _docsProcessed ++;
           if(this.extenssions.contains(tmp)){
             _urlsPushedCount++;
             pushOutput(obj, OUT_URL);
             if(this.debug){
               System.out.println(this.getAlias() + ": url = " + obj.toString());
             }
           }
         }
      }else urlsVector = null;
    }





    private boolean debug;
      public boolean getDebug(){return debug;}
      public void setDebug(boolean bl){debug = bl;}
      private int depth;
      public void setDepth(int d){
        if(d!=0 && d!= 1){
          d=DataObjectProxy.DEPTH_INFINITY;
        }
        depth = d;
      }
      public int getDepth (){return depth;}






}



