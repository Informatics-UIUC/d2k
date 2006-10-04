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

import java.net.URL;


/**
 * <p>Factory to create different DataObjectProxy implementations according to
 * the different protocols.</p>
 *
 * <p>This version only handles local files and files accessed via WebDAV (via
 * HTTP).</p>
 * 
 * @see DataObjectProxy
 * @see Input1FileURL
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class DataObjectProxyFactory {

   //~ Methods *****************************************************************

   /**
    * The main method: create a DataObjectProxy for the given object.
    *
    * @param  url - The URL the DataObjectProxy to point to.
    *
    * @return A DataObjectProxy.
    */
   static public DataObjectProxy getDataObjectProxy(URL url) {
      return getDataObjectProxy(url, "", "");
   }

   /**
    * Description of method getDataObjectProxy.
    *
    * <p>In the current implementation, the URL is examined to determine what
    * implementation class to select. In future versions, this selection may
    * well require a different method.</p>
    *
    * @param  url      - the URL the DataObjectProxy to point to.
    * @param  username - the username to be used to access DataObjectProxy.
    * @param  password - the password to be used to access DataObjectProxy.
    *
    * @return The new Data Object Proxy.
    */
   static public DataObjectProxy getDataObjectProxy(URL url, String username,
                                                    String password) {
      String protocol = url.getProtocol();

      if (protocol.equals("file")) {
         username = System.getProperty("user.name");

         return new LocalDataObjectProxyImpl(url, username, password);
      }

      if (protocol.equals("http") || protocol.equals("https")) {
         return new WebdavDataObjectProxyImpl(url, username, password);
      } else {
         return null;
      }
   }

} // end class DataObjectProxyFactory
