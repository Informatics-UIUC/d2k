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

// import org.scidac.cmcs.util.NSProperty;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Logger;


/**
 * DataObjectProxy is a common interface for every data object proxy.
 *
 * <p>The DataObjectProxy manages access to data objects, local or remote, using
 * alternative access protocols.</p>
 *
 * <p>To access an object, the general procedure is to construct a URL, and call
 * the DataObjectProxyFactory to get an instance of a proxy:
 * <p>
 *  <pre>
 *      DataObjectProxy dataobj =
 *      DataObjectProxyFactory.getDataObjectProxy(
 *          url, username, password);
 *     pushOutput(dataobj, 0);
 * </pre></p>
 *
 * <p>To read from the object, the proxy is called to get an input stream:
 * <p>
 *    <pre>
 *      BufferedReader reader = new BufferedReader(
 *      new InputStreamReader(DataObjProx.getInputStream()));
 *    </pre>
 *
 * <p>To write the object, a file or input stream is passed to the
 * DataObjectProxy. After the put, the DataObjectProxy should be closed to
 * complete the store.</p>
 * 
 * <p>To write a temporary file and then push: 
 *    <pre>
 *      FileWriter fw = new
 *           FileWriter( dataobj.getLocalFile());
 *
 *      fw.write(...); fw.flush(); fw.close();
 *
 *      dataobj.putFromFile(dataobj.getLocalFile()); 
 *      dataobj.close();
 *    </pre>
 *
 * <p>To write from a stream, 
 * <p>  <pre>
 *        InputStream is == ...
 *        dataobj.putFromFile(is); 
 *        dataobj.close();
 *     </pre></p>
 *
 * <p>All Known Implementing Classes:</p>
 *
 * <ul>
 *   <li>LocalDataObjectProxyImpl,WebDataObjectProxyImpl</li>
 * </ul>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 * @see     DataObjectProxyFactory
 */
public abstract class DataObjectProxy {

   //~ Instance fields *********************************************************

   /** Description of field mLogger. */
   protected Logger mLogger = null;

   /** Password to access the current DataObjectProxy if needed. */
   protected String mPassword = null;

   /** url of the current DataObjectProxy. */
   protected URL mURL = null;

   /** Username to access the current DataObjectProxy if needed. */
   protected String mUsername = null;

   /** A temporary directory to store locally cached files. */
   protected File tempDataDir = null;

   /**
    * tempFilesCreated store the paths of all of the temp files created in the
    * process.
    */
   /*
    * from where --- url destination date
    *
    */
   protected Vector tempFilesCreated;

   //~ Methods *****************************************************************

   /**
    * Get the password being used to access the current DataObjectProxy.
    *
    * @return the password
    */
   protected abstract String getPassword();

   /**
    * Close the connection and clean up the temp files created.
    */
   public abstract void close();

   /**
    * Get the InputStream from the URL being pointed to by the DataObjectProxy.
    *
    * <p>When the object is remote, this will be the input stream from the
    * remote service.</p>
    *
    * <p>When the object is local, this will be a FileInputStream.</p>
    *
    * @return InputStream opened from the URL.
    *
    * @throws DataObjectProxyException.
    * @throws DataObjectProxyException  Description of exception
    *                                   DataObjectProxyException.
    *
    * @see    getLocalInputStream, getLocalFile
    */
   public abstract InputStream getInputStream() throws DataObjectProxyException;

   /**
    * Get the locally cached copy.
    *
    * <p>When the object is remote, this will be a cached local copy from the
    * remote service.</p>
    *
    * <p>When the object is local, this will be a local File.*</p>
    *
    * @return a locally cached file.
    *
    * @throws DataObjectProxyException.
    * @throws DataObjectProxyException  Description of exception
    *                                   DataObjectProxyException.
    *
    * @see    getLocalInputStream, getInputStream
    */
   public abstract File getLocalFile() throws DataObjectProxyException;

   /**
    * Get InputStream from a locally cached file.
    *
    * <p>When the object is remote, this force a copy to be cached from the
    * remote service.</p>
    *
    * <p>When the object is local, this will be a local File.*</p>
    *
    * @return InputStream from a locally cached file.
    *
    * @throws DataObjectProxyException.
    * @throws DataObjectProxyException  Description of exception
    *                                   DataObjectProxyException.
    *
    * @see    getLocalFile, getInputStream
    */
   public abstract InputStream getLocalInputStream()
      throws DataObjectProxyException;

