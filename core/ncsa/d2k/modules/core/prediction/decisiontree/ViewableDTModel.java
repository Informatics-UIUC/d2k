package ncsa.d2k.modules.core.prediction.decisiontree;

public interface ViewableDTModel{

	
	/**
		Get the Viewable root of this decision tree.
		@return the root of the tree
	*/
	public ViewableDTNode getViewableRoot();

	/**
		Get the unique values of the output column.
		@param the unique items of the output column
	*/
	public String[] getUniqueOutputValues();
	
}

