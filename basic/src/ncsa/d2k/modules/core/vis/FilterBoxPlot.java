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

import ncsa.d2k.modules.core.transform.StaticMethods;

/**
 * This module creates a box-and-whisker plot of scalar <code>Table</code> data
 * that also allows the user to filter data at the ends of the plot.
 *
 * @todo: how to handle nominal columns? when golf.data was fed to this module
 * as the input table - got an array index out of bound exception...
 */
public class FilterBoxPlot extends HeadlessUIModule {

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
    sb.append("at the ends of the plot. <BR>Any row containing data greater than ");
    sb.append("the maximum red line or  smaller than the minimum red line, will ");
    sb.append("be removed from the table. You may drag the red boundaries with the ");
    sb.append("mouse arrow.");
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
  //as a headless ui modules, this method is already implemented by the supper class.
 /* public PropertyDescription[] getPropertiesDescriptions() {
   PropertyDescription[] pds = new PropertyDescription[0];
  }*/

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

        //headless conversion support
        Set set = map.keySet();

        Object[] arr = set.toArray();




        //setAttributes(arr);
        //boolean[] isChanged = new boolean[arr.length];
        int counter = 0;


        for(int i=0; i<arr.length; i++){
          FilterBoxPlotPane temp = (FilterBoxPlotPane) map.get((String)arr[i]);

          if(temp.getChanged())
            counter++;

        }//for i

        double[] _min = new double[counter];
        double[] _max = new double[counter];
        String[] atts = new String[counter];

        for(int i=0, j=0; i<arr.length; i++){
          FilterBoxPlotPane temp = (FilterBoxPlotPane) map.get( (String) arr[i]);
          if(temp.getChanged()){
             _min[j] = temp.getLower();
             _max[j] = temp.getUpper();
             atts[j] = (String)arr[i];
             j++;
          }//if temp
        }//for i

        setMin(_min);
        setMax(_max);
        setAttributes(atts);

        //headless conversion support

        boolean[] flags = (boolean[]) flist.get(flist.size()-1);

        // MutableTable mtable = (MutableTable) table;
        // mtable.removeRowsByFlag(flags);
        // pushOutput((Table) mtable, 0);




        for (int index = 0; index<flags.length; index++){
          flags[index] = !flags[index];

        }




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
		for (int i = flags.length-1 ; i >= 0 ; i--){
					 if (flags[i]) mt.removeRow(i);
				   }
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


  //headless conversion support

   private String[] attributes; //names of each attribute.

   public void setAttributes(Object[] atts){
   attributes = (String[])atts;
 }
   public Object[] getAttributes(){return attributes;}

   //for each attribute, saving its lower and upper boundaries.
   private double[] max;
   private double[] min;

   public void setMax(Object _max){max = (double[])_max;}
   public Object getMax(){return max;}

   public void setMin(Object _min){min = (double[])_min;}
   public Object getMin(){return min;}


   public void doit () throws Exception {

     Table _table = (Table)pullInput(0);
     boolean[] flags = new boolean[_table.getNumRows()];
     for (int i=0; i<flags.length; i++) flags[i] = false;

     if(attributes == null || attributes.length == 0){


       System.out.println("No attributes were chosed to be filtered. " +
                          "The transformation will be an empty one");
        pushOutput(new BooleanFilterTransformation(flags), 0);
        return;
     }//if attributes

    HashMap scalarMap = StaticMethods.getScalarAttributes(_table);
    if(scalarMap.size() == 0){
      System.out.println("Table " + _table.getLabel() +
                         " has no scalar columns\n" +
                         "The transformaiton will be an empty one.\n");
      pushOutput(new BooleanFilterTransformation(flags), 0);
        return;
    }//if scalar map

    boolean[] relevant = StaticMethods.getRelevant(attributes, scalarMap);

    if(relevant.length == 0){
      System.out.println(
          "None of the chosen attributes is scalar and/or in table "
          + _table.getLabel() + "\nThe transformation will be " +
          "an empty one");
      pushOutput(new BooleanFilterTransformation(flags), 0);
        return;
    }//if relevant

    //debug
    System.out.println("\nmin values:\n");
    for (int i=0; i<min.length; i++)
      System.out.print(min[i] + "\t");
    System.out.println("\n\nmax values:\n");
    for (int i=0; i<max.length; i++)
      System.out.print(max[i] + "\t");
    //end debug

    for(int i=0; i<relevant.length; i++)
      if(relevant[i])
        filter(i, flags, _table);



    pushOutput(new BooleanFilterTransformation(flags), 0);



   }//doit

   /**
    * creates the filter for <codE>table</code> according to min and max arrays.
    * for each value is column no. <code>column</codE> in <codE>table</code>,
    * if it is not in the boundaries of <code>[min[column], max[column]]</code>
    *  then its flag will be marked true.
    *
    * @param column - column number to check its values.
    * @param flags  - array of booleans. flags[i] = true means this row is marked for removal.
    * @param table  - a table to have its values at column no. <code>column</codE>
    *                 to be checked and marked for filteration
    */
   private void filter(int column, boolean[] flags, Table table){

     //debug
     System.out.println("going over values in columns " + column);
     //end debug



     //going over the flags
     for (int i=0; i<flags.length; i++)
       if(!flags[i]) //if this row is not yet marked for removal
         //if the value is not in the boundaries
         if(table.getDouble(i, column) < min[column] || table.getDouble(i, column) > max[column]){
           //mark this row for removal
           flags[i] = true;

                //debug
                     double val = table.getDouble(i, column);
                   System.out.println( val + "\t");

                         //end debug

         }



   }//filter



   //headless conversion support


}//FilterBoxPlot