/*
 * $Header$
 *
 * ===================================================================
 *
 * D2K-Workflow
 * Copyright (c) 1997,2006 THE BOARD OF TRUSTEES OF THE UNIVERSITY OF
 * ILLINOIS. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License v2.0
 * as published by the Free Software Foundation and with the required
 * interpretation regarding derivative works as described below.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License v2.0 for more details.
 *
 * This program and the accompanying materials are made available
 * under the terms of the GNU General Public License v2.0 (GPL v2.0)
 * which accompanies this distribution and is available at
 * http://www.gnu.org/copyleft/gpl.html (or via mail from the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.), with the special and mandatory
 * interpretation that software only using the documented public
 * Application Program Interfaces (APIs) of D2K-Workflow are not
 * considered derivative works under the terms of the GPL v2.0.
 * Specifically, software only calling the D2K-Workflow Itinerary
 * execution and workflow module APIs are not derivative works.
 * Further, the incorporation of published APIs of other
 * independently developed components into D2K Workflow code
 * allowing it to use those separately developed components does not
 * make those components a derivative work of D2K-Workflow.
 * (Examples of such independently developed components include for
 * example, external databases or metadata and provenance stores).
 *
 * Note: A non-GPL commercially licensed version of contributions
 * from the UNIVERSITY OF ILLINOIS may be available from the
 * designated commercial licensee RiverGlass, Inc. located at
 * (www.riverglassinc.com)
 * ===================================================================
 *
 */
package ncsa.d2k.modules.core.discovery.cluster.sample;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.discovery.cluster.util.TableMissingValuesException;


/**
 * <p>Title: CoverageSamplerOPT</p>
 *
 * <p>Overview:<br>
 * Chooses a sample set of examples that "cover" the space of all table
 * examples. The table examples are treated as vectors of a vector space.</p>
 *
 * <p>Detailed Description: This module selects a sample set of the input table
 * examples such that the set of sampled examples formed is approximately the
 * minimum number of samples needed such that for every example in the table
 * there is at least one example in the sample set of distance &lt= <i>Distance
 * Cutoff</i></p>
 *
 * <p>Data Handling: The original table input is not modified. The sample table
 * is a new table.</p>
 *
 * <p>Scalability: This module has a worst case time complexity of O(<i>Number
 * of Examples</i>^2). However, it typically runs much more quickly. A new table
 * of samples rows is created in memory.</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company:</p>
 *
 * @author  $Author$
 * @version 1.0
 */
public class CoverageSamplerOPT extends DataPrepModule
   implements ClusterParameterDefns {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 2051677851083144722L;

   //~ Instance fields *********************************************************

   /**
    * Specifies the percent of the max distance to use as a cutoff value in
    * forming new samples ([1...100].
    */
   protected int _cutOff = 10;

   /**
    * Determine the type of distance function used to calculate distance between
    * two examples.
    */
   protected int _distanceMetric = 0;


   /** Description of field _ifeatures. */
   protected int[] _ifeatures = null;

   /** Maximum number of samples to be generated. */
   protected int _maxNumCenters = 500;

   /**
    * Check missing values flag. If set to true, this module verifies prior to
    * computation, that there are no missing values in the input table. (In the
    * presence of missing values the module throws an Exception.)
    */
   protected boolean _mvCheck = true;


   /**
    * If true this module performs in verbose mode and outputs information to
    * stdout.
    */
   protected boolean _verbose = false;

   //~ Constructors ************************************************************

   /**
    * Creates a new CoverageSamplerOPT object.
    */
   public CoverageSamplerOPT() { }

   //~ Methods *****************************************************************

   /**
    *  Chooses a sample set of examples that "cover" the space of all table
 * examples. The table examples are treated as vectors of a vector space.
    * @throws Exception If check missing values property is set to true and
    * there are missing values in the input table.

    */
   protected void doit()
      throws Exception {

      ParameterPoint pp = (ParameterPoint) this.pullInput(0);
      this._maxNumCenters = (int) pp.getValue(0);
      this._cutOff = (int) pp.getValue(1);
      this._distanceMetric = (int) pp.getValue(2);

      Table itable = (Table) this.pullInput(1);

      if (this.getCheckMissingValues()) {

         if (itable.hasMissingValues()) {
            throw new TableMissingValuesException("Please replace or filter out missing values in your data.");
         }
      }

      Coverage cover =
         new Coverage(this._maxNumCenters, this._cutOff,
                      this._distanceMetric, this.getVerbose(),
                      this.getCheckMissingValues());

      itable = cover.sample(itable);

      this.pushOutput(itable, 0);

   }

   /**
    * Return the value if the check missing values flag.
    *
    * @return The value of the check missing values flag.
    */
   public boolean getCheckMissingValues() { return _mvCheck; }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  inputIndex Index of the input for which a description should be
    *                    returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int inputIndex) {

      if (inputIndex == 0) {
         return "Control Parameters";
      } else if (inputIndex == 1) {
         return "Table to be sampled.";
      } else {
         return "";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  inputIndex Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int inputIndex) {

      if (inputIndex == 0) {
         return "ParameterPoint";
      } else if (inputIndex == 1) {
         return "Table";
      } else {
         return "";
      }
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] in =
      {
         "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return in;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s +=
         "Chooses a sample set of examples that \"cover\" the space of all table examples.  The ";
      s += "table examples are treated as vectors of a vector space.";
      s += "</p>";

      s += "<p>Detailed Description: ";
      s +=
         "This module selects a sample set of the input table examples such that ";
      s +=
         "the set of sampled examples formed is approximately the minimum number of samples ";
      s +=
         "needed such that for every example in the table there is at least one example in ";
      s += "the sample set of distance &lt= <i>Distance Cutoff</i>";
      s += "</p>";

      s += "<p>Data Handling: ";
      s +=
         "The original table input is not modified.  The sample table is a new table.";
      s += "</p>";

      s += "<p>Scalability: ";
      s +=
         "This module has a worst case time complexity of O(<i>Number of Examples</i>^2).  However, ";
      s +=
         "it typically runs much more quickly.  A new table of samples rows is created in memory.";
      s += "</p>";

      return s;
   } // end method getModuleInfo


   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Coverage Sampler"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int outputIndex) {

      if (outputIndex == 0) {
         return "Sample Table";
      } else {
         return "";
      }
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  outputIndex Index of the output for which a description should be
    *                     returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int outputIndex) {

      if (outputIndex == 0) {
         return "Table";
      } else {
         return "";
      }
   }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] out = {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return out;
   }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] descriptions = new PropertyDescription[2];

      descriptions[0] =
         new PropertyDescription("checkMissingValues",
                                 "Check Missing Values",
                                 "Perform a check for missing values on the table inputs (or not).");

      descriptions[1] =
         new PropertyDescription("verbose",
                                 "Verbose Output",
                                 "Do you want verbose output to the console.");

      return descriptions;
   }

   /**
    * Returns the value of the verbosity flag.
    *
    * @return boolean The value of the verbosity flag
    */
   public boolean getVerbose() { return _verbose; }


   /**
    * Sets the check missing values flag.
    *
    * @param b If true this module verifies before start of computation, that
    *          there are no missing values in the input table. (In the presence
    *          of missing values the module throws an Exception.)
    */
   public void setCheckMissingValues(boolean b) { _mvCheck = b; }

   /**
    * Sets the value of the verbosity flag.
    *
    * @param b boolean If true, this setter method puts this module in verbose
    *          mode.
    */
   public void setVerbose(boolean b) { _verbose = b; }

} // end class CoverageSamplerOPT
