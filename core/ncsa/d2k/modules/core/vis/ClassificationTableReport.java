package ncsa.d2k.modules.core.vis;

import ncsa.d2k.modules.core.vis.widgets.*;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.UserView;
import ncsa.d2k.controller.userviews.swing.JUserPane;
import ncsa.d2k.util.datatype.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

/**
   @author David Clutter
*/
public class ClassificationTableReport extends VisModule implements HasNames {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "";
    }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "TableViewer";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.util.datatype.ExampleTable"};
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
		return "";
    }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		return "";
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
		return new ClassView();
    }

    /**
       The TableView class.  Uses a JTable and a VerticalTableModel to
       display the contents of a VerticalTable.
    */
    public class ClassView extends JUserPane {

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
		public void setInput(Object input, int ii) {
			//ClassificationTable ct = (ClassificationTable)input;
			ExampleTable pt = (ExampleTable)input;
			int []outputs = pt.getOutputFeatures();
			//int []cls = pt.getPredictionSet();
			int []cls;
			/*if(pt instanceof PredictionTable) {
				preds = ((PredictionTable)pt).getPredictionSet();
			}
			else { */
			cls = new int[outputs.length];
			// assumed that the last outputs.length columns are
			// classification columns
			int j = 0;
			for(int i = pt.getNumColumns()-cls.length;
				i < pt.getNumColumns(); i++) {
					cls[j] = i;
					//System.out.println(preds[j]);
					j++;
			}
			//}

			JTabbedPane jtp = new JTabbedPane();
			for(int i = 0; i < outputs.length; i++) {
				// create a new InfoArea and ConfusionMatrix for each
				// and put it in a JPanel and put the JPanel in
				// the tabbed pane

				//int outCol = outputs[i];
				int clsCol = cls[i];
				int outCol = outputs[i];

				int uid = 0;
				// keep the unique classes and assign them a uid
				HashMap clsNames = new HashMap();
				for(int k = 0; k < pt.getNumRows(); k++) {
					String s = pt.getString(k, clsCol);
					if(!clsNames.containsKey(s)) {
						clsNames.put(s, new Integer(uid));
						uid++;
					}
				}

				uid = 0;
				// keep the unique outputs and assign a uid
				HashMap outNames = new HashMap();
				for(int z = 0; z < pt.getNumRows(); z++) {
					String s = pt.getString(z, outCol);
					if(!outNames.containsKey(s)) {
						outNames.put(s, new Integer(uid));
						uid++;
					}
				}

				String []outs = new String[outNames.size()];
				Iterator it = outNames.keySet().iterator();
				while(it.hasNext()) {
					String s = (String)it.next();
					Integer id = (Integer)outNames.get(s);
					outs[id.intValue()] = s;//(String)it.next();
				}

				String []clss = new String[clsNames.size()];
				it = clsNames.keySet().iterator();
				while(it.hasNext()) {
					String s = (String)it.next();
					Integer id = (Integer)clsNames.get(s);
					clss[id.intValue()] = s;//(String)it.next();
				}


				// create the pie chart

				HashMap classTotal = new HashMap();
				for(int z = 0; z < clss.length; z++)
					classTotal.put(clss[z], new Integer(0));

				// calculate the confusion matrix
				int[][] d = new int[outs.length][clss.length];

				for(int row = 0; row < pt.getNumRows(); row++) {
					String one = pt.getString(row, outCol);
					String two = pt.getString(row, clsCol);

					Integer o1 = (Integer)outNames.get(one);
					Integer o2 = (Integer)clsNames.get(two);

					d[o1.intValue()][o2.intValue()]++;

					Integer q = (Integer)classTotal.get(two);
					int val = q.intValue();
					val++;
					classTotal.put(two, new Integer(val));
				}

				// append data to the JTextArea
				JTextArea jta = new JTextArea();
				jta.setEditable(false);
				StringColumn sc = new StringColumn(clsNames.size());
				IntColumn ic = new IntColumn(clsNames.size());
				jta.append("Classes\n");

				it = clsNames.keySet().iterator();
				int idx = 0;
				while(it.hasNext()) {
					String s = (String)it.next();
					Integer in = (Integer)classTotal.get(s);
					jta.append("  "+s+": "+in.intValue()+"\n");
					sc.setString(s, idx);
					ic.setInt(in.intValue(), idx);
					idx++;
				}
				jta.append("  Total Records: "+pt.getNumRows());

				Column[] col = new Column[2];
				col[0] = sc;
				col[1] = ic;
				VerticalTable tbl = new VerticalTable(col);

				// create the confusion matrix
				ConfusionMatrix cm = new ConfusionMatrix(d,
					outs, clss);

				// add everything to this
				DataSet ds = new DataSet("Accuracy", null, 0, 1);
				GraphSettings gs = new GraphSettings();
				gs.title = "Classes";
				gs.displaytitle = true;
				gs.displaylegend = true;
				PieChart pc = new PieChart(tbl, ds, gs, true);

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
