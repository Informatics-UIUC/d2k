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
package ncsa.d2k.modules.core.io.sql;

/**
 * <p>Title: CheckBoxRenderer</p> 
 * <p>Description: Renderer for Checkbox inside of a JTable</p> 
 * <p>Copyright: Copyright (c) 2003</p> 
 * <p>Company: </p> \
 * @author  Dora Cai 
 * @version 1.0
 */

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import java.awt.Color;
import java.awt.Component;


/**
 * Description of class CheckBoxRenderer.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

   //~ Static fields/initializers **********************************************

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 2779312576271583001L;

   //~ Constructors ************************************************************

   /**
    * Creates a new CheckBoxRenderer object.
    */
   public CheckBoxRenderer() {
      super();
      setHorizontalAlignment(0);
   }

   //~ Methods *****************************************************************

   /**
    * Override this method from the parent class. Only paint the background if
    * the row isn't selected
    *
    * @param  table      the JTable component
    * @param  value      the cell content's object
    * @param  isSelected boolean so we know if this is the currently selected
    *                    row
    * @param  hasFocus   does this cell currently have focus?
    * @param  row        the row number
    * @param  column     the column number
    *
    * @return override this method from the parent class. Only paint the
    *         background if the row isn't selected
    */
   public Component getTableCellRendererComponent(JTable table,
                                                  java.lang.Object value,
                                                  boolean isSelected,
                                                  boolean hasFocus, int row,
                                                  int column) {
      Color c = table.getBackground();
      this.setBackground(c);
      setSelected(value != null && ((Boolean) value).booleanValue());

      return this;
   }
} 
