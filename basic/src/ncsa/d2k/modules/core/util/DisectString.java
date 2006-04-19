package ncsa.d2k.modules.core.util;

import ncsa.d2k.core.modules.ReentrantComputeModule;

import ncsa.d2k.core.modules.*;

import java.util.regex.*;

public class DisectString
    extends ReentrantComputeModule {
  public DisectString(){
  }


  /**
   * given <code>str</code> which is a tag name (like a, p, h1 etc.)
   * returns a regular expression that represents the close tag for this
   * tag name. allowing case insesivity and also any white spaces before or
   * after the tag name.

   * @param str String an xml tag name
   * @return String a regular expression to represent the closing tag:
   *  for example:    </str >
   */
  protected String getClosingTagRegExp(String str){
  str = str.toUpperCase();
  //initializing with openning brackers followed by optional spaces
  //followed by forward slash and again optional spaces
  String regExp = "<\\s*/\\s*";

  //for each letter in str... create a 2 character array with the upper
  //case and the lower case version of this letter
  //then append [UPPER,lower] to the regular expresion
  for(int i=0; i<str.length(); i++){
    char[] temp = new char[2];
    temp[0] = str.charAt(i);
    temp[1] =(char)(str.charAt(i)+32);
    regExp+="[" + new String(temp) + "]";
  }
  //append white spaces followed by  the closing brackets.
  regExp +=  "\\s*>";
  return regExp;
}


/**
   * given <code>str</code> which is a tag name (like a, p, h1 etc.)
   * returns a regular expression that represents the open tag for this
   * tag name. allowing case insesivity and also any attributes names and values
   * after the tag name. allows also spaces between openning brackets '<'
   * and the tag name 'str'.
   * @param str String an xml tag name
   * @return String a regular expression to represent the open tag:
   *  for example:    < str att1=val1 ... att_n=val_n >
   */

protected String getOpenTagRegExp(String str){
  str = str.toUpperCase();
  //initializing with openning brackers followed by optional spaces.
  String regExp = "<\\s*";

  //for each letter in str... create a 2 character array with the upper
  //case and the lower case version of this letter
  //then append [UPPER,lower] to the regular expresion
  for(int i=0; i<str.length(); i++){
    char[] temp = new char[2];
    temp[0] = str.charAt(i);
    temp[1] =(char)(str.charAt(i)+32);
    regExp+="[" + new String(temp) + "]";
  }
  //append white spaces followed by any character (that is the attributes)
  //followed by the closing brackets.
  regExp +=  "\\s*.*>";
  return regExp;
}



/**
gets the content of the input document.
 splits it into sections that start after the openning tagName
 (and end right before another openning tagName)
 then for eahc such section - finds the start index of the closing tag
 and gets the sub string of this section from index 0 to the starting index.
 if no match is found for a section i, then no seb string is being pushed out.
 */
protected void doit(){

  String content = (String) pullInput(0);
String open = getOpenTagRegExp(this.tagName);
  if(this.verbose){

    System.out.println(this.getAlias() + ": the openning tag " +
                       "regular expression is: " + open);
  }



  String[] sections = content.split(open, 0);





  if(this.verbose){
    System.out.println(this.getAlias() + ": found " + sections.length +
                       " that starts with the openning tag.");
  }

  //for each string in sections, need to find the closing tag
  String closeTag = this.getClosingTagRegExp(this.tagName);


  if(this.verbose){
    System.out.println(this.getAlias() + ": the closing tag " +
                       "regular expression is: " + closeTag);
  }

  Pattern p = Pattern.compile(closeTag);

  for(int i=0; i<sections.length; i++){
    Matcher m = p.matcher(sections[i]);
    if(m.find()){

      int start = m.start();
      String subString = sections[i].substring(0, start);
      pushOutput(subString, 0);
      numOut++;

      if(this.verbose){
   System.out.println(this.getAlias() + ": found a match for the closing tag " +
                      " in section # " + i);
 }


    }//if found a match.
    else if(verbose){
      System.out.println(this.getAlias() + ": WARNING - found no match for the closing tag " +
                      " in section # " + i);
    }


  }//for

  pushOutput(new Integer(numOut),1);

}//doit

//properties
private String tagName;
public String getTagName(){return tagName;}
public void setTagName(String name){tagName = name;}

 /**
  * @return PropertyDescription[]
  */
 public PropertyDescription[] getPropertiesDescriptions()
{
   PropertyDescription[] pds = new PropertyDescription[1];
   pds[0] = new PropertyDescription("tagName", "Tag Name",
                                    "The tag name which is the anchor of disection." +
                                    " See module's description for detailed information.");
   return pds;
}



/**

 * @return String
 */
public String getModuleInfo(){
  return "<P><B>Overview:</b>:<BR>" +
      "This module takes the content of the <i>Input String</i>"
      + " and disects it into smaller strings, which are this modules's output.</p>" +
      "<P><b>Detailed Description</b>:<BR>" +
      "This module searches for open and close <i>Tag Name</i> tags in the " +
      "<i>Input String</i> and splits it into sub-strings containing only the text " +
     " within these tags.<br>" +
       "For example, if <i>Tag Name</i> value is \"foo\" " +
       "then this module will attempt to find &lt;foo&gt; .... &lt;/foo&gt;" +
       " sections in the <i>Input String</i> and output "+
       " the content of the tagged sections as String objects.<BR>" +
       "The search for the tags is very relaxed in the sence that it allows case insensitivity of " +
       "the tag name, white spaces before and after the tag name, and any characters " +
       "after the tag name in the open tag, to allow attributes and values.</P>";

}



/**

 * @return String
 */
public String getModuleName(){
  return "Disect String";
}

/**

 * @return String[]
 */
public String[] getInputTypes(){
  String [] types = new String[1];
  types[0] = "java.lang.String";

  return types;
}

/**

* @return String
*/
public String getInputInfo(int index){
  switch(index){
    case 0: return "String";
    default: return "no such input";
  }
}


/**

* @return String
*/
public String getInputName(int index){
  switch(index){
    case 0: return "Input String";
    default: return "no such input";
  }
}



/**

* @return String[]
*/
public String[] getOutputTypes(){
 String [] types = new String[2];
 types[0] = "java.lang.String";
 types[1] ="java.lang.Integer";
 return types;
}

/**

* @return String
*/
public String getOutputInfo(int index){
 switch(index){
   case 0: return "Sub String of the <i>Input String</i>.";
     case 1: return "Number of sub strings that were output. This is good only when expecting one input for this module.";
   default: return "no such output";
 }
}


/**

* @return String
*/
public String getOutputName(int index){
 switch(index){
   case 0: return "Sub String";
     case 1: return "Number of sub strings that were output";
   default: return "no such output";
 }
}


private boolean verbose;
public void setVerbose(boolean bl){verbose = bl;}
public boolean getVerbose(){return verbose;}

private int interval;
public void setInterval(int val){interval = val;}
public int getInterval(){return interval;}


private int numOut;
public void beginExecution(){
  numOut = 0;
}



}
