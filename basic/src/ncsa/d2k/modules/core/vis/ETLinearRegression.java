package ncsa.d2k.modules.core.vis;


import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.modules.core.vis.widgets.ETLinearRegressionWidget;

/**
   Given an ExampleTable, plot each numeric input variable against each
   numeric output variable in a FunctionPlot.  A matrix of these plots is
   shown.  The plots can be selected and a larger composite graph of
   these plots can be displayed.

   @author David Clutter
*/
public class ETLinearRegression extends ETScatterPlot {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
   /**
 * Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("Given an <i>ExampleTable</i>, this module plots each ");
      sb.append("numeric input variable against each numeric output ");
      sb.append("variable in a linear regression plot. A matrix of these ");
      sb.append("plots is displayed. These plots can be selected and a ");
      sb.append("larger composite graph of these plots can be displayed.");
      sb.append("if no numeric input or numeric output attributes are selected there are no plots to display.");
      sb.append("<P>Missing Values Handling: This module treats missing values as");
       sb.append(" regular values.");
      sb.append("</p>");
      return sb.toString();
   }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    /**
* Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleName() {
      return "Example Table Linear Regression";
   }

   /**
    * Return the human readable name of the indexed input.
    * @param index the index of the input.
    * @return the human readable name of the indexed input.
    */
   /**
 * Returns the name of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the input at the specified index.
 */
public String getInputName(int index) {
      switch(index) {
         case 0:
            return "Example Table";
         default: return "NO SUCH INPUT!";
      }
   }

   /**
    * Return the human readable name of the indexed output.
    * @param index the index of the output.
    * @return the human readable name of the indexed output.
    */
   /**
 * Returns the name of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the output at the specified index.
 */
public String getOutputName(int index) {
      switch(index) {
         default: return "NO SUCH OUTPUT!";
      }
   }

   protected UserView createUserView() {
      //return new ETLinearRegressionWidget();
      UserView uv = new ETLinearRegressionWidget(this.getPlotMissingValues());
      return uv;
   }



}

 /**
 * qa comments
 * 12-18-03
 * Vered started qa process
 * Added to module info documentation about missing values handling (as regular values)
 *
 * Problem with resulting vis: resolution of x and y axis is not the same when
  * viewing hte larger plot. this bug is in the widget file. [bug 187]
  *
  *
  * 02-02-04:
  * bug 187 is not to be resolved in this release. it is transformed into a wish
  * to be handled in absic 5 release.
  *
  * ready for basic 4.

  *
  *
*/
