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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


/**
 * Creates a <code>FileChannel</code> input stream from a <code>File</code>.
 *
 * @author  redman
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class FileChannelInputStream extends InputStream {

   //~ Instance fields *********************************************************

   /** Holds a buffer of bytes from the file. */
   ByteBuffer buffer;

   /**
    * The underlying <code>FileChannel</code> of this <code>InputStream</code>.
    */
   FileChannel channel;

   /** The underlying <code>File</code> of this <code>InputStream</code>. */
   File file;

   /** Buffer size, in bytes. */
   int size = 102400;

   //~ Constructors ************************************************************

   /**
    * Creates a new <code>FileChannelInputStream</code> object.
    *
    * @param  f <code>File</code> to create this <code>InputStream</code> from.
    *
    * @throws IOException If a problem occurs while creating the <code>
    *                     InputStream</code>
    */
   FileChannelInputStream(File f) throws IOException {
      super();
      file = f;
      channel = new FileInputStream(file).getChannel();
      buffer = ByteBuffer.allocateDirect(size);
      channel.read(buffer);
      buffer.rewind();
   }

   //~ Methods *****************************************************************

   /**
    * Reads data from the <code>FileChannel</code> into the buffer.
    *
    * @return Bitmask of the byte retrieved from the buffer
    *
    * @throws IOException If a problem occurs while reading the data from the
    *                     channel
    */
   public int read() throws IOException {

      if (buffer.remaining() == 0) {
         int amount;
         buffer.clear();
         amount = channel.read(buffer);

         if (amount == -1) {
            return -1;
         }

         buffer.rewind();
      }

      try {
         return buffer.get() & 0x000000FF;
      } catch (BufferUnderflowException fufe) {
         System.out.println("Buffer underflow.");

         return -1;
      }
   }

} // end class FileChannelInputStream
