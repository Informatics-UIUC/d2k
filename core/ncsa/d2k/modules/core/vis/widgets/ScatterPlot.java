package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.util.datatype.*;

import java.awt.*;

public class ScatterPlot extends Graph {

	public ScatterPlot () {}
	public ScatterPlot(VerticalTable table, DataSet[] sets, GraphSettings settings) {
		super(table, sets, settings);
	}

	public void drawDataSet(Graphics2D g2, DataSet set) {
		SimpleColumn xcolumn = (SimpleColumn) table.getColumn(set.x);
		SimpleColumn ycolumn = (SimpleColumn) table.getColumn(set.y);

		int size = xcolumn.getNumRows();

		for (int index=0; index < size; index++) {
			double xvalue;
			double yvalue;
			if(xcolumn instanceof NumericColumn)
				xvalue = xcolumn.getDouble(index);
			else {
				String v = xcolumn.getString(index);
				xvalue = (double)((Integer)xStringLookup[index].get(v)).intValue();
			}
			if(ycolumn instanceof NumericColumn)
				yvalue = ycolumn.getDouble(index);
			else {
				String v = ycolumn.getString(index);
				yvalue = (double)((Integer)yStringLookup[index].get(v)).intValue();
			}

			drawPoint(g2, set.color, xvalue, yvalue);
		}
	}
}
