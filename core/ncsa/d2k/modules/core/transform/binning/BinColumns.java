package  ncsa.d2k.modules.core.transform.binning;

import  java.awt.*;
import  java.awt.event.*;
import  java.text.*;
import  java.util.*;
import  javax.swing.*;
import  javax.swing.event.*;
import  ncsa.d2k.core.modules.*;
import  ncsa.d2k.gui.*;
import  ncsa.d2k.modules.core.datatype.table.*;
import  ncsa.d2k.modules.core.vis.widgets.*;
import  ncsa.d2k.userviews.swing.*;
import  ncsa.gui.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;


/**
 * put your documentation comment here
 */
public class BinColumns extends UIModule {
    private static final String EMPTY = "",
    COLON = " : ", COMMA = ",", DOTS = "...",
    OPEN_PAREN = "(", CLOSE_PAREN = ")", OPEN_BRACKET = "[", CLOSE_BRACKET = "]";

    private NumberFormat nf;

    /**
     * put your documentation comment here
     * @return
     */
    public String getModuleName () {
        return  "Bin Columns";
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String getModuleInfo () {
        StringBuffer sb = new StringBuffer( "<p>Overview: ");
        sb.append( "This module allows a user to interactively bin data from a table.");

	sb.append( "</p><p>Detailed Description: ");
        sb.append( "This module provides a powerful interface that allows the user to ");
        sb.append( "control how numeric (continuous/scalar) and nominal (categorical) data in the input ");
        sb.append( "Table should be divided into bins. ");

        sb.append( "</p><p> Numeric data can be binned in four ways:<br>" );
	sb.append( "<U>Uniform range:</U><br>" );
	sb.append( "Enter a positive integer value for the number of bins. Bin Columns will divide the ");
	sb.append( "binning range evenly over these bins. <br> ");
        sb.append( "<U>Specified range:</U><br> ");
        sb.append( "Enter a comma-separated sequence of integer or floating-point values for the endpoints of each bin. <br> ");
        sb.append( "<U>Bin Interval:</u><br> ");
        sb.append( "Enter an integer or floating-point value for the width of each bin.<br> ");
        sb.append( "<u>Uniform Weight:</U><br> ");
	sb.append( "Enter a positive integer value for even binning with that number in each bin. ");

        sb.append( "</P><P>The user may also bin nominal data. ");

        sb.append( "</P><P>For further information on how to use this module the user may click on the \"Help\" button ");
	sb.append( "during run time and get a detailed description of the functionality." );

        sb.append( "</p><P>Missing and Empty Values Handling: In scalar columns, missing and empty values will be binned into " );
        sb.append( "\"UNKNOWN\". In nominal columns however, missing/empty values which are represented by " );
        sb.append( "'?' will be treated as a unique value in the column. In this case, if the user does not group " );
        sb.append( "the '?' and assign it a bin, then it would also be binned into \"UNKNOWN\".");

        sb.append( "</p><P>Data Handling:  This module does not change its input.  ");
	sb.append( "Rather, it outputs a Transformation that can later be applied to the original table data.</P>" );

	return sb.toString();
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getInputTypes () {
        String[] types =  {
            "ncsa.d2k.modules.core.datatype.table.Table"
        };
        return  types;
    }

    /**
     * put your documentation comment here
     * @param i
     * @return
     */
    public String getInputName (int i) {
        if (i == 0)
            return  "Table";
        return  "no such input";
    }

    /**
     * put your documentation comment here
     * @param i
     * @return
     */
    public String getInputInfo (int i) {
        switch (i) {
            case 0:
                return  "A Table with columns to bin.";
            default:
                return  "No such input";
        }
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getOutputTypes () {
        String[] types =  {
            "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform"
        };
        return  types;
    }


    /**
     * put your documentation comment here
     * @param i
     * @return
     */
    public String getOutputName (int i) {
        switch (i) {
            case 0:
                return "Binning Transformation";
            default:
                return  "no such output!";
        }
    }

    /**
     * put your documentation comment here
     * @param i
     * @return
     */
    public String getOutputInfo (int i) {
        switch (i) {
            case 0:
 		String s = "A Binning Transformation, as defined by the user via this module.  " +
			   "This output is typically connected to a <i>Create Bin Tree</i> module " +
			   "or to an <i>Apply Transformation</i> module where it is applied to the input Table. ";
		return s;
            default:
                return  "No such output";
        }
    }

   /**
    * This method returns and array of property descriptions.  In this case,
    * there are no user-editable properties so we return an empty array.
    * If this method isn't defined here, then the user sees the windowName
    * property which they really should not be shown.
    **/
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[0];
      return pds;
   }

    /**
     * put your documentation comment here
     * @return
     */
    protected UserView createUserView () {
        return  new BinColumnsView();
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String[] getFieldNameMapping () {
        return  null;
    }

    private class BinColumnsView extends JUserPane {
        private boolean setup_complete;
        private BinDescriptor currentSelectedBin;
        private HashMap columnLookup;
        private HashSet[] uniqueColumnValues;
        private JList numericColumnLabels, textualColumnLabels, currentBins;
        private DefaultListModel binListModel;
        private MutableTable tbl;

        /* numeric text fields */
        private JTextField uRangeField, specRangeField, intervalField, weightField;

        /* textual lists */
        private JList textUniqueVals, textCurrentGroup;
        private DefaultListModel textUniqueModel, textCurrentModel;

        /* textual text field */
        private JTextField textBinName;

        /* current selection fields */
        private JTextField curSelName;
        private JList currentSelectionItems;
        private DefaultListModel currentSelectionModel;
        private JButton abort, done;
        private JCheckBox createInNewColumn;

        int uniqueTextualIndex = 0;

        /**
         * put your documentation comment here
         */
        private BinColumnsView () {
            setup_complete = false;
            nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(3);
        }

        /**
         * put your documentation comment here
         * @param o
         * @param id
         */
        public void setInput (Object o, int id) {
            tbl = (MutableTable)o;

            // set column lables on the table if necessary...
            for (int i = 0; i < tbl.getNumColumns(); i++) {
               if (tbl.getColumnLabel(i) == null || tbl.getColumnLabel(i).length() == 0) {
                  tbl.setColumnLabel("column_" + i, i);
               }
            }

            // clear all text fields and lists...
            curSelName.setText(EMPTY);
            textBinName.setText(EMPTY);
            uRangeField.setText(EMPTY);
            specRangeField.setText(EMPTY);
            intervalField.setText(EMPTY);
            weightField.setText(EMPTY);
            columnLookup = new HashMap();
            uniqueColumnValues = new HashSet[tbl.getNumColumns()];
            binListModel.removeAllElements();
            DefaultListModel numModel = (DefaultListModel)numericColumnLabels.getModel(),
            txtModel = (DefaultListModel)textualColumnLabels.getModel();
            numModel.removeAllElements();
            txtModel.removeAllElements();

            textCurrentModel.removeAllElements();
            textUniqueModel.removeAllElements();
            uniqueTextualIndex = 0;

            for (int i = 0; i < tbl.getNumColumns(); i++) {
                columnLookup.put(tbl.getColumnLabel(i), new Integer(i));
                //if (table.getColumn(i) instanceof NumericColumn)
                if (tbl.isColumnScalar(i))
                    numModel.addElement(tbl.getColumnLabel(i));
                else {          //if (table.getColumn(i) instanceof TextualColumn) {
                    txtModel.addElement(tbl.getColumnLabel(i));
                    uniqueColumnValues[i] = uniqueValues(i);
                }
            }
            // finished...
            setup_complete = true;
        }

        /**
         * Create all of the components and add them to the view.
         */
        public void initView (ViewModule m) {
            currentBins = new JList();
            binListModel = new DefaultListModel();
            currentBins.setModel(binListModel);
            currentBins.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            currentBins.addListSelectionListener(new CurrentListener());
            // set up the numeric tab
            numericColumnLabels = new JList();
            numericColumnLabels.setModel(new DefaultListModel());
            // uniform range
            JOutlinePanel urangepnl = new JOutlinePanel("Uniform Range");
            uRangeField = new JTextField(5);
            JButton addURange = new JButton("Add");
            addURange.addActionListener(new AbstractAction() {

                public void actionPerformed (ActionEvent e) {
                    addUniRange();
                    uRangeField.setText(EMPTY);
                }
            });
            JButton showURange = new JButton("Show");
            showURange.addActionListener(new AbstractAction() {

                public void actionPerformed (ActionEvent e) {
                    HashMap colLook = new HashMap();
                    for (int i = 0; i < tbl.getNumColumns(); i++) {
                        if(tbl.isColumnNumeric(i)) {
                            colLook.put(tbl.getColumnLabel(i), new Integer(i));
                        }
                    }

                    String txt = uRangeField.getText();
                    //vered: replacing if else with try catch
                    //if(txt != null && txt.length() != 0)
                    try
                    {
                      int i = Integer.parseInt(txt);  //if this is successful then it is safe to go on with the preview
                      if(i<=1)
                         throw new NumberFormatException();


                        String selCol = (String)numericColumnLabels.getSelectedValue();
                        if(selCol == null) throw new NullPointerException();

                    final Histogram H = new UniformHistogram(new TableBinCounts(tbl),
                            uRangeField.getText(), colLook, selCol);
                    JD2KFrame frame = new JD2KFrame("Uniform Range");
                    frame.getContentPane().setLayout(new GridBagLayout());
                    Constrain.setConstraints(frame.getContentPane(), H, 0,
                            0, 3, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                            1, 1);
                    final JButton uniformAdd = new JButton("Add");
                    Constrain.setConstraints(frame.getContentPane(), new JLabel(""),
                            0, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST,
                            .33, 0);
                    Constrain.setConstraints(frame.getContentPane(), uniformAdd,
                            1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                            .34, 0);
                    Constrain.setConstraints(frame.getContentPane(), new JLabel(""),
                            2, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHEAST,
                            .33, 0);
                    uniformAdd.addActionListener(new AbstractAction() {
                        final JSlider uniformSlider = H.getSlider();

                        public void actionPerformed (ActionEvent e) {
                            uRangeField.setText(Integer.toString(uniformSlider.getValue()));
                            numericColumnLabels.clearSelection();
                            setSelectedNumericIndex(H.getSelection());
                            addUniRange();
                            uRangeField.setText(EMPTY);
                        }
                    });
                    frame.pack();
                    frame.setVisible(true);
                    }//try
                    //else
                    catch(NumberFormatException nfe){
                        // message dialog...must specify range
                      ErrorDialog.showDialog("Must specify a valid range - an integer greater than 1.", "Error");
                        uRangeField.setText(EMPTY);
                    }
                    catch(NullPointerException npe){
                      ErrorDialog.showDialog("You must select a column to bin.", "Error");
                    }
                    /*
                     final JSlider uniformSlider = H.getSlider();
                     uniformSlider.addChangeListener(new ChangeListener() {
                     public void stateChanged(ChangeEvent e) {
                     uRangeField.setText(Integer.toString(uniformSlider.getValue()));
                     }
                     });
                     */

                }
            });
            urangepnl.setLayout(new GridBagLayout());
            Constrain.setConstraints(urangepnl, new JLabel("Number of Bins"),
                                     0, 0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            Box b = new Box(BoxLayout.X_AXIS);
            b.add(uRangeField);
            b.add(addURange);
            b.add(showURange);
            Constrain.setConstraints(urangepnl, b, 1, 0, 1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.EAST, 1, 1);
            // specified range
            JOutlinePanel specrangepnl = new JOutlinePanel("Specified Range");
            specrangepnl.setLayout(new GridBagLayout());
            Constrain.setConstraints(specrangepnl, new JLabel("Range"), 0,
                                     0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            specRangeField = new JTextField(5);
            JButton addSpecRange = new JButton("Add");
            addSpecRange.addActionListener(new AbstractAction() {
                public void actionPerformed (ActionEvent e) {
                    addSpecifiedRange();
                    specRangeField.setText(EMPTY);
                }
            });
            JButton showSpecRange = new JButton("Show");
            showSpecRange.addActionListener(new AbstractAction() {

                public void actionPerformed (ActionEvent e) {

                  //@todo: add exception handling for illegal input
                    String txt = specRangeField.getText();
                    //vered: wrapped it all with try catch.
                    try{
                      if(txt == null || txt.length() == 0) {
                          // show message dialog
                          //vered
                          throw new IllegalArgumentException();

                          //ErrorDialog.showDialog("Must specify range.", "Error");
                         // return;
                      }


                      HashMap colLook = new HashMap();
                      for (int i = 0; i < tbl.getNumColumns(); i++) {
                          if(tbl.isColumnNumeric(i)) {
                              colLook.put(tbl.getColumnLabel(i), new Integer(i));
                          }
                      }//for
                      JD2KFrame frame = new JD2KFrame("Specified Range");
                      String col = (String)numericColumnLabels.getSelectedValue();
                      //vered:
                      if (col == null) throw new NullPointerException();

                      frame.getContentPane().add(new RangeHistogram(new TableBinCounts(tbl),
                              /*Histogram.HISTOGRAM_RANGE,*/ specRangeField.getText(), colLook, col));
                               frame.pack();
                               frame.setVisible(true);
                    }//try
                    catch(NullPointerException npe){
                       ErrorDialog.showDialog("You must select a column to bin.", "Error");
                    }
                    catch(IllegalArgumentException iae){
                      ErrorDialog.showDialog("Please enter a comma-separated sequence of\ninteger or floating-point values for\nthe endpoints of each bin. ", "Error");
                    }
                }//action performed.
            });
            Box b1 = new Box(BoxLayout.X_AXIS);
            b1.add(specRangeField);
            b1.add(addSpecRange);
            b1.add(showSpecRange);
            Constrain.setConstraints(specrangepnl, b1, 1, 0, 1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.EAST, 1, 1);
            // interval
            JOutlinePanel intervalpnl = new JOutlinePanel("Bin Interval");
            intervalpnl.setLayout(new GridBagLayout());
            intervalField = new JTextField(5);
            JButton addInterval = new JButton("Add");
            addInterval.addActionListener(new AbstractAction() {
                public void actionPerformed (ActionEvent e) {
                    addFromInterval();
                    intervalField.setText(EMPTY);
                }
            });
            JButton showInterval = new JButton("Show");
            showInterval.addActionListener(new AbstractAction() {

                public void actionPerformed (ActionEvent e) {
                    HashMap colLook = new HashMap();
                    for (int i = 0; i < tbl.getNumColumns(); i++) {
                        if(tbl.isColumnNumeric(i)) {
                            colLook.put(tbl.getColumnLabel(i), new Integer(i));
                        }
                    }
                    String txt = intervalField.getText();

                    double intrval;
                    try {
                       intrval = Double.parseDouble(txt);
                    }
                    catch(NumberFormatException ex) {
                       ErrorDialog.showDialog("You must specify a valid, positive interval.", "Error");
                       return;
                    }

                    if (intrval <= 0) {
                       ErrorDialog.showDialog("You must specify a valid, positive interval.", "Error");
                       return;
                    }

                    //vered: inserting a try catch to deal with inllegal argument exceptions
                    try{
                    //if(txt != null && txt.length() != 0) {
                      if(txt == null || txt.length() == 0) throw new IllegalArgumentException("Must specify an interval");

                    String col = (String)numericColumnLabels.getSelectedValue();
                    //vered: added this to prevent null pointer exception in init of histogram.
                    if(col == null) throw new NullPointerException();

                    final Histogram H = new IntervalHistogram(new TableBinCounts(tbl),
                            /*Histogram.HISTOGRAM_INTERVAL,*/ intervalField.getText(), colLook, col);
                             JD2KFrame frame = new JD2KFrame("Bin Interval");
                             frame.getContentPane().setLayout(new GridBagLayout());
                             Constrain.setConstraints(frame.getContentPane(), H, 0,
                                     0, 3, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                                     1, 1);
                             final JButton intervalAdd = new JButton("Add");
                             Constrain.setConstraints(frame.getContentPane(), new JLabel(""),
                                     0, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST,
                                     .33, 0);
                             Constrain.setConstraints(frame.getContentPane(), intervalAdd,
                                     1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH,
                                     .34, 0);
                             Constrain.setConstraints(frame.getContentPane(), new JLabel(""),
                                     2, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHEAST,
                                     .33, 0);
                             intervalAdd.addActionListener(new AbstractAction() {
                                 final JSlider intervalSlider = H.getSlider();

                                 /**
                                  * put your documentation comment here
                                  * @param e
                                  */
                                 public void actionPerformed (ActionEvent e) {
                                     int sel = H.getSelection();
                                     numericColumnLabels.clearSelection();
                                     setSelectedNumericIndex(sel);
                                     double interval = getInterval();
                                     intervalField.setText(Double.toString(H.getPercentage()*interval));
                                     addFromInterval();
                                     intervalField.setText(EMPTY);
                                 }
                             });
                             frame.pack();
                             frame.setVisible(true);
                    }//try
                    catch(IllegalArgumentException iae){
                      String str = iae.getMessage();
                      if(str == null || str.length() == 0) str = "You must specify a valid interval";
                      ErrorDialog.showDialog(str, "Error");
                    }
                    catch(NullPointerException npe){
                      ErrorDialog.showDialog("You must select a column to bin.", "Error");
                    }
                    /*vered - the if else replaced by try catch
                    else {
                        // message dialog...you must specify an interval
                        ErrorDialog.showDialog("Must specify interval.", "Error");
                    }*/
                             /*
                     final JSlider intervalSlider = H.getSlider();
                     intervalSlider.addChangeListener(new ChangeListener() {
                     public void stateChanged(ChangeEvent e) {
                     intervalField.setText(Integer.toString(intervalSlider.getValue()));
                     }
                     });
                     */

                }
            });
            Constrain.setConstraints(intervalpnl, new JLabel("Interval"), 0,
                                     0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            Box b2 = new Box(BoxLayout.X_AXIS);
            b2.add(intervalField);
            b2.add(addInterval);
            b2.add(showInterval);
            Constrain.setConstraints(intervalpnl, b2, 1, 0, 1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.EAST, 1, 1);
            // uniform weight
            JOutlinePanel weightpnl = new JOutlinePanel("Uniform Weight");
            weightpnl.setLayout(new GridBagLayout());
            weightField = new JTextField(5);
            JButton addWeight = new JButton("Add");
            addWeight.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    addFromWeight();
                    weightField.setText(EMPTY);
                }
            });
            // JButton showWeight = new JButton("Show");
            // showWeight.setEnabled(false);
            Constrain.setConstraints(weightpnl, new JLabel("Number in each bin"),
                                     0, 0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            Box b3 = new Box(BoxLayout.X_AXIS);
            b3.add(weightField);
            b3.add(addWeight);
            // b3.add(showWeight);
            Constrain.setConstraints(weightpnl, b3, 1, 0, 1, 1, GridBagConstraints.NONE,
                                     GridBagConstraints.EAST, 1, 1);
            // add all numeric components
            JPanel numpnl = new JPanel();
            numpnl.setLayout(new GridBagLayout());
            JScrollPane jsp = new JScrollPane(numericColumnLabels);
            //jsp.setColumnHeaderView(new JLabel("Numeric Columns"));
            Constrain.setConstraints(numpnl, jsp, 0, 0, 4, 1, GridBagConstraints.BOTH,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numpnl, urangepnl, 0, 1, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numpnl, specrangepnl, 0, 2, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numpnl, intervalpnl, 0, 3, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(numpnl, weightpnl, 0, 4, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            // textual bins
            JPanel txtpnl = new JPanel();
            txtpnl.setLayout(new GridBagLayout());
            textualColumnLabels = new JList();
            textualColumnLabels.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            textualColumnLabels.addListSelectionListener(new TextualListener());
            textualColumnLabels.setModel(new DefaultListModel());
            textUniqueVals = new JList();
            textUniqueModel = new DefaultListModel();
            textUniqueVals.setModel(textUniqueModel);
            textUniqueVals.setFixedCellWidth(100);
            textCurrentGroup = new JList();
            textCurrentGroup.setFixedCellWidth(100);
            textCurrentModel = new DefaultListModel();
            textCurrentGroup.setModel(textCurrentModel);
            JButton addTextToGroup = new JButton(">");
            addTextToGroup.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    if (!setup_complete)
                        return;
                    Object[] sel = textUniqueVals.getSelectedValues();
                    for (int i = 0; i < sel.length; i++) {
                        // textUniqueModel.removeElement(sel[i]);
                        if (!textCurrentModel.contains(sel[i]))
                            textCurrentModel.addElement(sel[i]);
                    }
                }
            });
            JButton removeTextFromGroup = new JButton("<");
            removeTextFromGroup.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    if (!setup_complete)
                        return;
                    Object[] sel = textCurrentGroup.getSelectedValues();
                    for (int i = 0; i < sel.length; i++) {
                        textCurrentModel.removeElement(sel[i]);
                        // textUniqueModel.addElement(sel[i]);
                    }
                }
            });
            JTabbedPane jtp = new JTabbedPane(JTabbedPane.TOP);
            jtp.add(numpnl, "Scalar");
            jtp.add(txtpnl, "Nominal");
            Box bx = new Box(BoxLayout.Y_AXIS);
            bx.add(Box.createGlue());
            bx.add(addTextToGroup);
            bx.add(removeTextFromGroup);
            bx.add(Box.createGlue());
            Box bx1 = new Box(BoxLayout.X_AXIS);
            JScrollPane jp1 = new JScrollPane(textUniqueVals);
            jp1.setColumnHeaderView(new JLabel("Unique Values"));
            bx1.add(jp1);
            bx1.add(Box.createGlue());
            bx1.add(bx);
            bx1.add(Box.createGlue());
            JScrollPane jp2 = new JScrollPane(textCurrentGroup);
            jp2.setColumnHeaderView(new JLabel("Current Group"));
            bx1.add(jp2);
            textBinName = new JTextField(10);
            JButton addTextBin = new JButton("Add");
            addTextBin.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    Object[] sel = textCurrentModel.toArray();

                    if (sel.length == 0) {
                       ErrorDialog.showDialog("You must select some nominal values to group.", "Error");
                       return;
                    }

                    Object val = textualColumnLabels.getSelectedValue();
                    int idx = ((Integer)columnLookup.get(val)).intValue();

                    String textualBinName;

                    if (textBinName.getText().length() == 0)
                       textualBinName = "bin" + uniqueTextualIndex++;
                     else
                        textualBinName = textBinName.getText();

                    BinDescriptor bd = createTextualBin(idx, textualBinName,
                            sel);

                    HashSet set = uniqueColumnValues[idx];
                    for (int i = 0; i < sel.length; i++) {
                        textCurrentModel.removeElement(sel[i]);
                        set.remove(sel[i]);
                    }
                    addItemToBinList(bd);
                    textBinName.setText(EMPTY);
                }
            });
            JOutlinePanel jop = new JOutlinePanel("Group");
            JPanel pp = new JPanel();
            pp.add(new JLabel("Name"));
            pp.add(textBinName);
            pp.add(addTextBin);
            jop.setLayout(new BoxLayout(jop, BoxLayout.Y_AXIS));
            jop.add(bx1);
            jop.add(pp);
            JScrollPane jp3 = new JScrollPane(textualColumnLabels);
            Constrain.setConstraints(txtpnl, jp3, 0, 0, 4, 1, GridBagConstraints.BOTH,
                                     GridBagConstraints.WEST, 1, 1);
            Constrain.setConstraints(txtpnl, jop, 0, 1, 4, 1, GridBagConstraints.HORIZONTAL,
                                     GridBagConstraints.WEST, 1, 1);
            // now add everything
            JPanel pq = new JPanel();
            pq.setLayout(new BorderLayout());
            JScrollPane jp4 = new JScrollPane(currentBins);
            jp4.setColumnHeaderView(new JLabel("Current Bins"));
            pq.add(jp4, BorderLayout.CENTER);
            JOutlinePanel jop5 = new JOutlinePanel("Current Selection");
            currentSelectionItems = new JList();
            currentSelectionItems.setVisibleRowCount(4);
            currentSelectionItems.setEnabled(false);
            currentSelectionModel = new DefaultListModel();
            currentSelectionItems.setModel(currentSelectionModel);
            JPanel pt = new JPanel();
            curSelName = new JTextField(10);
            pt.add(new JLabel("Name"));
            pt.add(curSelName);
            JButton updateCurrent = new JButton("Update");
            updateCurrent.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    if (!setup_complete)
                        return;
                    if (currentSelectedBin != null) {
                        currentSelectedBin.name = curSelName.getText();
                        currentBins.repaint();
                    }
                }
            });
            JButton removeBin = new JButton("Remove Bin");
            removeBin.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    if (!setup_complete)
                        return;
                    if (currentSelectedBin != null) {
                        int col = currentSelectedBin.column_number;
                        if (currentSelectedBin instanceof TextualBinDescriptor)
                            uniqueColumnValues[col].addAll(((TextualBinDescriptor)currentSelectedBin).vals);
                        binListModel.removeElement(currentSelectedBin);
                        currentSelectionModel.removeAllElements();
                        curSelName.setText(EMPTY);
                        // update the group
                        Object lbl = textualColumnLabels.getSelectedValue();
                        // gpape:
                        if (lbl != null) {
                            int idx = ((Integer)columnLookup.get(lbl)).intValue();
                            HashSet unique = uniqueColumnValues[idx];
                            textUniqueModel.removeAllElements();
                            textCurrentModel.removeAllElements();
                            Iterator i = unique.iterator();
                            while (i.hasNext())
                                textUniqueModel.addElement(i.next());
                        }
                    }
                }
            });
            // gpape:
            JButton removeAllBins = new JButton("Remove All");
            removeAllBins.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    if (!setup_complete)
                        return;
                    binListModel.removeAllElements();
                    currentSelectionModel.removeAllElements();
                    curSelName.setText(EMPTY);
                }
            });
            // gpape:
            createInNewColumn = new JCheckBox("Create in new column", false);
            Box pg = new Box(BoxLayout.X_AXIS);
            pg.add(updateCurrent);
            //pg.add(removeItems);
            pg.add(removeBin);
            pg.add(removeAllBins);
            // gpape:
            Box pg2 = new Box(BoxLayout.X_AXIS);
            pg2.add(createInNewColumn);
            jop5.setLayout(new BoxLayout(jop5, BoxLayout.Y_AXIS));
            jop5.add(pt);
            JScrollPane pane = new JScrollPane(currentSelectionItems);
            pane.setColumnHeaderView(new JLabel("Items"));
            jop5.add(pane);
            jop5.add(pg);
            jop5.add(pg2);
            JPanel bgpnl = new JPanel();
            bgpnl.setLayout(new BorderLayout());
            bgpnl.add(jp4, BorderLayout.CENTER);
            bgpnl.add(jop5, BorderLayout.SOUTH);
            // finally add everything to this
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            Box bxl = new Box(BoxLayout.X_AXIS);
            bxl.add(jtp);
            bxl.add(bgpnl);
            JPanel buttonPanel = new JPanel();
            abort = new JButton("Abort");
            abort.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    viewCancel();
                }
            });
            done = new JButton("Done");
            done.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    //binIt(createInNewColumn.isSelected());
                    Object[] tmp = binListModel.toArray();
                    BinDescriptor[] bins = new BinDescriptor[tmp.length];
                    for (int i = 0; i < bins.length; i++)
                        bins[i] = (BinDescriptor)tmp[i];
                    BinTransform bt = new BinTransform(bins, createInNewColumn.isSelected());
                    pushOutput(bt, 0);
                    viewDone("Done");
                }
            });
            JButton showTable = new JButton("Show Table");
            showTable.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    JD2KFrame frame = new JD2KFrame("Table");
                    frame.getContentPane().add(new TableMatrix(tbl));
                    frame.addWindowListener(new DisposeOnCloseListener(frame));
                    frame.pack();
                    frame.setVisible(true);
                }
            });
            JButton helpButton = new JButton("Help");
            helpButton.addActionListener(new AbstractAction() {

                /**
                 * put your documentation comment here
                 * @param e
                 */
                public void actionPerformed (ActionEvent e) {
                    HelpWindow help = new HelpWindow();
                    help.setVisible(true);
                }
            });
            buttonPanel.add(abort);
            buttonPanel.add(done);
            buttonPanel.add(showTable);
            buttonPanel.add(helpButton);
            setLayout(new BorderLayout());
            add(bxl, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        /*private void binIt(boolean new_column) {
        // boolean replace = true;
         Object[] tmp = binListModel.toArray();
         BinDescriptor[] bins = new BinDescriptor[tmp.length];
         for (int i = 0; i < bins.length; i++)
         bins[i] = (BinDescriptor)tmp[i];
        // need to figure out which columns have been binned:
         boolean[] binRelevant = new boolean[table.getNumColumns()];
         for (int i = 0; i < binRelevant.length; i++)
         binRelevant[i] = false;
         for (int i = 0; i < bins.length; i++)
         binRelevant[bins[i].column_number] = true;
        // if (replace) {
        //Column[] c = new Column[table.getNumColumns()];
        //for(int i = 0; i < c.length; i++)
        //  c[i] = table.getColumn(i);
         String[][] newcols =
         new String[table.getNumColumns()][table.getNumRows()];
         for(int i = 0; i < table.getNumColumns(); i++) {
         if (binRelevant[i])
         for(int j = 0; j < table.getNumRows(); j++) {
        // find the correct bin for this column
         boolean binfound = false;
         for(int k = 0; k < bins.length; k++) {
         if(bins[k].column_number == i) {
        // this has the correct column index
        //if(c[i] instanceof NumericColumn) {
         if(table.isColumnNumeric(i)) {
         if(bins[k].eval(table.getDouble(j, i))) {
         newcols[i][j] = bins[k].name;
         binfound = true;
         }
         }
         else {
         if(bins[k].eval(table.getString(j, i))) {
         newcols[i][j] = bins[k].name;
         binfound = true;
         }
         }
         }
         if(binfound) {
         binRelevant[i] = true;
         break;
         }
         }
         if(!binfound)
         newcols[i][j] = "Unknown";
         }
         }
        //StringColumn[] sc = new StringColumn[table.getNumColumns()];
         for(int i = 0; i < table.getNumColumns(); i++) {
         if (binRelevant[i]) {
        //sc[i] = new StringColumn(newcols[i]);
        //sc[i].setComment(table.getColumn(i).getComment());
         if (new_column) {
         if (binRelevant[i]) {
        //sc[i].setLabel(table.getColumnLabel(i) + " bin");
         table.addColumn(newcols[i]);
         table.setColumnLabel(table.getColumnLabel(i)+" bin", table.getNumColumns()-1);
         }
         }
         else {
         String oldLabel = table.getColumnLabel(i);
         table.setColumn(newcols[i], i);
         table.setColumnLabel(oldLabel, i);
         }
         }
         }
        // }
         }*/

        private HashSet uniqueValues (int col) {
            // count the number of unique items in this column
            HashSet set = new HashSet();
            for (int i = 0; i < tbl.getNumRows(); i++) {
                String s = tbl.getString(i, col);
                if (!set.contains(s))
                    set.add(s);
            }
            return  set;
        }

        /**
         * Get the column indices of the selected numeric columns.
         */
        private int[] getSelectedNumericIndices () {
            Object[] setVals = numericColumnLabels.getSelectedValues();
            int[] colIdx = new int[setVals.length];
            for (int i = 0; i < colIdx.length; i++)
                colIdx[i] = ((Integer)columnLookup.get(setVals[i])).intValue();
            return  colIdx;
        }

        /**
         * put your documentation comment here
         * @param index
         */
        private void setSelectedNumericIndex (int index) {
            numericColumnLabels.setSelectedIndex(index);
        }

        /**
         * Get the range of the first selected column.
         */
        private double getInterval () {
            int colIdx = numericColumnLabels.getSelectedIndex();
            double max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY;
            for (int i = 0; i < tbl.getNumRows(); i++) {
                double d = tbl.getDouble(i, colIdx);
                if (d < min)
                    min = d;
                if (d > max)
                    max = d;
            }
            return  max - min;
        }

        /**
         * Add uniform range bins
         */
        private void addUniRange () {
            int[] colIdx = getSelectedNumericIndices();
            //vered
            if(colIdx.length == 0){
             ErrorDialog.showDialog("Must select a column to bin.", "Error");
                return;
            }



            // uniform range is the number of bins...
            String txt = uRangeField.getText();
            int num;
            // ...get this number
            try {
                num = Integer.parseInt(txt);
                //vered:
                if(num<=1) throw new NumberFormatException();
            } catch (NumberFormatException e) {
              //vered:
              ErrorDialog.showDialog("Must specify an integer greater thatn 1.", "Error");
                return;
            }
            // find the maxes and mins
            double[] maxes = new double[colIdx.length];
            double[] mins = new double[colIdx.length];
            for (int i = 0; i < colIdx.length; i++) {
                maxes[i] = Double.NEGATIVE_INFINITY;
                mins[i] = Double.POSITIVE_INFINITY;
            }
            for (int i = 0; i < colIdx.length; i++) {
                // find the max and min and make equally spaced bins
                //NumericColumn nc = (NumericColumn)table.getColumn(colIdx[i]);
                for (int j = 0; j < tbl.getNumRows(); j++) {
                    double d = tbl.getDouble(j, colIdx[i]);
                    if (d > maxes[i])
                        maxes[i] = d;
                    if (d < mins[i])
                        mins[i] = d;
                }
                double[] binMaxes = new double[num - 1];
                double interval = (maxes[i] - mins[i])/(double)num;
                // add the first bin manually
                binMaxes[0] = mins[i] + interval;
                BinDescriptor nbd = createMinNumericBinDescriptor(colIdx[i],
                        binMaxes[0]);
                addItemToBinList(nbd);
                for (int j = 1; j < binMaxes.length; j++) {
                    binMaxes[j] = binMaxes[j - 1] + interval;
                    // now create the BinDescriptor and add it to the bin list
                    nbd = createNumericBinDescriptor(colIdx[i], binMaxes[j -
                            1], binMaxes[j]);
                    addItemToBinList(nbd);
                }
                nbd = createMaxNumericBinDescriptor(colIdx[i], binMaxes[binMaxes.length
                        - 1]);
                addItemToBinList(nbd);
            }
        }

        /**
         * Add bins from a user-specified range
         */
        private void addSpecifiedRange () {
            int[] colIdx = getSelectedNumericIndices();
            //vered:
              if(colIdx.length == 0){
             ErrorDialog.showDialog("You must select a column to bin.", "Error");
                return;
            }

            // specified range is a comma-separated list of bin maxes
            String txt = specRangeField.getText();

              //vered:
              if(txt == null || txt.length() == 0) {
                ErrorDialog.showDialog("Please enter a comma-separated sequence of\ninteger or floating-point values for\nthe endpoints of each bin. ", "Error");
                  return;
              }

              ArrayList al = new ArrayList();
              StringTokenizer strTok = new StringTokenizer(txt, COMMA);
              double[] binMaxes = new double[strTok.countTokens()];
              int idx = 0;
            try {
                  while (strTok.hasMoreElements()) {
                      String s = (String)strTok.nextElement();
                      binMaxes[idx++] = Double.parseDouble(s);
                  }
            } catch (NumberFormatException e) {
              //vered
              ErrorDialog.showDialog("Please enter a comma-separated sequence of\ninteger or floating-point values for\nthe endpoints of each bin. ", "Error");
                return;
            }
            // now create and add the bins
            for (int i = 0; i < colIdx.length; i++) {
                BinDescriptor nbd = createMinNumericBinDescriptor(colIdx[i],
                binMaxes[0]);
                addItemToBinList(nbd);
                for (int j = 1; j < binMaxes.length; j++) {
                    // now create the BinDescriptor and add it to the bin list
                    nbd = createNumericBinDescriptor(colIdx[i], binMaxes[j -
                    1], binMaxes[j]);
                    addItemToBinList(nbd);
                }
                nbd = createMaxNumericBinDescriptor(colIdx[i], binMaxes[binMaxes.length
                        - 1]);
                addItemToBinList(nbd);
            }
        }

        /**
         * Add bins from an interval - the width of each bin
         */
        private void addFromInterval () {
            int[] colIdx = getSelectedNumericIndices();
            //vered:
            if(colIdx.length == 0){
              ErrorDialog.showDialog("You must select a column to bin.", "Error");
                return;
            }

            // the interval is the width
            String txt = intervalField.getText();
            double intrval;
            try {
                intrval = Double.parseDouble(txt);
            } catch (NumberFormatException e) {
               //vered:
              ErrorDialog.showDialog("Must specify a positive number", "Error");
                return;
            }

            if (intrval <= 0) {
               ErrorDialog.showDialog("Must specify a positive number", "Error");
               return;
            }

            // find the mins and maxes
            double[] maxes = new double[colIdx.length];
            double[] mins = new double[colIdx.length];
            for (int i = 0; i < colIdx.length; i++) {
                maxes[i] = Double.NEGATIVE_INFINITY;
                mins[i] = Double.POSITIVE_INFINITY;
            }
            for (int i = 0; i < colIdx.length; i++) {
                // find the max and min
                //NumericColumn nc = (NumericColumn)table.getColumn(colIdx[i]);
                for (int j = 0; j < tbl.getNumRows(); j++) {
                    double d = tbl.getDouble(j, colIdx[i]);
                    if (d > maxes[i])
                        maxes[i] = d;
                    if (d < mins[i])
                        mins[i] = d;
                }
                // the number of bins is (max - min) / (bin width)
                int num = (int)Math.ceil((maxes[i] - mins[i])/intrval);
                double[] binMaxes = new double[num];
                binMaxes[0] = mins[i];
                // add the first bin manually
                BinDescriptor nbd = createMinNumericBinDescriptor(colIdx[i],
                        binMaxes[0]);
                addItemToBinList(nbd);
                for (int j = 1; j < binMaxes.length; j++) {
                    binMaxes[j] = binMaxes[j - 1] + intrval;
                    // now create the BinDescriptor and add it to the bin list
                    nbd = createNumericBinDescriptor(colIdx[i], binMaxes[j -
                            1], binMaxes[j]);
                    addItemToBinList(nbd);
                }
                nbd = createMaxNumericBinDescriptor(colIdx[i], binMaxes[binMaxes.length
                        - 1]);
                addItemToBinList(nbd);
            }
        }

        /**
         * Add bins given a weight. This will attempt to make bins with an
         * equal number of tallies in each.
         */
        private void addFromWeight () {
            int[] colIdx = getSelectedNumericIndices();

            //vered:
            if(colIdx.length == 0){
              ErrorDialog.showDialog("You must select a column to bin.", "Error");
                return;
            }

            // the weight is the number of items in each bin
            String txt = weightField.getText();
            int weight;
            try {
                weight = Integer.parseInt(txt);
                //vered:
                if(weight <= 0) throw new NumberFormatException();

            } catch (NumberFormatException e) {
              //vered:
              ErrorDialog.showDialog("Must specify a positive integer", "Error");
                return;
            }
            // we need to sort the data, but do not want to sort the
            // actual column, so we get a copy of the data
            for (int i = 0; i < colIdx.length; i++) {
                //NumericColumn nc = (NumericColumn)table.getColumn(colIdx[i]);
                double[] data = new double[tbl.getNumRows()];
                for (int j = 0; j < data.length; j++)
                    data[j] = tbl.getDouble(j, colIdx[i]);
                // sort it
                Arrays.sort(data);
                ArrayList list = new ArrayList();
                // now find the bin maxes...
                // loop through the sorted data.  the next max will lie at
                // data[curLoc+weight] items
                int curIdx = 0;
                while (curIdx < data.length - 1) {
                    curIdx += weight;
                    if (curIdx > data.length - 1)
                        curIdx = data.length - 1;
                    // now loop until the next unique item is found
                    boolean done = false;
                    if (curIdx == data.length - 1)
                        done = true;
                    while (!done) {
                        if (data[curIdx] != data[curIdx + 1])
                            done = true;
                        else
                            curIdx++;
                        if (curIdx == data.length - 1)
                            done = true;
                    }
                    // now we have the current index
                    // add the value at this index to the list
                    Double dbl = new Double(data[curIdx]);
                    list.add(dbl);
                }
                double[] binMaxes = new double[list.size()];
                for (int j = 0; j < binMaxes.length; j++)
                    binMaxes[j] = ((Double)list.get(j)).doubleValue();
                // add the first bin manually
                BinDescriptor nbd = createMinNumericBinDescriptor(colIdx[i],
                        binMaxes[0]);
                addItemToBinList(nbd);
                for (int j = 1; j < binMaxes.length-1; j++) {
                    // now create the BinDescriptor and add it to the bin list
                    nbd = createNumericBinDescriptor(colIdx[i], binMaxes[j -
                    1], binMaxes[j]);
                    addItemToBinList(nbd);
                }
                nbd = createMaxNumericBinDescriptor(colIdx[i], binMaxes[binMaxes.length
                        - 2]);
                addItemToBinList(nbd);
            }
        }

        /**
         * put your documentation comment here
         * @param idx
         * @param name
         * @param sel
         * @return
         */
        private BinDescriptor createTextualBin (int idx, String name, Object[] sel) {
            String[] vals = new String[sel.length];
            for (int i = 0; i < vals.length; i++)
                vals[i] = sel[i].toString();
            return  new TextualBinDescriptor(idx, name, vals, tbl.getColumnLabel(idx));
        }

        /**
         * Create a numeric bin that goes from min to max.
         */
        private BinDescriptor createNumericBinDescriptor (int col, double min,
                double max) {
            StringBuffer nameBuffer = new StringBuffer();
            nameBuffer.append(OPEN_PAREN);
            nameBuffer.append(nf.format(min));
            nameBuffer.append(COLON);
            nameBuffer.append(nf.format(max));
            nameBuffer.append(CLOSE_BRACKET);
            BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
                    min, max, tbl.getColumnLabel(col));
            return  nb;
        }

        /**
         * Create a numeric bin that goes from Double.NEGATIVE_INFINITY to max
         */
        private BinDescriptor createMinNumericBinDescriptor (int col, double max) {
            StringBuffer nameBuffer = new StringBuffer();
            nameBuffer.append(OPEN_BRACKET);
            nameBuffer.append(DOTS);
            nameBuffer.append(COLON);
            nameBuffer.append(nf.format(max));
            nameBuffer.append(CLOSE_BRACKET);
            BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
                    Double.NEGATIVE_INFINITY, max, tbl.getColumnLabel(col));
            return  nb;
        }

        /**
         * Create a numeric bin that goes from min to Double.POSITIVE_INFINITY
         */
        private BinDescriptor createMaxNumericBinDescriptor (int col, double min) {
            StringBuffer nameBuffer = new StringBuffer();
            nameBuffer.append(OPEN_PAREN);
            nameBuffer.append(nf.format(min));
            nameBuffer.append(COLON);
            nameBuffer.append(DOTS);
            nameBuffer.append(CLOSE_BRACKET);
            BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
                    min, Double.POSITIVE_INFINITY, tbl.getColumnLabel(col));
            return  nb;
        }

        /**
         * put your documentation comment here
         * @param bd
         */
        private void addItemToBinList (BinDescriptor bd) {
            binListModel.addElement(bd);
        }

        private class CurrentListener
                implements ListSelectionListener {

            /**
             * put your documentation comment here
             * @param e
             */
            public void valueChanged (ListSelectionEvent e) {
                if (!setup_complete)
                    return;
                if (!e.getValueIsAdjusting()) {
                    currentSelectedBin = (BinDescriptor)currentBins.getSelectedValue();
                    if (currentSelectedBin == null) {
                        currentSelectionModel.removeAllElements();
                        curSelName.setText(EMPTY);
                        return;
                    }
                    curSelName.setText(currentSelectedBin.name);
                    if (currentSelectedBin instanceof NumericBinDescriptor) {
                        currentSelectionModel.removeAllElements();
                        currentSelectionModel.addElement(currentSelectedBin.name);
                    }
                    else {
                        currentSelectionModel.removeAllElements();
                        HashSet hs = (HashSet)((TextualBinDescriptor)currentSelectedBin).vals;
                        Iterator i = hs.iterator();
                        while (i.hasNext())
                            currentSelectionModel.addElement(i.next());
                    }
                }
            }
        }       // BinColumnsView$CurrentListener

        private class TextualListener
                implements ListSelectionListener {

            /**
             * put your documentation comment here
             * @param e
             */
            public void valueChanged (ListSelectionEvent e) {
                if (!setup_complete)
                    return;
                if (!e.getValueIsAdjusting()) {
                    Object lbl = textualColumnLabels.getSelectedValue();
                    if (lbl != null) {
                        int idx = ((Integer)columnLookup.get(lbl)).intValue();
                        HashSet unique = uniqueColumnValues[idx];
                        textUniqueModel.removeAllElements();
                        textCurrentModel.removeAllElements();
                        Iterator i = unique.iterator();
                        while (i.hasNext())
                            textUniqueModel.addElement(i.next());
                    }
                }
            }
        }       // BinColumnsView$TextualListener

        private class HelpWindow extends JD2KFrame {

            /**
             * put your documentation comment here
             */
            public HelpWindow () {
                super("About Bin Columns");
                JEditorPane ep = new JEditorPane("text/html", getHelpString());
                ep.setCaretPosition(0);
                getContentPane().add(new JScrollPane(ep));
                setSize(400, 400);
            }
        }

        /**
         * put your documentation comment here
         * @return
         */
        private String getHelpString () {
            StringBuffer sb = new StringBuffer();
            sb.append("<html><body><h2>Bin Columns</h2>");
            sb.append("This module allows a user to interactively bin data from a table. ");
            sb.append("Numeric data can be binned in four ways:<ul>");
            sb.append("<li><b>Uniform range</b><br>");
            sb.append("Enter a positive integer value for the number of bins. Bin Columns will ");
            sb.append("divide the binning range evenly over these bins.");
            sb.append("<br><li><b>Specified range</b>:<br>");
            sb.append("Enter a comma-separated sequence of integer or floating-point values ");
            sb.append("for the endpoints of each bin.");
            sb.append("<br><li><b>Bin Interval</b>:<br>");
            sb.append("Enter an integer or floating-point value for the width of each bin.");
            sb.append("<br><li><b>Uniform Weight</b>:<br>");
            sb.append("Enter a positive integer value for even binning with that number in each bin.");
            sb.append("</ul>");
            sb.append("To bin numeric data, select columns from the \"Numeric Columns\" ");
            sb.append("selection area (top left) and select a binning method by entering a value ");
            sb.append("in the corresponding text field and clicking \"Add\". Data can ");
            sb.append("alternately be previewed in histogram form by clicking the corresponding ");
            sb.append("\"Show\" button (this accepts, but does not require, a value in the text field). ");
            sb.append("Uniform weight binning has no histogram (it would always look the same).");
            sb.append("<br><br>To bin nominal data, click the \"Nominal\" tab (top left) to bring ");
            sb.append("up the \"Nominal Columns\" selection area. Click on a column to show a list ");
            sb.append("of unique nominal values in that column in the \"Unique Values\" area below. ");
            sb.append("Select one or more of these values and click the right arrow button to group ");
            sb.append("these values. They can then be assigned a collective name as before.");
            sb.append("<br><br>To assign a name to a particular bin, select that bin in ");
            sb.append("the \"Current Bins\" selection area (top right), enter a name in ");
            sb.append("the \"Name\" field below, and click \"Update\". To bin the data and ");
            sb.append("output the new table, click \"Done\".");
            sb.append("</body></html>");
            return  sb.toString();
        }
    }           // BinColumnsView
}








