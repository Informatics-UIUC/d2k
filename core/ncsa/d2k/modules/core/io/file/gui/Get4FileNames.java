package ncsa.d2k.modules.core.io.file.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;

/**
	This module allows a user to choose the name of one file.  The
	UserView is a simple Panel with three components: a Label, a
	TextArea, and a Button.  When the Button is pressed, a FileDialog
	will pop up, and the user can choose a file.  The name of the
	selected file will be displayed in the TextArea.  The path of the
	last file chosen and a value for the Label are saved as
	properties of this module.
	@author David Clutter
*/
public class Get4FileNames extends UIModule {

    private String defaultPath;
    /** The absolute path to a file */
    private String file0Path;
    /** The label to display in the UserView for file0 */
    private String label0;

    /** The absolute path to a file */
    private String file1Path;
    /** The label to display in the UserView for file0 */
    private String label1;

    /** The absolute path to a file */
    private String file2Path;
    /** The label to display in the UserView for file0 */
    private String label2;

    /** The absolute path to a file */
    private String file3Path;
    /** The label to display in the UserView for file0 */
    private String label3;

    /**
	    Set the pathname for file0
	    @param s The new path for file0
    */
    public void setFile0Path(String s) {
	    file0Path = s;
    }

    /**
	    Get the pathname for file0
	    @return The absolute path for file0
    */
    public String getFile0Path() {
	    return file0Path;
    }

    /**
	    Set the text to show in l0 in Get1FileView
	    @param s The new text to show in l0
    */
    public void setLabel0(String s) {
	    label0 = s;
    }

    /**
       Get the text to show in l0 in Get1FileView
       @return The text shown in l0
    */
    public String getLabel0() {
	    return label0;
    }

