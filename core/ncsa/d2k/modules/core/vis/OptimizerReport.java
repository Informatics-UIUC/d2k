package ncsa.d2k.modules.core.vis;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.swing.JUserPane;
import ncsa.d2k.modules.core.vis.widgets.VerticalTableMatrix;
import ncsa.d2k.util.datatype.VerticalTable;
import ncsa.gui.JEasyConstrainsPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import ncsa.d2k.modules.core.optimize.util.*;
/*
	A simple vis for saving the information of a solution space.
	contains a tabbed pane with the best/worst and constraint info
	and another that has a table showing all the solutions in the
	space

	@author pgroves
	*/

public class OptimizerReport extends VisModule
	implements HasNames{

	//////////////////////
	//d2k Props
	////////////////////


	/////////////////////////
	/// other fields
	////////////////////////


	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}
	public void endExecution(){
		return;
	}
	public void beginExecution(){
		return;
	}

	//////////////////////////
	//special ui module methods
	//////////////////////////
    /**

    */
    public String[] getFieldNameMapping() {
		return null;
    }

	/**
       @return The UserView part of this module.
    */
    public UserView createUserView() {
		return new ClassView();
    }
  /**
    */
    public class ClassView extends JUserPane
		implements java.io.Serializable, ActionListener{

		VerticalTable vt;
		VerticalTableMatrix vtm;

		ButtonGroup radios;
		JRadioButton ascendButton;
		JRadioButton descendButton;
		JComboBox columnSelectList;

		/**
	   		Initialize the view.  Insert all components into the view.
	   		@param mod The  uimodule that owns us
		*/
		public void initView(ViewModule mod) {
		}

		public Dimension getPreferredSize() {
			return new Dimension(500, 600);
		}

		/**
	   		Called whenever inputs arrive to the module.
	   		@param input the Object that is the input
	   		@param idx the index of the input
		*/
		public void setInput(Object obj, int index) {
			SolutionSpace ss=(SolutionSpace)obj;
			buildSolutionSpaceGui(ss);
		}

		/*
			makes the actual gui components
		*/
		public void buildSolutionSpaceGui(SolutionSpace ss){
			JTabbedPane tabPane=new JTabbedPane();
			this.add(tabPane);

			ss.computeStatistics();
			String info=ss.getSpaceDefinitionString();
			info+=ss.statusString();

			JTextArea spaceInfoArea=new JTextArea(info);
			JScrollPane infoScrollPane=new JScrollPane(spaceInfoArea);
			tabPane.add("Report", infoScrollPane);

			//the second tab of the gui
			JEasyConstrainsPanel solutionTablePanel=new JEasyConstrainsPanel();
			//solutionTablePanel.setLayout(new GridBagLayout());
			solutionTablePanel.setLayout(new BorderLayout());

			//the vt part
			vt=(VerticalTable)ss.getTable();
			/*
			if(ss instanceof SOSolutionSpace){
				int[] newOrder=((SOSolutionSpace)ss).sortSolutions();
				vt=(VerticalTable)vt.reOrderRows(newOrder);
			}
			*/
			vtm=new VerticalTableMatrix(vt);
			JTable vtTable=vtm.getJTable();
			if(vt.getNumColumns()<6)
				vtTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			Dimension dim=vtTable.getPreferredScrollableViewportSize();
			dim.setSize(dim.getWidth(), 400);
			//vtTable.setPreferredScrollableViewportSize(dim);

			JPanel vtmHolder=new ResizePanel();
			vtmHolder.setLayout(new BorderLayout());

			//vtmHolder.setBackground(Color.blue);
			//vtmHolder.setPreferredSize(dim);
			Insets ii = new Insets(0, 0, 0, 0);
			//vtmHolder.setPreferredSize(new Dimension(450, 420));
			vtmHolder.add(vtm, BorderLayout.CENTER);

			/*
			JPanel spacer1=new JPanel();
			JPanel spacer2=new JPanel();
			JPanel spacer3=new JPanel();
			JPanel spacer4=new JPanel();
			vtmHolder.add(spacer1, BorderLayout.NORTH);
			vtmHolder.add(spacer2, BorderLayout.SOUTH);
			vtmHolder.add(spacer3, BorderLayout.EAST);
			vtmHolder.add(spacer4, BorderLayout.WEST);
			*/

		//	vtmHolder.setPreferredSize(new Dimension(450, 420));
			//solutionTablePanel.add(vtm);
			/*solutionTablePanel.setConstraints(vtmHolder, 0,0,3,3,
											GridBagConstraints.BOTH,
											GridBagConstraints.CENTER,
											3.0,12.0, ii);
			*/
			////vtTable.setPreferredScrollableViewportSize(dim);

			//the sort part
			JEasyConstrainsPanel paramHolder = new JEasyConstrainsPanel();
			//paramHolder.setBackground(Color.red);
			paramHolder.setLayout(new GridBagLayout());

			//the sort button
			/*JButton sortButton=new JButton("Sort");
			sortButton.addActionListener(this);
			solutionTablePanel.setConstraints(sortButton, 0,1,1,2,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,2.0);
			//ascend/descend buttons
			radios=new ButtonGroup();
			ascendButton=new JRadioButton("Ascending");
			descendButton=new JRadioButton("Descending");
			radios.add(ascendButton);
			radios.add(descendButton);
			solutionTablePanel.setConstraints(ascendButton, 1,1,1,1,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,1.0);
			solutionTablePanel.setConstraints(descendButton, 1,2,1,1,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,1.0);
			JButton sortButton=new JButton("Sort");
			sortButton.addActionListener(this);
			solutionTablePanel.setConstraints(sortButton, 0,1,1,2,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,2.0);
			*/
			//ascend/descend buttons
			radios=new ButtonGroup();
			ascendButton=new JRadioButton("Ascending");
			descendButton=new JRadioButton("Descending");
			radios.add(ascendButton);
			radios.add(descendButton);
			paramHolder.setConstraints(ascendButton, 1,0,1,1,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,1.0, ii);
			paramHolder.setConstraints(descendButton, 1,1,1,1,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,1.0, ii);
			JButton sortButton=new JButton("Sort");
			sortButton.addActionListener(this);
			paramHolder.setConstraints(sortButton, 0,0,1,2,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,2.0, ii);

			//sort by column select
			Object[] colNames=new Object[vt.getNumColumns()];
			for(int i=0; i<colNames.length; i++){
				colNames[i]=vt.getColumnLabel(i);
			}
			columnSelectList=new JComboBox(colNames);

			/*solutionTablePanel.setConstraints(columnSelectList, 2,1,1,2,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,2.0);
			*/
			paramHolder.setConstraints(columnSelectList, 2,0,1,2,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,2.0, ii);
			//((GridBagLayout)(solutionTablePanel.getLayout())).invalidateLayout(solutionTablePanel);
			/*solutionTablePanel.setConstraints(paramHolder, 0, 4, 1, 1,
											GridBagConstraints.BOTH,
											GridBagConstraints.CENTER,
											1.0,1.0, ii);
			*/
			solutionTablePanel.add(vtmHolder, BorderLayout.CENTER);
			solutionTablePanel.add(paramHolder, BorderLayout.SOUTH);
			//((GridBagLayout)(solutionTablePanel.getLayout())).invalidateLayout(solutionTablePanel);
			tabPane.add("Solutions", solutionTablePanel);


		}

		class ResizePanel extends JEasyConstrainsPanel {
			public void setBounds(int x, int y, int w, int h) {
				Dimension d = vtm.getJTable().getPreferredScrollableViewportSize();
				vtm.getJTable().setPreferredScrollableViewportSize(
					new Dimension((int)d.getWidth(), h));
				super.setBounds(x, y, w, h);
			}
		}

		/*will only be called by the sort button*/
		public void actionPerformed(ActionEvent e){
			//System.out.println("action");
			//get ascend/descend
			boolean ascend=false;
			if(ascendButton.isSelected()){
				ascend=true;
			}
			//System.out.println(ascend);
			//get the column to sort by
			int colIndex=0;
			colIndex=columnSelectList.getSelectedIndex();
			//sort
			try{
				vt.sortByColumn(colIndex);
			}catch(Exception ne){
				ne.printStackTrace();
			}
			//reverse the order if necessary
			if(!ascend)
				reverseOrder();

			//vt.print();

			validate();
			repaint();

		}

		public void reverseOrder(){
			int swapCount=(int)vt.getNumRows()/2;
			int numRows=vt.getNumRows();
			for(int i=0; i<swapCount; i++){
				vt.swapRows(i, numRows-i-1);
			}
		}
	}

	/////////////////////
	//work methods
	////////////////////



	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Prepares a brief report based on information in the (evaluated)"+
				"solution space";
	}

   	public String getModuleName() {
		return "Optimizer Report Visualization";
	}
	public String[] getInputTypes(){
		String[] s= {"ncsa.d2k.modules.core.optimize.util.SolutionSpace"};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "The Solution Space object that has been optimized,"+
					" or at least evaluated";
			}
			default:{
				return "No such input.";
			}
		}
	}

	public String getInputName(int index) {
		switch (index){
			case(0): {
				return "Space";
			}
			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={};
		return s;
	}

	public String getOutputInfo(int index){
		switch (index){
			default:{
				return "No such output.";
			}
		}
	}
	public String getOutputName(int index) {
		switch (index){
			case(0): {
				return "";
			}
			default:{
				return "No such output.";
			}
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////

	/*
	public boolean get(){
		return ;
	}
	public void set(boolean b){
		=b;
	}
	public double  get(){
		return ;
	}
	public void set(double d){
		=d;
	}
	public int get(){
		return ;
	}
	public void set(int i){
		=i;
	}
	*/
}







