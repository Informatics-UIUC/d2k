package ncsa.d2k.modules.core.transform.attribute;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.userviews.swing.*;

/**
 * This module presents a user interface for the interactive scaling of
 * <code>MutableTable</code> data. Selected columns are scaled to a
 * user-specified range (the default range is 0 to 1).
 * <p>
 * All transformed columns are converted to type <code>double</code>.
 *
 * @author gpape
 */
public class Scale extends UIModule {

/******************************************************************************/
/* UIModule methods                                                           */
/******************************************************************************/

   public UserView createUserView() {
      return new ScaleUI();
   }

   public String[] getFieldNameMapping() { return null; }

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module presents a simple user interface for the ");
      sb.append("interactive scaling of numeric <i>MutableTable</i> data. ");
      sb.append("Numeric columns selected by the user can be scaled to ");
      sb.append("a specified range (the default range is 0 to 1).");


      sb.append("<P><U>Missing Values Handling:</U> This module handles missing values" +
        " as if they were real meaningful values. For example, if a missing values is represented " +
        "by a number (e.g. zero), then this number may appear as lower or upper bound of its column, " +
        "although it is marked as a missing value in its table. If it indeed becomes a lower or upper " +
        "bound, it affects of course the scaling results.</P>");


      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not modify its input data. Rather, its ");
      sb.append("output is a <i>Transformation</i> that can later be used to ");
      sb.append("scale the specified columns. All transformed columns will ");
      sb.append("be converted to type <i>double</i>.");
      sb.append("</p>");
      return sb.toString();
   }

   public String getModuleName() {
      return "Scale Values";
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "A <i>MutableTable</i> with columns to be scaled.";
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
         return "A scaling transformation for the specified columns.";
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

   private Insets emptyInsets  = new Insets( 0,  0,  0,  0),
                  labelInsets  = new Insets(10, 10, 10, 10),
                  buttonInsets = new Insets( 5,  5,  5,  5);

   private class ScaleUI extends JUserPane implements ActionListener {

      private MutableTable table;

      private HashMap panelMap; // map column indices to column panels
      private JButton abortButton, doneButton;

      private int[] indirection; // points into table, at numeric columns
      private String[] numericLabels;

      private Dimension preferredSize = new Dimension(600, 300);

      public Dimension getMinimumSize() {
         return preferredSize;
      }

      public Dimension getPreferredSize() {
         return preferredSize;
      }

      public void initView(ViewModule mod) { }

      public void setInput(Object obj, int ind) {

         if (ind != 0)
            return;

         table = (MutableTable)obj;
         removeAll();

         // how many numeric columns are there?

         int numNumericColumns = 0, columnType;
         for (int i = 0; i < table.getNumColumns(); i++) {

            columnType = table.getColumnType(i);

            if (columnType == ColumnTypes.BYTE    ||
                columnType == ColumnTypes.DOUBLE  ||
                columnType == ColumnTypes.FLOAT   ||
                columnType == ColumnTypes.INTEGER ||
                columnType == ColumnTypes.LONG    ||
                columnType == ColumnTypes.SHORT) {

               numNumericColumns++;

            }

         }

         // construct list of available numeric columns

         indirection = new int[numNumericColumns];
         numericLabels = new String[numNumericColumns];

         int index = 0;
         for (int i = 0; i < table.getNumColumns(); i++) {

            columnType = table.getColumnType(i);

            if (columnType == ColumnTypes.BYTE    ||
                columnType == ColumnTypes.DOUBLE  ||
                columnType == ColumnTypes.FLOAT   ||
                columnType == ColumnTypes.INTEGER ||
                columnType == ColumnTypes.LONG    ||
                columnType == ColumnTypes.SHORT) {

               indirection[index] = i;

               numericLabels[index] = table.getColumnLabel(i);

               if (numericLabels[index] == null ||
                   numericLabels[index].length() == 0) {

                  numericLabels[index] = "column " + i;

               }

               index++;

            }

         }

         // set up button panel

         abortButton = new JButton("Abort");
         abortButton.addActionListener(this);
         doneButton = new JButton("Done");
         doneButton.addActionListener(this);
         JLabel buttonFillerLabel = new JLabel(" ");

         JPanel buttonPanel = new JPanel();
         GridBagLayout buttonLayout = new GridBagLayout();
         buttonPanel.setLayout(buttonLayout);

         buttonLayout.addLayoutComponent(abortButton,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE,
            buttonInsets, 0, 0));
         buttonPanel.add(abortButton);

         buttonLayout.addLayoutComponent(buttonFillerLabel,
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         buttonPanel.add(buttonFillerLabel);

         buttonLayout.addLayoutComponent(doneButton,
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            buttonInsets, 0, 0));
         buttonPanel.add(doneButton);

         // if no numeric columns, attach a message to that effect. otherwise,
         // attach numeric column panels.

         GridBagLayout layout = new GridBagLayout();
         setLayout(layout);

         if (numNumericColumns == 0) {

            JLabel noNumericLabel = new JLabel(
               "This table does not have any numeric columns to scale.");

            layout.addLayoutComponent(noNumericLabel,
               new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,
               GridBagConstraints.CENTER, GridBagConstraints.BOTH,
               labelInsets, 0, 0));
            add(noNumericLabel);

         }
         else {

            Box numericColumnsBox = new Box(BoxLayout.Y_AXIS);

            panelMap = new HashMap();

            // add fields for scaling

            for (int count = 0; count < indirection.length; count++) {

               index = indirection[count];

               // find min and max for this column

               double min = Double.POSITIVE_INFINITY,
                      max = Double.NEGATIVE_INFINITY, d;

               for (int j = 0; j < table.getNumRows(); j++) {
                  d = table.getDouble(j, index);
                  if (d < min)
                     min = d;
                  if (d > max)
                     max = d;
               }

               // add column panel

               ColumnPanel columnPanel = new ColumnPanel(numericLabels[count],
                 min, max, (count < indirection.length - 1));

               panelMap.put(new Integer(index), columnPanel);

               numericColumnsBox.add(columnPanel);

            }

            JScrollPane numericScroll = new JScrollPane(numericColumnsBox);

            layout.addLayoutComponent(numericScroll,
               new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
               GridBagConstraints.CENTER, GridBagConstraints.BOTH,
               emptyInsets, 0, 0));
            add(numericScroll);

         }

         layout.addLayoutComponent(buttonPanel,
            new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0,
            GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         add(buttonPanel);

      }

      public void actionPerformed(ActionEvent event) {

         Object src = event.getSource();

         if (src == abortButton) {
            viewCancel();
         }

         else if (src == doneButton) {

            int numRelevantColumns = indirection.length;

            int[] indices = new int[numRelevantColumns];
            double[] from_min = new double[numRelevantColumns];
            double[] from_max = new double[numRelevantColumns];
            double[] to_min = new double[numRelevantColumns];
            double[] to_max = new double[numRelevantColumns];

            for (int count = 0; count < indirection.length; count++) {

               int index = indirection[count];

               ColumnPanel columnPanel = (ColumnPanel)panelMap.get(
                  new Integer(index));

               if (columnPanel.scaleCheck.isSelected()) {

                  indices[count] = index;

                  double d;
                  try {
                     d = Double.parseDouble(columnPanel.fromMinField.getText());
                  }
                  catch(NumberFormatException e) {
                     JOptionPane.showMessageDialog(this,
                        "not a number: " + columnPanel.fromMinField.getText(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                  }
                  from_min[count] = d;

                  try {
                     d = Double.parseDouble(columnPanel.fromMaxField.getText());
                  }
                  catch(NumberFormatException e) {
                     JOptionPane.showMessageDialog(this,
                        "not a number: " + columnPanel.fromMaxField.getText(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                  }
                  from_max[count] = d;

                  try {
                     d = Double.parseDouble(columnPanel.toMinField.getText());
                  }
                  catch(NumberFormatException e) {
                     JOptionPane.showMessageDialog(this,
                        "not a number: " + columnPanel.toMinField.getText(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                  }
                  to_min[count] = d;

                  try {
                     d = Double.parseDouble(columnPanel.toMaxField.getText());
                  }
                  catch(NumberFormatException e) {
                     JOptionPane.showMessageDialog(this,
                        "not a number: " + columnPanel.toMaxField.getText(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                  }
                  to_max[count] = d;

               }
               else {

                  indices[count] = -1;

               }

            }

            pushOutput(new ScalingTransformation(indices, from_min, from_max,
               to_min, to_max), 0);
            viewDone("Done");

         }

      }

   }

/******************************************************************************/
/* numeric column information panel                                           */
/******************************************************************************/

   private class ColumnPanel extends JPanel implements ActionListener {

      JCheckBox scaleCheck;
      JLabel fromLabel, fromMinLabel, fromMaxLabel,
             toLabel, toMinLabel, toMaxLabel;
      JTextField fromMinField, fromMaxField, toMinField, toMaxField;

      public ColumnPanel(String label, double fromMin, double fromMax,
         boolean addSeparator) {

         super();

         scaleCheck = new JCheckBox();
         scaleCheck.setSelected(true);
         scaleCheck.addActionListener(this);

         fromMinField = new JTextField(6);
         fromMinField.setText(Double.toString(fromMin));
         fromMaxField = new JTextField(6);
         fromMaxField.setText(Double.toString(fromMax));
         toMinField = new JTextField(6);
         toMinField.setText("0.0");
         toMaxField = new JTextField(6);
         toMaxField.setText("1.0");

         fromLabel = new JLabel("  From:  ");
         fromMinLabel = new JLabel("min");
         fromMaxLabel = new JLabel("max");
         toLabel = new JLabel("  To:  ");
         toMinLabel = new JLabel("min");
         toMaxLabel = new JLabel("max");

         JPanel controlsPanel = new JPanel();
         controlsPanel.add(scaleCheck);
         controlsPanel.add(fromLabel);
         controlsPanel.add(fromMinLabel);
         controlsPanel.add(fromMinField);
         controlsPanel.add(fromMaxLabel);
         controlsPanel.add(fromMaxField);
         controlsPanel.add(toLabel);
         controlsPanel.add(toMinLabel);
         controlsPanel.add(toMinField);
         controlsPanel.add(toMaxLabel);
         controlsPanel.add(toMaxField);

         GridBagLayout layout = new GridBagLayout();
         setLayout(layout);

         JLabel labelLabel = new JLabel(label); // *grin*

         layout.addLayoutComponent(labelLabel, new GridBagConstraints(
            0, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            labelInsets, 0, 0));
         add(labelLabel);

         layout.addLayoutComponent(controlsPanel, new GridBagConstraints(
            1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            emptyInsets, 0, 0));
         add(controlsPanel);

         if (addSeparator) {

            JSeparator separator = new JSeparator();

            layout.addLayoutComponent(separator, new GridBagConstraints(
               0, 1, 2, 1, 1.0, 0.0,
               GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
               emptyInsets, 0, 0));
            add(separator);

         }

      }

      public void actionPerformed(ActionEvent event) {

         boolean selected = scaleCheck.isSelected();

         fromMinField.setEnabled(selected);
         fromMaxField.setEnabled(selected);
         toMinField.setEnabled(selected);
         toMaxField.setEnabled(selected);

         fromLabel.setEnabled(selected);
         fromMinLabel.setEnabled(selected);
         fromMaxLabel.setEnabled(selected);
         toLabel.setEnabled(selected);
         toMinLabel.setEnabled(selected);
         toMaxLabel.setEnabled(selected);

      }

   }

/******************************************************************************/
/* Transformation                                                             */
/******************************************************************************/

   private class ScalingTransformation implements Transformation {

      private int[] indices;
      private double[] from_min, from_max, to_min, to_max;

      ScalingTransformation(int[] indices, double[] from_min, double[] from_max,
         double[] to_min, double[] to_max) {

         this.indices = indices;
         this.from_min = from_min;
         this.from_max = from_max;
         this.to_min = to_min;
         this.to_max = to_max;

      }

      public boolean transform(MutableTable table) {

         if (indices.length == 0 || table.getNumRows() == 0) {
            // no transformation is necessary
            return true;
         }

         for (int count = 0; count < indices.length; count++) {

            int index = indices[count];

            if (index < 0) // this column wasn't selected for scaling
               continue;

            double[] data = new double[table.getNumRows()];

            double from_range = from_max[count] - from_min[count];
            double to_range = to_max[count] - to_min[count];

            double d;

            if (from_range == 0) { // no variance in data...

               d = table.getDouble(0, index);

               if (d >= to_min[count] && d <= to_max[count]) {
                  // data is in new range; leave it alone
                  for (int j = 0; j < data.length; j++)
                     data[j] = table.getDouble(j, index);
               }
               else {
                  // data is out of new range; set to min
                  for (int j = 0; j < data.length; j++)
                     data[j] = to_min[count];
               }

            }
            else { // ordinary data; scale away!

               for (int j = 0; j < data.length; j++) {
                  d = table.getDouble(j, index);
                  data[j] = (d - from_min[count])*to_range/from_range
                          + to_min[count];
               }

            }

            String columnLabel = table.getColumnLabel(index);
            String columnComment = table.getColumnComment(index);

            table.setColumn(data, index);

            table.setColumnLabel(columnLabel, index);
            table.setColumnComment(columnComment, index);

         }

         return true;

      }

   }

}
/**
 * QA comments:
 * 2-28-03  Vered started qa.
 *          added to module info a note about missing values handling.
 */