package ncsa.d2k.modules.core.io.sql;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.transform.StaticMethods;
import ncsa.d2k.userviews.swing.JUserPane;

/**
 * <p>Title: SelectAttributes</p>
 *
 * <p>Description: 
 *
 * <p>Copyright: Copyright (c) 2002</p>
 *
 * <p>Company: NCSA ALG</p>
 *
 * @author 
 *	
 */
public class SelectAttributes extends ncsa.d2k.core.modules.HeadlessUIModule {
	/** Use serialVersionUID for interoperability. */
	   static private final long serialVersionUID = 4648503544541416222L;

  JOptionPane msgBoard = new JOptionPane();

	/**
	 * Method getModuleName ().
	 * Return the human readable name of the module.
	 *
	 * @return <code>String</code> describing the human readable name of the module.
	 */
  	public String getModuleName() {
		return "Select Attributes";
	}

	/**
	 * Method getModuleInfo ().
	 * @return <code>String</code> the description of the module.
	 */
	public String getModuleInfo () {
          String s = "<p>Overview: ";
          s += "This module allows the user to select one or more attributes from a list of attributes. </p>";
          s += "<p>Detailed Description: ";
          s += "This module provides a user-interface that allows one or more attributes to be chosen ";
          s += "from a list of attributes. The list of attributes is ";
          s += "retrieved from the <i>Attributes List</i> input port. ";
          s += "The selected attributes will be used to construct SQL queries.  </p>";
          s += "<p>Restrictions: ";
          s += "Currently only Oracle, SQLServer, DB2 and MySql databases are supported. ";
          return s;

	}

	/**
	 * Returns the name of the input at the specified index.
	 *
	 * @param inputIndex Index of the input for which a name should be returned.
	 *
	 * @return <code>String</code> containing the name of the input at the specified index.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Attributes List";
			default:
				return "No such input";
		}
	}

	/**
	 * Returns a description of the input at the specified index.
	 *
	 * @param inputIndex Index of the input for which a description should be returned.
	 *
	 * @return <code>String</code> describing the input at the specified index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0:
				return "A list of available attributes.";
			default:
				return "No such input.";
		}
	}

	/**
	 * This method returns an array of strings that contains the data types for the inputs.
	 * @return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"java.util.Vector"};
		return types;
	}

	/**
	 * Returns the name of the output at the specified index.
	 *
	 * @param outputIndex Index of the output for which a name should be returned.
	 *
	 * @return <code>String</code> containing the name of the output at the specified index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Selected Attributes";
			default:
				return "No such output";
		}
	}

	/**
	 * Returns a description of the output at the specified index.
	 *
	 * @param outputIndex Index of the output for which a description should be returned.
	 *
	 * @return <code>String</code> describing the output at the specified index.
	 */
	public String getOutputInfo (int index) {
		switch (index) {
			case 0:
				return "The attributes that were selected.";
			default:
				return "No such output";
		}
	}

	/**
	 *	This method returns an array of strings that contains the data types for the outputs.
	 *	@return the data types of all outputs.
	 */
	public String[] getOutputTypes () {
		String[] types = {"[Ljava.lang.String;"};
		return types;
	}

	/**
	 *	This method is called by D2K to get the UserView for this module.
	 *	@return the UserView.
	 */
	protected UserView createUserView() {
		return new SelectAttributeView();
	}

	/**
	 *	This method returns an array with the names of each DSComponent in the UserView
	 *	that has a value.  These DSComponents are then used as the outputs of this module.
	 */
	public String[] getFieldNameMapping() {
		return null;
	}

	/**
	 *	Private class  SelectAttributeView() 
	 *	This is the UserView class.
	 */
	private class SelectAttributeView extends JUserPane {

		JList selectedAttributes = new JList ();
		JList possibleAttributes = new JList ();
		JButton add = new JButton ("Add");
		JButton remove = new JButton ("Remove");
		DefaultListModel possibleModel = new DefaultListModel();
		DefaultListModel selectedModel = new DefaultListModel();

