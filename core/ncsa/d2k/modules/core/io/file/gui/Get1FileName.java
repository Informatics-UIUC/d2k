package ncsa.d2k.modules.core.io.file.gui;
 
import java.awt.*;
import java.awt.event.*;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.UserView;

import ncsa.d2k.controller.userviews.UserInputPane;
import ncsa.d2k.controller.userviews.widgits.*;
import ncsa.d2k.controller.userviews.AddField;

import ncsa.gui.Constrain;

import java.util.Hashtable;

import javax.swing.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.controller.userviews.widgits.swing.*;

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
public class Get1FileName extends UIModule 
	implements java.io.Serializable, HasNames, HasProperties {
    
    /** The absolute path to a file */
    protected String file0;
    /** The label to display in the UserView for file0 */
    protected String label0;
    
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
    
    /**
       Provide a description of this module.
       @return A description of this module.	
    */
    public String getModuleInfo() {
	return "This module allows a user to choose the name of one "+ 
	    "file.  When the user presses the button, a FileDialog "+
	    "is shown to navigate to the approriate directory and "+
	    "choose a file.";
    }
   
   	public String getModuleName() {
		return "Get1File";
	}
    
    /**
       Return an array containing the input types to this module.
       @return The input types.
    */
    public String[] getInputTypes() {
	return null;
    }
	
    /**
       Return an array containing the output types of this module.
       @return The output types.
    */
    public String[] getOutputTypes() {
	String []out = {"java.lang.String"};
	return out;
    }
    
    /**
       Return the info for a particular input.
       @param i The index of the input to get info about
    */
    public String getInputInfo(int i) {
	return "No such input!";
    }
    
	public String getInputName(int i) {
		return "No such input!";
	}
	
    /**
       Return the info for a particular output.
       @param i The index of the output to get info about
    */
    public String getOutputInfo(int i) {
	switch(i) {
	case(0):
	    return "The absolute path to the selected "+
		"file.";
	default:
	    return "No such output!";
	}
    }
   
   	public String getOutputName(int i) {
		if(i == 0)
			return "file0";
		else
			return "No such output!";
	}
    
    /**
       Get the field name map for this module-view combination.
       @return The field name map.
    */
    protected String[] getFieldNameMapping() {
	String[] fieldMap = {"tf0"};
	return fieldMap;
    }
    
    /**
       Create the UserView object for this module-view combination.
       @return The UserView associated with this module.
    */
    protected UserView createUserView() {
	return new Get1FileView();
    }
    
    /**
       Provides a simple user interface to get file names.  The 
       text values used in the Labels and textfields are 
       properties of the module class.  If these properties 
       are null, default values are used.
    */
    protected class Get1FileView extends JUserInputPane 
	implements ActionListener {
	/** A label for file0 */
	protected JLabel l0;
	/** A text field to show the path to file0 */
	protected DSJTextField tf0 = new DSJTextField(30);
	/** A button that brings up a FileDialog so the user 
	    can choose file0 */
	protected JButton b0 = new JButton("Browse");
	
	/** The module that creates this view.  We need a 
	    reference to it so we can get and set its properties. */
	Get1FileName parentModule;
	
	/**
	   Perform initializations here.
	   @param mod The module that created this UserView
	*/		
	public void initView(ViewModule mod) {
	    super.initView(mod);
	    parentModule = (Get1FileName)mod;
	    
	    JPanel placeholder = new JPanel() {
		    public Dimension getPreferredSize() { 
			return new Dimension(200, 0);
		    }
		};

	    JPanel p = new JPanel();
	    p.setLayout(new GridBagLayout());
	    
	    // Add file0 stuff
	    
	    // Try to get a label for file0 from Get1FileName 
	    String s = parentModule.getLabel0();
	    if(s != null)
		l0 = new JLabel(s);
	    else
		l0 = new JLabel("File0");

	    Constrain.setConstraints(p, l0, 0, 0, 1, 1, 
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST,
				     1, 1);
	    
	    // try to display the name of the last file chosen
	    s = parentModule.getFile0();
	  
	    if(s != null) 
		tf0.setText(s);
		tf0.setKey("tf0");
			    
	    Constrain.setConstraints(p, tf0, 1, 0, 2, 1, 
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST,
				     4, 1);
	    
	    Constrain.setConstraints(p, b0, 3, 0, 1, 1, 
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST,
				     1, 1);
	    b0.addActionListener(this);
	    
	    Constrain.setConstraints(p, placeholder, 0, 1, 4, 1,
				    GridBagConstraints.HORIZONTAL,
				    GridBagConstraints.WEST,
				    4, 1);

	    add(p);
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
	
	/**
	   When a button is pressed, display a file dialog.  
	   Then save the path of the chosen file as a 
	   String in the appropriate text area.
	   @param e An ActionEvent
	*/
	public void actionPerformed(ActionEvent e) {
	    Object src = e.getSource();
	    
	    /*Frame f = new Frame();
	    FileDialog fd = new FileDialog(f, "Select file", 
					   FileDialog.LOAD);
		*/

		JFileChooser chooser = null;

	    if(src == b0) {
			String d = parentModule.getFile0();
			// set the title of the FileDialog
			//fd.setTitle("Select "+l0.getText());

			if(d != null)
				chooser = new JFileChooser(d);
			else
				chooser = new JFileChooser();
		
			StringBuffer sb = new StringBuffer("Select ");
			sb.append(l0.getText());
			chooser.setDialogTitle(sb.toString());

			/*if(d != null)
		    	fd.setFile(d);
			*/
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
			
	    if(src == b0)
			if(fileName != null)
		   		// display the path to the chosen file
		   		tf0.setText(fileName);
	}

	/**
	   Overridden from superclass' implementation so that we can save
	   the text in the text fields as properties of the view module.
	   
	   @param m the message hashtable
	   @param subCo the list of subcomponents
	*/
	public void compileResults(Hashtable m, Component [] subCo) {
	    for(int i =0; i < subCo.length; i++) {
		if(subCo[i] instanceof AddField) {
		    m = ((AddField)subCo[i]).addFields(m);
		    if(subCo[i] == tf0)
			parentModule.setFile0(tf0.getText());
		}
		    
		if(subCo[i] instanceof Container) 
		    compileResults(m, ((Container)subCo[i]).getComponents());
	    }
	}
    }
}
