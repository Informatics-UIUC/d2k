package ncsa.d2k.modules.projects.dtcheng.ssa;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.Hashtable;
import java.util.zip.*;
import java.util.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;

public class ReadMBRFiles extends InputModule {
  
  
  public String getModuleName() {
    return "ReadMBRFiles";
  }
  
  public String getModuleInfo() {
    return "ReadMBRFiles";
  }
  
  
  private String KeyFeatureName = "CAN";
  
  
  private boolean reportRepeatValues = false;
  
  public void setReportRepeatValues(boolean value) {
    this.reportRepeatValues = value;
  }
  
  public boolean getReportRepeatValues() {
    return this.reportRepeatValues;
  }
  
  
  private boolean reportEachRecord = false;
  
  public void setReportEachRecord(boolean value) {
    this.reportEachRecord = value;
  }
  
  public boolean getReportEachRecord() {
    return this.reportEachRecord;
  }
  
  
  private boolean reportRecordStructrure = false;
  
  public void setReportRecordStructrure(boolean value) {
    this.reportRecordStructrure = value;
  }
  
  public boolean getReportRecordStructrure() {
    return this.reportRecordStructrure;
  }
  
  
  
  private int BufferSize = 1000000;
  public void setBufferSize(int value) {
    this.BufferSize = value;
  }
  public int getBufferSize() {
    return this.BufferSize;
  }
  
  
  private int KeyFieldIndex = 1;
  public void setKeyFieldIndex(int value) {
    this.KeyFieldIndex = value;
  }
  public int getKeyFieldIndex() {
    return this.KeyFieldIndex;
  }
  
  
  private int ReportInterval = 1000;
  
  public void setReportInterval(int value) {
    this.ReportInterval = value;
  }
  
  public int getReportInterval() {
    return this.ReportInterval;
  }
  
  private int maxNumRecordsToRead = 1000;
  
  public void setMaxNumRecordsToRead(int value) {
    this.maxNumRecordsToRead = value;
  }
  
  public int getMaxNumRecordsToRead() {
    return this.maxNumRecordsToRead;
  }
  
  
  private int BeginFileNumber = 1;
  private int EndFileNumber = 1;
  
