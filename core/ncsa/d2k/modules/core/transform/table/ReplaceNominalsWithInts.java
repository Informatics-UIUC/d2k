package ncsa.d2k.modules.core.transform.table;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.core.datatype.table.transformations.*;

public class ReplaceNominalsWithInts extends ComputeModule {

   public String getModuleInfo() {
      return "Prepares a Transformation to replaces nominal values with integers. " +
      "</P><P><B>Detailed Description:</b><br> This module receives as an input a " +
      "MutableTable. The module then creates a Transformation according to which " +
      "for each nominal column in the table, the nominal values will be replaced with " +
      "unique integers. The modules then outputs this Transformation.</P>"+
      "<P><B>Data Handling:</b><br>This module does not change the data of its input</P>";

   }

   public String getModuleName(){
    return "Replaces Nominals with Ints";
   }
   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
      return i;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "A MutableTable with nominal columns.";
      else
         return "no such input.";
   }

   public String getInputName(int index){
    if (index == 0) return "Table";
    else return "no such input";
   }


   public String getOutputName(int index){
    if (index == 0) return "Transformation";
    else return "no such output";
   }

   public String[] getOutputTypes() {

      String[] o = {"ncsa.d2k.modules.core.datatype.table.Transformation"};
      return o;
   }

   public String getOutputInfo(int index) {
      if (index == 0)

        return "The Transformation for replacing nominals with integers";
      else
         return "no such output.";
   }


   public void doit() {

      MutableTable mt = (MutableTable)pullInput(0);


	  ReplaceNominalValuesWithIntegersTransform it =
	  	new ReplaceNominalValuesWithIntegersTransform(mt);



      pushOutput(it, 0);

   }



}


/**
 * QA comments:
 * 2-27-03: vered started qa process. cleaned the dead code. waiting for greg response
 *          whether this module should change the table or output a transformation.
 */