package ncsa.d2k.modules.core.transform.attribute;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.gui.*;
import ncsa.gui.ErrorDialog;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ChooseFieldTypes extends UIModule {
    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Allows the user to choose which columns are scalar and which are nominal.  </body></html>";
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
	return "ChooseFieldTypes";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "the table with labels on the columns";
			default: return "No such input";
		}
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
		switch (i) {
			case 0: return "Table with nominal and scalar types assigned.";
			default: return "No such output";
		}
	}

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
    	if(i == 0)
			return "table";
		else
			return "no such output";
	}

	/**
		Return the UserView
		@returns the UserView
	*/
	protected UserView createUserView() {
		return new DefineView();
	}

	/**
		not used!
	*/
	protected String[] getFieldNameMapping() {
		return null;
	}
   	/**
		The user view class
	*/
	private class DefineView extends JUserPane implements ActionListener {
		private Table table;

		private ChooseFieldTypes module;
		private JButton abort;
		private JButton done;

		private JList scalarList;
		private JList nominalList;

        private HashMap indexLookup;

        private JCheckBoxMenuItem miColumnOrder;
        private JCheckBoxMenuItem miAlphaOrder;

        private JMenuBar menuBar;

        private DefaultListModel scalarListModel;
        private DefaultListModel nominalListModel;

        private JButton fromScalarToNom;
        private JButton fromNomToScalar;

		/**
			initialize
		*/
		public void initView(ViewModule v) {
			module = (ChooseFieldTypes)v;
			abort = new JButton("Abort");
			done = new JButton("Done");
			abort.addActionListener(this);
			done.addActionListener(this);
            /*menuBar = new JMenuBar();
            JMenu m1 = new JMenu("File");
            miColumnOrder = new JCheckBoxMenuItem("Column Order");
            miColumnOrder.addActionListener(this);
            miColumnOrder.setState(true);
            miAlphaOrder = new JCheckBoxMenuItem("Alphabetical Order");
            miAlphaOrder.addActionListener(this);
            miAlphaOrder.setState(false);
            m1.add(miColumnOrder);
            m1.add(miAlphaOrder);
            menuBar.add(m1);
            */
            addComponents();
		}

        /*public Object getMenu() {
            return menuBar;
        }*/

		/**
			called when inputs arrive
		*/
		public void setInput(Object o, int id) {
			if(id == 0) {
				table = (Table)o;
                fillComponents();
			}
		}

		/**
			make me at least this big
		*/
		public Dimension getPreferredSize() {
			return new Dimension(400, 300);
		}

		/**
			add all the components
		*/
		private void addComponents() {
			JPanel back = new JPanel();

			scalarList=new JList();
            scalarListModel = new DefaultListModel();
            scalarList.setModel(scalarListModel);

			nominalList=new JList();
            nominalListModel = new DefaultListModel();
            nominalList.setModel(nominalListModel);

			JScrollPane leftScrollPane=new JScrollPane(scalarList);
			JScrollPane rightScrollPane=new JScrollPane(nominalList);

			JLabel inputLabel=new JLabel("Scalar Columns");
			inputLabel.setHorizontalAlignment(SwingConstants.CENTER);
            leftScrollPane.setColumnHeaderView(inputLabel);

			JLabel outputLabel=new JLabel("Nominal Columns");
			outputLabel.setHorizontalAlignment(SwingConstants.CENTER);
            rightScrollPane.setColumnHeaderView(outputLabel);

            fromScalarToNom = new JButton(">");
            fromScalarToNom.addActionListener(this);
            fromNomToScalar = new JButton("<");
            fromNomToScalar.addActionListener(this);

            /*Box b1 = new Box(BoxLayout.Y_AXIS);
            b1.add(b1.createGlue());
            b1.add(fromScalarToNom);
            b1.add(fromNomToScalar);
            b1.add(b1.createGlue());
            */
            JPanel b1 = new JPanel();
            b1.setLayout(new GridLayout(2,1));
            b1.add(fromScalarToNom);
            b1.add(fromNomToScalar);

            JPanel b2 = new JPanel();
            b2.add(b1);

			back.setLayout(new GridBagLayout());

 			Constrain.setConstraints(back, leftScrollPane, 0, 0, 2, 1,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
 			Constrain.setConstraints(back, b2, 2, 0, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.CENTER, 0, 0);
			Constrain.setConstraints(back, rightScrollPane, 3, 0, 2, 1,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

			JPanel buttons = new JPanel();
			buttons.add(abort);
			buttons.add(done);

			this.add(back, BorderLayout.CENTER);
			this.add(buttons, BorderLayout.SOUTH);
		}

        private void fillComponents() {
            indexLookup = new HashMap();
            scalarListModel.removeAllElements();
            nominalListModel.removeAllElements();

            LinkedList scalars = new LinkedList();
            LinkedList nominals = new LinkedList();
            for(int i = 0; i < table.getNumColumns(); i++) {
              if(table.isColumnScalar(i))
                scalars.add(table.getColumnLabel(i));
              else
                nominals.add(table.getColumnLabel(i));
               indexLookup.put(table.getColumnLabel(i), new Integer(i));
            }

            for(int i = 0; i < scalars.size(); i++)
               scalarListModel.addElement(scalars.get(i));
            for(int i = 0; i < nominals.size(); i++)
                nominalListModel.addElement(nominals.get(i));
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
		            pushOutput(table, 0);
					viewDone("Done");
				}
			}
            else if(src == miColumnOrder) {
                /*String [] labels = orderedLabels();
                miAlphaOrder.setState(false);
                DefaultListModel dlm = (DefaultListModel)inputList.getModel();
                dlm.removeAllElements();
                for(int i = 0; i < labels.length; i++) {
                    dlm.addElement(labels[i]);
                }
                dlm = (DefaultListModel)outputList.getModel();
                dlm.removeAllElements();
                for(int i = 0; i < labels.length; i++) {
                    dlm.addElement(labels[i]);
                }*/
            }
            else if(src == miAlphaOrder) {
                /*String [] labels = alphabetizeLabels();
                miColumnOrder.setState(false);
                DefaultListModel dlm = (DefaultListModel)inputList.getModel();
                dlm.removeAllElements();
                for(int i = 0; i < labels.length; i++) {
                    dlm.addElement(labels[i]);
                }
                dlm = (DefaultListModel)outputList.getModel();
                dlm.removeAllElements();
                for(int i = 0; i < labels.length; i++) {
                    dlm.addElement(labels[i]);
                }*/
            }
            else if(src == fromNomToScalar) {
                Object[] selected = nominalList.getSelectedValues();
                for(int i = 0; i< selected.length; i++) {
                    boolean b = true;
                    Integer idx = (Integer)indexLookup.get(selected[i]);
                    if(!table.isColumnNumeric(idx.intValue())) {
                        b = ErrorDialog.showQuery(
                            table.getColumnLabel(idx.intValue())+" is not numeric.  Continue?",
                               "Warning!");
                    }
                    if(b) {
                        nominalListModel.removeElement(selected[i]);
                        scalarListModel.addElement(selected[i]);
                    }
                }
            }
            else if(src == fromScalarToNom) {
                Object[] selected = scalarList.getSelectedValues();
                for(int i = 0; i< selected.length; i++) {
                    scalarListModel.removeElement(selected[i]);
                    nominalListModel.addElement(selected[i]);
                }
            }
		}

		private void setFieldsInTable(){
            Enumeration e = scalarListModel.elements();
            while(e.hasMoreElements()) {
                String s = (String)e.nextElement();
                Integer ii = (Integer)indexLookup.get(s);
                table.setColumnIsScalar(true, ii.intValue());
                table.setColumnIsNominal(false, ii.intValue());
            }
            e = nominalListModel.elements();
            while(e.hasMoreElements()) {
                String s = (String)e.nextElement();
                Integer ii = (Integer)indexLookup.get(s);
                table.setColumnIsNominal(true, ii.intValue());
                table.setColumnIsScalar(false, ii.intValue());
            }
		}

		/**
			Make sure all choices are valid.
		*/
		protected boolean checkChoices() {
			/*if(outputList.getSelectedIndex() == -1){
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
            */
            return true;
		}
	}

}
