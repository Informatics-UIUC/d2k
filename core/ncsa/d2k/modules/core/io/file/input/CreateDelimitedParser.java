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

    private boolean hasLabels = true;
    public void setHasLabels(boolean b) {
        hasLabels = b;
    }
    public boolean getHasLabels() {
        return hasLabels;
    }
    private boolean hasTypes = true;
    public void setHasTypes(boolean b) {
        hasTypes = b;
    }
    public boolean getHasTypes() {
        return hasTypes;
    }
    private boolean hasSpecDelim = false;
    public void setHasSpecDelim(boolean b) {
        hasSpecDelim = b;
    }
    public boolean getHasSpecDelim() {
        return hasSpecDelim;
    }

    /*public PropertyDescription [] getPropertiesDescriptions () {
        PropertyDescription[] retVal = new PropertyDescription[2];
        retVal[0] = new PropertyDescription("labelsRow", "Labels Row Index",
            "This is the index of the labels row in the file, or -1 if there is no labels row.");
        retVal[1] = new PropertyDescription("typesRow", "Types Row Index",
            "This is the index of the types row in the file, or -1 if there is no types row.");
        return retVal;
    }*/

    public CustomModuleEditor getPropertyEditor() {
        return new PropEdit();
    }

    private class PropEdit extends JPanel implements CustomModuleEditor {

        JRadioButton lblbtn;
        JLabel lbllbl;
        JTextField lblrow;
        JRadioButton typbtn;
        JTextField typrow;
        JLabel typlbl;
        JRadioButton delim;
        JTextField delimfld;
        JLabel dellbl;

        boolean lblChange = false;
        boolean typChange = false;
        boolean delChange = false;

        private PropEdit() {
            int lr = getLabelsRow();

            /*final JRadioButton*/ lblbtn = new JRadioButton("File Has Labels Row", getHasLabels());
            lblbtn.setToolTipText("Select this option if the file has a row of column labels.");
            /*final JLabel*/ lbllbl = new JLabel("Labels Row:");
            lbllbl.setToolTipText("This is the index of the labels row in the file.");
            /*final JTextField*/ lblrow = new JTextField(5);

            if(!getHasLabels()) {
                lbllbl.setEnabled(false);
                lblrow.setEnabled(false);
            }

            lblrow.setText(Integer.toString(getLabelsRow()));
            lblrow.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    lblChange = true;
                }
            });

            lblbtn.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    lblChange = true;
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

            /*final JRadioButton*/ typbtn = new JRadioButton("File Has Types Row", getHasTypes());
            typbtn.setToolTipText("Select this option if the file has a row of data types for columns.");
            /*final JTextField*/ typrow = new JTextField(5);
            /*final JLabel*/ typlbl = new JLabel("Types Row");
            typlbl.setToolTipText("This is the index of the types row in the file.");

            if(!getHasTypes()) {
                typrow.setEnabled(false);
                typlbl.setEnabled(false);
            }

            typrow.setText(Integer.toString(getTypesRow()));
            typrow.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    typChange = true;
                }
            });

            typbtn.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    typChange = true;
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

            /*final JRadioButton*/ delim = new JRadioButton("File Has User-specified Delimiter", getHasSpecDelim());
            /*final JTextField*/ delimfld = new JTextField(5);
            /*final JLabel*/ dellbl = new JLabel("Delimiter:");
            if(getHasSpecDelim())
                delimfld.setText(specDelim);
            else {
                delimfld.setEnabled(false);
                dellbl.setEnabled(false);
            }
            delimfld.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    delChange = true;
                }
            });

            delim.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    delChange = true;
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

            /*if(getLabelsRow() > 0)
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
            */

            // add the components for delimited properties
            setLayout(new GridBagLayout());
        /*Constrain.setConstraints(pnl, new JLabel("Delimited Format File Properties"), 0, 0, 2, 1,
                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                 1,1, new Insets(2, 2, 15, 2));
        */
            Constrain.setConstraints(this, lblbtn, 0, 0, 1, 1,
                                     GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1,1);
            Constrain.setConstraints(this, lbllbl, 1, 1, 1, 1,
                                     GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1,1);
            Constrain.setConstraints(this , lblrow, 2, 1, 1, 1,
                                     GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1,1);
            Constrain.setConstraints(this, typbtn, 0, 2, 1, 1,
                                     GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1,1);
            Constrain.setConstraints(this, typlbl, 1, 3, 1, 1,
                                     GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1,1);
            Constrain.setConstraints(this, typrow, 2, 3, 1, 1,
                                     GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1,1);
            Constrain.setConstraints(this, delim, 0, 4, 1, 1,
                                     GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1,1);
            Constrain.setConstraints(this, dellbl, 1, 5, 1, 1,
                                     GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
            Constrain.setConstraints(this, delimfld, 2, 5, 1, 1,
                                     GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
                                     1, 1);
        }

        public boolean updateModule() throws Exception {
            boolean didChange = false;

            if(lblChange) {
                if(!lblbtn.isSelected()) {
                    setLabelsRow(-1);
                    setHasLabels(false);
                }
                else {
                    String lrow = lblrow.getText();
                    int lrownum;
                    try {
                        lrownum = Integer.parseInt(lrow);
                    }
                    catch(NumberFormatException e) {
                        throw new Exception("The Labels Row was not a number.");
                    }
                    if(lrownum != getLabelsRow()) {
                        setLabelsRow(lrownum);
                        if(lrownum >= 0)
                            setHasLabels(true);
                        else
                            setHasLabels(false);
                        didChange = true;
                    }
                }
            }

            if(typChange) {
                if(!typbtn.isSelected()) {
                    setTypesRow(-1);
                    setHasTypes(false);
                }
                else {
                    String trow = typrow.getText();
                    int trownum;
                    try {
                        trownum = Integer.parseInt(trow);
                    }
                    catch(NumberFormatException e) {
                        throw new Exception("The Types Row was not a number.");
                    }
                    if(trownum != getTypesRow()) {
                        setTypesRow(trownum);
                        if(trownum >= 0)
                            setHasTypes(true);
                        else
                            setHasTypes(false);
                        didChange = true;
                    }
                }
            }

            if(delChange) {
                if(!delim.isSelected()) {
                    setHasSpecDelim(false);
                    setSpecDelim(null);
                }
                else {
                    String dd = null;
                    if(delim.isSelected()) {
                        dd = delimfld.getText(); // dd = delim.getText();
												//System.out.println("dd.lenght " + dd.length() + " dd " + dd);
                        if(dd.length() > 1)
                            throw new Exception("The delimiter must be one character long.");
                    }
                    if(dd != getSpecDelim()) {
                        setSpecDelim(dd);
                        setHasSpecDelim(true);
                        didChange = true;
                    }
                }
            }
            return didChange;
        }
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
        return "File Name";
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
        s.append("<p>Properties are used to specify the delimiter, the labels row number, ");
        s.append("and the types row number. The row numbers are indexed from zero.");
        s.append("<p>Data Type Restrictions: ");
        s.append("The input to this module must be a delimited file. If the file is");
				s.append("large a java OutOfMemory error might occur");
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

        int lbl = -1;
        if(getHasLabels())
            lbl = getLabelsRow();
        int typ = -1;
        if(getHasTypes())
            typ = getTypesRow();


	       if(!getHasSpecDelim())
             df = new DelimitedFileParser(file, lbl, typ);
         else {
             String s = getSpecDelim();
             char[] del = s.toCharArray();
             df = new DelimitedFileParser(file, lbl, typ, del[0]);
         }

        pushOutput(df, 0);
    }
}
// QA Comments
// 2/14/03 - Handed off to QA by David Clutter
// 2/16/03 - Anca started QA process.  Updated module info and changed input
//           name to File Name.
// 2/18/03 - checked into basic.
// END QA Comments
