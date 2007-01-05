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

import ncsa.d2k.core.D2KLogger;
import org.scidac.cmcs.dsmgmt.dsi.DSI;
import org.scidac.cmcs.dsmgmt.dsi.DSIProperty;
import org.scidac.cmcs.dsmgmt.dsi.StatusException;
import org.scidac.cmcs.dsmgmt.util.ResourceList;
//import org.scidac.cmcs.util.LoggerUtils;
import org.scidac.cmcs.util.NSProperty;

//import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.*;


/**
 * WebdavDataObjectProxyImpl manages access to files through the http/https
 * protocol. Users can read file or InputStream from, write file or InputStream
 * to the url.
 *
 * <p>This class is built on the Data Storage Interface (DSI) from the SCIDAC
 * CMCS project.</p>
 *
 * <p>See <u>http://collaboratory.emsl.pnl.gov/</u></p>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class WebdavDataObjectProxyImpl extends DataObjectProxy {

   //~ Instance fields *********************************************************

   /** The read cache manager. */
   private DataObjectCacheManager cache = null;

   /** The locally cached file. */
   private File localCopy = null;

   /** Internal variable: false will disable the read cache. */
   private boolean usingCache = true;

   /** The DSI object, implements access to WebDAV. */
   protected DSI mDSI = null;

   //~ Constructors ************************************************************

   /**
    * WebdavDataObjectProxyImpl.
    *
    * <p>Creates a new WebdavDataObjectProxyImpl object based on the given
    * URL.</p>
    *
    * @param url - the URI to a WebDAV server.
    */
   public WebdavDataObjectProxyImpl(URL url) { this(url, "", ""); }

   /**
    * WebdavDataObjectProxyImpl.
    *
    * <p>Creates a new WebdavDataObjectProxyImpl object.</p>
    *
    * @param url      - the URI to a WebDAV server.
    * @param username - username to access the server.
    * @param password - password to access the server.
    */
   public WebdavDataObjectProxyImpl(URL url, String username, String password) {
      try{
    	  cache = DataObjectCacheManagerFactory.getCacheManager();
      } catch (DataObjectProxyException dopex) {
    	  String msg = "Error initializing Cache Manager";
    	  D2KLogger.logger.fatal(msg,dopex);
      }
      mURL = url;
  
      
      org.apache.log4j.Level ll = D2KLogger.logger.getEffectiveLevel();
      java.util.logging.Level l = convertLevel(ll);
      setJavaUtilLogger(l);
      
   
      
      
      if (!username.equals("")) {
         this.setUsername(username);
      }

      if (!password.equals("")) {
         this.setPassword(password);
      }

      if (!this.getUsername().equals("")) {
         mDSI = new DSI(url.toString(), this.getUsername(), this.getPassword());
      } else {
         mDSI = new DSI(url.toString());
      }
     
   }
   
   private  void setJavaUtilLogger(java.util.logging.Level l)
   {
	   LogManager lm = LogManager.getLogManager();
	   
	   java.util.Enumeration e = lm.getLoggerNames();
	   
	   while (e.hasMoreElements()) {
		   Logger jlogger = lm.getLogger( ( String) e.nextElement() );
		   jlogger.setLevel(l);
	   }

   
   }
   
  
   
   private java.util.logging.Level convertLevel(org.apache.log4j.Level lin) {
	   if (lin == null) {
		   // Fatal
		   return null;
	   }
	   if (lin == org.apache.log4j.Level.DEBUG) {
		   return java.util.logging.Level.FINEST;
	   } else if (lin == org.apache.log4j.Level.WARN) {
		   return java.util.logging.Level.WARNING;
	   } else if (lin == org.apache.log4j.Level.INFO) {
		   return java.util.logging.Level.INFO;
	   } else if (lin == org.apache.log4j.Level.ERROR) {
		   return java.util.logging.Level.SEVERE;
	   }   else if (lin == org.apache.log4j.Level.FATAL) {
			   return java.util.logging.Level.SEVERE;
	   } else if (lin == org.apache.log4j.Level.OFF) {
		   return java.util.logging.Level.OFF;   
	   } else if (lin == org.apache.log4j.Level.ALL) {
		   return java.util.logging.Level.ALL;
	   } else {
		   return java.util.logging.Level.INFO;
	   }
   }

   //~ Methods *****************************************************************

   /**
    * Check if the URL ends in a '/'
    *
    * <p>If so, the Web server will be confused if you try to put a file to the
    * URL.
    *
    * @return Description of return value.
    */
   private boolean checkLastComp()  {
      try {
         URL pu = new URL(mDSI.getURL());
         String pt = pu.getPath();
         if (pt.contains("/")) {
            pt = pt.substring(pt.lastIndexOf('/') + 1);

            if (pt.length() == 0) {
            	return false;
            }
         }
      } catch (MalformedURLException mfe) {
    	  return false;
      }
      return true;
   }

   /*
    * Check if userID and password are necessary to access the url if yes,
    * return true; if no, return no; How ?
    */
