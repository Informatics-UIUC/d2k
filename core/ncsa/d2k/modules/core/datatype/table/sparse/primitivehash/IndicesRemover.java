package ncsa.d2k.modules.core.datatype.table.sparse.primitivehash;

import gnu.trove.TObjectProcedure;
import gnu.trove.TIntHashSet;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class IndicesRemover implements TObjectProcedure {

  public IndicesRemover() {
  }
  private int[] idx;

   public IndicesRemover(int[] indices) {
    idx = indices;
  }

  public boolean execute(Object object) {
    ((TIntHashSet)object).removeAll(idx);
    return true;
  }

}