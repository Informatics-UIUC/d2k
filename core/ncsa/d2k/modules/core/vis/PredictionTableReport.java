package ncsa.d2k.modules.core.vis;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.UserView;
import ncsa.d2k.controller.userviews.swing.JUserPane;
import ncsa.d2k.util.datatype.*;

import ncsa.d2k.modules.core.vis.widgets.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
   @author David Clutter
*/
public class PredictionTableReport extends VisModule implements HasNames {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		String s = "Display some statistics about a PredictionTable, including ";
		s += "the number of correct predictions and a confusion matrix.";
		return s;
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
		String[] in = {"ncsa.d2k.util.datatype.PredictionTable"};
		return in;
    }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		return null;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		return "A PredictionTable.";
    }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		return "predTable";
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		return "";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
		return "";
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
			return new Dimension(400, 400);
		}

		/**
	   		Called whenever inputs arrive to the module.
	   		@param input the Object that is the input
	   		@param idx the index of the input
		*/
		public void setInput(Object input, int idx) {
			PredictionTable pt = (PredictionTable)input;
			//ExampleTable pt = (ExampleTable)input;
			int []outputs = pt.getOutputFeatures();
			int []preds = pt.getPredictionSet();
			/*if(pt instanceof PredictionTable) {
				preds = ((PredictionTable)pt).getPredictionSet();
			}
			else {*/

			//preds = new int[outputs.length];

			// assumed that the last outputs.length columns are
			// prediction columns
			/*int j = 0;
			for(int i = pt.getNumColumns()-preds.length;
				i < pt.getNumColumns(); i++) {
					preds[j] = i;
					//System.out.println(preds[j]);
					j++;
			}
			//}
			*/

			JTabbedPane jtp = new JTabbedPane();
			for(int i = 0; i < outputs.length; i++) {
				// create a new InfoArea and ConfusionMatrix for each
				// and put it in a JPanel and put the JPanel in
				// the tabbed pane

				int outCol = outputs[i];
				int predCol = preds[i];

				// create the confusion matrix
				ConfusionMatrix cm = new ConfusionMatrix(pt,
					outputs[i], preds[i]);

				// get the number correct from the confusion matrix
				int numCorrect = cm.correct;
				int numIncorrect = pt.getNumRows() - numCorrect;

				// append data to the JTextArea
				JTextArea jta = new JTextArea();
				jta.append("Accuracy\n");
				jta.append("   Correct: "+numCorrect+"\n");
				jta.append("   Incorrect: "+numIncorrect+"\n");
				jta.append("   Total: "+pt.getNumRows());
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
				VerticalTable tbl = new VerticalTable(col);
				// create the pie chart
				DataSet ds = new DataSet("Accuracy", null, 0, 1);
				GraphSettings gs = new GraphSettings();
				gs.title = "Accuracy";
				gs.displaytitle = true;
				gs.displaylegend = true;
				PieChart pc = new PieChart(tbl, ds, gs);
				/*Dimension dd = cm.getPreferredSize();
				double pw = dd.getWidth();
				double ph = dd.getHeight();
				if(pw > 400)
					pw = 400;
				if(ph > 400)
					ph = 400;
				cm.setPreferredSize(new Dimension((int)pw, (int)ph));
				pc.setPreferredSize(new Dimension((int)pw/2, (int)pw/2));
				*/

				JPanel p1 = new JPanel();
				p1.setLayout(new GridLayout(1, 2));
				p1.add(new JScrollPane(jta));
				p1.add(pc);
				// add everything to this
				JPanel pp = new JPanel();
				pp.setLayout(new GridLayout(2, 1));
				pp.add(p1);
				pp.add(cm);
				jtp.addTab(pt.getColumnLabel(outputs[i]), pp);
			}
			setLayout(new BorderLayout());
			add(jtp, BorderLayout.CENTER);
		}
	}
}
