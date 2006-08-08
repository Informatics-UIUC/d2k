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
package ncsa.d2k.modules.core.datatype.table.paging;

import ncsa.d2k.modules.core.datatype.table.Table;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * The <code>Page</code> class encapsulates a <code>Table</code> that can be
 * paged to and from disk.
 *
 * @author  redman
 * @version $Revision$, $Date$
 */
public class SubsetsPage extends Page {

   //~ Static fields/initializers **********************************************

   /** The universal version identifier. */
   static final long serialVersionUID = -3452910203130295662L;

   /** Prefix for temp files. */
   static final String OFFSET_PREFIX = "offsets-";

   //~ Instance fields *********************************************************

   /** File we save the table in. */
   File subsetFile;

   //~ Constructors ************************************************************

   /**
    * Constructs a new <code>Page</code> that can read the given <code>
    * Table</code> from (and write it to) the given <code>File</code>. The
    * <code>Table</code> itself will be retained in memory if and only if the
    * <code>keepInMemory</code> flag is <code>true</code>.
    *
    * @param  table        The <code>Table</code> to store
    * @param  subset       A file on disk
    * @param  keepInMemory Keep the <code>Table</code> in memory after
    *                      construction?
    *
    * @throws IOException If a problem occurs while constructing the page
    */
   SubsetsPage(Table table, int[] subset, boolean keepInMemory)
      throws IOException {
      this.pageFile = File.createTempFile(PAGE_PREFIX, SUFFIX);
      this.pageFile.deleteOnExit();
      this.subsetFile = File.createTempFile(OFFSET_PREFIX, SUFFIX);
      this.subsetFile.deleteOnExit();
      this.table = table;
      numRows = table.getNumRows();
      numColumns = table.getNumColumns();
      this.subset = subset;
      pageOut();

      if (!keepInMemory) {
         this.table = null;
         this.subset = null;
      }

      timestamp = System.currentTimeMillis();
   }

   /**
    * If we are creating a new subset of the table, the page for the data
    * already exists, we just need to create a new subset. The table has already
    * been written to memory since it is already part of another paging table.
    *
    * @param  pp    File on disk containing the data
    * @param  table The <code>Table</code> to store
    * @param  s     The subset data
    *
    * @throws IOException If a problem occurs while constructing the page
    */
   SubsetsPage(File pp, Table table, int[] s) throws IOException {
      this.pageFile = pp;
      this.table = null;
      numRows = table.getNumRows();
      numColumns = table.getNumColumns();
      this.subset = null;

      // Write the subset to an object output stream.
      this.subsetFile = File.createTempFile(OFFSET_PREFIX, SUFFIX);
      this.subsetFile.deleteOnExit();

      ObjectOutputStream O =
         new ObjectOutputStream(new FileOutputStream(subsetFile));
      O.writeObject(s);
      O.close();
      timestamp = System.currentTimeMillis();
   }

   /**
    * Constructs a new <code>Page</code> that can read the given <code>
    * Table</code> from (and write it to) the given <code>File</code>. The
    * <code>Table</code> itself will be retained in memory if and only if the
    * <code>keepInMemory</code> flag is <code>true</code>.
    *
    * @param  table        The <code>Table</code> to store
    * @param  keepInMemory Keep the <code>Table</code> in memory after
    *                      construction?
    *
    * @throws IOException If a problem occurs while constructing the page
    */

   public SubsetsPage(Table table, boolean keepInMemory) throws IOException {
      this.pageFile = File.createTempFile(PAGE_PREFIX, SUFFIX);
      this.pageFile.deleteOnExit();
      this.subsetFile = File.createTempFile(OFFSET_PREFIX, SUFFIX);
      this.subsetFile.deleteOnExit();
      this.table = table;
      numRows = table.getNumRows();
      numColumns = table.getNumColumns();
      subset = new int[this.numRows];

      for (int i = 0; i < this.numRows; i++) {
         this.subset[i] = i;
      }

      pageOut();

      if (!keepInMemory) {
         this.table = null;
         this.subset = null;
      }

      timestamp = System.currentTimeMillis();
   }

   //~ Methods *****************************************************************

   /**
    * Writes the table out to disk, if it has been modified, and frees the table
    * for garbage collection.
    *
    * <p><b>WARNING:</b> This method must only be called while the resource is
    * externally write-locked!</p>
    */
   void free() {
      super.free();
      subset = null;
   }

   /**
    * Returns the table subset, if it is spooled off to disk read it in.
    *
    * @return The table.
    */
   int[] getSubset() {

      if (table == null) {
         pageIn();
      }

      return subset;
   }

   /**
    * Reads the table in from disk.
    *
    * <p><b>WARNING:</b> This method must only be called while the resource is
    * externally write-locked!</p>
    */
   void pageIn() {

      if (this.pageFile == null) {
         return;
      }

      try {
         super.pageIn();

         // Read the subset, it is in a seperate file so we can have subset
         // tables which share the same data.
         long start = System.currentTimeMillis();
         ObjectInputStream I =
            new ObjectInputStream(new BufferedInputStream(new FileInputStream(subsetFile)));
         this.subset = (int[]) I.readObject();
         I.close();

         long start2 = System.currentTimeMillis();
         fileReadTime += (start2 - start);
      } catch (Exception e) {
         System.err.println("Page " + pageFile + ": exception on pageIn:");
         e.printStackTrace(System.err);
      } finally {
         dirty = false;
      }

   } // end method pageIn

   /**
    * Writes the table out to disk.
    *
    * <p><b>WARNING:</b> This method must only be called while the resource is
    * externally write-locked!</p>
    */
   protected void pageOut() {

      if (pageFile == null) {
         return;
      }

      try {
         super.pageOut();

         // Write the subset to an object output stream.
         ObjectOutputStream O =
            new ObjectOutputStream(new FileOutputStream(subsetFile));
         O.writeObject(this.subset);
         O.close();
      } catch (Exception e) {
         System.err.println("Page " + pageFile + ": exception on pageOut:");
         e.printStackTrace(System.err);
      }

      dirty = false;
   }
} // end class SubsetsPage
