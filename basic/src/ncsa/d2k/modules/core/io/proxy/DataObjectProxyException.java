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
package ncsa.d2k.modules.core.io.proxy;


/**
 * Signals that an DataObjectProxyException has occurred.
 * 
 * <p>This exception can be used to wrap an exception
 * that occurs in other code, such as XML parsing.</p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class DataObjectProxyException extends Exception {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = -311010850266894842L;

   //~ Constructors ************************************************************

   /**
    * Creates a new DataObjectProxyException object.
    */
   public DataObjectProxyException() { super(); }

   /**
    * Creates a new DataObjectProxyException object.
    *
    * @param msg Description of parameter msg.
    */
   public DataObjectProxyException(String msg) { super(msg); }

   /**
    * Creates a new DataObjectProxyException object.
    *
    * @param cause Description of parameter cause.
    */
   public DataObjectProxyException(Throwable cause) { super(cause); }

   /**
    * Creates a new DataObjectProxyException object.
    *
    * @param msg   Description of parameter msg.
    * @param cause Description of parameter cause.
    */
   public DataObjectProxyException(String msg, Throwable cause) {
      super(msg, cause);
   }

} // end class DataObjectProxyException
