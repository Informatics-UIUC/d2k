package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import javax.swing.*;

/*
	DecisionTreeVis

	Colors and fonts used
*/
public final class DecisionTreeScheme {

	public static final JFrame frame = new JFrame();

	// Background
	public static final Color backgroundcolor = new Color(219, 217, 203);//new Color(243, 243, 237);

	// Text
	public static final Font textfont = new Font("Sans Serif", Font.PLAIN, 12);
	public static final Color textcolor = new Color(0, 0, 0);

	// Decision tree
	public static final Color treebackgroundcolor = new Color(233, 232, 230);//new Color(252, 252, 252);
	public static final Color treelinelevelcolor = new Color(219, 217, 214);
	public static final Color treecirclestrokecolor = new Color(100, 98, 87);
	public static final Color treecirclebackgroundcolor = new Color(252, 252, 252);
	public static final Color treelinecolor = new Color(100, 98, 87);

	// View nodes
	public static final Color viewbackgroundcolor = new Color(219, 217, 206);
	public static final Color viewtickcolor = new Color(164, 164, 164);
	public static final Color viewtrianglecolor = new Color(76, 76, 76);
	public static final Color viewrollcolor = new Color(197, 195, 184);
	public static final Color viewsearchcolor = new Color(177, 72, 69);

	// Scaled nodes
	public static final Color scaledviewbackgroundcolor = new Color(76, 76, 76);
	public static final Color scaledviewbarcolor = new Color(219, 217, 206);

	// Expanded graphs
	public static final Font expandedfont = new Font("Sans Serif", Font.PLAIN, 18);
	public static final Color expandedfontcolor = new Color(51, 51, 51);

	public static final Color expandedbackgroundcolor = new Color(219, 217, 206);
	public static final Color expandedborderbackgroundcolor = new Color(252, 255, 255);
	public static final Color expandedgraphgridcolor = new Color(0, 0, 0);

	// Navigator
	public static final Color viewercolor = new Color(0, 0, 0);

	// Border
	public static final Color borderbackgroundcolor = new Color(219, 217, 206);
	public static final Color borderupperbevelcolor = new Color(127, 127, 127);
	public static final Color borderlowerbevelcolor = new Color(10, 10, 10);
	public static final Color borderhighlightcolor = new Color(242, 242, 242);
	public static final Color bordershadowcolor = new Color(127, 127, 127);

	// Components
	public static final Font componentbuttonfont = new Font("Sans Serif", Font.PLAIN, 10);
	public static final Font componentlabelfont = new Font("Sans Serif", Font.PLAIN, 12);

	public BarColors getBarColors() {
		return new BarColors();
	}
}

// Circular array that holds bar graph colors
class BarColors {
	private static final Color barcolor0 = new Color(71, 74, 98);//new Color(11, 95, 132);
	private static final Color barcolor1 = new Color(191, 191, 115);//new Color(156, 0, 0);
	private static final Color barcolor2 = new Color(111, 142, 116);//new Color(0, 84, 34);
	private static final Color barcolor3 = new Color(178, 198, 181);
	private static final Color barcolor4 = new Color(153, 185, 216);
	private static final Color barcolor5 = new Color(96, 93, 71);
	private static final Color barcolor6 = new Color(146, 205, 163);
	private static final Color barcolor7 = new Color(203, 84, 84);
	private static final Color barcolor8 = new Color(217, 183, 170);
	private static final Color barcolor9 = new Color(140, 54, 57);
	private static final Color barcolor10 = new Color(203, 136, 76);

	int size;
	int index;
	Color[] colors;

	BarColors() {
		size = 11;
		index = 0;

		colors = new Color[size];
		colors[0] = barcolor0;
		colors[1] = barcolor1;
		colors[2] = barcolor2;
		colors[3] = barcolor3;
		colors[4] = barcolor4;
		colors[5] = barcolor5;
		colors[6] = barcolor6;
		colors[7] = barcolor7;
		colors[8] = barcolor8;
		colors[9] = barcolor9;
		colors[10] = barcolor10;
	}

	Color getNextColor() {
		Color color = colors[index];
		index++;
		if (index == size)
			index = 0;

		return color;
	}
}
