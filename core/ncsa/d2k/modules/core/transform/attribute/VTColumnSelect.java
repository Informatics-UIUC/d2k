package ncsa.d2k.modules.core.transform.attribute;

import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import ncsa.gui.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.controller.userviews.swing.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
 * VTColumnSelectUI
 * @author gpape
 */
public class VTColumnSelect extends UIModule implements Serializable {

   public String getModuleInfo() {
      return "Allows the user to visually split one Table into " +
             "two and reorder the columns of these tables.";
   }

   public String[] getInputTypes() {
      String[] i = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"}; return i;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "The Table to be manipulated.";
      else
         return "VTColumnSelectUI has no such input.";
   }

   public String[] getOutputTypes() {
      String[] o = {"ncsa.d2k.modules.core.datatype.basic.TableImpl",
         "ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
      return o;
   }

   public String getOutputInfo(int index) {
      if (index == 0)
         return "One of the new Tables.";
      else if (index == 1)
         return "The other new Table.";
      else
         return "VTColumnSelectUI has no such output.";
   }

   VTColumnSelectUIView vis;

   protected UserView createUserView() {
      vis = new VTColumnSelectUIView(); return vis;
   }

   protected String[] getFieldNameMapping() {
      return null;
   }

   protected class VTColumnSelectUIView extends JUserPane
      implements Serializable, ActionListener {

      VTColumnSelect parent;
      TableImpl vt;

      Vector v_all, v_red;

      JButton b_abort, b_done,
         b_append, b_insert, /* b_first, b_last, */ b_remove,
         b_up, b_down, b_copy;

      JList l_all, l_red;
      JScrollPane sp_all, sp_red;
      //JOutlinePanel jop_all, jop_red;

      JPanel p_buttons, p_commands;

      public void setInput(Object o, int i) {
         if (i == 0) {

            vt = (TableImpl)o;

            v_all = new Vector(vt.getNumColumns());
            v_red = new Vector(vt.getNumColumns());

            for (int count = 0; count < vt.getNumColumns(); count++) {
               v_all.add(vt.getColumnLabel(count));
            }

            l_all.setListData(v_all);
            l_red.setListData(v_red);

         }
      }

      public void initView(ViewModule m) {
         parent = (VTColumnSelect)m;

         b_abort = new JButton("Abort");
         b_abort.addActionListener(this);
         b_done = new JButton("Done");
         b_done.addActionListener(this);

         //b_append = new JButton("Append");
		 b_append = new JButton("Move Selected");
		 b_append.setToolTipText("Move the selected columns to the other list");
         b_append.addActionListener(this);
         b_insert = new JButton("Insert");
		 b_insert.setToolTipText("Insert the items from Table 1 after the first selected item in Table 2");
         b_insert.addActionListener(this);
         // b_first = new JButton("First");
         // b_first.addActionListener(this);
         // b_last = new JButton("Last");
         // b_last.addActionListener(this);
         b_remove = new JButton("Remove Selected");
		 b_remove.setToolTipText("Remove the selected items from each list");
         b_remove.addActionListener(this);

         b_up = new JButton("Move Up");
		 b_up.setToolTipText("Move the selected items up in their list");
         b_up.addActionListener(this);
         b_down = new JButton("Move Down");
		 b_down.setToolTipText("Move the selected items down in their list");
         b_down.addActionListener(this);

		b_copy = new JButton("Copy Selected");
		b_copy.setToolTipText("Copy the selected items to the other list");
		b_copy.addActionListener(this);

         //jop_all = new JOutlinePanel("Table 1");
         l_all = new JList();
         l_all.setSelectionModel(new DefaultListSelectionModel());
         //jop_all.add(sp_all = new JScrollPane(l_all));
         sp_all = new JScrollPane(l_all);
		JViewport jv1 = new JViewport();
		jv1.setView(new JLabel("Table 1"));
		sp_all.setColumnHeader(jv1);

         //jop_red = new JOutlinePanel("Table 2");
         l_red = new JList();
         l_red.setSelectionModel(new DefaultListSelectionModel());
         //jop_red.add(sp_red = new JScrollPane(l_red));
         sp_red = new JScrollPane(l_red);
		JViewport jv2 = new JViewport();
		jv2.setView(new JLabel("Table 2"));
		sp_red.setColumnHeader(jv2);

         p_buttons = new JPanel();
         p_buttons.setLayout(new GridLayout(6, 1));
         p_buttons.add(b_append);
         p_buttons.add(b_insert);
         // p_buttons.add(b_first);
         // p_buttons.add(b_last);
		 p_buttons.add(b_copy);
         p_buttons.add(b_remove);
         p_buttons.add(b_up);
         p_buttons.add(b_down);

         p_commands = new JPanel();
         p_commands.add(b_abort);
         p_commands.add(b_done);

         //p_buttons.setLayout(new GridLayout(0, 1));

         /*setLayout(new GridBagLayout());
         Constrain.setConstraints(this, jop_all, 0, 0, 3, 6,
            GridBagConstraints.BOTH, GridBagConstraints.WEST, 3, 6);
         Constrain.setConstraints(this, p_buttons, 3, 0, 1, 5,
            GridBagConstraints.BOTH, GridBagConstraints.WEST, 1, 5);
         Constrain.setConstraints(this, p_commands, 3, 5, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.WEST, 1, 1);
         Constrain.setConstraints(this, jop_red, 4, 0, 3, 6,
            GridBagConstraints.BOTH, GridBagConstraints.WEST, 3, 6);
		*/
		setLayout(new BorderLayout());
		JPanel p = new JPanel();
		/*p.setLayout(new GridBagLayout());
        Constrain.setConstraints(p, sp_all, 0, 0, 3, 1,
            GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST, 3, 1);
         Constrain.setConstraints(p, p_buttons, 3, 0, 1, 1,
            GridBagConstraints.VERTICAL, GridBagConstraints.NORTHWEST, 0, 0);
         Constrain.setConstraints(p, sp_red, 4, 0, 3, 1,
            GridBagConstraints.BOTH, GridBagConstraints.WEST, 3, 1);
		*/
		p.setLayout(new GridLayout(1, 3));
		p.add(sp_all);
		p.add(p_buttons);
		p.add(sp_red);

		add(p, BorderLayout.CENTER);
		add(p_commands, BorderLayout.SOUTH);
      }

      public void actionPerformed(ActionEvent e) {

         Object src = e.getSource();

         if (src == b_append) {

			// move selected indices from l_all to l_red
            int[] selected = l_all.getSelectedIndices();

            for (int count = 0; count < selected.length; count++)
               v_red.add(v_all.remove(selected[count] - count));

			// move selected indices from l_red to l_all
            selected = l_red.getSelectedIndices();

            for (int count = 0; count < selected.length; count++)
               v_all.add(v_red.remove(selected[count] - count));

            l_all.setListData(v_all);
            l_red.setListData(v_red);

         }
         else if (src == b_remove) {
			// remove selected items from l_red
            int[] selected = l_red.getSelectedIndices();

            for (int count = 0; count < selected.length; count++)
               /*v_all.add(*/v_red.remove(selected[count] - count)/*)*/;

			// remove selected items from l_all
			selected = l_all.getSelectedIndices();
            for (int count = 0; count < selected.length; count++)
               /*v_all.add(*/v_all.remove(selected[count] - count)/*)*/;

            l_all.setListData(v_all);
            l_red.setListData(v_red);
         }
         else if (src == b_insert) {

            if (l_red.getMinSelectionIndex() != -1) {

               int[] selected = l_all.getSelectedIndices();

               for (int count = 0; count < selected.length; count++)
                  v_red.add(l_red.getMinSelectionIndex() + count + 1,
                     v_all.remove(selected[count] - count));

               l_all.setListData(v_all);
               l_red.setListData(v_red);

            }

         }
         else if (src == b_up) {

			// move selected items from l_all
            int[] selected = l_all.getSelectedIndices();

            for (int count = 0; count < selected.length; count++)
               if (selected[count] > 0)
                  v_all.add(selected[count] - 1,
                     v_all.remove(selected[count]));

            l_all.setListData(v_all);

            for (int count = 0; count < selected.length; count++)
               if (selected[count] > 0)
                  selected[count]--;

            l_all.setSelectedIndices(selected);

			// move selected items from l_red
            selected = l_red.getSelectedIndices();

            for (int count = 0; count < selected.length; count++)
               if (selected[count] > 0)
                  v_red.add(selected[count] - 1,
                     v_red.remove(selected[count]));

            l_red.setListData(v_red);

            for (int count = 0; count < selected.length; count++)
               if (selected[count] > 0)
                  selected[count]--;

            l_red.setSelectedIndices(selected);

         }
         else if (src == b_down) {
			// move selected items from l_all
            int[] selected = l_all.getSelectedIndices();

            for (int count = selected.length - 1; count >= 0; count--)
               if (selected[count] < v_all.size() - 1)
                  v_all.add(selected[count] + 1,
                     v_all.remove(selected[count]));

            l_all.setListData(v_all);

            for (int count = selected.length - 1; count >= 0; count--)
               if (selected[count] < v_all.size() - 1)
                  selected[count]++;

            l_all.setSelectedIndices(selected);

			// move selected items from l_red
            selected = l_red.getSelectedIndices();

            for (int count = 0; count < selected.length; count++)
               if (selected[count] < v_red.size() - 1)
                  v_red.add(selected[count] + 1,
                     v_red.remove(selected[count]));

            l_red.setListData(v_red);

            for (int count = 0; count < selected.length; count++)
               if (selected[count] < v_red.size() - 1)
                  selected[count]++;

            l_red.setSelectedIndices(selected);

         }
		 else if (src == b_copy) {
			// copy selected items from l_red to l_all
            int[] selected = l_red.getSelectedIndices();

            for (int count = 0; count < selected.length; count++)
               v_all.add(v_red.elementAt(selected[count]));

			// copy selected items from l_all to l_red
			selected = l_all.getSelectedIndices();
            for (int count = 0; count < selected.length; count++)
               v_red.add(v_all.elementAt(selected[count]));

            l_all.setListData(v_all);
            l_red.setListData(v_red);
		 }
         else if (src == b_abort)
            parent.viewCancel();
         else if (src == b_done) {

            TableImpl out_all = (TableImpl)DefaultTableFactory.getInstance().createTable();
            TableImpl out_red = (TableImpl)DefaultTableFactory.getInstance().createTable();

            HashMap vt_labels = new HashMap();

            for (int count = 0; count < vt.getNumColumns(); count++)
               vt_labels.put(vt.getColumnLabel(count), new Integer(count));

            for (int count = 0; count < l_all.getModel().getSize(); count++) {
               out_all.addColumn(vt.getColumn(((Integer)vt_labels.get(l_all.getModel().getElementAt(count))).intValue()));
            }

            for (int count = 0; count < l_red.getModel().getSize(); count++) {
               out_red.addColumn(vt.getColumn(((Integer)vt_labels.get(l_red.getModel().getElementAt(count))).intValue()));
            }

            vt = null;
            pushOutput(out_all, 0);
            pushOutput(out_red, 1);
            //executionManager.moduleDone(parent);
			viewDone("Done");

         }
      }
   }
}
