package ncsa.d2k.modules.core.vis;

import java.awt.*;

public class DecisionTreeScheme {

	// Background
	public static Color backgroundcolor = new Color(243, 243, 237);

	// Text
	public static Font textfont = new Font("Sans Serif", Font.PLAIN, 12);
	public static Color textcolor = new Color(0, 0, 0);

	// Tree
	public static Color treebackgroundcolor = new Color(252, 252, 252);
	public static Color treelinelevelcolor = new Color(219, 217, 214);
	public static Color treecirclestrokecolor = new Color(100, 98, 87);
	public static Color treecirclebackgroundcolor = new Color(252, 252, 252);
	public static Color treelinecolor = new Color(100, 98, 87);

	// View nodes
	public static Color viewbackgroundcolor = new Color(219, 217, 206);
	public static Color viewtickcolor = new Color(164, 164, 164);
	public static Color viewtrianglecolor = new Color(76, 76, 76);
	public static Color viewrollcolor = new Color(197, 195, 184);
	public static Color viewsearchcolor = new Color(177, 72, 69);

	// Scaled view nodes
	public static Color scaledviewbackgroundcolor = new Color(76, 76, 76);
	public static Color scaledviewbarcolor = new Color(219, 217, 206);

	// Expanded graphs
	public static Font expandedfont = new Font("Sans Serif", Font.PLAIN, 18);
	public static Color expandedfontcolor = new Color(51, 51, 51);

	public static Color expandedbackgroundcolor = new Color(219, 217, 206);
	public static Color expandedborderbackgroundcolor = new Color(252, 255, 255);
	public static Color expandedgraphgridcolor = new Color(0, 0, 0);

	// Viewer
	public static Color viewercolor = new Color(0, 0, 0);

	// Border
	public static Color borderbackgroundcolor = new Color(219, 217, 206);
	public static Color borderupperbevelcolor = new Color(127, 127, 127);
	public static Color borderlowerbevelcolor = new Color(10, 10, 10);
	public static Color borderhighlightcolor = new Color(242, 242, 242);
	public static Color bordershadowcolor = new Color(127, 127, 127);

	// Components
	public static Font componentbuttonfont = new Font("Sans Serif", Font.PLAIN, 10);
	public static Font componentlabelfont = new Font("Sans Serif", Font.PLAIN, 12);

	public BarColors getBarColors() {
		return new BarColors();
	}
}

// Wrapped circular array holding bar graph colors
class BarColors {
	private static Color barcolor0 = new Color(11, 95, 132);
	private static Color barcolor1 = new Color(156, 0, 0);
	private static Color barcolor2 = new Color(0, 84, 34);

	int size;
	int index;
	Color[] colors;

	BarColors() {
		size = 3;
		index = 0;

		colors = new Color[size];
		colors[0] = barcolor0;
		colors[1] = barcolor1;
		colors[2] = barcolor2;
	}

	Color getNextColor() {
		Color color = colors[index];
		index++;
		if (index == size)
			index = 0;

		return color;
	}
}
