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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;


/**
 * LocalDataObjectProxyImpl handles access to a local file. Users can read file
 * or InputStream from, write file or InputStream to the URL with <code>
 * file</code> as protocol.
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
    * Format a date in to a standard string.
    *
    * @param  when The Data object.
    *
    * @return The data in a string.
    *
    */
   private String formatModificationDate(Date when) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      sdf.setTimeZone(TimeZone.getDefault());

      return sdf.format(when);
   }

   /**
    * Get a list of all the descendants of a directory.
    *
    * @param  depth   The depth to descend, currently 1 or infinite.
    * @param  url The root of the descent.
    *
    * @return A list of all the children.
    *
    * @throws DataObjectProxyException 
    */
   private Vector getChildrenURLs(int depth, URL url)
      throws DataObjectProxyException {
      Vector ret = new Vector();

      if (isCollection()) {

         // For depth_0
         ret.add(url);

         File dir = new File(url.getFile());
         File[] relFileNames = dir.listFiles();

         // For depth_1
         if (depth == DataObjectProxy.DEPTH_1) {

            for (int j = 0; j < relFileNames.length; j++) {

               try {
                  ret.add(relFileNames[j].toURL());
               } catch (Exception e) {
                  this.handleExceptions(e);
               }
            }
         }

         // For depth_infinity
         if (depth == DataObjectProxy.DEPTH_INFINITY) {

            for (int j = 0; j < relFileNames.length; j++) {

               if (relFileNames[j].isDirectory()) {

                  try {

                    Vector subdir =
                        this.getChildrenURLs(DataObjectProxy.DEPTH_INFINITY,
                                             relFileNames[j].toURL());

                     for (int k = 0; k < subdir.size(); k++) {
                        ret.add(subdir.elementAt(k));
                     }
                  } catch (Exception e) {
                     this.handleExceptions(e);
                  }
               } else {

                  try {
                     ret.add(relFileNames[j].toURL());
                  } catch (Exception e) {
                     this.handleExceptions(e);
                  }
               }
            }
         } // end if
      } else {
         ret.add(mURL);
      }

      return ret;


   } // end method getChildrenURLs

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
    * Give the resource name not including the path.
    *
    * @return give the resource name not including the path.
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   protected String getResourceName() throws DataObjectProxyException {
      File file = new File(this.getURL().getFile());

      return file.getName();
   }

   /**
    * No action required to clean up a local file.
    */
   public void close() { }

   /**
    * Create a directory at path.
    *
    * @param  path a relative path (relative to current URL).
    *
    * @return DataObjectProxy for the new object.
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   public DataObjectProxy createCollection(String path)
      throws DataObjectProxyException {

      if (!mURL.getProtocol().equals("file")) {
         throw new DataObjectProxyException("createCollection: not local url");
      }

      File file =
         new File(File.separator +
                  mURL.getHost() + File.separator +
                  mURL.getPath() + File.separator + path);


      try {

         if (file.exists()) {
            return this.resetDataObjectProxy(file.toURI().toURL());
         }

         file.mkdirs();

         if (file.exists()) {
            return this.resetDataObjectProxy(file.toURI().toURL());
         }
      } catch (MalformedURLException mfu) {
         throw new DataObjectProxyException(mfu);
      }

      return this;
   } // end method createCollection

   /**
    * Copy all the files from the source to the destination.
    *
    * @param   wheretosave : A local directory to store the downloaded files
    *           fromdop: A DataObjectProxy pointing to a collection depth: A
    *           integer to indicate how you would like to download the
    *           directory. There are only two valid values for depth:
    *           DataObjectProxy.DEPTH_1: Only download the files under the
    *           collection, no sub directories DataObjectProxy.DEPTH_INFINITY:
    *           download all of the files and subdirectories
    * @param  depth Description of parameter depth.
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   public void downloadDir(URL wheretosave, int depth)
      throws DataObjectProxyException {

      if (
          depth != DataObjectProxy.DEPTH_1 &&
             depth != DataObjectProxy.DEPTH_INFINITY) {
         throw new DataObjectProxyException("Depth value is not valid");
      }

      URL curdir = wheretosave;
      Vector childurls = new Vector();
      DataObjectProxy tempdop = this;

      try {

         /*
          * If it is not a collection, go ahead and download the file
          */
         if (!tempdop.isCollection()) {
        	  URL desurl =
               new URL(curdir.toString() + "/" + tempdop.getResourceName());
           tempdop.readFile(new File(desurl.getFile()));
         }
         /*
          * If it is a collection, get the children URLs Note: The childrenURLs
          * probably will include the parent directory, in order to avoid a dead
          * loop, we will first check if the dop is the same as the parent dop
          */
         else {

            /* DataObjectProxy.DEPTH_1 is correct
             * First we get the direct children URLs If the depth is infinity,
             * we will loop through the whole directory */
            childurls = tempdop.getChildrenURLs(DataObjectProxy.DEPTH_1);
            curdir = new URL(wheretosave + "/" + tempdop.getResourceName());
                 
            File ff = new File(curdir.getFile());
            
            if (!ff.exists()) {

            	boolean success = ff.mkdir();

            	if (!success) {
            		throw new DataObjectProxyException("Failed to create directory " +
                                                  curdir);
            	}
            }

            for (int i = 0; i < childurls.size(); i++) {

               // Reset the dop point to each child urls
               tempdop =
                  tempdop.resetDataObjectProxy(new URL(childurls.elementAt(i)
                                                                .toString()));

               // Check if it is pointing to src collection
               if (!(tempdop.isCollection())) {
                  URL fileurl =
                     new URL(curdir.toString() + "/" +
                             tempdop.getResourceName());
                  tempdop.readFile(new File(fileurl.getFile()));
               } else {
                  String thisurl = this.getURL().toString();

                  if (
                      !tempdop.getURL().toString().equals(thisurl) &&
                         depth == DataObjectProxy.DEPTH_INFINITY) {
                     tempdop.downloadDir(curdir, depth);
                  }
               }
            }
         } // end if
      } catch (Exception e) {
         this.handleExceptions(e);
      }
   } // end method downloadDir


   /**
    * Get he URLs for a given depth.
    *
    * @param  depth Get the descendents to depth 0, 1, or innitinty.
    *
    * @return A list of relative paths from the root, one for each descendent.
    *
    * @throws DataObjectProxyException 
    */
   public Vector getChildrenURLs(int depth) throws DataObjectProxyException {
      return this.getChildrenURLs(depth, mURL);
   }

   
   private void readTheFile(String localpath) throws DataObjectProxyException {
      String line;

      try {
         // Transfer bytes from in to out
         BufferedReader reader =
            new BufferedReader(new FileReader(mURL.toURI().getPath()));
         BufferedWriter writer = new BufferedWriter(new FileWriter(localpath));

         while ((line = reader.readLine()) != null) {
            writer.write(line);
         }

         writer.close();
         reader.close();
      } catch (Exception e) {
         this.handleExceptions(e);
      }

   }

   /**
    * Get locally cached file.
    * <p>If destination is null, then use the main file.
    * <p>Else, will copy to a new file.
    * 
    * @param dest The destination file to copy to.
    *
    * @return locally cached file
    *
    * @throws DataObjectProxyException
    * @throws Exception                if the local file can not be created.
    */
   public File readFile(File dest) throws DataObjectProxyException {

	      if (dest == null) {
	         File f = null;

	         try {
	            f = new File(mURL.toURI().getPath());
	         } catch (URISyntaxException ue) { }

	         return f;
	        }

	      File lf = initLocalFile(dest);
	      this.readTheFile(lf.getAbsolutePath());

	      return lf;
	   }
   
   public File getLocalFile() {
	   File f = null;
   
	   try {
	    f = new File(mURL.toURI().getPath());
	   } catch (URISyntaxException ue) {
		   
	   }
	   return f;
   }
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
   public File initLocalFile(File dest) throws DataObjectProxyException {
      boolean doCreate = true; // This may become a parameter.
      File file = dest;
      try {
        // file = new File(mURL.toURI().getPath());
         // If file doesn't exist, create a empty file based on url.
         if (!file.exists()) {
        	 if (doCreate) {
               File p = file.getParentFile();
               p.mkdirs();
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
    * Get the value of the Etag
    * <p>
    * For a local file, this is the modification time.
    *
    * @return The etag value
    *
    * @throws DataObjectProxyException 
    */
   public String getTag() throws DataObjectProxyException {

      /* If URL refers to a local file, we think
       * the content is changed if the modification date is changed, not changed
       * if the modification date is same as before.
       */
      return this.getURLLastModified();
   }

   /**
    * Get the tiem the file was last modified.
    *
    * @return The modified time (in a formated string).
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   public String getURLLastModified() throws DataObjectProxyException {
      File f = null;

      try {
         f = new File(mURL.toURI().getPath());
      } catch (Exception e) {
         this.handleExceptions(e);
      }

      Date when = new Date(f.lastModified());

      return this.formatModificationDate(when);
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
      File file = new File(mURL.getPath());
      
      return file.isDirectory();
   }

   /**
    * Load a file from store.  Does nothing for local files.
    *
    * @throws DataObjectProxyException
    */
 //  public void loadFile() throws DataObjectProxyException { return; }

   /**
    * <p>Copy the the file being pointed to by the current URL to destination
    * file.</p>
    *
    * @param  dest - store the contents of URL to this destination file.
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   public void putFromFile(File dest) throws DataObjectProxyException {
      // Do nothing ?
   }


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
    * Remove directory refered by this DOP.
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   public void removeDirectory() throws DataObjectProxyException {
      DataObjectProxy tempdop = this;

      if (!(this.isCollection())) {
         boolean success = new File(this.getURL().getFile()).delete();

         if (!success) {
            throw new DataObjectProxyException("Delete File " +
                                               this.getURL().getFile() +
                                               " Failed.");
         }
      } else {
         Vector childurls = this.getChildrenURLs(DataObjectProxy.DEPTH_1);

         for (int i = 0; i < childurls.size(); i++) {

            try {
               tempdop =
                  tempdop.resetDataObjectProxy(new URL(childurls.elementAt(i)
                                                                .toString()));
            } catch (MalformedURLException me) {
               this.handleExceptions(me);
            }

            if (!tempdop.isCollection()) {
               boolean success = new File(tempdop.getURL().getFile()).delete();

               if (!success) {
                  throw new DataObjectProxyException("Delete File " +
                                                     tempdop.getURL().getFile() +
                                                     " Failed.");
               }
            } else {

               if (!tempdop.getURL().equals(this.getURL())) {
                  tempdop.removeDirectory();
               }
            }
         }

         boolean success = new File(this.getURL().getFile()).delete();

         if (!success) {
            throw new DataObjectProxyException("Delete File " +
                                               tempdop.getURL().getFile() +
                                               " Failed.");
         }
      } // end if

   } // end method removeDirectory

   
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
    * Search metadata. Not defined for local files (yet?)
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
    * See reason in webdavObjectProxyImpl.
    *
    * @param user Description of parameter user.
    * @param pass Description of parameter pass.
    */
   public void setUserInfo(String user, String pass) {
      this.mUsername = user;
      this.mPassword = pass;
   }

   /**
    * Reset the username.
    *
    * @param s - username to be used.
    */
   public void setUsername(String s) { mUsername = s; }

   /**
    * For local DataObjectProxy, upload is same as upload except we need to
    * reverse the src and destination.
    *
    * @param  srcdop Source to copy
    * @param  depth  Depth of the copy, currently 1 or infinite.
    *
    * @throws DataObjectProxyException 
    */
   public void uploadDir(DataObjectProxy srcdop, int depth)
      throws DataObjectProxyException {

      URL where = this.getURL();

      try {
         srcdop.downloadDir(where, depth);
      } catch (Exception e) {
         this.handleExceptions(e);
      }

   }
} // end class LocalDataObjectProxyImpl
