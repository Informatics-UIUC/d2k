package ncsa.d2k.modules.core.io.numeric;


import java.awt.*;
import java.awt.event.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.userviews.UserInputPane;
import ncsa.d2k.userviews.widgets.*;
import ncsa.d2k.userviews.AddField;
import ncsa.gui.Constrain;
import java.util.Hashtable;
import javax.swing.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.userviews.widgets.swing.*;

/**

*/
public class GetDoubleArray extends UIModule
	 {

    protected int arraySize;

	public int getArraySize(){
		return arraySize;
	}
	public void setArraySize(int i){
		arraySize=i;
	}
    protected double[] doubles;

    /**
       Provide a description of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Gets list of doubles from the user and passes it as a double[]  </body></html>";
	}

   	public String getModuleName() {
		return "GetDoubles";
	}

    /**
       Return an array containing the input types to this module.
       @return The input types.
    */
    public String[] getInputTypes() {
		String[] types = {"java.lang.Object"};
		return types;
	}

    /**
       Return an array containing the output types of this module.
       @return The output types.
    */
    public String[] getOutputTypes() {
		String[] types = {"[D"};
		return types;
	}

    /**
       Return the info for a particular input.
       @param i The index of the input to get info about
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "a dummy object for triggering";
			default: return "No such input";
		}
	}

	public String getInputName(int i) {
		switch(i) {
			case 0:
				return "trigger";
			default: return "NO SUCH INPUT!";
		}
	}

    /**
       Return the info for a particular output.
       @param i The index of the output to get info about
    */
    public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The double array ";
			default: return "No such output";
		}
	}

   	public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "dArray";
			default: return "NO SUCH OUTPUT!";
		}
	}

    /**
       Get the field name map for this module-view combination.
       @return The field name map.
    */
    protected String[] getFieldNameMapping() {
	return null;
    }

    /**
       Create the UserView object for this module-view combination.
       @return The UserView associated with this module.
    */

   	private GetDoubleArrayView dav;

	public UserView createUserView(){
		dav=new GetDoubleArrayView();
		//dav.initView(this);
		return dav;
	}

    /**
       This method is called by the UserView when the user is finished
       viewing the params.  This module pushes the table as an output.
       @param t the table to push as output.
    */
    public void moduleFinish(double[] da) {
		doubles=da;
		this.pushOutput(da, 0);
		//System.out.println("da finishing");
		//executionManager.moduleDone(this);
		viewDone("Done");
    }

    /**
       Provides a simple user interface to get file names.  The
       text values used in the Labels and textfields are
       properties of the module class.  If these properties
       are null, default values are used.
    */
    protected class GetDoubleArrayView extends JUserPane
	{
		/** labels for the fields */
		protected JLabel[] labels;
		/** A text field to show the path to file0 */
		protected JTextField[] tFields;

		/** The module that creates this view.  We need a
		    reference to it so we can get and set its properties. */
		GetDoubleArray parentModule;

		double[] viewsDoubles;


		JButton doneButton;
		JButton abortButton;
	/**
	   Perform initializations here.
	   @param mod The module that created this UserView
	*/
	public void initView(ViewModule mod) {
	    parentModule = (GetDoubleArray)mod;
	}

	private class endButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(e.getSource()==doneButton){
				for(int j=0; j<viewsDoubles.length; j++){
					double d=Double.parseDouble(tFields[j].getText());
					viewsDoubles[j]=d;
				}
				parentModule.moduleFinish(viewsDoubles);
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
		this.removeAll();
	   	int numberDoubles=parentModule.getArraySize();

		if((parentModule.doubles != null) &&
			(parentModule.doubles.length == numberDoubles)){
				viewsDoubles=parentModule.doubles;

		}else{
			viewsDoubles=new double[numberDoubles];
		}
		labels=new JLabel[numberDoubles];
		tFields=new JTextField[numberDoubles];

		JPanel mainPanel=new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		for (int i=0; i<numberDoubles; i++){

		    JPanel placeholder = new JPanel() {
			    public Dimension getPreferredSize() {
				return new Dimension(100, 0);
			    }
			};

			JPanel p = new JPanel();
	   		p.setLayout(new GridBagLayout());

			labels[i]=new JLabel("double["+i+"]");
		    Constrain.setConstraints(p, labels[i], 0, 0, 1, 1,
					     GridBagConstraints.NONE,
					     GridBagConstraints.WEST,
					     1, 1);

		    double d = viewsDoubles[i];
	  		tFields[i]=new JTextField(15);
			tFields[i].setText(Double.toString(d));

	   	 	Constrain.setConstraints(p, tFields[i], 1, 0, 2, 1,
						     GridBagConstraints.HORIZONTAL,
						     GridBagConstraints.WEST,
						     4, 1);

	  	  	Constrain.setConstraints(p, placeholder, 0, 1, 4, 1,
						    GridBagConstraints.HORIZONTAL,
						    GridBagConstraints.WEST,
						    4, 1);

	    	mainPanel.add(p);
		}
		JPanel buttonPanel=new JPanel();
		abortButton=new JButton("Abort");
		abortButton.addActionListener(new endButtonListener());
		buttonPanel.add(abortButton);

		doneButton = new JButton("Done");
		doneButton.addActionListener(new endButtonListener());
		buttonPanel.add(doneButton);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(mainPanel);
		add(buttonPanel);


	}


    }
}
