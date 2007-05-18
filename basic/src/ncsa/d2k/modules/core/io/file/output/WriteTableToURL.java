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
package ncsa.d2k.modules.core.io.file.output;

import ncsa.d2k.core.modules.OutputModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxyException;
import ncsa.d2k.modules.core.util.*;

import java.beans.PropertyVetoException;
import java.io.FileWriter;
import java.io.IOException;


/**
 * This module writes the contents of a <code>Table</code> to a flat, local or
 * remote.
 *
 * <p>The file is provided by the Data object Proxy.</P>
 *
 * <p><b>Note:</b> This module is the same as deprecated module <i>
 * WriteTableToFile</i>, extended to access the data through <i>
 * DataObjectProxy</i>.</p>
 *
 * @author  David Clutter
 * @version $Revision$, $Date$
 */
public class WriteTableToURL extends OutputModule {

   //~ Instance fields *********************************************************

   /** When true, put default metadata on the stored file. NOT IMPLEMENTED YET */
   boolean addDefaultMeta = false;

   /** The delimChar. */
   String delimChar = "C";

   /** The field delimiter. */
   transient String delimiter;

   /** Store Ccolumn labels or not. */
   boolean useColumnLabels = true;

   /** STore Ccolumn labels or not. */
   boolean useDataTypes = true;

   //~ Methods *****************************************************************
   private D2KModuleLogger myLogger = 
       D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
   
   public void beginExecution() {
	   myLogger.setLoggingLevel(moduleLoggingLevel);
	   }
   
