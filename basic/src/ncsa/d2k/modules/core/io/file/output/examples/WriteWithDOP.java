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
package ncsa.d2k.modules.core.io.file.output.examples;


import ncsa.d2k.core.modules.InputModule;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxyException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


/**
 * Example of how to write data using a DataObjectProxy.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class WriteWithDOP extends InputModule {

   //~ Methods *****************************************************************

   /**
    * writeStreamToURL--example of use of DOP. write the contents of an input
    * stream to the target.
    *
    * @param dop The DataObjectProxy for either local or remote file.
    */
   private void writeStreamToURL(DataObjectProxy dop) {

      FileOutputStream fos = null;
      ObjectOutputStream out = null;


      try {
         File temp = File.createTempFile("xxy", "txt");

         /* Allocate local file  */
         fos = new FileOutputStream(dop.initLocalFile(temp));
         out = new ObjectOutputStream(fos);

         /* write stuff to the file */
         out.writeObject("Contents of the output.");

         byte bb = (byte) 44;
         out.write(bb);
         out.writeInt(77);
         out.writeObject("The end");
         out.flush();
         out.close();

         // Demonstrate: Open the file as input stream, upload from stream
         FileInputStream fis = new FileInputStream(temp);

         /* upload file to server */
         dop.putFromStream(fis);

         /* close connection an ddelete temp file */
         dop.close();
      } catch (IOException e) {
         System.out.println("Unable to serialize object " +
                            "\n" + e);

      } catch (DataObjectProxyException dopex) {
         System.out.println("Exception: " + dopex);
      } catch (SecurityException se) {
         System.out.println("Could not open file: \n" + se);
      }

      System.out.println("Write done");
   } // end method writeStreamToURL

   /**
    * writeToURL--example of use of DOP. write the contents of a file to the
    * target.
    *
    * @param dop The DataObjectProxy for either local or remote file.
    */
   private void writeToURL(DataObjectProxy dop) {

      FileOutputStream fos = null;
      ObjectOutputStream out = null;


      try {
         File temp = File.createTempFile("xxy", "txt");

         /* Allocate local file  */
         fos = new FileOutputStream(dop.initLocalFile(temp));
         out = new ObjectOutputStream(fos);

         /* write stuff to the file */
         out.writeObject("Contents of the output.");
         out.flush();
         out.close();

         /* upload file to server */
         dop.putFromFile(temp);

         /* close connection an ddelete temp file */
         dop.close();
      } catch (IOException e) {
         System.out.println("Unable to serialize object " +
                            "\n" + e);

      } catch (DataObjectProxyException dopex) {
         System.out.println("Exception: " + dopex);
      } catch (SecurityException se) {
         System.out.println("Could not open file: \n" + se);
      }

      System.out.println("Write done");
   } // end method writeToURL


   /**
    * Performs the main work of the module.
    *
    * <p>This step takes in a single DataObjectProxy, and calls the two examples
    * of writing.</p>
    *
    * <p>Use "Input1FileURL" to get the DOP.</p>
    *
    * <p>Try either a local file (path) or a URL (path plus server)</p>
    *
    * @throws Exception Description of exception Exception.
    */
   public void doit() throws Exception {
      DataObjectProxy dop = (DataObjectProxy) pullInput(0);

      /*
       * Write the data:  2 variations that do the same thing.
       */

      writeStreamToURL(dop);

      // Reset the proxy.  Otherwise, internal checks may prevent
      // second write to exactly the same connection.
      DataObjectProxy dop2 = dop.resetDataObjectProxy(dop.getURL());

      writeToURL(dop2);


   }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) {

      switch (i) {

         case (0):
            return "The Data Object Proxy for the file to write.";

         default:
            return "";
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

         case (0):
            return "Data Object Proxy";

         default:
            return "";
      }
   }

   /**
    * Description of method getInputTypes.
    *
    * @return Description of return value.
    */
   public String[] getInputTypes() {
      String[] in = {
         "ncsa.d2k.modules.core.io.proxy.DataObjectProxy",
      };

      return in;
   }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module writes data f to a local or remote file using " +
                "the <i>DataObjectProxy</i>.</p>");
      sb.append("</p><p>Detailed Description: ");
      sb.append("Given a <i>DataObjectProxy</i> this module writes the ");
      sb.append("contents of the target file. ");
      sb.append("</p>");


      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not destroy or modify its input data.");
      sb.append("</p>");

      return sb.toString();
   } // end method getModuleInfo

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleName() { return "Write using DOP"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) { return null; }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) { return ""; }

   /**
    * Description of method getOutputTypes.
    *
    * @return Description of return value.
    */
   public String[] getOutputTypes() {
      String[] out = {};

      return out;
   }

} // end class WriteWithDOP
