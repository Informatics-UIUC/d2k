//package ncsa.d2k.modules.projects.clutter.rdr;
package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.gui.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Create a DelimitedFileReader for a file.
 */
public class CreateDelimitedParser extends InputModule {

    private int labelsRow = 0;
    public int getLabelsRow() {
        return labelsRow;
    }
    public void setLabelsRow(int i) {
        labelsRow = i;
    }

    private int typesRow = 1;
    public int getTypesRow() {
        return typesRow;
    }
    public void setTypesRow(int i) {
        typesRow = i;
    }

    private String specDelim = null;
    public void setSpecDelim(String s) {
        specDelim = s;
    }
    public String getSpecDelim() {
        return specDelim;
    }

    /*public PropertyDescription [] getPropertiesDescriptions () {
        PropertyDescription[] retVal = new PropertyDescription[2];
        retVal[0] = new PropertyDescription("labelsRow", "Labels Row Index",
            "This is the index of the labels row in the file, or -1 if there is no labels row.");
        retVal[1] = new PropertyDescription("typesRow", "Types Row Index",
            "This is the index of the types row in the file, or -1 if there is no types row.");
        return retVal;
    }*/

    public Component getPropertyEditor() {
        JPanel pnl = new JPanel();

        int lr = getLabelsRow();

        final JRadioButton lblbtn = new JRadioButton("File Has Labels Row", true);
        lblbtn.setToolTipText("Select this option if the file has a row of column labels.");
        final JLabel lbllbl = new JLabel("Labels Row:");
        lbllbl.setToolTipText("This is the index of the labels row in the file.");
        final JTextField lblrow = new JTextField(5);

        if(getLabelsRow() == -1) {
            lbllbl.setEnabled(false);
            lblrow.setEnabled(false);
        }

        lblrow.setText(Integer.toString(getLabelsRow()));

        lblbtn.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if(lblbtn.isSelected()) {
                    lbllbl.setEnabled(true);
                    lblrow.setEnabled(true);
                }
                else {
                    lbllbl.setEnabled(false);
                    lblrow.setEnabled(false);
                }
            }
        });

        final JRadioButton typbtn = new JRadioButton("File Has Types Row", true);
        typbtn.setToolTipText("Select this option if the file has a row of data types for columns.");
        final JTextField typrow = new JTextField(5);
        final JLabel typlbl = new JLabel("Types Row");
        typlbl.setToolTipText("This is the index of the types row in the file.");

        if(getTypesRow() == -1) {
            typrow.setEnabled(false);
            typlbl.setEnabled(false);
        }

        typrow.setText(Integer.toString(getTypesRow()));

        typbtn.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if(typbtn.isSelected()) {
                    typlbl.setEnabled(true);
                    typrow.setEnabled(true);
                }
                else {
                    typlbl.setEnabled(false);
                    typrow.setEnabled(false);
                }
            }
        });

        final JRadioButton delim = new JRadioButton("File Has User-specified Delimiter", false);
        final JTextField delimfld = new JTextField(5);
        final JLabel dellbl = new JLabel("Delimiter:");
        if(getSpecDelim() != null)
            delimfld.setText(specDelim);
        else {
            delimfld.setEnabled(false);
            dellbl.setEnabled(false);
        }

        delim.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if(delim.isSelected()) {
                    dellbl.setEnabled(true);
                    delimfld.setEnabled(true);
                }
                else {
                    dellbl.setEnabled(false);
                    delimfld.setEnabled(false);
                }
            }
        });

        if(getLabelsRow() > 0)
            lblbtn.setSelected(true);
        else
            lblbtn.setSelected(false);

        if(getTypesRow() > 0)
            typbtn.setSelected(true);
        else
            typbtn.setSelected(false);

        if(getSpecDelim() == null)
            delim.setSelected(false);
        else
            delim.setSelected(true);

        // add the components for delimited properties
        pnl.setLayout(new GridBagLayout());
        /*Constrain.setConstraints(pnl, new JLabel("Delimited Format File Properties"), 0, 0, 2, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1,1, new Insets(2, 2, 15, 2));
        */
        Constrain.setConstraints(pnl, lblbtn, 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1,1);
        Constrain.setConstraints(pnl, lbllbl, 1, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1,1);
        Constrain.setConstraints(pnl , lblrow, 2, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1,1);
        Constrain.setConstraints(pnl, typbtn, 0, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1,1);
        Constrain.setConstraints(pnl, typlbl, 1, 3, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1,1);
        Constrain.setConstraints(pnl, typrow, 2, 3, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1,1);
        Constrain.setConstraints(pnl, delim, 0, 4, 1, 1,
                                GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                1,1);
        Constrain.setConstraints(pnl, dellbl, 1, 5, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(pnl, delimfld, 2, 5, 1, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1, 1);

        return pnl;
    }

    /*private int inOutRow = -1;
    public int getInOutRow() {
        return inOutRow;
    }
    public void setInOutRow(int i) {
        inOutRow = i;
    }*/

    /*private int nomScalarRow = -1;
    public int getNomScalarRow() {
        return nomScalarRow;
    }
    public void setNomScalarRow(int i) {
        nomScalarRow = i;
    }*/

    public String[] getInputTypes() {
        String[] in = {"java.lang.String"};
        return in;
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.io.file.input.DelimitedFileParser"};
        return out;
    }

    public String getInputInfo(int i) {
        return "The absolute path to the delimited file.";
    }

    public String getOutputInfo(int i) {
        return "A DelimitedFileParser for the specified file.";
    }

    public String getInputName(int i) {
        return "File";
    }

    public String getOutputName(int i) {
        return "File Parser";
    }

    /*public String getModuleInfo() {
        return "Create a DelimitedFileReader for the specified file.  Properties: "+
                " labelsRow: the row of the file that contains labels, indexed from "+
                " zero.  If the file does not contain column labels, this should be "+
                " a negative number.  typesRow: the row of the file that contains types, "+
                " indexed from zero.  If the file does not contain column types, this "+
                " should be a negative number.";
    }*/

    public String getModuleInfo() {
        StringBuffer s = new StringBuffer("<p>Overview: ");
        s.append("This module creates a DelimitedFileParser for the specified file. ");
        s.append("<p>DetailedDescription: ");
        s.append("DelimitedFileParser is used to read data from a delimited file. ");
        s.append("The delimiter can be found automatically, or it can be input in the properties ");
        s.append("editor.  The file can contain a row of labels, and a row of data ");
        s.append("types.  These are also input in the properties editor.");
        s.append("<p>Properties are used to specify the delimiter, the labels row, ");
        s.append("and the types row.  The labels row and types row are indexed from ");
        s.append("zero.");
        s.append("<p>Data Type Restrictions: ");
        s.append("The input to this module must be a delimited file.");
        s.append("<p>Data Handling: ");
        s.append("The module does not destroy or modify the input data.");
        return s.toString();
    }

    public String getModuleName() {
        return "Create Delimited File Parser";
    }

    public void doit() throws Exception {
        String fn = (String)pullInput(0);
        File file = new File(fn);
        if(!file.exists())
            throw new FileNotFoundException(getAlias()+": "+file+" did not exist.");
        DelimitedFileParser df;
        df = new DelimitedFileParser(file, labelsRow, typesRow);
        // df = new DelimitedFileParser(file, labelsRow, typesRow, delimiter);
        pushOutput(df, 0);
    }
}