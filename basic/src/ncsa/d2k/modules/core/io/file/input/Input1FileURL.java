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
import ncsa.d2k.preferences.UserPreferences;
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.net.URL;
import ncsa.d2k.modules.core.util.*;


/**
 * Input1FileURL allows the user to input the url to a local or remote resource.
 * The url can be set in the properties editor.
 *
 * <p>This module creates a Data Object Proxy for the specified file or URL. The
 * <i>DataObjectProxy</i> is pushed to the output.</p>
 *
 * <p>In general, this replaces the deprecated <i>Input1FileName</i>.</p>
 *
 * @author  unascribed
 * @version 1.0
 */
public class Input1FileURL extends InputModule {

   //~ Instance fields *********************************************************

   /** The file name property. */
   private String fileName;

   /** The host url property. */
   private String hostURL;

   /** The password property. */
   private String password = "";

   /** The username property. */
   private String username = "";


   /** the end of the font style. */
   final String endfontstyle = "</font>";


   /** the fontstyle tags. */
   final String fontstyle =
      "<font size=\"" +
      UserPreferences.thisPreference.getFontSize() +
      "\" face=\"Arial,Helvetica,SansSerif \">";

   //~ Methods *****************************************************************

   /**
    * Construct path for URL.
    *
    * <p>In order to use DSI, We need to get the absolute URL of the file or
    * collection. For illustration, using the followingg URL as an example:</p>
    *
    * <p>http://verbena.ncsa.uiuc.edu:8080/slide/files/data/data1.txt</p>
    *
    * <p>User need to type Host URL:</p>
    *
    * <p>http://verbena.ncsa.uiuc.edu:8080</p>
    *
    * <p>File Name:</p>
    *
    * <p>/slide/files/data/data1.txt</p>
    *
    * <p>In order to prevent users type in two duplicated "/", one at the end of
    * host and one at the begining of the file name, or missing this "/". remove
    * one if duplication happens and add one if missing happens. So each one of
    * the following will have the same url as the above one. Host URL File Name
    * </p>
    *
    * <ul>
    *   <li>http://verbena.ncsa.uiuc.edu:8080<br>
    *     /slide/files/data/data1.txt
    *
    *     <p>http://verbena.ncsa.uiuc.edu:8080/<br>
    *     /slide/files/data/data1.txt</p>
    *
    *     <p>http://verbena.ncsa.uiuc.edu:8080/<br>
    *     slide/files/data/data1.txt</p>
    *
    *     <p>http://verbena.ncsa.uiuc.edu:8080<br>
    *     slide/files/data/data1.txt</p>
    *   </li>
    * </ul>
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
   protected String getPassword() { return password; }


   /**
    * Create a DataObjectProxy based on the properties given by user in the
    * property editor.
    *
    * @throws Exception                Misc. error.
    * @throws DataObjectProxyException Error in creating proxy.
    */
 
   private D2KModuleLogger myLogger = 
	                       D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
   public void beginExecution() {
		  myLogger.setLoggingLevel(moduleLoggingLevel);
   }
   
