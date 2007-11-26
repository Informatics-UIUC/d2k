package ncsa.d2k.modules.core.util;

public class StringUtils {
	
	public static String[] stringToStringArray(String original, String regExp){
		String[] retVal = original.split(regExp, 0);
		for(int i=0; i<retVal.length; i++){
			retVal[i] = retVal[i].trim();
		}
		return retVal;
	}
	
	public static Object[] stringToObjectArray(String original, String regExp){
		String[] temp = stringToStringArray(original,regExp);
		Object[] retVal = new Object[temp.length];
		for(int i=0; i<retVal.length; i++){
			retVal[i] = temp[i];
		}
		return retVal;
	}
	
	
	public static String stringArrayToString(String[] array){
		String retVal = "";
		for(int i=0; i<array.length; i++){
			retVal += array[i] + ",";
		}
		retVal = retVal.substring(0, retVal.length()-1);
		return retVal;
	}
	

	public static String objectArrayToString(Object[] array){
		String retVal = "";
		for(int i=0; i<array.length; i++){
			retVal += (String)array[i] + ",";
		}
		retVal = retVal.substring(0, retVal.length()-1);
		return retVal;
	}

}
