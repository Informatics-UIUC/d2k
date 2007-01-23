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
package ncsa.d2k.modules.core.io.file.input.examples;

import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.InputModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxyException;
import ncsa.gui.Constrain;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;


/**
 * Example of how to download a directory using DataObjectProxy.
 *
 * <p>The source can be a local or remote directory.</p>
 *
 * <p>The destination must be a local directory.</p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class getDirectory extends InputModule {

   //~ Instance fields *********************************************************

   /** the depth to traverse: infinity or 1. */
   private String depthLevel = "infinity";

   //~ Methods *****************************************************************

   /**
    * List the contents pointed to by the DataObjectProxy, to the
    * specified depth.
    *
    * @param  dop   DataObjectProxy of local or remot directory.
    * @param  depth Depth to descend (1, or infinity)
    * 
    * @throws DataObjectProxyException 
    */
   private void listDir(DataObjectProxy dop, int depth)
      throws DataObjectProxyException {
      Vector list = dop.getChildrenURLs(depth);

      Enumeration en = list.elements();

      while (en.hasMoreElements()) {
         Object s = en.nextElement();
         System.out.println("  " + s);
      }
   }

   /**
    * List the only the files (omit directories) pointed to by the 
    * DataObjectProxy, to the specified depth.
    *
    * @param  dop   DataObjectProxy of local or remot directory.
    * @param  depth Depth to descend (1, or infinity)
    * 
    * @throws DataObjectProxyException 
    */
   private void listFiles(DataObjectProxy dop, int depth)
      throws DataObjectProxyException {
      Vector list = dop.getChildrenURLs(depth, true);

      Enumeration en = list.elements();

      while (en.hasMoreElements()) {
         Object s = en.nextElement();
         System.out.println("  " + s);
      }
   }

   /**
    * Example of downloading a whole directory using DataObjectProxy.
    *
    * @throws Exception                
    */
   public void doit() throws Exception {

	   /*
	    * Get the source and destination from input.
	    */
      DataObjectProxy srcdop = (DataObjectProxy) pullInput(0);
      DataObjectProxy desdop = (DataObjectProxy) pullInput(1);
      int depth;
      System.out.println("depth is " + this.getDepth());
      System.out.println("Source DOP is " + srcdop.getURL());
      System.out.println("Destination DOP is " + desdop.getURL());

      /*
       * This example sets depth as a property.
       */
      if (this.getDepth().equals("1")) {
         depth = 1;
      } else if (this.getDepth().equalsIgnoreCase("infinity")) {
         depth = DataObjectProxy.DEPTH_INFINITY;
      } else {
         throw new DataObjectProxyException("Not valid depth value.");
      }

      URL desurl = desdop.getURL();
      System.out.println("List the contents of the source:");

      long b1 = System.currentTimeMillis();
      listDir(srcdop, depth);

      long a1 = System.currentTimeMillis();

      System.out.println("List just the files ");
      listFiles(srcdop, depth);

      System.out.println("Start downloading " + srcdop.getURL() + " to " +
                         desurl);

      long b2 = System.currentTimeMillis();

      if (desdop.getUsername().equals("")) {
         srcdop.downloadDir(desurl, depth);
      } else {
         srcdop.downloadDir(desdop, depth);
      }

      // srcdop.downloadDirP(desdop, depth);
      long a2 = System.currentTimeMillis();
      System.out.println("End downloading " + srcdop.getURL() + " to " +
                         desurl);


      System.out.println("List the contents of the destination:");

      long b3 = System.currentTimeMillis();
      listDir(desdop, depth);

      long a3 = System.currentTimeMillis();
      System.out.println("List just the files ");
      listFiles(desdop, depth);
      desdop.close();
      srcdop.close();
      System.out.println("Timing:");
      System.out.println("  List Source: " + (a1 - b1) + " ms.");
      System.out.println("  Download: " + (a2 - b2) + " ms.");
      System.out.println("  List Dest: " + (a3 - b3) + " ms.");
   } // end method doit

   /**
    * Value of the property depth.
    *
    * @return depth.
    */
   public String getDepth() { return depthLevel; }

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
            return "The DataObjectProxy to source directory.";

         case 1:
            return "The DataObjectProxy to destination directory.";

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
            return "DataObjectProxy";

         case 1:
            return "DataObjectProxy";

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
         "ncsa.d2k.modules.core.io.proxy.DataObjectProxy",
         "ncsa.d2k.modules.core.io.proxy.DataObjectProxy"
      };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: Example download a directory to a directory.";

      return s;
   } // end method getModuleInfo

   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "getDirectory"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int arg0) { return null; }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName() { return null; }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() { return null; }

   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[1];

      pds[0] =
         new PropertyDescription("depthLevel",
                                 "depth Level",
                                 "The depth level, it should be one of \"1\" or \"infinity\"");

      return pds;
   }

   /**
    * Return a custom property editor.
    *
    * @return return a custom property editor.
    */
   public CustomModuleEditor getPropertyEditor() { return new PropEdit(); }

   /**
    * Set the depth of the traversal.
    * 
    * <p>"1" or "infinity"</p>
    *
    * @param s Description of parameter s.
    */
   public void setDepth(String s) { depthLevel = s; }

   //~ Inner Classes ***********************************************************

   private class PropEdit extends JPanel implements CustomModuleEditor {

      /** Use serialVersionUID for interoperability. */
      static private final long serialVersionUID = 2637786544956495261L;

      /* the value for the depth */
      private JTextField depthjtf;

      private PropEdit() {
         setLayout(new GridBagLayout());
         this.setMinimumSize(new Dimension(14, 14));

         String dl = getDepth();
         depthjtf = new JTextField(10);
         depthjtf.setText(dl);

         Constrain.setConstraints(this, new JLabel("Depth Level"), 0, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(this, depthjtf, 1, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);


      }

      public boolean updateModule() throws Exception {
         String f0 = depthjtf.getText();

         boolean didChange = false;
         String sdl = getDepth();

         /*
          * Idelly, should check the value of the input, which must be "1", 
          * or "infinity".
          */
         if (f0 != sdl) {
            setDepth(f0);
            didChange = true;
         }

         return didChange;
      } // end method updateModule
   } // end class PropEdit

} // end class getDirectory
