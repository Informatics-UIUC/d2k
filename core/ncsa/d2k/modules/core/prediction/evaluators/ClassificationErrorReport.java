package ncsa.d2k.modules.core.prediction.evaluators;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import javax.swing.*;
import java.text.*;
import java.awt.*;

/**
 * Shows some statistics about models evaluated using ClassificationErrorEvaluator.
 */
public class ClassificationErrorReport extends VisModule implements HasNames {

	public String getModuleInfo() {
		return "Shows statistics about each model built.";
	}

	public String getModuleName() {
		return "ClassificationErrorReport";
	}

	public String[] getInputTypes() {
		String[] t = {"ncsa.d2k.modules.core.prediction.evaluators.ClassificationErrorStats",
			"java.lang.Integer"};
		return t;
	}

	public String[] getOutputTypes() {
		return null;
	}

	public String getInputInfo(int i) {
		if(i == 0)
			return "Statistics about each model built.";
		else
			return "The number of models built.";
	}

	public String getInputName(int i) {
		if(i == 0)
			return "Stats";
		else
			return "N";
	}

	public String getOutputInfo(int i) {
		return "";
	}

	public String getOutputName(int i) {
		return "";
	}

	public String[] getFieldNameMapping() {
		return null;
	}

	protected UserView createUserView() {
		return new StatsView();
	}

	/**
	 * Consists of a JTabbedPane with N tabs.  Each tab contains
	 * a JTextArea with a description of each fold.
	 */
	private final class StatsView extends JUserPane {

		ClassificationErrorStats stats;
		int numFolds;
		NumberFormat nf;

		public void initView(ViewModule m) {
			nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
		}

		boolean hasStats = false;
		boolean hasNum = false;

		public void setInput(Object o, int i) {
			if(i == 0) {
				stats = (ClassificationErrorStats)o;
				hasStats = true;
			}
			else if(i == 1) {
				numFolds = ((Integer)o).intValue();
				hasNum = true;
			}
			if(hasStats && hasNum)
				setup();
		}

		private final void setup() {
			/*JTabbedPane jtp = new JTabbedPane();
			JTextArea [] jta = new JTextArea[numFolds];
			for(int i = 0; i < jta.length; i++)
				jta[i] = new JTextArea();

			for(int i = 0; i < jta.length; i++) {
				int train = stats.getTrainingSize(i);
				int test = stats.getTestingSize(i);
				int correct = stats.getCorrectPredictions(i);
				double per = stats.getPercentageCorrect(i);

				StringBuffer sb = new StringBuffer("Fold: ");
				sb.append(i);
				sb.append("\n");
				sb.append("\tTraining Set Size: ");
				sb.append(train);
				sb.append("\n");
				sb.append("\tTesting Set Size: ");
				sb.append(test);
				sb.append("\n");
				sb.append("\tNumber of Correct Predictions: ");
				sb.append(correct);
				sb.append("\n");
				sb.append("\tPercentage Correct: ");
				sb.append(nf.format(per*100));
				sb.append(" %");

				jta[i].append(sb.toString());
				jta[i].setEditable(false);
				jtp.addTab("Fold "+i, jta[i]);
			}

			setLayout(new BorderLayout());
			add(jtp, BorderLayout.CENTER);
			*/
			JTabbedPane bigTabbed = new JTabbedPane();

			for(int j =0; j < numFolds; j++) {
				JTabbedPane jtp = new JTabbedPane();
				PredictionTable pt = (PredictionTable)stats.getTable(j);
				int[] outputs = pt.getOutputFeatures();
				int[] preds = pt.getPredictionSet();
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
					TableImpl tbl = (TableImpl)DefaultTableFactory.getInstance().createTable(col);
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
					pq.add(cm, BorderLayout.CENTER);
					pp.add(pq);
					jtp.addTab(pt.getColumnLabel(outputs[i]), pp);
				}
				bigTabbed.addTab("Fold "+j, jtp);
			}
			setLayout(new BorderLayout());
			add(bigTabbed, BorderLayout.CENTER);
		}

		public Dimension getPreferredSize() {
			return new Dimension(400, 400);
		}
	}
}