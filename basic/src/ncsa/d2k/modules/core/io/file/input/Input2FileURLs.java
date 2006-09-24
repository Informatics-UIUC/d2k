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
package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.InputModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxyException;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxyFactory;
import ncsa.gui.Constrain;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyVetoException;
import java.io.File;
import java.net.URL;


/**
 * Input2FileURL allows the user to input the URLSs to two
 *  local or remote resources.
 * The URLs can be set in the properties editor.
 *
 * <p>This module creates a Data Object Proxy for each specified file or URL. 
 * The two
 * <i>DataObjectProxy</i> objects are pushed to the output ports.</p>
 *
 * <p>In general, this replaces the deprecated <i>Input2FileName</i>.</p>
 *
 * @author  unascribed
 * @version 1.0
 */
public class Input2FileURLs extends InputModule {

   //~ Instance fields *********************************************************

   /** The file name property. */
   private String fileName0;

   /** The file name property. */
   private String fileName1;

   /** The host url property. */
   private String hostURL0;

   /** The host url property. */
   private String hostURL1;

   /** The password property. */
   private String password0 = "";

   /** The password property. */
   private String password1 = "";

   /** The username property. */
   private String username0 = "";

   /** The username property. */
   private String username1 = "";

   //~ Methods *****************************************************************

   /**
    * Construct path for URL.
    * <p>In order to use DSI, We need to get the absolute URL of
    * the file or collection. For illustration, using the followingg URL as an
    * example:
    * <p>
    * http://verbena.ncsa.uiuc.edu:8080/slide/files/data/data1.txt 
    * <p>User
    * need to type Host URL: 
    * <p>http://verbena.ncsa.uiuc.edu:8080 
    * <p>File Name:
    * <p>
    * /slide/files/data/data1.txt 
    * <p>In order to prevent users type in two
    * duplicated "/", one at the end of host and one at the begining of the file
    * name, or missing this "/". remove one if duplication happens and add one
    * if missing happens. So each one of the following will have the same url as
    * the above one. Host URL File Name 
    * <ul>
    * <li>http://verbena.ncsa.uiuc.edu:8080
    * <br>
    * /slide/files/data/data1.txt 
    * <p>http://verbena.ncsa.uiuc.edu:8080/
    * <br>
    * /slide/files/data/data1.txt 
    * <p>http://verbena.ncsa.uiuc.edu:8080/
    * <br>
    * slide/files/data/data1.txt 
    * <p>http://verbena.ncsa.uiuc.edu:8080
    * <br>
    * slide/files/data/data1.txt
    *
    * @param  hosturl - url to host including protocol,path and port.
    * @param  fn      - relative path on the host.
    *
    * @return url.
    */
   private String getAbsURL(String hosturl, String fn) {

      String absurl;
      hosturl = hosturl.trim();

      if (hosturl.endsWith("/")) {

         if (!fn.startsWith("/")) {
            absurl = hosturl + fn;
         } else {
            absurl = hosturl + fn.substring(1, fn.length());
         }
      } else {

         if (fn.startsWith("/")) {
            absurl = hosturl + fn;
         } else {
            absurl = hosturl + "/" + fn;
         }
      }


      return absurl;
   }

   /**
    * Get the password.
    *
    * @return password.
    */
   protected String getPassword0() { return password0; }

   /**
    * Get Password1.
    *
    * @return the value.
    */
   protected String getPassword1() { return password1; }


