package ncsa.d2k.modules.core.vis.pgraph.nodes;

import edu.umd.cs.piccolo.nodes.*;
import java.awt.*;

class PLabel extends PText {

   private Color color;

   PLabel(Color c) {
      super();
      color = c;
   }

   PLabel(String text, Color c) {
      super(text);
      color = c;
   }

   public Paint getPaint() {
      return color;
   }

   void setColor(Color c) {
      color = c;
   }

}
