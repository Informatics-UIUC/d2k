package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;

public class DataSet implements java.io.Serializable {
   String name;
   public Color color;
   public int x, y, z, g, l;

   public DataSet(String name, Color color, int x, int y) {
      this.name = name;
      this.color = color;
      this.x = x;
      this.y = y;
   }

   // added to support cluster bar chart
   public DataSet(String name, Color color, int x, int y, int z) {
      this.name = name;
      this.color = color;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   // added to support time cluster bar chart - add an item for granularity
   public DataSet(String name, Color color, int x, int y, int z, int g, int l) {
      this.name = name;
      this.color = color;
      this.x = x;
      this.y = y;
      this.z = z;
      this.g = g;  // for granularity
      this.l = l;  // for granularity level
   }

   public String toString() {
      return name;
   }
}