   /**
    * Create a DataObjectProxy based on the properties given by user in the
    * property editor.
    *
    * @throws Exception                Misc.Exception.
    * @throws DataObjectProxyException error creating proxy.
    */
   public void doit() throws Exception {
      String fn0 = getFileName0();
      String hosturl0 = getHostURL0();
      URL url0;

      if (fn0 == null || fn0.length() == 0) {
         throw new DataObjectProxyException(getAlias() +
                                            ": No file name was given.");
      }

      setFileName0(fn0);

      /*
       * Notes on usage of method toURL():
       *
       * 1. The exact form of the URL is system-dependent. If it can be determined
       * that the file denoted by this abstract pathname is a directory, then
       * the resulting URL will end with a slash.
       *
       * 2. This method does not automatically escape characters that are illegal
       * in URLs. It is recommended that new code convert an abstract pathname
       * into a URL by first converting it into a URI, via the toURI method, and
       * then converting the URI into a URL via  the URI.toURL method.
       */

      if (hosturl0 == null || hosturl0.trim().length() == 0) {
         File file = new File(fn0);
         url0 = file.toURI().toURL();
      } else {
         setHostURL0(hosturl0);
         url0 = new URL(getAbsURL(hosturl0, fn0));

         /*
          * If url points to a collection, how can we handle it?
          */
      }

      DataObjectProxy dataobj0 =
         DataObjectProxyFactory.getDataObjectProxy(url0, username0, password0);

      pushOutput(dataobj0, 0);

      String fn1 = getFileName1();
      String hosturl1 = getHostURL1();
      URL url1;

      if (fn1 == null || fn1.length() == 0) {
         throw new DataObjectProxyException(getAlias() +
                                            ": No file name was given.");
      }

      setFileName1(fn1);

      /*
       * Notes on usage of method toURL():
       *
       * 1. The exact form of the URL is system-dependent. If it can be determined
       * that the file denoted by this abstract pathname is a directory, then
       * the resulting URL will end with a slash.
       *
       * 2. This method does not automatically escape characters that are illegal
       * in URLs. It is recommended that new code convert an abstract pathname
       * into a URL by first converting it into a URI, via the toURI method, and
       * then converting the URI into a URL via  the URI.toURL method.
       */

      if (hosturl1 == null || hosturl1.trim().length() == 0) {
         File file = new File(fn1);
         url1 = file.toURI().toURL();
      } else {
         setHostURL1(hosturl1);
         url1 = new URL(getAbsURL(hosturl1, fn1));

         /*
          * If url points to a collection, how can we handle it?
          */
      }

      DataObjectProxy dataobj1 =
         DataObjectProxyFactory.getDataObjectProxy(url1, username1, password1);

      pushOutput(dataobj1, 1);
   } // end method doit

   /**
    * Get the file name propery.
    *
    * @return file name.
    */
   public String getFileName0() { return fileName0; }

   /**
    * Get FileName1.
    *
    * @return the value.
    */
   public String getFileName1() { return fileName1; }

   /**
    * Get the url to host.
    *
    * @return url to host.
    */
   public String getHostURL0() { return hostURL0; }

   /**
    * Get HostURL1.
    *
    * @return the value.
    */
   public String getHostURL1() { return hostURL1; }

   /**
 * Returns a description of the input at the specified index.
 *
 * @param i Index of the input for which a description should be returned.
 *
 * @return <code>String</code> describing the input at the specified index.
 */
   public String getInputInfo(int i) { return ""; }

   /**
    * Returns an array of <code>String</code> objects each containing the 
    * fully qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing 
    * the fully qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() { return null; }

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: ";
      s +=
         "This module is used to enter two URLs to local or remote resources. ";
      s += "</p><p>Detailed Description: ";
      s += "<p>Collect two URLs or local paths, and create a <i>DataObjectProxy</i>";
      s  += " to access each. The proxies are output.</p>";
      s += "The module provides a properties editor that can be used to ";
      s += "enter URLS to two local or remote resource.  If the url points ";
      s += "to a local file, the user can enter the name directly into ";
      s += "a text area. ";
      s += "If the url points to a remote file, the user ";
      s += "has to type in the host url, which include protocol,path and port ";
      s += "in the text area for host url, and the relative path of ";
      s += "the resource to the server in the text area for file name.";
      s += "</p><p>";
      s += "This module does not perform any checks to verify that ";
      s += "the url exists and is accessible with the username and password ";
      s += "given by the user. A check is performed to ";
      s += "make sure that a file name has been entered and an exception is ";
      s += "thrown if the editor text area is blank. ";
      s += "</p><p>";
      s +=
         "The DataObjectProxy objects are made available on " +
         "the <i>DataObjectProxy</i> output ";
      s +=
         "ports.  For local URL, a path may or may not be included in the file name ";
      s += "string.  The final form shown in the properties editor ";
      s += "text box is sent to the <i>DataObjectProxy</i> output port. ";
      s += "Typically when the Browser is used, the absolute path is ";
      s += "included.";

      return s;
   } // end method getModuleInfo

   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleName() { return "Input 2 URLs"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param i Index of the output for which a description 
    * should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {

      if (i == 0) {
         return "A Data Object Proxy pointing to a resource.";
      } else if (i == 1) {
         return "A Data Object Proxy pointing to a resource.";
      }

      return "No such output";
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param i Index of the output for which a description 
    * should be returned.
    *
    * @return <code>String</code> containing the name of the output 
    * at the specified index.
    */
   public String getOutputName(int i) {

      if (i == 0) {
         return "Data Object Proxy 0";
      } else if (i == 1) {
         return "Data Object Proxy 1";
      }

      return "No such output";
   }

