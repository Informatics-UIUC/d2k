package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

import gnu.trove.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author vered goren
 * @version 1.0
 *
 * for each key in <code>map</code>, if the its mapped value is greater
 * than <code>value</code>, adjust this value by <codE>delta</code>.
 */

public class ValueAdjuster implements TIntProcedure {
  public ValueAdjuster() {
  }
  TIntIntHashMap map;
  int delta;
  int value;
  public ValueAdjuster(TIntIntHashMap _map, int _delta, int _value) {
    map = _map;
    delta = _delta;
    value = _value;
  }
  public boolean execute(int key) {
    if(map.get(key) > value)
      map.adjustValue(key, delta);

    return true;
  }

}