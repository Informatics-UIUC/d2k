package ncsa.d2k.modules.core.transform.binning;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;


/**
 *
 */
public class CreateBinTree extends DataPrepModule {

   public String[] getInputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable"
      };
   }

   public String[] getOutputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.transform.binning.BinTree"
      };
   }

   public String getInputInfo(int i) {
       switch (i) { 
       case 0 : 
	   return "Binning Transformation containing the bin definitions"; 
       case 1 : 
	   return "Example Table containing the names of the input/output features"; 
       default : 
	   return "No such input"; 
       } 
   }

   public String getOutputInfo(int i) {
      return "Bin Tree structure holding counts";
   }

   public String getInputName(int i) {
       switch (i) { 
       case 0 : 
	   return "Binning Transformation"; 
       case 1 : 
	   return "Example Table"; 
       default : 
	   return "No such input"; 
       } 
   }

   public String getOutputName(int i) {
      return "Bin Tree";
   }

   public String getModuleInfo() {
       StringBuffer sb = new StringBuffer("<p>Overview: "); 
       sb.append("Creates an empty BinTree."); 
       sb.append("</p><p>Detailed Description: "); 
       sb.append( "Given a Binning Transformation containing the definition of the bins, "); 
       sb.append( "and an Example Table that has the input/ output attribute labels and types, "); 
       sb.append( "this module builds a Bin Tree that can be later used to clasify data. ");
       sb.append( "</p><p>A Bin Tree holds information about the number of examples that fall into each bin ");
       sb.append( "for each class. The Bin Tree can use only one output "); 
       sb.append( "feature as a class. If more are selected in the Example Table, only the first one will be used." );
       sb.append( "</p><p> Scalability: a large enough number of features will result "); 
       sb.append( "in an OutOfMemory error. Use feature selection to reduce the number of features.</p>");
       return sb.toString(); 

   }

   public String getModuleName() {
      return "Create Bin Tree";
   }

   public void doit() throws Exception {
      BinTransform bt = (BinTransform)pullInput(0);
      ExampleTable et = (ExampleTable)pullInput(1);

      BinTree tree = createBinTree(bt, et);
      int [] ins = et.getInputFeatures();
      int [] out = et.getOutputFeatures();

      if ((ins == null) || (ins.length == 0))
	  throw new Exception("Input features are missing. Please select the input features.");
      
      if (out == null || out.length == 0)
	  throw new Exception("Output feature is missing. Please select an output feature.");

      // we only support one out variable..
      int classColumn = out[0];

      if (et.isColumnScalar(classColumn)) 
	  throw new Exception("Output feature should be nominal. Please transform it.");

      int numRows = et.getNumRows();
      long startTime = System.currentTimeMillis();
      for(int i = 0; i < ins.length; i++) {
	  // numeric columns
	  if(et.isColumnScalar(ins[i])) {
	      for(int j = 0; j < numRows; j++) {
		  tree.classify(et.getString(j, classColumn),
				et.getColumnLabel(ins[i]), et.getDouble(j, ins[i]));
	      }
	      
	  }
	  
	  // everything else is treated as textual columns
	  else {
	      for(int j = 0; j < numRows; j++)
		  tree.classify(et.getString(j, classColumn),
				et.getColumnLabel(ins[i]), et.getString(j, ins[i]));
	  }
      }
      
      long endTime = System.currentTimeMillis();
      if (debug) System.out.println ( "time in msec " + (endTime-startTime));
      if (debug)tree.printAll();
      pushOutput(tree, 0);
   
}


   public static BinTree createBinTree(BinDescriptor[] bins, String[] cn, String[] an) {
       
       // converting the attribute labels to lower case
       // done for compatibility with BinTrees produced by SQL
       /*   String lowerCaseAn [] = new String[an.length];
       for (int i =0; i < an.length ; i++)
	   lowerCaseAn[i] = an[i].toLowerCase();
       */

      BinTree bt = new BinTree(cn, an);

      //System.out.println("bins.length " + bins.length);
      for(int i = 0; i < bins.length; i++) {
         BinDescriptor bd = bins[i];
         String attLabel = bd.label;//.toLowerCase();
         //System.out.println("bin label " +attLabel + " " + bd.name);
	 if(bd instanceof NumericBinDescriptor) {
	     double max = ((NumericBinDescriptor)bd).max;
	     double min = ((NumericBinDescriptor)bd).min;
	     
	     try {
		 bt.addNumericBin(attLabel, bd.name, min, false,
				  max, true);
	     }
	     catch(Exception e) {
	     }
         }
         else {
	     HashSet vals = ((TextualBinDescriptor)bd).vals;
	     String[] values = new String[vals.size()];
	     Iterator ii = vals.iterator();
	     int idx = 0;
	     while(ii.hasNext()) {
		 values[idx] = (String)ii.next();
		 idx++;
	     }
	     
	     try {
		 bt.addStringBin(attLabel, bd.name, values);
            }
	     catch(Exception e) {
	     }
         }
	 
      }
      return bt;

   }
    

    public static BinTree createBinTree(BinTransform bt, ExampleTable et) {
	int[] outputs = et.getOutputFeatures();
	int[] inputs = et.getInputFeatures();
	
	HashMap used = new HashMap();
	
	String[] cn = TableUtilities.uniqueValues(et, outputs[0]);
	String[] an = new String[inputs.length];
	for(int i = 0; i < an.length; i++)
	    an[i] = et.getColumnLabel(inputs[i]);
	return createBinTree(bt.getBinDescriptors(), cn, an);
    }
    

    boolean debug; 
 
    public boolean getDebug() { 
	return debug; 
    } 
 
    public void setDebug(boolean deb) { 
	debug = deb; 
    } 

    public PropertyDescription[] getPropertiesDescriptions(){ 
	PropertyDescription[] pd = new PropertyDescription[1] ; 
	pd[0] = new PropertyDescription("debug", "Verbose Mode", 
					"This property controls the module's output to the stdout. " + 
					"If set to true the created BinTree will be printed. ");
					
	return pd; 
    } 




}
// QA comments Anca:
// added input/output names, description, module info text
// module gives OutOfMemory error for large number of attributes
// merged Binning module functionality in this module
// added debug variable
