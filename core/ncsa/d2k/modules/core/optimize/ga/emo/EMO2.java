package ncsa.d2k.modules.core.optimize.ga.emo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;

public class EMO2
    extends UIModule {

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.NsgaPopulation"};
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulationInfo",
        "ncsa.d2k.modules.core.optimize.ga.emo.NsgaPopulation",
        "ncsa.d2k.modules.core.optimize.ga.emo.NsgaPopulation"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  protected UserView createUserView() {
    return new EMOVisPane();
  }

  public String[] getFieldNameMapping() {
    return null;
  }

  private static final String SMALL_MULT = "Small Multiples of Scatterplots";
  private static final String PAR_COOR = "Parallel Coordinates";

  /**
   * The UserView
   */
  private class EMOVisPane
      extends JUserPane {
    JComboBox viewType;

    ScatterPlotWidget cumulative;
    ScatterPlotWidget current;
    ScatterPlotWidget lastRun;
    ScatterPlotWidget twoRunsAgo;

    NsgaPopulation currentPop;
    NsgaPopulation lastPop;
    NsgaPopulation twoPopsAgo;
    NsgaPopulation cumulativePop;

    MutableTable currentPopTable;
    MutableTable lastPopTable;
    MutableTable twoPopsAgoTable;
    MutableTable cumulativePopTable;

    JButton continueButton;
    JButton stopButton;

    boolean newPop = true;
    int maxGen = 0;
    int currentGen = 0;

    public void initView(ViewModule vm) {
      viewType = new JComboBox();
      viewType.addItem(SMALL_MULT);
      viewType.addItem(PAR_COOR);

      setLayout(new BorderLayout());

      JPanel buttonPanel = new JPanel();
      continueButton = new JButton("Continue");
      continueButton.setEnabled(false);
      continueButton.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          pushOutput(new EMOPopulationInfo(), 0);
          pushOutput(currentPop, 1);
          continueButton.setEnabled(false);
          newPop = true;
        }
      });
      stopButton = new JButton("Stop");
      stopButton.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          viewCancel();
        }
      });
      buttonPanel.add(continueButton);
      buttonPanel.add(stopButton);

      add(buttonPanel, BorderLayout.SOUTH);

      cumulative = new ScatterPlotWidget();
      current = new ScatterPlotWidget();
      lastRun = new ScatterPlotWidget();
      twoRunsAgo = new ScatterPlotWidget();

      JPanel pnl = new JPanel();
      pnl.setLayout(new GridLayout(2, 2));
      pnl.add(current);
      pnl.add(cumulative);
      pnl.add(lastRun);
      pnl.add(twoRunsAgo);

      add(pnl, BorderLayout.CENTER);

      cumulativePop = null;
      currentPop = null;
      lastPop = null;
      twoPopsAgo = null;
    }

    public void setInput(Object o, int z) {
      NsgaPopulation pop = (NsgaPopulation) o;
      if (newPop) {
        // reset the populations

        // update the cumulative population
        if (cumulativePop == null) {
          if (currentPop != null && lastPop != null) {
            cumulativePop = new NewNsgaPopulation(currentPop, lastPop);
            cumulativePopTable = (MutableTable)currentPopTable.copy();
            NsgaSolution[] nis = (NsgaSolution[]) (cumulativePop.getMembers());
            int num = nis.length;
            cumulativePopTable.setNumRows(num);
            fillFitnessTable(cumulativePop, cumulativePopTable);
          }
        }
        else {

          cumulativePop = new NewNsgaPopulation(currentPop, cumulativePop);
          NsgaSolution[] nis = (NsgaSolution[]) (cumulativePop.getMembers());
          int num = nis.length;
          cumulativePopTable.setNumRows(num);
          fillFitnessTable(cumulativePop, cumulativePopTable);
        }

        twoPopsAgo = lastPop;
        if(lastPopTable != null)
          twoPopsAgoTable = (MutableTable)lastPopTable.copy();
        lastPop = currentPop;
        if(currentPopTable != null)
          lastPopTable = (MutableTable)currentPopTable.copy();
        currentPop = pop;

        maxGen = currentPop.getMaxGenerations();
        newPop = false;

        if(currentPopTable == null) {
          int numObjs = currentPop.getNumObjectives();
          NsgaSolution[] nis = (NsgaSolution[]) (currentPop.getMembers());
          int num = nis.length;
          currentPopTable = (MutableTable) DefaultTableFactory.getInstance().
              createTable();
          for (int i = 0; i < numObjs; i++) {
            currentPopTable.addColumn(new double[num]);
            currentPopTable.setColumnLabel(currentPop.getObjectiveConstraints()[
                                           i].getName(),
                                           currentPopTable.getNumColumns() - 1);
          }
        }
        else {
          NsgaSolution[] nis = (NsgaSolution[]) (currentPop.getMembers());
          int num = nis.length;
          currentPopTable.setNumRows(num);
        }
      }
      currentGen = currentPop.getCurrentGeneration();
      if (currentGen == maxGen - 1)
        continueButton.setEnabled(true);

      fillFitnessTable(currentPop, currentPopTable);

      int[] sc = {0,1};

      if(currentPop != null)
        current.setTable(currentPopTable, sc);
      if(lastPop != null)
        lastRun.setTable(lastPopTable, sc);
      if(twoPopsAgo != null)
        twoRunsAgo.setTable(twoPopsAgoTable, sc);
      if(cumulativePop != null)
        cumulative.setTable(cumulativePopTable, sc);

      if (currentGen != maxGen - 1) {
        pushOutput(currentPop, 2);
      }
    }

    private void fillFitnessTable(NsgaPopulation pp, MutableTable tbl) {
      int numObjs = pp.getNumObjectives();
      NsgaSolution[] nis = (NsgaSolution[]) (pp.getMembers());
      int num = nis.length;

      tbl.setNumRows(num);

      for (int i = 0; i < num; i++) {
        for (int j = 0; j < numObjs; j++) {
          tbl.setDouble(nis[i].getObjective(j), i, j);
        }
      }
    }

    private class MainView
        extends JPanel {
      MainView() {
        setLayout(new GridLayout(2, 2));
      }
    }

    /**
       Shows all the Graphs in a JTable
     */
    private class ScatterPlotWidget
        extends JPanel
        implements
        java.io.Serializable, MouseListener {

      int ROW_HEIGHT = 130;
      int ROW_WIDTH = 130;
      int NUM_ROW = 2;
      int NUM_COL = 2;

      //ExampleTable et;
      //int[] inputs;
      //int[] outputs;
      //String[] rowheaders;
      //HashMap outputsmap;
      //boolean[][] selected = null;

      Table table;
      GraphTable jTable = null;
      int[] scalarColumns;

      ScatterPlotWidget() {
        scalarColumns = new int[0];
        setup();
      }

      public ScatterPlotWidget(Table table, int[] sc) {
        setTable(table, sc);
        setup();
      }

      void setTable(Table t, int[] sc) {
        scalarColumns = sc;
        table = t;
        jTable.setModel(new ColumnPlotTableModel());
      }

      void setup() {
        // setup the JTable

        // setup the columns for the matrix
        TableColumnModel cm = new DefaultTableColumnModel() {
          boolean first = true;
          public void addColumn(TableColumn tc) {
            if (first) {
              first = false;
              return;
            }
            tc.setMinWidth(ROW_WIDTH);
            super.addColumn(tc);
          }
        };

        // setup the columns for the row header table
        TableColumnModel rowHeaderModel = new DefaultTableColumnModel() {
          boolean first = true;
          public void addColumn(TableColumn tc) {
            if (first) {
              super.addColumn(tc);
              first = false;
            }
          }
        };

        ColumnPlotTableModel tblModel = new ColumnPlotTableModel();

        JComboBox combobox = new JComboBox();

        // setup the row header table
        JTable headerColumn = new JTable(tblModel, rowHeaderModel);
        headerColumn.setRowHeight(ROW_HEIGHT);
        headerColumn.setRowSelectionAllowed(false);
        headerColumn.setColumnSelectionAllowed(false);
        headerColumn.setCellSelectionEnabled(false);
        headerColumn.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        headerColumn.getTableHeader().setReorderingAllowed(false);
        headerColumn.createDefaultColumnsFromModel();
        headerColumn.setDefaultEditor(new String().getClass(),
                                      new DefaultCellEditor(combobox));

        // setup the graph matrix
        jTable = new GraphTable(tblModel, cm);
        jTable.createDefaultColumnsFromModel();
        //jTable.setDefaultRenderer(ImageIcon.class, new GraphRenderer());
        jTable.setDefaultRenderer(JPanel.class, new GraphRenderer2());
        jTable.setRowHeight(ROW_HEIGHT);
        jTable.setRowSelectionAllowed(false);
        jTable.setColumnSelectionAllowed(false);
        jTable.setCellSelectionEnabled(false);
        jTable.addMouseListener(this);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.getTableHeader().setResizingAllowed(false);

        int numRows = jTable.getModel().getRowCount();
        int numColumns = jTable.getModel().getColumnCount();

        int longest = 0;
        // we know that the first column will only contain
        // JLabels...so create them and find the longest
        // preferred width
        JLabel tempLabel = new JLabel();
        for (int i = 0; i < numRows; i++) {
          tempLabel.setText(
              jTable.getModel().getValueAt(i, 0).toString());
          if (tempLabel.getPreferredSize().getWidth() > longest) {
            longest = (int) tempLabel.getPreferredSize().getWidth();
          }
          tempLabel.setText("");
        }

        TableColumn column;
        // set the default column widths
        for (int i = 0; i < numColumns; i++) {
          if (i == 0) {
            column = headerColumn.getColumnModel().getColumn(i);
            column.setPreferredWidth(longest + 4);
          }
          else {
            column = jTable.getColumnModel().getColumn(i - 1);
            column.setPreferredWidth(ROW_WIDTH);
          }
        }

        // make the preferred view show four or less graphs
        int prefWidth;
        int prefHeight;
        if (numColumns < 4) {
          prefWidth = (numColumns - 1) * ROW_WIDTH;
        }
        else {
          prefWidth = (4) * ROW_WIDTH;
        }
        if (numRows < 4) {
          prefHeight = numRows * ROW_HEIGHT;
        }
        else {
          prefHeight = 4 * ROW_HEIGHT;
        }
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
      }

      /**
         When the mouse is pressed, update the selected item.
       */
      public void mousePressed(MouseEvent e) {
      }

      public void mouseClicked(MouseEvent e) {}

      public void mouseReleased(MouseEvent e) {}

      public void mouseEntered(MouseEvent e) {}

      public void mouseExited(MouseEvent e) {}

      /**
         Listen for when the buttons are pressed
       */
      public void actionPerformed(ActionEvent e) {
      }

      /**
         We handle the selection of cells in the JTable on our own.
         Cells that have been selected are marked true in the selected[][]
         matrix.
       */
      class GraphTable
          extends JTable {
        public GraphTable(TableModel tm, TableColumnModel tcm) {
          super(tm, tcm);
        }

        /**
           Same as the superclass' implementation, except this Table
           keeps track of the selected cells on its own.
         */
        public Component prepareRenderer(TableCellRenderer renderer,
                                         int row, int column) {

          Object value = getValueAt(row, column);
          //boolean isSelected = selected[row][column];
          boolean rowIsAnchor =
              (selectionModel.getAnchorSelectionIndex() == row);
          boolean colIsAnchor =
              (columnModel.getSelectionModel().getAnchorSelectionIndex() ==
               column);
          boolean hasFocus = (rowIsAnchor && colIsAnchor) && hasFocus();

          return renderer.getTableCellRendererComponent(this, value,
              false, hasFocus,
              row, column);
        }
      }

      /**
         A custom cell renderer that shows an ImageIcon.  A blue border is
         painted around the selected items.
       */
      class GraphRenderer
          extends JLabel
          implements TableCellRenderer {

        //Border unselectedBorder = null;
        //Border selectedBorder = null;

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
          this.setIcon( (ImageIcon) value);
          return this;
        }
      }

      /**
         A custom cell renderer that shows an ImageIcon.  A blue border is
         painted around the selected items.
       */
      class GraphRenderer2
          extends JPanel
          implements TableCellRenderer {

        //Border unselectedBorder = null;
        //Border selectedBorder = null;

        public GraphRenderer2() {
        }

        /**
           Set the icon and paint the border for this cell.
         */
        public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
          //this.setIcon((ImageIcon) value);
          //return this;
          return (Component) value;
        }
      }

      /**
         The table's data model.  Keeps a matrix of images that are
         displayed in the table.  The images are created from the
         Graphs.
       */
      class ColumnPlotTableModel
          extends AbstractTableModel {
        //ImageIcon[][] images;
        JPanel[][] images;

        ColumnPlotTableModel() {
          //rowheaders = et.getOutputNames();
          //images = new ImageIcon[outputs.length][inputs.length];
          //images = new ImageIcon[2][2];
          images = new JPanel[3][3];
          //selected = new boolean[outputs.length][inputs.length];
          GraphSettings settings = new GraphSettings();
          settings.displayaxislabels = false;
          settings.displaylegend = false;
          settings.displaytitle = false;
          Image img;

          // create each graph and make an image of it.
          // the image is what is shown in the JTable,
          // because it is more efficient than showing the
          // actual graph
          for (int i = 0; i < NUM_ROW; i++) {
            for (int j = 0; j < NUM_COL; j++) {
              /*StringBuffer sb = new StringBuffer(
                 et.getColumnLabel(inputs[j]));
                                 sb.append(" vs ");
                                 sb.append(et.getColumnLabel(outputs[i]));
               */

              if ( (i < scalarColumns.length) && (j < scalarColumns.length)) {
                DataSet[] data = new DataSet[1];
                data[0] = new DataSet("",
                                      Color.red, scalarColumns[j],
                                      scalarColumns[i]);
                settings.xaxis = table.getColumnLabel(scalarColumns[j]);
                settings.yaxis = table.getColumnLabel(scalarColumns[i]);
                settings.xmaximum = new Integer(0);
                settings.xminimum = new Integer( -20);
                settings.yminimum = new Integer( -20);
                settings.ymaximum = new Integer(0);

                Graph graph = createSmallGraph(table, data, settings);
                //img = new BufferedImage(ROW_WIDTH, ROW_HEIGHT,
                //                        BufferedImage.TYPE_INT_ARGB);
                //Graphics imgG = img.getGraphics();
                graph.setSize(new Dimension(ROW_WIDTH, ROW_HEIGHT));
                //graph.paintComponent(imgG);
                //images[i][j] = new ImageIcon(img);
                images[i][j] = graph;
              }
              else {
                JPanel pnl = new JPanel();
                pnl.setBackground(Color.white);
                //img = new BufferedImage(ROW_WIDTH, ROW_HEIGHT,
                //                        BufferedImage.TYPE_INT_ARGB);
                //Graphics imgG = img.getGraphics();
                pnl.setSize(new Dimension(ROW_WIDTH, ROW_HEIGHT));
                //pnl.paint(imgG);
                //images[i][j] = new ImageIcon(img);
                images[i][j] = pnl;
              }
            }
          }
        }

        /**
           There is one more column than there are input features.
           The first column shows the output variables.
         */
        public int getColumnCount() {
          //return inputs.length+1;
          return NUM_COL + 1;
        }

        /**
           There are the same number of rows as output features.
         */
        public int getRowCount() {
          //return outputs.length;
          return NUM_ROW;
        }

        public String getColumnName(int col) {
          if (col == 0) {
            return "";
          }
          else {

            //return et.getColumnLabel(inputs[col-1]);
            return "col";
          }
        }

        public Object getValueAt(int row, int col) {
          if (col == 0) {

            //return rowheaders[row];
            return "";
          }
          else {
            return images[row][col - 1];
          }
        }

        public void setValueAt(Object value, int row, int col) {
          if (col == 0) {
            //rowheaders[row] = (String) value;
            //ScatterPlotWidget.this.setup();
            //ScatterPlotWidget.this.revalidate();
          }
        }

        /**
           This must be overridden so that our custom cell renderer is
           used.
         */
        public Class getColumnClass(int c) {
          return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
          if (col == 0) {
            return true;
          }

          return false;
        }
      }

      /**
         Create a small graph to be shown in the matrix.
         @param vt the table with the data values
         @param d the DataSets to plot
         @param gs the GraphSettings for this plot
       */
      protected Graph createSmallGraph(Table vt, DataSet[] d,
                                       GraphSettings gs) {
        return new ScatterPlotSmall(vt, d, gs);
      }
    }
  }
}
