package ncsa.d2k.modules.core.prediction;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author vered goren
 * @version 1.0
 *

 *
 * This is an interface. a model that implements UpdateableModelModule is a
 * model that can be retrained on new data sets via the update(ExampleTable)
 * method.
 *
 * the copy method is needed to create a deep copy of the model. this way
 * other modules can use the older versions of the model without being
 * affected by the update.
 *
 * the init method together with the update method aim that an  UpdateableModelModule
 * will be generated and trained as follows:
 * a model producer module will generate the model and call the init method.
 * then this initialized model is sent to IncrementingModule that calls update method
 * each time a new dataset arrives.
 *
 */

public interface UpdateableModelModule {

  public void update(ExampleTable tbl) throws Exception;
  public void init();
  public UpdateableModelModule copy();
}