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
 * <code>MutableTable</code> data. Selected numeric columns are normalized
 * (<i>standardized</i>) to a set of values such that the mean of that set
 * is approximately zero and the standard deviation of that set is
 * approximately one.
 * <p>
 * All transformed columns are converted to type <code>double</code>.
 *
 * @author gpape
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
      sb.append("This module presents a simple user interface for the ");
      sb.append("interactive normalization (<i>standardization</i>) of ");
      sb.append("numeric <i>MutableTable</i> data. Numeric columns selected ");
      sb.append("by the user are normalized to a set of values such that the ");
      sb.append("mean of that set is approximately zero and the standard ");
      sb.append("deviation of that set is approximately one.");
      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not modify its input data. Rather, its ");
      sb.append("output is a <i>Transformation</i> that can later be used to ");
      sb.append("normalize the specified columns. All transformed columns ");
      sb.append("will be converted to type <i>double</i>.");
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

      private MutableTable table;

      private JList numericList; // list of available numeric columns
      private JButton abortButton, doneButton;

      private int[] indirection; // points into table, at numeric columns
      private String[] numericLabels;

      private Insets emptyInsets  = new Insets( 0,  0,  0,  0),
                     labelInsets  = new Insets(10, 10, 10, 10),
                     buttonInsets = new Insets( 5,  5,  5,  5);

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

         numericList = new JList(numericLabels);

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
         // attach the JList.

         GridBagLayout layout = new GridBagLayout();
         setLayout(layout);

         if (numNumericColumns == 0) {

            JLabel noNumericLabel = new JLabel(
               "This table does not have any numeric columns to normalize.");

            layout.addLayoutComponent(noNumericLabel,
               new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
               GridBagConstraints.CENTER, GridBagConstraints.BOTH,
               labelInsets, 0, 0));
            add(noNumericLabel);

         }
         else {

            JScrollPane numericScroll = new JScrollPane(numericList);

            layout.addLayoutComponent(numericScroll,
               new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
               GridBagConstraints.CENTER, GridBagConstraints.BOTH,
               emptyInsets, 0, 0));
            add(numericScroll);

         }

         layout.addLayoutComponent(buttonPanel,
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
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

            if (indirection.length == 0) {
               pushOutput(new NormalizingTransformation(new int[0]), 0);
            }
            else {

               int[] indices = numericList.getSelectedIndices();
               int[] transform = new int[indices.length];

               for (int i = 0; i < indices.length; i++) {
                  transform[i] = indirection[indices[i]];
               }

               pushOutput(new NormalizingTransformation(transform), 0);

            }

            viewDone("Done");

         }

      }

   }

/******************************************************************************/
/* Transformation                                                             */
/******************************************************************************/

   private class NormalizingTransformation implements Transformation {

      private int[] indices; // numeric column indices in the table

      NormalizingTransformation(int[] indices) {
         this.indices = indices;
      }

      public boolean transform(MutableTable table) {

         if (indices.length == 0 || table.getNumRows() == 0) {
            // no transformation is necessary
            return true;
         }

         // loop over all relevant numeric column indices in the table
         for (int count = 0; count < indices.length; count++) {

            double[] data = new double[table.getNumRows()];

            int index = indices[count];

            // data first represents the data from the table:
            for (int j = 0; j < data.length; j++)
               data[j] = table.getDouble(j, index);

            // calculate mean
            double mean = 0;
            for (int j = 0; j < data.length; j++)
               mean += data[j];

            mean /= data.length;

            // data now represents differences from the mean:
            for (int j = 0; j < data.length; j++)
               data[j] = data[j] - mean;

            // calculate sum of squares of differences
            double sq_diff_sum = 0;
            for (int j = 0; j < data.length; j++)
               sq_diff_sum += (data[j] * data[j]);

            // calculate sample variance
            double sample_variance = 0;

            if (data.length == 1)
               sample_variance = sq_diff_sum;
            else
               sample_variance = sq_diff_sum / (data.length - 1);

            // calculate sample standard deviation
            double sample_std_dev = Math.sqrt(sample_variance);

            // divide to normalize data:
            if (sample_std_dev == 0.0) {
               for (int j = 0; j < data.length; j++)
                  data[j] = 0;
            }
            else {
               for (int j = 0; j < data.length; j++)
                  data[j] = data[j] /= sample_std_dev;
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
