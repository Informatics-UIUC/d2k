package ncsa.d2k.modules.core.vis.widgets;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.gui.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;


public class FPlotAll extends JPanel {

	public FPlotAll(Table table1, Table table2){
		super();
		PlotMatrix plot = new PlotMatrix(table1, table2);
		MatrixHolder matrix = new MatrixHolder(plot);
		int numAttributes = table2.getNumColumns()-1;

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;

		JPanel horizontalLabels = new JPanel();
		JPanel verticalLabels = new JPanel();
		horizontalLabels.setLayout(new GridLayout(0,numAttributes,2,2));
		verticalLabels.setLayout(new GridLayout(numAttributes,1,2,2));

		int num = table2.getNumColumns()-1;
		JLabel blank = new JLabel(table2.getColumnLabel(num));
		verticalLabels.add(blank);

		for (int i=0; i<numAttributes; i++){
			JLabel l = new JLabel(table2.getColumnLabel(i));
			horizontalLabels.add(l);
		}

		for (int i=0; i<numAttributes-1; i++){
			JLabel l = new JLabel(table2.getColumnLabel(i));
			verticalLabels.add(l);
		}

		//add four components
		c.gridx = 0;
		c.gridy = 0;
		JPanel blankpanel = new JPanel();
		gridbag.setConstraints(blankpanel, c);
		add(blankpanel);

		c.weightx = .2;
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(verticalLabels, c);
		add(verticalLabels);

		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(horizontalLabels, c);
		add(horizontalLabels);

		c.weightx = 0.8;
		c.weighty = 1.0;
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(matrix, c);
		add(matrix);

	}
}


class MatrixHolder extends JScrollPane {

	/**Constructor
		This is the scroll pane that contains the PlotMatrix object.  All of
		the functionality of the visualization is within the PlotMatrix class
		@param PlotMatrix plot - the matrix of scatterplots and functionplots
	*/
	public MatrixHolder(PlotMatrix plot) {
		super(plot);
		setPreferredSize(new Dimension(800,600));

	}

}

class PlotMatrix extends JPanel implements MouseListener{

	Table plotTable;
	Table scatterTable;

	int NUM_ATTRIBUTES; //the number of input variables (i.e. x_1....x_n)
	JPanel[] graphs; //an array to store all of the graphs

	/**Constructor
		This makes the matrix of scatter and function plots.  The matrix is
		(n+1) rows by n columns (n = number attributes).
		@param table1 - the table of data for lines of best fit
		@param table2 - the table of data for scatter plots
	*/
	public PlotMatrix(Table table1, Table table2){
		plotTable = table1;// the table containing line of best fit data
		scatterTable = table2;// the table containing scatter plot data
		NUM_ATTRIBUTES = scatterTable.getNumColumns()-1;
		graphs = new JPanel[NUM_ATTRIBUTES*(NUM_ATTRIBUTES+1)];

		for (int i=0; i<(NUM_ATTRIBUTES); i++){
			//set up a function plot with graphsettings
			//there is one function plot for each attribute vs. the output
			int xvalues = 3*i;
			int yvalues = xvalues+1;
			GraphSettings settings = new GraphSettings();
			settings.displaylegend = false;
			DataSet[] data = {new DataSet("function "+i, Color.blue, xvalues, yvalues)};
			settings.gridsize = 5;
			graphs[i] = new FunctionPlotSmall(plotTable, data, settings);
			graphs[i].setMinimumSize(new Dimension(80,60));
		}
		for (int h=NUM_ATTRIBUTES; h<((NUM_ATTRIBUTES)*(NUM_ATTRIBUTES));h++){
			if ((h/NUM_ATTRIBUTES-1) >= h%NUM_ATTRIBUTES) {
				//makes only the upper triangular scatterplot matrix
				graphs[h] = new JPanel();
				graphs[h].setBackground(Color.white);
				graphs[h].setMinimumSize(new Dimension(80,60));
			}
			else {
				//set up a scatter plot with graphsettings
				int xvalues = h%NUM_ATTRIBUTES;
				int yvalues = h/NUM_ATTRIBUTES-1;
				GraphSettings settings = new GraphSettings();
				settings.displaylegend = false;
				DataSet[] set = {new DataSet("scatter "+h, Color.black, xvalues, yvalues)};
				settings.gridsize = 5;
				graphs[h] = new ScatterPlotSmall(scatterTable,set,settings);
				graphs[h].setMinimumSize(new Dimension(80,60));
			}
		}

		setPreferredSize(new Dimension(80*NUM_ATTRIBUTES,60*(1+NUM_ATTRIBUTES)));
		setLayout(new GridLayout(0,NUM_ATTRIBUTES,2,2));
		addMouseListener(this);
		//add all the graphs
		for (int j=0; j<(NUM_ATTRIBUTES*(NUM_ATTRIBUTES)); j++){
			add(graphs[j]);
		}

	}

	public void mouseClicked(MouseEvent e) {
		//Adds the functionality of making a larger pop-up plot come up when
		//the user clicks
		Point p = e.getPoint();
		int x = (int)p.getX();
		int y = (int)p.getY();
		double col = x/(graphs[0].getSize()).getWidth();
		int theColumn = (int) col;
		double row = y/(graphs[0].getSize()).getHeight();
		int theRow = (int) row;
		GraphSettings settings = new GraphSettings();
		settings.displaylegend = false;
		settings.gridsize = 5;
		JPanel popUp;
		DataSet[] set;
		if (theRow == 0){
			DataSet tmpset = new DataSet("scatter-popup", Color.green, 3*theColumn, 3*theColumn+1);
			set = new DataSet[1];
			set[0] = tmpset;

			popUp = new FunctionPlot(plotTable, set, settings);
			popUp.setPreferredSize(new Dimension(500,333));

		}
		else{
			DataSet tmpset = new DataSet("scatter-popup", Color.green, theColumn, theRow-1);
			set = new DataSet[1];
			set[0] = tmpset;
			popUp = new ScatterPlot(scatterTable, set, settings);
			popUp.setPreferredSize(new Dimension(500,333));
		}

		/*System.out.println("mouse clicked");
		System.out.println("the Column is "+set[0].x);
		System.out.println("the Row is "+set[0].y);*/
		JFrame frame = new JFrame("Graph");
		frame.getContentPane().add(popUp, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);


	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

}
