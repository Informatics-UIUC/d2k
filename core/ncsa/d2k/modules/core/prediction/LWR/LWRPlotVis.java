package ncsa.d2k.modules.core.prediction.LWR;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.gui.JD2KFrame;

import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.modules.core.vis.*;

import java.io.Serializable;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

/**
   LWRPlotVis.java

	Given a PredictionTable, plot each numeric input variable against the 
	single numeric output, giving curve of some type.  The ExampleTable is
	used to make scatter plots of each of the inputs versus all other inputs.

   @author David Clutter
*/
public class LWRPlotVis extends VisModule
	implements HasNames, Serializable {

	static int ROW_HEIGHT = 150;
	static int ROW_WIDTH = 150;

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		StringBuffer sb = new StringBuffer("Given an ExampleTable, plot ");
		sb.append("each numeric input variable against each numeric ");
		sb.append("output variable in a ScatterPlot.  A matrix of these ");
		sb.append("plots is shown.  The plots can be selected and a ");
		sb.append("larger composite graph of these plots can be displayed.");
		return sb.toString();
    }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "ETScatterPlot";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String []in = {"ncsa.d2k.modules.core.datatype.table.PredictionTable",
					"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
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
		if (i == 0)
			return "The PredictionTable to plot.";
		else
			return "A original data to graph in a ScatterPlot.";
    }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		return "ET";
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

	protected UserView createUserView() {
		return new LWRPlotView();
	}

	protected String[] getFieldNameMapping() {
		return null;
	}

	/**
		Create a small graph to be shown in the matrix.
		@param vt the table with the data values
		@param d the DataSets to plot
		@param gs the GraphSettings for this plot
	*/
	protected Graph createSmallScatterGraph(TableImpl vt, DataSet[] d,
		GraphSettings gs) {
		return new ScatterPlotSmall(vt, d, gs);
	}

	protected Graph createSmallFunctionGraph(TableImpl vt, DataSet[] d,
		GraphSettings gs) {
		return new FunctionPlotSmall(vt, d, gs);
	}

	/**
		Create a large graph to be shown in the composite window.
		@param vt the table with the data values
		@param d the DataSets to plot
		@param gs the GraphSettings for this plot
	*/
	protected Graph createGraph(TableImpl vt, DataSet[] d,
		GraphSettings gs) {
		return new ScatterPlot(vt, d, gs);
	}

	Random r = new Random();

	/**
		Get a random Color.  Tries to avoid pure white and pure black.
	*/
	protected Color getRandomColor() {
		while(true) {
			int red = r.nextInt() % 256;
			int green = r.nextInt() % 256;
			int blue = r.nextInt() % 256;

			// try to avoid white and black
			if(red > 5 && green > 5 && blue > 5)
				if(red < 250 && green < 250 && blue < 250)
					return new Color(red, green, blue);
		}
	}

	/**
		Shows all the Graphs in a JTable
	*/
	class LWRPlotView extends JUserPane implements
		Serializable, MouseListener /*, ActionListener*/ {

		ExampleTableImpl et;
		//JButton showComposite;
		//JButton clearSelected;
		int []inputs;
		int []outputs;
		JTable jTable = null;
		//boolean [][]selected = null;

		PredictionTableImpl plotTable;
		ExampleTableImpl scatterTable;

		public void initView(ViewModule m) {
		}

		public void setInput(Object o, int i) {
			if(i == 0)
				plotTable = (PredictionTableImpl)o;
			else if(i == 1) {
				scatterTable = (ExampleTableImpl)o;
				setup();
			}
	 	}

		void setup() {
			/*int []tempinputs = et.getInputFeatures();
			int []tempoutputs = et.getOutputFeatures();

			LinkedList list = new LinkedList();
			// get the numeric output features
			for(int i = 0; i < tempoutputs.length; i++) {
				if(et.getColumn(tempoutputs[i]) instanceof NumericColumn)
					list.add(new Integer(tempoutputs[i]));
			}

			outputs = new int[list.size()];
			Iterator iter = list.iterator();
			int idx = 0;
			while(iter.hasNext()) {
				outputs[idx] = ((Integer)iter.next()).intValue();
				idx++;
			}

			list.clear();

			// get the numeric input features
			for(int i = 0; i < tempinputs.length; i++) {
				if(et.getColumn(tempinputs[i]) instanceof NumericColumn)
					list.add(new Integer(tempinputs[i]));
			}

			inputs = new int[list.size()];
			iter = list.iterator();
			idx = 0;
			while(iter.hasNext()) {
				inputs[idx] = ((Integer)iter.next()).intValue();
				idx++;
			}
			list.clear();
			list = null;
			*/

			/*for(int i = 0; i < inputs.length; i++)
				System.out.println("in: "+inputs[i]);
			for(int i = 0; i <  outputs.length; i++)
				System.out.println("out: "+outputs[i]);
			*/

			// setup the JTable

			// setup the columns for the matrix
			TableColumnModel cm = new DefaultTableColumnModel() {
				boolean first = true;
				public void addColumn(TableColumn tc) {
					if(first) { first = false; return; }
					tc.setMinWidth(ROW_WIDTH);
					super.addColumn(tc);
				}
			};

			// setup the columns for the row header table
			TableColumnModel rowHeaderModel = new DefaultTableColumnModel() {
				boolean first = true;
				public void addColumn(TableColumn tc) {
					if(first) {
						super.addColumn(tc);
						first = false;
					}
				}
			};

			LWRPlotTableModel tblModel = new LWRPlotTableModel();

			// setup the row header table
			JTable headerColumn = new JTable(tblModel, rowHeaderModel);
			headerColumn.setRowHeight(ROW_HEIGHT);
			headerColumn.setRowSelectionAllowed(false);
			headerColumn.setColumnSelectionAllowed(false);
			headerColumn.setCellSelectionEnabled(false);
			headerColumn.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			headerColumn.getTableHeader().setReorderingAllowed(false);
			headerColumn.createDefaultColumnsFromModel();

			// setup the graph matrix
			jTable = new JTable(tblModel, cm);
			jTable.createDefaultColumnsFromModel();
			//jTable.setModel(new ColumnPlotTableModel());
			jTable.setDefaultRenderer(ImageIcon.class, new GraphRenderer());
			jTable.setRowHeight(ROW_HEIGHT);
			//jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jTable.setRowSelectionAllowed(false);
			jTable.setColumnSelectionAllowed(false);
			jTable.setCellSelectionEnabled(false);
			jTable.addMouseListener(this);
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			jTable.getTableHeader().setReorderingAllowed(false);
			jTable.getTableHeader().setResizingAllowed(false);

			// put the row headers in the viewport
			/*JViewport jv = new JViewport();
			jv.setView(headerColumn);
			jv.setPreferredSize(headerColumn.getPreferredSize());

			// setup the scroll pane
			JScrollPane sp = new JScrollPane(jTable);
			sp.setRowHeader(jv);
			sp.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			sp.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			*/

			int numRows = jTable.getModel().getRowCount();
			int numColumns = jTable.getModel().getColumnCount();

			int longest = 0;
			// we know that the first column will only contain
			// JLabels...so create them and find the longest
			// preferred width
			JLabel tempLabel = new JLabel();
			for(int i = 0; i < numRows; i++) {
				tempLabel.setText(
					jTable.getModel().getValueAt(i, 0).toString());
				if(tempLabel.getPreferredSize().getWidth() > longest)
					longest = (int)tempLabel.getPreferredSize().getWidth();
				tempLabel.setText("");
			}

			TableColumn column;
			// set the default column widths
			for(int i = 0; i < numColumns; i++) {
				if(i == 0) {
					column = headerColumn.getColumnModel().getColumn(i);
					column.setPreferredWidth(longest+4);
				}
				else {
					column = jTable.getColumnModel().getColumn(i-1);
					column.setPreferredWidth(ROW_WIDTH);
				}
			}

			// make the preferred view show four or less graphs
			int prefWidth;
			int prefHeight;
			if(numColumns < 4)
				prefWidth = (numColumns-1)*ROW_WIDTH;
			else
				prefWidth = (4)*ROW_WIDTH;
			if(numRows < 4)
				prefHeight = numRows * ROW_HEIGHT;
			else
				prefHeight = 4 * ROW_HEIGHT;
			jTable.setPreferredScrollableViewportSize(new Dimension(
				prefWidth, prefHeight));

			// put the row headers in the viewport
			JViewport jv = new JViewport();
			jv.setView(headerColumn);
			jv.setPreferredSize(headerColumn.getPreferredSize());

			// setup the scroll pane
			JScrollPane sp = new JScrollPane(jTable);
			sp.setRowHeader(jv);
			sp.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			sp.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

			this.add(sp, BorderLayout.CENTER);

			/*JPanel buttonPanel = new JPanel();
			showComposite = new JButton("Show Composite");
			showComposite.addActionListener(this);
			buttonPanel.add(showComposite);
			clearSelected = new JButton("Clear Selected Graphs");
			clearSelected.addActionListener(this);
			buttonPanel.add(clearSelected);
			this.add(buttonPanel, BorderLayout.SOUTH);
			*/
		}

		/**
			When the mouse is pressed, update the selected item.
		*/
		public void mousePressed(MouseEvent e) {/*
			int iRow = e.getY()/jTable.getRowHeight();
			int iCol = jTable.getTableHeader().columnAtPoint(new Point(
				e.getX(), e.getY()));
			selected[iRow][iCol] = !selected[iRow][iCol];
			jTable.repaint();*/
		}
		public void mouseClicked(MouseEvent e) {
			GraphSettings settings = new GraphSettings();
			settings.displaylegend = false;
			settings.gridsize = 5;
			JPanel popUp;
			DataSet[] set;

			int iRow = e.getY()/jTable.getRowHeight();
			int iCol = jTable.getTableHeader().columnAtPoint(new Point(
				e.getX(), e.getY()));
			//if (iCol >0) {
			if (iRow == 0){

				DataSet tmpset = new DataSet("scatter-popup", Color.blue, iCol, 2*iCol+scatterTable.getNumInputFeatures()+1);
				set = new DataSet[1];
				set[0] = tmpset;

				popUp = new FunctionPlot(plotTable, set, settings);
				popUp.setPreferredSize(new Dimension(500,333));

			}
			else{
				DataSet tmpset = new DataSet("scatter-popup", Color.blue, iCol, iRow-1);
				set = new DataSet[1];
				set[0] = tmpset;
				popUp = new ScatterPlot(scatterTable, set, settings);
				popUp.setPreferredSize(new Dimension(500,333));
			}

			JFrame frame = new JFrame("Graph");
			frame.getContentPane().add(popUp, BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
			//}
		}

		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}

		/**
			Create a graph made up of all the selected items.
		*/
		/*public void actionPerformed(ActionEvent e) {
			if(e.getSource() == showComposite) {
				// get all the selected graphs
				// and make a list of them
				LinkedList ll = new LinkedList();
				for(int i = 0; i < outputs.length; i++) {
					for(int j = 0; j < inputs.length; j++) {
						if(selected[i][j]) {
							StringBuffer sb = new StringBuffer(
								et.getColumnLabel(outputs[i]));
							sb.append(",");
							sb.append(et.getColumnLabel(inputs[j]));
							// use a random color for each DataSet
							DataSet ds = new DataSet(sb.toString(),
								getRandomColor(), inputs[j], outputs[i]);
							ll.add(ds);
						}
					}
				}

				DataSet []data = new DataSet[ll.size()];
				Iterator iter = ll.iterator();
				int idx = 0;
				while(iter.hasNext()) {
					data[idx] = (DataSet)iter.next();
					idx++;
				}
				GraphSettings settings = new GraphSettings();
				settings.title = "Composite";
				settings.xaxis = "";
				settings.yaxis = "";
				settings.displayaxislabels = false;
				settings.displaylegend = true;
				settings.displaytitle = false;

				if(data.length > 0) {
					Graph graph = createGraph(et, data, settings);
					JD2KFrame frame = new JD2KFrame("Composite Graph");
					frame.getContentPane().add(graph);
					frame.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							((Frame)e.getSource()).setVisible(false);
							((Frame)e.getSource()).dispose();
						}
					});
					frame.setSize(400, 350);
					frame.show();
				}
			}
			else if(e.getSource() == clearSelected) {
				for(int i = 0; i < outputs.length; i++)
					for(int j = 0; j < inputs.length; j++)
						selected[i][j] = false;
				jTable.repaint();
			}
		}*/

		/**
			We handle the selection of cells in the JTable on our own.
			Cells that have been selected are marked true in the selected[][]
			matrix.
		*/
		/*class GraphTable extends JTable {
			public GraphTable(TableModel tm, TableColumnModel tcm) {
				super(tm, tcm);
			}

			public Component prepareRenderer(TableCellRenderer renderer,
				int row, int column) {

				Object value = getValueAt(row, column);
				boolean isSelected = selected[row][column];
				boolean rowIsAnchor =
					(selectionModel.getAnchorSelectionIndex() == row);
				boolean colIsAnchor =
	    			(columnModel.getSelectionModel().getAnchorSelectionIndex() == column);
				boolean hasFocus = (rowIsAnchor && colIsAnchor) && hasFocus();

				return renderer.getTableCellRendererComponent(this, value,
					isSelected, hasFocus,
					row, column);
			}
    	}*/

		/**
			A custom cell renderer that shows an ImageIcon.  A blue border is
			painted around the selected items.
		*/
		class GraphRenderer extends JLabel implements TableCellRenderer {

			Border unselectedBorder = null;
			Border selectedBorder = null;

			public GraphRenderer() {
				super();
				setOpaque(true);
			}

			/**
				Set the icon and paint the border for this cell.
			*/
			public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
				this.setIcon((ImageIcon)value);
				if (true) {
                	if (isSelected) {
                    	if (selectedBorder == null) {
                        	selectedBorder = BorderFactory.createMatteBorder(2,
								2,2,2,
								/*table.getSelectionBackground()*/Color.blue);
                    	}
                    	setBorder(selectedBorder);
                	}
					else {
                    	if (unselectedBorder == null) {
                        	unselectedBorder =
								BorderFactory.createMatteBorder(2,2,2,2,
								table.getBackground());
                    	}
                    setBorder(unselectedBorder);
                	}
            	}
				return this;
			}
		}

		/**
			The table's data model.  Keeps a matrix of images that are
			displayed in the table.  The images are created from the
			Graphs.
		*/
		//class ColumnPlotTableModel extends AbstractTableModel {
		class LWRPlotTableModel extends AbstractTableModel {
			ImageIcon [][]images;

			//ColumnPlotTableModel() {
			LWRPlotTableModel() {

				int numAttributes = scatterTable.getNumInputFeatures();
				images = new ImageIcon[numAttributes+1][numAttributes];
				//selected = new boolean[numAttributes+1][numAttributes];
				GraphSettings settings = new GraphSettings();
				settings.displayaxislabels = false;
				settings.displaylegend = false;
				settings.displaytitle = false;
				Image img;
				// a frame is needed to create an Image
				Frame f = new Frame();
				f.addNotify();

				// create each graph and make an image of it.
				// the image is what is shown in the JTable,
				// because it is more efficient than showing the
				// actual graph
				for(int i = 0; i < numAttributes+1; i++) {	//iterate over rows

					for(int j = 0; j < numAttributes; j++) {	//iterate over column
						if (i == 0) {
							DataSet []data = new DataSet[1];
							data[0] = new DataSet("", Color.red, j, 2*j+scatterTable.getNumInputFeatures()+1);
							Graph graph = createSmallFunctionGraph(plotTable, data, settings);
							img = f.createImage(ROW_WIDTH, ROW_HEIGHT);
							Graphics imgG = img.getGraphics();
							graph.setSize(new Dimension(ROW_WIDTH, ROW_HEIGHT));
							graph.paintComponent(imgG);
							/*DataSet []data = new DataSet[1];
							data[0] = new DataSet("",
								Color.red, j, 0);
							settings.xaxis = scatterTable.getColumnLabel(j);
							settings.yaxis = scatterTable.getColumnLabel(0);

							Graph graph = createSmallScatterGraph(scatterTable, data, settings);
							img = f.createImage(ROW_WIDTH, ROW_HEIGHT);
							Graphics imgG = img.getGraphics();
							graph.setSize(new Dimension(ROW_WIDTH, ROW_HEIGHT));
							graph.paintComponent(imgG);*/


						}
						else {
							DataSet []data = new DataSet[1];
							data[0] = new DataSet("",
								Color.red, j, i-1);
							//settings.xaxis = scatterTable.getColumnLabel(j);
							//settings.yaxis = scatterTable.getColumnLabel(i-1);
							settings.xaxis = getColumnName2(j);
							settings.yaxis = getColumnName2(i-1);

							Graph graph = createSmallScatterGraph(scatterTable, data, settings);
							img = f.createImage(ROW_WIDTH, ROW_HEIGHT);
							Graphics imgG = img.getGraphics();
							graph.setSize(new Dimension(ROW_WIDTH, ROW_HEIGHT));
							graph.paintComponent(imgG);
						}

						images[i][j] = new ImageIcon(img);
					}
				}
				f.dispose();
			}

			/**
				There is one more column than there are input features.
				The first column shows the output variables.
			*/
			public int getColumnCount() {
				//return scatterTable.getNumColumns();
				return scatterTable.getNumInputFeatures()+1;
			}

			/**
				There are the same number of rows as output features.
			*/
			public int getRowCount() {
				//return scatterTable.getNumColumns();
				return scatterTable.getNumInputFeatures()+1;
			}

			public String getColumnName2(int col) {
				int[] inputs = scatterTable.getInputFeatures();
				if (col < scatterTable.getNumInputFeatures())
					return scatterTable.getColumnLabel(inputs[col]);
				else
					return scatterTable.getColumnLabel(scatterTable.getNumColumns()-1);

			}

			public String getColumnName(int col) {
				if(col == 0)
					return "";
				else {
					int[] inputs = scatterTable.getInputFeatures();
					return scatterTable.getColumnLabel(inputs[col-1]);
				}
			}


			public Object getValueAt(int row, int col) {
				if(col == 0)
					if (row == 0)
						return scatterTable.getColumnLabel(scatterTable.getNumColumns()-1);
					else
						return scatterTable.getColumnLabel(row-1);
				else
					return images[row][col-1];
			}

			/**
				This must be overridden so that our custom cell renderer is
				used.
			*/
			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		}
	}
}