		/**
		 * Called by the D2K Infrastructure to allow the view to perform initialization tasks.
		 * This method adds the components to a Panel and then adds the Panel
		 * to the view.
		 *
		 * @param module The module this view is associated with.
		 */
		public void initView(ViewModule mod) {
			JPanel canvasArea = new JPanel();
			canvasArea.setLayout (new BorderLayout ());
			JPanel buttons = new JPanel ();
			buttons.setLayout (new GridLayout(2,1));
			buttons.add (add);
			buttons.add (remove);

			JPanel b1 = new JPanel();
			b1.add(buttons);

			// The listener for the add button moves stuff from the possible list
			// to the selected list.
			add.addActionListener (new AbstractAction() {
				public void actionPerformed (ActionEvent e) {
					Object[] sel = possibleAttributes.getSelectedValues();
					for(int i = 0; i < sel.length; i++) {
						//possibleAttributes.remove (selection);
						possibleModel.removeElement(sel[i]);
						//selectedAttributes.add (selection);
						selectedModel.addElement(sel[i]);
					}
				}
			});

			// The listener for the remove button moves stuff from the selected list
			// to the possible list.
			remove.addActionListener (new AbstractAction () {
				public void actionPerformed (ActionEvent e) {
					Object[] sel = selectedAttributes.getSelectedValues();
					for(int i = 0; i < sel.length; i++) {
						//selectedAttributes.remove (selection);
						selectedModel.removeElement(sel[i]);
						//possibleAttributes.add (selection);
						possibleModel.addElement(sel[i]);
					}
				}
			});

			selectedAttributes.setModel(selectedModel);
			possibleAttributes.setModel(possibleModel);

			JScrollPane jsp = new JScrollPane(possibleAttributes);
			jsp.setColumnHeaderView(new JLabel("Possible Attributes"));
			JScrollPane jsp1 = new JScrollPane(selectedAttributes);
			jsp1.setColumnHeaderView(new JLabel("Selected Attributes"));

			canvasArea.add (b1, BorderLayout.CENTER);
			canvasArea.add (/*possibleAttributes*/jsp, BorderLayout.WEST);
			canvasArea.add (/*selectedAttributes*/jsp1, BorderLayout.EAST);

			JPanel buttonPanel = new JPanel();
			JButton done = new JButton("Done");
			done.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent ae) {
					Object[] values = selectedModel.toArray();
					String[] retVal = new String[values.length];
                                        if (retVal.length==0) {
                                          JOptionPane.showMessageDialog(msgBoard,
                                                    "You must select at least one attribute.", "Error",
                                                    JOptionPane.ERROR_MESSAGE);
                                          System.out.println("No columns are selected");
                                        }
                                        else {
                                          for(int i = 0; i < retVal.length; i++) {
						retVal[i] = (String)values[i];
                                          }
                                          pushOutput(retVal, 0);
                                          setSelectedAttributes(retVal);
                                          viewDone("Done");
                                        }
				}
			});

			JButton abort = new JButton("Abort");
			abort.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent ae) {
					viewCancel();
				}
			});

			buttonPanel.add(abort);
			buttonPanel.add(done);
			canvasArea.add(buttonPanel, BorderLayout.SOUTH);
			add(canvasArea);
	}

	/**
	 *  Method setInput().
	 *  
	 *	This method is called whenever an input arrives, and is responsible
	 *	for modifying the contents of any gui components that should reflect
	 *	the value of the input.
	 *
	 * @param input The object that has been input.
	 * @param index The index of the module input that been received.
	 */
	public void setInput(Object o, int index) {
			Vector attributes = (Vector) o;
			selectedModel.removeAllElements();
			possibleModel.removeAllElements();
			String longest = null;
			int lengthOfLongest = 0;
			for (int i = 0 ; i < attributes.size (); i++) {
				String elem = (String)attributes.elementAt(i);
				possibleModel.addElement (elem);

				if(elem.length() > lengthOfLongest) {
					longest = elem;
					lengthOfLongest = elem.length();
				}
			}

			// If Possible Attributes or Selected Attributes labels are
                        // longer than any of the attributes, we want the width to be
                        // based on those, not on the longest attribute.  -RA 6/03
			String label = "XXXXXXXX Attributes";
			if ( label.length() > lengthOfLongest) {
				longest = label;
			}

			possibleAttributes.setPrototypeCellValue(longest);
			selectedAttributes.setPrototypeCellValue(longest);
		}
	}

    //headless conversion support
    private String[] selectedAttributes;
    public void setSelectedAttributes(Object[] att){selectedAttributes = (String[])att;}
    public Object[] getSelectedAttributes( ){return selectedAttributes;}
		
	/**
	 * Performs the main work of the module.
	 */
	public void doit() throws Exception{

          Vector availableAttributes = (Vector)pullInput(0);
          if(selectedAttributes == null || selectedAttributes.length == 0)
            throw new Exception(getAlias() + " has not been configured. Before running " +
                                "headless configure the properties via running with GUI.");

          String[] targetAttributes = StaticMethods.getIntersection(selectedAttributes, availableAttributes);
         if(targetAttributes.length < selectedAttributes.length)
           throw new Exception(getAlias() + ": Some of the configured attributes were not " +
                               "found in the input attributes list. " +
                               "Please reconfigure this module so it can run Headless.");

         pushOutput(targetAttributes, 0);

    }
}
