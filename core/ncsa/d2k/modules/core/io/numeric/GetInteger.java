package ncsa.d2k.modules.core.io.numeric;

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

*/
public class GetInteger extends UIModule
	implements java.io.Serializable, HasNames, HasProperties {

    protected int integer;
	public int getInteger(){
		return integer;
	}
	public void setInteger(int i){
		integer=i;
	}


    /**
       Provide a description of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
	return "Gets an int from the user and passes it as an Integer" ;

    }

   	public String getModuleName() {
		return "GetInteger";
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
	String []out = {"java.lang.Integer"};
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
	    return "The integer ";
			default:
	    return "No such output!";
	}
    }

   	public String getOutputName(int i) {
		if(i == 0)
			return "Integer";
		else
			return "No such output!";
	}

    /**
       Get the field name map for this module-view combination.
       @return The field name map.
    */
    protected String[] getFieldNameMapping() {
	//String[] fieldMap = {"tField"};
	return null;
    }

    /**
       Create the UserView object for this module-view combination.
       @return The UserView associated with this module.
    */
    protected UserView createUserView() {
	return new GetIntView();
    }
     /**
       This method is called by the UserView when the user is finished
       viewing the params.  This module pushes the table as an output.
       @param t the table to push as output.
    */
    public void moduleFinish(int i) {
		integer=i;
		this.pushOutput(new Integer(i), 0);
		//executionManager.moduleDone(this);
		viewDone("Done");
    }
    /**
       Provides a simple user interface to get file names.  The
       text values used in the Labels and textfields are
       properties of the module class.  If these properties
       are null, default values are used.
    */
    protected class GetIntView extends JUserInputPane
	{
		/** A label for the Int */
		protected JLabel label;
		/** A text field to show the path to file0 */
		protected DSJNumberField tField = new DSJNumberField(5);

		/** The module that creates this view.  We need a
		    reference to it so we can get and set its properties. */
		GetInteger parentModule;
		JButton doneButton;
		JButton abortButton;
	/**
	   Perform initializations here.
	   @param mod The module that created this UserView
	*/
	public void initView(ViewModule mod) {
		removeAll();
	    parentModule = (GetInteger)mod;

	    JPanel placeholder = new JPanel() {
		    public Dimension getPreferredSize() {
			return new Dimension(100, 0);
		    }
		};

	    JPanel p = new JPanel();
	    p.setLayout(new GridBagLayout());

		label=new JLabel("Type the Integer:");
	    Constrain.setConstraints(p, label, 0, 0, 1, 1,
				     GridBagConstraints.NONE,
				     GridBagConstraints.WEST,
				     1, 1);

	    int i = parentModule.getInteger();

		tField.setText(Integer.toString(i));

	    Constrain.setConstraints(p, tField, 1, 0, 2, 1,
				     GridBagConstraints.HORIZONTAL,
				     GridBagConstraints.WEST,
				     4, 1);

	    Constrain.setConstraints(p, placeholder, 0, 1, 4, 1,
				    GridBagConstraints.HORIZONTAL,
				    GridBagConstraints.WEST,
				    4, 1);

	   	JPanel buttonPanel=new JPanel();
		abortButton=new JButton("Abort");
		abortButton.addActionListener(new endButtonListener());
		buttonPanel.add(abortButton);

		doneButton = new JButton("Done");
		doneButton.addActionListener(new endButtonListener());
		buttonPanel.add(doneButton);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(p);
		add(buttonPanel);

	}
	private class endButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(e.getSource()==doneButton){
				int i=Integer.parseInt(tField.getText());
				parentModule.moduleFinish(i);
			}
			else
				parentModule.viewCancel();

		}
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
