package ncsa.d2k.modules.core.optimize.ga.emo;

public abstract class Property implements java.io.Serializable {
  public static final int INT = 0;
  public static final int DOUBLE = 1;
  public static final int STRING = 2;
  //public static final int BOOLEAN = 3;

  private int type;
  private String name;
  private String description;
  private Object value;

  protected Property(int typ, String nme, String desc, Object defVal) {
    type = typ;
    name = nme;
    description = desc;
    setValue(defVal);
  }

  public final int getType() {
    return type;
  }

  public final String getName() {
    return name;
  }

  public final String getDescription() {
    return description;
  }

  public Object getValue() {
    return value;
  }
  public void setValue(Object val) {
    value = val;
  }
}