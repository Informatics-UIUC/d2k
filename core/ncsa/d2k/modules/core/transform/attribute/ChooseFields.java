package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.infrastructure.views.UserView;

import ncsa.d2k.util.datatype.*;
import ncsa.gui.Constrain;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

/**
   ChooseFields.java

   Allows the user to choose which columns are inputs and
   which are outputs, assigns them in an ET.

   @author Peter Groves, c/o David Clutter
*/
public class ChooseFields extends UIModule implements HasNames {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
	return "Allows the user to choose which columns are inputs and which are outputs, assigns them in an ET.";
    }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
	return "ChsFlds";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.util.datatype.VerticalTable"};
    	return in;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.util.datatype.ExampleTable"
						};
    	return out;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
    	if(i == 0)
			return "the Verticaltable with labels on the columns";
		else
			return "no such output";
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
    	if(i == 0)
			return "rawtable";
		else
			return "no such input";
	}

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
    	if(i == 0)
			return "ExampleTable with input and output features assigned";
		else
			return "no such output";
	}

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
    	if(i == 0)
			return "exampletable";
		else
			return "no such output";
	}

	/**
		Return the UserView
		@returns the UserView
	*/
	protected UserView createUserView() {
		return new AttributeView();
	}

	/**
		not used!
	*/
	protected String[] getFieldNameMapping() {
		return null;
	}


	/**
		Pushes the outputs. Called when the view has finished.
	*/
	public void finish(ExampleTable et) {
		this.pushOutput(et, 0);
		executionManager.moduleDone(this);
	}

	/**
		The user view class
	*/
	class AttributeView extends JUserPane implements ActionListener {
		//the old data
		VerticalTable vt;
		//the updated table
		ExampleTable et;

		ChooseFields module;
		JButton abort;
		JButton done;

		JList inputList;
		JList outputList;

		JLabel inputLabel;
		JLabel outputLabel;

		/**
			initialize
		*/
		public void initView(ViewModule v) {
			module = (ChooseFields)v;
			abort = new JButton("Abort");
			done = new JButton("Done");
			abort.addActionListener(this);
			done.addActionListener(this);
		}

		/**
			called when inputs arrive
		*/
		public void setInput(Object o, int id) {
			if(id == 0) {
				vt = (VerticalTable)o;
				this.removeAll();
				addComponents();
			}
		}

		/**
			make me at least this big
		*/
		public Dimension getPreferredSize() {
			return new Dimension(400, 500);
		}
		/**
			add all the components
		*/
		protected void addComponents() {
			JPanel back = new JPanel();

			int numColumns = vt.getNumColumns();

			String[] labels=new String[numColumns];

			for(int i = 0; i < numColumns; i++) {
				labels[i] = vt.getColumnLabel(i);
			}

			inputList=new JList(labels);
			if(vt instanceof ExampleTable)
				inputList.setSelectedIndices(((ExampleTable)vt).getInputFeatures());
			outputList=new JList(labels);
			if(vt instanceof ExampleTable)
				outputList.setSelectedIndices(((ExampleTable)vt).getOutputFeatures());
			JScrollPane leftScrollPane=new JScrollPane(inputList);
			JScrollPane rightScrollPane=new JScrollPane(outputList);

			inputLabel=new JLabel("Input Columns");
			inputLabel.setHorizontalAlignment(SwingConstants.CENTER);

			outputLabel=new JLabel("Output Columns");
			outputLabel.setHorizontalAlignment(SwingConstants.CENTER);

			back.setLayout(new GridBagLayout());

			Constrain.setConstraints(back, inputLabel, 0, 0, 1, 1, GridBagConstraints.BOTH,
									GridBagConstraints.CENTER, 0, 0);
			Constrain.setConstraints(back, outputLabel, 1, 0, 1, 1, GridBagConstraints.BOTH,
									GridBagConstraints.CENTER, 0, 0);
 			Constrain.setConstraints(back, leftScrollPane, 0, 1, 1, 1, GridBagConstraints.BOTH,
									GridBagConstraints.CENTER, 1, 1);
			Constrain.setConstraints(back, rightScrollPane, 1, 1, 1, 1, GridBagConstraints.BOTH,
									GridBagConstraints.CENTER, 1, 1);



			JPanel buttons = new JPanel();
			buttons.add(abort);
			buttons.add(done);

			this.add(back, BorderLayout.CENTER);
			this.add(buttons, BorderLayout.SOUTH);
		}

		/**
			listen for ActionEvents
		*/
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if(src == abort)
				module.viewCancel();
			else if(src == done) {
				if(checkChoices()) {
					setFieldsInTable();
					module.finish(et);
					et = null;
					this.removeAll();
				}
			}
		}

		private void setFieldsInTable(){
			et=new ExampleTable(vt);
			et.setInputFeatures(inputList.getSelectedIndices());
			et.setOutputFeatures(outputList.getSelectedIndices());
			vt = null;
		}

		/**
			Make sure all choices are valid.
			*/

		protected boolean checkChoices() {
			if(outputList.getSelectedIndex() == -1){
				JOptionPane.showMessageDialog(this,
						"You must select at least one output",
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if(inputList.getSelectedIndex() == -1){
				JOptionPane.showMessageDialog(this,
						"You must select at least one input",
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			return true;
		}
	}
}
