package ncsa.d2k.modules.core.datatype.table.transformations;

import ncsa.d2k.modules.core.datatype.table.*;

/***WeightedSumTransform.
*
*This transforms the inputs of an example table to a set of features
*that are computed as the weighted sum of the inputs for each example.
*The weights are given in a second table, where the number of columns
*is equal to the number of inputs in the first table, and the number
*of rows is the number of new features to create. This process is 
*abstracted slightly, however, so that any set of columns can be replaced
*by a set of columns based on a weighted summation of the original set.
**/


public class WeightedSumTransform implements Transformation {

	/**which of the original columns to replace with new features*/
	private int[] origColumns;

	/**A table that holds the weights/coefficients.*/
	private Table weights;

	private String newFeatureLabelPrefix="SumFeature-";


/**Constructor.

	@param columnIndices The indices of the columns in the mutable table
								to replace with new features. The indices
								should be of all numeric columns.
	@param coefficients 	The weights for the weighted sum. The number of
								columns in the table must be equal to the size
								of columnIndices.
*/
	public WeightedSumTransform(int[] columnIndices, Table coefficients){
		origColumns=columnIndices;
		weights=coefficients;
	}

/**transform.

	This will take a table and replace the first M columns of the columnIndices 
	with new features and delete the other columns specified by columnIndices.
	M is the number of rows in the input coefficients Table. This will cause
	a loss in precision if IntColumns are part of the set of columns to
	transform, but is unavoidable if we want to be able to support
	test/train tables.
	
	@param table 	The table which will have the specified columns replaced
						by weighted summations of those columns.
	
*/	 
	public boolean transform(MutableTable table) {
		 try{
		 
		 int numNewFeatures=weights.getNumRows();
		 int numOldFeatures=origColumns.length;
		 int numExamples=table.getNumRows();
		 
		 double[][] newFeatures=new double[numNewFeatures][numExamples];
		 

		 //calculate the new features
		 int r,comp,i;
		 double sum;
		 for(r=0; r<numExamples; r++){
			for(comp=0; comp<numNewFeatures; comp++){
				
				sum=0.0;
				
				for(i=0; i<numOldFeatures;i++){
					newFeatures[comp][r]+=(table.getDouble(r,origColumns[i]))*
								(weights.getDouble(comp, i));
				}
			}
		}

		//set the first numNewFeatures columns to be the newFeatures
		for(i=0;i<numNewFeatures;i++){
			 table.setColumn(newFeatures[i], origColumns[i]);
			 table.setColumnLabel(newFeatureLabelPrefix+i, origColumns[i]);
		}
		//delete any remaining old features in origColumns
		int numRemaining=numOldFeatures-numNewFeatures;
		int[] remainingCols=new int[numRemaining];
		for(r=0;r<numRemaining;r++){
			 remainingCols[r]=origColumns[i];
			 i++;
		}
		table.removeColumnsByIndex(remainingCols);
		}catch(Exception e){
			 System.out.println("WeightedSumTransform Failed");
			 e.printStackTrace();
			 return false;
		}
	
		return true;
	}

	/**get the label prefix for new features*/
	public String getNewFeatureLabelPrefix(){
		 return newFeatureLabelPrefix;
	}
	/**set the label prefix for new features*/
	public void setNewFeatureLabelPrefix(String s){
		 newFeatureLabelPrefix=s;
	}

}

