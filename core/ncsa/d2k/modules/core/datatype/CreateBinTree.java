package ncsa.d2k.modules.core.datatype;

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
         "ncsa.d2k.modules.core.datatype.BinTree"
      };
   }

   public String getInputInfo(int i) {
      return "";
   }

   public String getOutputInfo(int i) {
      return "";
   }

   public String getInputName(int i) {
      return "";
   }

   public String getOutputName(int i) {
      return "";
   }

   public String getModuleInfo() {
      return "";
   }

   public String getModuleName() {
      return "";
   }

   public void doit() {
      BinTransform bt = (BinTransform)pullInput(0);
      ExampleTable et = (ExampleTable)pullInput(0);

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