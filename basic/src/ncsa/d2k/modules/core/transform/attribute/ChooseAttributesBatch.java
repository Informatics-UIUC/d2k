package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.Table;

public class ChooseAttributesBatch extends ChooseAttributes {
	private String _inputs = "";
	private String _outputs = "";

	
	public void setInputs(String ins){
		if(ins == null || ins.trim().length() == 0){
			Object[] temp = super.getSelectedInputs();
			ins = "";
			if(temp!= null && temp.length > 0){
				for(int i=0; i<temp.length; i++){
					ins += (String)temp[i] + ",";
				}
				
				_inputs = ins.substring(0, ins.length()-1);
			}
			
			return;
		}
		
		_inputs = ins;
		String[] temp = _inputs.split(", ", 0);
		Object[] inArr  = new Object[temp.length];
		for(int i=0; i<temp.length; i++){
			inArr[i] = temp[i];
		}
		this.setSelectedInputs(inArr);
	}
	
	public void setOutputs(String outs){
		
		if(outs == null || outs.trim().length() == 0){
			Object[] temp = super.getSelectedOutputs();
			outs = "";
			if(temp!= null && temp.length > 0){
				for(int i=0; i<temp.length; i++){
					outs += (String)temp[i] + ",";
				}
				
				_outputs = outs.substring(0, outs.length()-1);
			}
			
			return;
		}
		_outputs = outs;
		String[] temp = _outputs.split(", ", 0);
		Object[] outArr  = new Object[temp.length];
		for(int i=0; i<temp.length; i++){
			outArr[i] = temp[i];
		}
		this.setSelectedOutputs(outArr);
	}
	
	public String getInputs(){
		if(_inputs == null || _inputs.trim().length() == 0){
			Object[] temp = super.getSelectedInputs();
			String ins = "";
			if(temp!= null && temp.length > 0){
				for(int i=0; i<temp.length; i++){
					ins += (String)temp[i] + ",";
				}
				
				_inputs = ins.substring(0, ins.length()-1);
			}
		}
		return _inputs;
		}
	public String getOutputs(){
		if(_outputs == null || _outputs.trim().length() == 0){
			Object[] temp = super.getSelectedOutputs();
			String outs = "";
			if(temp!= null && temp.length > 0){
				for(int i=0; i<temp.length; i++){
					outs += (String)temp[i] + ",";
				}
				
				_outputs = outs.substring(0, outs.length()-1);
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

}
