package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;

/**
 * This module creates a box-and-whisker plot of scalar <code>Table</code> data
 * that also allows the user to filter data at the ends of the plot.
 */
public class FilterBoxPlot extends UIModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

  public String getInputInfo(int i) {
    if (i==0)
      return "A <i>MutableTable</i> with data to be visualized (and optionally filtered).";
    return "NO SUCH INPUT";
  }

  public String getInputName(int i) {
    if (i==0)
      return "Mutable Table";
    return "NO SUCH INPUT";
  }

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.datatype.table.MutableTable"
    };
  }

  public String getModuleInfo() {
    StringBuffer sb = new StringBuffer("<p>Overview: ");
    sb.append("This module creates a box-and-whisker plot of scalar ");
    sb.append("<i>Table</i> data that also allows the user to filter data ");
    sb.append("at the ends of the plot.");
    sb.append("</p><p>Data Handling: ");
    sb.append("This module does not modify its input data directly. ");
    sb.append("Rather, its output is a <i>Transformation</i> that can ");
    sb.append("later be applied to filter the data.");
    sb.append("</p>");
    return sb.toString();
  }

  public String getModuleName() {
    return "Filter Box Plot";
  }

  public String getOutputInfo(int i) {
    if (i==0)
      return "A <i>Transformation</i> to filter the <i>Table</i>.";
    return "NO SUCH OUTPUT";
  }

  public String getOutputName(int i) {
    if (i==0)
      return "Transformation";
    return "NO SUCH OUTPUT";
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.datatype.table.Transformation"
    };
  }

  protected UserView createUserView() {
    return new FilterBoxPlotView();
  }

  protected String[] getFieldNameMapping() {
    return null;
  }

  //QA Anca added this:
  public PropertyDescription[] getPropertiesDescriptions() {
    // so that "windowname" property is invisible
    return new PropertyDescription[0];
  }

////////////////////////////////////////////////////////////////////////////////
// user view                                                                  //
////////////////////////////////////////////////////////////////////////////////

  private class FilterBoxPlotView extends JUserPane implements ActionListener {

    Table table;

    BoxPlotGroup group;

    boolean[] flags;

    ArrayList flist;
    ArrayList slist;

    ArrayList features;
    HashMap map;

    JList list;
    JPanel panel;
    JButton done, abort;

    //JTabbedPane tabbedpane;

    public void initView(ViewModule mod) {}

    public void layoutPanes() {
      removeAll();

      //tabbedpane = new JTabbedPane();

      features = new ArrayList();
      map = new HashMap();

      for (int column = 0; column<table.getNumColumns(); column++) {
        if (table.isColumnScalar(column)) {
          String label = table.getColumnLabel(column);
          features.add(label);

          FilterBoxPlotPane boxplotpane = new FilterBoxPlotPane(flist, slist, group, table, column);
          group.add(boxplotpane);

          map.put(label, boxplotpane);

          //tabbedpane.add(table.getColumnLabel(column), boxplotpane);
        }
      }

      list = new JList(features.toArray());

      panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());

      list.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent event) {
          String label = (String) list.getSelectedValue();
          FilterBoxPlotPane pane = (FilterBoxPlotPane) map.get(label);

          panel.removeAll();
          panel.add(pane);
          panel.revalidate();
          panel.repaint();
        }
      });

      list.setSelectedIndex(0);

      done = new JButton("Done");
      abort = new JButton("Abort");

      done.addActionListener(this);
      abort.addActionListener(this);

      JPanel buttonpanel = new JPanel();
      buttonpanel.add(abort);
      buttonpanel.add(done);

      setLayout(new GridBagLayout());
      Constrain.setConstraints(this, new JScrollPane(list), 0, 0, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST, 1, 1);
      Constrain.setConstraints(this, panel, 1, 0, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(this, buttonpanel, 0, 1, 2, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

      //add(tabbedpane, BorderLayout.CENTER);
      //add(buttonpanel, BorderLayout.SOUTH);
    }

    public void setInput(Object object, int input) {
      if (input==0) {
        table = (Table) object;

        group = new BoxPlotGroup(this);

        flags = new boolean[table.getNumRows()];
        for (int index = 0; index<flags.length; index++) {
          flags[index] = true;
        }

        flist = new ArrayList();
        slist = new ArrayList();

        flist.add(flags);
        slist.add(new Integer(table.getNumRows()));

        layoutPanes();
      }
    }

    public void actionPerformed(ActionEvent event) {
      Object source = event.getSource();

      if (source==done) {
        boolean[] flags = (boolean[]) flist.get(flist.size()-1);

        // MutableTable mtable = (MutableTable) table;
        // mtable.removeRowsByFlag(flags);
        // pushOutput((Table) mtable, 0);

        for (int index = 0; index<flags.length; index++)
          flags[index] = !flags[index];

        pushOutput(new BooleanFilterTransformation(flags), 0);

        viewDone("Done");
      }

      if (source==abort)
        viewAbort();
    }

    public Dimension getPreferredSize() {
      return new Dimension(500, 375);
    }

  }

////////////////////////////////////////////////////////////////////////////////
// output Transformation                                                      //
////////////////////////////////////////////////////////////////////////////////

  private class BooleanFilterTransformation implements Transformation {

    private boolean[] flags;

    BooleanFilterTransformation(boolean[] flags) {
      this.flags = flags;
    }

    public boolean transform(MutableTable mt) {
      try {
        mt.removeRowsByFlag(flags);
      }
      catch (Exception e) {
        return false;
      }
      // 4/7/02 commented out by Loretta...
      // this add gets done by applyTransformation
      // mt.addTransformation(this);
      return true;
    }

  }

}