package ncsa.d2k.modules.weka.vis;

import weka.core.Drawable;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.infrastructure.modules.DataPrepModule;

/**
 * Casts a PredictionModelModule to a weka.core.Drawable, if appropriate.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class WEKA_CastModelToDrawable extends DataPrepModule {

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.PredictionModelModule"};
		return in;
	}

	public String getInputInfo(int i) {
		return "A PredictionModelModule that implements the Drawable interface.";
	}

	public String getInputName(int i) {
		return "PredModel";
	}

	public String[] getOutputTypes() {
		String[] out = {"weka.core.Drawable"};
		return out;
	}

	public String getOutputInfo(int i) {
		return "A weka.core.Drawable object.";
	}

	public String getOutputName(int i) {
		return "DrawableObject";
	}

	public String getModuleInfo() {
		String s = "Casts a PredictionModelModule to a weka.core.Drawable.";
		return s;
	}

	public void doit() {
		PredictionModelModule pmm = (PredictionModelModule)pullInput(0);
		if(pmm instanceof weka.core.Drawable)
			pushOutput((Drawable)pmm, 0);
	}
}