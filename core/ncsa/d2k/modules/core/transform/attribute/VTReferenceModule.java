package ncsa.d2k.modules.core.transform.attribute;

import java.io.*;
import java.util.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.modules.core.vis.*;
import ncsa.d2k.modules.core.datatype.*; // eliminate this when HLT gets added to d2k

/**
 * VTReferenceModule
 * @author gpape
 */
public class VTReferenceModule extends DataPrepModule
   implements Serializable, HasProperties {

   public String getModuleInfo() {
      return "Converts one of its two input VerticalTables into a " +
             "HashLookupTable that references the other with VTReferences.";
   }

   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.util.datatype.VerticalTable",
         "ncsa.d2k.util.datatype.VerticalTable"};
      return i;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "The VerticalTable to be referenced to (unchanged).";
      else if (index == 1)
         return "The VerticalTable to be converted to a HashLookupTable";
      else
         return "VTReferenceModule has no such input.";
   }

   public String[] getOutputTypes() {
      String[] o = {"ncsa.d2k.modules.core.datatype.HashLookupTable",
         "ncsa.d2k.util.datatype.VerticalTable"};
      return o;
   }

   public String getOutputInfo(int index) {
      if (index == 0)
         return "A HashLookupTable referencing the output VerticalTable.";
      else if (index == 1)
         return "The first VerticalTable input, unaltered.";
      else
         return "VTReferenceModule has no such output.";
   }

   public String referenceLabel;

   public String getreferenceLabel() {
      return referenceLabel;
   }

   public void setreferenceLabel(String s) {
      referenceLabel = s;
   }

   public void doit() {

      VerticalTable vt_uni = (VerticalTable)pullInput(0); // unique data
      VerticalTable vt_red = (VerticalTable)pullInput(1); // redundant data

      HashLookupTable hlt = new HashLookupTable();

      // copy column labels of redundant data to HLT
      String[] labels = new String[vt_red.getNumColumns() + 1];
      for (int count = 0; count < vt_red.getNumColumns(); count++) {
         labels[count] = vt_red.getColumnLabel(count);
      }
      labels[labels.length - 1] = referenceLabel;
      hlt.setLabels(labels);

      // generate HashMap of non-redundant column labels
      HashMap map = new HashMap(vt_uni.getNumColumns(), 1.0f);
      for (int count = 0; count < vt_uni.getNumColumns(); count++) {
         map.put(vt_uni.getColumnLabel(count), new Integer(count));
      }

      // add redundant data to HLT
      Object[] keys = new Object[vt_red.getNumColumns() + 1];
      VTReference vtref;
      for (int count = 0; count < vt_red.getNumRows(); count++) {

         for (int i = 0; i < vt_red.getNumColumns(); i++)
            keys[i] = vt_red.getObject(count, i);

         for (int j = 0; j < vt_uni.getNumColumns(); j++) {
            keys[keys.length - 1] = (Object)vt_uni.getColumnLabel(j);

            vtref = new VTReference(count,
               ((Integer)map.get(vt_uni.getColumnLabel(j))).intValue());

            hlt.put(keys, (Object)vtref);

         }

      }

      vt_red = null; // garbage collection

      pushOutput(hlt,0);
      pushOutput(vt_uni,1);

   }

}
