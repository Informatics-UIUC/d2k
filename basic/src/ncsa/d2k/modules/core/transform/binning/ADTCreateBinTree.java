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
package ncsa.d2k.modules.core.transform.binning;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.ADTree;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.transformations.BinTransform;

import java.util.ArrayList;
import java.util.HashMap;
import ncsa.d2k.modules.core.util.*;


/**
 * <p>Overview:
 * Creates and fills a BinTree with counts
 * <p>Detailed Description:
 * Given a BinTransform containing the definition of the bins, an ADTree count
 * index structure and an ExampleTable that has the input/ output attribute
 * labels and types, builds a BinTree and fills the tree with the needed counts.
 * <p> Data Issues: ADTree was designed to work with nominal data, cannot handle
 * real data.  Thus this module cannot create a BinTree for numeric data.
 * <p> Scalability: The ADTree is an index structure for a datacube.
 * Optimizations have been made so that only the first branch of the tree is
 * stored in memory and the rest are build as the counts are retrieved.
 * Performance is greatly improved if the class variable is in the first column
 * when the ADTree is built.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ADTCreateBinTree extends DataPrepModule {

   //~ Instance fields *********************************************************

   /**
    * This property controls the module's output to stdout. If set to true the
    * created BinTree will be printed.
    */
   private boolean debug;
   
   private D2KModuleLogger myLogger;
   
   //~ Methods *****************************************************************
   public void beginExecution() {
		  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
	   }

   /**
    * Performs the main work of the module.
    *
    * @throws Exception if a problem occurs while performing the work of the
    *                   module
    */
   public void doit() throws Exception {
      ADTree adt = (ADTree) pullInput(0);
      BinTransform btrans = (BinTransform) pullInput(1);

      // ExampleTable vt = (ExampleTable) pullInput(2);
      ExampleTable vt;

      try {
         vt = (ExampleTable) pullInput(2);
      } catch (ClassCastException ce) {
         throw new Exception(getAlias() +
                             ": Select input/output features using ChooseAttributes before this module");
      }

      int[] ins = vt.getInputFeatures();

      if (ins == null || ins.length == 0) {
         throw new Exception("Input features are missing. Please select the input features.");
      }

      int[] out = vt.getOutputFeatures();

      if (out == null || out.length == 0) {
         throw new Exception("Output feature is missing. Please select an output feature.");
      }

      // we only support one out variable..
      int classColumn = out[0];
      String classLabel = vt.getColumnLabel(classColumn);
      int totalClassified = 0;
      int classTotal;
      int binListTotal;

      long startTime = System.currentTimeMillis();

      // get class values

      int index = adt.getIndexForLabel(classLabel);
      String[] cn = adt.getUniqueValues(index);
      // for (int i =0; i < cn.length; i ++) System.out.println("cn[i] " +
      // cn[i]);

      // get the attributes names from the input features
      int[] inputFeatures = vt.getInputFeatures();
      String[] an = new String[inputFeatures.length];

      for (int i = 0; i < inputFeatures.length; i++) {
         an[i] = vt.getColumnLabel(i);
      }

      // given feature names and class values create bin tree
      BinTree bt =
         CreateBinTree.createBinTree(btrans.getBinDescriptors(), cn, an);
      // cn = bt.getClassNames();
      // an = bt.getAttributeNames();

      // get counts and set the tallys
      for (int i = 0; i < cn.length; i++) {
         classTotal = 0;

         for (int j = 0; j < an.length; j++) {
            String[] bn = bt.getBinNames(cn[i], an[j]);
            binListTotal = 0;

            BinTree.ClassTree.BinList bl = null;

            for (int k = 0; k < bn.length; k++) {

               BinTree.ClassTree ct = (BinTree.ClassTree) bt.get(cn[i]);

               bl = (BinTree.ClassTree.BinList) ct.get(an[j]);

               BinTree.ClassTree.Bin b = (BinTree.ClassTree.Bin) bl.get(bn[k]);
               String condition = b.getCondition(an[j]);

               ArrayList pairs = b.getAttrValuePair(an[j]);
               int len = pairs.size();
               HashMap hm;

               for (int l = 0; l < len; l++) {
                  hm = (HashMap) pairs.get(l);
                  hm.put(classLabel, cn[i]);
               }

               // if (debug) System.out.println("pairs " + pairs);

               int s = adt.getDirectCount(adt, pairs);
               // int s = adt.getCount(adt,pairs);

               if (debug) {
             	  myLogger.setDebugLoggingLevel();//temp set to debug
             	  myLogger.debug("COUNT(class: " + cn[i] +
                          " ,att:" + condition + ")=" + s);
                  myLogger.resetLoggingLevel();//re-set level to original level
               }

               b.setTally(s);

               totalClassified = totalClassified + s;
               classTotal = classTotal + s;
               binListTotal = binListTotal + s;
            } // end for

            if (bl != null) {
               bl.setTotal(binListTotal);
            }


         } // end for

         // System.out.println("totalClassified " + totalClassified + "
         // classTotal " + classTotal);
         bt.setClassTotal(cn[i], classTotal);
      } // end for

      // System.out.println("totalClassified " + totalClassified);
      bt.setTotalClassified(totalClassified);

      long endTime = System.currentTimeMillis();

      if (debug) {
    	  myLogger.setDebugLoggingLevel();//temp set to debug
    	  myLogger.debug("time in msec " + (endTime - startTime));
          myLogger.resetLoggingLevel();//re-set level to original level
         bt.printAll();
      }

      pushOutput(bt, 0);

      // pushOutput(vt, 1);
   } // end method doit

   /**
    * Get debug.
    *
    * @return debug
    */
   public boolean getDebug() { return debug; }


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      switch (i) {

         case 0:
            return "An ADTree containing counts";

         case 1:
            return "BinningTransformation containing the bin definitions";

         case 2:
            return "ExampleTable containing the names of the input/output features";

         default:
            return "No such input";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  i Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int i) {

      switch (i) {

         case 0:
            return "AD Tree";

         case 1:
            return "Binning Transformation";

         case 2:
            return "Meta Data Example Table";

         default:
            return "No such input";
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
      String[] types =
      {
         "ncsa.d2k.modules.core.datatype.ADTree",
         "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform",
         "ncsa.d2k.modules.core.datatype.table.ExampleTable"
      };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("Creates and fills a BinTree with counts");
      sb.append("<p>Detailed Description: ");
      sb.append("Given a BinTransform containing the definition of the bins, an ADTree count index structure ");
      sb.append("and an ExampleTable that has the input/ output attribute labels and types, builds a BinTree ");
      sb.append("and fills the tree with the needed counts. ");
      sb.append("<p> Data Issues: ADTree was designed to work with nominal data, cannot handle real data.");
      sb.append("Thus this module cannot create a BinTree for numeric data. ");
      sb.append("<p> Scalability: The ADTree is an index structure for a datacube.");
      sb.append("Optimizations have been made so that only the first branch of the tree is stored ");
      sb.append("in memory and the rest are build as the counts are retrieved. Performance is greatly ");
      sb.append("improved if the class variable is in the first column when the ADTree is built. ");

      return sb.toString();

   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "AD Tree Create Bin Tree"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {
      return "BinTree containing counts for all bins";
   }


   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) { return "Bin Tree"; }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] types = { "ncsa.d2k.modules.core.transform.binning.BinTree" };

      return types;
   }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pd = new PropertyDescription[1];
      pd[0] =
         new PropertyDescription("debug", "Generate Verbose Output",
                                 "This property controls the module's output to stdout. If set to true " +
                                 " the created BinTree will be printed. ");

      return pd;
   }

   /**
    * Set debug.
    *
    * @param deb new debug value
    */
   public void setDebug(boolean deb) { debug = deb; }


} // end class ADTCreateBinTree
