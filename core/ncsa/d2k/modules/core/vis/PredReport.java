package ncsa.d2k.modules.core.vis;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.util.datatype.*;

import java.awt.*;
import ncsa.d2k.modules.core.prediction.naivebayes.*;

/**
	PredReport.java

*/
public class PredReport extends ncsa.d2k.infrastructure.modules.VisModule
{

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"predictions\">    <Text>Data to predict, with the predictions in the last column. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"mdl\">    <Text>A NaiveBayesModel modules. </Text>  </Info></D2K>";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		/*String [] types =  {
			"ncsa.d2k.util.datatype.VerticalTable",
			"java.util.HashMap",
			"ncsa.d2k.modules.compute.learning.modelgen.naivebayes.NaiveBayesModel"};
		*/
		String []types = {"ncsa.d2k.util.datatype.ExampleTable",
			"ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		return null;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"predReport\">    <Text>This module displays the results of a prediction algorithm.  It displays accuracy calculations and a confusion matrix. </Text>  </Info></D2K>";
	}

	/**
		This method is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
		return new PredReportView();
	}

	/**
		This method returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {
		return null;
	}
}


/**
	PredReportView
	This is the UserView class.
*/
class PredReportView extends ncsa.d2k.controller.userviews.UserPane {
	//VerticalTable predictions;
	ExampleTable predictions;
	//HashMap typesmap;
	ReportPanel reportPanel;
	NaiveBayesModel mdl;
	String[] classNames;
	int[][] confMatrix;
	int allinputs = 0;
	int correct = 0;


	public Dimension getMinimumSize() {
		return new Dimension(300, 300);
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	/**
		This method adds the components to a Panel and then adds the Panel
		to the view.
	*/
	public void initView(ViewModule mod) {
		Panel canvasArea = new Panel();
		canvasArea.setLayout(new BorderLayout());
		add(canvasArea);
		reportPanel = new ReportPanel();
		canvasArea.add(reportPanel,BorderLayout.CENTER);
	}

	class ReportPanel extends Panel {
		public void paint(Graphics g) {
		int total = predictions.getNumRows();
		float percentage;
			g.drawString("Accuracy",15,15);
			g.drawString("Correct",25,30);
			percentage = correct/(float)total*100;
			g.drawString(String.valueOf(correct)+"   "+String.valueOf(percentage)+"%",100,30);
			g.drawString("Wrong",25,45);
			percentage = (total-correct)/(float)total*100;
			g.drawString(String.valueOf(total-correct)+"   "+String.valueOf(percentage)+"%",100,45);
			g.drawString("Total",25,60);
			g.drawString(String.valueOf(total),100,60);

			g.drawString("Confusion Matrix",15,80);
			g.drawString("Prediction",100,100);
			g.drawString("Actual",25,120);

			for(int j=0;j<classNames.length;j++)
				g.drawString(classNames[j],100+j*50,120);
			for(int j=0;j<classNames.length;j++)
				g.drawString(classNames[j],25,140+j*15);


			for(int i=0;i<classNames.length;i++)
				for(int j=0;j<classNames.length;j++)
					g.drawString(String.valueOf(confMatrix[i][j]),100+i*50,140+j*15);
		}
	}

	void calcConfusionMatrix() {

	// first find the column that has the correct classes in it
	String classLabel = null;
	String predLabel = "prediction";
	int classColumn = 0;
	int predColumn = 0;
	/*Iterator it = typesmap.keySet().iterator();
	while(it.hasNext()) {
		String name = (String)it.next();
		String type = (String)typesmap.get(name);
		if(type.equals(ChooseAttributes.CLASS)) {
			classLabel = name;
			break;
		}
	}*/

	int []outFeatures = predictions.getOutputFeatures();
	classColumn = outFeatures[0];

	// find the index of the column with the classes in it
	/*for(int i = 0; i < predictions.getNumColumns(); i++) {
		if(predictions.getColumn(i).getLabel().trim().equals(classLabel.trim()))
			classColumn = i;
	}*/

	// find the index of the column with the predictions in it
	for(int i = 0; i < predictions.getNumColumns(); i++) {
		if(predictions.getColumn(i).getLabel().trim().equals(predLabel.trim()))
			predColumn = i;
	}
	System.out.println("numClasses = "+classNames.length);
	System.out.println("classLabel = "+classLabel);
	System.out.println("classColumn = "+classColumn);
	System.out.println("predColumn = "+predColumn);

	confMatrix = new int[classNames.length][classNames.length];

	// for each row
	for (int row=0; row<predictions.getNumRows(); row++) {
		int actual = 0;
		int predicted = 0;
		for (int i=0; i<classNames.length; i++) {
			if (predictions.getString(row,classColumn).equals(classNames[i])) {
				actual = i;
				break;
			}
		}
		for (int i=0; i<classNames.length; i++) {
			if (predictions.getString(row,predColumn).equals(classNames[i])) {
				predicted = i;
				break;
 			}
		}
		confMatrix[predicted][actual]++;
	}
	correct = 0;
	for(int i=0;i<classNames.length;i++)
		for(int j=0;j<classNames.length;j++)
			if (i==j) correct += confMatrix[i][j];
	}

	/**
		This method is called whenever an input arrives, and is responsible
		for modifying the contents of any gui components that should reflect
		the value of the input.

		@param input this is the object that has been input.
		@param index the index of the input that has been received.
	*/
	public void setInput(Object o, int index) {
	if (index == 0) {
		//predictions = (ncsa.d2k.util.datatype.VerticalTable) o;
		predictions = (ncsa.d2k.util.datatype.ExampleTable) o;
		allinputs++;
	}
	/*else if (index == 1) {
		typesmap = (HashMap) o;
		allinputs++;
	}*/
	else if (index == 1) {
		mdl = (NaiveBayesModel) o;
		classNames = mdl.getClassNames();
		allinputs++;
	}
	if (allinputs == 2)
		calcConfusionMatrix();
	}
}

