package ncsa.d2k.modules.core.vis;

import java.io.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
 * This module creates a two-dimensional scatter plot of <i>Table</i> data,
 * plotting any numeric column against itself or any other numeric column.
 */
public class ScatterPlot2D extends VisModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

   protected UserView createUserView() {
      return new ScatterPlotUserPane();
   }

   public String[] getFieldNameMapping() {
      return null;
   }

   /**
 * Returns a description of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a description should be returned.
 *
 * @return <code>String</code> describing the input at the specified index.
 */
public String getInputInfo(int index) {
      if (index == 0)
         return "A <i>Table</i> with data to be visualized.";
      return "NO SUCH INPUT";
   }

   /**
 * Returns the name of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the input at the specified index.
 */
public String getInputName(int index) {
      if (index == 0)
         return "Table";
      return "NO SUCH INPUT";
   }

   public String[] getInputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };
   }

   /**
 * Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module creates a two-dimensional scatter plot of ");
      sb.append("<i>Table</i> data, plotting any numeric column against ");
      sb.append("itself or any other numeric column.");
      sb.append("<P>Missing Values Handling: The module treats missing values " );
      sb.append("as regular ones.</P>");
      return sb.toString();
   }

   /**
* Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleName() {
      return "2D Scatter Plot";
   }

   /**
 * Returns a description of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a description should be returned.
 *
 * @return <code>String</code> describing the output at the specified index.
 */
public String getOutputInfo(int index) {
      return "NO SUCH OUTPUT";
   }

   /**
 * Returns the name of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the output at the specified index.
 */
public String getOutputName(int index) {
      return "NO SUCH OUTPUT";
   }

   public String[] getOutputTypes() {
      return null;
   }

////////////////////////////////////////////////////////////////////////////////
// properties                                                                 //
////////////////////////////////////////////////////////////////////////////////

   /**
 * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects for each property of the module.
 *
 * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects.
 */
public PropertyDescription[] getPropertiesDescriptions() {
      return new PropertyDescription[0];
   }

}
 /**
  * QA comments
  * 12-29-03
  * Vered started qa process.
  * added to module info documentation about missing values handling (as regular values)
  *
  *
  * 02-02-04:
  * bug 187 is not to be resolved in this release. it is transformed into a wish
  * to be handled in absic 5 release. (the bug was not reported for this modules,
  * but applies to it too).
  *
  * ready for basic 4.

 */