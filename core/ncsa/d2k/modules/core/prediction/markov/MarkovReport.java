//package opie;
package ncsa.d2k.modules.core.prediction.markov;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.infrastructure.views.*;

import java.awt.*;


/**
   Template.java
   @author David Clutter
*/
public class MarkovReport extends VisModule implements HasNames, java.io.Serializable {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "Display some basic stats about a MarkovModel.";
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
		String []in = {"ncsa.d2k.modules.core.prediction.markov.MarkovModel"};
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
		return "The MarkovModel to display.";
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