   /**
    * Get all properties on the current DataObjectProxy.
    *
    * @return A Hashtable of property objects.
    *
    * @throws Exception                Exception in read.
    */
   public abstract Object getMeta() throws DataObjectProxyException;

   /**
    * Get the value of the specified property on the current DataObjectProxy.
    *
    * @param  prop - property including namespace and key.
    *
    * @return a single propery.
    *
    * @throws DataObjectProxyException Excetion in read.
    */
   public abstract Object getMeta(Object prop) throws DataObjectProxyException;

   /**
    * Get the username being used to access the current DataObjectProxy.
    *
    * @return the username in use.
    */
   public abstract String getUsername();

   /**
    * Does the URL point to a collection or a directory?
    *
    * @return true if the current URL is a collection, false otherwise.
    *
    * @throws DataObjectProxyException.
    * 
    */
   public abstract boolean isCollection() throws DataObjectProxyException;

   /**
    * Create a directory at path
    *
    * @return a DOP for the new object.
    *
    * 
    * @throws DataObjectProxyException  
    */
   public abstract DataObjectProxy createCollection(String path) 
      throws DataObjectProxyException;

   /**
    * Put the file to the current URL being pointed to by the current
    * DataObjectProxy.
    *
    * <p>When the URL is remote, this will push the file to the remote server.
    * </p>
    *
    * <p>When the URL is local, this will copy the file to the destination if
    * necessary.</p>
    *
    * @param  file - the file to be put to the current URL.
    *
    * @throws DataObjectProxyException.
    * @throws DataObjectProxyException  Description of exception
    *                                   DataObjectProxyException.
    */

   public abstract void putFromFile(File file) throws DataObjectProxyException;

   /*
    * Put the file and the given properties to the current URL @param  file  -
    * the file to be put to the current URL. @param  nsp   - the property to be
    * put to the current URL including namespace and key. @param  value -  the
    * property value to be put to the current URL. @throws
    * DataObjectProxyException.
    */
// public abstract void putFromFileWithProp(File file, NSProperty nsp,String
// value) throws Exception;

   /**
    * Put InputStream to the current URL.
    *
    * <p>When the object is remote, this will copy from the input stream to the
    * destination URL.</p>
    *
    * <p>When the object is remote, the inptu strema will be copied to a local
    * file.</p>
    *
    * @param  is - InputStream to be put to the current URL.
    *
    * @throws Exception                Description of exception Exception.
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   public abstract void putFromStream(InputStream is)
      throws DataObjectProxyException;

   /**
    * Get a new DataObjectProxy represents the new URL.
    *
    * <p>This method is used to change the target of a proxy.</p>
    *
    * @param  newURL URL to be used to create a DataObjectProxy.
    *
    * @return a DataObjectProxy.
    */
   public abstract DataObjectProxy resetDataObjectProxy(URL newURL);

   /**
    * Get a new DataObjectProxy based on the new URL, username and password.
    *
    * @param  newURL  - url to be used to create a DataObjectProxy.
    * @param  newUser - username to access the URL.
    * @param  newPass - password to access the URL.
    *
    * @return DataObjectProxy.
    */
   public abstract DataObjectProxy resetDataObjectProxy(URL newURL,
                                                        String newUser,
                                                        String newPass);

   /**
    * Set up the password.
    *
    * @param pass - password to be used.
    */
   public abstract void setPassword(String pass);

   /**
    * Set up the username.
    *
    * @param user - username to be used.
    */
   public abstract void setUsername(String user);

   /**
    * Get the full URL currently being pointed to by the DataObjectProxy.
    *
    * @return The current URL the DataObjectProxy points to.
    */
   public URL getURL() { return mURL; }

   /**
    * Put a set of properties on the current DataObjectProxy.
    *
    * <p><b>Note:</b> Metadata is not implemented yet.</p>
    *
    * @param  proptable - a Hashtable of key/value pairs.
    *
    * @throws DataObjectProxyException.
    * @throws DataObjectProxyException  Description of exception
    *                                   DataObjectProxyException.
    */
   public void putMeta(Object proptable) throws DataObjectProxyException { }

   /*
    * Put a single property on the current DataObjectProxy. @param  prop  - the
    * property with namespace and key. @param  value - the value of the
    * property. @throws DataObjectProxyException.
    */
   // public void putMeta(NSProperty prop, String value) throws
   // DataObjectProxyException { }

   /**
    * Search metadata. Not implmented yet.
    *
    * @return null.
    *
    * @throws Exception Description of exception Exception.
    */
   public Object searchMeta() throws Exception { return null; }


} // end class DataObjectProxy
