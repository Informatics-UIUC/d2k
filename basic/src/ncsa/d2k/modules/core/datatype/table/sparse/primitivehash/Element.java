package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;
import ncsa.d2k.modules.core.datatype.table.sparse.ObjectComparator;
/**
 * <p>Title: Element</p>
 * <p>Description: a wrapper for an item in a sparse column.
* this is used in sorting methods.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Vered
 * @version 1.0
 */

public class Element implements Comparable{
  private Object obj;
  private int index;

  private boolean missing;
  public boolean getMissing(){return missing;}
  public void setMissing(boolean bl){missing = bl;}

  private boolean empty;
  public boolean getEmpty(){return empty;}
  public void setEmpty(boolean bl){empty = bl;}


  private boolean exist;
  public boolean getExist(){return exist;}
  public void setExist(boolean bl){exist = bl;}


  private boolean _default;
  public boolean getDefault(){return _default;}
  public void setDefault(boolean bl){_default = bl;}



  public Element(Object _obj, int _index) {
    obj = _obj;
    index = _index;
    missing = false;
    empty = false;
    _default = false;
    exist = true;
  }


  public Element(Object _obj, int _index, boolean _missing, boolean _empty,
                 boolean _def, boolean _exist) {
    obj = _obj;
    index = _index;
    exist = _exist;
    missing = _missing;
    empty =_empty;
    _default = _def;
  }


  public Object getObj(){return obj;}
  public int getIndex(){return index;}

  public String toString(){
    if(missing) return "Missing";
    if(empty) return "Empty";
    if(!exist) return "Does not exist";
    if(_default) return "Default";

    if(obj instanceof char[]) return new String((char[])obj);
    if(obj instanceof byte[]) return new String((byte[])obj);
    return obj.toString();
  }

  public int compareTo(Object obj){
    if(obj instanceof Element) {
      System.out.println("WARNING: Cannot compare object " +
                          "of type Element to object of type " +
                          obj.getClass().getName());
       return -1;
    }

       Element other = (Element) obj;
    //checking for missing values of any kind;

    //if this object is not valid
  if (missing || empty || _default || !exist) {
    //and the other object is not valid too
    if (other.getMissing() || other.getEmpty() || other.getDefault() ||
        !other.getExist()) {
      return 0; //then they are equal
    }
    //if the other object is valid, then this object is "greater" than other. (missing values are sorted to the end)
    else return 1;
  }
  //this object is valid but the other is not valid
  else if (other.getMissing() || other.getEmpty() || other.getDefault() ||
           !other.getExist()) {
    return -1; //then this object is smaller.
  }


 //comparing the class of obj
    Object otherObj = other.getObj();
    String myObjectClass = this.obj.getClass().getName();
    String otherObjectClass = otherObj.getClass().getName();
    if(!myObjectClass.equals(otherObjectClass)){
      System.out.println("WARNING: Cannot compare object " +
                          "of type " + myObjectClass + " to object of type " +
                          otherObjectClass);
       return -1;
    }




    //both objects are valid objects. compare them using ObjectComparator
    return new ObjectComparator().compare(obj, otherObj);
  }


}
