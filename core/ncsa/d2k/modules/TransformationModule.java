package ncsa.d2k.modules;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * This module does some kind of transformation on a datatable, and
 * must be able to perform the reverse transformation on it as well. It
 * is made serializable as it will have to be saved in some way with the
 * model, if there is one. In this way the model can transform input data
 * in the original format used by the model builder, and back again.
 */

import java.io.Serializable;

public abstract class TransformationModule extends ComputeModule implements Serializable
{
	/**
	 * reverses the data transformation.
	 * @param input the table to untransform.
	 * @returns the untransformed table.
	 */
	abstract public Table untransform (Table input);

	/**
	 * perform the data transformation.
	 * @param input the table to transform.
	 * @returns the transformed table.
	 */
	abstract public Table transform (Table input);
}
