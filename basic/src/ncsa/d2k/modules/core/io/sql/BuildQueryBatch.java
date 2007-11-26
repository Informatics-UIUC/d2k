package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.util.StringUtils;

public class BuildQueryBatch extends BuildQuery {
	
	private String tbl = "";
	public void setTbl(String str){
		if(str == null || str.trim().length() == 0 ){
			tbl = super.getSelectedTable();
			return;
		}
		tbl = str;
		super.setSelectedTable(tbl);
		}
	
	
	public String getTbl(){
		if(tbl == null || tbl.trim().length() == 0){
			tbl = super.getSelectedTable();
		}
		return tbl;
		}
	
	private String attList = "";
	public String getAttList(){
		if(attList == null || attList.trim().length() == 0){
			String[] temp = (String[])super.getSelectedAttributes();			
			
			if(temp!= null && temp.length > 0){
				attList = StringUtils.stringArrayToString(temp);				
			}
		}
		return attList;
		
	}
	
	
	public void setAttList(String atts){
		if(atts == null || atts.trim().length() == 0){
			String[] temp =(String[]) super.getSelectedAttributes();
			
			if(temp!= null && temp.length > 0){
				attList = StringUtils.stringArrayToString(temp);
			}
			
			return;
		}
		
		attList = atts;
		String[] objArr = StringUtils.stringToStringArray(attList, ",");
		super.setSelectedAttributes(objArr);
	}
	
	
	public PropertyDescription[] getPropertiesDescriptions(){
		PropertyDescription[] temp = super.getPropertiesDescriptions();
		PropertyDescription[] pds = new PropertyDescription[temp.length + 2];
		for(int i=0; i<temp.length; i++){
			pds[i] = temp[i];
		}

		pds[temp.length ] = new PropertyDescription("tbl", "Selected Table",
		"A valid Table name from which the records are to be retrieved.");
		pds[temp.length + 1] = new PropertyDescription("attList", "Selected Attributes",
				"A cooma separated list of valid Attributes to be selected from the Table.");
		
		return pds;
	}
	
	
	public String getModuleName(){
		return "Batch Mode Build Query";
	}
	
	public String getModuleInfo(){
		return "This module is an extension of the UI module Build Query.<br>" +
		"It allows the user to set the Table name and the selected attributes of that table " +
		"via the properties" +
		" editor, by supplying a comma delimited string of attributes labels that " +
		"can be found in the DB Table. The user should take extra care to make " +
		"sure the labels are valid as the Table name, otherwise the run of the " +
		"itinerary will be " +
		"aborted due to an Exception.<BR>Please note those extra 2 properties "+
		" will be processed only if the UI is suppressed.";
	}
	
	public void endExecution(){
		if(!this.suppressGui){
			//copying over the properties from the super class
			this.tbl = super.getSelectedTable();
			String[] temp =(String[]) super.getSelectedAttributes();
			String atts = "";
			if(temp!= null && temp.length > 0){				
				attList = StringUtils.stringArrayToString(temp);
			}
			
			
			
			
		}
		super.endExecution();
	}
	

}
