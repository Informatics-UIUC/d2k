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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    */
   private String formatModificationDate(Date when) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      sdf.setTimeZone(TimeZone.getDefault());

      return sdf.format(when);
   }

   /**
    * Get a list of all the descendants of a directory.
    *
    * @param  depth The depth to descend, currently 1 or infinite.
    * @param  url   The root of the descent.
    * @param  dirs  include directories in outptu
    *
    * @return A list of all the children.
    *
    * @throws DataObjectProxyException
    */
   private Vector getChildrenURLs(int depth, URL url)
      throws DataObjectProxyException {
	   Vector ret = new Vector();

      if (isCollection()) {
	  
    	  ret.add(url);	

         File dir = new File(url.getPath());
         File[] relFileNames = dir.listFiles();

         // For depth_1
         if (depth == DataObjectProxy.DEPTH_1) {

            for (int j = 0; j < relFileNames.length; j++) {

               try {
            	   URL turl = (URL)relFileNames[j].toURL();		
            	   ret.add(turl);
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
                    	 URL turl = (URL)subdir.elementAt(k);
                    	 ret.add(turl);
                       }
                  } catch (Exception e) {
                     this.handleExceptions(e);
                  }
               } else {

                  try {
                	  URL turl = (URL)relFileNames[j].toURL();
                 	ret.add(turl);
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
    * Description of method readTheFile.
    *
    * @param  localpath Description of parameter localpath.
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   private void readTheFile(String localpath) throws DataObjectProxyException {
      
     try {
         String rpath = mURL.getPath();
         BufferedInputStream isr = new BufferedInputStream(new FileInputStream(rpath));
         BufferedOutputStream osr = new BufferedOutputStream(new FileOutputStream(localpath));
         byte b[] =new byte[4096];
         while (isr.available() > 0) {
             	int howmany = isr.read(b); 
                 osr.write(b,0,howmany);  
          }
          osr.close();
          isr.close();
      } catch (Exception e) {
         this.handleExceptions(e);
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
	      File file = null;
	   file = new File(this.getURL().getPath());

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
        	 return this.resetDataObjectProxy(file.toURL());
             }

         file.mkdirs();

         if (file.exists()) {
        	 return this.resetDataObjectProxy(file.toURL());
             }
      } catch (MalformedURLException mfu) {
         throw new DataObjectProxyException(mfu);
      }

      return this;
   } // end method createCollection

      /* ???
    * Copy all the files from the source to the destination.
    *
    * @param  wheretosave : A local directory to store the downloaded files
    *                     fromdop: A DataObjectProxy pointing to a collection
    *                     depth: A integer to indicate how you would like to
    *                     download the directory. There are only two valid
    *                     values for depth: DataObjectProxy.DEPTH_1: Only
    *                     download the files under the collection, no sub
    *                     directories DataObjectProxy.DEPTH_INFINITY: download
    *                     all of the files and subdirectories
    * @param  depth       Description of parameter depth.
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   
   
   private void createPath(URL pu, String rest,boolean includelast) throws DataObjectProxyException {

	   String pt = pu.getPath();
       pt = new String(pt+"/"+rest);
       if (!includelast) {
       pt = pt.substring(0, pt.lastIndexOf('/'));
       }   
        File ff = new File(pt);
        if (!ff.exists()) {

           boolean success = ff.mkdirs();

           if (!success) {
              throw new DataObjectProxyException("Failed to create directory " +
                                                 pt);
           }
        }     
   }
 
   /**
    * Get he URLs for a given depth.
    *
    * @param  depth Get the descendents to depth 0, 1, or infitinty.
    *
    * @return A list of relative paths from the root, one for each descendent.
    *
    * @throws DataObjectProxyException
    */
   public Vector getChildrenURLs(int depth) throws DataObjectProxyException { 
	   return this.getChildrenURLs(depth, mURL);
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
    * Description of method getLocalFile.
    *
    * @return Description of return value.
    */
   public File getLocalFile() {
      File f = null;

      try {
         f = new File(mURL.toURI().getPath());
      } catch (URISyntaxException ue) { }

      return f;
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
    * Get the value of the Etag.
    *
    * <p>For a local file, this is the modification time.</p>
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
    * Get the file the LocalDataObjectProxyImpl points to, if it doesn't exist,
    * create a new file using the path and file name given by the url being
    * pointed to by the current LocalDataObjectImpl.
    *
    * <p>In this version, when this method is called for a file does not exist,
    * an empty file will be created.</p>
    *
    * <p>In future versions, this behavior may be changed.</p>
    *
    * @param  dest Description of parameter dest.
    *
    * @return File The file being pointed to by the current LocalDataObjectImpl
    *
    * @throws DataObjectProxyException
    */
   public File initLocalFile(File dest) throws DataObjectProxyException {
      boolean doCreate = true; // This may become a parameter.
      File file = dest;
      if (file == null) {
    	  try {
    		  file = new File(mURL.toURI().getPath());
    	  } catch (URISyntaxException ue) { }
      }
      if (file != null) {

         try {
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
      
   } else {
       throw new DataObjectProxyException("No local path specified.");
   }

      return file;
   } // end method initLocalFile

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
    * Load a file from store. Does nothing for local files.
    *
    * @param  dest Description of parameter dest.
    *
    * @throws DataObjectProxyException
    */
   // public void loadFile() throws DataObjectProxyException { return; }

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
    	  File file = new File(mURL.getPath());
            BufferedInputStream isr = new BufferedInputStream(is);
            BufferedOutputStream osr = new BufferedOutputStream(new FileOutputStream(file));
            byte b[] =new byte[4096];
            while (isr.available() > 0) {
                	int howmany = isr.read(b); 
                    osr.write(b,0,howmany);  
             }
             osr.close();
             isr.close();
        	
      } catch (IOException ioe) {
         handleExceptions(ioe);
      }
   }

   /**
    * Actually the method is copying the file refered by mURL to the given
    * localpath.
    *
    * @param  dest the destination to copy to.
    *
    * @return actually the method is copying the file refered by mURL to the
    *         given localpath.
    *
    * @throws DataObjectProxyException
    */
   public File readFile(File dest) throws DataObjectProxyException {
      if (dest == null) {
         File f = null;

         try {
            f = new File(mURL.toURI().getPath());
         } catch (URISyntaxException ue) { }

         return f;
         // throw new DataObjectProxyException("Destination is null");
      }
      File lf = initLocalFile(dest);
      this.readTheFile(lf.getAbsolutePath());

      return lf;
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
	       
   if (
       depth != DataObjectProxy.DEPTH_1 &&
          depth != DataObjectProxy.DEPTH_INFINITY) {
      throw new DataObjectProxyException("Depth value is not valid");
   }
  
   // 1. create the first directory, if needed
   String firstdir = srcdop.getResourceName();
       
   DataObjectProxy dop2 = DataObjectProxyFactory.getDataObjectProxy(this.getURL(),
                                                    this.getUsername(),
                                                 this.getPassword());
   
   DataObjectProxy dop3 = dop2.createCollection(firstdir);

   String parenturl = dop3.getURL().toString(); 
   // get the children in the source area
   Vector childrenURLs = srcdop.getChildrenURLs(DataObjectProxy.DEPTH_1);

   DataObjectProxy childdop;
   String tempurl;

   DataObjectProxy tempdop = this.resetDataObjectProxy(this.getURL());
   URL childurl = null;
 
   try {

      for (int i = 0; i < childrenURLs.size(); i++) {
         childurl = new URL(childrenURLs.elementAt(i).toString());
   
         childdop = srcdop.resetDataObjectProxy(childurl);
  
         if (!(childdop.isCollection())) {
        	 String childname = childurl.getPath();
             if (childname.contains("/")) {         
             	childname = childname.substring(childname.lastIndexOf('/'));
             }

             tempurl = parenturl + "/" + childname;;
             
             tempdop = this.resetDataObjectProxy(new URL(tempurl));
             
          	InputStream is = childdop.getInputStream();
          	tempdop.putFromStream(is);
         } else {
         	
         	if (depth == DataObjectProxy.DEPTH_1) {
         		continue;
         	}
        	      	
         	if (srcdop.getURL().sameFile(childdop.getURL())) {
         		continue;
         	}
  
         	tempdop = this.resetDataObjectProxy(new URL(parenturl));
         	
         	tempdop.uploadDir(childdop,depth);
         }

      }

   } catch (Exception e) {
      this.handleExceptions(e);
   }

} // end method uploadDir

   public void downloadDir(DataObjectProxy wheretosave, int depth)
   throws DataObjectProxyException {
	   	DataObjectProxy srcdop = this.resetDataObjectProxy(this.getURL());
	   wheretosave.uploadDir(srcdop, depth);
   }
   
   public void downloadDir(URL wheretosave, int depth)
   throws DataObjectProxyException 
   {
	   DataObjectProxy destdop = DataObjectProxyFactory.getDataObjectProxy(wheretosave);
	   
	   this.downloadDir(destdop, depth);
   }
} // end class LocalDataObjectProxyImpl
