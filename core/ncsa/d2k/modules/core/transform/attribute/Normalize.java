// package ncsa.d2k.modules.projects.gpape;
package ncsa.d2k.modules.core.transform.attribute;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.userviews.swing.*;

/**
 * This module presents a user interface for interactive normalization of
 * <code>MutableTable</code> data.
 */
public class Normalize extends UIModule {

/******************************************************************************/
/* UIModule methods                                                           */
/******************************************************************************/

   public UserView createUserView() {
      return new NormalizeUI();
   }

   public String[] getFieldNameMapping() { return null; }

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module presents a simple user interface for ");
      sb.append("the interactive normalization of float and double data. ");
      sb.append("The user can specify which of these columns should be ");
      sb.append("normalized and to what range they should be mapped ");
      sb.append("(the default range is 0 to 1).");
      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not modify its input data. Rather, its ");
      sb.append("output is a <i>Transformation</i> that can later be used ");
      sb.append("to normalize the specified columns.");
      sb.append("</p>");
      return sb.toString();
   }

   public String getModuleName() {
      return "Normalize Values";
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "A <i>MutableTable</i> with columns to be normalized.";
      return null;
   }

   public String getInputName(int index) {
      if (index == 0)
         return "Mutable Table";
      return null;
   }

   public String[] getInputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.MutableTable"
      };
   }

   public String getOutputInfo(int index) {
      if (index == 0)
         return "A normalizing transformation for the specified columns.";
      return null;
   }

   public String getOutputName(int index) {
      if (index == 0)
         return "Transformation";
      return null;
   }

   public String[] getOutputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.Transformation"
      };
   }

