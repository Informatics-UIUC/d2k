package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.BufferedReader;

public class ModuleProcessor {
  
  public ModuleProcessor(BufferedReader templateReader, int bufferSize) {
    this.bufferSize = bufferSize;
    this.templateReader = templateReader;
    this.nModulesRead = 0;
    this.nLinesInModule = 0;
    this.originalLines = new String[bufferSize];
    this.modifiedLines = new String[bufferSize];
    this.contentsHaveBeenChanged = false;
    this.doneWithModules = false;
  }
  
  private int bufferSize = -1;
  private int nModulesRead = Integer.MIN_VALUE;
  private BufferedReader templateReader = null;
  private int nLinesInModule = Integer.MIN_VALUE + 1;
  private boolean contentsHaveBeenChanged = false;
  private boolean doneWithModules = false;
  
  private String[] originalLines = null;
  private String[] modifiedLines = null;
  
  public int nModulesRead() {
    return nModulesRead;
  }
  
  public int nLinesInModule() {
    return nLinesInModule;
  }
  
  public boolean contentsHaveBeenChanged() {
    return this.contentsHaveBeenChanged;
  }
  
  public boolean doneWithModules() {
    return this.doneWithModules;
  }
  
  public void readAModule() throws Exception {
    nLinesInModule = 0;
    boolean endOfModuleReached = false;
    
    while (!endOfModuleReached) {
      originalLines[nLinesInModule] = templateReader.readLine();
      
      if (originalLines[nLinesInModule].indexOf("</module>") > -1) {
        endOfModuleReached = true;
      } else if ((originalLines[nLinesInModule].indexOf("</itinerary>") > -1) ||
          (originalLines[nLinesInModule].indexOf("</toolkit>") > -1)) {
        endOfModuleReached = true;
        doneWithModules = true;
      }
      nLinesInModule++;
    }
    nModulesRead++;
    contentsHaveBeenChanged = false;
  }
  
  public String getModuleAlias() {
    
    int aliasBeginIndex = originalLines[0].indexOf("alias");
    int aliasRealBeginIndex = originalLines[0].indexOf("\"", aliasBeginIndex) + 1;
    int aliasEndIndex = originalLines[0].indexOf("\"", aliasRealBeginIndex ); // Beware the MAGIC NUMBER!!!
    String theAlias = new String(originalLines[0].substring(aliasRealBeginIndex,aliasEndIndex));
    
    return theAlias;
  }
  
  public String getClassName() {
    String[] linesToUse = null;
    
    if (contentsHaveBeenChanged) {
      linesToUse = modifiedLines;
    } else {
      linesToUse = originalLines;
    }
    int classBeginIndex = linesToUse[0].indexOf("classname");
    int classRealBeginIndex = linesToUse[0].indexOf("\"", classBeginIndex) + 1;
    int classEndIndex = linesToUse[0].indexOf("\"", classRealBeginIndex ); // Beware the MAGIC NUMBER!!!
    String theClassName = new String(linesToUse[0].substring(classRealBeginIndex,classEndIndex));
    
    return theClassName;
  }
  
  public String[] theModulesLines() {
    String[] onlyTheRealLines = new String[nLinesInModule];
    
    if (contentsHaveBeenChanged) {
      for (int lineIndex = 0; lineIndex < nLinesInModule; lineIndex++) {
        onlyTheRealLines[lineIndex] = modifiedLines[lineIndex];
      }
    } else {
      for (int lineIndex = 0; lineIndex < nLinesInModule; lineIndex++) {
        onlyTheRealLines[lineIndex] = originalLines[lineIndex];
      }
      
    }
    
    return onlyTheRealLines;
  }
  
  public void prependAliases(String newPrefix) {
    int aliasBeginIndex = -2;
    int aliasRealBeginIndex = -3;
    int aliasEndIndex = -4;
    String oldAlias;
    String beforeAlias;
    String afterAlias;
    
//    String[] renamedModule = new String[oldModule.length];
    String[] linesToUse = null;
    if (contentsHaveBeenChanged) {
      linesToUse = modifiedLines;
    } else {
      linesToUse = originalLines;
    }
    
    for (int lineIndex = 0; lineIndex < nLinesInModule; lineIndex++) {
      
      aliasBeginIndex = linesToUse[lineIndex].indexOf("alias");
      if (aliasBeginIndex > -1) {
        // there is an alias in here that needs to be renamed...
        aliasRealBeginIndex = linesToUse[lineIndex].indexOf("\"", aliasBeginIndex) + 1;
        aliasEndIndex = linesToUse[lineIndex].indexOf("\"", aliasRealBeginIndex ); // Beware the MAGIC NUMBER!!!
        
        oldAlias = new String(linesToUse[lineIndex].substring(aliasRealBeginIndex,aliasEndIndex));
        beforeAlias = linesToUse[lineIndex].substring(0,aliasRealBeginIndex);
        afterAlias = linesToUse[lineIndex].substring(aliasEndIndex);
        
        modifiedLines[lineIndex] = new String(beforeAlias + newPrefix + oldAlias + afterAlias);
        
      } else {
        modifiedLines[lineIndex] = linesToUse[lineIndex];
      }
    }
    
    contentsHaveBeenChanged = true;
  }
  
  public void setPropertyValue(String propertyName, String newValue)
  {
    int nameBeginIndex = -2;
    int nameRealBeginIndex = -3;
    int nameEndIndex = -4;
    String actualPropertyName;

    int valueBeginIndex = -2;
    int valueRealBeginIndex = -3;
    int valueEndIndex = -4;

    String beforeValue;
    String afterValue;

    String[] linesToUse = null;
    if (contentsHaveBeenChanged) {
      linesToUse = modifiedLines;
    } else {
      linesToUse = originalLines;
    }

    
    for (int lineIndex = 0; lineIndex < nLinesInModule; lineIndex++) {
      
      nameBeginIndex = linesToUse[lineIndex].indexOf("name");
      if (nameBeginIndex > -1) {
        // there is a property that needs to be compared in here that needs to be renamed...
        nameRealBeginIndex = linesToUse[lineIndex].indexOf("\"", nameBeginIndex) + 1;
        nameEndIndex = linesToUse[lineIndex].indexOf("\"", nameRealBeginIndex ); // Beware the MAGIC NUMBER!!!
        
        actualPropertyName = new String(linesToUse[lineIndex].substring(nameRealBeginIndex,nameEndIndex));
        
        if (actualPropertyName.equals(propertyName)) {
          valueBeginIndex = linesToUse[lineIndex].indexOf("value");
          valueRealBeginIndex = linesToUse[lineIndex].indexOf("\"", valueBeginIndex) + 1;
          valueEndIndex = linesToUse[lineIndex].indexOf("\"", valueRealBeginIndex ); // Beware the MAGIC NUMBER!!!

          beforeValue = linesToUse[lineIndex].substring(0,valueRealBeginIndex);
          afterValue = linesToUse[lineIndex].substring(valueEndIndex);
          
          modifiedLines[lineIndex] = new String(beforeValue + newValue + afterValue);
        }
        
//        renamedModule[lineIndex] = new String(beforeAlias + null + oldAlias + afterAlias);
        
      } else {
        modifiedLines[lineIndex] = linesToUse[lineIndex];
      }
    }
    
    contentsHaveBeenChanged = true;
  }
  
}
