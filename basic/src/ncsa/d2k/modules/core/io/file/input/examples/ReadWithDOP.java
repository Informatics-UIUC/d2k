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


import ncsa.d2k.core.modules.InputModule;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxyException;
import org.apache.log4j.Logger;
import ncsa.d2k.core.D2KLogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Example use of DataObjecProxy to read data.
 *
 * 
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ReadWithDOP extends InputModule {

   //~ Methods *****************************************************************

   /**
    * readFromLocalCopy--example of use of DOP.  Reads the contents of the
    * target from a stream.
    * <p>
    * If the target is remote, the file is downloaded to a temporary file, and
    * the local file stream is read.
    * <p> 
    * Compare to readFromStream, which reads the remote from an HTTP stream.
    * <p>
    * When the DOP is local, there is no difference.
    *
    * @param _dop The DataObjectProxy for either local or remote file.
    */
   private void readFromLocalCopy(DataObjectProxy _dop) {
	int _tableLength = 0;

      // demonstration: count the number of lines in the file
      try {
         BufferedReader rdr =
            new BufferedReader(new InputStreamReader(_dop.getLocalInputStream()));
         String line = null;

         while ((line = rdr.readLine()) != null) {
            _tableLength++;
         }
      } catch (DataObjectProxyException dopex) {
         System.out.println("Exception: " + dopex);
      } catch (IOException ioex) {
         System.out.println("Exception: " + ioex);
      }
      System.out.println("Read "+_tableLength+" lines from "+_dop.getURL());
   }

   /**
    * readFromStream--example of use of DOP.  Reads the contents of the
    * target from a stream.
    * <p>
    * If the target is remote, an HTTP stream is read.
    * <p> 
    * Compare to readFromLocalCopy, which reads from a locally cached copy.
    * <p>
    * When the DOP is local, there is no difference.
    *
    * @param _dop The DataObjectProxy for either local or remote file.
    */
   private void readFromStream(DataObjectProxy _dop) {

      int _tableLength = 0;

      // Demonstration: count the number of lines in the file
      try {
         BufferedReader rdr =
            new BufferedReader(new InputStreamReader(_dop.getInputStream()));
         String line = null;

         while ((line = rdr.readLine()) != null) {
            _tableLength++;
         }
      } catch (DataObjectProxyException dopex) {
         System.out.println("Exception: " + dopex);
      } catch (IOException ioex) {
         System.out.println("Exception: " + ioex);
      }
      System.out.println("Read "+_tableLength+" lines from "+_dop.getURL());
      
   }
   
   /**
    * readFile--example of use of DOP.  Reads the contents of the
    * target from a stream.
    * <p>
    * If the target is remote, an HTTP stream is read.
    * <p> 
    * Compare to readFromLocalCopy, which reads from a locally cached copy.
    * <p>
    * When the DOP is local, there is no difference.
    *
    * @param _dop The DataObjectProxy for either local or remote file.
    */
   private void readFile(DataObjectProxy _dop,File dest) {
	   int _tableLength = 0;
	   try {
		   // manually read into local file
	    	  File file = _dop.readFile(dest);

	      // count the number of lines in the local file
	     
	         BufferedReader rdr =
	            new BufferedReader(new FileReader(file));
	         String line = null;

	         while ((line = rdr.readLine()) != null) {
	            _tableLength++;
	         }
	      } catch (DataObjectProxyException dopex) {
	         System.out.println("Exception: " + dopex);
	      } catch (IOException ioex) {
	         System.out.println("Exception: " + ioex);
	      }
	      System.out.println("Read "+_tableLength+" lines from "+_dop.getURL());
	   }

   /**
    * Performs the main work of the module.
    * <p>
    * This step takes in a single DataObjectProxy, and calls the three examples
    * of reading.
    * <p>
    * Use "Input1FileURL" to get the DOP.
    * <p>
    * Try either a local file (path) or a URL (path plus server)
    *
    * @throws Exception Description of exception Exception.
    */
   public void doit() throws Exception {
	   DataObjectProxy dop = (DataObjectProxy) pullInput(0);

      /*
       * Read the data:  3 variations that do the same thing.
       */
      readFromStream(dop);

      readFromLocalCopy(dop);
      
      readFile(dop,null);
      
      dop.close();
      
      /*
       * 'readFile(dop,destfile)' would read into local file
       */
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
            return "The Data Object Proxy for the file to read.";

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
    * List of the input types.
    *
    * @return The input types.
    */
   public String[] getInputTypes() {
      String[] in = {
         "ncsa.d2k.modules.core.io.proxy.DataObjectProxy",
      };

      return in;
   }

   /**
    * Get info about the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module reads data from a local or remote file using " +
                "the <i>DataObjectProxy</i>.</p>");
      sb.append("</p><p>Detailed Description: ");
      sb.append("Given a <i>DataObjectProxy</i> this module reads the ");
      sb.append("contents of the target file. ");
      sb.append("</p>");
      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not destroy or modify its input data.");
      sb.append("</p>");

      return sb.toString();
   } // end method getModuleInfo

   /**
    * Get the pretty name of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleName() { return "Read using DOP"; }

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
    * Get the output types.
    *
    * @return Description of return value.
    */
   public String[] getOutputTypes() {
      String[] out = {};

      return out;
   }

} // end class ReadWithDOP