/*
 * public boolean checkAuth() {   boolean ret = false;
 *
 * return ret; }
 */
   /**
    * Delete all of the temp files created during the process.
    * <p>
    * When 
    */
   private void cleanUp() {

      if (usingCache) {
    	  /*
    	   * When using the cache, call the cache manager to
    	   * clean out the cache areas.
    	   */
         if (tempDataDir == null) {

            try {
               setTempDataDir();
            } catch (DataObjectProxyException d) { }
         }

         cache.cleanup(tempDataDir);

         return;
      }

      /* Non cache version called only if 'useCache' is off */
      /*
       * Delete the files inside temp data directory. For all of the files which
       * can not be deleted, delete then on exit.
       */
      if (tempDataDir != null) {
         boolean isTempDataDel = deleteDir(tempDataDir);

         if (!isTempDataDel) {
            File[] children = tempDataDir.listFiles();

            for (int i = 0; i < children.length; i++) {
               children[i].delete();
            }
         }

      }
   } // end method cleanUp

   /**
    * Create all the components of a path.
    * <p>
    * Given a source URL, create all the components of the
    * path at the destination.
    *
    * @throws DataObjectProxyException  */

   private void createPath(URL pu, String rest, boolean includelast) throws DataObjectProxyException {

	   	      // get the path to the file
	   	      try {

	   	         String pt = pu.getPath();
	   	         if (!includelast) {
	   	        	 pt = pt.substring(0, pt.lastIndexOf('/'));
	   	         }
	   	         if (!rest.equals("")) {
	   	        	pt = new String(pt+"/"+rest);
	   	         }
	   	         
	   	      URL pcheck =
	               new URL(pu.getProtocol(), pu.getHost(), pu.getPort(), "/" +
	                       pt);
	         DSI dcheck = null;

	            if (this.getUsername().equals("")) {
	               dcheck = new DSI(pcheck.toString());
	            } else {
	               dcheck =
	                  new DSI(pcheck.toString(), this.getUsername(),
	                          this.getPassword());
	            }
	            if (dcheck.exists()) {
	            	return;
	            }
	   	         String[] comps = pt.split("/");
	   	         String path = new String("");
	   	         String ppath = new String("");
	   	         String newpath;
	   	         
	   	         for (int i = 0; i < comps.length; i++) {
	   	            if (comps[i] == null || comps[i].equals("")) {
	   	               continue;
	   	            }

	   	            path = ppath + comps[i];

	   	            URL pp =
	   	               new URL(pu.getProtocol(), pu.getHost(), pu.getPort(), "/" +
	   	                       path);

	   	            DSI d2 = null;

	   	            if (this.getUsername().equals("")) {
	   	               d2 = new DSI(pp.toString());
	   	            } else {
	   	               d2 =
	   	                  new DSI(pp.toString(), this.getUsername(),
	   	                          this.getPassword());
	   	            }
	   	            if (!d2.exists()) {
	   	            	
	   	               URL root =
	   	                  new URL(pu.getProtocol(), pu.getHost(), pu.getPort(), "/" +
	   	                          ppath);
	   	               
	   	              DSI rootdsi =null;
	   	              if (this.getUsername().equals("")) {
	   	            	  rootdsi = new DSI(root.toString());
	   	              } else {
	   	            	  rootdsi =
	   	                  new DSI(root.toString(), this.getUsername(),
	   	                          this.getPassword());
	   	              }
	   	                                                    
	   	              try {
	   	            	  newpath = rootdsi.makeCollection(comps[i]);
	   	              } catch (StatusException se) {
	   	       	   		if (se.getStatusCode() == 405 || se.getStatusCode() == 409) {
	   	       	   			/* already exists? */
	   	       	   			System.out.println("405 or 409 "+se.getStatusCode());  
	   	       	   		}
	   	       	   		se.printStackTrace();
	   	       	   		handleExceptions(se);
	   	              }

	   	            }

	   	            ppath = path + "/";
	   	         } // end for
	   	      } catch (MalformedURLException mfe) {
	   	         this.handleExceptions(mfe);
	   	      } catch (StatusException se) {
	   	         this.handleExceptions(se);
	   	      }
	   	   } 
   /**
    * <p>Create a temporary local file in the specified directory and save the
    * path of this temporary file into a Vector.</p>
    *
    * @throws IOException              If a file could not be created.
    * @throws DataObjectProxyException
    *
    * @see    setTempDataDir.
    */
   private void createTempFile() throws DataObjectProxyException {
	  
      if (tempDataDir == null) {
         setTempDataDir();
      }

      try {
         localCopy = File.createTempFile("d2k", ".out", tempDataDir);
      } catch (IOException ioe) {
         handleExceptions(ioe);
      }

      if (tempFilesCreated == null) {
         tempFilesCreated = new Vector();
      }

      /*
       * For future use, save the path of the newly created files into a
       * Vector.  Not needed when using the cache.
       */
      tempFilesCreated.add(localCopy.getAbsolutePath());
   }


   /**
    * <p>Delete all of its children resources of a directory. Only used to clean
    * up temporary files.</p>
    *
    * @param  dir - the directory to be cleaned.
    *
    * @return true if all of the children resources are deleted successfully,
    *         false otherwise.
    */
   private boolean deleteDir(File dir) {
      boolean ret = true;

      if (dir.isDirectory()) {
         String[] children = dir.list();

         for (int i = 0; i < children.length; i++) {
            boolean success = deleteDir(new File(dir, children[i]));

            if (!success) {
               ret = false;
            }
         }
      } else {
         ret = false;
      }

      return ret;
   }


   /**
    * Format a Date in to a string.
    *
    * @param  when The date.
    *
    * @return String
    *
    * @throws DataObjectProxyException  */
   private String formatModificationDate(Date when)
      throws DataObjectProxyException {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      sdf.setTimeZone(TimeZone.getDefault());

      return sdf.format(when);
   }

   /**
    * Get the value of the specified property on the current url.
    *
    * @param  prop - the property of interest including namespace
    *
    * @return a possibly null DSIProperty object
    *
    * @throws DataObjectProxyException
    */
   private Object getMeta(NSProperty prop) throws DataObjectProxyException {
      Object ret = null;
      try {
         ret = mDSI.getMetaData(prop);
      } catch (StatusException se) {
         handleExceptions(se);
      }

      return ret;
   }

   /**
    * Handles exceptions.
    *
    * @param  ex Description of parameter ex.
    *
    * @throws DataObjectProxyException
    */
   private void handleExceptions(Exception ex) throws DataObjectProxyException {

      /*
       * HttpException method getReasonCode() is deprecated. DSI
       * handleExceptions() method is using this deprecated method. The
       * following se.getStatusCode() might not give correct status code because
       * the usage of this deprecated method.
       */
      if (ex instanceof StatusException) {
         StatusException se = (StatusException) ex;
         Throwable theCause = se.getCause();
         if (se.getStatusCode() == 200) {
        	 throw new DataObjectProxyException("Unknown error (200), possibly authorization failed");
         }
         else if (theCause != null && theCause instanceof StatusException) {

            // extract the encased exception
            StatusException se2 = (StatusException) theCause;
            if (se2.getStatusCode() == 401) {
               throw new DataObjectProxyException("Unauthorized (401)");
            } else if (se2.getStatusCode() == 200) {
            	throw new DataObjectProxyException("Unknown error (200), possibly authorization failed");
            }
         }
         throw new DataObjectProxyException("*****StatusException " +
                                            se.getStatusCode() + " " +
                                            se.getImprovedMessage());
      } else if (ex instanceof FileNotFoundException) {
         FileNotFoundException fe = (FileNotFoundException) ex;
         throw new DataObjectProxyException("*****FileNotFoundException " +
                                            fe.getLocalizedMessage());
      } else if (ex instanceof MalformedURLException) {
         MalformedURLException me = (MalformedURLException) ex;
         throw new DataObjectProxyException("******MalformedURLException " +
                                            me.getLocalizedMessage());
      } else {
    	  ex.printStackTrace();
         throw new DataObjectProxyException("******Unknown Exception " +
                                            ex.getLocalizedMessage());
      }
   } // end method handleExceptions

   /**
    * <p>Try to create a directory to store locally cached files in the users
    * working directory or users home directory. If users don't have writing
    * access to either of these two directories, throw a
    * DataObjectProxyException.</p>
    *
    * @throws DataObjectProxyException
    */
   private void setTempDataDir() throws DataObjectProxyException {
      File parentDir = new File(System.getProperty("user.dir"));

      if (parentDir.canWrite()) {
         tempDataDir = new File("d2kTempData");
      } else {
         parentDir = new File(System.getProperty("user.home"));

         if (parentDir.canWrite()) {
            tempDataDir = new File(parentDir + File.separator + "d2kTempData");
         } else {
            throw new DataObjectProxyException("Need writing access to \n" +
                                               System.getProperty("user.dir") +
                                               "\n or \n" +
                                               System.getProperty("user.home"));
         }

      }

      tempDataDir.mkdirs();
   }

   /**
    * Get the password to access the server.
    *
    * @return the current password to access the server.
    */
   protected String getPassword() {

      if (mPassword == null) {
         mPassword = "";
      }

      return mPassword;
   }

   /**
    * Give the resource name not including the path.
    *
    * @return give the resource name not including the path.
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   protected String getResourceName() throws DataObjectProxyException {
      Object rsrname = null;
      NSProperty prop = new NSProperty("DAV::displayname");

      DSIProperty val = (DSIProperty) this.getMeta(prop);
      String rn = "";
      rn =  val.getPropertyAsString();
      return rn;
     }

   /**
    * <p>Close DSI object, clean up the tempory files created, username and
    * password</p>
    */
   public void close() {

      if (mDSI != null) {
         mDSI.close();
      }

      mUsername = null;
      mPassword = null;
      cleanUp();
   }

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

   public DataObjectProxy createCollection(String relativePath)
   throws DataObjectProxyException {


	   createPath(this.getURL(),relativePath,true);
   
	   try {
		   URL newU = new URL(mDSI.getURL());

		   newU = new URL(mDSI.getURL()+"/"+relativePath);
		   return this.resetDataObjectProxy(newU,
                                       this.getUsername(),
                                       this.getPassword());
	   } catch (MalformedURLException mfe) {
		   handleExceptions(mfe);
   	 	}

	   return this;
	}

   /**
    * Download the current collection refered by this to local.
    *
    * @param   wheretosave : A local directory to store the downloaded files
    *           depth: A integer to indicate how you would like to download the
    *           directory. There are only two valid values for depth:
    *           DataObjectProxy.DEPTH_1: Only download the files under the
    *           collection, no sub directories DataObjectProxy.DEPTH_INFINITY:
    *           download all of the files and subdirectories
    * @param  depth Description of parameter depth.
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */

   public void downloadDir(DataObjectProxy wheretosave, int depth)
   throws DataObjectProxyException {
	   	DataObjectProxy srcdop =  this.resetDataObjectProxy(this.getURL());
	   	wheretosave.uploadDir(srcdop, depth);
   } // end method downloadDir
   
   public void downloadDir(URL wheretosave, int depth)
   throws DataObjectProxyException {
	   /* this method will fail if the destination requires user/password */
	   DataObjectProxy destdop = DataObjectProxyFactory.getDataObjectProxy(wheretosave);
	   
	   this.downloadDir(destdop, depth);
}// end method downloadDir
   
 
   private Vector getChildrenURLs(int depth, URL url) throws DataObjectProxyException {
	   Vector childrenURLs = new Vector();
	   Vector childrenPaths = new Vector();
	   Vector properties = new Vector();

	   DataObjectProxy tempdop = this.resetDataObjectProxy(url);
	  
	   DSI lDSI = new DSI(url.toString());
	   	   
	   /*
	    * Get the children urls of input url with depth 1
	    */
	   properties.add(new NSProperty("DAV::displayname"));
	   ResourceList rl = null;

	   try {
		   /*  the boolean is for setting separateParent
		    * But it seems not working
	          */
		   rl = lDSI.getResources(properties,DataObjectProxy.DEPTH_1, true);
	   } catch (StatusException e) {
	         this.handleExceptions(e);
	   }
	   rl.selectAll();
	   childrenPaths = rl.getSelectedFilePaths();
	   String server = rl.getServer();
	   
	   if (childrenPaths.size() == 0) {
		   /* some servers return empty list when no children */
		   return childrenURLs;
	   }
	   /* check for alternative behavior on servers */
	   String first = (String)childrenPaths.firstElement();
	   boolean hasServer = first.startsWith(server);
	   
	   /*  
	    * If depth is 1, DSI getResources method works fine
	    */
	   if (depth == DataObjectProxy.DEPTH_1) {
		   for (int i = 0; i < childrenPaths.size(); i++) {
			   URL utmp = null;
			   try { 
				   if (hasServer) {
					   utmp  = new URL((String)childrenPaths.elementAt(i));
				   } else {
				  utmp  = new URL(server+childrenPaths.elementAt(i));
				   }
			  }
			  catch(Exception ex){
				  this.handleExceptions(ex);
			  }
			  childrenURLs.add(utmp);
		   }
	   }
	   
	   /*
	    * If depth is infinity, DSI getResources method does not work for url 
	    * with more than 3 levels.
	    * 
	    * So, recurse to implementa a depth first list.
	    */
	   if (depth == DataObjectProxy.DEPTH_INFINITY) {
		   if(tempdop.isCollection()) {
			    childrenURLs.add(url);
			   		   
			   /*
			    * Loop through all of the children at this level.
			    */
			   URL walker=null;
			   for (int i = 0; i < childrenPaths.size(); i++) {
		    		  try {
		    			  if (hasServer) {
		    				  walker = new URL((String)childrenPaths.elementAt(i));
		    			  } else {
		    				  walker = new URL(server+childrenPaths.elementAt(i));  
		    			  }
		    			  /*
		    			   * If the path is not same as the input url
		    			  */
		    			  if(!(walker.toString().equals(url.toString()))) {
		    				  tempdop=tempdop.resetDataObjectProxy(walker);
		    			  }
		    		  }
		    		  catch(Exception ex){
		    			  this.handleExceptions(ex);
		    		  }
		    		  
		    		  /*
		    		   * If the walker is not a collection (directory), 
		    		   * just add url in childrenURLs
		    		   */
		    		  if(!tempdop.isCollection()) {
		    			  childrenURLs.add(tempdop.getURL());
		    		  }
		    		  
		    		  /*
		    		   * If the walker is a collection and not same as input url
		    		   * recursively call this method with walker as input url
		    		   */
		    		  else {
		    			  if(!(walker.toString().equals(url.toString()))) {
			    			  Vector subdir = this.getChildrenURLs(DataObjectProxy.DEPTH_INFINITY,walker);
			    			  for (int k = 0; k < subdir.size(); k++) {
			    				  if(!childrenURLs.contains(subdir.elementAt(k))) {
			    					  childrenURLs.add(subdir.elementAt(k));
			    				  }
			    			  }
			    		  }
		    		  }
			   }
		   }
	   }
 
	   return childrenURLs;
   }// end method getChildrenURLs
     
   /**
    * if the url is not pointing to a collection return the url itself if it is,
    * return the collection url and all of its children.
    *
    * @param  depth --- indicator of DSI.DEPTH i can be one of DSI.DEPTH_0: only
    *               the current url will be included
    *
    *               <p>DSI.DEPTH_1: The children files and subdirectories will
    *               be included, not the chidren of those subdirectories
    *               DSI.DEPTH_INFINITY: All of files including direct children
    *               and children of subdirectories</p>
    *
    * @return if the url is not pointing to a collection return the url itself
    *         if it is, return the collection url and all of its children.
    *
    * @throws DataObjectProxyException
    */
   public Vector getChildrenURLs(int depth) throws DataObjectProxyException {
	   return getChildrenURLs(depth,mURL);
   }
   
   
   /**
    * Add more DSI methods wrapper later.
    *
    * @param  localpath Description of parameter localpath.
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   public File readFile(File destination) throws DataObjectProxyException {
	   File lf = initLocalFile(destination);
	   loadFile();
	   return localCopy;
   }
   
   public File getLocalFile() {
	   return localCopy;
   }

   
   /**
    * Get a input stream to the current DSI object.
    *
    * @return Description of return value.
    *
    * @throws DataObjectProxyException
    */
   public InputStream getInputStream() throws DataObjectProxyException {
      InputStream ret = null;

      try {
         ret = mDSI.getDataSetAsFIS();
      } catch (StatusException e) {
         handleExceptions(e);
      }

      return ret;
   }

   /**
    * Get locally cached file.
    *
    * @return locally cached file
    *
    * @throws DataObjectProxyException
    * @throws Exception                if the local file can not be created.
    */
   public File initLocalFile(File dest) throws DataObjectProxyException {
	  if (dest != null) {
		  localCopy = dest;
		  if (!localCopy.exists()) {
			  try {
			       File p = localCopy.getParentFile();
			       if (!p.exists()) {
			    	   try {
			    	   p.mkdirs();
			    	   } catch (Exception io) {
			    		   //nothing
			    	   }
			       }
			       localCopy.createNewFile();
			  } catch (IOException ioe) {
				  handleExceptions(ioe);
			  }
		  }
		  return localCopy;
	  }
	  
      if (localCopy == null || (!localCopy.exists())) {
         createTempFile();
      }

      return localCopy;
   }

   /**
    * Get a InputStream from locally cached copy.
    *
    * @return a InputStream from locally cached copy
    *
    * @throws DataObjectProxyException
    * @throws Exception                Description of exception Exception.
    */
   public InputStream getLocalInputStream() throws DataObjectProxyException {
      InputStream is = null;
      String copyTo = null; // in this case, allocate a local temp file
      localCopy = cache.getCachedCopy(this, copyTo, true);

      try {
         is = new FileInputStream(localCopy);
      } catch (Exception e) {
         this.handleExceptions(e);
      }

      return is;
   }

   /**
    * Get the metadata of the current DSI object.
    *
    * @return a meatdata Hashtable
    *
    * @throws DataObjectProxyException
    */
   public Hashtable getMeta() throws DataObjectProxyException {
      Hashtable ret = null;

      try {
         ret = mDSI.getMetaData();
      } catch (StatusException se) {
         handleExceptions(se);
      }

      return ret;
   }

   /**
    * Get metadata.  
    *
    * @param  prop The NSProp
    *
    * @return the values.
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   public Object getMeta(Object prop) throws DataObjectProxyException {
      return getMeta((NSProperty) prop);
   }

   /**
    * Get the Etag from the server.
    * <p>
    * The Etag will change if the file is updated on the server.
    *
    * @return The tag.
    *
    * @throws DataObjectProxyException  */
   public String getTag() throws DataObjectProxyException {

      /* If URL refers to a local file, we think
       * the content is changed if the modification date is changed, not changed
       * if the modification date is same as before.
       */
      NSProperty propNamex = new NSProperty("DAV:", "getetag");
      DSIProperty val = (DSIProperty) this.getMeta(propNamex);
      String tag = val.getPropertyAsString();

      return tag;
   }

   /**
    * Get the modification date.
    * <p>
    * The data is not returned correctly from some servers.
    *
    * @return Teh modification date.
    *
    * @throws DataObjectProxyException */
   public String getURLLastModified() throws DataObjectProxyException {
      NSProperty propNamex = new NSProperty("DAV:", "getlastmodified");
      DSIProperty val = (DSIProperty) this.getMeta(propNamex);
      String modified = val.getPropertyAsString();

      return modified;

   }

   /**
    * Get the current user to access the current WebDataObjectProxy.
    *
    * @return username.
    */
   public String getUsername() {

      if (mUsername == null) {
         mUsername = "";
      }

      return mUsername;
   }

   /**
    * Test if the current DSI represents a collection or not.
    *
    * @return true if DSI represents a collection, false otherwise;
    *
    * @throws DataObjectProxyException
    */
   public boolean isCollection() throws DataObjectProxyException {
      boolean ret = false;

      try {
         ret = mDSI.isCollection();
      } catch (StatusException se) {
         handleExceptions(se);
      }

      return ret;
   }

   /*
    * Get the file from the remote server.
    *
    * @throws  */
   private void loadFile() throws DataObjectProxyException {
      try {

         if (localCopy == null) {
            createTempFile();
         }

         if (localCopy != null /* and not read yet */) {
            localCopy = mDSI.getDataSet(localCopy.getAbsolutePath());
         }
         /* *** */

      } catch (Exception e) {
         this.handleExceptions(e);
      }

   }

   /**
    * Put a local file to the url represented by the current DSI object.
    *
    * @param  file - local file to be put to url.
    *
    * @throws DataObjectProxyException
    */
   public void putFromFile(File file) throws DataObjectProxyException {
	   boolean doCreate = true;
	  try {

         if (!mDSI.exists()) {
            if (doCreate) {
            	createPath(this.getURL(),"",false);
            }
         }

         if (checkLastComp()) {
            mDSI.putDataSet(file);
         } else {
        	 /* All done--everything should be OK */
            //throw new DataObjectProxyException(mDSI.getURL() +
            }
      } catch (StatusException se) {
         this.handleExceptions(se);
      }
   }
   
   

   /**
    * <p>Put a local file to the url represented by the current DSI object Put
    * the specified properties to the current url and its parent url.</p>
    *
    * @param  is - local file to be put to url
    *
    * @throws DataObjectProxyException
    */
   // public void putFromFileWithProp(File file, NSProperty nsp,String value)
   // throws DataObjectProxyException {   try { //       mDSI.putDataSet(file);
   //        this.putMeta(nsp,value);
   // mDSI.setURL(DSI.splitUrl(mDSI.getURL())[0]);
   // this.putMeta(nsp,value);         mDSI.setURL(mURL.toString());   }   catch
   // (StatusException se){        this.handleExceptions(se);   }

   // }

   /**
    * <p>Put InputStream to the url being pointed to by the current
    * WebDataObjectProxyImpl.</p>
    *
    * @param  is - InputStream to be put to the current URL.
    *
    * @throws DataObjectProxyException
    */
   public void putFromStream(InputStream is) throws DataObjectProxyException {
      boolean doCreate = true;
      int verysmall = 1024;
      
      try {

         if (!mDSI.exists()) {

            if (doCreate) {
              createPath(this.getURL(),"",false);
            }
         }
         
         if (checkLastComp()) {
        	 try {
        	 if (is.available() < verysmall) {
        		 File ftmp = File.createTempFile("xxx",".tmp");
                 java.io.BufferedInputStream isr = new java.io.BufferedInputStream(is);
                 java.io.BufferedOutputStream osr = new java.io.BufferedOutputStream(new java.io.FileOutputStream(ftmp));
                 byte b[] =new byte[2*verysmall];
                 while (isr.available() > 0) {
                	int howmany = isr.read(b); 
                    osr.write(b,0,howmany);  
                 }
                 osr.close();
                 
                 mDSI.putDataSet(ftmp);
                 
                 ftmp.delete();
                 return;
        	 }
        	 } catch(Exception e) {
        		 this.handleExceptions(e);
        	 }
        	 mDSI.putDataSet(is);
         } else {

            // not sure what to do in this case
            throw new DataObjectProxyException(mDSI.getURL() +
                                               ": target is a directory?");
         }
      } catch (StatusException se) {
         this.handleExceptions(se);
      }
   }

   /**
    * Put a set of properties on the current WebDataObjectProxyImpl.
    *
    * @param  proptable - a Hashtable of key/value pairs.
    *
    * @throws DataObjectProxyException.
    * @throws DataObjectProxyException  Description of exception
    *                                   DataObjectProxyException.
    */
   public void putMeta(Hashtable proptable) throws DataObjectProxyException {

      try {
         mDSI.putMetaData(proptable);
      } catch (StatusException se) {
         this.handleExceptions(se);
      }
   }

   /**
    * Put a single property on the current WebDataObjectProxyImpl.
    *
    * @param  prop  - the property with namespace and key.
    * @param  value - the value of the property.
    *
    * @throws DataObjectProxyException.
    * @throws DataObjectProxyException  Description of exception
    *                                   DataObjectProxyException.
    */
   public void putMeta(NSProperty prop, String value)
      throws DataObjectProxyException {

      try {

         // Put the property on the current url
         mDSI.putMetaData(prop, value);

         // reset the dsi point to the parent url of the current one
         mDSI.setURL(DSI.splitUrl(mDSI.getURL())[0]);
         mDSI.putMetaData(prop, value);

         // set the url back to the original one
         mDSI.setURL(mURL.toString());
      } catch (StatusException se) {
         this.handleExceptions(se);
      }

   }


   /**
    * Remove a collection.
    * <p>
    * <b>Warning:</b>  This method is extremely dangerous to use!
    *
    * @throws DataObjectProxyException */
   public void removeDirectory() throws DataObjectProxyException {
      this.removeResource();
   }

   /**
    * Delete a directory and all its contents.
    *
    * @throws DataObjectProxyException  */
   private void removeResource() throws DataObjectProxyException {

      try {
         mDSI.removeResource();
      } catch (Exception e) {
         this.handleExceptions(e);
      }
   }

   /**
    * Reset WebDataObjectProxyImpl using the new url and current user and
    * password.
    *
    * @param  newURL - new url to be accessed.
    *
    * @return a new DataObjectProxy representing the new url.
    */
   public DataObjectProxy resetDataObjectProxy(URL newURL) {
      return resetDataObjectProxy(newURL, this.getUsername(),
                                  this.getPassword());
   }

   /**
    * <p>Reset the WebDataObjectProxyImpl to represent the new url,user and
    * password. If no user and password provided,use the current user and
    * password.</p>
    *
    * @param  newURL  - new url to be accessed.
    * @param  newUser - new user name to access the WebDataObjectProxyImpl.
    * @param  newPass - new password to access the WebDataObjectProxyImpl.
    *
    * @return a new DataObjectProxy representing the new url.
    */
   public DataObjectProxy resetDataObjectProxy(URL newURL, String newUser,
                                               String newPass) {
      return new WebdavDataObjectProxyImpl(newURL, newUser, newPass);
   }

   /**
    * Search for metadata. Not implemented yet.
    *
    * @return Description of return value.
    *
    * @throws DataObjectProxyException Description of exception
    *                                  DataObjectProxyException.
    */
   public Object searchMeta() throws DataObjectProxyException {
      throw new DataObjectProxyException("Search metadata not implemented yet");
      // return null;
   }

   /**
    * Set the password to access the WebDataObjectProxyImpl.
    *
    * @param pass - password to access the server
    */
   public void setPassword(String pass) { mPassword = pass; }

   /**
    * This method should replace the old setUsername and setPassword
    * Reason: If we generate a dop without username and password after that we
    * do dop.setUserName and dop.setPassword the set will not set the
    * credentials to access DSI.
    *
    * @param user The user name to access the server.
    * @param pass password to access the server.
    */
   public void setUserInfo(String user, String pass) {
      this.mUsername = user;
      this.mPassword = pass;
      mDSI = new DSI(mURL.toString(), mUsername, mPassword);
   }

   /**
    * Set the username to access the server.
    *
    * @param user - username to access the server
    */
   public void setUsername(String user) { mUsername = user; }

   /**
    * Upload all the files of a local directory to a webdav collections,
    * creating all the directories and files as needed.
    * <p>
    *
    * @param  srcdop a DataObjectProxy pointing to the collection in the server
    *                where the directory to be uploaded will be stored
    * @param  depth  how far to traverse the sub-tree: currently, 1 or infinite.
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
                tempurl = parenturl + "/" + childname;
                
                tempdop = this.resetDataObjectProxy(new URL(tempurl));
             	InputStream is = childdop.getInputStream();
             	tempdop.putFromStream(is);
             	tempdop.close();
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
   
} // end class WebdavDataObjectProxyImpl