   /**
    * Returns an array of <code>String</code> objects each containing the 
    * fully qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the 
    * fully qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] out =
      {
         "ncsa.d2k.modules.core.io.DataObjectProxy",
         "ncsa.d2k.modules.core.io.DataObjectProxy"
      };

      return out;
   }

   /**
    * Return an array with information on the properties the user may update.
    *
    * @return The PropertyDescriptions for properties the user may update.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[8];

      pds[0] =
         new PropertyDescription("fileName0",
                                 "File Name",
                                 "The name of a file, possibly including the path.");
      pds[1] =
         new PropertyDescription("hostURL0", "protocol+host+port for a server",
                                 "Example: http://verbena.ncsa.uiuc.edu:8888.");
      pds[2] = new PropertyDescription("username0", "User Name" + hostURL0,
                                       "");
      pds[3] = new PropertyDescription("password0", "Password" + hostURL0, "");
      pds[4] =
         new PropertyDescription("fileName1",
                                 "File Name",
                                 "The name of a file, possibly including the path.");
      pds[5] =
         new PropertyDescription("hostURL1", "protocol+host+port for a server",
                                 "Example: http://verbena.ncsa.uiuc.edu:8888.");
      pds[6] = new PropertyDescription("username1", "User Name" + hostURL1,
                                       "");
      pds[7] = new PropertyDescription("password1", "Password" + hostURL1,
                                       "");

      return pds;
   } // end method getPropertiesDescriptions

   /**
    * Return a custom property editor.
    *
    * @return return a custom property editor.
    */
   public CustomModuleEditor getPropertyEditor() { return new PropEdit(); }

   /**
    * Get UserName0.
    *
    * @return the value of the first user name.
    */
   public String getUserName0() { return username0; }

   /**
    * Get UserName1.
    *
    * @return the value of the second.
    */
   public String getUserName1() { return username1; }


   /**
    * Set FileName0.
    *
    * @param s the new value.
    *
    * @throws PropertyVetoException not implemented yet.
    */
   public void setFileName0(String s) throws PropertyVetoException {

      /**
       ** Remove checks as too annoying... would like to add them back if module
       * info eventually available w/o triggering this so just commenting out
       * this section for now.
       *
       * // here we check for length of 0 but not for null as we don't want this
       * // to get thrown if an itinerary is saved/reloaded without the //
       * property dialog being used if ( s != null && s.length() == 0) { throw
       * new PropertyVetoException(        "A file name must be entered before
       * the dialog can be closed.",         null); }
       *
       * End of commented out section.
       **/

      fileName0 = s;
   }

   /**
    * Set FileName1.
    *
    * @param s the new value.
    *
    * @throws PropertyVetoException not implemented yet.
    */
   public void setFileName1(String s) throws PropertyVetoException {

      /**
       ** Remove checks as too annoying... would like to add them back if module
       * info eventually available w/o triggering this so just commenting out
       * this section for now.
       *
       * // here we check for length of 0 but not for null as we don't want this
       * // to get thrown if an itinerary is saved/reloaded without the //
       * property dialog being used if ( s != null && s.length() == 0) { throw
       * new PropertyVetoException(        "A file name must be entered before
       * the dialog can be closed.",         null); }
       *
       * End of commented out section.
       **/

      fileName1 = s;
   }

   /**
    * Set HostURL.
    *
    * @param s the new value.
    *
    */
   public void setHostURL0(String s) {
      hostURL0 = s;
   }

   /**
    * Set HostURL1.
    *
    * @param s the new value.
    *
    */
   public void setHostURL1(String s) {
      hostURL1 = s;
   }

   /**
    * Set Password0.
    *
    * @param s the new value.
    *
    */
   public void setPassword0(String s) {
      password0 = s;
   }

   /**
    * Set Password1.
    *
    * @param s the new value.
    *
    */
   public void setPassword1(String s) {
      password1 = s;
   }

   /**
    * Set UserName1.
    *
    * @param s the new value.
    *
    */
   public void setUserName0(String s) {
      username0 = s;
   }

   /**
    * Set UserName1.
    *
    * @param s the new value.
    *
    */
   public void setUserName1(String s) {
      username1 = s;
   }

   //~ Inner Classes ***********************************************************

   // This class needs additional work to make it nicer to use.  Ideally would 
   // like 'browse' buttons for remote objects.
   private class PropEdit extends JPanel implements CustomModuleEditor {

      /** Use serialVersionUID for interoperability. */
      static private final long serialVersionUID = -8620817673571802994L;

