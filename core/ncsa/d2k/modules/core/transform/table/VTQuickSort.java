package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.Date;
import java.io.Serializable;

/************************
	VTQuickSort

	Sorts the VerticalTable based on the column 'sortColumn' indicated by that
	property.  Uses a fancy tri-pivot technique and then an insertion sort
	to clean up what it misses.  Also displays/prints the time each component
	of the sort takes

***/

public class VTQuickSort extends DataPrepModule
	implements Serializable{

	TableImpl sorted;
	NumericColumn sortColumn;

	int sortColumnIndex=0;
	public void setSortColumnIndex(int i){
		sortColumnIndex=i;
	}

	public int getSortColumnIndex(){
		return sortColumnIndex;
	}

	public boolean printTime=false;
	public boolean getPrintTime(){
		return printTime;
	}
	public void setPrintTime(boolean b){
		printTime=b;
	}

	public boolean ascending=true;
	public boolean getAscending(){
		return ascending;
	}
	public void setAscending(boolean b){
		ascending=b;
	}
	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The Vertical Table to sort</Text>  </Info></D2K>";
			default: return "No such input";
		}
	}


	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {

		String [] types =  {
			"ncsa.d2k.modules.datatype.table.basic.TableImpl"

			};
		return types;

	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {

		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Sorted Table\">    <Text>The Sorted Table </Text>  </Info></D2K>";
			default: return "No such output";
		}

	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {

		String [] types =  {
			"ncsa.d2k.modules.datatype.table.basic.TableImpl"};
		return types;

	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {

		 return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Basic Vertical Table Quick Sort\">    <Text>This module takes in a VerticalTable and uses quick sort to sort the table based on the column (must be an instance of NumericColumn) indicated by the property sortColumn. PROPS: sortColumn - the column to sort by. printTime - will print the time taken by the algorithms two parts to standard out. ascending- if true, results will be in ascending order, if not, descending (descending requires a rather inefficient reversal of a table sorted to be ascending).</Text>  </Info></D2K>";

	}

  /*///////////////////
  //THE ALMIGHTY DOIT
  //////////////////*/
	public void doit () throws Exception {

	TableImpl raw=(TableImpl)pullInput(0);
	sorted=	(TableImpl)DefaultTableFactory.getInstance().createTable(2);

	sortColumn=(NumericColumn)raw.getColumn(sortColumnIndex).copy();
	/*System.out.println(sortColumn.getNumRows());
	for(int i=0; i<sortColumn.getNumRows(); i++){
		System.out.println(i+" "+sortColumn.getRow(i).toString());
	}*/
	IntColumn orderColumn=new IntColumn(sortColumn.getNumRows());

	for (int i=0; i<orderColumn.getNumRows(); i++){
		orderColumn.setInt(i, i);
	}
	/*
	for(int i=0; i<orderColumn.getNumRows(); i++){
		System.out.println(orderColumn.getRow(i).toString());
	}
	*/
	sorted.setColumn(sortColumn, 0);
	sorted.setColumn(orderColumn, 1);

	Date startTime=new Date();

	quickSort(0, sortColumn.getNumRows()-1);
	//for(int i=0; i<sortColumn.getNumRows(); i++)
	//System.out.println(sortColumn.getRow(i).toString());
	//System.out.println();
    Date quickTime=new Date();

	insertionSort(0, sortColumn.getNumRows()-1);

	Date endTime=new Date();
	if(printTime){
	    System.out.println("QS: QuickSort Time:"+(quickTime.getTime()-startTime.getTime())+" InsertionSort Time:"+(endTime.getTime()-quickTime.getTime()));
	}
	/*
	for(int i=0; i<sortColumn.getNumRows(); i++)
	System.out.println(sortColumn.getRow(i).toString());
	*/

	/*
		now use the VT that is sorted and contains the row numbers from the orig
		table in sorted order (sorted by the sorting column) to make a new sorted
		column that contains all the data (not just the sorting column)
	*/

	int[] newOrder=(int[])orderColumn.getInternal();
	TableImpl fullSorted=(TableImpl)raw.copy();

	for(int i=0; i<newOrder.length; i++){
		Object[] row = new Object[raw.getNumColumns()];
		raw.getRow(row, i);
		fullSorted.setRow(/*raw.getRow(newOrder[i])*/row, i);
	}
	if(!ascending){
		reverse(fullSorted);
	}

	pushOutput(fullSorted, 0);

  }

	private void quickSort(int l, int r){

		if(r-l<=3){
			return;
		}
		int pivot;

		int i=(r+l)/2;

		/*
			of {i, l, r}, make the pivot the one with value in sort column
			between the other two (Tri-median method)
		*/
		/*
		System.out.println("QS: l:"+l+" i:"+i+" r:"+r);
		*/
		if(sortColumn.compareRows(l, i)>0)
			sorted.swapRows(l, i);
		if(sortColumn.compareRows(l, r)>0)
			sorted.swapRows(l, r);
		if(sortColumn.compareRows(i, r)>0);
			sorted.swapRows(i, r);




		/*
	   		XXXXXmove the pivot to the end, and the one we know is greater than
   			XXXXXXthe pivot to one position before the end
		*/

		sorted.swapRows(i, r-1);

		pivot=r-1;
		/*
   			from position i=l+1 start moving to the right, from j=r-2 start moving
   			to the left, and swap when the fitness of i is more than the pivot
   			and j's fitness is less than the pivot
		*/

		i=l+1;
		int j=r-2;

		while(j>i){

			while((sortColumn.compareRows(i, pivot)<=0) && (i<j)){
				i++;
			}

			while((sortColumn.compareRows(j, pivot)>=0) && (i<j)){
				j--;
			}

			if(i<j){
				sorted.swapRows(i, j);
			}
		}

		/*
	   		put the pivot back in the middle, at this point j should equal i
		*/

			sorted.swapRows(r-1, j);

		/*
			sort the two halves
		*/

			quickSort(l, i-1);
			quickSort(j+1, r);
	}

	public void insertionSort(int l, int r){

			int i;
			int j;
			Object[] v;//the row
			Object w;
			for(j=l+1; j<=r; j++){
				v = new Object[sorted.getNumColumns()];
				sorted.getRow(v, j);
				//v=sorted.getRow(j);
				w=sortColumn.getRow(j);
				i=j-1;
				while((i>=l) && (sortColumn.compareRows(w, i)<0)){
					Object [] rr = new Object[sorted.getNumColumns()];
					sorted.getRow(rr, i+1);
					sorted.setRow(/*sorted.getRow(i)*/rr, i+1);
					i--;
				}
				sorted.setRow(v, i+1);
			}
	}

	private void reverse(TableImpl vt){
		int numRows=vt.getNumRows();
		int halfWay=(int)(numRows/2D);
		for(int i=0;i<halfWay; i++){
			vt.swapRows(i, numRows-i-1);
		}
	}
}
