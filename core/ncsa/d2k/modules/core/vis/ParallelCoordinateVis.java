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
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.gui.*;

/**
 * Plots the data in a Table on parallel axes.
 */
public class ParallelCoordinateVis extends VisModule {

	public String getModuleInfo() {
		return "Plots the data in a Table on parallel axes.";
	}

	public String getInputInfo(int i) {
		return "A table to plot.";
	}

	public String getOutputInfo(int i) {
		return "";
	}

	public String[] getInputTypes() {
		String[] i = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return i;
	}

	public String[] getOutputTypes() {
		return null;
	}

	public UserView createUserView() {
		return new PCView();
	}

	public String[] getFieldNameMapping() {
		return null;
	}


}