   private int moduleLoggingLevel=
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass())
	   .getLoggingLevel();

   public int getmoduleLoggingLevel(){
	   return moduleLoggingLevel;
	   }
   
   public void setmoduleLoggingLevel(int level){
	   moduleLoggingLevel = level;
	   }
   
   /**
    * Get the datatype of a column.
    *
    * @param  i Description of parameter i.
    *
    * @return get the datatype of a column.
    */
   static public final String getDataType(int i) {

      switch (i) {

         case 0:
            return "int";

         case 1:
            return "float";

         case 2:
            return "double";

         case 3:
            return "short";

         case 4:
            return "long";

         case 5:
            return "String";

         case 6:
            return "char[]";

         case 7:
            return "byte[]";

         case 8:
            return "boolean";

         case 9:
            return "Object";

         case 10:
            return "byte";

         case 11:
            return "char";

         default:
            return "String";
      }
   } // end method getDataType

   /**
    * Write the table vt to the Data Object Proxy. Parameters indicate
    * formatting to use.
    *
    * @param  vt                The Table.
    * @param  delimiter         Delimiter for columns.
    * @param  dataobj           The Data Object Proxy to write.
    * @param  writeColumnLabels Output labels.
    * @param  writeColumnTypes  Output types.
    *
    * @throws IOException              Error in write.
    * @throws DataObjectProxyException Error in proxy.
    */
   static public void writeTable(Table vt, String delimiter,
                                 DataObjectProxy dataobj,
                                 boolean writeColumnLabels,
                                 boolean writeColumnTypes)
      throws IOException, DataObjectProxyException {
	  
	  FileWriter fw = new FileWriter(dataobj.initLocalFile(null));
      String newLine = "\n";

      // write the column labels
      if (writeColumnLabels) {

         for (int i = 0; i < vt.getNumColumns(); i++) {
            String s = vt.getColumnLabel(i);

            if (s == null || s.length() == 0) {
               s = "column_" + i;
            }

            fw.write(s, 0, s.length());

            if (i != (vt.getNumColumns() - 1)) {
               fw.write(delimiter.toCharArray(), 0, delimiter.length());
            }
         }

         fw.write(newLine.toCharArray(), 0, newLine.length());
      }

      // write the datatypes.
      if (writeColumnTypes) {

         for (int i = 0; i < vt.getNumColumns(); i++) {
            String s = getDataType(vt.getColumnType(i));
            fw.write(s, 0, s.length());

            if (i != (vt.getNumColumns() - 1)) {
               fw.write(delimiter.toCharArray(), 0, delimiter.length());
            }
         }

         fw.write(newLine.toCharArray(), 0, newLine.length());
      }

      // write the actual data
      boolean checkMissing = true;

      for (int i = 0; i < vt.getNumRows(); i++) {

         for (int j = 0; j < vt.getNumColumns(); j++) {
            String s;

            try {

               if (
                   checkMissing &&
                      (vt.isValueMissing(i, j) || vt.isValueEmpty(i, j))) {
                  s = "";
               } else {
                  s = vt.getString(i, j);
               }
            } catch (NullPointerException npe) {

               s = "";
               // This table has not missing or empty values, don't check. ???

            }

            fw.write(s, 0, s.length());

            if (j != (vt.getNumColumns() - 1)) {
               fw.write(delimiter.toCharArray(), 0, delimiter.length());
            }
         } // end for

         fw.write(newLine.toCharArray(), 0, newLine.length());
      } // end for

      fw.flush();
      fw.close();

   } // end method writeTable

   /**
    * Write the table to the file.
    *
    * @throws Exception                Misc. Exception.
    * @throws DataObjectProxyException Error in the proxy.
    * @throws IOException              Error writing local file.
    */
   public void doit() throws Exception {
	      myLogger.setLoggingLevel(moduleLoggingLevel);
	      
	   setAddDefaultMeta(false);
	  
      DataObjectProxy dataobj;
      Table vt = (Table) pullInput(1);

      if (this.isInputPipeConnected(0)) {
         dataobj = (DataObjectProxy) pullInput(0);
      } else {
         throw new DataObjectProxyException("no output specified");
      }
      delimiter = ","; // default to comma

      if (delimChar.equals("S")) {
         delimiter = " ";
      } else if (delimChar.equals("T")) {
         delimiter = "\t";
      }

      try {
    	 // write the actual data
         writeTable(vt, delimiter, dataobj, useColumnLabels, useDataTypes);
      } catch (IOException e) {
         throw new IOException(getAlias() +
                               ": Could not open file: " + dataobj.getURL() +
                               "\n" + e);
      }
      if (this.getAddDefaultMeta() == false) {
    	  dataobj.putFromFile(dataobj.initLocalFile(null));
      } // else ?
      /*
       * Clean up temp files
       */
      dataobj.close();

   } // end method doit

   /**
    * Get value of addDefaultMeta.
    *
    * @return The value.
    */
   public boolean getAddDefaultMeta() { return addDefaultMeta; }

   /**
    * Get value of DelimChar.
    *
    * @return The value.
    */
   public String getDelimChar() { return delimChar; }

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
            return "The object to be written.";

         case 1:
            return "The Table to write.";

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
            return "Table";

         default:
            return "No such input";
      }
   }

   /**
    * Return a String array containing the datatypes the inputs to this module.
    *
    * @return The datatypes of the inputs.
    */
   public String[] getInputTypes() {
      String[] types =
      {
         "ncsa.d2k.modules.core.io.proxy.DataObjectProxy",
         "ncsa.d2k.modules.core.datatype.table.Table"
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
      sb.append("This module writes the contents of a Table to a file.</p>");
      sb.append("<p>Detailed Description: ");
      sb.append("<p><b>Note:</b>  This module is the same as deprecated " +
                "module <i>WriteTableToFile</i>, extended to access the data " +
                "through <i>DataObjectProxy</i>.</p>");
      sb.append("This module writes the contents of the input ");
      sb.append("<i>Table</i> to the file specified by the " +
                "input <i>File Name</i> if that input is connected, otherwise " +
                "it will write the table to a file whose name is constructed " +
                "from the table label.");
      sb.append("The user can select a space, a common, or a tab as the ");
      sb.append("column delimiter using the properties editor. ");
      sb.append("If the <i>useColumnLabels</i> property is set, ");
      sb.append("the first row of the file will be the column labels. ");
      sb.append("If the <i>useDataTypes</i> property is set, the data type of ");
      sb.append("each column will be written to the file.");
      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not destroy or modify its input data.");
      sb.append("</p>");

      return sb.toString();
   }

   /**
    * Returns the name of the module that is appropriate for end user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Write Table to URL or File"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      switch (i) {

         default:
            return "No such output";
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
   public String getOutputName(int i) {

      switch (i) {

         default:
            return "No such output";
      }
   }

   /**
    * Return a String array containing the datatypes of the outputs of this
    * module.
    *
    * @return The datatypes of the outputs.
    */
   public String[] getOutputTypes() {
      String[] types = {};

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

      PropertyDescription[] descriptions = new PropertyDescription[4];

      descriptions[0] =
         new PropertyDescription("delimChar",
                                 "Delimiter Character (C=comma, S=space, T=tab)",
                                 "Selects the delimiter character used to separate columns in the file.  " +
                                 "Enter C for comma, S for space, or T for tab.");

      descriptions[1] =
         new PropertyDescription("useColumnLabels",
                                 "Write Column Labels",
                                 "Controls whether the table's column labels are written to the file.");

      descriptions[2] =
         new PropertyDescription("useDataTypes",
                                 "Write Data Types",
                                 "Controls whether the table's column data types are written to the file.");
      descriptions[3] = 
          new PropertyDescription("moduleLoggingLevel", "Module Logging Level",
                  "The logging level of this modules"+"\n 0=DEBUG; 1=INFO; 2=WARN; 3=ERROR; 4=FATAL; 5=OFF");

      return descriptions;

   }

   /**
    * Get the value of useColumnLabels.
    *
    * @return The value.
    */
   public boolean getUseColumnLabels() { return useColumnLabels; }

   /**
    * Get the value of UseDataTypes.
    *
    * @return The value.
    */
   public boolean getUseDataTypes() { return useDataTypes; }

   /**
    * Called by the D2K Infrastructure to determine if the module is ready to
    * run.
    *
    * <p>if the input is not connected, ignore it.</p>
    *
    * @return Whether or not the module is ready to run.
    */
   public boolean isReady() {

      if (this.isInputPipeConnected(0)) {
         return super.isReady();
      } else {
         return this.getFlags()[1] > 0;
      }
   }

   /**
    * Set addDefaultMeta.
    *
    * <p>Note: NOT IMPLEMENTED YET.</p>
    *
    * @param b The value.
    */
   public void setAddDefaultMeta(boolean b) { addDefaultMeta = b; }

   /**
    * Set DelimChar.
    *
    * @param  c The new value of the delim.
    *
    * @throws PropertyVetoException Reject illegal values.
    */
   public void setDelimChar(String c) throws PropertyVetoException {

      // here we check for valid entries and save as upper case
      if (c.equalsIgnoreCase("C")) {
         delimChar = "C";
      } else if (c.equalsIgnoreCase("S")) {
         delimChar = "S";
      } else if (c.equalsIgnoreCase("T")) {
         delimChar = "T";
      } else {
         throw new PropertyVetoException("An invalid Delimiter Character was entered. " +
                                         "Enter C for comma, S for space, or T for tab.",
                                         null);
      }
   }

   /**
    * Set UseColumnLabels.
    *
    * @param b The new value.
    */
   public void setUseColumnLabels(boolean b) { useColumnLabels = b; }

   /**
    * Set UseDataTypes.
    *
    * @param b new value.
    */
   public void setUseDataTypes(boolean b) { useDataTypes = b; }

} // end class WriteTableToURL
