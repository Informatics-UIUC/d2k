package ncsa.d2k.modules.core.vis;


import java.awt.*;
import java.text.*;
import javax.swing.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
 * Displays statistics about any PredictionTable.  The number of correct
 * predictions and a confusion matrix are included.
   @author David Clutter
*/
public class PredictionTableReport extends VisModule  {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Display some statistics about a PredictionTable, including the number of     correct predictions and a confusion matrix.  </body></html>";
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "PredictionTableReport";
	}

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
		return types;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "A PredictionTable.";
			default: return "No such input";
		}
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		switch(i) {
			case 0:
				return "predTable";
			default: return "NO SUCH INPUT!";
		}
	}

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		switch (i) {
			default: return "No such output";
		}
	}

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
		switch(i) {
			default: return "NO SUCH OUTPUT!";
		}
	}

    /**
       Not used.
    */
    public String[] getFieldNameMapping() {
		return null;
    }

    /**
       Return the UserView that will display the table.
       @return The UserView part of this module.
    */
    public UserView createUserView() {
		return new PredView();
    }

    /**
       The TableView class.  Uses a JTable and a VerticalTableModel to
       display the contents of a VerticalTable.
    */
    public class PredView extends JUserPane {

		/**
	   		Initialize the view.  Insert all components into the view.
	   		@param mod The VerticalTableViewer module that owns us
		*/
		public void initView(ViewModule mod) {
		}

		public Dimension getPreferredSize() {
			Dimension d = jtp.getPreferredSize();
			double wid = d.getWidth();
			double hei = d.getHeight();

			if(wid > 400)
				wid = 400;
			if(hei > 400)
				hei = 400;

			return new Dimension((int)wid, (int)hei);
		}

		JTabbedPane jtp;

		/**
	   		Called whenever inputs arrive to the module.
	   		@param input the Object that is the input
	   		@param idx the index of the input
		*/
		public void setInput(Object input, int idx) {
			PredictionTable pt = (PredictionTable)input;
			int []outputs = pt.getOutputFeatures();
			int []preds = pt.getPredictionSet();

			jtp = new JTabbedPane();
			for(int i = 0; i < outputs.length; i++) {
				// create a new InfoArea and ConfusionMatrix for each
				// and put it in a JPanel and put the JPanel in
				// the tabbed pane

				int outCol = outputs[i];
				int predCol = preds[i];

				// create the confusion matrix
				ConfusionMatrix cm = new ConfusionMatrix(pt, outputs[i], preds[i]);

				// get the number correct from the confusion matrix
				int numCorrect = cm.correct;
				int numIncorrect = pt.getNumRows() - numCorrect;

				// append data to the JTextArea
				JTextArea jta = new JTextArea();
				jta.append("Accuracy\n");
				jta.append("   Correct Predictions: "+numCorrect+"\n");
				jta.append("   Incorrect Predictions: "+numIncorrect+"\n");
				jta.append("   Total Number of Records: "+pt.getNumRows()+"\n");
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(2);
				jta.append("\n");

				double pCorrect = ((double)numCorrect)/((double)pt.getNumRows())*100;
				double pIncorrect = ((double)numIncorrect)/((double)pt.getNumRows())*100;

				jta.append("   Percent correct: "+nf.format(pCorrect)+"%\n");
				jta.append("   Percent incorrect: "+nf.format(pIncorrect)+"%\n");

				jta.setEditable(false);

				StringColumn sc = new StringColumn(2);
				sc.setString("Correct", 0);
				sc.setString("Incorrect", 1);
				DoubleColumn ic = new DoubleColumn(2);
				ic.setDouble(((double)numCorrect)/((double)pt.getNumRows()), 0);
				ic.setDouble(((double)numIncorrect)/((double)pt.getNumRows()), 1);
				Column[] col = new Column[2];
				col[0] = sc;
				col[1] = ic;
				MutableTableImpl tbl = (MutableTableImpl)DefaultTableFactory.getInstance().createTable(col);
				// create the pie chart
				DataSet ds = new DataSet("Accuracy", null, 0, 1);
				GraphSettings gs = new GraphSettings();
				gs.title = "Accuracy";
				gs.displaytitle = true;
				gs.displaylegend = true;
				PieChart pc = new PieChart(tbl, ds, gs);

				JPanel p1 = new JPanel();
				p1.setLayout(new GridLayout(1, 2));
				p1.add(new JScrollPane(jta));
				p1.add(pc);
				// add everything to this
				JPanel pp = new JPanel();
				pp.setLayout(new GridLayout(2, 1));
				pp.add(p1);
				JPanel pq = new JPanel();
				pq.setLayout(new BorderLayout());
				pq.add(new JLabel("Confusion Matrix"), BorderLayout.NORTH);
				//cm.setColumnHeaderView(new JLabel("Confusion Matrix"));
				pq.add(cm, BorderLayout.CENTER);
				pp.add(pq);
				jtp.addTab(pt.getColumnLabel(outputs[i]), pp);
			}
			setLayout(new BorderLayout());
			add(jtp, BorderLayout.CENTER);
		}
	}
}
