package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;

public class DataSet implements java.io.Serializable {
	String name;
	public Color color;
	public int x, y;

	public DataSet(String name, Color color, int x, int y) {
		this.name = name;
		this.color = color;
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return name;
	}
}
