package ncsa.d2k.modules.projects.dtcheng.examples;

import ncsa.d2k.modules.core.datatype.table.ExampleTable;

/**
 */
public interface TchengExampleTable extends ExampleTable {

  public void deleteInputs(boolean[] deleteFeatures);

  public void setNumGroups(int numGroups);
  public int getNumGroups();
  
  
  public void setGroup(int e, int groupIndex);
  public int getGroup(int e);

  
  public void setInput(int e, int v, double value);
  public void setOutput(int e, int v, double value);
  
}
