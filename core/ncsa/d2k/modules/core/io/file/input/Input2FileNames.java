package ncsa.d2k.modules.core.io.file.input;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import ncsa.d2k.core.modules.*;
import ncsa.gui.*;

/**
 * InputFileName allows the user to input the name of a single file.  The file
 * name is input in the properties editor.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class Input2FileNames extends InputModule {

    public String[] getInputTypes() {
        return null;
    }

    public String[] getOutputTypes() {
        String[] out = {"java.lang.String", "java.lang.String"};
        return out;
    }

    public String getInputInfo(int i) {
        return "";
    }

    public String getOutputInfo(int i) {
        return "The absolute pathname of the specified file.";
    }

    public String getOutputName(int i) {
        if(i == 0)
            return "filename1";
        else
            return "filename2";
    }

    public String getModuleInfo() {
        return "Input the names of two files.  The file names are input in "+
                "the property editor for this module.  Use the 'Browse' buttons "+
                "to select the files from your local filesystem.";
    }

    /** file name0 property */
    private String fileName0;

    public void setFileName0(String s) {
        fileName0 =  s;
        //fn0.setText(fileName0);
    }

    public String getFileName0() {
        return fileName0;
    }

    /** file name1 property */
    private String fileName1;

    public void setFileName1(String s) {
        fileName1 = s;
        //fn1.setText(fileName1);
    }

    public String getFileName1() {
        return fileName1;
    }

    /**
     * Return a custom property editor.
     * @return
     */
    public CustomModuleEditor getPropertyEditor() {
        return new PropEdit();
    }

    private class PropEdit extends JPanel implements CustomModuleEditor {
        private JTextField fn0;
        private JTextField fn1;

        PropEdit() {
            setLayout(new GridBagLayout());
            fn0 = new JTextField(20);
            fn1 = new JTextField(20);

            // first file name
            JButton b0 = new JButton("Browse");

            JPanel p2 = new JPanel();
            p2.setLayout(new BorderLayout());
            p2.add(fn0, BorderLayout.CENTER);
            p2.add(b0, BorderLayout.EAST);

            JPanel p1 = new JPanel();
            p1.setLayout(new GridLayout(2, 1));
            p1.add(new JLabel("File Name 1"));
            p1.add(p2);

            // second file name
            JButton b1 = new JButton("Browse");
            JPanel p3 = new JPanel();
            p3.setLayout(new BorderLayout());
            p3.add(fn1, BorderLayout.CENTER);
            p3.add(b1, BorderLayout.EAST);

            JPanel p4 = new JPanel();
            p4.setLayout(new GridLayout(2, 1));
            p4.add(new JLabel("File Name 2"));
            p4.add(p3);

            Constrain.setConstraints(this, p1, 0, 0, 1, 1,
                                     GridBagConstraints.NONE,
                                     GridBagConstraints.WEST, 0, 0);

            Constrain.setConstraints(this, p4, 0, 1, 1, 1,
                                     GridBagConstraints.NONE,
                                     GridBagConstraints.WEST, 0, 0);

            Constrain.setConstraints(this, new JPanel(), 0, 3, 2, 1,
                                     GridBagConstraints.BOTH,
                                     GridBagConstraints.WEST, 2, 1);

            b0.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();

                    String d = getFileName0();
                    if(d != null)
                        chooser.setSelectedFile(new File(d));

                    // set the title of the FileDialog
                    StringBuffer sb = new StringBuffer("Select File 1");
                    chooser.setDialogTitle(sb.toString());

                    // get the file
                    String fn;
                    int retVal = chooser.showOpenDialog(null);
                    if(retVal == JFileChooser.APPROVE_OPTION)
                        fn = chooser.getSelectedFile().getAbsolutePath();
                    else
                        return;

                    if(fn != null) {
                        // display the path to the chosen file
                        //jtf.setText(fileName);
                        //setFileName0(fn);
                        fn0.setText(fn);
                    }
                }
            });

            b1.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();

                    String d = getFileName1();
                    if(d != null)
                        chooser.setSelectedFile(new File(d));

                    // set the title of the FileDialog
                    StringBuffer sb = new StringBuffer("Select File 2");
                    chooser.setDialogTitle(sb.toString());

                    // get the file
                    String fn;
                    int retVal = chooser.showOpenDialog(null);
                    if(retVal == JFileChooser.APPROVE_OPTION)
                        fn = chooser.getSelectedFile().getAbsolutePath();
                    else
                        return;

                    if(fn != null) {
                        // display the path to the chosen file
                        //jtf.setText(fileName);
                        //setFileName1(fn);
                        fn1.setText(fn);
                    }
                }
            });
        }

        public boolean updateModule() throws Exception {
            boolean didChange = false;
            String f0 = fn0.getText();
            if(f0 != getFileName0()) {
                setFileName0(f0);
                didChange = true;
            }
            String f1 = fn1.getText();
            if(f1 != getFileName1()) {
                setFileName1(f1);
                didChange = true;
            }
            return didChange;
        }
    }

    public void doit() throws Exception {
        String fn0 = getFileName0();
        String fn1 = getFileName1();
        if(fn0 == null || fn0.length() == 0)
            throw new Exception(getAlias()+": File name 1 was not given.");

        if(fn1 == null || fn1.length() == 0)
            throw new Exception(getAlias()+": File name 2 was not given.");
        setFileName0(fn0);
        setFileName1(fn1);
        pushOutput(fn0, 0);
        pushOutput(fn1, 1);
    }
}