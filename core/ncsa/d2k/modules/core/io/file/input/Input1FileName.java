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
public class Input1FileName extends InputModule {

    public String[] getInputTypes() {
        return null;
    }

    public String[] getOutputTypes() {
        String[] out = {"java.lang.String"};
        return out;
    }

    public String getInputInfo(int i) {
        return "";
    }

    public String getOutputInfo(int i) {
        return "The absolute pathname of the specified file.";
    }

    public String getOutputName(int i) {
        return "filename";
    }

    public String getModuleInfo() {
        return "Input the name of a single file.  The file name is input in "+
                "the properties editor.  Use the 'Browse' button to select "+
                "the file from your local filesytem.";
    }

    /** The file name property */
    private String fileName;

    public void setFileName(String s) {
        fileName =  s;
        //jtf.setText(fileName);
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * Return a custom property editor.
     * @return
     */
    public CustomModuleEditor getPropertyEditor() {
        return new PropEdit();
    }

    private class PropEdit extends JPanel implements CustomModuleEditor {
        private JTextField jtf;

        private PropEdit() {
            setLayout(new GridBagLayout());
            jtf = new JTextField(20);
            jtf.setText(getFileName());

            //jtf = new JTextField(15);

            JButton b0 = new JButton("Browse");

            JPanel p2 = new JPanel();
            p2.setLayout(new BorderLayout());
            p2.add(jtf, BorderLayout.CENTER);
            p2.add(b0, BorderLayout.EAST);

            JPanel p1 = new JPanel();
            p1.setLayout(new GridLayout(2, 1));
            p1.add(new JLabel("File Name"));
            p1.add(p2);

            Constrain.setConstraints(this, p1, 0, 0, 1, 1,
                                     GridBagConstraints.NONE,
                                     GridBagConstraints.WEST, 0, 0);

            Constrain.setConstraints(this, new JPanel(), 0, 2, 2, 1,
                                     GridBagConstraints.BOTH,
                                     GridBagConstraints.WEST, 2, 1);

            b0.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();

                    String d = getFileName();
                    if(d != null)
                        chooser.setSelectedFile(new File(d));

                    // set the title of the FileDialog
                    StringBuffer sb = new StringBuffer("Select File");
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
                        //System.out.println("HERE "+fileName);
                        jtf.setText(fn);
                        //setFileName(fn);
                    }
                }
            });
        }

        public boolean updateModule() throws Exception {
            String f0 = jtf.getText();
            if(f0 != getFileName()) {
                setFileName(f0);
                return true;
            }
            return false;
        }
    }

    public void doit() throws Exception {
        String fn = getFileName();
        if(fn == null || fn.length() == 0)
            throw new Exception(getAlias()+": No file name was given.");
        setFileName(fn);
        pushOutput(fn, 0);
    }
}