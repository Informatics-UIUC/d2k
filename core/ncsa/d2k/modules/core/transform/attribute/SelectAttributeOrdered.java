
package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.widgits.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;

import ncsa.d2k.modules.core.datatype.table.*;
/**
	SelectAttributeOrdered.java
*/
public class SelectAttributeOrdered extends ncsa.d2k.infrastructure.modules.UIModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"table\">    <Text>The input table to select columns from. </Text>  </Info></D2K>";
			default: return "No such input";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.Table"};
		return types;

	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"attArray\">    <Text>A String array containing the attributes in the desired order. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"table\">    <Text>The original table. </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"[Ljava.lang.String;", "ncsa.d2k.modules.core.datatype.Table"};
		return types;

	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"\">    <Text> </Text>  </Info></D2K>";

	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit() throws Exception {
	}

	public void done( String[] arr, Table t, int j){
		pushOutput(arr, j);
		pushOutput(t, j+1);
		viewDone("");
	}
	/**
		This pair is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
		return new SelectUserView();
	}

	/**
		This pair returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {
		return null;

	}

}


/**
	SelectUserView
	This is the UserView class.
*/
class SelectUserView extends ncsa.d2k.controller.userviews.UserPane implements ActionListener{

	Table table;
	JPanel panel;
	JComboBox attBox;
	JButton done;
	String[] values;
	int count = 0;
	SelectAttributeOrdered module;

	public Dimension getMinimumSize(){
		return new Dimension(300,200);
	}

	public Dimension getPreferredSize(){
		return getMinimumSize();
	}

	/**
		This pair adds the components to a Panel and then adds the Panel
		to the view.
	*/
	public void initView(ViewModule mod) {
		module = (SelectAttributeOrdered) mod;

	}

	/**
		This pair is called whenever an input arrives, and is responsible
		for modifying the contents of any gui components that should reflect
		the value of the input.

		@param input this is the object that has been input.
		@param index the index of the input that has been received.
	*/
	public void setInput(Object o, int index) {
		table = (Table) o;
		int columns = table.getNumColumns();
		String[] labels = new String[columns];
		values = new String[columns];
		for (int i=0; i<columns; i++){
			labels[i] = table.getColumnLabel(i);
			values[i] = "";
		}
		attBox = new JComboBox(labels);
		done = new JButton("Done");

		buildView();

	}

	public void buildView(){
		count = 0;
		removeAll();
		setLayout(new BorderLayout());
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		attBox.addActionListener(this);
		done.addActionListener(this);
		panel.add(attBox);
		panel.add(done);
		add(panel);
	}
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source.equals(attBox)){
			int b = attBox.getSelectedIndex();
			System.out.println(b+"<--");
			String str = table.getColumnLabel(b);
			System.out.println(str+"<--");
			//values[count] = table.getColumnLabel(attBox.getSelectedIndex());
			values[count] = str;
			count++;
		}

		if (source.equals(done)){
			module.done(values, table ,0);
		}
	}
}