/******************************************************************************/
/* GUI                                                                        */
/******************************************************************************/

   private class NormalizeUI extends JUserPane implements ActionListener {

      private Dimension minimumSize = new Dimension(400, 250);

      private HashMap panelMap;
      private Insets emptyInsets = new Insets(0, 0, 0, 0),
                     labelInsets = new Insets(0, 10, 0, 10),
                     buttonInsets = new Insets(5, 5, 5, 5);

      private MutableTable table;

      private JButton abortButton, doneButton;

      public void initView(ViewModule mod) { }

      public Dimension getMinimumSize() {
         return minimumSize;
      }

      public void setInput(Object obj, int ind) {

         if (ind != 0)
            return;

         this.removeAll();
         panelMap = new HashMap();

         table = (MutableTable)obj;

         JPanel scalarColumnsPanel = new JPanel();
         GridBagLayout scalarColumnsLayout = new GridBagLayout();
         scalarColumnsPanel.setLayout(scalarColumnsLayout);

         int layoutVIndex = 0; // vertical position for scalarColumnsLayout

         ///////////////////////////////////////////////////////////////////////
         // add fields for normalization
         ///////////////////////////////////////////////////////////////////////

         for (int i = 0; i < table.getNumColumns(); i++) {

            if (table.getColumnType(i) == ColumnTypes.FLOAT ||
                table.getColumnType(i) == ColumnTypes.DOUBLE) {

               // add separator if necessary

               if (panelMap.size() > 0) {
                  JSeparator separator = new JSeparator();
                  scalarColumnsLayout.addLayoutComponent(
                     separator, new GridBagConstraints(
                        0, layoutVIndex++, 2, 1, 1.0, 1.0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        emptyInsets, 0, 0
                     )
                  );
                  scalarColumnsPanel.add(separator);
               }

               // add column label

               JLabel labelLabel = new JLabel(table.getColumnLabel(i));

               scalarColumnsLayout.addLayoutComponent(
                  labelLabel, new GridBagConstraints(
                     0, layoutVIndex, 1, 1, 1.0, 1.0,
                     GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                     labelInsets, 0, 0
                  )
               );
               scalarColumnsPanel.add(labelLabel);

               // add column panel

               ColumnPanel columnPanel = new ColumnPanel();

               panelMap.put(new Integer(i), columnPanel);

               scalarColumnsLayout.addLayoutComponent(
                  columnPanel, new GridBagConstraints(
                     1, layoutVIndex++, 1, 1, 1.0, 1.0,
                     GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                     emptyInsets, 0, 0
                  )
               );
               scalarColumnsPanel.add(columnPanel);

            }

         }

         ///////////////////////////////////////////////////////////////////////
         // add buttons
         ///////////////////////////////////////////////////////////////////////

         JPanel buttonsPanel = new JPanel();
         GridBagLayout buttonsLayout = new GridBagLayout();
         buttonsPanel.setLayout(buttonsLayout);

         abortButton = new JButton("Abort");
         abortButton.addActionListener(this);
         doneButton = new JButton("Done");
         doneButton.addActionListener(this);

         JLabel fillerLabel = new JLabel(" ");

         buttonsLayout.addLayoutComponent(abortButton, new GridBagConstraints(
            0, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE,
            buttonInsets, 0, 0));
         buttonsPanel.add(abortButton);
         buttonsLayout.addLayoutComponent(fillerLabel, new GridBagConstraints(
            1, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         buttonsPanel.add(fillerLabel);
         buttonsLayout.addLayoutComponent(doneButton, new GridBagConstraints(
            2, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            buttonInsets, 0, 0));
         buttonsPanel.add(doneButton);

         ///////////////////////////////////////////////////////////////////////
         // set up this panel's layout
         ///////////////////////////////////////////////////////////////////////

         GridBagLayout layout = new GridBagLayout();
         this.setLayout(layout);

         JScrollPane scroll = new JScrollPane(scalarColumnsPanel);

         layout.addLayoutComponent(scroll, new GridBagConstraints(
            0, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            emptyInsets, 0, 0));
         layout.addLayoutComponent(buttonsPanel, new GridBagConstraints(
            0, 1, 1, 1, 1.0, 0.0,
            GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));

         this.add(scroll);
         this.add(buttonsPanel);

      }

      public void actionPerformed(ActionEvent event) {

         Object src = event.getSource();

         if (src == abortButton) {
            viewCancel();
         }

         else if (src == doneButton) {

            int numRelevantColumns = panelMap.size();

            int[] indices = new int[numRelevantColumns];
            double[] min = new double[numRelevantColumns];
            double[] max = new double[numRelevantColumns];

            int index = 0;
            for (int i = 0; i < table.getNumColumns(); i++) {

               if (table.getColumnType(i) == ColumnTypes.FLOAT ||
                   table.getColumnType(i) == ColumnTypes.DOUBLE) {

                  ColumnPanel columnPanel = (ColumnPanel)panelMap.get(
                     new Integer(i));

                  if (columnPanel.normalizeCheck.isSelected()) {

                     indices[index] = i;

                     double d;
                     try {
                        d = Double.parseDouble(columnPanel.minField.getText());
                     }
                     catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this,
                              "not a number: " + columnPanel.minField.getText(),
                              "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                     }
                     min[index] = d;

                     try {
                        d = Double.parseDouble(columnPanel.maxField.getText());
                     }
                     catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this,
                              "not a number: " + columnPanel.maxField.getText(),
                              "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                     }
                     max[index] = d;

                  }
                  else {

                     indices[index] = -1;

                  }

                  index++;

               }

            }

            pushOutput(new NormalizingTransformation(indices, min, max), 0);
            viewDone("Done");

         }

      }

   }

   private class ColumnPanel extends JPanel implements ActionListener {

      JCheckBox normalizeCheck;
      JLabel minLabel, maxLabel;
      JTextField minField, maxField;

      public ColumnPanel() {

         super();

         normalizeCheck = new JCheckBox();
         normalizeCheck.setSelected(true);
         normalizeCheck.addActionListener(this);

         minLabel = new JLabel("Min:");
         minField = new JTextField(5);
         minField.setText("0.0");
         maxLabel = new JLabel("Max:");
         maxField = new JTextField(5);
         maxField.setText("1.0");

         add(normalizeCheck);
         add(minLabel);
         add(minField);
         add(maxLabel);
         add(maxField);

      }

      public void actionPerformed(ActionEvent e) {

         boolean selected = normalizeCheck.isSelected();

         minLabel.setEnabled(selected);
         minField.setEnabled(selected);
         maxLabel.setEnabled(selected);
         maxField.setEnabled(selected);

      }

   }

/******************************************************************************/
/* Transformation                                                             */
/******************************************************************************/

   private class NormalizingTransformation implements Transformation {

      private int[] indices;
      private double[] min;
      private double[] max;

      NormalizingTransformation(int[] indices, double[] min, double[] max) {
         this.indices = indices;
         this.min = min;
         this.max = max;
      }

      public boolean transform(MutableTable table) {

         for (int i = 0; i < indices.length; i++) {

            if (indices[i] < 0)
               continue;

            int index = indices[i];

            if (table.getColumnType(index) == ColumnTypes.FLOAT) {

               float oldMin = Float.MAX_VALUE, oldMax = Float.MIN_VALUE;

               int numRows = table.getNumRows();
               float f;

               for (int j = 0; j < table.getNumRows(); j++) {
                  f = table.getFloat(j, index);
                  if (f < oldMin)
                     oldMin = f;
                  if (f > oldMax)
                     oldMax = f;
               }

               float oldRange = oldMax - oldMin;
               float newRange = (float)max[i] - (float)min[i];

               float nf;
               for (int j = 0; j < table.getNumRows(); j++) {

                  f = table.getFloat(j, index);

                  nf = (f - oldMin)*newRange/oldRange + (float)min[i];

                  table.setFloat(nf, j, index);

               }

            }
            else if (table.getColumnType(index) == ColumnTypes.DOUBLE) {

               double oldMin = Double.MAX_VALUE, oldMax = Double.MIN_VALUE;

               int numRows = table.getNumRows();
               double d;

               for (int j = 0; j < table.getNumRows(); j++) {
                  d = table.getDouble(j, index);
                  if (d < oldMin)
                     oldMin = d;
                  if (d > oldMax)
                     oldMax = d;
               }

               double oldRange = oldMax - oldMin;
               double newRange = max[i] - min[i];

               double nd;
               for (int j = 0; j < table.getNumRows(); j++) {

                  d = table.getDouble(j, index);

                  nd = (d - oldMin)*newRange/oldRange + min[i];

                  table.setDouble(nd, j, index);

               }

            }
            else {
               return false;
            }

         }

         return true;

      }

   }

}