      private JTextField hosturljtf;
      private JTextField hosturljtf1;
      private JTextField jtf;
      private JTextField jtf1;
      private JPasswordField passwordjpf;
      private JPasswordField passwordjpf1;
      private JTextField usernamejtf;
      private JTextField usernamejtf1;


      private PropEdit() {
         setLayout(new GridBagLayout());
         this.setMinimumSize(new Dimension(14, 14));

         String name = getFileName0();
         jtf = new JTextField(10);

         /*
          * Set HostURL
          */
         hosturljtf = new JTextField(10);
         hosturljtf.setText(getHostURL0());


         /*
          * Set UserName
          */
         usernamejtf = new JTextField(10);
         usernamejtf.setText(getUserName0());

         /*
          * Set Password
          */

         passwordjpf = new JPasswordField();
         passwordjpf.setEchoChar('*');
         passwordjpf.setText(getPassword0());

         jtf.setText(name);

         String name1 = getFileName1();
         jtf1 = new JTextField(10);

         /*
          * Set HostURL
          */
         hosturljtf1 = new JTextField(10);
         hosturljtf1.setText(getHostURL1());


         /*
          * Set UserName
          */
         usernamejtf1 = new JTextField(10);
         usernamejtf1.setText(getUserName1());

         /*
          * Set Password
          */

         passwordjpf1 = new JPasswordField();
         passwordjpf1.setEchoChar('*');
         passwordjpf1.setText(getPassword1());

         jtf1.setText(name1);

         Constrain.setConstraints(this, new JLabel("File Name"), 0, 0, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(this, jtf, 1, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);

// HostURl label and text field
         Constrain.setConstraints(this, new JLabel("Host URL"), 0, 2, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(this, hosturljtf, 1, 2, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);


         // User name and text field
         Constrain.setConstraints(this, new JLabel("User Name"), 0, 3, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(this, usernamejtf, 1, 3, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);

         // Password and text field
         Constrain.setConstraints(this, new JLabel("Password"), 0, 4, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(this, passwordjpf, 1, 4, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);

         Constrain.setConstraints(this, new JLabel("File Name2"), 0, 5, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(this, jtf1, 1, 5, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);

// HostURl label and text field
         Constrain.setConstraints(this, new JLabel("Host URL 2"), 0, 6, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(this, hosturljtf1, 1, 6, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);


// User name and text field
         Constrain.setConstraints(this, new JLabel("User Name 2"), 0, 7, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(this, usernamejtf1, 1, 7, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);

// Password and text field
         Constrain.setConstraints(this, new JLabel("Password 2"), 0, 8, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(this, passwordjpf1, 1, 8, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);

      }


      public boolean updateModule() throws Exception {
         String f0 = jtf.getText();

         boolean didChange = false;

         if (f0 != getFileName0()) {
            setFileName0(f0);
            didChange = true;
         }

         f0 = hosturljtf.getText();

         if (f0 != getHostURL0()) {
            setHostURL0(f0);
            didChange = true;
         }

         f0 = usernamejtf.getText();

         if (f0 != getUserName0()) {
            setUserName0(f0);
            didChange = true;
         }

         /*
          * Use passwordjpf.getPassword.toString will not give the password text
          */
         char[] chararray = passwordjpf.getPassword();
         StringBuffer sb = new StringBuffer();

         for (int i = 0; i < chararray.length; i++) {
            sb.append(chararray[i]);
         }

         f0 = sb.toString();

         if (f0 != getPassword0()) {

            setPassword0(f0);
            didChange = true;
         }
         
         String f1 = jtf1.getText();

         if (f1 != getFileName1()) {
            setFileName1(f1);
            didChange = true;
         }

         f1 = hosturljtf1.getText();

         if (f1 != getHostURL1()) {
            setHostURL1(f1);
            didChange = true;
         }

         f1 = usernamejtf1.getText();

         if (f1 != getUserName1()) {
            setUserName1(f1);
            didChange = true;
         }

         /*
          * Use passwordjpf.getPassword.toString will not give the password text
          */
         char[] chararray1 = passwordjpf1.getPassword();
         StringBuffer sb1 = new StringBuffer();

         for (int i = 0; i < chararray1.length; i++) {
            sb1.append(chararray1[i]);
         }

         f1 = sb1.toString();

         if (f1 != getPassword1()) {
            setPassword1(f1);
            didChange = true;
         }
         
         return didChange;
      } // end method updateModule
   } // end class PropEdit
} // end class Input2FileURLs
