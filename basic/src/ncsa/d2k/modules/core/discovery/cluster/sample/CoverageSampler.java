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

// ==============
// Java Imports
// ==============

// ===============
// Other Imports
// ===============
import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties
          .CoverageSampler_Props;
import java.beans.PropertyVetoException;
import ncsa.d2k.modules.core.discovery.cluster.hac.HAC;


/**
 * <p>Title: CoverageSampler</p>
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
public class CoverageSampler extends CoverageSamplerOPT {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -6628317766358834561L;

   //~ Constructors ************************************************************


   /**
    * Creates a new CoverageSampler object.
    */
   public CoverageSampler() { }

   //~ Methods *****************************************************************

   /**
    * Performs the main work of the module: Chooses a sample set of examples
    * that "cover" the space of all table examples. The table examples are
    * treated as vectors of a vector space.
    *
    * @throws Exception if checking for missing values AND the input table has
    *                   missing values
    */
   protected void doit() throws Exception {

      Table itable = (Table) this.pullInput(0);

      Coverage cover =
         new Coverage(this.getMaxNumCenters(), this.getCutOff(),
                      this.getDistanceMetric(), this.getVerbose(),
                      this.getCheckMissingValues());

      itable = cover.sample(itable);

      this.pushOutput(itable, 0);

   }


   /**
    * Returns the value of the Distance Threshold property.
    *
    * @return int The value of the Distance Threshold property.
    */
   public int getCutOff() { return _cutOff; }


   /**
    * Returns the value of the Distance Metric property.
    *
    * @return int The value of the Distance Metric property.
    */
   public int getDistanceMetric() { return _distanceMetric; }


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
      String[] in = {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };

      return in;
   }


   /**
    * Returns the value of the Maximum Samples property.
    *
    * @return int The value of the Maximum Samples property.
    */
   public int getMaxNumCenters() { return _maxNumCenters; }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] descriptions = new PropertyDescription[5];
      descriptions[0] =
         new PropertyDescription("maxNumCenters",
                                 "Maximum Samples.",
                                 "Maximum number of samples to be generated");
      descriptions[1] =
         new PropertyDescription("cutOff",
                                 "Distance Threshold",
                                 "This property specifies the percent of the max distance to use " +
                                 "as a cutoff value in forming new samples ([1...100].  The max distance between examples " +
                                 "is approximated by taking the min and max of each attribute and forming a " +
                                 "min example and a max example -- then finding the distance between the two.");
      descriptions[2] =
         new PropertyDescription("distanceMetric",
                                 "Distance Metric",
                                 "This property determine the type of distance function used to calculate " +
                                 "distance between two examples." +
                                 "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
                                 "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
                                 "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>");
      descriptions[3] =
         new PropertyDescription("CheckMissingValues",
                                 "Check Missing Values",
                                 "Perform a check for missing values on the table inputs (or not).");
      descriptions[4] =
         new PropertyDescription("verbose",
                                 "Verbose Output",
                                 "Do you want verbose output to the console.");

      return descriptions;
   } // end method getPropertiesDescriptions


   /**
    * Return a custom gui for setting properties.
    *
    * @return A custorm properties editor (GUI component)
    */
   public CustomModuleEditor getPropertyEditor() {
      return new CoverageSampler_Props(this, true, true);
   }

   /**
    * Sets the value of the Distance Threshold property.
    *
    * @param co The value of the Distance Threshold property, should be in the
    *           range [1,100].
    */
   public void setCutOff(int co) throws PropertyVetoException {
     if (co < 0 || co > 100)
       throw new PropertyVetoException(
           "Distance Threshold value should be in the range [0, 100]", null);
     _cutOff = co;
   }

   /**
    * Sets the value of the Distance Metric property.
    *
    * @param dm The value of the Distance Metric property.
    */
   public void setDistanceMetric(int dm) throws PropertyVetoException
   {
     int max = HAC.s_DistanceMetricDesc.length;
     if(dm < 0 || dm >= max){
       String msg = "Distance Metric ID must be in the range " +
           "[0," + (max-1) + "]";
       msg += ". The Distance Metrics IDs are as follows: " ;
       for(int i=0; i<max; i++){
         msg += i + " - " +HAC.s_DistanceMetricLabels[i];
         if(i != max-1){
           msg += ", " ;
         }
       }
       throw new PropertyVetoException(msg, null);

     }
 _distanceMetric = dm; }

   /**
    * Sets the value of Maximum Samples property.
    *
    * @param mnc The value for the Maximum Samples property (should be greater
    *            than zero)
    */
   public void setMaxNumCenters(int mnc) throws PropertyVetoException
   {
     if (mnc < 1)
       throw new PropertyVetoException(this.NUM_CLUSTERS +
                                       " value should be greater than zero", null);
     _maxNumCenters = mnc;
   }

} // end class CoverageSampler