  public String getInputName(int i) {
    switch (i) {
      default:
        return "NotAvailable";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }
  
  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "NotAvailable";
    }
  }
  
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "SSNHashtable";
      default:
        return "No such output";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = { "java.util.Hashtable" };
    return types;
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "SSNHashtable";
      default:
        return "No such output";
    }
  }
  
  
  String [] GroupFileNames = {
    
    "C:/data/ssa/do410x.txt.gz",
  };
  
  
  int NumFiles = GroupFileNames.length;
  
  String [] NestingMode = {
    
    "normal",
    
  };
  
  String [] CompressionMode = {
    
    "gzip",
    
    
  };
  
  String [] FileDescriptions = {
    
    "Disabled Beneficiaries and Dependents MBR File (DBADMBR)",
  };
  
  
  boolean [] FixedFormat = {
    
    true,
    
  };
  
  
  int [][] ComponentNumRepetitions = {
    
    // DBADMBR
    {1,
     8,
     1,
     2,
     1,
     10,
     1,
     6,
     1,
     5,
     1,
     5,
     1,
     6,
     1,
     3,
     1,
     2,
     1,
     5,
     1,
     35,
     1,
     5,
     1,
     5,
     1,
     5,
     1,
     4,
     1},
     
  };
  
  
  String [][][] ComponentFieldNames =  {
    
    // DBADMBR
    {
      {
        "SEL-DATE",
        "CAN",
        "PCCOM",
        "PCCR",
        "FPCCOM",
        "FPCCR",
        "BIC",
        "CIS1-FOLDER",
        "CIS1-DISAB",
        "CIS1-NEWCMP",
        "CIS1-WORKER",
        "SCIC2-PP",
        "SROP-RACE",
        "LSEC-SUR-LSDP",
        "LSEC-CONV",
        "LSEC-RIB",
        "LSEC-DIB",
        "MIL",
        "RRC",
        "NPIH",
      },
      {
        "PIED",
        "PIFC",
        "PIA",
        "FMAX",
        "TOM",
        "IME",
        "ELY",
        "PIFC2",
      },
      {
        "NINSD",
      },
      {
        "INSD-CLMTYP",
        "INSD-DCF",
        "INSD-LAST-MTH",
        "INSD-WPSD-MTH",
        "INSD-2040-XMT",
        "INSD-2040-NXMT",
        "INSD-A31-XMT",
        "INSD-A31-NXMT",
        "INSD-A24-XMT",
        "INSD-A24-NXMT",
        "INSD-DIB-REQ",
        "INSD-DIB-HAS",
        "INSD-FIS-XMT",
        "INSD-FIS-NXMT",
        "INSD-FIS-REQ",
        "INSD-FIS-HAS",
        "INSD-STBL-IND",
      },
      {
        "MPA",
        "DOC",
        "SCC",
        "ZIP",
        "ZIP-ADD-ON",
        "PDB",
        "PFA",
        "FAT",
        "LAF",
        "DOB",
        "DOEI",
        "DOEC",
        "DE-TYPE",
        "RED-FOR-AGE",
        "RIBLIM-APPLIES",
        "DRD",
        "DOST",
        "DOCA",
        "SEX",
        "MBP",
        "LANG",
        "RRIC",
        "TOC",
        "ABOD",
        "NBCLM",
      },
      {
        "BCLM-DOE-START",
        "BCLM-DOF",
        "BCLM-APPRECPT",
        "BCLM-BIC",
        "BCLM-CEC",
        "BCLM-OTSID-MAX",
        "BCLM-DOETERM",
      },
      {
        "NDENY",
      },
      {
        "DENY-DOF",
        "DENY-APPRECPT",
        "DENY-BIC",
        "DENY-CEC",
        "DENY-DDO",
        "DENY-RDD",
        "DENY-RDD-LOD",
      },
      {
        "NDDIB",
      },
      {
        "DDIB-SUSPEFF",
        "DDIB-RESUMREQ",
      },
      {
        "NCDIB",
      },
      {
        "CDIB-SUSPEFF",
        "CDIB-SUSPRSN",
        "CDIB-RESUMREQ",
      },
      {
        "DPO",
        "DPRD",
        "DPOT",
        "DWA",
        "PROTEST",
        "DOS",
        "TOP",
        "CC",
        "GS",
        "YIPS",
        "SIFT",
        "SISC",
        "BOAN",
        "BDOD",
        "LPDA",
        "LPWD",
        "DPI",
        "DST",
        "DSP",
        "NDIB",
      },
      {
        "DIG",
        "SDIG",
        "DOED",
        "DDBC",
        "DSD",
        "HDD",
        "SDS",
        "ADC",
        "APS",
        "DDO",
        "DAC",
        "LOD",
        "BDC",
        "CDR",
        "PRY",
        "EBD",
        "EMD",
        "DAA",
      },
      {
        "NXRD",
      },
      {
        "XRTC",
        "XRAN",
        "XBIC",
      },
      {
        "NDED",
      },
      {
        "TOD",
        "OTAN",
        "OTBIC",
        "OTDOE",
        "OTPIA",
        "LFMBA",
        "LEMBA",
        "SFMBA",
        "SAMBA",
        "DESC",
      },
      {
        "NENFD",
      },
      {
        "YOE",
        "YOES",
        "TEE",
        "ECC",
        "ESOC",
        "EPT",
        
      },
      {
        "NHIST",
      },
      {
        "EFD",
        "MBA",
        "RFD",
        "WIC",
        "BPD",
        "MBC",
        "HRFST",
      },
      {
        "NDTPD",
      },
      {
        "TPI",
        "DCF",
      },
      {
        "NHI",
      },
      {
        "HI-START",
        "HI-TERM",
        "HI-BASIS",
        "HI-NONCOVRSN",
      },
      {
        "NSMI",
      },
      {
        "SMI-START",
        "SMI-TERM",
        "SMI-BASIS",
        "SMI-NONCOVRSN",
      },
      {
        "NDRAM",
      },
      {
        "PDST-REL",
        "PDED-REL",
        "PDSOURCE",
      },
      {
        "NCTZN",
        "CTZN-START",
        "CTZN-STOP",
        "CTZN-COUNTRY",
        "CTZN-USPROOF",
        "NLWPR",
        "LWPR-START",
        "LWPR-STOP",
        "LWPR-PROOF",
        "LWPR-STATUS",
        "FILLER1"
      },
    },
  };
  
  
  int [][][] ComponentFieldSizes =
  {
    // DBADMBR
    {
      {
        8,
        9,
        6,
        1,
        6,
        1,
        2,
        1,
        1,
        1,
        1,
        2,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        2,
      },
      
      {
        6,
        1,
        5,
        5,
        1,
        4,
        4,
        1,
      },
      
      {
        2,
      },
      
      {
        1,
        8,
        6,
        6,
        1,
        1,
        1,
        1,
        1,
        1,
        2,
        2,
        1,
        1,
        2,
        2,
        1,
      },
      
      {
        5,
        3,
        5,
        5,
        4,
        6,
        6,
        1,
        2,
        8,
        6,
        6,
        1,
        1,
        1,
        8,
        6,
        6,
        1,
        5,
        1,
        1,
        1,
        8,
        2,
      },
      
      {
        6,
        8,
        8,
        2,
        1,
        1,
        6,
      },
      
      {
        2,
      },
      
      {
        8,
        8,
        2,
        1,
        8,
        3,
        1,
        
      },
      
      {
        1,
      },
      
      {
        6,
        6,
      },
      
      {
        1,
      },
      
      {
        6,
        1,
        6,
      },
      
      {
        8,
        6,
        2,
        4,
        1,
        6,
        1,
        1,
        1,
        4,
        1,
        1,
        9,
        8,
        7,
        6,
        2,
        6,
        6,
        
        2,
      },
      
      {
        4,
        4,
        6,
        6,
        6,
        6,
        6,
        6,
        6,
        8,
        1,
        1,
        2,
        1,
        1,
        6,
        6,
        1,
      },
      {
        1,
      },
      {
        1,
        9,
        2,
      },
      
      {
        1,
      },
      { 1,
        9,
        2,
        6,
        5,
        5,
        5,
        5,
        5,
        1,
      },
      
      
      {
        1,
      },
      {
        4,
        4,
        7,
        2,
        1,
        1,
      },
      
      
      {
        2,
      },
      {
        6,
        5,
        1,
        1,
        1,
        5,
        6,
      },
      
      {
        1,
      },
      {
        1,
        6,
        
      },
      
      {
        2,
      },
      {
        6,
        6,
        1,
        1,
      },
      
      {
        2,
      },
      {
        6,
        6,
        1,
        1,
      },
      
      {
        1,
      },
      {
        6,
        6,
        1,
      },
      
      
      {
        1,
        8,
        8,
        2,
        1,
        2,
        8,
        8,
        1,
        1,
        8,
      },
      
    },
    
    
  };
  
  int TotalNumberOfFields;
  byte[] readBuffer = null;
  
  int[] IntReadBuffer = null;
  
  int NumAllFields = 0;
  String[] AllFieldNames;
  
  String[] FieldNames;
  
  int[] FieldSizes;
  
  public void beginExecution() {
    
  }
  
  Integer StaticObject = new Integer(1);
  public void doit() throws Exception {
    
    
    
    int [][] ComponentNumFields = new int[NumFiles][];
    
    
    Hashtable Hashtable = new Hashtable();
    
    
    ///////////////////
    // For Each File //
    ///////////////////
    
    for (int FileIndex = BeginFileNumber - 1; FileIndex < EndFileNumber; FileIndex++) {
      
      System.out.println("File #" + (FileIndex + 1) + " : " + FileDescriptions[FileIndex]);
      
      
      int NumParts = ComponentFieldNames[FileIndex].length;
      
      if (NumParts != ComponentFieldNames[FileIndex].length) {
        throw new Exception();
      }
      
      ComponentNumFields[FileIndex] = new int[NumParts];
      for (int i = 0; i < NumParts; i++) {
        ComponentNumFields[FileIndex][i] = ComponentFieldNames[FileIndex][i].length;
      }
      
      TotalNumberOfFields = 0;
      for (int p = 0; p < NumParts; p++) {
        
        TotalNumberOfFields += ComponentNumFields[FileIndex][p] * ComponentNumRepetitions[FileIndex][p];
      }
      
      FieldSizes = new int[TotalNumberOfFields];
      FieldNames = new String[TotalNumberOfFields];
      int TotalSize = 0;
      
      int index = 0;
      for (int p = 0; p < NumParts; p++) {
        
        if (NestingMode[FileIndex].equals("normal")) {
          for (int r = 0; r < ComponentNumRepetitions[FileIndex][p]; r++) {
            for (int f = 0; f < ComponentNumFields[FileIndex][p]; f++) {
              FieldSizes[index] = ComponentFieldSizes[FileIndex][p][f];
              TotalSize += ComponentFieldSizes[FileIndex][p][f];
              if (ComponentNumRepetitions[FileIndex][p] == 1)
                FieldNames[index] = ComponentFieldNames[FileIndex][p][f];
              else
                FieldNames[index] = ComponentFieldNames[FileIndex][p][f] + "_" + (r+1);
              index++;
            }
          }
        } else {
          for (int f = 0; f < ComponentNumFields[FileIndex][p]; f++) {
            for (int r = 0; r < ComponentNumRepetitions[FileIndex][p]; r++) {
              FieldSizes[index] = ComponentFieldSizes[FileIndex][p][f];
              TotalSize += ComponentFieldSizes[FileIndex][p][f];
              if (ComponentNumRepetitions[FileIndex][p] == 1)
                FieldNames[index] = ComponentFieldNames[FileIndex][p][f];
              else
                FieldNames[index] = ComponentFieldNames[FileIndex][p][f] + "_" + (r+1);
              index++;
            }
          }
        }
      }
      
      
      System.out.println("TotalSize = " + TotalSize);
      
      //BufferedReader input = null;
      //GZIPInputStream gzipInputStream = null;
      BufferedInputStream bufferedInputStream = null;
      
      
      if (reportRecordStructrure) {
        int byteIndex = 0;
        for (int i = 0; i < TotalNumberOfFields; i++) {
          System.out.println("Field #" + (i + 1) + "  Byte #" + (byteIndex + 1) + "  Name = " + FieldNames[i] + "  Size = " + FieldSizes[i]);
          byteIndex += FieldSizes[i];
        }
        
      }
      
      
      
      if (bufferedInputStream == null) {
        
        try {
          if (CompressionMode[FileIndex].equals("none")) {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(GroupFileNames[FileIndex]), BufferSize);
          }
          if (CompressionMode[FileIndex].equals("zip")) {
            ZipFile zipFile = new ZipFile(GroupFileNames[FileIndex]);
            Enumeration e = zipFile.entries();
            ZipEntry zipEntry = (ZipEntry) e.nextElement();
            bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry), BufferSize);
          }
          if (CompressionMode[FileIndex].equals("gzip")) {
            //bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(GroupFileNames[FileIndex]))));
            bufferedInputStream = new BufferedInputStream(new GZIPInputStream(new FileInputStream(GroupFileNames[FileIndex])), BufferSize);
          }
          
        } catch (Exception e) {
          System.out.println("couldn't open file: " + GroupFileNames[FileIndex]);
          throw e;
        }
      }
      
      int ByteValue = 0;
      
      long ByteIndex = 0;
      int FieldIndex = 0;
      int RecordIndex = 0;
      
      //int focusFieldIndex = keyFieldNumber - 1;
      
      long masterSum = 0;
      int numKeyRepeats = 0;
      boolean EOF = false;
      while (true) {
        
        
        // allocate and clear buffer for reading field
        byte[] ByteBuffer = new byte[FieldSizes[FieldIndex]];
        for (int i = 0; i < FieldSizes[FieldIndex]; i++) {
          ByteBuffer[i] = 64;  // fill with blank
        }
        
        // read field
        int FieldByteIndex = 0;
        // read each byte in field
        while (true) {
          
          int byteInt;
          try {
            //byteInt = bufferedReader.read();
            byteInt = bufferedInputStream.read();
            
            if (byteInt == -1) {
              System.out.println("EOF");
              EOF = true;
              break;
            }
          } catch (Exception e) {
            System.out.println("EOF");
            EOF = true;
            break;
            //throw e;
          }
          
          masterSum += byteInt;
          
          ByteIndex++;
          
          
          //System.out.println(ByteIndex + "," + byteInt);
          
          if (FixedFormat[FileIndex]) {
            
            
            ByteBuffer[FieldByteIndex] = (byte) byteInt;
            FieldByteIndex++;
            
            // check for fixed format end of field
            if (FieldByteIndex == FieldSizes[FieldIndex]) {
              break;
            }
          } else {
            //check for end of field hexB0
            if (byteInt == 176) {
              break;
            }
            
            if (FieldByteIndex == ByteBuffer.length) {
              System.out.println("Error!   FieldByteIndex == ByteBuffer.length ");
              String ByteString = new String(ByteBuffer, 0, ByteBuffer.length, "cp037");
              System.out.println((RecordIndex + 1) + "," + (FieldIndex + 1) + "," + FieldNames[FieldIndex] + "," + ByteString);
            }
            
            
            ByteBuffer[FieldByteIndex] = (byte) byteInt;
            FieldByteIndex++;
          }
          
          
        }
        
        if (EOF) {
          break;
        }
        
        
        if (FieldIndex == KeyFieldIndex) {
            String ByteString = new String(ByteBuffer, 0, ByteBuffer.length, "cp037");
            System.out.println(ByteString);
      }
            
        //System.out.println("Field # " + (FieldIndex + 1));
        
//        if (FieldNames[FieldIndex].indexOf(KeyFeatureName) != -1) {
//          if (reportEachRecord || ((RecordIndex + 1) % ReportInterval == 0)) {
//            String ByteString = new String(ByteBuffer, 0, ByteBuffer.length, "cp037");
//            System.out.println("file # " + (FileIndex + 1) + "," + "record # " + (RecordIndex + 1) + ","  + "field # " + (FieldIndex + 1) + " Byte# " + (ByteIndex + 1) +
//            "," + FieldNames[FieldIndex] + "," + ByteString);
//            System.out.println(ByteString);
//          }
//        }
        
        FieldIndex++;
        
        if (FieldIndex == TotalNumberOfFields) {
          FieldIndex = 0;
          RecordIndex++;
          
          if (/*(GroupIndex != 0) && */ (maxNumRecordsToRead != -1) && RecordIndex >= maxNumRecordsToRead) {
            break;
          }
          // on end of file, break out of the while loop
          if (bufferedInputStream.available() == 0) {
            break;
          }
          
        }
        
      }
      bufferedInputStream.close();
      System.out.println("final ByteIndex = " + ByteIndex);
      System.out.println("final RecordIndex = " + RecordIndex);
      System.out.println("masterSum = " + masterSum);
      
      this.pushOutput(Hashtable, 0);
    }
  }
}



