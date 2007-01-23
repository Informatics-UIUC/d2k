
package ncsa.d2k.modules.core.util;
import java.net.URL;
import ncsa.d2k.core.modules.ReentrantComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;


/**
 * InputFileName allows the user to input the name of a single file. The file
 * name is input in the properties editor.
 *
 * @author  unascribed
 * @version 1.0
 */
public class StringToURL extends ReentrantComputeModule {

   //~ Instance fields *********************************************************



   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module.
    *
    * @throws Exception Description of exception Exception.
    */
   public void doit()  {
     try{
       String str = (String) pullInput(0);

       if (str == null || str.length() == 0) {
         System.out.println(getAlias() +
                            ": The input string is either null or empty.");
         return;
       }

       URL url = new URL(str);

       pushOutput(url, 0);
     }catch(Exception e){
       e.printStackTrace();
     }
   }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {
     switch(i){
       case 0:
         return "String";
         default: return "no such input";
     }
   }

   public String getInputName(int i) {
    switch(i){
      case 0:
        return "String";
        default: return "no such input";
    }
  }


   /**
    * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
     String[] types = {"java.lang.String"};
     return types;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s += "This module is used to convert a String to a URL.</p>";

      return s;
   }

   /**
    * Returns the name of the module that is appropriate for end user consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Convert String To URL"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {
     switch (i) {
       case 0:
         return "The input String as a URL";
       default:
         return "no such output.";
     }
   }
   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) {  switch (i) {
       case 0:
         return "URL";
       default:
         return "no such output.";
     }
 }

   /**
    * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] out = { "java.net.URL" };

      return out;
   }

   /**
    * Return an array with information on the properties the user may update.
    *
    * @return The PropertyDescriptions for properties the user may update.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[0];

      return pds;
   }


} // end class Input1String
