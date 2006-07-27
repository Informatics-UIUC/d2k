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
package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.core.gui.JD2KFrame;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.core.modules.VisModule;
import ncsa.d2k.modules.core.prediction.decisiontree.widgets.BrushPanel;
import ncsa.d2k.modules.core.prediction.decisiontree.widgets.DecisionTreeScheme;
import ncsa.d2k.modules.core.prediction.decisiontree.widgets.NavigatorPanel;
import ncsa.d2k.modules.core.prediction.decisiontree.widgets.RectangleBorder;
import ncsa.d2k.modules.core.prediction.decisiontree.widgets.SearchPanel;
import ncsa.d2k.modules.core.prediction.decisiontree.widgets.TreeScrollPane;
import ncsa.d2k.modules.core.prediction.decisiontree.widgets.Viewport;
import ncsa.gui.Constrain;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * DecisionTreeVis.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public final class DecisionTreeVis extends VisModule {

   //~ Static fields/initializers **********************************************

   /** zoom icon location. */
   static private final String zoomicon =
      File.separator + "images" +
      File.separator + "zoom.gif";

   /** search icon location. */
   static private final String searchicon =
      File.separator + "images" +
      File.separator + "search.gif";

   /** print icon location. */
   static private final String printicon =
      File.separator + "images" +
      File.separator + "printit.gif";

   /** home icon location. */
   static private final String homeicon =
      File.separator + "images" +
      File.separator + "home.gif";

   /** help icon location. */
   static private final String helpicon =
      File.separator + "images" +
      File.separator + "help.gif";

   /** size of buttons. */
   static private final Dimension buttonsize = new Dimension(22, 22);

   /** Description of field MENUITEMS. */
   static private final int MENUITEMS = 15;

   //~ Methods *****************************************************************

   /**
    * the help string for help info.
    *
    * @return help string
    */
   private String getHelpString() {
      StringBuffer s = new StringBuffer("<html>");
      s.append("<h1>Decision Tree Vis Help</h1>");
      s.append("<p>Overview: Decision Tree Vis is an interactive display of the ");
      s.append("contents of a decision tree.");
      s.append("<hr>");
      s.append("<p>Detailed Description: Decision Tree Vis is comprised of three main ");
      s.append("components: the Main Area, the Navigator, and the Node Info.");
      s.append("<p>The Main Area shows the decision tree.  When the cursor is ");
      s.append("positioned over a node in the tree, the Node Info is updated to show ");
      s.append("the contents of the node.  The main area also shows the branch labels ");
      s.append("of the decision tree.  The labels are displayed approximately halfway ");
      s.append("between the parent and child.  Subtrees in the Main Area can be ");
      s.append("collapsed using the arrow widget next to a node in the tree.  A ");
      s.append("single-click on a node will show an expanded view of the node.  This ");
      s.append("will show the distributions of the outputs at this particular node.");
      s.append("<p>The Navigator displays a smaller view of the Main Area.  The current ");
      s.append("portion of the tree that is displayed by the Main Area is shown by a ");
      s.append("box in the Navigator.");
      s.append("<p>The Node Info shows the distributions of the classified values at ");
      s.append("the node under the mouse cursor.");
      s.append("<hr>");
      s.append("Menu Options:");
      s.append("<ul>");
      s.append("<li>Options");
      s.append("<ul>");
      s.append("	<li>Set Colors: The color used to display each unique classified");
      s.append("	value can be changed.");
      s.append("	<li>Print Tree: The entire tree can be printed using this option.");
      s.append("	This will be the entire contents of the Main Area.");
      s.append("	<li>Print Window: The Decision Tree Window itself can be printed");
      s.append("	using this option.");
      s.append("  <li>Save as PMML: The Decision Tree Model is saved in a PMML file.");
      s.append("	</ul>");
      s.append("<li>Views");
      s.append("	<ul>");
      s.append("	<li>Maximum Depth: The maximum depth of the tree to be shown can");
      s.append("	be selected using the maximum depth option.  Nodes with a depth");
      s.append("	greater than this number will not be displayed.");
      s.append("	<li>Node Spacing: The space in pixels between nodes in the tree");
      s.append("	can be adjusted using this option.");
      s.append("	<li>Show labels: Toggles the display of branch labels in the Main");
      s.append("	Area.");
      s.append("	</ul>");
      s.append("<li>Tools");
      s.append("	<ul>");
      s.append("	<li>Search: ");
      s.append("Searches for nodes that satisfy the logical expression. The expression is the ");
      s.append("logical AND or logical OR of conditions.<BR>The most basic condition is an ");
      s.append("inequality based on the node population, percent, purity or split value. The ");
      s.append("population is the number of records with the given output value. The percent ");
      s.append("is the population of the given output value relative to the total number of ");
      s.append("records. The purity is a measure of the entropy. The split value is the input ");
      s.append("value used to split the node.<BR>The user may add a series of conditions to the Current ");
      s.append("Conditions list. Pairs of conditions can then be selected and logically ");
      s.append("combined clicking on the 'Replace' button.<br>The single remaining condition is then used to search ");
      s.append("the tree. The search result nodes can be visited  by using Next and Previous.");
      s.append("	</ul>");
      s.append("</ul>");
      s.append("<hr>");
      s.append("Toolbar Options:");
      s.append("<ul>");
      s.append("	<li>Reset: Reset the view to the default viewpoint.");
      s.append("	<li>Print Tree: Print the entire contents of the tree.");
      s.append("	<li>Zoom: When this button is toggled, left-click the mouse ");
      s.append("	button to zoom in and right-click the mouse button to zoom out.");
      s.append("	<li>Search: Display the Search interface.");
      s.append("	<li>Help: Show this help window.");
      s.append("</ul>");
      s.append("<html>");

      return s.toString();
   } // end method getHelpString


   /**
    * Create a new instance of a UserView object that provides the user
    * interface for this user interaction module.
    *
    * @return a new instance of a UserView providing the interface for this
    *         module.
    */
   protected UserView createUserView() { return new DecisionTreeUserView(); }


   /**
    * The list of strings returned by this method allows the module to map the
    * results returned from the pier to the position dependent outputs. The
    * order in which the names appear in the string array is the order in which
    * to assign them to the outputs.
    *
    * @return a string array containing the names associated with the outputs in
    *         the order the results should appear in the outputs.
    */
   public String[] getFieldNameMapping() { return null; }


   /**
    * Returns a description of the input at the specified index.
    *
    * @param  index Index of the input for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the input at the specified index.
    */
   public String getInputInfo(int index) {

      switch (index) {

         case 0:
            return "An Object implementing the ViewableDTModel interface.";

         default:
            return "No such input";
      }
   }


   /**
    * Returns the name of the input at the specified index.
    *
    * @param  index Index of the input for which a name should be returned.
    *
    * @return <code>String</code> containing the name of the input at the
    *         specified index.
    */
   public String getInputName(int index) {

      switch (index) {

         case 0:
            return "Decision Tree Model";

         default:
            return "NO SUCH INPUT!";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the input at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the input at the corresponding index.
    */
   public String[] getInputTypes() {
      String[] types =
      {
         "ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTModel"
      };

      return types;
   }


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s =
         "<p>Overview: Visualize a decision tree. " +
         "<p>Detailed Description: Given a ViewableDTModel, displays the structure " +
         "and contents of the nodes of the decision tree.  The <i>Navigator</i> " +
         "on the left shows a small view of the entire tree.  The main area " +
         "shows an expanded view of the tree. For more information look up the help " +
         "provided in the UI of the module";

      return s;
   }


   /**
    * Returns the name of the module that is appropriate for end-user
    * consumption.
    *
    * @return The name of the module.
    */
   public String getModuleName() { return "Decision Tree Visualization"; }


   /**
    * Returns a description of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> describing the output at the specified index.
    */
   public String getOutputInfo(int index) {

      switch (index) {

         default:
            return "No such output";
      }
   }


   /**
    * Returns the name of the output at the specified index.
    *
    * @param  index Index of the output for which a description should be
    *               returned.
    *
    * @return <code>String</code> containing the name of the output at the
    *         specified index.
    */
   public String getOutputName(int index) {

      switch (index) {

         default:
            return "NO SUCH OUTPUT!";
      }
   }


   /**
    * Returns an array of <code>String</code> objects each containing the fully
    * qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the fully
    *         qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      String[] types = {};

      return types;
   }


   /**
    * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    * objects for each property of the module.
    *
    * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code>
    *         objects.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
      return new PropertyDescription[0]; // so that "WindowName" property

      // is invisible
   }

   //~ Inner Classes ***********************************************************

   /*
    * DecisionTreeUserView
    */
   private class DecisionTreeUserView
      extends ncsa.d2k.userviews.swing.JUserPane implements ActionListener,
                                                            Printable {

      private BrushPanel brushpanel;
      private JPanel buttonpanel;
      private ColorMenuItem[] coloritems;

      private Hashtable colortable;
      private JMenuItem depth;
      private JD2KFrame depthframe;
      private DepthPanel depthpanel;
      private JButton helpbutton;
      private transient HelpWindow helpWindow;

      private JMenuBar menubar;
      private NavigatorPanel navigatorpanel;
      private Hashtable ordertable;
      private JButton printbutton;
      private JMenuItem printtree;
      private JMenuItem printwindow;
      private JButton resetbutton;
      private JMenuItem saveAsPmml;
      private DecisionTreeScheme scheme;
      private JMenuItem search;
      private JButton searchbutton;
      private JD2KFrame searchframe;
      private SearchPanel searchpanel;
      private JCheckBoxMenuItem showlabels;

      private JPanel sidepanel;
      private JScrollPane sidescrollpane;
      private JMenuItem spacing;
      private JD2KFrame spacingframe;
      private SpacingPanel spacingpanel;

      private JPanel toolpanel;
      private TreeScrollPane treescrollpane;
      private JCheckBoxMenuItem zoom;
      private JToggleButton zoombutton;

      ViewableDTModel model;


       /**
        * Invoked when an action occurs.
        */
       public void actionPerformed(ActionEvent event) {
           Object source = event.getSource();

           if (source instanceof ColorMenuItem) {
               ColorMenuItem coloritem = (ColorMenuItem) source;
               Color oldcolor = getColor(coloritem.getText());
               Color newcolor =
                       JColorChooser.showDialog(this, "Choose Color",
                               oldcolor);

               if (newcolor != null) {
                   colortable.put(coloritem.getText(), newcolor);

                   Enumeration keys = colortable.keys();
                   Color[] colors = new Color[colortable.size()];

                   while (keys.hasMoreElements()) {
                       String key = (String) keys.nextElement();
                       Integer index = (Integer) ordertable.get(key);
                       colors[index.intValue()] = (Color) colortable.get(key);
                   }

                   scheme.setColors(colors);
                   brushpanel.repaint();
                   treescrollpane.repaint();
               }
           } else if (source == showlabels) {
               treescrollpane.toggleLabels();

               /*else if (source == zoom) {
            * if (zoom.isSelected()) zoombutton.setSelected(true); else
            * zoombutton.setSelected(false); treescrollpane.toggleZoom(); }*/

           } else if (source == zoombutton) {
               /*if (zoombutton.isSelected())
               * zoom.setSelected(true);  else zoom.setSelected(false);
               */

               treescrollpane.toggleZoom();
           } else if (source == depth) {
               depthframe.getContentPane().removeAll();
               depthframe.getContentPane().add(new DepthPanel(depthframe,
                       treescrollpane));
               depthframe.pack();
               depthframe.setVisible(true);
           } else if (source == spacing) {
               spacingframe.getContentPane().removeAll();
               spacingframe.getContentPane().add(new SpacingPanel(spacingframe,
                       treescrollpane,
                       navigatorpanel));
               spacingframe.pack();
               spacingframe.setVisible(true);
           } else if (source == printtree || source == printbutton) {
               PrinterJob pj = PrinterJob.getPrinterJob();
               pj.setPrintable(treescrollpane.getPrintable());

               if (pj.printDialog()) {

                   try {
                       pj.print();
                   } catch (Exception exception) {
                       exception.printStackTrace();
                   }
               }
           } else if (source == printwindow) {
               PrinterJob pj = PrinterJob.getPrinterJob();
               pj.setPrintable(this);

               if (pj.printDialog()) {

                   try {
                       pj.print();
                   } catch (Exception exception) {
                       exception.printStackTrace();
                   }
               }
           } else if (source == resetbutton) {
               treescrollpane.reset();
           } else if (source == search || source == searchbutton) {
               searchframe.getContentPane().add(searchpanel);
               searchframe.pack();
               searchframe.setVisible(true);
           } else if (source == saveAsPmml) {
               JFileChooser jfc = new JFileChooser();

               int returnVal = jfc.showSaveDialog(null);

               if (returnVal == JFileChooser.APPROVE_OPTION) {

                   try {

                       // get the selected file
                       File newFile = jfc.getSelectedFile();

                       // TODO add back PMML WriteDecisionTreePMML.writePMML(
                       // (DecisionTreeModel) model,
                       // newFile.getAbsolutePath());
                   } catch (Exception e) {
                       ncsa.d2k.core.gui.ErrorDialog.showDialog(e,
                               "Error Writing PMML");
                   }
               }
           } else if (source == helpbutton) {
               helpWindow.setVisible(true);
           }
       } // end method actionPerformed

       /**
        * Get a color from color map
        * @param string
        * @return  color
        */
      public Color getColor(String string) {
         return (Color) colortable.get(string);
      }


       /**
        * Supplies the default behavior for getMenu, which is to return null indicating that
        * no menu specific to this component is to be added.
        *
        * @return a menubar specific to this component.
        */
       public Object getMenu() {
           return menubar;
       }


       /**
        * If the <code>preferredSize</code> has been set to a
        * non-<code>null</code> value just returns it.
        * If the UI delegate's <code>getPreferredSize</code>
        * method returns a non <code>null</code> value then return that;
        * otherwise defer to the component's layout manager.
        *
        * @return the value of the <code>preferredSize</code> property
        * @see #setPreferredSize
        * @see javax.swing.plaf.ComponentUI
        */
       public Dimension getPreferredSize() {
           Dimension top = buttonpanel.getPreferredSize();
           Dimension left = navigatorpanel.getPreferredSize();
           Dimension mainarea = treescrollpane.getPreferredSize();

           int width = (int) left.getWidth() + (int) mainarea.getWidth();
           int height = (int) top.getHeight() + (int) mainarea.getHeight();

           if (width > 800) {
               width = 800;
           }

           if (height > 600) {
               height = 600;
           }

           return new Dimension(width, height);
       }

       /**
        * Called by the D2K Infrastructure to allow the view to perform initialization tasks.
        *
        * @param module The module this view is associated with.
        */
       public void initView(ViewModule module) {
           menubar = new JMenuBar();
           helpWindow = new HelpWindow();
       }

       /**
        * Print this view
        * @param g
        * @param pf
        * @param pi
        * @return
        * @throws PrinterException
        */
       public int print(Graphics g, PageFormat pf, int pi)
         throws PrinterException {
         double pageHeight = pf.getImageableHeight();
         double pageWidth = pf.getImageableWidth();

         double cWidth = getWidth();
         double cHeight = getHeight();

         double scale = 1;

         if (cWidth >= pageWidth) {
            scale = pageWidth / cWidth;
         }

         if (cHeight >= pageHeight) {
            scale = Math.min(scale, pageHeight / cHeight);

         }

         double cWidthOnPage = cWidth * scale;
         double cHeightOnPage = cHeight * scale;

         if (pi >= 1) {
            return Printable.NO_SUCH_PAGE;
         }

         Graphics2D g2 = (Graphics2D) g;
         g2.translate(pf.getImageableX(), pf.getImageableY());
         g2.scale(scale, scale);
         print(g2);

         return Printable.PAGE_EXISTS;
      } // end method print

       /**
        * Called to pass the inputs received by the module to the view.
        *
        * @param object The object that has been input.
        * @param index The index of the module input that been received.
        */
       public void setInput(Object object, int index) {
           model = (ViewableDTModel) object;

           /*String[] outputs = model.getUniqueOutputValues();
      *     scheme = new DecisionTreeScheme(outputs.length);    colortable =
      * new Hashtable(outputs.length);    ordertable = new
      * Hashtable(outputs.length);    for (int outindex = 0; outindex <
      * outputs.length; outindex++) { colortable.put(outputs[outindex],
      * scheme.getNextColor()); ordertable.put(outputs[outindex], new
      * Integer(outindex));    }*/

           // Menu
           JMenu optionsmenu = new JMenu("Options");
           JMenu viewsmenu = new JMenu("Views");
           JMenu toolsmenu = new JMenu("Tools");

           menubar.add(optionsmenu);
           menubar.add(viewsmenu);
           menubar.add(toolsmenu);

           /*JMenu colorsmenu = new JMenu("Set Colors");
    *     coloritems = new ColorMenuItem[outputs.length];    JMenu
    * currentmenu = colorsmenu;    int items = 0;    for (int outindex =
    * 0; outindex < coloritems.length; outindex++) { coloritems[outindex]
    * = new ColorMenuItem(outputs[outindex]);
    * coloritems[outindex].addActionListener(this); if (items ==
    * MENUITEMS) { JMenu nextmenu = new JMenu("More...");
    * currentmenu.insert(nextmenu, 0); nextmenu.add(coloritems[outindex]);
    * currentmenu = nextmenu; items = 1; } else {
    * currentmenu.add(coloritems[outindex]); items++; }    }*/

           printtree = new JMenuItem("Print Tree...");
           printtree.addActionListener(this);

           printwindow = new JMenuItem("Print Window...");
           printwindow.addActionListener(this);

           saveAsPmml = new JMenuItem("Save as PMML...");
           saveAsPmml.addActionListener(this);

           if (!(model instanceof NominalViewableDTModel)) {
               saveAsPmml.setEnabled(false);
           }

           // optionsmenu.add(colorsmenu);
           optionsmenu.addSeparator();
           optionsmenu.add(printtree);
           optionsmenu.add(printwindow);
           optionsmenu.add(saveAsPmml);

           depth = new JMenuItem("Maximum Depth...");
           depth.addActionListener(this);

           spacing = new JMenuItem("Node Spacing...");
           spacing.addActionListener(this);

           // zoom = new JCheckBoxMenuItem("Zoom");
           // zoom.addActionListener(this);

           showlabels = new JCheckBoxMenuItem("Show Labels");
           showlabels.setState(true);
           showlabels.addActionListener(this);

           viewsmenu.add(depth);
           viewsmenu.add(spacing);
           viewsmenu.addSeparator();

           // viewsmenu.add(zoom);
           viewsmenu.add(showlabels);

           search = new JMenuItem("Search...");
           search.addActionListener(this);

           if (!(model instanceof NominalViewableDTModel)) {
               search.setEnabled(false);
           }

           toolsmenu.add(search);

           // Tool panel
           toolpanel = new JPanel();

           Image image = getImage(homeicon);
           ImageIcon icon = null;

           if (image != null) {
               icon = new ImageIcon(image);
           }

           if (icon != null) {
               resetbutton = new JButton(icon);
               resetbutton.setMaximumSize(buttonsize);
               resetbutton.setPreferredSize(buttonsize);
           } else {
               resetbutton = new JButton("Reset");
           }

           resetbutton.addActionListener(this);
           resetbutton.setToolTipText("Reset");

           image = getImage(printicon);
           icon = null;

           if (image != null) {
               icon = new ImageIcon(image);
           }

           if (icon != null) {
               printbutton = new JButton(icon);
               printbutton.setMaximumSize(buttonsize);
               printbutton.setPreferredSize(buttonsize);
           } else {
               printbutton = new JButton("Print");
           }

           printbutton.addActionListener(this);
           printbutton.setToolTipText("Print Tree");

           image = getImage(searchicon);
           icon = null;

           if (image != null) {
               icon = new ImageIcon(image);
           }

           if (icon != null) {
               searchbutton = new JButton(icon);
               searchbutton.setMaximumSize(buttonsize);
               searchbutton.setPreferredSize(buttonsize);
           } else {
               searchbutton = new JButton("Search");
           }

           searchbutton.addActionListener(this);
           searchbutton.setToolTipText("Search");

           if (!(model instanceof NominalViewableDTModel)) {
               searchbutton.setEnabled(false);
           }

           image = getImage(zoomicon);
           icon = null;

           if (image != null) {
               icon = new ImageIcon(image);
           }

           if (icon != null) {
               zoombutton = new JToggleButton(icon);
               zoombutton.setMaximumSize(buttonsize);
               zoombutton.setPreferredSize(buttonsize);
           } else {
               zoombutton = new JToggleButton("Zoom");
           }

           zoombutton.addActionListener(this);
           zoombutton.setToolTipText("Zoom");

           image = getImage(helpicon);

           if (image != null) {
               icon = new ImageIcon(image);
           }

           if (icon != null) {
               helpbutton = new JButton(icon);
               helpbutton.setMaximumSize(buttonsize);
               helpbutton.setPreferredSize(buttonsize);
           } else {
               helpbutton = new JButton("H");
           }

           helpbutton.addActionListener(this);
           helpbutton.setToolTipText("Help");

           toolpanel.setLayout(new GridBagLayout());
           Constrain.setConstraints(toolpanel, new JPanel(), 0, 0, 1, 1,
                   GridBagConstraints.BOTH,
                   GridBagConstraints.NORTHWEST, 1, 1);
           buttonpanel = new JPanel();
           buttonpanel.setLayout(new GridLayout(1, 5));
           buttonpanel.add(resetbutton);
           buttonpanel.add(printbutton);
           buttonpanel.add(zoombutton);
           buttonpanel.add(searchbutton);
           buttonpanel.add(helpbutton);
           Constrain.setConstraints(toolpanel, buttonpanel, 1, 0, 1, 1,
                   GridBagConstraints.HORIZONTAL,
                   GridBagConstraints.NORTHWEST, 0, 0);
           /*Constrain.setConstraints(toolpanel, resetbutton, 1, 0, 1, 1,
           * GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST, 0, 0);
           * Constrain.setConstraints(toolpanel, printbutton, 2, 0, 1, 1,
           * GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST, 0, 0);
           * Constrain.setConstraints(toolpanel, zoombutton, 3, 0, 1, 1,
           * GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST, 0, 0);
           * Constrain.setConstraints(toolpanel, searchbutton, 4, 0, 1, 1,
           * GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST, 0, 0);
           */

           // Split pane
           brushpanel = new BrushPanel(model);
           brushpanel.setBorder(new RectangleBorder("Node Info"));

           treescrollpane = new TreeScrollPane(model, brushpanel);

           navigatorpanel = new NavigatorPanel(model, treescrollpane);
           navigatorpanel.setBorder(new RectangleBorder("Navigator"));

           depthframe = new JD2KFrame("Maximum Depth");

           spacingframe = new JD2KFrame("Node Spacing");

           searchframe = new JD2KFrame("Search");

           if (model instanceof NominalViewableDTModel) {
               searchpanel = new SearchPanel(treescrollpane, searchframe);
           }

           sidepanel = new JPanel();
           sidepanel.setMinimumSize(new Dimension(0, 0));

           sidepanel.setBackground(DecisionTreeScheme.backgroundcolor);
           sidepanel.setLayout(new GridBagLayout());
           Constrain.setConstraints(sidepanel, navigatorpanel, 0, 0, 1, 1,
                   GridBagConstraints.HORIZONTAL,
                   GridBagConstraints.NORTHWEST, 0, 0,
                   new Insets(10, 10, 10, 10));
           Constrain.setConstraints(sidepanel, brushpanel, 0, 1, 1, 1,
                   GridBagConstraints.HORIZONTAL,
                   GridBagConstraints.NORTHWEST, 0, 0,
                   new Insets(10, 10, 10, 10));
           Constrain.setConstraints(sidepanel, new JPanel(), 0, 2, 1, 1,
                   GridBagConstraints.NONE,
                   GridBagConstraints.NORTHWEST, 1, 1,
                   new Insets(10, 10, 10, 10));

           sidescrollpane = new JScrollPane(sidepanel);

           JSplitPane splitpane =
                   new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                           sidescrollpane, treescrollpane);
           splitpane.setOneTouchExpandable(true);
           splitpane.setDividerLocation(260);

           add(toolpanel, BorderLayout.NORTH);
           add(splitpane, BorderLayout.CENTER);
           setBackground(DecisionTreeScheme.backgroundcolor);
       } // end method setInput

      private class ColorMenuItem extends JMenuItem {
         ColorMenuItem(String s) { super(s); }
      }
   } // end class DecisionTreeUserView

    /**
     * A window to hold the help text
     */
   private final class HelpWindow extends JD2KFrame {
      HelpWindow() {
         super("About Decision Tree Vis");

         JEditorPane jep = new JEditorPane("text/html", getHelpString());
         getContentPane().add(new JScrollPane(jep));
         setSize(400, 400);
      }
   }

    /**
     *
     */
   class DepthPanel extends JPanel implements ActionListener {
      JButton apply;
      JButton cancel;
      JButton close;

      int depth;
      JD2KFrame depthframe;
      JTextField dfield;

      JLabel dlabel;
      TreeScrollPane treescrollpane;

        /**
         * Constructor
         * @param frame
         * @param scrollpane
         */
      DepthPanel(JD2KFrame frame, TreeScrollPane scrollpane) {
         depthframe = frame;
         treescrollpane = scrollpane;

         depth = treescrollpane.getDepth();

         dlabel = new JLabel("Maximum Depth:");

         dfield = new JTextField(Integer.toString(depth), 5);

         apply = new JButton("Apply");
         apply.addActionListener(this);

         close = new JButton("Close");
         close.addActionListener(this);

         cancel = new JButton("Cancel");
         cancel.addActionListener(this);

         JPanel buttonpanel = new JPanel();
         buttonpanel.add(cancel);
         buttonpanel.add(close);
         buttonpanel.add(apply);

         setLayout(new GridBagLayout());
         Constrain.setConstraints(this, dlabel, 0, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTHWEST, 0, 0,
                                  new Insets(5, 5, 5, 5));
         Constrain.setConstraints(this, dfield, 1, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTHWEST, 0, 0,
                                  new Insets(5, 5, 5, 5));
         Constrain.setConstraints(this, buttonpanel, 0, 1, 2, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTHWEST, 1, 1,
                                  new Insets(5, 5, 5, 5));

      }

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent event) {
            Object source = event.getSource();

            if (source == close) {
                depthframe.setVisible(false);

            }

            if (source == cancel) {
                treescrollpane.setDepth(depth);

                depthframe.setVisible(false);
            }

            if (source == apply) {
                String svalue = dfield.getText();

                try {
                    int ivalue = Integer.parseInt(svalue);

                    treescrollpane.setDepth(ivalue);

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } // end method actionPerformed
   } // end class DepthPanel

    /**
     *
     */
   class SpacingPanel extends JPanel implements ActionListener {
      JButton apply;
      JButton cancel;
      JButton close;
      JTextField hfield;

      JLabel hlabel;
      NavigatorPanel navigatorpanel;
      JD2KFrame spacingframe;
      TreeScrollPane treescrollpane;
      JTextField vfield;
      Viewport viewroot;
      JLabel vlabel;

      double xspace;
      double yspace;

        /**
         * Constructor
         * @param frame
         * @param scrollpane
         * @param navigator
         */
      SpacingPanel(JD2KFrame frame, TreeScrollPane scrollpane,
                   NavigatorPanel navigator) {
         spacingframe = frame;
         treescrollpane = scrollpane;
         navigatorpanel = navigator;

         viewroot = treescrollpane.getViewRoot();
         xspace = viewroot.xspace;
         yspace = viewroot.yspace;

         hlabel = new JLabel("Horizontal Spacing:");
         vlabel = new JLabel("Vertical Spacing:");

         hfield = new JTextField(Double.toString(xspace), 5);
         vfield = new JTextField(Double.toString(yspace), 5);

         apply = new JButton("Apply");
         apply.addActionListener(this);

         close = new JButton("Close");
         close.addActionListener(this);

         cancel = new JButton("Cancel");
         cancel.addActionListener(this);

         JPanel buttonpanel = new JPanel();
         buttonpanel.add(cancel);
         buttonpanel.add(close);
         buttonpanel.add(apply);

         setLayout(new GridBagLayout());
         Constrain.setConstraints(this, hlabel, 0, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTHWEST, 0, 0,
                                  new Insets(5, 5, 5, 5));
         Constrain.setConstraints(this, hfield, 1, 0, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTHWEST, 0, 0,
                                  new Insets(5, 5, 5, 5));
         Constrain.setConstraints(this, vlabel, 0, 1, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTHWEST, 0, 0,
                                  new Insets(5, 5, 5, 5));
         Constrain.setConstraints(this, vfield, 1, 1, 1, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTHWEST, 0, 0,
                                  new Insets(5, 5, 5, 5));
         Constrain.setConstraints(this, buttonpanel, 0, 3, 2, 1,
                                  GridBagConstraints.HORIZONTAL,
                                  GridBagConstraints.NORTHWEST, 1, 1,
                                  new Insets(5, 5, 5, 5));

      }

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent event) {
            Object source = event.getSource();

            if (source == close) {
                spacingframe.setVisible(false);

            }

            if (source == cancel) {
                viewroot.xspace = xspace;
                viewroot.yspace = yspace;

                treescrollpane.rebuildTree();
                navigatorpanel.rebuildTree();

                spacingframe.setVisible(false);
            }

            if (source == apply) {
                String hsvalue = hfield.getText();
                String vsvalue = vfield.getText();

                try {
                    double hdvalue = Double.parseDouble(hsvalue);
                    double vdvalue = Double.parseDouble(vsvalue);

                    viewroot.xspace = hdvalue;
                    viewroot.yspace = vdvalue;

                    treescrollpane.rebuildTree();
                    navigatorpanel.rebuildTree();

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } // end method actionPerformed
   } // end class SpacingPanel
} // end class DecisionTreeVis
