package ncsa.d2k.modules.core.vis;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.print.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.gui.*;

/**
 * Plots the data in a Table on parallel axes.
 */
public class ParallelCoordinateVis extends VisModule {

	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Plots the data in a Table on parallel axes.  </body></html>";
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "A table to plot.";
			default: return "No such input";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
			default: return "No such output";
		}
	}

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

	public UserView createUserView() {
		return new PCView();
	}

	public String[] getFieldNameMapping() {
		return null;
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "ParallelCoordinateVis";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			default: return "NO SUCH OUTPUT!";
		}
	}
}

