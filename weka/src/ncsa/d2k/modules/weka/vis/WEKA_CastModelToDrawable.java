package ncsa.d2k.modules.weka.vis;


import weka.core.Drawable;
import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.core.modules.DataPrepModule;

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
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "A PredictionModelModule that implements the Drawable interface.";
			default: return "No such input";
		}
	}

	public String getInputName(int i) {
		return "PredModel";
	}

	public String[] getOutputTypes() {
		String[] types = {"weka.core.Drawable"};
		return types;
	}

	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "A weka.core.Drawable object.";
			default: return "No such output";
		}
	}

	public String getOutputName(int i) {
		return "DrawableObject";
	}

	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Casts a PredictionModelModule to a weka.core.Drawable.  </body></html>";
	}

	public void doit() {
		PredictionModelModule pmm = (PredictionModelModule)pullInput(0);
		if(pmm instanceof weka.core.Drawable)
			pushOutput((Drawable)pmm, 0);
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "WEKA_CastModelToDrawable";
	}

}
