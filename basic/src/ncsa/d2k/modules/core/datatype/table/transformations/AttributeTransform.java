package ncsa.d2k.modules.core.datatype.table.transformations;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.transform.attribute.*;

/**
 * <code>AttributeTransform</code> is a <code>Transformation</code> which uses
 * <code>ColumnExpression</code>s to construct new attributes from existing
 * attributes in a <code>Table</code>.
 */
public class AttributeTransform implements Transformation {

   private Object[] constructions;

   public AttributeTransform(Object[] constructions) {
      this.constructions = constructions;
   }

   public boolean transform(MutableTable table) {

      if (constructions == null || constructions.length == 0)
         return true;

      for (int i = 0; i < constructions.length; i++) {

         ColumnExpression exp = new ColumnExpression(table);
         Construction current = (Construction)constructions[i];

         Object evaluation = null;
         try {
            exp.setExpression(current.expression);
            evaluation = exp.evaluate();
         }
         catch(Exception e) {
            e.printStackTrace();
            return false;
         }

         switch (exp.evaluateType()) {

            case ColumnExpression.TYPE_BOOLEAN:
               boolean[] b = (boolean[])evaluation;
               table.addColumn(b);
               break;
            case ColumnExpression.TYPE_BYTE:
               byte[] bb = (byte[])evaluation;
               table.addColumn(bb);
               break;
            case ColumnExpression.TYPE_DOUBLE:
               double[] d = (double[])evaluation;
               table.addColumn(d);
               break;
            case ColumnExpression.TYPE_FLOAT:
               float[] f = (float[])evaluation;
               table.addColumn(f);
               break;
            case ColumnExpression.TYPE_INTEGER:
               int[] I = (int[])evaluation;
               table.addColumn(I);
               break;
            case ColumnExpression.TYPE_LONG:
               long[] l = (long[])evaluation;
               table.addColumn(l);
               break;
            case ColumnExpression.TYPE_SHORT:
               short[] s = (short[])evaluation;
               table.addColumn(s);
               break;
             case ColumnExpression.TYPE_STRING:
               String[] p = (String[]) evaluation;
               table.addColumn(p);
            break;

            default:
               return false;

         }

         table.setColumnLabel(current.label, table.getNumColumns() - 1);

      }

      return true;

   }



}
