package ncsa.d2k.modules.core.optimize.ga.emo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;
import java.util.*;

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

    /** the cumulative graphs */
    ScatterPlotWidget cumulative;
    /** the current graphs */
    ScatterPlotWidget current;
    /** graphs of the last run */
    ScatterPlotWidget lastRun;
    /** graphs from two runs ago */
    ScatterPlotWidget twoRunsAgo;

    /** the current population */
    NsgaPopulation currentPop;
    /** the last population */
    NsgaPopulation lastPop;
    /** the population from two runs ago */
    NsgaPopulation twoPopsAgo;
    /** the cumulative (combined and filtered) population */
    NsgaPopulation cumulativePop;

    /** the table holding the values for the fitness functions for the current population */
    MutableTable currentPopTable;
    /** the table holding the values for the fitness functions for the last population */
    MutableTable lastPopTable;
    /** the table holding the values for the fitness functions for two populations ago */
    MutableTable twoPopsAgoTable;
    /** the table holding the values for the fitness functions for the cumulative population */
    MutableTable cumulativePopTable;

    PopInfo currentPopInfo;
    PopInfo lastPopInfo;
    PopInfo twoPopsAgoInfo;
    PopInfo cumulativePopInfo;

    JButton continueButton;
    JButton stopButton;

    boolean newPop = true;
    int maxGen = 0;
    int currentGen = 0;

    public void initView(ViewModule vm) {
      // the type of view
      viewType = new JComboBox();
      viewType.addItem(SMALL_MULT);
      viewType.addItem(PAR_COOR);

      setLayout(new BorderLayout());

      // add the buttons
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

      cumulative = new BufferedScatterPlotWidget();
      current = new ScatterPlotWidget();
      lastRun = new BufferedScatterPlotWidget();
      twoRunsAgo = new BufferedScatterPlotWidget();

      JPanel pnl = new JPanel();
      pnl.setLayout(new GridLayout(2, 2));

      JPanel currPnl = new JPanel(new BorderLayout());
      currentPopInfo = new PopInfo();
      currPnl.add(currentPopInfo, BorderLayout.NORTH);
      currPnl.add(current, BorderLayout.CENTER);
      pnl.add(currPnl);

      JPanel cumulPnl = new JPanel(new BorderLayout());
      cumulativePopInfo = new PopInfo(true);
      cumulPnl.add(cumulativePopInfo, BorderLayout.NORTH);
      cumulPnl.add(cumulative, BorderLayout.CENTER);
      pnl.add(cumulPnl);

      JPanel lastPnl = new JPanel(new BorderLayout());
      lastPopInfo = new PopInfo();
      lastPnl.add(lastPopInfo, BorderLayout.NORTH);
      lastPnl.add(lastRun, BorderLayout.CENTER);
      pnl.add(lastPnl);

      JPanel twoPnl = new JPanel(new BorderLayout());
      twoPopsAgoInfo = new PopInfo();
      twoPnl.add(twoPopsAgoInfo, BorderLayout.NORTH);
      twoPnl.add(twoRunsAgo, BorderLayout.CENTER);
      pnl.add(twoPnl);

      add(pnl, BorderLayout.CENTER);

      cumulativePop = null;
      currentPop = null;
      lastPop = null;
      twoPopsAgo = null;
    }

    public void setInput(Object o, int z) {
      NsgaPopulation pop = (NsgaPopulation) o;

      // reset the populations
      if (newPop) {
        // update the cumulative population
        if (cumulativePop == null) {
          if (currentPop != null && lastPop != null) {
            cumulativePop = new NewNsgaPopulation(currentPop, lastPop);
            ((NewNsgaPopulation)cumulativePop).filtering();

            cumulativePopTable = new MutableTableImpl();
            NsgaSolution[] nis = (NsgaSolution[]) (currentPop.getMembers());
            int num = nis.length;
            for(int i = 0; i < currentPopTable.getNumColumns(); i++) {
              cumulativePopTable.addColumn(new double[num]);
              cumulativePopTable.setColumnLabel(currentPopTable.getColumnLabel(i),
                                                cumulativePopTable.getNumColumns()-1);
            }
            fillFitnessTable(cumulativePop, cumulativePopTable);
          }
        }
        else {
          cumulativePop = new NewNsgaPopulation(currentPop, cumulativePop);
          ((NewNsgaPopulation)cumulativePop).filtering();
          cumulativePopTable = new MutableTableImpl();
            NsgaSolution[] nis = (NsgaSolution[]) (currentPop.getMembers());
            int num = nis.length;
          for(int i = 0; i < currentPopTable.getNumColumns(); i++) {
            cumulativePopTable.addColumn(new double[num]);
            cumulativePopTable.setColumnLabel(currentPopTable.getColumnLabel(i),
                                              cumulativePopTable.getNumColumns()-1);
          }
          fillFitnessTable(cumulativePop, cumulativePopTable);
        }

        twoPopsAgo = lastPop;
        if(lastPopTable != null)
          twoPopsAgoTable = lastPopTable;

        lastPop = currentPop;
        if(currentPopTable != null)
          lastPopTable = currentPopTable;

        currentPop = pop;

        maxGen = currentPop.getMaxGenerations();
        newPop = false;

        //if(currentPopTable == null) {
        int numObjs = currentPop.getNumObjectives();
        NsgaSolution[] nis = (NsgaSolution[]) (currentPop.getMembers());
        int num = nis.length;
        currentPopTable = (MutableTable) DefaultTableFactory.getInstance().createTable();
        for (int i = 0; i < numObjs; i++) {
          currentPopTable.addColumn(new double[num]);
          currentPopTable.setColumnLabel(currentPop.getObjectiveConstraints()[
                                           i].getName(),
                                           currentPopTable.getNumColumns() - 1);
        }

        int[] sc = {0,1};
        if(lastPop != null) {
          lastPopInfo.setPopulation(lastPop);
          lastRun.setTable(lastPopTable, sc);
        }
        if(twoPopsAgo != null) {
          twoPopsAgoInfo.setPopulation(twoPopsAgo);
          twoRunsAgo.setTable(twoPopsAgoTable, sc);
        }
        if(cumulativePop != null) {
          cumulativePopInfo.setPopulation(cumulativePop);
          cumulative.setTable(cumulativePopTable, sc);
        }
      }

      currentGen = currentPop.getCurrentGeneration();
      if (currentGen == maxGen - 1)
        continueButton.setEnabled(true);

      fillFitnessTable(currentPop, currentPopTable);

      int[] sc = {0,1};

      if(currentPop != null) {
        currentPopInfo.setPopulation(currentPop);
        current.setTable(currentPopTable, sc);
      }

      if (currentGen != maxGen - 1) {
        pushOutput(currentPop, 2);
      }
    }

    private void fillFitnessTable(NsgaPopulation pp, MutableTable tbl) {
      int numObjs = pp.getNumObjectives();
      NsgaSolution[] nis = (NsgaSolution[]) (pp.getMembers());
      int num = nis.length;

      //tbl.setNumRows(num);

      for (int i = 0; i < num; i++) {
        for (int j = 0; j < numObjs; j++) {
          tbl.setDouble(nis[i].getObjective(j), i, j);
        }
      }
    }


    private class PopInfo extends JPanel {
      JLabel runNumber;
      JLabel popSize;
      JLabel numSolutions;

      String RUN = "Run: ";
      String POP_SIZE = "Population Size: ";
      String NUM_SOL = "Number of Solutions: ";
      String CUMUL = "Cumulative";

      boolean isCumulative = false;

      PopInfo() {
        runNumber = new JLabel(RUN);
        popSize = new JLabel(POP_SIZE);
        numSolutions = new JLabel(NUM_SOL);

        setLayout(new GridLayout(3, 1));
        add(runNumber);
        add(popSize);
        add(numSolutions);
      }

      PopInfo(boolean cumulative) {
        isCumulative = true;
        runNumber = new JLabel(CUMUL);
        popSize = new JLabel(POP_SIZE);
        numSolutions = new JLabel(NUM_SOL);

        setLayout(new GridLayout(3, 1));
        add(runNumber);
        add(popSize);
        add(numSolutions);
      }

      void setPopulation(NsgaPopulation p) {
        if(!isCumulative)
          runNumber.setText(RUN);
        popSize.setText(POP_SIZE+p.getMembers().length);
        numSolutions.setText(NUM_SOL);
      }
    }

    /**
       Shows all the Graphs in a JTable
     */
    private class ScatterPlotWidget
        extends JPanel
        implements java.io.Serializable {

      protected int ROW_HEIGHT = 130;
      protected int ROW_WIDTH = 130;
      protected int NUM_ROW = 2;
      protected int NUM_COL = 2;

      protected Table table;
      protected JTable jTable = null;
      protected int[] scalarColumns;

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
        jTable = new JTable(tblModel, cm);
        jTable.createDefaultColumnsFromModel();
        //jTable.setDefaultRenderer(ImageIcon.class, new GraphRenderer());
        jTable.setDefaultRenderer(JPanel.class, new GraphRenderer2());
        jTable.setRowHeight(ROW_HEIGHT);
        jTable.setRowSelectionAllowed(false);
        jTable.setColumnSelectionAllowed(false);
        jTable.setCellSelectionEnabled(false);
        //jTable.addMouseListener(this);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumnModel columnModel = jTable.getColumnModel();
        jTable.setTableHeader(new EditableHeader(columnModel));

        String[] items = {"f1","f2"};
        JComboBox combo = new JComboBox();
        for (int i=0;i<items.length;i++)
          combo.addItem(items[i]);
        ComboRenderer renderer = new ComboRenderer(items);

        EditableHeaderTableColumn col;
        // column 1
        col = (EditableHeaderTableColumn)jTable.getColumnModel().getColumn(0);
        col.setHeaderValue(combo.getItemAt(0));
        //col.setHeaderRenderer(renderer);
        col.setHeaderEditor(new DefaultCellEditor(combo));

        // column 3
        col = (EditableHeaderTableColumn)jTable.getColumnModel().getColumn(1);
        col.setHeaderValue(combo.getItemAt(0));
        //col.setHeaderRenderer(renderer);
        col.setHeaderEditor(new DefaultCellEditor(combo));

        //jTable.getTableHeader().setReorderingAllowed(false);
        //jTable.getTableHeader().setResizingAllowed(false);

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
        /*int prefWidth;
        int prefHeight;
        if (numColumns < 2) {
          prefWidth = (numColumns - 1) * ROW_WIDTH;
        }
        else {
          prefWidth = (2) * ROW_WIDTH;
        }
        if (numRows < 2) {
          prefHeight = numRows * ROW_HEIGHT;
        }
        else {
          prefHeight = 2 * ROW_HEIGHT;
        }*/
        jTable.setPreferredScrollableViewportSize(new Dimension(
            2*ROW_WIDTH, 2*ROW_HEIGHT));

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
         A custom cell renderer that shows an ImageIcon.  A blue border is
         painted around the selected items.
       */
      class GraphRenderer
          extends JLabel
          implements TableCellRenderer {

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
       * Renderer for a JComboBox
       */
      class ComboRenderer extends JComboBox
                         implements TableCellRenderer {

        ComboRenderer(String[] items) {
          for (int i=0;i<items.length;i++) {
            addItem(items[i]);
          }
        }

        public Component getTableCellRendererComponent(
                     JTable table, Object value,
                     boolean isSelected, boolean hasFocus,
                     int row, int column) {
          setSelectedItem(value);
          return this;
        }
      }

      /**
       * A TableColumn with an editable header.
       */
      public class EditableHeaderTableColumn extends TableColumn {

        protected TableCellEditor headerEditor;
        protected boolean isHeaderEditable;

        public EditableHeaderTableColumn() {
          setHeaderEditor(createDefaultHeaderEditor());
          isHeaderEditable = true;
        }

        public void setHeaderEditor(TableCellEditor headerEditor) {
          this.headerEditor = headerEditor;
        }

        public TableCellEditor getHeaderEditor() {
          return headerEditor;
        }

        public void setHeaderEditable(boolean isEditable) {
          isHeaderEditable = isEditable;
        }

        public boolean isHeaderEditable() {
          return isHeaderEditable;
        }

        public void copyValues(TableColumn base) {
          modelIndex     = base.getModelIndex();
          identifier     = base.getIdentifier();
          width          = base.getWidth();
          minWidth       = base.getMinWidth();
          setPreferredWidth(base.getPreferredWidth());
          maxWidth       = base.getMaxWidth();
          headerRenderer = base.getHeaderRenderer();
          headerValue    = base.getHeaderValue();
          cellRenderer   = base.getCellRenderer();
          cellEditor     = base.getCellEditor();
          isResizable    = base.getResizable();
        }

        protected TableCellEditor createDefaultHeaderEditor() {
          return new DefaultCellEditor(new JTextField());
        }
      }

      public class EditableHeaderUI extends BasicTableHeaderUI {

        protected MouseInputListener createMouseInputListener() {
          return new MouseInputHandler((EditableHeader)header);
        }

        public class MouseInputHandler extends BasicTableHeaderUI.MouseInputHandler {
          private Component dispatchComponent;
          protected EditableHeader header;

          public MouseInputHandler(EditableHeader header) {
            this.header = header;
          }

          private void setDispatchComponent(MouseEvent e) {
            Component editorComponent = header.getEditorComponent();
            Point p = e.getPoint();
            Point p2 = SwingUtilities.convertPoint(header, p, editorComponent);
            dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent,
                                                                     p2.x, p2.y);
          }

          private boolean repostEvent(MouseEvent e) {
            if (dispatchComponent == null) {
              return false;
            }
            MouseEvent e2 = SwingUtilities.convertMouseEvent(header, e, dispatchComponent);
            dispatchComponent.dispatchEvent(e2);
            return true;
          }

          public void mousePressed(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) {
              return;
            }
            super.mousePressed(e);

            if (header.getResizingColumn() == null) {
              Point p = e.getPoint();
              TableColumnModel columnModel = header.getColumnModel();
              int index = columnModel.getColumnIndexAtX(p.x);
              if (index != -1) {
                if (header.editCellAt(index, e)) {
                  setDispatchComponent(e);
                  repostEvent(e);
                }
              }
            }
          }

          public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            if (!SwingUtilities.isLeftMouseButton(e)) {
              return;
            }
            repostEvent(e);
            dispatchComponent = null;
          }

        }
      }

      public class EditableHeader extends JTableHeader
                               implements CellEditorListener {
        public final int HEADER_ROW = -10;
        transient protected int editingColumn;
        transient protected TableCellEditor cellEditor;
        transient protected Component       editorComp;

        public EditableHeader(TableColumnModel columnModel) {
          super(columnModel);
          setReorderingAllowed(false);
          cellEditor = null;
          recreateTableColumn(columnModel);
        }

        public void updateUI(){
          setUI(new EditableHeaderUI());
          resizeAndRepaint();
          invalidate();
        }

        protected void recreateTableColumn(TableColumnModel columnModel) {
          int n = columnModel.getColumnCount();
          EditableHeaderTableColumn[] newCols = new EditableHeaderTableColumn[n];
          TableColumn[] oldCols = new TableColumn[n];
          for (int i=0;i<n;i++) {
            oldCols[i] = columnModel.getColumn(i);
            newCols[i] = new EditableHeaderTableColumn();
            newCols[i].copyValues(oldCols[i]);
          }
          for (int i=0;i<n;i++) {
            columnModel.removeColumn(oldCols[i]);
          }
          for (int i=0;i<n;i++) {
            columnModel.addColumn(newCols[i]);
          }
        }

        public boolean editCellAt(int index){
          return editCellAt(index);
        }

        public boolean editCellAt(int index, EventObject e){
          if (cellEditor != null && !cellEditor.stopCellEditing()) {
            return false;
          }
          if (!isCellEditable(index)) {
            return false;
          }
          TableCellEditor editor = getCellEditor(index);

          if (editor != null && editor.isCellEditable(e)) {
            editorComp = prepareEditor(editor, index);
            editorComp.setBounds(getHeaderRect(index));
            add(editorComp);
            editorComp.validate();
            setCellEditor(editor);
            setEditingColumn(index);
            editor.addCellEditorListener(this);

            return true;
          }
          return false;
        }


        public boolean isCellEditable(int index) {
          if (getReorderingAllowed()) {
            return false;
          }
          int columnIndex = columnModel.getColumn(index).getModelIndex();
          EditableHeaderTableColumn col = (EditableHeaderTableColumn)columnModel.getColumn(columnIndex-1);
          return col.isHeaderEditable();
        }

        public TableCellEditor getCellEditor(int index) {
          int columnIndex = columnModel.getColumn(index).getModelIndex();
          EditableHeaderTableColumn col = (EditableHeaderTableColumn)columnModel.getColumn(columnIndex-1);
          return col.getHeaderEditor();
        }

        public void setCellEditor(TableCellEditor newEditor) {
          TableCellEditor oldEditor = cellEditor;
          cellEditor = newEditor;

          // firePropertyChange

          if (oldEditor != null && oldEditor instanceof TableCellEditor) {
            ((TableCellEditor)oldEditor).removeCellEditorListener((CellEditorListener)this);
          }
          if (newEditor != null && newEditor instanceof TableCellEditor) {
            ((TableCellEditor)newEditor).addCellEditorListener((CellEditorListener)this);
          }
        }

        public Component prepareEditor(TableCellEditor editor, int index) {
          Object       value = columnModel.getColumn(index).getHeaderValue();
          boolean isSelected = true;
          int            row = HEADER_ROW;
          JTable       table = getTable();
          Component comp = editor.getTableCellEditorComponent(table,
                             value, isSelected, row, index);
          if (comp instanceof JComponent) {
                  ((JComponent)comp).setNextFocusableComponent(this);
          }
          return comp;
        }

        public TableCellEditor getCellEditor() {
          return cellEditor;
        }

        public Component getEditorComponent() {
          return editorComp;
        }

        public void setEditingColumn(int aColumn) {
          editingColumn = aColumn;
        }

        public int getEditingColumn() {
          return editingColumn;
        }

        public void removeEditor() {
          TableCellEditor editor = getCellEditor();
          if (editor != null) {
            editor.removeCellEditorListener(this);

            requestFocus();
            remove(editorComp);

            int index = getEditingColumn();
            Rectangle cellRect = getHeaderRect(index);

            setCellEditor(null);
            setEditingColumn(-1);
            editorComp = null;

            repaint(cellRect);
          }
        }

        public boolean isEditing() {
          return (cellEditor == null)? false : true;
        }


        //
        // CellEditorListener
        //
        public void editingStopped(ChangeEvent e) {
          TableCellEditor editor = getCellEditor();
          if (editor != null) {
            Object value = editor.getCellEditorValue();
            int index = getEditingColumn();
            columnModel.getColumn(index).setHeaderValue(value);
            removeEditor();
          }
        }

        public void editingCanceled(ChangeEvent e) {
          removeEditor();
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


    private class BufferedScatterPlotWidget extends ScatterPlotWidget {

      void setTable(Table t, int[] sc) {
        scalarColumns = sc;
        table = t;
        jTable.setModel(new BufferedScatterPlotTableModel());
      }

      public BufferedScatterPlotWidget() {
        super();
      }

      public BufferedScatterPlotWidget(Table table, int[] sc) {
        setTable(table, sc);
        setup();
      }


      /**
         The table's data model.  Keeps a matrix of images that are
         displayed in the table.  The images are created from the
         Graphs.
       */
      class BufferedScatterPlotTableModel
          extends AbstractTableModel {
        ImageIcon[][] images;
        //JPanel[][] images;

        BufferedScatterPlotTableModel() {
          //rowheaders = et.getOutputNames();
          //images = new ImageIcon[outputs.length][inputs.length];
          images = new ImageIcon[2][2];
          //images = new JPanel[3][3];
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
                img = new BufferedImage(ROW_WIDTH, ROW_HEIGHT,
                                        BufferedImage.TYPE_INT_ARGB);
                Graphics imgG = img.getGraphics();
                graph.setSize(new Dimension(ROW_WIDTH, ROW_HEIGHT));
                graph.paintComponent(imgG);
                images[i][j] = new ImageIcon(img);
                //images[i][j] = graph;
              }
              else {
                JPanel pnl = new JPanel();
                pnl.setBackground(Color.white);
                img = new BufferedImage(ROW_WIDTH, ROW_HEIGHT,
                                        BufferedImage.TYPE_INT_ARGB);
                Graphics imgG = img.getGraphics();
                //pnl.setSize(new Dimension(ROW_WIDTH, ROW_HEIGHT));
                pnl.paint(imgG);
                images[i][j] = new ImageIcon(img);
                //images[i][j] = pnl;
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

        BufferedScatterPlotTableModel tblModel = new BufferedScatterPlotTableModel();

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
        jTable = new JTable(tblModel, cm);
        jTable.createDefaultColumnsFromModel();
        //jTable.setDefaultRenderer(ImageIcon.class, new GraphRenderer());
        jTable.setDefaultRenderer(JPanel.class, new GraphRenderer2());
        jTable.setRowHeight(ROW_HEIGHT);
        jTable.setRowSelectionAllowed(false);
        jTable.setColumnSelectionAllowed(false);
        jTable.setCellSelectionEnabled(false);
        //jTable.addMouseListener(this);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumnModel columnModel = jTable.getColumnModel();
        jTable.setTableHeader(new EditableHeader(columnModel));

        String[] items = {"Dog","Cat"};
        JComboBox combo = new JComboBox();
        for (int i=0;i<items.length;i++)
          combo.addItem(items[i]);
        ComboRenderer renderer = new ComboRenderer(items);

        EditableHeaderTableColumn col;
        // column 1
        col = (EditableHeaderTableColumn)jTable.getColumnModel().getColumn(0);
        col.setHeaderValue(combo.getItemAt(0));
        //col.setHeaderRenderer(renderer);
        col.setHeaderEditor(new DefaultCellEditor(combo));

        // column 3
        col = (EditableHeaderTableColumn)jTable.getColumnModel().getColumn(1);
        col.setHeaderValue(combo.getItemAt(0));
        //col.setHeaderRenderer(renderer);
        col.setHeaderEditor(new DefaultCellEditor(combo));

        //jTable.getTableHeader().setReorderingAllowed(false);
        //jTable.getTableHeader().setResizingAllowed(false);

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
        /*int prefWidth;
        int prefHeight;
        if (numColumns < 2) {
          prefWidth = (numColumns - 1) * ROW_WIDTH;
        }
        else {
          prefWidth = (2) * ROW_WIDTH;
        }
        if (numRows < 2) {
          prefHeight = numRows * ROW_HEIGHT;
        }
        else {
          prefHeight = 2 * ROW_HEIGHT;
        }*/
        jTable.setPreferredScrollableViewportSize(new Dimension(
            2*ROW_WIDTH, 2*ROW_HEIGHT));

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

    }
  }
}
