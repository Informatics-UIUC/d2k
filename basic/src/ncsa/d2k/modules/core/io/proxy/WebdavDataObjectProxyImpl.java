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

import org.scidac.cmcs.dsmgmt.dsi.DSI;
import org.scidac.cmcs.dsmgmt.dsi.StatusException;
//import org.scidac.cmcs.security.auth.DialogAuthListener;
import org.scidac.cmcs.util.NSProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

/**
 * WebdavDataObjectProxyImpl manages access to files through the http/https 
 * protocol. Users can read file 
 * or InputStream from, write file or InputStream to the url.
 *
 *<p>This class is built on the Data Storage Interface (DSI) from
 *the SCIDAC CMCS project.</p>
 *<p>
 *  See <u>http://collaboratory.emsl.pnl.gov/</u>
 *</p>
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class WebdavDataObjectProxyImpl extends DataObjectProxy {

   //~ Instance fields *********************************************************

   /** The locally cached file.  */
   private File localCopy = null;

   /** The DSI object, implements access to WebDAV */
   protected DSI mDSI=null;

   //~ Constructors ************************************************************

   /**
    * WebdavDataObjectProxyImpl.
    * <p>Creates a new WebdavDataObjectProxyImpl object based on the given URL.</p>
    *
    * @param url - the URI to a WebDAV server.
    */
   public WebdavDataObjectProxyImpl(URL url) { this(url, "", ""); }

   /**
    * WebdavDataObjectProxyImpl
    * <p>Creates a new WebdavDataObjectProxyImpl object.</p>
    *
    * @param url      - the URI to a WebDAV server.
    * @param username - username to access the server.
    * @param password - password to access the server.
    */
   public WebdavDataObjectProxyImpl(URL url, String username, String password) {
	  mURL = url;
      mDSI = new DSI(url.toString());
      
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

   //~ Methods *****************************************************************

   /**
    * <p> Create a temporary local file in the specified directory and
    * save the path of this temporary file into a Vector.
    * </p>
    * @throws IOException If a file could not be created.
    * @throws DataObjectProxyException 
    * 
    * @see setTempDataDir.
    */
   private void createTempFile() throws DataObjectProxyException  {
      if (tempDataDir == null) {
         setTempDataDir();
      }
      try {
    	  localCopy = File.createTempFile("d2k", ".out", tempDataDir);
      }
      catch(IOException ioe) {
    	  handleExceptions(ioe);
      }
      if (tempFilesCreated == null) {
         tempFilesCreated = new Vector();
      }

      /*
       * For future use purpose, save the path of the newly created files 
       * into a Vector.
       */
      tempFilesCreated.add(localCopy.getAbsolutePath());
   }


   /**
    * <p>Delete all of its children resources of a directory. Only used to clean up 
    * temporary files.</p>
    * @param  dir - the director
    * y to be cleaned.
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
      }

      return ret;
   }
   
   /**
    * <p> Try to create a directory to store locally cached files in the users 
    * working directory or users home directory. If users don't have writing 
    * access to either of these two directories, throw a DataObjectProxyException. 
    * <p>
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
         }
         else {
        	 throw new DataObjectProxyException("Need writing access to \n"
        			 +System.getProperty("user.dir")
        			 +"\n or \n"
        			 +System.getProperty("user.home"));
         }
         
      }

      tempDataDir.mkdir();
      tempDataDir.deleteOnExit();

   }

   /**
    * Get the password to access the server.
    * @return the current password to access the server.
    */
   protected String getPassword() { 
	   if (mPassword == null) mPassword = "";
	   return mPassword; 
	  }

   /**
    * Check if userID and password are necessary to access the url if yes,
    * return true; if no, return no; How ?
    *
    * @return Description of return value.
    */
/*
   public boolean checkAuth() {
      boolean ret = false;

      return ret;
   }
*/
   /**
    * Dlete all of the temp files created during the process.
    */
   private void cleanUp() {

      /*
       * Delete the files inside temp data directory. For all of 
       * the files which can not be deleted, delete then on exit.
       */
      if (tempDataDir != null) {
         boolean isTempDataDel = deleteDir(tempDataDir);

         if (!isTempDataDel) {
            String[] children = tempDataDir.list();

            for (int i = 0; i < children.length; i++) {
               new File(children[i]).deleteOnExit();
            }
         }
         
      }
   }
   
   /**
    * <p>Close DSI object, clean up the tempory files created,
    * username and password</p>
    */
   public void close() {

      if (mDSI != null) {
         mDSI.close();
      }
      mUsername=null;
      mPassword=null;
      cleanUp();
   }

   /**
    * Get the DSI object representing the current url.
    * @return the current DSI object .
    */
 //  public DSI getDSI() { return mDSI; }

   /**
    * Get a input stream to the current DSI object.
    *
    * @return Description of return value.
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
    * Get locally cached file 
    * 
    * @return locally cached file 
 * @throws DataObjectProxyException 
    * @throws Exception if the local file can not be created.
    */
   public File getLocalFile() throws DataObjectProxyException  {
	   if (localCopy == null || (!localCopy.exists())) {
		   createTempFile();
	   }
	   return localCopy;
   }

   /**
    * Get a InputStream from locally cached copy
    *
    * @return a InputStream from locally cached copy
 * @throws DataObjectProxyException 
    *
    * @throws Exception Description of exception Exception.
    */
   public InputStream getLocalInputStream() throws DataObjectProxyException {
      InputStream is = null;
	   try {
    	  is = new FileInputStream(localCopy);
      }
      catch (Exception e) {
    	  this.handleExceptions(e);
      }
      return is;
   }

   /**
    * Get the metadata of the current DSI object
    * 
    * *<p><b>Note:  This class is under development.</b>
    *
    * @return a metadata Hashtable
    *  
    * @throws DataObjectProxyException 
    *
    * 
    */
   public Object getMeta() throws DataObjectProxyException {
	   Hashtable ret=null;
	   try {
    	  ret = mDSI.getMetaData();
	   }
	   catch (StatusException se) {
		   handleExceptions(se);
      }
	   return ret;
   }

   /**
    * Get the value of the specified property on the current url.
    * @param prop - the property of interest including namespace
    * @return a possibly null DSIProperty object
    * @throws DataObjectProxyException 
    *
    * 
    */
   private Object getMeta(NSProperty prop) throws DataObjectProxyException {
	   Object ret=null;
	   try {
		   ret=mDSI.getMetaData(prop);		   
	   }
	   catch (StatusException se) {
		   handleExceptions(se);
	   }
	   return ret;      
   }
   
   public Object getMeta(Object prop) throws DataObjectProxyException {
	   return getMeta((NSProperty)prop);
   }
   
   /**
    * Get the current user to access the current WebDataObjectProxy. 
    * @return username.
    */
   public String getUsername() { 
	   if (mUsername == null) mUsername = "";
	   return mUsername;  
   }

   /**
    * Handles exceptions
    * @throws DataObjectProxyException 
    */
   private void handleExceptions(Exception ex) throws DataObjectProxyException {
	
	   /*
	    * HttpException method getReasonCode() is deprecated. 
	    * DSI handleExceptions() method is using this deprecated method. 
	    * The following se.getStatusCode() might not give correct status code
	    * because the usage of this deprecated method. 
	    */
	   if( ex instanceof StatusException) {
		   StatusException se = (StatusException)ex;
		   Throwable theCause = se.getCause();
		   if (theCause instanceof StatusException) {
			   // extract the encased exception
			   StatusException se2 = (StatusException)theCause;
			   if (se2.getStatusCode() == 401) {
				   throw new DataObjectProxyException("Unauthorized (401)");
			   }
		   }
		   throw new DataObjectProxyException("*****StatusException "+
				   se.getStatusCode()+ " "+se.getImprovedMessage());   
	   }
	   else if (ex instanceof FileNotFoundException) {
		   FileNotFoundException fe = (FileNotFoundException)ex;
		   throw new DataObjectProxyException("*****FileNotFoundException "+
				   fe.getLocalizedMessage());		   
	   }
	   else if (ex instanceof MalformedURLException) {
		   MalformedURLException me = (MalformedURLException)ex;
		   throw new DataObjectProxyException("******MalformedURLException "+
				   me.getLocalizedMessage());
	   }
	   else {
		   throw new DataObjectProxyException("******Unknown Exception "+
				   ex.getLocalizedMessage());
	   }
   }
   
   /**
    * Test if the current DSI represents a collection or not.
    * 
    * @return true if DSI represents a collection, 
    *         false otherwise;
    * @throws DataObjectProxyException  
    */
   public boolean isCollection() throws DataObjectProxyException  {
	   boolean ret = false;
	   try {
		   ret = mDSI.isCollection();
	   }
	   catch(StatusException se) {
		   handleExceptions(se);
	   }
      return ret;
   }
   
   /**
    * Create a directory at path
    * 
    *  @param path a relative path (relative to current URL).
    *  
    * @return DataObjectProxy for the new object.
    */
   public DataObjectProxy createCollection(String path) throws DataObjectProxyException {
	      String newpath = "";
		   try {
			   newpath = mDSI.makeCollection(path);
		   }
		   catch(StatusException se) {
			   handleExceptions(se);
		   }
		    
		   try {
			   URL newU = new URL(mDSI.getURL());
			   return this.resetDataObjectProxy(
				   newU,
				   this.getUsername(), 
				   this.getPassword());
		   } catch(MalformedURLException mfe) {
			   handleExceptions(mfe);
		   }
		   
		   return this;
}

   private void createPath() throws DataObjectProxyException {
	   // get the path to the file
	   try {
		URL pu = new URL(mDSI.getURL());
		String pt = pu.getPath();
		pt = pt.substring(0,pt.lastIndexOf('/'));
		String comps[] = pt.split("/");
		String path = new String("");
 		String ppath = new String("");
 		for (int i =0; i < comps.length; i++) {
 			if (comps[i] == null || comps[i].equals("")) {
 				continue;
 			}
 			path = ppath+comps[i];
 			URL pp = new URL(pu.getProtocol(),pu.getHost(),pu.getPort(),"/"+path);
 			
 			DSI d2 = null;
 			if (this.getUsername().equals("")) {
 				d2 = new DSI(pp.toString());
 			} else {
 				d2 = new DSI(pp.toString(),this.getUsername(), this.getPassword());	
 			}
 			if (!d2.exists()) {
 				URL root = new URL(pu.getProtocol(),pu.getHost(),pu.getPort(),"/"+ppath);
 				DataObjectProxy dop2 = 
 					DataObjectProxyFactory.getDataObjectProxy(root,
 							this.getUsername(), this.getPassword());
 				dop2.createCollection(comps[i]);
 			}
 			ppath = path+"/";
 		}
	   } catch (MalformedURLException mfe) {
		   this.handleExceptions(mfe);
	   } catch (StatusException se) {
		   this.handleExceptions(se);
	   }
   }
   
   /*
    *  Check if the URL ends in a '/'
    *  <p>
    *  If so, the Web server will be confused if you try to
    *  put a file to the URL.
    *  
    *  
    *
    */
   private boolean checkLastComp() {
	   try {
			URL pu = new URL(mDSI.getURL());
			String pt = pu.getPath();
			if (pt.contains("/")) {
				pt = pt.substring(pt.lastIndexOf('/')+1);
				if (pt.length() == 0) return false;
			} 
		} catch (MalformedURLException mfe) {
				 return false;
	   }
	   return true;
   }
   
   /**
    * Put a local file to the url represented by the current DSI object.
    *
    * @param  file  - local file to be put to url.
    * @throws DataObjectProxyException 
    *
    * 
    */
   public void putFromFile(File file) throws DataObjectProxyException{
     boolean doCreate = true;
     
	   try {
    	 if (!mDSI.exists()) {
    		 	if (doCreate) {
    		 		createPath();
    		 	}
    	 }
    	 if (checkLastComp()) {
    		 mDSI.putDataSet(file);
    	 } else {
    		 // not sure what to do in this case
    		 throw new DataObjectProxyException(mDSI.getURL()+
    				 ": target is a directory?");
    	 }
     }
     catch(StatusException se) {
    	 this.handleExceptions(se);
     }
   }
   /**
    * <p>Put a local file to the url represented by the current DSI object
    * Put the specified properties to the current url and its parent url. </p>
    *
    * @param  file - local file to be put to url
    * @param  nsp  - NSProperty including namspace and key
    * @param  value - Property value
    * @throws DataObjectProxyException 
    *
    * 
    */
   //public void putFromFileWithProp(File file, NSProperty nsp,String value) throws DataObjectProxyException {
	//   try {
	////	   mDSI.putDataSet(file);
	//	   this.putMeta(nsp,value);
	//	   mDSI.setURL(DSI.splitUrl(mDSI.getURL())[0]);
	//	   this.putMeta(nsp,value);
	//	   mDSI.setURL(mURL.toString());
	//   }
	//   catch (StatusException se){
	//	  this.handleExceptions(se);
	//   }
	      
   //}

   /**
    * <p>Put InputStream to the url being pointed to by the current 
    * WebDataObjectProxyImpl.</p>
    * @param  is - InputStream to be put to the current URL.
    * @throws DataObjectProxyException 
    */
   public void putFromStream(InputStream is) throws DataObjectProxyException {
	   boolean doCreate = true;
	   try {
		   if (!mDSI.exists()) {
	    		 	if (doCreate) {		 		
	    		 		createPath();	 	
	    		 	}
	    	 }
		     if (checkLastComp()) {
			   mDSI.putDataSet(is);
	    	 } else {
	    		 // not sure what to do in this case
	    		 throw new DataObjectProxyException(mDSI.getURL()+
	    				 ": target is a directory?");
	    	 }
	   }
	   catch (StatusException se) {
		   this.handleExceptions(se);
	   }
   }

   /**
    * Put a set of properties on the current WebDataObjectProxyImpl.
    * @param  proptable - a Hashtable of key/value pairs.
    * @throws DataObjectProxyException.
    */
   public void putMeta(Hashtable proptable) throws DataObjectProxyException{
      try {
    	  mDSI.putMetaData(proptable);
      }
      catch(StatusException se) {
    	  this.handleExceptions(se);
      }
   }

   /**
    * Put a single property on the current WebDataObjectProxyImpl.
    * @param  prop  - the property with namespace and key.
    * @param  value - the value of the property.
    * @throws DataObjectProxyException.
    */
   public void putMeta(NSProperty prop, String value) throws DataObjectProxyException {
	   try {
		   //Put the property on the current url
		   mDSI.putMetaData(prop, value);
		   
		   //reset the dsi point to the parent url of the current one
		   mDSI.setURL(DSI.splitUrl(mDSI.getURL())[0]);
		   mDSI.putMetaData(prop,value);
		   
		   //set the url back to the original one
		   mDSI.setURL(mURL.toString());
	   }
	   catch(StatusException se) {
		   this.handleExceptions(se);
	   }
	   
   }

   /**
    * Reset WebDataObjectProxyImpl using the new url and current user and password.
    * @param  newURL - new url to be accessed.
    * @return a new DataObjectProxy representing the new url.
    */
   public DataObjectProxy resetDataObjectProxy(URL newURL) {
      return resetDataObjectProxy(newURL, this.getUsername(), this.getPassword());
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
    * Search for metadata.  Not implemented yet.
    *
    * @return Description of return value.
    */
   public Object searchMeta() throws DataObjectProxyException { 
	   throw new DataObjectProxyException("Search metadata not implemented yet");
	 //  return null; 
	   }

   /**
    * Set the password to access the WebDataObjectProxyImpl.
    * @param pass - password to access the server
    */
   public void setPassword(String pass) { mPassword = pass; }

   /**
    * Set the username to access the server.
    *
    * @param user - username to access the server
    */
   public void setUsername(String user) { mUsername = user; }


   // Add more DSI methods wrapper later
   

} // end class WebdavDataObjectProxyImpl
