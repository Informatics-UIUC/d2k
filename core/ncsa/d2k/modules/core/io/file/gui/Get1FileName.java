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
public class Get1FileName extends UIModule {

    private String defaultPath;
    /** The absolute path to a file */
    private String file0Path;
    /** The label to display in the UserView for file0 */
    private String label0;

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
       Provide a description of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module allows a user to choose the name of one file. When the user     presses the button, a FileDialog is shown to navigate to the approriate     directory and choose a file.  </body></html>";
	}

   	public String getModuleName() {
		return "Get1FileName";
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
		String[] types = {"java.lang.String"};
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
			case 0: return "The absolute path to the selected file.";
			default: return "No such output";
		}
	}

   	public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "file0";
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

    private Get1FileView view;

    /**
       Create the UserView object for this module-view combination.
       @return The UserView associated with this module.
    */
    protected UserView createUserView() {
        view = new Get1FileView();
        return view;
    }

    /**
       Provides a simple user interface to get file names.  The
       text values used in the Labels and textfields are
       properties of the module class.  If these properties
       are null, default values are used.
    */
    protected class Get1FileView extends JUserPane {
	    /** A label for file0 */
	    private JLabel lbl0;
	    /** A text field to show the path to file0 */
	    private JTextField filename0 = new JTextField(30);

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


            JPanel buttonPanel = new JPanel();
            JButton done = new JButton("Done");
            done.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    viewDone("Done");
                    String file = filename0.getText();
                    setFile0Path(file);
                    pushOutput(file, 0);
                }
            });
            JButton abort = new JButton("Abort");
            abort.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    viewCancel();
                }
            });

            buttonPanel.add(done);
            buttonPanel.add(abort);

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
