package ncsa.d2k.modules.core.datatype.table.sparse;

import java.util.Comparator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ObjectComparator implements Comparator {
  public ObjectComparator() {
  }
  private String str1, str2;

  public void getStrings(Object o1, Object o2){
    if (o1 instanceof char[]) {
        str1 = new String( (char[]) o1);
        str2 = new String( (char[]) o2);
      }
    else if(o1 instanceof byte[]){
      str1 = new String( (byte[]) o1);
        str2 = new String( (byte[]) o2);
    }
      else {
        str1 = o1.toString();
        str2 = o2.toString();
      }

  }


  public int compare(Object o1, Object o2) {
          getStrings(o1, o2);

      try{
        float f1 = Float.parseFloat(str1);
        float f2 = Float.parseFloat(str2);
        return (int)(f1 - f2);
      }
      catch(NumberFormatException e){
        return str1.compareTo(str2);
      }


  }

  public boolean equals(Object obj) {
    if(! obj.getClass().getName().equals(getClass().getName())) return false;

    return (compare(this, obj) == 0);
  }

}