package ncsa.d2k.modules.core.optimize.ga.emo;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.userviews.swing.*;

public class EMOVis
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

  public String getInputName(int i) {
    return "population";
  }

  public String getOutputName(int i) {
    if(i == 0)
      return "Dummy Population Info";
    else if(i == 1)
      return "population";
    return "population";
  }

  public String getInputInfo(int i) {
    return "The current NsgaPopulation";
  }

  public String getOutputInfo(int i) {
    if (i == 0) {
      return "The current NsgaPopulation.";
    }
    else if (i == 1) {
      return "";
    }
    else if (i == 2) {
      return "";
    }
    return "";
  }

  public String getModuleInfo() {
    return
        "Displays the current population and the last 3 cumulative populations " +
        "in a matrix of scatter plots.";
  }

  protected UserView createUserView() {
    return new EMOVisPane();
  }

  public String[] getFieldNameMapping() {
    return null;
  }

  /*  Return an array with information on the properties the user may update.
   *  @return The PropertyDescriptions for properties the user may update.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    return new PropertyDescription[0];
  }

  private boolean firstTime = true;
  public void beginExecution() {
    firstTime = true;
  }

  private int runNumber = 0;
  public void endExecution() {
    runNumber = 0;
  }

  private static final String SMALL_MULT = "Small Multiples of Scatterplots";
  private static final String PAR_COOR = "Parallel Coordinates";

  /**
   * The UserView
   */
  private class EMOVisPane
      extends JUserPane {
    JComboBox viewType;

    /** the current population */
    private NsgaPopulation currentPop;
    /** the last population */
    private NsgaPopulation lastCumulativePop;
    /** the population from two runs ago */
    private NsgaPopulation twoCumulativeAgo;
    /** the cumulative (combined and filtered) population */
    private NsgaPopulation cumulativePop;

    private JButton continueButton;
    private JButton stopButton;

    /** true if the next input is a new population (ie generation 0) */
    private boolean newPop;

    /** the maximum number of generations of the current population */
    private int maxGen = 0;

    /** the current generation of the current population */
    private int currentGen = 0;

    /** keep a reference to the first pop; it is needed to create
     * the first cumulative pop with the first and second populations
     */
    private NsgaPopulation firstPop;

    private PopInfo currentPopInfo;
    private PopInfo cumulativePopInfo;
    private PopInfo lastCumulativePopInfo;
    private PopInfo twoCumulativeAgoInfo;

    abstract class Runner
        implements Runnable {};

    public void initView(ViewModule vm) {
      // the type of view
      viewType = new JComboBox();
      viewType.addItem(SMALL_MULT);
      viewType.addItem(PAR_COOR);

      setLayout(new BorderLayout());

      // add the buttons
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton stop = new JButton("Stop");
      stop.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          viewCancel();
        }
      });
      continueButton = new JButton("Continue");
      continueButton.setEnabled(false);
      continueButton.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          SwingUtilities.invokeLater(new Runner() {
            public void run() {
              // reset the twoCumulativeAgo pop to be the lastCumulativePop
              twoCumulativeAgo = lastCumulativePop;
              // reset the lastCumulativePop to be the cumulativePop
              lastCumulativePop = cumulativePop;

              // if cumulativePop already exists, reset it to be
              // a junction of the currentPop and the cumulativePop
              if (cumulativePop != null) {
                cumulativePop = new NewNsgaPopulation(cumulativePop, currentPop);
                ( (NewNsgaPopulation) cumulativePop).filtering();
              }
              // otherwise, if this is the second population,
              // create a cumulativePop that is the junction of the
              // first and second populations
              else if (firstPop != null && firstPop != currentPop) {
                cumulativePop = new NewNsgaPopulation(firstPop, currentPop);
                ( (NewNsgaPopulation) cumulativePop).filtering();
                firstPop = null;
              }

              if (twoCumulativeAgo != null) {
                //new Thread("twoCumulative") {
                //  public void run() {
                twoCumulativeAgoInfo.setPopulation(twoCumulativeAgo, null);
                twoCumulativeAgoInfo.setRunNumber(runNumber - 2);
                //  }
                //}

                //.start();
              }
              if (lastCumulativePop != null) {
                //new Thread("lastCumulative") {
                //  public void run() {
                lastCumulativePopInfo.setPopulation(lastCumulativePop, null);
                lastCumulativePopInfo.setRunNumber(runNumber - 1);
                //  }
                //}.start();
              }
              if (cumulativePop != null) {
                //new Thread("cumulative") {
                //  public void run() {
                cumulativePopInfo.setPopulation(cumulativePop, null);
                cumulativePopInfo.setRunNumber(runNumber);
                //  }
                //}.start();
              }

          // send this to EMOGeneratePopulation to double the pop size
          // for the next run
          pushOutput(new EMOPopulationInfo(), 0);
          //pushOutput(currentPop, 1);
          continueButton.setEnabled(false);
          // the next input should be a new population
          newPop = true;
            }
          });
        }
      });
      stopButton = new JButton("View Population");
      stopButton.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          pushOutput(currentPop, 1);
        }
      });
      buttonPanel.add(continueButton);
      buttonPanel.add(stop);
      buttonPanel.add(stopButton);
      add(buttonPanel, BorderLayout.SOUTH);

      // current
      currentPopInfo = new CurrentPopInfo();
      currentPopInfo.setPreferredSize(new Dimension(350, 375));
      cumulativePopInfo = new PopInfo();
      cumulativePopInfo.setPreferredSize(new Dimension(350, 375));
      lastCumulativePopInfo = new PopInfo();
      lastCumulativePopInfo.setPreferredSize(new Dimension(350, 375));
      twoCumulativeAgoInfo = new PopInfo();
      twoCumulativeAgoInfo.setPreferredSize(new Dimension(350, 375));

      JPanel pnl = new JPanel();
      pnl.setLayout(new GridLayout(2, 2));
      pnl.add(currentPopInfo);
      pnl.add(cumulativePopInfo);
      pnl.add(lastCumulativePopInfo);
      pnl.add(twoCumulativeAgoInfo);

      JScrollPane jsp = new JScrollPane(pnl);
      jsp.setPreferredSize(new Dimension(800, 375));

      add(jsp, BorderLayout.CENTER);

      cumulativePop = null;
      currentPop = null;
      lastCumulativePop = null;
      twoCumulativeAgo = null;
      newPop = true;
      firstPop = null;
    }

    public void setInput(Object o, int z) {
      NsgaPopulation pop = (NsgaPopulation) o;

      if (firstTime) {
        // get the names of the objectives
        ObjectiveConstraints[] con = (ObjectiveConstraints[]) pop.
            getObjectiveConstraints();

        MutableTableImpl clean = new MutableTableImpl();
        String[] names = new String[con.length];
        for (int i = 0; i < names.length; i++) {
          clean.addColumn(new double[0]);
        }
        for (int i = 0; i < names.length; i++) {
          names[i] = con[i].getName();
          clean.setColumnLabel(names[i], i);
        }

        cumulativePop = null;
        currentPop = null;
        lastCumulativePop = null;
        twoCumulativeAgo = null;
        newPop = true;
        firstPop = null;

        currentPopInfo.scatterPlot.setObjectiveNames(names);
        currentPopInfo.setPopulation(null, clean);
        cumulativePopInfo.scatterPlot.setObjectiveNames(names);
        cumulativePopInfo.setPopulation(null, clean);
        lastCumulativePopInfo.scatterPlot.setObjectiveNames(names);
        lastCumulativePopInfo.setPopulation(null, clean);
        twoCumulativeAgoInfo.scatterPlot.setObjectiveNames(names);
        twoCumulativeAgoInfo.setPopulation(null, clean);
        firstTime = false;
      }

      // the first generation of a new population
      if (newPop) {
        runNumber++;
        // reset the maximum number of generations
        maxGen = pop.getMaxGenerations();
        maxGen--;
        newPop = false;
        currentPop = pop;
      }

      currentGen = currentPop.getCurrentGeneration();

      // now copy the data from the currentPop into the currentPopTable
      // we need to make a copy because the current population will be
      // mutated by the rest of the itinerary, causing the graphing to fail
      int numObjs = currentPop.getNumObjectives();
      NsgaSolution[] nis = (NsgaSolution[]) (currentPop.getMembers());
      int numSolutions = nis.length;

      // we only copy the rank zero members
      //int numRankZero = 0;
      //for(int i = 0; i < nis.length; i++) {
      //  if(nis[i].getRank() == 0)
      //    numRankZero++;
      //}

      // create the table
      final MutableTable currentPopTable = (MutableTable) DefaultTableFactory.
          getInstance().createTable();
      for (int i = 0; i < numObjs; i++) {
        //currentPopTable.addColumn(new float[numRankZero]);
        currentPopTable.addColumn(new float[numSolutions]);
        currentPopTable.setColumnLabel(currentPop.getObjectiveConstraints()[
                                       i].getName(),
                                       currentPopTable.getNumColumns() - 1);
      }
      // fill the table
//      int curRow = 0;

      // for each solution
      for (int i = 0; i < numSolutions; i++) {
        // if it is rank 0
//        if(nis[i].getRank() == 0) {
        // copy each objective into the table
        for (int j = 0; j < numObjs; j++) {
          currentPopTable.setFloat( (float) nis[i].getObjective(j), i, j);
//          curRow++;
//        }
        }
      }

      if (currentPop != null) {
        //new Thread("current") {
        //  public void run() {
            currentPopInfo.setPopulation(currentPop, currentPopTable);
        //  }
        //}.start();
      }

      // if we are at max gens, enable the continue button
      if (currentGen == maxGen) {
        if (firstPop == null && cumulativePop == null) {
          firstPop = currentPop;
        }
        continueButton.setEnabled(true);
      }
      // otherwise, pass the population through
      else {
        pushOutput(currentPop, 2);
      }
    }

    private class PopInfo
        extends JPanel {
      JPanel labels;

      JLabel runNumber;
      JLabel popSize;
      JLabel numSolutions;

      String RUN = "Run: ";
      String POP_SIZE = "Population Size: ";
      String NUM_SOL = "Number of Nondominated Solutions: ";
      String CUMUL = "Cumulative ";

      boolean isCumulative = false;

      ObjectiveModel tableModel;
      ObjectiveMatrix scatterPlot;

      int run;

      PopInfo() {
        runNumber = new JLabel(RUN);
        popSize = new JLabel(POP_SIZE);
        numSolutions = new JLabel(NUM_SOL);

        labels = new JPanel();
        labels.setLayout(new GridLayout(3, 1));
        labels.add(runNumber);
        labels.add(popSize);
        labels.add(numSolutions);

        scatterPlot = new ObjectiveMatrix();
        tableModel = (ObjectiveModel) scatterPlot.jTable.getModel();

        this.setLayout(new BorderLayout());
        labels.setBorder(new EmptyBorder(0, 30, 0, 0));
        add(labels, BorderLayout.NORTH);
        add(scatterPlot, BorderLayout.CENTER);
      }

      void setRunNumber(int r) {
        runNumber.setText(CUMUL + RUN + r);
      }

      void setPopulation(NsgaPopulation p, MutableTable table) {
        if (!isCumulative) {
          runNumber.setText(RUN + run);
        }
        if (p != null) {
          popSize.setText(POP_SIZE + p.getMembers().length);

          NsgaSolution[] nis = (NsgaSolution[]) p.getMembers();
          int numRankZero = 0;
          for (int i = 0; i < nis.length; i++) {
            if (nis[i].getRank() == 0) {
              numRankZero++;
            }
          }
          numSolutions.setText(NUM_SOL + numRankZero);
        }
        else {
          popSize.setText(POP_SIZE);
          numSolutions.setText(NUM_SOL);
        }

        if (table != null) {
          tableModel.setPopulationTable(table);
        }
        else if(p != null){
          tableModel.setPopulation(p);
        }
      }
    }

    private class CurrentPopInfo
        extends PopInfo {

      CurrentPopInfo() {
        runNumber = new JLabel(RUN);
        popSize = new JLabel(POP_SIZE);
        numSolutions = new JLabel(NUM_SOL);

        labels = new JPanel();
        labels.setLayout(new GridLayout(3, 1));
        labels.add(runNumber);
        labels.add(popSize);
        labels.add(numSolutions);

        scatterPlot = new ObjectiveMatrix();
        tableModel = (ObjectiveModel) scatterPlot.jTable.getModel();

        this.setLayout(new BorderLayout());
        labels.setBorder(new EmptyBorder(0, 30, 0, 0));
        add(labels, BorderLayout.NORTH);
        add(scatterPlot, BorderLayout.CENTER);
      }

      void setPopulation(NsgaPopulation p, MutableTable table) {
        if (!isCumulative) {
          runNumber.setText(RUN + EMOVis.this.runNumber);
        }

        if (p != null) {
          popSize.setText(POP_SIZE + p.getMembers().length);

            NsgaSolution[] nis = (NsgaSolution[]) p.getMembers();
            int numRankZero = 0;
            for (int i = 0; i < nis.length; i++) {
              if (nis[i].getRank() == 0) {
                numRankZero++;
              }
            }
            numSolutions.setText(NUM_SOL + numRankZero);
        }
        else {
          popSize.setText(POP_SIZE);
          numSolutions.setText(NUM_SOL);
        }

        if (table != null) {
          tableModel.setPopulationTable(table);
        }
        else if(p != null){
          tableModel.setPopulation(p);
        }
      }
    }

    protected int ROW_HEIGHT = 150;
    protected int ROW_WIDTH = 150;
    protected int NUM_ROW = 2;
    protected int NUM_COL = 2;

    /**
       Shows all the Graphs in a JTable
     */
    private class ObjectiveMatrix
        extends JPanel
        implements java.io.Serializable {

      ObjectiveModel tblModel;
      JTable headerColumn;

      public ObjectiveMatrix() {
        setup();
      }

      void setObjectiveNames(String[] names) {
        tblModel.setObjectiveNames(names);

        JComboBox combo = new JComboBox();
        for (int i = 0; i < names.length; i++) {
          combo.addItem(names[i]);
        }
        //ComboRenderer renderer = new ComboRenderer(names);
        headerColumn.setDefaultEditor(String.class,
                                      new DefaultCellEditor(combo));

        EditableHeaderTableColumn col;
        // column 0
//        col = (EditableHeaderTableColumn) jTable.getColumnModel().getColumn(0);
//        col.setHeaderValue(combo.getItemAt(0));
        //col.setHeaderRenderer(renderer);

        // column 1
//        col = (EditableHeaderTableColumn) jTable.getColumnModel().getColumn(1);
//        col.setHeaderValue(combo.getItemAt(0));
        //col.setHeaderRenderer(renderer);
//        col.setHeaderEditor(new DefaultCellEditor(combo));

        for (int i = 0; i < 2; i++) {
          tblModel.setColumnSelection(i, names[i]);
          col = (EditableHeaderTableColumn) jTable.getColumnModel().getColumn(i);
          col.setHeaderEditor(new DefaultCellEditor(combo));
          col.setHeaderValue(combo.getItemAt(i));
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

        tblModel = new ObjectiveModel();

        JComboBox combobox = new JComboBox();

        // setup the row header table
        headerColumn = new JTable(tblModel, rowHeaderModel);
        headerColumn.setRowHeight(ROW_HEIGHT);
        headerColumn.setRowSelectionAllowed(false);
        headerColumn.setColumnSelectionAllowed(false);
        headerColumn.setCellSelectionEnabled(false);
        headerColumn.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        headerColumn.getTableHeader().setReorderingAllowed(false);
        headerColumn.createDefaultColumnsFromModel();
        headerColumn.setDefaultRenderer(String.class, new RotatedLabelRenderer());
        headerColumn.setDefaultEditor(new String().getClass(),
                                      new DefaultCellEditor(combobox));

        // setup the graph matrix
        jTable = new JTable(tblModel, cm);
        jTable.createDefaultColumnsFromModel();
        //jTable.setDefaultRenderer(ImageIcon.class, new GraphRenderer());
        jTable.setDefaultRenderer(JPanel.class, new ComponentRenderer());
        jTable.setDefaultEditor(JPanel.class, new ComponentEditor());
        jTable.setRowHeight(ROW_HEIGHT);
        jTable.setRowSelectionAllowed(false);
        jTable.setColumnSelectionAllowed(false);
        jTable.setCellSelectionEnabled(false);

        /*jTable.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            int col = jTable.columnAtPoint(e.getPoint());
            int row = jTable.rowAtPoint(e.getPoint());

            TableCellRenderer render = jTable.getCellRenderer(row, col);
            MouseEvent me = SwingUtilities.convertMouseEvent(jTable, e, (Component)render);
            System.out.println(me);
            Component c = render.getTableCellRendererComponent(jTable,
                                                 tblModel.getValueAt(row, col),
                                                 false, false, row, col);
          }
        });*/

        //jTable.addMouseListener(this);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumnModel columnModel = jTable.getColumnModel();
        jTable.setTableHeader(new EditableHeader(columnModel));

        String[] items = {
            "ff", "fg"};
        JComboBox combo = new JComboBox(items);

        EditableHeaderTableColumn col;
        // column 0
        col = (EditableHeaderTableColumn) jTable.getColumnModel().getColumn(0);
        col.setHeaderValue(items[0]);
        //col.setHeaderRenderer(renderer);
        col.setHeaderEditor(new DefaultCellEditor(combo));

        // column 1
        col = (EditableHeaderTableColumn) jTable.getColumnModel().getColumn(1);
        col.setHeaderValue(items[0]);
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
        /*JLabel tempLabel = new JLabel();
                 for (int i = 0; i < numRows; i++) {
          tempLabel.setText(
              jTable.getModel().getValueAt(i, 0).toString());
          if (tempLabel.getPreferredSize().getWidth() > longest) {
            longest = (int) tempLabel.getPreferredSize().getWidth();
          }
          tempLabel.setText("");
                 }*/

        TableColumn column;
        // set the default column widths
        for (int i = 0; i < numColumns; i++) {
          if (i == 0) {
            column = headerColumn.getColumnModel().getColumn(i);
            column.setPreferredWidth(20);
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
            ROW_WIDTH * 2, ROW_HEIGHT * 2));

        // put the row headers in the viewport
        JViewport jv = new JViewport();
        jv.setView(headerColumn);
        jv.setPreferredSize(headerColumn.getPreferredSize());

        // setup the scroll pane
        JScrollPane sp = new JScrollPane(jTable);
        sp.setRowHeader(jv);
        sp.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        this.add(sp, BorderLayout.CENTER);
      }

      protected Table table;
      protected JTable jTable = null;

      /**
       * Renderer for a JComboBox
       */
      class ComboRenderer
          extends JComboBox
          implements TableCellRenderer {

        ComboRenderer(String[] items) {
          for (int i = 0; i < items.length; i++) {
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
      public class EditableHeaderTableColumn
          extends TableColumn {

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

        public void setHeaderValue(Object value) {
          super.setHeaderValue(value);
          ObjectiveMatrix.this.tblModel.setColumnSelection(this.getModelIndex() -
              1, (String) value);
        }

        public void copyValues(TableColumn base) {
          modelIndex = base.getModelIndex();
          identifier = base.getIdentifier();
          width = base.getWidth();
          minWidth = base.getMinWidth();
          setPreferredWidth(base.getPreferredWidth());
          maxWidth = base.getMaxWidth();
          headerRenderer = base.getHeaderRenderer();
          headerValue = base.getHeaderValue();
          cellRenderer = base.getCellRenderer();
          cellEditor = base.getCellEditor();
          isResizable = base.getResizable();
        }

        protected TableCellEditor createDefaultHeaderEditor() {
          return new DefaultCellEditor(new JTextField());
        }
      }

      public class EditableHeaderUI
          extends BasicTableHeaderUI {

        protected MouseInputListener createMouseInputListener() {
          return new MouseInputHandler( (EditableHeader) header);
        }

        public class MouseInputHandler
            extends BasicTableHeaderUI.MouseInputHandler {
          private Component dispatchComponent;
          protected EditableHeader header;

          public MouseInputHandler(EditableHeader header) {
            this.header = header;
          }

          private void setDispatchComponent(MouseEvent e) {
            Component editorComponent = header.getEditorComponent();
            Point p = e.getPoint();
            Point p2 = SwingUtilities.convertPoint(header, p, editorComponent);
            dispatchComponent = SwingUtilities.getDeepestComponentAt(
                editorComponent,
                p2.x, p2.y);
          }

          private boolean repostEvent(MouseEvent e) {
            if (dispatchComponent == null) {
              return false;
            }
            MouseEvent e2 = SwingUtilities.convertMouseEvent(header, e,
                dispatchComponent);
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

      public class EditableHeader
          extends JTableHeader
          implements CellEditorListener {
        public final int HEADER_ROW = -10;
        transient protected int editingColumn;
        transient protected TableCellEditor cellEditor;
        transient protected Component editorComp;

        public EditableHeader(TableColumnModel columnModel) {
          super(columnModel);
          setReorderingAllowed(false);
          cellEditor = null;
          recreateTableColumn(columnModel);
        }

        public void updateUI() {
          setUI(new EditableHeaderUI());
          resizeAndRepaint();
          invalidate();
        }

        protected void recreateTableColumn(TableColumnModel columnModel) {
          int n = columnModel.getColumnCount();
          EditableHeaderTableColumn[] newCols = new EditableHeaderTableColumn[n];
          TableColumn[] oldCols = new TableColumn[n];
          for (int i = 0; i < n; i++) {
            oldCols[i] = columnModel.getColumn(i);
            newCols[i] = new EditableHeaderTableColumn();
            newCols[i].copyValues(oldCols[i]);
          }
          for (int i = 0; i < n; i++) {
            columnModel.removeColumn(oldCols[i]);
          }
          for (int i = 0; i < n; i++) {
            columnModel.addColumn(newCols[i]);
          }
        }

        /*        public boolean editCellAt(int index) {
                  return editCellAt(index);
                }*/

        public boolean editCellAt(int index, EventObject e) {
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
          EditableHeaderTableColumn col = (EditableHeaderTableColumn)
              columnModel.getColumn(columnIndex - 1);
          return col.isHeaderEditable();
        }

        public TableCellEditor getCellEditor(int index) {
          int columnIndex = columnModel.getColumn(index).getModelIndex();
          EditableHeaderTableColumn col = (EditableHeaderTableColumn)
              columnModel.getColumn(columnIndex - 1);
          return col.getHeaderEditor();
        }

        public void setCellEditor(TableCellEditor newEditor) {
          TableCellEditor oldEditor = cellEditor;
          cellEditor = newEditor;

          // firePropertyChange
          if (oldEditor != null && oldEditor instanceof TableCellEditor) {
            ( (TableCellEditor) oldEditor).removeCellEditorListener( (
                CellEditorListener)this);
          }
          if (newEditor != null && newEditor instanceof TableCellEditor) {
            ( (TableCellEditor) newEditor).addCellEditorListener( (
                CellEditorListener)this);
          }
        }

        public Component prepareEditor(TableCellEditor editor, int index) {
          Object value = columnModel.getColumn(index).getHeaderValue();
          boolean isSelected = true;
          int row = HEADER_ROW;
          JTable table = getTable();
          Component comp = editor.getTableCellEditorComponent(table,
              value, isSelected, row, index);
          if (comp instanceof JComponent) {
            ( (JComponent) comp).setNextFocusableComponent(this);
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
            setEditingColumn( -1);
            editorComp = null;

            repaint(cellRect);
          }
        }

        public boolean isEditing() {
          return (cellEditor == null) ? false : true;
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
    }

    /**
       A custom cell renderer that shows an ImageIcon.  A blue border is
       painted around the selected items.
     */
    class ComponentRenderer
        extends JPanel
        implements TableCellRenderer {

      public ComponentRenderer() {
        setDoubleBuffered(false);
      }

      /**
         Set the icon and paint the border for this cell.
       */
      public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus, int row,
          int column) {
        return (Component) value;
      }
    }

    class ComponentEditor
        extends AbstractCellEditor implements TableCellEditor {

      Object val;

      public ComponentEditor() {
      }

      public Object getCellEditorValue() {
        return val;
      }

      /**
         Set the icon and paint the border for this cell.
       */
      public Component getTableCellEditorComponent(JTable table,
          Object value, boolean isSelected, int row,
          int column) {
        val = value;
        return (Component) val;
      }
    }


    class RotatedLabelRenderer extends JComponent implements TableCellRenderer {
      public RotatedLabelRenderer() {
      }

      /**
         Set the icon and paint the border for this cell.
       */
      public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus, int row,
          int column) {
//        JPanel pnl = new JPanel(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        ncsa.gui.RotatedLabel rot = new ncsa.gui.RotatedLabel((String)value);
        box.add(box.createVerticalGlue());
        box.add(rot);
        box.add(box.createVerticalGlue());
        rot.setBackground(Color.white);
//        pnl.add(rot, BorderLayout.CENTER);
//        return pnl;
        return box;
      }
    }

    /**
       The table's data model.  Keeps a matrix of images that are
       displayed in the table.  The images are created from the
       Graphs.
     */
    class ObjectiveModel
        extends AbstractTableModel {

      ObjectiveScatterPlot[][] plots;

      int[] ySelections;
      int[] xSelections;
      String[] objectiveNames;
      HashMap nameToIndexMap;

      ObjectiveModel() {
        plots = new ObjectiveScatterPlot[2][2];
        for (int i = 0; i < 2; i++) {
          for (int j = 0; j < 2; j++) {
            plots[i][j] = new ObjectiveScatterPlot();
          }
        }

        ySelections = new int[2];
        xSelections = new int[2];
        for(int i = 0; i < 2; i++) {
          ySelections[i] = i;
          xSelections[i] = i;
        }
      }

      /**
       * This will change the y objective for all plots in this row.
       * @param index
       * @param value
       */
      void setRowSelection(int index, String value) {
        int val;
        try {
          val = ( (Integer) nameToIndexMap.get(value)).intValue();
        }
        catch (Exception e) {
          return;
        }
        ySelections[index] = val;

        // now, for all the plots in this row, update the objectives
        for (int i = 0; i < 2; i++) {
          //plots[index][i].setObjectives(val, ySelections[i]);
          plots[index][i].setObjectives(xSelections[i], val);
        }
        fireTableDataChanged();
      }

      void setColumnSelection(int index, String value) {
        int val;
        try {
          val = ( (Integer) nameToIndexMap.get(value)).intValue();
        }
        catch (Exception e) {
          return;
        }
        xSelections[index] = val;

        // now, for all the plots in this column, update the objectives
        for (int i = 0; i < 2; i++) {
          //plots[i][index].setObjectives(xSelections[i], val);
          plots[i][index].setObjectives(val, ySelections[i]);
        }
        fireTableDataChanged();
      }

      void setObjectiveNames(String[] names) {
        objectiveNames = names;
        nameToIndexMap = new HashMap(names.length);
        for (int i = 0; i < names.length; i++) {
          nameToIndexMap.put(names[i], new Integer(i));

        }
        /*for (int i = 0; i < 2; i++) {
          setRowSelection(i, objectiveNames[i]);
          setColumnSelection(i, objectiveNames[i]);
        }*/
      }

      void setPopulationTable(MutableTable popTable) {
        for (int i = 0; i < plots.length; i++) {
          for (int j = 0; j < plots[i].length; j++) {
            ( (ObjectiveScatterPlot) plots[i][j]).setTable(popTable);
            //( (ObjectiveScatterPlot) plots[i][j]).setObjectives(xSelections[i],
            //    ySelections[j]);
            ( (ObjectiveScatterPlot) plots[i][j]).setObjectives(xSelections[j],
                ySelections[i]);
          }
        }
        fireTableDataChanged();
      }

      void setPopulation(NsgaPopulation p) {
        for (int i = 0; i < plots.length; i++) {
          for (int j = 0; j < plots[i].length; j++) {
            ( (ObjectiveScatterPlot) plots[i][j]).setPopulation(p);
            //( (ObjectiveScatterPlot) plots[i][j]).setObjectives(xSelections[i],
            //    ySelections[j]);
            ( (ObjectiveScatterPlot) plots[i][j]).setObjectives(xSelections[j],
                ySelections[i]);
          }
        }
        //this.fireTableDataChanged();
        fireTableDataChanged();
      }

      /**
         There is one more column than there are input features.
         The first column shows the output variables.
       */
      public int getColumnCount() {
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
          if (objectiveNames == null) {
            return "";
          }
          else {
            return objectiveNames[ySelections[row]];
          }
        }
        else {
          return plots[row][col - 1];
        }
      }

      public void setValueAt(Object value, int row, int col) {
        if (col == 0) {
          //rowheaders[row] = (String) value;
          //ScatterPlotWidget.this.setup();
          //ScatterPlotWidget.this.revalidate();
          setRowSelection(row, (String) value);
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
        return true;
      }
    } // Objective Model
  } // EMOVisPane
} // EMO2