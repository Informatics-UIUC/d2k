package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Element {
  private Object obj;
  private int index;
  public Element(Object _obj, int _index) {
    obj = _obj;
    index = _index;
  }

  public Object getObj(){return obj;}
  public int getIndex(){return index;}

  public String toString(){
    if(obj instanceof char[]) return new String((char[])obj);
    if(obj instanceof byte[]) return new String((byte[])obj);
    return obj.toString();
  }


}