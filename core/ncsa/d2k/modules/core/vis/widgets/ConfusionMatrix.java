package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

import ncsa.d2k.util.datatype.*;

/**
	A simple ConfusionMatrix.  Implemented as a JTable inside a JScrollPane.
	This class will calculate the confusion matrix given a Table,
	the index of the output column and the index of its associated
	prediction column.  ConfusionMatrix can also be used as a simple
	table of numbers when the appropriate constructor is used.
*/
public class ConfusionMatrix extends JScrollPane {

	public static void main(String[] args) {
		String[] r = {"one", "two", "three"};
		int[][] d = {{1, 2, 3}, {1, 2, 3}, {1, 2, 3}};
		ConfusionMatrix cm = new ConfusionMatrix(d, r, r);
		JFrame f = new JFrame("");
		f.setSize(400, 400);
		f.getContentPane().add(cm);
		f.setVisible(true);
	}

	public int correct;

	/**
		Create a new ConfusionMatrix
		@param d the data to show
		@param r the row headers
		@param c the column headers
	*/
	public ConfusionMatrix(int[][] d, String[] r, String[] c) {
		super();
		setupTable(d, r, c);
	}

	/**
		Create a new ConfusionMatrix
		@param vt the Table with the outputs and predictions
		@param o the index of the output column
		@param p the index of the prediction column
	*/
	public ConfusionMatrix(Table vt, int o, int p) {
		super();
		int numRows = vt.getNumRows();

		// keep the unique outputs and predictions
		HashMap outNames = new HashMap();
		for(int i = 0; i < numRows; i++) {
			String s = vt.getString(i, o);
			if(!outNames.containsKey(s))
				outNames.put(s, s);
		}

		// keep the unique outputs and predictions
		HashMap predNames = new HashMap();
		for(int i = 0; i < numRows; i++) {
			String s = vt.getString(i, p);
			if(!predNames.containsKey(s))
				predNames.put(s, s);
		}

		String []outs = new String[outNames.size()];
		String []outlabels = new String[outNames.size()];
		int idx = 0;
		Iterator it = outNames.keySet().iterator();
		while(it.hasNext()) {
			String s = (String)it.next();
			outs[idx] = s;
			outlabels[idx] = "Out: "+s;
			idx++;
		}

		String []preds = new String[predNames.size()];
		String []predlabels = new String[predNames.size()];
		idx = 0;
		it = predNames.keySet().iterator();
		while(it.hasNext()) {
			String s = (String)it.next();
			preds[idx] = s;
			predlabels[idx] = "Pred: "+s;
			idx++;
		}

		// calculate the confusion matrix
		int[][] d = new int[outs.length][preds.length];

		for(int row = 0; row < vt.getNumRows(); row++) {
			int actual = 0;
			int predicted = 0;
			for(int i = 0; i < outs.length; i++) {
				if(vt.getString(row, o).equals(outs[i])) {
					actual = i;
					break;
				}
			}
			for(int i = 0; i < preds.length; i++) {
				if(vt.getString(row, p).equals(preds[i])) {
					predicted = i;
					break;
				}
			}
			//try {
			d[predicted][actual]++;
			/*}
			catch(Exception e) {
				System.out.println("pred: "+predicted);
				System.out.println("act: "+actual);
				System.out.println(preds.length);
				System.out.println(outs.length);
			}*/
		}

		correct = 0;
		for(int i = 0; i < outs.length; i++)
			for(int j = 0; j < outs.length; j++)
				if(i == j)
					correct += d[i][j];

		setupTable(d, outlabels, predlabels);
	}

	/**
		Setup the row headers.
	*/
	private void setupTable(int[][] d, String[] r, String[] c) {
		MatrixModel tblModel = new MatrixModel(d, r, c);
		/*JTable tmp = new JTable();
		Graphics g = tmp.getGraphics();
		FontMetrics fm = g.getFontMetrics();
		int max = 0;
		for (int i = 0; i < r.length; i++) {
			if(fm.stringWidth(c[i]) > max)
				max = fm.stringWidth(c[i]);
		}

		final int mm = max;*/
		TableColumnModel cm = new DefaultTableColumnModel() {
			boolean first = true;
			public void addColumn(TableColumn tc) {
				if(first) { first = false; return; }
				tc.setMinWidth(100);
				super.addColumn(tc);
			}
		};

		/*max = 0;
		for (int i = 0; i < r.length; i++) {
			if(fm.stringWidth(r[i]) > max)
				max = fm.stringWidth(r[i]);
		}

		final int mx = max;*/
		// setup the columns for the row header table
		TableColumnModel rowHeaderModel = new DefaultTableColumnModel() {
			boolean first = true;
			public void addColumn(TableColumn tc) {
				if(first) {
					tc.setMinWidth(100);
					super.addColumn(tc);
					first = false;
				}
			}
		};

		// setup the row header table
		JTable table = new JTable(tblModel, cm);
		table.createDefaultColumnsFromModel();

		JTable headerColumn = new JTable(tblModel, rowHeaderModel);
		headerColumn.createDefaultColumnsFromModel();
		headerColumn.setMaximumSize(new Dimension(75, 10000));

		JViewport jv = new JViewport();
		jv.setView(headerColumn);
		jv.setPreferredSize(headerColumn.getPreferredSize());

		setViewportView(table);
		setRowHeader(jv);
	}

	/**
		The TableModel for the ConfusionMatrix
	*/
	class MatrixModel extends AbstractTableModel {
		int[][] data;
		String[] rowNames;
		String[] colNames;

		MatrixModel(int[][] d, String[] r, String[] c) {
			data = d;
			rowNames = r;
			colNames = c;
		}

		public String getColumnName(int col) {
			if(col == 0)
				return "";
			return colNames[col-1];
		}

		public int getRowCount() {
			return rowNames.length;
		}

		public int getColumnCount() {
			return colNames.length+1;
		}

		public Object getValueAt(int row, int col) {
			if(col == 0)
				return rowNames[row];
			return Integer.toString(data[row][col-1]);
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}
}
