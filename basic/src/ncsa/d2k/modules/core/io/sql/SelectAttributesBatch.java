package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.core.modules.PropertyDescription;

public class SelectAttributesBatch extends SelectAttributes {
	
	private String _selectedAtts = "";
	public void setSelectedAtts(String atts){
		if(atts == null || atts.trim().length() == 0){
			String[] temp = super.getSelectedAttributes();
			atts = "";
			if(temp!= null && temp.length > 0){
				for(int i=0; i<temp.length; i++){
					atts += temp[i] + ",";
				}
				
				_selectedAtts = atts.substring(0, atts.length()-1);
			}
			
			return;
		}
		
		_selectedAtts = atts;
		String[] temp = _selectedAtts.split(", ", 0);
		/*Object[] objArr  = new Object[temp.length];
		for(int i=0; i<temp.length; i++){
			objArr[i] = temp[i];
		}*/
		super.setSelectedAttributes(temp);
		
	}
	
	public String getSelectedAtts(){
		if(_selectedAtts == null || _selectedAtts.trim().length() == 0){
			String[] temp = super.getSelectedAttributes();
			String atts = "";
			if(temp!= null && temp.length > 0){
				for(int i=0; i<temp.length; i++){
					atts += temp[i] + ",";
				}
				
				_selectedAtts = atts.substring(0, atts.length()-1);
			}
		}
		return _selectedAtts;
	}
	
	
	
	public PropertyDescription[] getPropertiesDescriptions(){
		PropertyDescription[] temp = super.getPropertiesDescriptions();
		PropertyDescription[] pds = new PropertyDescription[temp.length + 1];
		for(int i=0; i<temp.length; i++){
			pds[i] = temp[i];
		}
		pds[temp.length] = new PropertyDescription("selectedAtts", "Selected Attributes",
				"A comma separated list of labels that are valid attributes in " +
				"the expected input List of attributes.");
		
		return pds;
	}
	
	public String getModuleName(){
		return "Batch Mode Select Attributes";
	}
	
	public String getModuleInfo(){
		return "This module is an extension of the UI module Select Attributes.<br>" +
		"It allows the user to set the selected attributes via the properties" +
		" editor, by supplying a comma delimited string of attributes labels that " +
		"can be found in the input attribtes List. The user should take extra care to make " +
		"sure the labels are valid, otherwise the run of the itinerary will be " +
		"aborted due to an Exception.<BR>Please note that the comma separated list" +
		" will be processed only if the UI is suppressed.";
	}

}
