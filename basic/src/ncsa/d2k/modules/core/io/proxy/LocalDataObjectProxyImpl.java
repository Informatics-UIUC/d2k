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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * LocalDataObjectProxyImpl handles access to a local file. Users can read file or
 * InputStream from, write file or InputStream to the URL with <code>file</code>
 * as protocol.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class LocalDataObjectProxyImpl extends DataObjectProxy {

   //~ Constructors ************************************************************

   /**
    * Creates a new LocalDataObjectProxyImpl object.
    *
    * @param url      - The URL the LocalDataObjectProxyImpl to point to.
    * @param username - The username to be used to access the
    *                 LocalDataObjectProxyImpl.
    * @param password - The password to be used to access the
    *                 LocalDataObjectProxyImpl.
    */
   public LocalDataObjectProxyImpl(URL url, String username, String password) {
      mURL = url;
      setUsername(username);
   }

   //~ Methods *****************************************************************

   /**
    * <p>Catch exceptions and throw new corresponding DataObjectProxyExceptions
    * with the root cause.</p>
    *
    * @param  e - A type of Exception to be passed in.
    *
    * @throws DataObjectProxyException
    */
   private void handleExceptions(Exception e) throws DataObjectProxyException {

      // Handle IOException
      if (e instanceof IOException) {
         throw new DataObjectProxyException("*****IOException " +
                                            e.getLocalizedMessage());
      }

      // Handle URISyntaxException
      else if (e instanceof URISyntaxException) {
         throw new DataObjectProxyException("*****URISyntaxException " +
                                            e.getLocalizedMessage());
      }

      // Handle MalformedURLException
      else if (e instanceof MalformedURLException) {
         throw new DataObjectProxyException("*****MalformedURLException " +
                                            e.getLocalizedMessage());
      } else {
         throw new DataObjectProxyException("*****Unknown Exception " +
                                            e.getLocalizedMessage());
      }


   }

   /**
    * Null because the password is probably not necessary to access local files.
    *
    * @return null because the password is probably not necessary to access
    *         local files.
    */
   protected String getPassword() { return null; }

   /**
    * No action required to clean up a local file.
    */
   public void close() { }


   /**
    * Get InputStream from URL being pointed to by the current
    * LocalDataObjectProxy.
    *  
    * @return InputStream from the url.
    *
    * @throws DataObjectProxyException
    */
   public InputStream getInputStream() throws DataObjectProxyException {
      InputStream is = null;

      try {
         is = mURL.openStream();
      } catch (IOException ioe) {
         handleExceptions(ioe);
      }

      return is;
   }

   /**
    * Get the file the LocalDataObjectProxyImpl points to, if it doesn't exist,
    * create a new file using the path and file name given by the url being
    * pointed to by the current LocalDataObjectImpl.
    * 
    * <p>In this version, when this method is called for a file does not exist,
    * an empty file will be created.</p>
    *
    * <p>In future versions, this behavior may be changed.</p>
    *
    * @return File The file being pointed to by the current LocalDataObjectImpl
    *
    * @throws DataObjectProxyException
    */
   public File getLocalFile() throws DataObjectProxyException {
	  boolean doCreate = true; // This may become a parameter.
      File file = null;

      try {
          file = new File(mURL.toURI().getPath());

         // If file doesn't exist, create a empty file based on url.
         if (!file.exists()) {

            if (doCreate) {
            	 file.createNewFile();
            } else {
               throw new DataObjectProxyException(file + ": not found");
            }
         }
      } catch (Exception e) {
         handleExceptions(e);
      }

      return file;
   }

   /**
    * Same as getInputStream() for a local file.
    *
    * @return InputStream from locally cached copy.
    *
    * @throws DataObjectProxyException
    */
   public InputStream getLocalInputStream() throws DataObjectProxyException {
      return getInputStream();
   }

   /**
    * getMeta.
    *
    * <p>Metadata is not defined for local files.</p>
    *
    * @return null
    *
    * @throws DataObjectProxyException Always throws.
    */
   public Object getMeta() throws DataObjectProxyException {
      throw new DataObjectProxyException("metadata not supported for local files");
   }

   /**
    * getMeta.
    *
    * <p>Metadata is not defined for local files.</p>
    *
    * @param  props properties to get.
    *
    * @return null.
    *
    * @throws DataObjectProxyException always throws.
    */
   public Object getMeta(Object props) throws DataObjectProxyException {
      throw new DataObjectProxyException("metadata not supported for local files");
   }


   /**
    * Get the username.
    *
    * @return username.
    */
   public String getUsername() { return mUsername; }

   /**
    * Does the LocalDataObjectProxy point to a directory?
    *
    * @return true - if the URL points to a directory, false - otherwise.
    */
   public boolean isCollection() {
      File file = new File(mURL.toString());

      return file.isDirectory();
   }

   /**
    * <p>Copy the the file being pointed to by the current URL to destination
    * file.</p>
    *
    * @param dest - store the contents of URL to this destination file.
    */
   public void putFromFile(File dest) { dest = new File(mURL.getFile()); }


   // FUTURE:  what to do with metadata?  This method was commented for now.
   /*
    * Same as putFromFile. @param  dest  - store the contents of URL to this
    * destination file. @param  nsp   - property including namespace and
    * property key. @param  value - property value.
    */
   //  public void putFromFileWithProp(File dest, NSProperty nsp, String value)
   // throws Exception {              this.putFromFile(dest);               }


   /**
    * putFromStream.
    *
    * <p>put the given InputStream into the URL being pointed to by the current
    * LocalDataObjectProxy.</p>
    *
    * @param  is - InputStream to be put into current URL.
    *
    * @throws DataObjectProxyException
    */
   public void putFromStream(InputStream is) throws DataObjectProxyException {

      try {
         File file = new File(mURL.toString());
         FileWriter fw = new FileWriter(file);
         BufferedReader reader = new BufferedReader(new InputStreamReader(is));

         while (is.available() != 0) {
            String s = reader.readLine();
            fw.write(s);
         }

         reader.close();
         fw.close();
      } catch (IOException ioe) {
         handleExceptions(ioe);
      }
   }

   /**
    * Get a new DataObjectProxy represents the new URL.
    *
    * @param  newURL URL to be used to create a DataObjectProxy.
    *
    * @return a DataObjectProxy.
    */
   public DataObjectProxy resetDataObjectProxy(URL newURL) {
      return resetDataObjectProxy(newURL, mUsername, mPassword);
   }

   /**
    * Get a new DataObjectProxy based on the new URL, username and password.
    *
    * @param  newURL  - URL to be used to create a DataObjectProxy.
    * @param  newUser - username to access the URL.
    * @param  newPass - password to access the URL.
    *
    * @return DataObjectProxy.
    */
   public DataObjectProxy resetDataObjectProxy(URL newURL, String newUser,
                                               String newPass) {
      return DataObjectProxyFactory.getDataObjectProxy(newURL, newUser,
                                                       newPass);
   }

   /**
    * Search metadata.  Not defined for local files (yet?)
    *
    * @return null.
    *
    * @throws DataObjectProxyException Always throws DataObjectException
    */
   public Object searchMeta() throws DataObjectProxyException {
      throw new DataObjectProxyException("metadata not supported for local files");
   }

   /**
    * <p>Resetting password for localDataObjectProxyImpl is probably not
    * necessary. This method would not do anything.</p>
    *
    * @param pass - password to be used.
    */
   public void setPassword(String pass) { }

   /**
    * Reset the username.
    *
    * @param s - username to be used.
    */
   public void setUsername(String s) { mUsername = s; }

} // end class LocalDataObjectProxyImpl
