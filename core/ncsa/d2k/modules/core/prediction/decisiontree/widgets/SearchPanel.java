package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ncsa.gui.*;

/**
	Search interface
*/
public class SearchPanel extends JPanel implements ActionListener {
	int searchsize = 3;

	JScrollPane attributesscroll, classificationsscroll;
	JPanel attributespanel, classificationspanel;

	SearchAttributes[] searchattributes;
	SearchClassifications[] searchclassifications;

	JLabel attributelabel, classificationlabel;
	JButton searchbutton, resetbutton;

	public SearchPanel() {
		setSearchAttributes();
		attributespanel = new JPanel();
		attributespanel.setLayout(new BoxLayout(attributespanel, BoxLayout.Y_AXIS));
		attributespanel.add(Box.createRigidArea(new Dimension(0, 10)));
		for (int index=0; index<searchsize; index++) {
			attributespanel.add(searchattributes[index]);
			attributespanel.add(Box.createRigidArea(new Dimension(0, 5)));
		}
		attributespanel.add(Box.createRigidArea(new Dimension(0, 10)));
		attributesscroll = new JScrollPane(attributespanel);

		setSearchClassifications();
		classificationspanel = new JPanel();
		classificationspanel.setLayout(new BoxLayout(classificationspanel, BoxLayout.Y_AXIS));
		classificationspanel.add(Box.createRigidArea(new Dimension(0, 10)));
		for (int index=0; index<searchsize; index++) {
			classificationspanel.add(searchclassifications[index]);
			classificationspanel.add(Box.createRigidArea(new Dimension(0, 5)));
		}
		classificationspanel.add(Box.createRigidArea(new Dimension(0, 10)));
		classificationsscroll = new JScrollPane(classificationspanel);

		attributelabel = new JLabel("Attributes");
		classificationlabel = new JLabel("Classifications");

		searchbutton = new JButton("Search");
		searchbutton.addActionListener(this);

		resetbutton = new JButton("Reset");
		resetbutton.addActionListener(this);

		attributelabel.setFont(DecisionTreeScheme.componentlabelfont);
		classificationlabel.setFont(DecisionTreeScheme.componentlabelfont);

		searchbutton.setFont(DecisionTreeScheme.componentbuttonfont);
		resetbutton.setFont(DecisionTreeScheme.componentbuttonfont);

		setLayout(new GridBagLayout());
		Constrain.setConstraints(this, attributelabel, 0, 0, 2, 1, GridBagConstraints.NONE,
			GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
		Constrain.setConstraints(this, attributesscroll, 0, 1, 2, 1, GridBagConstraints.BOTH,
			GridBagConstraints.NORTHWEST, 0, .5, new Insets(5, 5, 5, 5));
		Constrain.setConstraints(this, classificationlabel, 0, 2, 2, 1, GridBagConstraints.NONE,
			GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
		Constrain.setConstraints(this, classificationsscroll, 0, 3, 2, 1, GridBagConstraints.BOTH,
			GridBagConstraints.NORTHWEST, 1, .5, new Insets(5, 5, 5, 5));
		Constrain.setConstraints(this, searchbutton, 0, 4, 1, 1, GridBagConstraints.NONE,
			GridBagConstraints.NORTHEAST, 1, 0, new Insets(5, 5, 5, 5));
		Constrain.setConstraints(this, resetbutton, 1, 4, 1, 1, GridBagConstraints.NONE,
			GridBagConstraints.NORTHEAST, 0, 0, new Insets(5, 5, 5, 5));
	}

	public String getAttributeSearchString() {
		StringBuffer buffer = new StringBuffer();
		for (int index=0; index<searchsize; index++) {
			String searchstring = searchattributes[index].getSearchString();
			if (searchstring != null)
				buffer.append(searchstring);
		}

		return new String(buffer);
	}

	public String getClassificationSearchString() {
		StringBuffer buffer = new StringBuffer();
		for (int index=0; index<searchsize; index++) {
			String searchstring = searchclassifications[index].getSearchString();
			if (searchstring != null)
				buffer.append(searchstring);
		}

		return new String(buffer);
	}

	public void setSearchAttributes() {
		searchattributes = new SearchAttributes[searchsize];
		for (int index=0; index<searchsize; index++) {
			searchattributes[index] = new SearchAttributes();
		}
	}

	public void setSearchClassifications() {
		searchclassifications = new SearchClassifications[searchsize];
		for (int index=0; index<searchsize; index++) {
			searchclassifications[index] = new SearchClassifications();
		}
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == searchbutton) {
			System.out.println(getAttributeSearchString());
			System.out.println(getClassificationSearchString());
		}
		else if (source == resetbutton) {
			for (int index=0; index<searchsize; index++) {
				searchattributes[index].reset();
				searchclassifications[index].reset();
			}
		}
	}

	/**
		Inner class

		Represents a search condition for attributes
	*/
	public class SearchAttributes extends JPanel implements ActionListener {
		String[] attributes = {"none", "calc", "test", "val"};
		String[] comparisons = {"contains", "=", ">", "<", ">=", "<="};
		String[] operators = {"and", "or"};

		JComboBox attributebox, comparebox, operatorbox;
		JTextField valuefield;

		public SearchAttributes() {
			attributebox = new JComboBox(attributes);
			comparebox  = new JComboBox(comparisons);
			operatorbox = new JComboBox(operators);
			valuefield = new JTextField(8);

			attributebox.addActionListener(this);
			comparebox.addActionListener(this);

			comparebox.setEnabled(false);
			operatorbox.setEnabled(false);
			valuefield.setEnabled(false);

			Font font = new Font("Sans Serif", Font.PLAIN, 10);
			attributebox.setFont(font);
			comparebox.setFont(font);
			operatorbox.setFont(font);
			valuefield.setFont(font);

			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(Box.createRigidArea(new Dimension(10, 0)));
			add(attributebox);
			add(Box.createRigidArea(new Dimension(5, 0)));
			add(comparebox);
			add(Box.createRigidArea(new Dimension(5, 0)));
			add(valuefield);
			add(Box.createRigidArea(new Dimension(5, 0)));
			add(operatorbox);
			add(Box.createRigidArea(new Dimension(10, 0)));
		}

		public String getAttribute() {
			return (String) attributebox.getSelectedItem();
		}

		public String getComparator() {
			return (String) comparebox.getSelectedItem();
		}

		public String getValue() {
			String value = valuefield.getText();
			if (value.compareTo("") == 0)
				return null;
			else
				return value;
		}

		public String getOperator() {
			return (String) operatorbox.getSelectedItem();
		}

		public String getSearchString() {
			if (attributebox.getSelectedIndex() == 0)
				return null;

			if (comparebox.getSelectedIndex() == 0)
				return getComparator() + " " + getAttribute() + " " + getOperator() + " ";

			if (getValue() == null)
				return null;

			return getAttribute() + " " + getComparator() + " " + getValue() + " " + getOperator() + " ";
		}

		public void reset() {
			attributebox.setSelectedIndex(0);
			comparebox.setSelectedIndex(0);
			valuefield.setText("");
			operatorbox.setSelectedIndex(0);
		}

		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();

			if (source == comparebox) {
				if (comparebox.getSelectedIndex() == 0)
					valuefield.setEnabled(false);
				else
					valuefield.setEnabled(true);
			}

			if (source == attributebox) {
				if (attributebox.getSelectedIndex() == 0) {
					comparebox.setEnabled(false);
					operatorbox.setEnabled(false);
					valuefield.setEnabled(false);
				}
				else {
					comparebox.setEnabled(true);
					operatorbox.setEnabled(true);
					if (comparebox.getSelectedIndex() == 0)
						valuefield.setEnabled(false);
					else
						valuefield.setEnabled(true);
				}
			}
		}
	}