class TableBinCounts implements BinCounts {

    private Table table;
    double[][] minMaxes;

    private static final int MIN = 0;
    private static final int MAX = 1;

    public TableBinCounts(Table t) {
        table = t;
        minMaxes = new double[table.getNumColumns()][];
        for(int i = 0; i < minMaxes.length; i++) {
            if(table.isColumnNumeric(i)) {
                minMaxes[i] = new double[2];

                double max = Double.NEGATIVE_INFINITY;
                double min = Double.POSITIVE_INFINITY;

                // get the max and min
                for (int j = 0; j < table.getNumRows(); j++) {
                    if (table.getDouble(j, i) < min)
                        min = table.getDouble(j, i);
                    if (table.getDouble(j, i) > max)
                        max = table.getDouble(j, i);
                }

                minMaxes[i][MIN] = min;
                minMaxes[i][MAX] = max;
            }
        }
    }

    public int getNumRows() {
        return table.getNumRows();
    }

    public double getMin(int col) {
        return minMaxes[col][MIN];
    }

    public double getMax(int col) {
        return minMaxes[col][MAX];
    }

    public double getTotal(int col) {
        double tot = 0;
        for(int i = 0; i < table.getNumRows(); i++)
            tot += table.getDouble(i, col);
        return tot;
    }

    public int[] getCounts(int col, double[] borders) {
        int[] counts = new int[borders.length+1];

        // some redundancy here
        boolean found;
        for (int i = 0; i < table.getNumRows(); i++) {
            found = false;
            for (int j = 0; j < borders.length; j++) {
                if (table.getDouble(i, col) <= borders[j] && !found) {
                    counts[j]++;
                    found = true;
                    break;
                }
            }
            if (!found)
                counts[borders.length]++;
        }

        return counts;
    }
}

//  QA comments:
// 2-27-03 vered started qa. added module description, exception handling.
// 2-27-03 commit back to core and back to greg - to reveiw bin nominal columns tab.
// 3-6-03 added to module info a note about missing values handling.
// 3-17 -3 anca: changed last bin in addFromWeigth
// 3-24-03 ruth: changed QA comment char so that they won't be seen by JavaDocs
//               changed input type to Table; deleted output Table port; updated
//               output port name; updated descriptions; added getPropertiesDescriptions
//               so no properties a user can't edit are shown.
//
