package ncsa.d2k.modules.core.io.file.gui;


import java.awt.*;
import java.awt.event.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.*;
import ncsa.d2k.userviews.widgets.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.userviews.AddField;
import ncsa.gui.Constrain;
import java.util.Hashtable;
import javax.swing.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.userviews.widgets.swing.*;

/**
   This module allows a user to choose the name of four files.  The UserView
   is a simple Panel with three components for each file to choose: a Label,
   a TextArea, and a Button.  When a Button is pressed, a FileDialog will
   pop up, and the user can choose a file.  The name of the selected file
   will be displayed in the TextArea.  The path of the last file chosen and
   a value for the Label are saved as properties of this module.
   @author David Clutter
*/
public class Get4FileNames extends UIModule
	 {

    /** The absolute path to file0 */
    String file0;
    /** The label to display for file0 */
    String label0;

    /** The absolute path to file1 */
    String file1;
    /** The label to display for file1 */
    String label1;

    /** The absolute path to file2 */
    String file2;
    /** The label to display for file2 */
    String label2;

    /** The absolute path to file3 */
    String file3;
    /** The label to display for file3 */
    String label3;

    /**
	Set the pathname for file0
	@param s The new path for file0
    */
    public void setFile0(String s) {
	file0 = s;
    }

    /**
	Get the pathname for file0
	@return The absolute path for file0
    */
    public String getFile0() {
	return file0;
    }

    /**
	Set the text to show in l0 in Get4FileView
	@param s The new text to show in l0
    */
    public void setLabel0(String s) {
	label0 = s;
    }

    /**
       Get the text to show in l0 in Get4FileView
       @return The text shown in l0
    */
    public String getLabel0() {
	return label0;
    }

    /**
	Set the pathname for file1
	@param s The new path for file1
    */
    public void setFile1(String s) {
	file1 = s;
    }

    /**
	Get the pathname for file1
	@return The absolute path for file1
    */
    public String getFile1() {
	return file1;
    }

    /**
		Set the text to show in l1 in Get4FileView
		@param s The new text to show in l1
    */
    public void setLabel1(String s) {
	label1 = s;
    }

    /**
       Get the text to show in l1 in Get4FileView
       @return The text shown in l1
	*/
    public String getLabel1() {
	return label1;
    }

    /**
	Set the pathname for file2
	@param s The new path for file2
    */
    public void setFile2(String s) {
	file2 = s;
    }

    /**
	Get the pathname for file2
	@return The absolute path for file2
    */
    public String getFile2() {
	return file2;
    }

    /**
	Set the text to show in l2 in Get4FileView
	@param s The new text to show in l2
    */
    public void setLabel2(String s) {
	label2 = s;
    }

    /**
       Get the text to show in l2 in Get4FileView
       @return The text shown in l2
    */
    public String getLabel2() {
		return label2;
    }

    /**
	Set the pathname for file3
	@param s The new path for file3
    */
    public void setFile3(String s) {
	file3 = s;
    }

    /**
	Get the pathname for file3
	@return The absolute path for file3
    */
    public String getFile3() {
	return file3;
    }

    /**
	Set the text to show in l3 in Get4FileView
	@param s The new text to show in l3
    */
    public void setLabel3(String s) {
	label3 = s;
    }

    /**
       Get the text to show in l3 in Get4FileView
       @return The text shown in l3
    */
    public String getLabel3() {
	return label3;
    }

    /**
		Provide a description of this module.
		@return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module allows the user to choose four files using file dialogs. The     absolute pathnames of the files are the outputs.  </body></html>";
	}

	public String getModuleName() {
		return "Get4Files";
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
		String[] types = {"java.lang.String","java.lang.String","java.lang.String","java.lang.String"};
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
			case 0:
				return "file0";
			case 1:
				return "file1";
			case 2:
				return "file2";
			case 3:
				return "file3";
			default: return "NO SUCH OUTPUT!";
		}
	}

    /**
       Get the field name map for this module-view combination.
       @return The field name map.
    */
    public String[] getFieldNameMapping() {
	String[] fieldMap = {"tf0", "tf1", "tf2", "tf3"};
	return fieldMap;
    }

    /**
       Create the UserView object for this module-view combination.
       @return The UserView associated with this module.
    */
    protected UserView createUserView() {
	return new Get4FileView();
    }

    /**
       Provides a simple user interface to get file names.  The text values
       used in the Labels and textfields are properties of the module class.
       If these properties are null, default values are used.
    */
    class Get4FileView extends JUserInputPane implements ActionListener {
	/** A label for file0 */
	protected JLabel l0;
	/** A text field to show the path to file0 */
	protected DSJTextField tf0 = new DSJTextField(30);
	/** A button that brings up a FileDialog so the user can choose
	    file0 */
	protected JButton b0 = new JButton("Browse");

	/** A label for file1 */
	protected JLabel l1;
	/** A text field to show the path to file1 */
	protected DSJTextField tf1 = new DSJTextField(30);
	/** A button that brings up a FileDialog so the user can choose
	    file1 */
	protected JButton b1 = new JButton("Browse");

	/** A label for file2 */
	protected JLabel l2;
	/** A text field to show the path to file2 */
	protected DSJTextField tf2 = new DSJTextField(30);
	/** A button that brings up a FileDialog so the user can choose
	    file2 */
	protected JButton b2 = new JButton("Browse");

	/** A label for file3 */
	protected JLabel l3;
	/** A text field to show the path to file3 */
	protected DSJTextField tf3 = new DSJTextField(30);
	/** A button that brings up a FileDialog so the user can choose
	    file3 */
	protected JButton b3 = new JButton("Browse");

	/** The module that creates this view.  We need a reference to it so
	    we can get and set its properties. */
	Get4FileNames parentModule;

	/**
	   Perform initializations here.
	   @param mod The module that created this UserView
	*/
	public void initView(ViewModule mod) {
	    super.initView(mod);
	    parentModule = (Get4FileNames)mod;

	    JPanel placeholder = new JPanel();

	    JPanel p = new JPanel();
	    p.setLayout(new GridBagLayout());

	    // Add file0 stuff
	    // Try to get a label for file0 from Get4FileNames
	    String s = parentModule.getLabel0();
	    if(s != null)
		l0 = new JLabel(s);
	    else
		l0 = new JLabel("File0");
	    Constrain.setConstraints(p, l0, 0, 0, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);

	    // try to display the name of the last file chosen
	    s = parentModule.getFile0();
	    if(s != null)
		tf0.setText(s);
		tf0.setKey("tf0");

	    Constrain.setConstraints(p, tf0, 1, 0, 2, 1,
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST, 4, 1);

	    Constrain.setConstraints(p, b0, 3, 0, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);
	    b0.addActionListener(this);

	    // Add file1 stuff
	    // Try to get a label for file1 from Get4FileNames
	    s = parentModule.getLabel1();
	    if(s != null)
		l1 = new JLabel(s);
	    else
		l1 = new JLabel("File1");
	    Constrain.setConstraints(p, l1, 0, 1, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);

	    // try to display the name of the last file chosen
	    s = parentModule.getFile1();
	    if(s != null)
		tf1.setText(s);
		tf1.setKey("tf1");

	    Constrain.setConstraints(p, tf1, 1, 1, 2, 1,
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST, 4, 1);

	    Constrain.setConstraints(p, b1, 3, 1, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);
	    b1.addActionListener(this);

	    // Add file2 stuff
	    // Try to get a label for file2 from Get4FileNames
	    s = parentModule.getLabel2();
	    if(s != null)
		l2 = new JLabel(s);
	    else
		l2 = new JLabel("File2");
	    Constrain.setConstraints(p, l2, 0, 2, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);

	    // try to display the name of the last file chosen
	    s = parentModule.getFile2();
	    if(s != null)
		tf2.setText(s);
		tf2.setKey("tf2");

	    Constrain.setConstraints(p, tf2, 1, 2, 2, 1,
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST, 4, 1);

	    Constrain.setConstraints(p, b2, 3, 2, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);
	    b2.addActionListener(this);

	    // Add file3 stuff
	    // Try to get a label for file3 from Get4FileNames
	    s = parentModule.getLabel3();
	    if(s != null)
		l3 = new JLabel(s);
	    else
		l3 = new JLabel("File3");
	    Constrain.setConstraints(p, l3, 0, 3, 2, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);

	    // try to display the name of the last file chosen
	    s = parentModule.getFile3();
	    if(s != null)
		tf3.setText(s);
		tf3.setKey("tf3");

	    Constrain.setConstraints(p, tf3, 1, 3, 2, 1,
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST, 4, 1);

	    Constrain.setConstraints(p, b3, 3, 3, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 1, 1);
	    b3.addActionListener(this);

	    Constrain.setConstraints(p, placeholder, 0, 4, 4, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST, 4, 1);
	    add(p);
	}

	/**
	   This method is called when inputs arrive to the ViewModule.
	   Get3FileNames does not receive any inputs, so this method is
	   not used.
	   @param input The input
	   @param index The index of the input
	*/
	public void setInput(Object input, int index) {
	}

	/**
	   When a button is pressed, display a file dialog.  Then save the
	   path of the chosen file as a String in the appropriate text area.
	   @param e An ActionEvent
	*/
	public void actionPerformed(ActionEvent e) {
	    Object src = e.getSource();

	    //Frame f = new Frame();
	    //FileDialog fd = new FileDialog(f, "Select file", FileDialog.LOAD);
	    String d;
		JFileChooser chooser = null;

	    if(src == b0) {
			d = parentModule.getFile0();
			// set the title of the FileDialog
			//fd.setTitle("Select "+l0.getText());
			if(d != null)
				chooser = new JFileChooser(d);
			else
				chooser = new JFileChooser();	

			StringBuffer sb = new StringBuffer("Select ");
			sb.append(l0.getText());
			chooser.setDialogTitle(sb.toString());
	    }
	    else if(src == b1) {
			d = parentModule.getFile1();
			// set the title of the FileDialog
			//fd.setTitle("Select "+l1.getText());

			if(d != null)
				chooser = new JFileChooser(d);
			else
				chooser = new JFileChooser();	

			StringBuffer sb = new StringBuffer("Select ");
			sb.append(l1.getText());
			chooser.setDialogTitle(sb.toString());
	    }
	    else if(src == b2) {
			d = parentModule.getFile2();
			// set the title of the FileDialog
			//fd.setTitle("Select "+l2.getText());
			if(d != null)
				chooser = new JFileChooser(d);
			else
				chooser = new JFileChooser();	

			StringBuffer sb = new StringBuffer("Select ");
			sb.append(l2.getText());
			chooser.setDialogTitle(sb.toString());
	    }
	    else if(src == b3) {
			d = parentModule.getFile3();
			// set the title of the FileDialog
			//fd.setTitle("Select "+l3.getText());

			if(d != null)
				chooser = new JFileChooser(d);
			else
				chooser = new JFileChooser();	

			StringBuffer sb = new StringBuffer("Select ");
			sb.append(l3.getText());
			chooser.setDialogTitle(sb.toString());
	    }

	    // get the file
	    //fd.show();
	    //String fileName = fd.getDirectory()+fd.getFile();
		String fileName;

		int retVal = chooser.showOpenDialog(null);
		if(retVal == JFileChooser.APPROVE_OPTION)
			fileName = chooser.getSelectedFile().getAbsolutePath();
		else
			return;

	    if(src == b0) {
			if(fileName != null)
		    	// display the path to the chosen file
		    	tf0.setText(fileName);
	    }
	    else if(src == b1) {
			if(fileName != null)
		    	// display the path to the chosen file
		    	tf1.setText(fileName);
	    }
	    else if(src == b2) {
			if(fileName != null)
		    	// display the path to the chosen file
		    	tf2.setText(fileName);
	    }
	    else if(src == b3) {
			if(fileName != null)
		    	// display the path to the chosen file
		    	tf3.setText(fileName);
	    }
	}

	/**
	   Overridden from superclass' implementation so that we can save
	   the text in the text fields as properties of the view module.

	   @param m the message hashtable
	   @param subCo the list of subcomponents
	*/
	public void compileResults(Hashtable m, Component [] subCo) {
	    for(int i=0; i < subCo.length; i++) {
		if(subCo[i] instanceof AddField) {
		    m = ((AddField)subCo[i]).addFields(m);
		    if(subCo[i] == tf0)
			parentModule.setFile0(tf0.getText());
		    else if(subCo[i] == tf1)
			parentModule.setFile1(tf1.getText());
		    else if(subCo[i] == tf2)
			parentModule.setFile2(tf2.getText());
		    else if(subCo[i] == tf3)
			parentModule.setFile3(tf3.getText());
		}

		if(subCo[i] instanceof Container)
		    compileResults(m, ((Container)subCo[i]).getComponents());
	    }
	}

    }
}
