package ncsa.d2k.modules.core.datatype.parameter.basic;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.*;

public class ParameterPointImpl extends FloatExample implements Example, java.io.Serializable {

  Table [] subspaceTables;
  int numSubspaces;
  int [] subspaceSizes;

  public ParameterPointImpl () {
  }

  public ParameterPointImpl (Table table) {

    this.subspaceTables = new Table[1];
    this.subspaceTables[0] = table;
    int [] subspaceSizes = new int[1];
    this.subspaceSizes[0] = table.getNumColumns();

  }
}