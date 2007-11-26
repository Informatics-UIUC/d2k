package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.util.StringUtils;

public class ChooseAttributesBatch extends ChooseAttributes {
	private String _inputs = "";
	private String _outputs = "";

	
	public void setInputs(String ins){
		if(ins == null || ins.trim().length() == 0){
			Object[] temp = super.getSelectedInputs();			
			if(temp!= null && temp.length > 0){				
				_inputs = StringUtils.objectArrayToString(temp);
			}			
			return;
		}		
		_inputs = ins;		
		Object[] inArr  = StringUtils.stringToObjectArray(_inputs,",");			
		this.setSelectedInputs(inArr);
	}
	
	public void setOutputs(String outs){
		
		if(outs == null || outs.trim().length() == 0){
			Object[] temp = super.getSelectedOutputs();			
			if(temp!= null && temp.length > 0){				
				_outputs = StringUtils.objectArrayToString(temp);
			}
			
			return;
		}
		_outputs = outs;		
		Object[] outArr  = StringUtils.stringToObjectArray(_outputs,",");		
		this.setSelectedOutputs(outArr);
	}
	
	public String getInputs(){
		if(_inputs == null || _inputs.trim().length() == 0){
			Object[] temp = super.getSelectedInputs();			
			if(temp!= null && temp.length > 0){			
				_inputs = StringUtils.objectArrayToString(temp);
			}
		}
		return _inputs;
		}
	public String getOutputs(){
		if(_outputs == null || _outputs.trim().length() == 0){
			Object[] temp = super.getSelectedOutputs();			
			if(temp!= null && temp.length > 0){				
				_outputs = StringUtils.objectArrayToString(temp);
			}
		}
		return _outputs;
		}
	
	public PropertyDescription[] getPropertiesDescriptions(){
		PropertyDescription[] temp = super.getPropertiesDescriptions();
		PropertyDescription[] pds = new PropertyDescription[temp.length + 2];
		for(int i=0; i<temp.length; i++){
			pds[i] = temp[i];
		}
		pds[temp.length] = new PropertyDescription("inputs", "Selected Input",
				"A comma separated list of labels that are valid column names in " +
				"the input Table, to be designated as the input attributes of this table.");
		
		pds[temp.length +1] = new PropertyDescription("outputs", "Selected Output",
				"A comma separated list of labels that are valid column names in " +
				"the input Table, to be designated as the output attributes of this table.");
		return pds;
	}
	
	public String getModuleName(){
		return "Batch Mode Choose Attributes";
	}
	
	public String getModuleInfo(){
		return "This module is an extension of the UI module Choose Attributes.<br>" +
		"It allows the user to set the selected inputs and outputs via the properties" +
		" editor, by supplying a comma delimited string of columns labels that " +
		"can be found in the input table. The user should take extra care to make " +
		"sure the labels are valid, otherwise the run of the itinerary will be " +
		"aborted due to an Exception." +
		"<BR>Please note that the comma separated lists" +
		" will be processed only if the UI is suppressed.";
	}
	
	public void endExecution(){
		//if ran with gui
		if(!this.suppressGui){
			//copy the new settings over here.
			Object[] temp = super.getSelectedOutputs();			
			if(temp!= null && temp.length > 0){				
				_outputs = StringUtils.objectArrayToString(temp);
			}
			 temp = super.getSelectedInputs();			
			if(temp!= null && temp.length > 0){				
				_inputs = StringUtils.objectArrayToString(temp);
			}
		}
		super.endExecution();
	}

}
