//package opie;
package ncsa.d2k.modules.core.prediction.markov;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.core.modules.*;
import java.awt.*;


/**
   Template.java
   @author David Clutter
*/
public class MarkovReport extends VisModule  {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Display some basic stats about a MarkovModel.  </body></html>";
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "markovReport";
	}

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.markov.MarkovModel"};
		return types;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "The MarkovModel to display.";
			default: return "No such input";
		}
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		switch(i) {
			case 0:
				return "";
			default: return "NO SUCH INPUT!";
		}
	}

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		switch (i) {
			default: return "No such output";
		}
	}

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
		switch(i) {
			default: return "NO SUCH OUTPUT!";
		}
	}

	public String[] getFieldNameMapping() {
		return null;
	}

	protected UserView createUserView() {
		return new MarkovVis();
	}

	class MarkovVis extends JUserPane implements java.io.Serializable {

		MarkovModel model;
		String order;
		String numExamples;
		String inputFeatures;
		String outputFeatures;

		public void setInput(Object o, int i) {
			model = (MarkovModel)o;
			StringBuffer sb = new StringBuffer("Order: ");
			sb.append(model.getOrder());
			order = sb.toString();
			sb = new StringBuffer("Number of examples: ");
			sb.append(model.getNumExamples());
			numExamples = sb.toString();
			sb = new StringBuffer("Number of input features: ");
			sb.append(model.getNumInputFeatures());
			inputFeatures = sb.toString();
			sb = new StringBuffer("Number of output features: ");
			sb.append(model.getNumOutputFeatures());
			outputFeatures = sb.toString();
		}

		public void initView(ViewModule mod) {
		}

		public Dimension getPreferredSize() {
			return new Dimension(300, 300);
		}

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			int yLoc = 15;
			int height = g.getFontMetrics().getAscent();
			g.drawString(order, 15, yLoc);
			yLoc += height + 5;
			g.drawString(numExamples, 15, yLoc);
			yLoc += height + 5;
			g.drawString(inputFeatures, 15, yLoc);
			yLoc += height + 5;
			g.drawString(outputFeatures, 15, yLoc);
		}
	}
}
