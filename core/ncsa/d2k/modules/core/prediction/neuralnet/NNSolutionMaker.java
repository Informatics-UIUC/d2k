package ncsa.d2k.modules.core.prediction.neuralnet;


import ncsa.d2k.modules.core.optimize.util.gui.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.userviews.UserInputPane;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.userviews.widgets.swing.*;

/*
	Makes a single, user defined parameter set
	that is represented by a SOSolution object


	@author pgroves 09/28/01
*/

public class NNSolutionMaker extends UIModule
	{


	/* makes the ranges and initial parameter arrays*/

	public NNSolutionMaker(){

		params=NNModelGenerator.getDefaultParameters();

		ranges=NNModelGenerator.getRanges();

		rangeMap=new int[10];
		rangeMap[0]=NNModelGenerator.EPOCHS;
		rangeMap[1]=NNModelGenerator.SEED;
		rangeMap[2]=NNModelGenerator.WEIGHT_INIT_RANGE;
		rangeMap[3]=NNModelGenerator.INITIAL_LEARNING_RATE;
		rangeMap[4]=NNModelGenerator.FINAL_LEARNING_RATE;
		rangeMap[5]=NNModelGenerator.HIDDEN_LAYERS;
		rangeMap[6]=NNModelGenerator.NODES_IN_LAYER_01;
		rangeMap[7]=NNModelGenerator.NODES_IN_LAYER_02;
		rangeMap[8]=NNModelGenerator.NODES_IN_LAYER_03;
		rangeMap[9]=NNModelGenerator.NODES_IN_LAYER_04;
	}




	//////////////////////
	//d2k Props
	////////////////////

	protected boolean displayGui=true;


	/////////////////////////
	/// other fields
	////////////////////////
	/*this modules instance of ClassView, defined below*/
	ClassView theView;

	/*the ranges of all the paramters*/
	protected Range[] ranges;

	/*the current list of the user defined parameters*/
	protected double[] params;

	/*the index map of how the range[] in the
	paramlistpanel corresponds to the main range[] 'ranges'*/
	protected int[] rangeMap;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
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
		return theView;
    }

	public UserView getUserView ( ){

		if(theView==null){
			theView=new ClassView();
			theView.buildGui(this);
		}
		theView.setParameters(params);
		if(!displayGui){
			moduleFinish(params);
		}
		return super.getUserView();

	}

	/*finishes things up, pushes outputs*/
	public void moduleFinish(double [] p){
		params=p;
		SOMixedSolution sol=new SOMixedSolution(ranges);
		sol.setParameters(p);

		pushOutput(sol, 0);

		executionManager.moduleDone(this);
	}


  /**
    */
    public class ClassView extends JUserPane implements ActionListener{

		//buttons at the bottom
		private JButton doneButton;
		private JButton abortButton;
		private JPanel buttonPanel;

		//the parent module
		protected NNSolutionMaker parentModule;


		//the specific nn param selection devices

		ParamListPanel numericParams;

		ComboPanel actCombo;
		ComboPanel alphaCombo;
		ComboPanel updateCombo;



		/**
	   		Initialize the view.  Insert all components into the view.
	   		@param mod The  uimodule that owns us
		*/
		public void initView(ViewModule mod) {
			}

		public void buildGui(ViewModule mod){
			parentModule = (NNSolutionMaker)mod;

			int[] map=parentModule.getRangeMap();

			Range[] panelRanges=new Range[map.length];

			for(int i=0; i<map.length; i++){
				panelRanges[i]=parentModule.getRanges()[map[i]];
			}
			numericParams=new ParamListPanel(panelRanges);

			String[] actStrings={"Elliot","FastSigmoid",
									"FastTanh","Sigmoid",
									"Tanh"};
			String[] alphaStrings={"Linear by Epoch",
									"Linear by Time"};

			String[] updateStrings={"Incremental BP",
									"Batch BP"};

			actCombo=new ComboPanel("Activation Function", actStrings);
			alphaCombo=new ComboPanel("Learning Rate Funtion", alphaStrings);
			updateCombo=new ComboPanel("Update/Learning Method", updateStrings);


			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(numericParams);
			this.add(actCombo);
			this.add(alphaCombo);
			this.add(updateCombo);
			this.add(makeButtonsPanel());
			this.validate();

		}
		/*public Dimension getPreferredSize() {
			return new Dimension(400, 400);
		}*/

		/**
	   		Called whenever inputs arrive to the module.
	   		@param input the Object that is the input
	   		@param idx the index of the input
		*/
		public void setInput(Object obj, int index) {
			//this.removeAll();

		}

		private double[] compileParams(){
			double[] params=new double[13];

			//those in the generic panel
			int [] map=parentModule.getRangeMap();
			double[] panelParams=numericParams.getParameters();

			for(int i=0; i<map.length; i++){
				params[map[i]]=panelParams[i];
			}
			//the combo boxes
			params[NNModelGenerator.UPDATE_FUNCTION]=updateCombo.getIndex();
			params[NNModelGenerator.ACTIVATION_FUNCTION]=actCombo.getIndex();
			params[NNModelGenerator.LEARNING_RATE_FUNCTION]=alphaCombo.getIndex();

			return params;
		}


		private void setParameters(double[] params){
			//those in the generic panel
			int [] map=parentModule.getRangeMap();

			for(int i=0; i<map.length; i++){
				numericParams.setParameter(params[map[i]], i);
			}

			//the combo boxes
			updateCombo.setIndex((int)
						params[NNModelGenerator.UPDATE_FUNCTION]);

			actCombo.setIndex((int)
						params[NNModelGenerator.ACTIVATION_FUNCTION]);

			alphaCombo.setIndex((int)
						params[NNModelGenerator.LEARNING_RATE_FUNCTION]);

		}
		public void actionPerformed(ActionEvent e){

			if(e.getSource()==doneButton){
				double[] ps=compileParams();

				parentModule.moduleFinish(ps);
			}
			if(e.getSource()==abortButton){
				parentModule.viewCancel();
			}

		}

		/* gets a panel that has the abort/done buttons
			on it (and all set up)
		*/

		private JPanel makeButtonsPanel(){
			buttonPanel=new JPanel();
			abortButton=new JButton("Abort");
			abortButton.addActionListener(this);
			buttonPanel.add(abortButton);

			doneButton = new JButton("Done");
			doneButton.addActionListener(this);
			buttonPanel.add(doneButton);
			buttonPanel.setBorder(BorderFactory.createMatteBorder(
							3,0,0,0, Color.black));
			return buttonPanel;

		}

		private class ComboPanel extends JPanel{
			JComboBox box;

			public ComboPanel(String s, String[] els){
				box=new JComboBox(els);
				JTextField lbl=new JTextField(s, 15);
				lbl.setEditable(false);
				lbl.setBorder(BorderFactory.createEmptyBorder());
				this.add(lbl);
				this.add(box);
			}
			public int getIndex(){
				return box.getSelectedIndex();
			}
			public void setIndex(int i){
				box.setSelectedIndex(i);
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
		return "<paragraph>  <head>  </head>  <body>    <p>          </p>  </body></paragraph>";
	}

   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] types = {		};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SOMixedSolution"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "";
			default: return "NO SUCH OUTPUT!";
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public boolean getDisplayGui(){
		return displayGui;
	}
	public void setDisplayGui(boolean b){
		displayGui=b;
	}

	//these are to allow the parameters to be saved by d2k
	public double[] getParameters(){
		return params;
	}
	public void setParameters(double[] d){
		params=d;
	}
	public Range[] getRanges(){
		return ranges;
	}
	/*public void setRanges(Range[] rns){
		ranges=rns;
	}*/

	public int[] getRangeMap(){
		return rangeMap;
	}
	public void setRangeMap(int[] i){
		rangeMap=i;
	}
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







