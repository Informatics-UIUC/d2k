package ncsa.d2k.modules.core.transform.binning;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;


class TableBinCounts implements BinCounts {

  private Table table;
  double[][] minMaxes;

  private static final int MIN = 0;
  private static final int MAX = 1;

  public TableBinCounts(Table t) {
    table = t;
    minMaxes = new double[table.getNumColumns()][];
    for(int i = 0; i < minMaxes.length; i++) {
      if(table.isColumnNumeric(i)) {
        minMaxes[i] = new double[2];

        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;

        // get the max and min
        for (int j = 0; j < table.getNumRows(); j++) {
          if (table.getDouble(j, i) < min)
            min = table.getDouble(j, i);
          if (table.getDouble(j, i) > max)
            max = table.getDouble(j, i);
        }

        minMaxes[i][MIN] = min;
        minMaxes[i][MAX] = max;
      }
    }
  }

  public int getNumRows() {
    return table.getNumRows();
  }

  public double getMin(int col) {
    return minMaxes[col][MIN];
  }

  public double getMax(int col) {
    return minMaxes[col][MAX];
  }

  public double getTotal(int col) {
    double tot = 0;
    for(int i = 0; i < table.getNumRows(); i++)
      tot += table.getDouble(i, col);
    return tot;
  }

  public int[] getCounts(int col, double[] borders) {
    int[] counts = new int[borders.length+1];

    // some redundancy here
    boolean found;
    for (int i = 0; i < table.getNumRows(); i++) {
      found = false;
      for (int j = 0; j < borders.length; j++) {
        if (table.getDouble(i, col) <= borders[j] && !found) {
          counts[j]++;
          found = true;
          break;
        }
      }
      if (!found)
        counts[borders.length]++;
    }

    return counts;
  }
}

//  QA comments:
// 2-27-03 vered started qa. added module description, exception handling.
// 2-27-03 commit back to core and back to greg - to reveiw bin nominal columns tab.
// 3-6-03 added to module info a note about missing values handling.
// 3-17 -3 anca: changed last bin in addFromWeigth
// 3-24-03 ruth: changed QA comment char so that they won't be seen by JavaDocs
//               changed input type to Table; deleted output Table port; updated
//               output port name; updated descriptions; added getPropertiesDescriptions
//               so no properties a user can't edit are shown.
//