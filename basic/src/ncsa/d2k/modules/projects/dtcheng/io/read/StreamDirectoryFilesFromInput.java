package ncsa.d2k.modules.projects.dtcheng.io.read;

import ncsa.d2k.core.modules.InputModule;
import ncsa.d2k.modules.projects.dtcheng.streams.*;

import java.io.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class StreamDirectoryFilesFromInput extends InputModule {

  private int MaxNumDataFiles = 10000;

  public void setMaxNumDataFiles(int value) {
    this.MaxNumDataFiles = value;
  }

  public int getMaxNumDataFiles() {
    return this.MaxNumDataFiles;
  }

  private int MaxNumDirectories = 1000;

  public void setMaxNumDirectories(int value) {
    this.MaxNumDirectories = value;
  }

  public int getMaxNumDirectories() {
    return this.MaxNumDirectories;
  }

  private boolean PrintDirectoryNames = false;

  public void setPrintDirectoryNames(boolean value) {
    this.PrintDirectoryNames = value;
  }

  public boolean getPrintDirectoryNames() {
    return this.PrintDirectoryNames;
  }

  private boolean Recursive = false;

  public void setRecursive(boolean value) {
    this.Recursive = value;
  }

  public boolean getRecursive() {
    return this.Recursive;
  }

  private String FileNameFilter = "";

  public void setFileNameFilter(String value) {
    this.FileNameFilter = value;
  }

  public String getFileNameFilter() {
    return this.FileNameFilter;
  }

  public String getModuleName() {
    return "StreamDirectoryFilesFromInput";
  }

  public String getModuleInfo() {
    return "StreamDirectoryFilesFromInput";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "Directory";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] types = { "java.io.File" };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "Directory";
    default:
      return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "DirectoryFile Stream";
    case 1:
      return "DataFile Stream";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] types = { "java.io.File", "java.io.File" };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "File";
    default:
      return "No such output";
    }
  }

  public void findDirectories(File currentDirectory, SubstringFileFilter filter, File[] allDirectories, int[] numDirectories, boolean recursive) {

    File[] directoryFiles = currentDirectory.listFiles(filter);

    int numFiles = directoryFiles.length;
    for (int i = 0; i < numFiles; i++) {
      if (directoryFiles[i].isDirectory()) {

        allDirectories[numDirectories[0]] = directoryFiles[i];
        numDirectories[0] = numDirectories[0] + 1;

        if (recursive)
          findDirectories(directoryFiles[i], filter, allDirectories, numDirectories, recursive);
      }
    }

  }

  Class streamMarkerClass = (new StreamMarker()).getClass();
  public void doit() throws Exception {

    //System.out.println("Hello");
    //System.out.println("DirectoryPath = " + DirectoryPath);

    Object object = this.pullInput(0);
    if (streamMarkerClass.isInstance(object)) {
      this.pushOutput(object, 0);
      return;
    }
    
   
    
    File rootDirectoryFile = (File) object;

    SubstringFileFilter filter = new SubstringFileFilter(FileNameFilter);

    //System.out.println("directory = " + directory);

    File[] files = null;
    File[] directoryFiles = new File[MaxNumDirectories];
    int[] numDirectoryFiles = new int[] { 0 };

    // recursively find all directores
    directoryFiles[0] = rootDirectoryFile;
    numDirectoryFiles[0] = 1;

    findDirectories(rootDirectoryFile, filter, directoryFiles, numDirectoryFiles, Recursive);

    int numDirectories = numDirectoryFiles[0];

    if (PrintDirectoryNames) {
      for (int i = 0; i < numDirectories; i++) {
        System.out.println(directoryFiles[i]);
      }
    }

    //System.out.println("Number of Directories in Folder = " + (numDirectories
    // - 1));

    int numDataFiles = 0;
    long numDataBytesInFolder = 0;
    File[] dataFiles = new File[MaxNumDataFiles];
    for (int i = 0; i < numDirectories; i++) {
      files = directoryFiles[i].listFiles(filter);
      if (files == null) {
        continue;
      }
      for (int f = 0; f < files.length; f++) {
        if (files[f].isFile()) {
          dataFiles[numDataFiles++] = files[f];
          numDataBytesInFolder += files[f].length();
        }
      }
    }

    if (false) {
      System.out.println("Number of Files in Folder = " + numDataFiles);
      System.out.println("Number of Data Bytes in Folder = " + numDataBytesInFolder);
    }

    this.pushOutput(new StreamStartMarker(), 0);
    for (int i = 0; i < numDirectories; i++) {
      this.pushOutput(directoryFiles[i], 0);
    }
    this.pushOutput(new StreamEndMarker(), 0);
    
    this.pushOutput(new StreamStartMarker(), 1);
    for (int i = 0; i < numDataFiles; i++) {
      this.pushOutput(dataFiles[i], 1);
    }
    this.pushOutput(new StreamEndMarker(), 1);
  }
}