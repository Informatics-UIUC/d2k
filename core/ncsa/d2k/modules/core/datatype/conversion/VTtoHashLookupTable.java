package ncsa.d2k.modules.core.datatype.conversion;

import java.io.Serializable;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.HashLookupTable;

public class VTtoHashLookupTable extends DataPrepModule
   implements Serializable {

   public String getModuleInfo() {
      return "This module converts a Table into a HashLookupTable.";
   }

   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.modules.core.datatype.Table"};
      return i;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "A Table to be converted to a HashLookupTable.";
      else
         return "No such input exists.";
   }

   public String[] getOutputTypes() {
      String[] o = {"ncsa.d2k.modules.core.datatype.HashLookupTable"};
      return o;
   }

   public String getOutputInfo(int index) {
      if (index == 0)
         return "A HashLookupTable built from the Table input.";
      else
         return "No such output exists.";
   }

   public void doit() {

      Table v = (Table)pullInput(0);
      HashLookupTable t = new HashLookupTable();

      Object[] keys;
      int keys_l = v.getNumColumns() - 1;

      for (int i = 0; i < v.getNumRows(); i++) {

         keys = new Object[keys_l];

         for (int j = 0; j < keys_l; j++)
            keys[j] = v.getObject(i, j);

         t.put(keys, v.getObject(i, keys_l));

      }

      pushOutput(t, 0);

   }

}
