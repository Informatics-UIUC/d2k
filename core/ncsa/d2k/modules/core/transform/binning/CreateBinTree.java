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
	   return "ExampleTable containing the names of the input/output features"; 
       default : 
	   return "No such input"; 
       } 
   }

   public String getOutputInfo(int i) {
      return "BinTree structure without counts";
   }

   public String getInputName(int i) {
       switch (i) { 
       case 0 : 
	   return "Bin Transform"; 
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
       sb.append("<p>Detailed Description: "); 
       sb.append( "Given a BinTransform containing the definition of the bins, "); 
       sb.append( "an ExampleTable "); 
       sb.append( "that has the input/ output attribute labels and types, builds a BinTree "); 
       sb.append( "that can be later used to clasify data. " );
       sb.append( "<p> Scalability: a large enough number of attributes will result "); 
       sb.append( "in an OutOfMemory error. Use attribute selection to reduce the number.");
       return sb.toString(); 

   }

   public String getModuleName() {
      return "Create Bin Tree";
   }

   public void doit() {
      BinTransform bt = (BinTransform)pullInput(0);
      ExampleTable et = (ExampleTable)pullInput(1);

      BinTree tree = createBinTree(bt, et);
      pushOutput(tree, 0);
   }

   public static BinTree createBinTree(BinDescriptor[] bins, String[] cn, String[] an) {
      BinTree bt = new BinTree(cn, an);

      for(int i = 0; i < bins.length; i++) {
         BinDescriptor bd = bins[i];
         String attlabel = bd.label;
				 if(bd instanceof NumericBinDescriptor) {
            double max = ((NumericBinDescriptor)bd).max;
            double min = ((NumericBinDescriptor)bd).min;

            try {
               bt.addNumericBin(bd.label, bd.name, min, false,
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
							   bt.addStringBin(bd.label, bd.name, values);
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

}
// QA comments Anca:
// added input/output names, description, module info text
// module gives OutOfMemory error for large number of attributes
