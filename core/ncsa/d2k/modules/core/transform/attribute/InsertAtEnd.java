
package ncsa.d2k.modules.core.transform.attribute;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.*;
import ncsa.d2k.userviews.widgets.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
	InsertAtEnd.java
*/
public class InsertAtEnd extends ncsa.d2k.core.modules.UIModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "         ";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "      The original table with the selected column removed and then inserted at the end.   ";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<paragraph>  <head>  </head>  <body>    <p>          </p>  </body></paragraph>";
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit() throws Exception {
	}

	public void done(Table t, int j){
		pushOutput(t,j);
		viewDone("");
	}

	/**
		This pair is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
		return new Viewer();
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
	Viewer
	This is the UserView class.
*/
class Viewer extends ncsa.d2k.userviews.swing.JUserPane implements ActionListener{

	InsertAtEnd module;
	TableImpl table;
	JPanel panel;
	JComboBox box;
	JButton done;
	int theIndex = 0;

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
		module = (InsertAtEnd) mod;

	}

	/**
		This pair is called whenever an input arrives, and is responsible
		for modifying the contents of any gui components that should reflect
		the value of the input.

		@param input this is the object that has been input.
		@param index the index of the input that has been received.
	*/
	public void setInput(Object o, int index) {
		table = (TableImpl) o;
		int numCol = table.getNumColumns();
		String[] labels = new String[numCol];
		for (int i=0; i<numCol; i++){
			System.out.println(table.getColumnLabel(i));
			labels[i] = table.getColumnLabel(i);
		}
		box = new JComboBox(labels);
		done = new JButton("Done");
		buildView();
	}

	public void buildView(){
		theIndex = 0;
		removeAll();
		setLayout(new BorderLayout());
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		box.addActionListener(this);
		done.addActionListener(this);
		panel.add(box);
		panel.add(done);
		add(panel);
	}

	public void actionPerformed(ActionEvent e){
		Object source = e.getSource();
		if (source.equals(box)){
			theIndex = box.getSelectedIndex();
		}
		if (source.equals(done)){
			Column col = table.getColumn(theIndex);
			table.removeColumn(theIndex);
			table.addColumn(col);
			module.done(table,0);
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "table";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "outputTable";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