   private int moduleLoggingLevel=
	   D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass())
	   .getLoggingLevel();
   
   public int getmoduleLoggingLevel(){
	   return moduleLoggingLevel;
   }

   public void setmoduleLoggingLevel(int level){
	   moduleLoggingLevel = level;
   }
     
   
   public void doit() throws Exception {      	   
      String fn = getFileName();
      String hosturl = getHostURL();
      URL url;

      myLogger.setLoggingLevel(moduleLoggingLevel);
     
      myLogger.debug(this.getAlias()+": Here is some DEBUG-test");
      myLogger.info(this.getAlias()+": Here is some INFO-test");
      myLogger.warn(this.getAlias()+": Here is some WARN-test");
      myLogger.error(this.getAlias()+": Here is some ERROR-test");
      myLogger.fatal(this.getAlias()+"; Here is some FATAL-test");
      
      if (fn == null || fn.length() == 0) {
         throw new DataObjectProxyException(getAlias() +
                                            ": No file name was given.");
      }

      setFileName(fn);

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

      if (hosturl == null || hosturl.trim().length() == 0) {
         File file = new File(fn);
         url = file.toURI().toURL();
      } else {
         setHostURL(hosturl);
         url = new URL(getAbsURL(hosturl, fn));
         /*
          * If url points to a collection, how can we handle it?
          */
      }

      DataObjectProxy dataobj =
         DataObjectProxyFactory.getDataObjectProxy(url, username, password);
      pushOutput(dataobj, 0);
   } // end method doit

   /**
    * Get the file name propery.
    *
    * @return file name.
    */
   public String getFileName() { return fileName; }

   /**
    * Get the url to host.
    *
    * @return url to host.
    */
   public String getHostURL() { return hostURL; }

   /**
    * Returns a description of the input at the specified index.
    *
    * @param  i Index of the input for which a description should be returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int i) { return ""; }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
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
         "This module is used to enter the url to a local or remote resource. ";
      s += "</p><p>Detailed Description: ";
      s += "<p>Collect a URL or local path, and create a <i>DataObjectProxy</i>";
      s += " to access it. The proxy is output.</p>";
      s += "The module provides a properties editor that can be used to ";
      s += "enter a url to a local or remote resource.  If the url points ";
      s += "to a local file, the user can enter the name directly into ";
      s += "a text area.";
      s += "If the url points to a remote file, the user ";
      s += "has to type in the host url, which include protocol, path and port ";
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
         "The DataObjectProxy is made available on the " +
         "<i>DataObjectProxy</i> output ";
      s +=
         "port.  For local url, a path may or may not be included " +
         "in the file name ";
      s += "string.  The final form shown in the properties editor ";
      s += "text box is sent to the <i>DataObjectProxy</i> output port. ";
      s += "Typically when the Browser is used, the absolute path is ";
      s += "included.";

      return s;
   } // end method getModuleInfo

   /**
    * Return the name of this module.
    *
    * @return The display name for this module.
    */
   public String getModuleName() { return "Input URL or Path"; }

   /**
    * Returns a description of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int i) {
      return "An data object proxy pointing to a resource.";
   }

   /**
    * Returns the name of the output at the specified index.
    *
    * @param  i Index of the output for which a description should be returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int i) { return "DataObjectProxy"; }

   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] out = { "ncsa.d2k.modules.core.io.proxy.DataObjectProxy" };

      return out;
   }

   /**
    * Return an array with information on the properties the user may update.
    *
    * @return The PropertyDescriptions for properties the user may update.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[5];

      pds[0] =
         new PropertyDescription("fileName",
                                 "File Name",
                                 "The name of a file, possibly including the path.");
      pds[1] =
         new PropertyDescription("hostURL", "protocol+host+port for a server",
                                 "Example: http://verbena.ncsa.uiuc.edu:8888.");
      pds[2] =
         new PropertyDescription("username", "User Name",
                                 "The user login name to access the object, if needed.");
      pds[3] =
         new PropertyDescription("password", "Password",
                                 "The user password to access the object, if needed.");
      pds[4] =
    	 new PropertyDescription("moduleLoggingLevel", "Module Logging Level",
    			                 "The logging level of this modules");  

      return pds;
   }

   /**
    * Return a custom property editor.
    *
    * @return return a custom property editor.
    */
   public CustomModuleEditor getPropertyEditor() { return new PropEdit(); }

   /**
    * Get UserName.
    *
    * @return The user name.
    */
   public String getUserName() { return username; }

   /**
    * Set FileName.
    *
    * @param  s the new value.
    *
    * @throws PropertyVetoException bad file name (not implemented yet).
    */
   public void setFileName(String s) throws PropertyVetoException {

      /**
       ** Remove checks as too annoying... would like to add them back if module
       * info eventually available w/o triggering this so just commenting out
       * this section for now.
       *
       * // here we check for length of 0 but not for null as we don't want this
       * // to get thrown if an itinerary is saved/reloaded without the //
       * property dialog being used
       *
       * if ( s != null && s.length() == 0) { throw new PropertyVetoException(
       *    "A file name must be entered before the dialog can be closed.",
       *    null); }
       *
       * End of commented out section.
       **/

      fileName = s;
   }

   /**
    * Set HostURL.
    *
    * @param s new value.
    */
   public void setHostURL(String s) { hostURL = s; }

   /**
    * Set Password.
    *
    * @param s the new value.
    */
   public void setPassword(String s) { password = s; }

   /**
    * Set UserName.
    *
    * @param s new value.
    */
   public void setUserName(String s) { username = s; }

   //~ Inner Classes ***********************************************************

   private class PropEdit extends JPanel implements CustomModuleEditor {

      /** Use serialVersionUID for interoperability. */
      static private final long serialVersionUID = 2637786544956495261L;
      private JTextField hosturljtf;
      private JTextField jtf;
      private JPasswordField passwordjpf;
      private String prophost;
      private JTextField usernamejtf;
      private JComboBox logginglevelcb;
      
      public JComboBox getLoggerComboBox(){return logginglevelcb;}

      private PropEdit() {
         setLayout(new GridBagLayout());
         this.setMinimumSize(new Dimension(14, 14));

         String name = getFileName();
         jtf = new JTextField(10);
         jtf.setText(name);
         hosturljtf = new JTextField(10);
         hosturljtf.setText(getHostURL());

         /*
          * Set UserName
          */
         usernamejtf = new JTextField(10);
         usernamejtf.setText(getUserName());

         /*
          * Set Password
          */

         passwordjpf = new JPasswordField(10);
         passwordjpf.setEchoChar('*');
         passwordjpf.setText(getPassword());

         /*
          * Set module logging level
          */
         String[] loglevelEnum = {"DEBUG","INFO","WARN","ERROR","FATAL","OFF"};
         logginglevelcb = new JComboBox(loglevelEnum);
         logginglevelcb.setEditable(false);
         logginglevelcb.setSelectedIndex(moduleLoggingLevel);

         JOutlinePanel namePanel = new JOutlinePanel("File Name");
         namePanel.setLayout(new GridBagLayout());

         // set up the description of the property
         StringBuffer tp = new StringBuffer("<html>");
         tp.append(fontstyle);
         tp.append("The name of a file, possibly including the path.");
         tp.append(endfontstyle);
         tp.append("</html>");

         JEditorPane jta =
            new JEditorPane("text/html", tp.toString()) {
               static private final long serialVersionUID = 1L;

               // we can no longer have a horizontal scrollbar if we are always
               // set to the same width as our parent....may need to be fixed.
               public Dimension getPreferredSize() {
                  Dimension d = this.getMinimumSize();

                  return new Dimension(450, d.height);
               }
            };
         jta.setEditable(false);
         jta.setBackground(namePanel.getBackground());

         JButton b0 = new JButton("Browse Local");

         Constrain.setConstraints(namePanel, jta, 0, 0, 3, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 0, 0);

         Constrain.setConstraints(namePanel, jtf, 0, 1, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(namePanel, b0, 1, 1, 1, 1,
                                  GridBagConstraints.NONE,
                                  GridBagConstraints.CENTER, 0, 0);


         JOutlinePanel urlPanel = new JOutlinePanel("Server URL");
         urlPanel.setLayout(new GridBagLayout());

         // set up the description of the property
         tp = new StringBuffer("<html>");
         tp.append(fontstyle);
         tp.append("The name of a server, including port and protocol");
         tp.append(endfontstyle);
         tp.append("</html>");

         JEditorPane jta2 =
            new JEditorPane("text/html", tp.toString()) {
               static private final long serialVersionUID = 1L;

               // we can no longer have a horizontal scrollbar if we are always
               // set to the same width as our parent....may need to be fixed.
               public Dimension getPreferredSize() {
                  Dimension d = this.getMinimumSize();

                  return new Dimension(450, d.height);
               }
            };
         jta2.setEditable(false);
         jta2.setBackground(urlPanel.getBackground());

         Constrain.setConstraints(urlPanel, jta2, 0, 0, 2, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(urlPanel, hosturljtf, 0, 1, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);

         JOutlinePanel usernamePanel = new JOutlinePanel("User Name");
         usernamePanel.setLayout(new GridBagLayout());

         // set up the description of the property
         tp = new StringBuffer("<html>");
         tp.append(fontstyle);
         tp.append("The login name to use");
         tp.append(endfontstyle);
         tp.append("</html>");

         JEditorPane jta3 =
            new JEditorPane("text/html", tp.toString()) {
               static private final long serialVersionUID = 1L;

               // we can no longer have a horizontal scrollbar if we are always
               // set to the same width as our parent....may need to be fixed.
               public Dimension getPreferredSize() {
                  Dimension d = this.getMinimumSize();

                  return new Dimension(450, d.height);
               }
            };
         jta3.setEditable(false);
         jta3.setBackground(usernamePanel.getBackground());

         Constrain.setConstraints(usernamePanel, jta3, 0, 0, 2, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 0, 0);
         Constrain.setConstraints(usernamePanel, usernamejtf, 0, 1, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 0, 0);

         JOutlinePanel passwordPanel = new JOutlinePanel("Password");
         passwordPanel.setLayout(new GridBagLayout());

         // set up the description of the property
         tp = new StringBuffer("<html>");
         tp.append(fontstyle);
         tp.append("The password");
         tp.append(endfontstyle);
         tp.append("</html>");

         JEditorPane jta4 =
            new JEditorPane("text/html", tp.toString()) {
               static private final long serialVersionUID = 1L;

               // we can no longer have a horizontal scrollbar if we are always
               // set to the same width as our parent....may need to be fixed.
               public Dimension getPreferredSize() {
                  Dimension d = this.getMinimumSize();

                  return new Dimension(450, d.height);
               }
            };
         jta4.setEditable(false);
         jta4.setBackground(passwordPanel.getBackground());
         Constrain.setConstraints(passwordPanel, jta4, 0, 0, 2, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 0, 0);

         Constrain.setConstraints(passwordPanel, passwordjpf, 0, 1, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 0, 0);

         JOutlinePanel loggingLevelPanel = new JOutlinePanel("Module Logging Level");
         loggingLevelPanel.setLayout(new GridBagLayout());
         
         tp = new StringBuffer("<html>");
         tp.append(fontstyle);
         tp.append("The Logging Level");
         tp.append(endfontstyle);
         tp.append("</html>");
         
         JEditorPane jta5 =
             new JEditorPane("text/html", tp.toString()) {
                static private final long serialVersionUID = 1L;

                // we can no longer have a horizontal scrollbar if we are always
                // set to the same width as our parent....may need to be fixed.
                public Dimension getPreferredSize() {
                   Dimension d = this.getMinimumSize();

                   return new Dimension(450, d.height);
                }
             };         

             jta5.setBackground(loggingLevelPanel.getBackground());

             Constrain.setConstraints(loggingLevelPanel, jta5, 0, 0, 2, 1,
            		 GridBagConstraints.HORIZONTAL,
                     GridBagConstraints.CENTER, 0, 0);

             Constrain.setConstraints(loggingLevelPanel, logginglevelcb, 0, 1, 1, 1,
                     GridBagConstraints.HORIZONTAL,
                     GridBagConstraints.CENTER, 0, 0);
                     
         Constrain.setConstraints(this, namePanel, 0, 0, 3, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(this, urlPanel, 0, 1, 3, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(this, usernamePanel, 0, 2, 3, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(this, passwordPanel, 0, 3, 3, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.CENTER, 1, 0);
         Constrain.setConstraints(this, loggingLevelPanel, 0, 4, 3, 1,
                 				  GridBagConstraints.HORIZONTAL,
                 				  GridBagConstraints.CENTER, 1, 0);   

         b0.addActionListener(new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                  prophost = "";
                  prophost = hosturljtf.getText();

                  boolean nohost =
                     (prophost == null) || (prophost.trim().length() == 0);

                  if (!nohost) {
                     JFrame frame = new JFrame("Ooops");
                     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                     JOptionPane.showMessageDialog(frame,
                                                   "Browse is not supported for remote services");

                     return;


                  }

                  JFileChooser chooser = new JFileChooser();
                  chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                  chooser.addChoosableFileFilter(new CSVFilter());
                  chooser.addChoosableFileFilter(new TXTFilter());
                  chooser.addChoosableFileFilter(new XMLFilter());
                  chooser.addChoosableFileFilter(new JPGFilter());
                  chooser.addChoosableFileFilter(new GIFFilter());
                  chooser.setFileFilter(chooser.getAcceptAllFileFilter());

                  String d = jtf.getText();

                  if (d == null) {
                     d = getFileName();
                  }

                  ;

                  // if(d != null) {                  added 3.25.2004 by DC ---
                  // d.trim().length() > 0
                  if (d != null && (d.trim().length() > 0)) {
                     File thefile = new File(d);

                     chooser.setSelectedFile(thefile);

                  }
// added 3.25.2004 by DC
                  else {
                     chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                  }
// end

                  // set the title of the FileDialog
                  StringBuffer sb = new StringBuffer("Select File");
                  chooser.setDialogTitle(sb.toString());

                  // get the file
                  String fn;
                  int retVal = chooser.showOpenDialog(null);

                  if (retVal == JFileChooser.APPROVE_OPTION) {
                     fn = chooser.getSelectedFile().getAbsolutePath();
                  } else {
                     return;
                  }

                  if (fn != null) {

                     // display the path to the chosen file
                     jtf.setText(fn);
                  }

               } // End of ActionPerformed
            });

      } // End of Constructor PropEdit()

      public boolean updateModule() throws Exception {
         String f0 = jtf.getText();

         boolean didChange = false;

         if (f0 != getFileName()) {
            setFileName(f0);
            didChange = true;
         }

         f0 = hosturljtf.getText();

         if (f0 != getHostURL()) {
            setHostURL(f0);
            didChange = true;
         }

         f0 = usernamejtf.getText();

         if (f0 != getUserName()) {
            setUserName(f0);
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

         if (f0 != getPassword()) {

            setPassword(f0);
            didChange = true;
         }

         f0 = Integer.toString(logginglevelcb.getSelectedIndex());
         if (Integer.parseInt(f0) != getmoduleLoggingLevel()){
        	 setmoduleLoggingLevel(Integer.parseInt(f0));
        	 didChange = true;
         }     
         return didChange;
      } // end method updateModule

      class CSVFilter extends javax.swing.filechooser.FileFilter {
         public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".csv");
         }

         public String getDescription() {
            return "Comma-Separated Values (.csv)";
         }
      }

      class GIFFilter extends javax.swing.filechooser.FileFilter {
         public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".gif");
         }

         public String getDescription() { return "GIF Image (.gif)"; }
      }

      class JPGFilter extends javax.swing.filechooser.FileFilter {
         public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".jpg");
         }

         public String getDescription() { return "JPG Image (.jpg)"; }
      }

      class TXTFilter extends javax.swing.filechooser.FileFilter {
         public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".txt");
         }

         public String getDescription() { return "Text Files (.txt)"; }
      }

      class XMLFilter extends javax.swing.filechooser.FileFilter {
         public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".xml");
         }

         public String getDescription() { return "XML Files (.xml)"; }
      }

   } // end class PropEdit

} // end class InputFileURL