	/**
		Inner class

		Represents a search condition for classifications
	*/
	public class SearchClassifications extends JPanel implements ActionListener {
		String[] classifications = {"none", "yes", "no", "maybe"};
		String[] comparisons = {"=", ">", "<", ">=", "<="};
		String[] operators = {"and", "or"};

		JComboBox classificationbox, comparebox, operatorbox;
		JTextField valuefield;

		public SearchClassifications() {
			classificationbox = new JComboBox(classifications);
			comparebox  = new JComboBox(comparisons);
			operatorbox = new JComboBox(operators);
			valuefield = new JTextField(8);

			classificationbox.addActionListener(this);

			comparebox.setEnabled(false);
			operatorbox.setEnabled(false);
			valuefield.setEnabled(false);

			Font font = new Font("Sans Serif", Font.PLAIN, 10);
			classificationbox.setFont(font);
			comparebox.setFont(font);
			operatorbox.setFont(font);
			valuefield.setFont(font);

			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(Box.createRigidArea(new Dimension(10, 0)));
			add(classificationbox);
			add(Box.createRigidArea(new Dimension(5, 0)));
			add(comparebox);
			add(Box.createRigidArea(new Dimension(5, 0)));
			add(valuefield);
			add(Box.createRigidArea(new Dimension(5, 0)));
			add(operatorbox);
			add(Box.createRigidArea(new Dimension(10, 0)));
		}

		public String getClassification() {
			return (String) classificationbox.getSelectedItem();
		}

		public String getComparator() {
			return (String) comparebox.getSelectedItem();
		}

		public String getValue() {
			String value = valuefield.getText();
			if (value.compareTo("") == 0)
				return null;
			else
				return value;
		}

		public String getOperator() {
			return (String) operatorbox.getSelectedItem();
		}

		public String getSearchString() {
			if (classificationbox.getSelectedIndex() == 0)
				return null;

			if (getValue() == null)
				return null;

			return getClassification() + " " + getComparator() + " " + getValue() + " " + getOperator() + " ";
		}

		public void reset() {
			classificationbox.setSelectedIndex(0);
			comparebox.setSelectedIndex(0);
			valuefield.setText("");
			operatorbox.setSelectedIndex(0);
		}

		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();

			if (source == classificationbox) {
				if (classificationbox.getSelectedIndex() == 0) {
					comparebox.setEnabled(false);
					operatorbox.setEnabled(false);
					valuefield.setEnabled(false);
				}
				else {
					comparebox.setEnabled(true);
					operatorbox.setEnabled(true);
					valuefield.setEnabled(true);
				}
			}
		}
	}
}
