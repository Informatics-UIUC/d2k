package ncsa.d2k.modules.core.io;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.maids.integrated.*;
import ncsa.d2k.modules.core.datatype.table.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author vered goren.
 * @version 1.0
 *
 * this module pulls chunks of data from a data stream.
 * it registers itself as a DataListener and then pushes out chunks of data
 * in the form of a table.

 */

public class PullDataChunk extends InputModule implements DataListener {
  public PullDataChunk() {
  }

  private DataCollector dc = null;

  private boolean hasrun = false;
   public boolean isReady() { return true; }


  /**
   * pushes a chunk of data in the form of a Table.
   * @param table - a new chunk of data ready from the stream.
   */
  public void acceptTable(Table table){

    pushOutput(table, 0);
  }

  public String[] getInputTypes() {
    String[] retVal = {"ncsa.d2k.modules.projects.maids.integrated.DataCollector"};
    return retVal;
  }
  public String[] getOutputTypes() {
    String[] retVal = {"ncsa.d2k.modules.core.datatype.table.Table"};
    return retVal;
  }

  /**
   * registering this module as a data listener.
   * the pushing of the output is done in the listener's method - acceptTable
   * @throws java.lang.Exception
   */
  protected void doit() throws java.lang.Exception {
    if (!hasrun) {
         dc = (DataCollector) pullInput(0);
         dc.addDataListener(this);

      }
      hasrun = true;

  }

   public String getModuleName() {
     return "Pull Data Chunk";
   }

  public String getModuleInfo() {
    return "This module pulls chunks of data from a data stream. " +
 " It registers itself as a DataListener and then pushes out chunks of data " +
 " in the form of a Table. " +
 "Each time a chunk of data is ready, this module will pull it from the <i>Data Collector</I> and push it out.";

  }
  public String getInputInfo(int parm1) {
    return "A DataCollector Object that collects the data from a stream in chunks" ;
  }
  public String getOutputInfo(int parm1) {
return "A Table Object that holds a chunk of data from the stream.";
  }

  public String getInputName(int parm1) {
  return "Data Collector" ;
}
public String getOutputName(int parm1) {
 return "Table";
}




}