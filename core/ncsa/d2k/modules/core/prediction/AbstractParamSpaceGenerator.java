package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.gui.Constrain;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
abstract public class AbstractParamSpaceGenerator extends ncsa.d2k.core.modules.DataPrepModule {

	protected ParameterSpace space;
	public ParameterSpace getCurrentSpace() { return space; }
	public void setCurrentSpace(ParameterSpace space) { this.space = space; }

	/**
	 * Returns a reference to the developer supplied defaults. These are
	 * like factory settings, absolute ranges and definitions that are not
	 * mutable.
	 * @return the factory settings space.
	 */
	abstract protected ParameterSpace getDefaultSpace();

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<p>      This is the parameter space that will be searched.    </p>";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Parameter Space";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.parameter.ParameterSpace"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "Overview: This module will produce a parameter space object that     constrains the search space"+
			" for parameter optimization.Detailed Description: For model builders to work, they must have"+
			" an input that     defines their control parameters. For example, a neural net will often  "+
			"   quire an integer that defines the number of hidden layers and an     activation value. The"+
			" optimal settings for these parameters for a given     dataset to model are not usually known"+
			" before the run begins. Therefor,     several models can often be built and evaluated to find"+
			" the best settings     for these control parameters.To automate this, we define an API that"+
			" allows the optimization of these     control parameters. The range of possible values for each"+
			" control     parameters must therefor be defined for the optimization to search the     space"+
			" of possible parameters. We use the <i>ParameterSpace</i> object to     define these spaces."+
			" This module defines those spaces.";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Parameter Space Generator";
	}

	/**
	 * All we have to do here is push the parameter space.
	 */
	public void doit() {
		this.pushOutput(space, 0);
	}

	/**
	 * return a reference a custom property editor to select the percent test
	 * and train.
	 * @returna reference a custom property editor
	 */
	public CustomModuleEditor getPropertyEditor() {
		if (space == null) space = this.getDefaultSpace();
		return new SetParameterSpace();
	}

	/**
	 * This panel displays the editable properties of the SimpleTestTrain modules.
	 * @author Thomas Redman
	 */
	class SetParameterSpace extends JPanel implements CustomModuleEditor {
		private ParameterEditor [] editors;
		int count;
		SetParameterSpace () {
			count = space.getNumParameters();
			ParameterSpace defSpace = AbstractParamSpaceGenerator.this.getDefaultSpace();
			this.setLayout(new GridLayout(count, 1));
			editors = new ParameterEditor [count];
			for (int i = 0 ; i < count ; i++) {
				editors[i] = new ParameterEditor(space.getName(i), space.getMinValue(i),
				space.getMaxValue(i), space.getDefaultValue(i), space.getResolution(i), defSpace.getMinValue(i),
				defSpace.getMaxValue(i), defSpace.getDefaultValue(i), defSpace.getResolution(i));
				this.add(editors[i]);
			}
		}

		/**
		 * Update the fields of the module
		 * @return a string indicating why the properties could not be set, or null if successfully set.
		 */
		public boolean updateModule() throws Exception {
			double [] min = new double [count];
			double [] max = new double [count];
			double [] def = new double [count];
			int [] res = new int [count];
			ParameterSpace mydef = getDefaultSpace();
			for (int i = 0 ; i < count ; i++) {
				min[i] = editors[i].getMin();
				max[i] = editors[i].getMax();
				def[i] = editors[i].getDefault();
				res[i] = editors[i].getResolution();
			}

			// check ot see if anything changed.
			boolean changed = false;
			for (int i = 0 ; i < count ; i++) {
				if (min[i] != space.getMinValue(i)) {
					if (min[i] < mydef.getMinValue(i))
						throw new Exception ("User minimum must not be less than the default settings.");
					changed = true;
				}
				if (max[i] != space.getMaxValue(i)) {
					if (max[i] > mydef.getMaxValue(i))
						throw new Exception ("User maximum must not be greater than the default maximum.");
					changed = true;
				}
				if (def[i] != space.getDefaultValue(i)) {
					if (def[i] < mydef.getMinValue(i) || def[i] > mydef.getMaxValue(i))
						throw new Exception ("Default must be between the min and max of the factory settings.");
					changed = true;
				}
				if (res[i] != space.getResolution(i)) {
					changed = true;
				}
				if (max[i] <= min[i])
		throw new Exception ("The max must be greater than the min.");
			}


			// if any changes, create a new parameter space and return true;
			if (changed) {
				String [] names = new String [space.getNumParameters()];
				int [] types = new int [space.getNumParameters()];
				for (int i = 0 ; i < names.length; i++) {
					names[i] = space.getName(i);
					types[i] = space.getType(i);
				}
				ParameterSpace space = (ParameterSpace) space.getClass().newInstance();
				space = space.createFromData(names, min, max, def, res, types);
				return true;
			} else

				// no change.
				return false;
		}
	}

	/**
	 * Edit an individual parameter definition, including max, min, increment and default
	 */
	class ParameterEditor extends JPanel implements java.io.Serializable{
		private JTextField minText = new JTextField(6);
		private JTextField maxText = new JTextField(6);
		private JTextField defaultText = new JTextField(6);
		private JTextField incrementText = new JTextField(6);

		double defaultMin;
		double defaultMax;
		double defaultDefault;
		int defaultResolution;

		ParameterEditor (String name, double min, double max, double d, int inc,
						  double def_min, double def_max, double def_def, int def_inc) {

			// Store the defaults.
			this.defaultMin = def_min;
			this.defaultMax = def_max;
			this.defaultDefault = def_def;
			this.defaultResolution = def_inc;

			this.setLayout(new GridBagLayout());

			// The first row will contain the editors.
			Constrain.setConstraints(this, new JLabel("Min"), 0, 0, 1, 1, GridBagConstraints.NONE,
									  GridBagConstraints.NORTH, 0.0, 0.0);
			minText.setToolTipText("Minimum value for the parameter");
			minText.setText(Double.toString(min));
			Constrain.setConstraints(this, minText, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
									  GridBagConstraints.NORTH, 0.33, 0.0);

			Constrain.setConstraints(this, new JLabel("Max"), 2, 0, 1, 1, GridBagConstraints.NONE,
									  GridBagConstraints.NORTH, 0.0, 0.0);
			maxText.setToolTipText("Maxmimum value for the parameter");
			maxText.setText(Double.toString(max));
			Constrain.setConstraints(this, maxText, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL,
									  GridBagConstraints.NORTH, 0.33, 0.0);

			Constrain.setConstraints(this, new JLabel("Default"), 4, 0, 1, 1, GridBagConstraints.NONE,
									  GridBagConstraints.NORTH, 0.0, 0.0);
			defaultText.setToolTipText("Default value for the parameter");
			defaultText.setText(Double.toString(d));
			Constrain.setConstraints(this, defaultText, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL,
									  GridBagConstraints.NORTH, 0.33, 0.0);

			Constrain.setConstraints(this, new JLabel("Res"), 6, 0, 1, 1, GridBagConstraints.NONE,
									  GridBagConstraints.NORTH, 0.0, 0.0);
			incrementText.setToolTipText("Number of evenly spaced points in the space to search");
			incrementText.setText(Integer.toString(inc));
			Constrain.setConstraints(this, incrementText, 7, 0, 1, 1, GridBagConstraints.HORIZONTAL,
									  GridBagConstraints.NORTH, 0.33, 0.0);

			// Second row displays the defaults.
			Color dg = Color.gray;
			/*JLabel lab = new JLabel("Defaults:");
			lab.setForeground(dg);*/
			JButton restore = new JButton("Reset");
			restore.addActionListener (new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					restoreFactorySettings();
				}
			});
			restore.setToolTipText("Restore factory defaults for all values");
			Constrain.setConstraints(this, restore, 0, 1, 1, 1, GridBagConstraints.NONE,
									  GridBagConstraints.NORTH, 0.0, 0.0);
			JLabel dminText = new JLabel (Double.toString(defaultMin));
			dminText.setForeground(dg);
			Constrain.setConstraints(this, dminText, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL,
									  GridBagConstraints.NORTH, 0.33, 0.0);

			JLabel dmaxText = new JLabel (Double.toString(defaultMax));
			dmaxText.setForeground(dg);
			Constrain.setConstraints(this, dmaxText, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL,
									  GridBagConstraints.NORTH, 0.33, 0.0);

			JLabel ddText = new JLabel (Double.toString(defaultDefault));
			ddText.setForeground(dg);
			Constrain.setConstraints(this, ddText, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL,
									  GridBagConstraints.NORTH, 0.33, 0.0);

			JLabel dresText = new JLabel (Integer.toString(defaultResolution));
			dresText.setForeground(dg);
			Constrain.setConstraints(this, dresText, 7, 1, 1, 1, GridBagConstraints.HORIZONTAL,
									  GridBagConstraints.NORTH, 0.33, 0.0);
			this.setBorder(new TitledBorder(new EtchedBorder(), name));
		}

		/**
		 * Restore everything to it's factory setting.
		 */
		void restoreFactorySettings() {
			minText.setText(Double.toString(defaultMin));
			maxText.setText(Double.toString(defaultMax));
			defaultText.setText(Double.toString(defaultDefault));
			incrementText.setText(Integer.toString(defaultResolution));
		}

		/**
		 * get the value for the current max.
		 * @return the value for the current max.
		 * @throws java.lang.NumberFormatException
		 */
		double getMax () throws java.lang.NumberFormatException {
			return Double.parseDouble(maxText.getText());
		}

		/**
		 * get the value for the current max.
		 * @return the value for the current max.
		 * @throws java.lang.NumberFormatException
		 */
		double getMin () throws java.lang.NumberFormatException {
			return Double.parseDouble(minText.getText());
		}

		/**
		 * get the value for the current max.
		 * @return the value for the current max.
		 * @throws java.lang.NumberFormatException
		 */
		double getDefault () throws java.lang.NumberFormatException {
			return Double.parseDouble(defaultText.getText());
		}

		/**
		 * get the value for the current max.
		 * @return the value for the current max.
		 * @throws java.lang.NumberFormatException
		 */
		int getResolution () throws java.lang.NumberFormatException {
			return Integer.parseInt(incrementText.getText());
		}
	}
}