    public void setDefaultPath(String s) {
        defaultPath = s;
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    /**
        Set the pathname for file0
        @param s The new path for file0
    */
    public void setFile1Path(String s) {
        file1Path = s;
    }

    /**
        Get the pathname for file0
        @return The absolute path for file0
    */
    public String getFile1Path() {
        return file1Path;
    }

    /**
        Set the text to show in l0 in Get1FileView
        @param s The new text to show in l0
    */
    public void setLabel1(String s) {
        label1 = s;
    }

    /**
       Get the text to show in l0 in Get1FileView
       @return The text shown in l0
    */
    public String getLabel1() {
        return label1;
    }

    /**
        Set the pathname for file0
        @param s The new path for file0
    */
    public void setFile2Path(String s) {
        file2Path = s;
    }

    /**
        Get the pathname for file0
        @return The absolute path for file0
    */
    public String getFile2Path() {
        return file2Path;
    }

    /**
        Set the text to show in l0 in Get1FileView
        @param s The new text to show in l0
    */
    public void setLabel2(String s) {
        label2 = s;
    }

    /**
       Get the text to show in l0 in Get1FileView
       @return The text shown in l0
    */
    public String getLabel2() {
        return label2;
    }

    /**
        Set the pathname for file0
        @param s The new path for file0
    */
    public void setFile3Path(String s) {
        file3Path = s;
    }

    /**
        Get the pathname for file0
        @return The absolute path for file0
    */
    public String getFile3Path() {
        return file3Path;
    }

    /**
        Set the text to show in l0 in Get1FileView
        @param s The new text to show in l0
    */
    public void setLabel3(String s) {
        label3 = s;
    }

    /**
       Get the text to show in l0 in Get1FileView
       @return The text shown in l0
    */
    public String getLabel3() {
        return label3;
    }

    /**
       Provide a description of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module allows a user to choose the name of one file. When the user     presses the button, a FileDialog is shown to navigate to the approriate     directory and choose a file.  </body></html>";
	}

   	public String getModuleName() {
		return "Get4FileNames";
	}

    /**
       Return an array containing the input types to this module.
       @return The input types.
    */
    public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

    /**
       Return an array containing the output types of this module.
       @return The output types.
    */
    public String[] getOutputTypes() {
		String[] types = {"java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String"};
		return types;
	}

    /**
       Return the info for a particular input.
       @param i The index of the input to get info about
    */
    public String getInputInfo(int i) {
		switch (i) {
			default: return "No such input";
		}
	}

	public String getInputName(int i) {
		switch(i) {
			default: return "NO SUCH INPUT!";
		}
	}

    /**
       Return the info for a particular output.
       @param i The index of the output to get info about
    */
    public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The absolute path to the first selected file.";
            case 1: return "The absolute path to the second selected file.";
            case 2: return "The absolute path to the third selected file.";
            case 3: return "The absolute path to the fourth selected file.";
			default: return "No such output";
		}
	}

   	public String getOutputName(int i) {
		switch(i) {
			case 0: return "file0";
			case 1: return "file1";
			case 2: return "file2";
			case 3: return "file3";
			default: return "NO SUCH OUTPUT!";
		}
	}

    public void beginExecution() {
        if(view != null)
            view.doSetup();
    }

    /**
       Get the field name map for this module-view combination.
       @return The field name map.
    */
    protected String[] getFieldNameMapping() {
        return null;
    }

    private Get4FileView view;

    /**
       Create the UserView object for this module-view combination.
       @return The UserView associated with this module.
    */
    protected UserView createUserView() {
        view = new Get4FileView();
        return view;
    }

    /**
       Provides a simple user interface to get file names.  The
       text values used in the Labels and textfields are
       properties of the module class.  If these properties
       are null, default values are used.
    */
    protected class Get4FileView extends JUserPane {
	    /** A label for file0 */
	    private JLabel lbl0;
	    /** A text field to show the path to file0 */
	    private JTextField filename0 = new JTextField(30);

        private JLabel lbl1;
        private JTextField filename1 = new JTextField(30);

        private JLabel lbl2;
        private JTextField filename2 = new JTextField(30);
        private JLabel lbl3;
        private JTextField filename3 = new JTextField(30);

	    /**
	        Perform initializations here.
	        @param mod The module that created this UserView
	    */
	    public void initView(ViewModule mod) {
            doSetup();
	    }

        private void doSetup() {
            removeAll();

	        JPanel p = new JPanel();
	        p.setLayout(new GridBagLayout());

	        // Try to get a label for file0 from Get1FileName
	        String s = getLabel0();
	        if(s != null)
		        lbl0 = new JLabel(s);
	        else
		        lbl0 = new JLabel("File0");

	        Constrain.setConstraints(p, lbl0, 0, 0, 2, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST,
				     1, 1);

	        // try to display the name of the last file chosen
	        s = getFile0Path();

	        if(s != null)
		        filename0.setText(s);

	        Constrain.setConstraints(p, filename0, 0, 1, 2, 1,
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST,
				     4, 1);

            JButton b0 = new JButton("...");

	        Constrain.setConstraints(p, b0, 2, 1, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST,
				     1, 1);
            b0.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = null;

                    String d = getFile0Path();
                    // set the title of the FileDialog
                    //fd.setTitle("Select "+l0.getText());

                    chooser = new JFileChooser();
                    if(d != null)
                        chooser.setSelectedFile(new File(d));
                    else {
                        d = getDefaultPath();
                        if(d != null)
                            chooser.setCurrentDirectory(new File(d));
                    }

                    StringBuffer sb = new StringBuffer("Select ");
                    sb.append(lbl0.getText());
                    chooser.setDialogTitle(sb.toString());

                    // get the file
                    String fileName;
                    int retVal = chooser.showOpenDialog(null);
                    if(retVal == JFileChooser.APPROVE_OPTION)
                        fileName = chooser.getSelectedFile().getAbsolutePath();
                    else
                        return;

                    if(fileName != null)
                        // display the path to the chosen file
		   		        filename0.setText(fileName);
                }
            });


            // Try to get a label for file0 from Get1FileName
            s = getLabel1();
            if(s != null)
                lbl1 = new JLabel(s);
            else
                lbl1 = new JLabel("File1");

            Constrain.setConstraints(p, lbl1, 0, 2, 2, 1,
                     GridBagConstraints.NONE,
                     GridBagConstraints.WEST,
                     1, 1);

            // try to display the name of the last file chosen
            s = getFile1Path();

            if(s != null)
                filename1.setText(s);

            Constrain.setConstraints(p, filename1, 0, 3, 2, 1,
                     GridBagConstraints.HORIZONTAL,
                     GridBagConstraints.WEST,
                     4, 1);

            JButton b1 = new JButton("...");

            Constrain.setConstraints(p, b1, 2, 3, 1, 1,
                     GridBagConstraints.NONE,
                     GridBagConstraints.WEST,
                     1, 1);
            b1.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = null;

                    String d = getFile1Path();
                    // set the title of the FileDialog
                    //fd.setTitle("Select "+l0.getText());

                    chooser = new JFileChooser();
                    if(d != null)
                        chooser.setSelectedFile(new File(d));
                    else {
                        d = getDefaultPath();
                        if(d != null)
                            chooser.setCurrentDirectory(new File(d));
                    }

                    StringBuffer sb = new StringBuffer("Select ");
                    sb.append(lbl1.getText());
                    chooser.setDialogTitle(sb.toString());

                    // get the file
                    String fileName;
                    int retVal = chooser.showOpenDialog(null);
                    if(retVal == JFileChooser.APPROVE_OPTION)
                        fileName = chooser.getSelectedFile().getAbsolutePath();
                    else
                        return;

                    if(fileName != null)
                        // display the path to the chosen file
                           filename1.setText(fileName);
                }
            });

            //////
            // Try to get a label for file0 from Get1FileName
            s = getLabel2();
            if(s != null)
                lbl2 = new JLabel(s);
            else
                lbl2 = new JLabel("File2");

            Constrain.setConstraints(p, lbl2, 0, 4, 2, 1,
                     GridBagConstraints.NONE,
                     GridBagConstraints.WEST,
                     1, 1);

            // try to display the name of the last file chosen
            s = getFile2Path();

            if(s != null)
                filename2.setText(s);

            Constrain.setConstraints(p, filename2, 0, 5, 2, 1,
                     GridBagConstraints.HORIZONTAL,
                     GridBagConstraints.WEST,
                     4, 1);

            JButton b2 = new JButton("...");

            Constrain.setConstraints(p, b2, 2, 5, 1, 1,
                     GridBagConstraints.NONE,
                     GridBagConstraints.WEST,
                     1, 1);
            b2.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = null;

                    String d = getFile2Path();
                    // set the title of the FileDialog
                    //fd.setTitle("Select "+l0.getText());

                    chooser = new JFileChooser();
                    if(d != null)
                        chooser.setSelectedFile(new File(d));
                    else {
                        d = getDefaultPath();
                        if(d != null)
                            chooser.setCurrentDirectory(new File(d));
                    }

                    StringBuffer sb = new StringBuffer("Select ");
                    sb.append(lbl2.getText());
                    chooser.setDialogTitle(sb.toString());

                    // get the file
                    String fileName;
                    int retVal = chooser.showOpenDialog(null);
                    if(retVal == JFileChooser.APPROVE_OPTION)
                        fileName = chooser.getSelectedFile().getAbsolutePath();
                    else
                        return;

                    if(fileName != null)
                        // display the path to the chosen file
                           filename2.setText(fileName);
                }
            });

            //*****
            // Try to get a label for file0 from Get1FileName
            s = getLabel3();
            if(s != null)
                lbl3 = new JLabel(s);
            else
                lbl3 = new JLabel("File3");

            Constrain.setConstraints(p, lbl3, 0, 6, 2, 1,
                     GridBagConstraints.NONE,
                     GridBagConstraints.WEST,
                     1, 1);

            // try to display the name of the last file chosen
            s = getFile3Path();

            if(s != null)
                filename3.setText(s);

            Constrain.setConstraints(p, filename3, 0, 7, 2, 1,
                     GridBagConstraints.HORIZONTAL,
                     GridBagConstraints.WEST,
                     4, 1);

            JButton b3 = new JButton("...");

            Constrain.setConstraints(p, b3, 2, 7, 1, 1,
                     GridBagConstraints.NONE,
                     GridBagConstraints.WEST,
                     1, 1);
            b3.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = null;

                    String d = getFile3Path();
                    // set the title of the FileDialog
                    //fd.setTitle("Select "+l0.getText());

                    chooser = new JFileChooser();
                    if(d != null)
                        chooser.setSelectedFile(new File(d));
                    else {
                        d = getDefaultPath();
                        if(d != null)
                            chooser.setCurrentDirectory(new File(d));
                    }

                    StringBuffer sb = new StringBuffer("Select ");
                    sb.append(lbl3.getText());
                    chooser.setDialogTitle(sb.toString());

                    // get the file
                    String fileName;
                    int retVal = chooser.showOpenDialog(null);
                    if(retVal == JFileChooser.APPROVE_OPTION)
                        fileName = chooser.getSelectedFile().getAbsolutePath();
                    else
                        return;

                    if(fileName != null)
                        // display the path to the chosen file
                           filename3.setText(fileName);
                }
            });


            JPanel buttonPanel = new JPanel();
            JButton done = new JButton("Done");
            done.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    String file = filename0.getText();
                    String file1 = filename1.getText();
                    String file2 = filename2.getText();
                    String file3 = filename3.getText();
                    setFile0Path(file);
                    setFile1Path(file1);
                    setFile2Path(file2);
                    setFile3Path(file3);
                    pushOutput(file, 0);
                    pushOutput(file1, 1);
                    pushOutput(file2, 2);
                    pushOutput(file3, 3);
                    viewDone("Done");
                }
            });
            JButton abort = new JButton("Abort");
            abort.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    viewCancel();
                }
            });

            buttonPanel.add(abort);
            buttonPanel.add(done);

            setLayout(new BorderLayout());
	        add(p, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
	    }

	    /**
	        This method is called when inputs arrive to the
	        ViewModule.  Get1FileName does not receive any inputs,
	        so this method is not used.
	        @param input The input
	        @param index The index of the input
	    */
	    public void setInput(Object input, int index) {
	    }
    }
}
