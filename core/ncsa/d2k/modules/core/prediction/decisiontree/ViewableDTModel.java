package ncsa.d2k.modules.core.prediction.decisiontree;

public interface ViewableDTModel{

	public ViewableDTNode getViewableRoot();

	public String[] getUniqueOutputValues();

	public String[] getUniqueInputValues(int index);

	public String[] getInputs();

	public boolean scalarInput(int index);
}

