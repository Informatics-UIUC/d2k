package ncsa.d2k.modules.projects.dtcheng.ssa;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.Hashtable;
import java.util.zip.*;
import java.util.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;

public class ReadSSIFiles extends InputModule {
  
  
  public String getModuleName() {
    return "ReadSSAFiles";
  }
  
  public String getModuleInfo() {
    return "ReadSSAFiles";
  }
  
  
  private String KeyFeatureName = "none";
  
  public void setKeyFeatureName(String value) {
    this.KeyFeatureName = value;
  }
  
  public String getKeyFeatureName() {
    return this.KeyFeatureName;
  }
  
  
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
  
  public void setBeginFileNumber(int value) {
    this.BeginFileNumber = value;
  }
  
  public int getBeginFileNumber() {
    return this.BeginFileNumber;
  }
  
  private int EndFileNumber = 10;
  
  public void setEndFileNumber(int value) {
    this.EndFileNumber = value;
  }
  
  public int getEndFileNumber() {
    return this.EndFileNumber;
  }
  
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
  
  
  
  /*
  String [] GroupFileNames = {
    
    "C:/data/ssa/do410x.txt.gz",
    
    "C:/data/ssa/wwttw01.txt.gz",
    "C:/data/ssa/wwttw02.txt.gz",
    "C:/data/ssa/wwttw03.txt.gz",
    "C:/data/ssa/wwttw04.txt.gz",
    "C:/data/ssa/wwttw05.txt.gz",
    "C:/data/ssa/wwttw06.txt.gz",
    "C:/data/ssa/wwttw07.txt.gz",
    "C:/data/ssa/wwttw08.txt.gz",
    "C:/data/ssa/wwttw09.txt.gz",
    "C:/data/ssa/wwttw10.txt.gz",
    "C:/data/ssa/wwttw11.txt.gz",
    "C:/data/ssa/wwttw12.txt.gz",
    "C:/data/ssa/wwttw13.txt.gz",
    "C:/data/ssa/wwttw14.txt.gz",
    
    "C:/data/ssa/group01.txt.gz",
    "C:/data/ssa/group02.txt.gz",
    "C:/data/ssa/group03.txt.gz",
    "C:/data/ssa/group04.txt.gz",
    "C:/data/ssa/group05.txt.gz",
    "C:/data/ssa/group06.txt.gz",
    "C:/data/ssa/group07.txt.gz",
    "C:/data/ssa/group08.txt.gz",
    "C:/data/ssa/group09.txt.gz",
    "C:/data/ssa/group10.txt.gz",
  };
  
  */
  
  
  String [] GroupFileNames = {
    
    "C:/data/ssa/do410x.txt.gz",
    
    "C:/data/ssa/wwttw01.txt.gz",
    "C:/data/ssa/wwttw02.txt.gz",
    "C:/data/ssa/wwttw03.txt.gz",
    "C:/data/ssa/wwttw04.txt.gz",
    "C:/data/ssa/wwttw05.txt.gz",
    "C:/data/ssa/wwttw06.txt.gz",
    "C:/data/ssa/wwttw07.txt.gz",
    "C:/data/ssa/wwttw08.txt.gz",
    "C:/data/ssa/wwttw09.txt.gz",
    "C:/data/ssa/wwttw10.txt.gz",
    "C:/data/ssa/wwttw11.txt.gz",
    "C:/data/ssa/wwttw12.txt.gz",
    "C:/data/ssa/wwttw13.txt.gz",
    "C:/data/ssa/wwttw14.txt.gz",
    
    "C:/data/ssa/group01.txt.gz",
    "C:/data/ssa/group02.txt.gz",
    "C:/data/ssa/group03.txt.gz",
    "C:/data/ssa/group04.txt.gz",
    "C:/data/ssa/group05.txt.gz",
    "C:/data/ssa/group06.txt.gz",
    "C:/data/ssa/group07.txt.gz",
    "C:/data/ssa/group08.txt.gz",
    "C:/data/ssa/group09.txt.gz",
    "C:/data/ssa/group10.txt.gz",
  };
  
  
  int NumFiles = GroupFileNames.length;
  
  String [] NestingMode = {
    
    "normal",
    
    "normal",
    "normal",
    "normal",
    "normal",
    "normal",
    "normal",
    "normal",
    "normal",
    "normal",
    "normal",
    "normal",
    "normal",
    "normal",
    "normal",
    
    "abnormal",
    "abnormal",
    "abnormal",
    "abnormal",
    "abnormal",
    "abnormal",
    "abnormal",
    "abnormal",
    "abnormal",
    "abnormal",
  };
  
  String [] CompressionMode = {
    
    "gzip",
    
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    "gzip",
    
  };
  
  String [] FileDescriptions = {
    
    "Disabled Beneficiaries and Dependents MBR File (DBADMBR)",
    
    "SSI Longitudinal File #1",
    "SSI Longitudinal File #2",
    "SSI Longitudinal File #3",
    "SSI Longitudinal File #4",
    "SSI Longitudinal File #5",
    "SSI Longitudinal File #6",
    "SSI Longitudinal File #7",
    "SSI Longitudinal File #8",
    "SSI Longitudinal File #9",
    "SSI Longitudinal File #10",
    "SSI Longitudinal File #11",
    "SSI Longitudinal File #12",
    "SSI Longitudinal File #13",
    "SSI Longitudinal File #14",
    
    "Group01 Fixed Header/Person Data",
    "Group02 Disability Claim Data",
    "Group03 Earnings",
    "Group04 Ticket To Work",
    "Group05 T2 Medical Data",
    "Group06 T16 Medical",
    "Group07 T16 Child",
    "Group08 T2 Work CDR",
    "Group09 Expedited Reinstatement",
    "Group10 Development Worksheet",
  };
  
  
  boolean [] FixedFormat = {
    
    true,
    
    true,
    true,
    true,
    true,
    true,
    true,
    true,
    true,
    true,
    true,
    true,
    true,
    true,
    true,
    
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
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
     
     // SSI File 1
     {1, 504},
     // SSI File 2
     {1, 367},
     // SSI File 3
     {1, 504},
     // SSI File 4
     {1, 367},
     // SSI File 5
     {1, 504},
     // SSI File 6
     {1, 367},
     // SSI File 7
     {1, 504},
     // SSI File 8
     {1, 504},
     // SSI File 9
     {1, 504},
     // SSI File 10
     {1, 367},
     // SSI File 11
     {1, 504},
     // SSI File 12
     {1, 367},
     // SSI File 13
     {1, 504},
     // SSI File 14
     {1, 367},
     
     // Group01
     {1, 1, 1},
     // Group02
     {1, 1, 99},
     // Group03
     {1, 1, 180},
     // Group04
     {1, 1, 30, 1, 300},
     // Group05
     {1, 1, 99, 1, 300},
     // Group06
     {1, 1, 99, 1, 300},
     // Group07
     {1, 1, 99, 1, 300},
     // Group08
     {1, 1, 99, 1, 300},
     // Group09
     {1, 1, 99, 1, 300},
     // Group10
     {1, 1, 100},
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
    
    // SSI File 01
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "NOE",
        "FILLER5",
      },
      
      {
        "CMTH-RD",
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "CUR-COMP",
        "FEDPMT",
        "STATPMT",
        "PAY-STATBC-IND",
        
      },
      
    },
    // SSI File 02
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "FILLER5",
      },
      
      {
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "FEDPMT",
        "STATPMT",
      },
      
    },
    // SSI File 03
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "NOE",
        "FILLER5",
      },
      
      {
        "CMTH-RD",
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "CUR-COMP",
        "FEDPMT",
        "STATPMT",
        "PAY-STATBC-IND",
        
      },
      
    },
    // SSI File 04
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "FILLER5",
      },
      
      {
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "FEDPMT",
        "STATPMT",
      },
      
    },
    // SSI File 05
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "NOE",
        "FILLER5",
      },
      
      {
        "CMTH-RD",
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "CUR-COMP",
        "FEDPMT",
        "STATPMT",
        "PAY-STATBC-IND",
        
      },
      
    },
    // SSI File 06
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "FILLER5",
      },
      
      {
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "FEDPMT",
        "STATPMT",
      },
      
    },
    // SSI File 07
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "NOE",
        "FILLER5",
      },
      
      {
        "CMTH-RD",
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "CUR-COMP",
        "FEDPMT",
        "STATPMT",
        "PAY-STATBC-IND",
        
      },
      
    },
    // SSI File 08
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "NOE",
        "FILLER5",
      },
      
      {
        "CMTH-RD",
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "CUR-COMP",
        "FEDPMT",
        "STATPMT",
        "PAY-STATBC-IND",
        
      },
      
    },
    // SSI File 09
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "NOE",
        "FILLER5",
      },
      
      {
        "CMTH-RD",
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "CUR-COMP",
        "FEDPMT",
        "STATPMT",
        "PAY-STATBC-IND",
        
      },
      
    },
    // SSI File 10
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "FILLER5",
      },
      
      {
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "FEDPMT",
        "STATPMT",
      },
      
    },
    // SSI File 11
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "NOE",
        "FILLER5",
      },
      
      {
        "CMTH-RD",
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "CUR-COMP",
        "FEDPMT",
        "STATPMT",
        "PAY-STATBC-IND",
        
      },
      
    },
    // SSI File 12
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "FILLER5",
      },
      
      {
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "FEDPMT",
        "STATPMT",
      },
      
    },
    // SSI File 13
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "NOE",
        "FILLER5",
      },
      
      {
        "CMTH-RD",
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "CUR-COMP",
        "FEDPMT",
        "STATPMT",
        "PAY-STATBC-IND",
        
      },
      
    },
    // SSI File 14
    {
      {
        "HUN",  // encrypted
        "PAN",
        "TOA",
        "COMP-STAT-TOA",
        "MFT",
        "START-RD",
        "FILLER1",
        "AP-TYPE",
        "RCD-EST-JD",
        "BIRTH-JD",
        "DEATH-JD",
        "LAF",
        "CURSTAT",
        "SEX",
        "RACE",
        "ELG-RD",
        "APPL-JD",
        "8080-JD",
        "X-MITNG-DO",
        "T8VET",
        "DENCDE",
        "DENIAL-JD",
        "FILLER2",
        "CLM-FIL-JD",
        "DISPAYCDE",
        "STAG-FLD-JD",
        "FILLER3",
        "START-PREDIB-RD",
        "DIB-DIG",
        "DIB-DIG2",
        "DIB-MDR",
        "DIB-DPM",
        "PDSCC",
        "PDZIP",
        "PDZIP6-9",
        "DO",
        "REP-PAY-JD",
        "REPPAYTYP",
        "REPCUS",
        "AAZIP",
        "AAZIP6-9",
        "STALE-RCD-IND",
        "NOP-9",
        "SEL-DATE",
        "FIRST-PAY-DTE",
        "STCOCNV",
        "REP-GC",
        "FILLER4",
        "STOP-RD",
        "LANG-PREF-WRITTEN",
        "REV-JD",
        "FILLER5",
      },
      
      {
        "PSTAT",
        "LIVF",
        "STCONCATM",
        "TKT-STAT-IND",
        "EINCM",
        "UINCM",
        "FEDAMT",
        "SUPAMT",
        "MEDTEST",
        "FEDPMT",
        "STATPMT",
      },
      
    },
    
    
    
    
    
    // Group01
    {
      
      {
        "CLM-SEL-DATE",
        "CLM-FIXED-COSSN",
        "CLM-FIXED-NOSSNID",
        "CLM-FIXED-TITLE",
        "CLM-MULTI-T2-SW",
        "DCF-ORES-GRP",
      },
      
      {
        "CDR-VERSION-NUM",
        "FIXED-CONV-IND",
        "FIXED-LOCK",
        "FIXED-DEATH-CD",
        "FIXED-DELN-CD",
        "FIXED-DELN-DT",
        "FIXED-DWS-TKL-DT",
        "FIXED-LAST-CHG-DT",
        "FIXED-LAST-CHG-BY",
        "FIXED-OL-CHG-IND",
        "FIXED-SYS-ACTN-DT",
        "FIXED-LAST-BTCH-DT",
        "FIXED-ERNGS-SW",
        "FIXED-TKT-STUS",
        "FIXED-TKT-STUS-DT",
        "FIXED-TKT-SLTD-DT",
        "FIXED-TKT-ALERT",
        "FIXED-TKT-TITLE",
        "FIXED-TKT-PMT-CTR",
        "FIXED-DUAL-ENT-SW",
        "FIXED-DEMO-IND",
      },
      
      {
        "PRSN-FNM",
        "PRSN-MNM",
        "PRSN-LNM",
        "PRSN-SFX",
        "PRSN-DOB",
        "PRSN-DOD",
        "PRSN-BLND-DT",
        "PRSN-NDDSS-DT",
        "PRSN-FLDR-RECON-IND",
        "PRSN-OD-DIARY-MNTH",
        "PRSN-OD-DIG",
        "PRSN-OD-SDIG",
        "PRSN-OD-DPM",
        "PRSN-OD-MDR",
        "PRSN-INTGT-CD",
        "PRSN-INTGT-DT",
        "PRSN-BTN-ALT",
        "PRSN-BTC",
      },
      
    },
    
    // Group02
    {
      
      {
        "CLM-SEL-DATE",
        "CLM-FIXED-COSSN",
        "CLM-FIXED-NOSSNID",
        "CLM-FIXED-TITLE",
        "CLM-MULTI-T2-SW",
        "DCF-ORES-GRP",
      },
      
      {
        "CLAIM-NOE",
      },
      
      {
        "CLM-EFD-MNTH",
        "CLM-ADJ-DT",
        "CLM-ONSET-DT",
        "CLM-TITLE",
        "CLM-CLMSSN",
        "CLM-SSNID",
        "CLM-EXR-MNTH",
        "CLM-EXR-DENL-SW",
        "CLM-DIB-ENT-MNTH",
        "CLM-DENIAL-RSN",
        "CLM-TERM-MNTH",
        "CLM-TERM-RSN",
        "CLM-TWP-CMPL-MNTH",
        "CLM-PENDING-SW",
      },
      
    },
    
    // Group03
    {
      
      {
        "CLM-SEL-DATE",
        "CLM-FIXED-COSSN",
        "CLM-FIXED-NOSSNID",
        "CLM-FIXED-TITLE",
        "CLM-MULTI-T2-SW",
        "DCF-ORES-GRP",
      },
      
      {
        "ERNGS-MONTHS-NOE",
        
      },
      {
        "ERNGS-MNTH",
        "ERNGS-ALLGD-AMT",
        "T16-ERNGS-GRS-AMT",
        "T16-ERNGS-VRFD-SW",
        "T16-ERNGS-WRK-EXP-AMT",
        "T16-ERNGS-PASS-AMT",
        "T16-ERNGS-STUD-EXCL-AMT",
        "T16-ERNGS-SE-NET-AMT",
        "T16-ERNGS-SE VRFD-SW",
        "T2-ERNGS-GRS-AMT",
        "T2-ERNGS-VRFD-DATA",
        "T2-ERNGS-WRK-EXP-AMT",
        "T2-ERNGS-SBDY-AMT",
        "T2-ERNGS-SPCL-CDN-AMT",
        "T2-ERNGS-SE-NET-AMT",
        "T2-ERNGS-SE-UBE-AMT",
        "T2-ERNGS-SE-HRS",
        "T2-ERNGS-SE VRFD-SW",
        "ERNGS-LAST-UPD-DATE",
        "T2-ERNGS-TWP-Data",
        
      },
    },
    // Group04
    {
      
      {
        "CLM-SEL-DATE",
        "CLM-FIXED-COSSN",
        "CLM-FIXED-NOSSNID",
        "CLM-FIXED-TITLE",
        "CLM-MULTI-T2-SW",
        "DCF-ORES-GRP",
      },
      {
        "TKT-NOE",
      },
      {
        "TKT-NUMBER",
        "TKT-PM-NUM",
        "TKT-MAILED-DT",
        "TKT-CNTRCT-DT",
        "TKT-ASSGND-DT",
        "TKT-UNASSGND-DT",
        "TKT-TERMD-DT",
        "TKT-TERM-RSN",
        "TKT-EIN",
        "TKT-PMT-TYPE",
        "TKT-CLAIM-ITER-NO",
        
      },
      {
        "TKT-MONTHS-NOE",
      },
      {
        //"DER-TKT-ITER-NO",
        "TKT-MONTH",
        "TKT-INUSE-SW",
        "TKT-CLOCK-STOP-SW",
        "TKT-TITLE",
        "TKT-T2-CUR-PAY-SW",
        "TKT-T2-ERNGS-SUSP-SW",
        "TKT-T16-FPMO-SW",
        "TKT-T16-ERNGS-SUSP-SW",
        "TKT-PAID",
        "TKT-WARNING-SW",
        "TKT-DIB-TRUST-FUND-SW",
        "TKT-T2-ERNGS-TERMD-SW",
        "TKT-T16-ERNGS-TERMD-SW",
        
      },
    },
    // Group05
    {
      
      {
        "CLM-SEL-DATE",
        "CLM-FIXED-COSSN",
        "CLM-FIXED-NOSSNID",
        "CLM-FIXED-TITLE",
        "CLM-MULTI-T2-SW",
        "DCF-ORES-GRP",
      },
      
      {
        "T2MED-NOE",
      },
      
      {
        "T2MED-CLM-ITER-NO",
        "T2MED-ORB1",
        "T2MED-ORB2",
        "T2MED-STDT",
        "T2MED-ENDT",
        "T2MED-DIG",
        "T2MED-SDIG",
        "T2MED-DEC-DT",
        "T2MED-MRED-MNTH",
        "T2MED-DPM",
        "T2MED-MED-DRY-RSN",
        "T2MED-LISTNG-CD",
        "T2MED-VOCL-RULE",
        "T2MED-RBC",
        "T2MED-DDS-CDT",
        "T2MED-DOM1",
        "T2MED-DOM2",
        "T2MED-MAILER-SW",
        "T2MED-DR-INIT-DT",
        "T2MED-DFR-DT",
        "T2MED-RPT-MNTH",
        "T2MED-SSNX",
        "T2MED-APPEAL",
        "T2MED-RECON",
        "T2MED-ALJAC",
        "T2MED-BENCON",
        "T2MED-ELECT-RECON",
        "T2MED-ELECT-ALJ",
      },
      
      {
        "T2MED-DATA-NOE",
      },
      
      {
        "T2MED-EVNT-CD",
        "T2MED-DT",
        "T2MED-DEST",
        "T2MED-RSN-CD",
        "T2MED-APL-DT",
        "T2MED-APL-CD",
        "T2MED-FLDR-REQ",
        "T2MED-TITLE-IND",
        "T2MED-ITER-NO",
        
      },
    },
    // Group06
    {
      
      {
        "CLM-SEL-DATE",
        "CLM-FIXED-COSSN",
        "CLM-FIXED-NOSSNID",
        "CLM-FIXED-TITLE",
        "CLM-MULTI-T2-SW",
        "DCF-ORES-GRP",
      },
      
      {
        "MED-NOE",
      },
      
      {
        "MED-CLM-ITER-NO",
        "MED-ORB1",
        "MED-ORB2",
        "MED-STDT",
        "MED-ENDT",
        "MED-DIG",
        "MED-SDIG",
        "MED-DEC-DT",
        "MED-MRED-MNTH",
        "MED-DPM",
        "MED-MED-DRY-RSN",
        "MED-LISTING-CD",
        "MED-VOCL-RULE",
        "MED-RBC",
        "MED-DDS-CDT",
        "MED-DOM1",
        "MED-DOM2",
        "MED-MAILER-SW",
        "MED-DR-INIT-DT",
        "MED-FLDR-LOC-CODE",
        "MED-FLDR-STAT",
        "MED-DFR-DT",
        "MED-RPT-MNTH",
        "MED-APPEAL",
        "MED-RECON",
        "MED-ALJAC",
        "MED-BENCON",
        "MED-ELECT-RECON",
        "MED-ELECT-ALJ",
        
      },
      
      {
        "MED-DATA-NOE",
      },
      
      {
        "MED-EVNT-CD",
        "MED-DT",
        "MED-DEST",
        "MED-RSN-CD",
        "MED-APL-DT",
        "MED-APL-CD",
        "MED-FLDR-REQ",
        "TITLE-IND",
        "MED-ITER-NO",
        
      },
    },
    // Group07
    {
      
      {
        "CLM-SEL-DATE",
        "CLM-FIXED-COSSN",
        "CLM-FIXED-NOSSNID",
        "CLM-FIXED-TITLE",
        "CLM-MULTI-T2-SW",
        "DCF-ORES-GRP",
      },
      
      {
        "CHLD-NOE",
      },
      
      {
        "CHLD-CLM-ITER-NO",
        "CHLD-ORB1",
        "CHLD-ORB2",
        "CHLD-STDT",
        "CHLD-ENDT",
        "CHLD-DIG",
        "CHLD-SDIG",
        "CHLD-DEC-DT",
        "CHLD-MRED-MNTH",
        "CHLD-DPM",
        "CHLD-MED-DRY-RSN",
        "CHLD-LISTNG-CD",
        "CHLD-VOCL-RULE",
        "CHLD-RBC",
        "CHLD-DDS-CDT",
        "CHLD-DOM1",
        "CHLD-DOM2",
        "CHLD-MAILER-SW",
        "CHLD-DR-INIT-DT",
        "CHLD-FLDR-LOC-CODE",
        "CHLD-FLDR-STAT",
        "CHLD-DFR-DT",
        "CHLD-RPT-MNTH",
        "CHLD-APPEAL",
        "CHLD-RECON",
        "CHLD-ALJAC",
        "CHLD-BENCON",
        "CHLD-ELECT-RECON",
        "CHLD-ELECT-ALJ",
        
      },
      
      {
        "CHLD-DATA-NOE",
      },
      
      {
        "CHLD-EVNT-CD",
        "CHLD-DT",
        "CHLD-DEST",
        "CHLD-RSN-CD",
        "CHLD-APL-DT",
        "CHLD-APL-CD",
        "CHLD-FLDR-REQ",
        "CHLD-TITLE-IND",
        "CHLD-ITER-NO",
        
      },
    },
    // Group08
    {
      {
        "CLM-SEL-DATE",
        "CLM-FIXED-COSSN",
        "CLM-FIXED-NOSSNID",
        "CLM-FIXED-TITLE",
        "CLM-MULTI-T2-SW",
        "DCF-ORES-GRP",
      },
      
      {
        "WORK-NOE",
      },
      
      {
        "WORK-CLM-ITER-NO",
        "WORK-ORB1",
        "WORK-ORB2",
        "WORK-STDT",
        "WORK-ENDT",
        "WORK-DEC-DT",
        "WORK-ENFORCE-AMT",
        "WORK-FLDR-LOC-CODE",
        "WORK-FLDR-STAT",
        "WORK-FIRST-MNTH",
        "WORK-LAST-MNTH",
        "WORK-SSNX",
        "WORK-APPEAL",
        "WORK-RECON",
        "WORK-ALJAC",
        "WORK-BENCON",
        "WORK-ELECT-RECON",
        "WORK-ELECT-ALJ",
      },
      
      {
        "WORK-DATA-NOE",
      },
      
      {
        "WORK-EVNT-CD",
        "WORK-DT",
        "WORK-DEST",
        "WORK-RSN-CD",
        "WORK-APL-DT",
        "WORK-APL-CD",
        "WORK-FLDR-REQ",
        "WORK-TITLE-IND",
        "WORK-ITER-NO",
      },
    },
    // Group09
    {
      
      {
        "CLM-SEL-DATE",
        "CLM-FIXED-COSSN",
        "CLM-FIXED-NOSSNID",
        "CLM-FIXED-TITLE",
        "CLM-MULTI-T2-SW",
        "DCF-ORES-GRP",
      },
      
      {
        "EXR-NOE",
      },
      
      {
        "EXR-CLM-ITER-NO",
        "EXR-ORB1",
        "EXR-ORB2",
        "EXR-STDT",
        "EXR-ENDT",
        "EXR-DIG",
        "EXR-SDIG",
        "EXR-DEC-DT",
        "EXR-MRED-MNTH",
        "EXR-DPM",
        "EXR-MED-DRY-RSN",
        "EXR-LISTG-CD",
        "EXR-VOCL-RULE",
        "EXR-RBC",
        "EXR-DDS-CDT",
        "EXR-FLDR-LOC-CODE",
        "EXR-FLDR-STAT",
        "EXR-SSNX",
        "EXR-APPEAL",
        "EXR-RECON",
        "EXR-ALJAC",
        "EXR-BENCON",
        "EXR-ELECT-RECON",
        "EXR-ELECT-ALJ",
        
      },
      
      {
        "CDR-EXR-DATA-NOE",
      },
      
      {
        "EXR-EVNT-CD",
        "EXR-DT",
        "CDR-EXR-DEST",
        "EXR-RSN-CD",
        "EXR-APL-DT",
        "EXR-APL-CD",
        "EXR-TITLE-IND",
        "EXR-ITER-NO",
      },
    },
    // Group10
    {
      
      {
        "CLM-SEL-DATE",
        "CLM-FIXED-COSSN",
        "CLM-FIXED-NOSSNID",
        "CLM-FIXED-TITLE",
        "CLM-MULTI-T2-SW",
        "DCF-ORES-GRP",
      },
      
      {
        "DWS-NOE",
      },
      
      {
        "DWS-LOC",
        "DWS-TYPE",
        "DWS-ISSUE",
        "DWS-RQST-DT",
        "DWS-FUP1-DT",
        "DWS-FUP2-DT",
        "DWS-TKL-DT",
        "DWS-CLSD-DT",
        
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
    
    // SSI File 01
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        3,
        21,
      },
      {
        6,
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        1,
        6,
        6,
        1,
      },
    },
    // SSI File 02
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        24,
      },
      {
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        5,
        5,
        1,
      },
    },
    // SSI File 03
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        3,
        21,
      },
      {
        6,
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        1,
        6,
        6,
        1,
      },
    },
    // SSI File 04
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        24,
      },
      {
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        5,
        5,
        1,
      },
    },
    // SSI File 05
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        3,
        21,
      },
      {
        6,
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        1,
        6,
        6,
        1,
      },
    },
    // SSI File 06
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        24,
      },
      {
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        5,
        5,
        1,
      },
    },
    // SSI File 07
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        3,
        21,
      },
      {
        6,
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        1,
        6,
        6,
        1,
      },
    },
    // SSI File 08
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        3,
        21,
      },
      {
        6,
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        1,
        6,
        6,
        1,
      },
    },
    // SSI File 09
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        3,
        21,
      },
      {
        6,
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        1,
        6,
        6,
        1,
      },
    },
    // SSI File 10
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        24,
      },
      {
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        5,
        5,
        1,
      },
    },
    // SSI File 11
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        3,
        21,
      },
      {
        6,
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        1,
        6,
        6,
        1,
      },
    },
    // SSI File 12
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        24,
      },
      {
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        5,
        5,
        1,
      },
    },
    // SSI File 13
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        3,
        21,
      },
      {
        6,
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        1,
        6,
        6,
        1,
      },
    },
    // SSI File 14
    {
      {
        9,
        9,
        2,
        2,
        2,
        6,
        9,
        1,
        8,
        8,
        8,
        2,
        3,
        1,
        1,
        6,
        8,
        8,
        3,
        1,
        3,
        8,
        3,
        8,
        1,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        6,
        5,
        4,
        3,
        8,
        3,
        3,
        5,
        4,
        1,
        3,
        6,
        8,
        5,
        1,
        6,
        6,
        2,
        8,
        24,
      },
      {
        3,
        1,
        1,
        1,
        4,
        4,
        3,
        3,
        1,
        5,
        5,
        1,
      },
    },
    
    
    // Group01
    {
      
      {
        8,
        9,
        2,
        1,
        1,
        2,
      },
      
      {
        2,
        1,
        12,
        1,
        1,
        8,
        8,
        8,
        6,
        1,
        8,
        8,
        1,
        1,
        8,
        8,
        1,
        1,
        2,
        1,
        2,
        
      },
      
      {
        15,
        15,
        20,
        4,
        8,
        8,
        8,
        8,
        1,
        6,
        4,
        4,
        1,
        1,
        2,
        8,
        13,
        1,
        
      },
      
    },
    
    // Group02
    {
      
      {
        8,
        9,
        2,
        1,
        1,
        2,
      },
      
      {
        4,
      },
      
      {
        6,
        8,
        8,
        1,
        9,
        2,
        6,
        1,
        6,
        3,
        6,
        6,
        6,
        1,
        
      },
    },
    
    // Group03
    {
      
      {
        8,
        9,
        2,
        1,
        1,
        2,
      },
      
      {
        4,
      },
      
      {
        6,
        7,
        7,
        1,
        7,
        7,
        7,
        8, // !!! documentations says 7 but needs to be at least 8 ???
        1,
        7,
        1,
        7,
        7,
        7,
        8, // !!! documentations says 7 but needs to be at least 8 ???
        7,
        5, // !!! documentations says 3 but needs to be at least 5 ???
        1,
        8,
        1,
      },
    },
    
    // Group04
    {
      
      {
        8,
        9,
        2,
        1,
        1,
        2,
      },
      
      {
        4,
      },
      
      {
        1,
        1,
        8,
        8,
        8,
        8,
        8,
        1,
        9,
        1,
        2,
        
      },
      
      {
        4,
      },
      
      {
        8,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
      },
    },
    
    // Group05
    {
      
      {
        8,
        9,
        2,
        1,
        1,
        2,
      },
      
      {
        4,
      },
      
      {
        2,
        1,
        1,
        8,
        8,
        4,
        4,
        8,
        6,
        1,
        1,
        7,
        5,
        2,
        2,
        8,
        8,
        1,
        8,
        8,
        6,
        5,
        1,
        1,
        1,
        1,
        1,
        1,
        
      },
      
      {
        4,
      },
      
      {
        3,
        8,
        3,
        2,
        8,
        1,
        1,
        1,
        2,
        
      },
    },
    
    // Group06
    {
      
      {
        8,
        9,
        2,
        1,
        1,
        2,
      },
      
      {
        4,
      },
      
      {
        2,
        1,
        1,
        8,
        8,
        4,
        4,
        8,
        6,
        1,
        1,
        7,
        5,
        2,
        2,
        8,
        8,
        1,
        8,
        3,
        1,
        8,
        6,
        1,
        1,
        1,
        1,
        1,
        1,
        
      },
      
      {
        4,
      },
      
      {
        3,
        8,
        3,
        2,
        8,
        1,
        1,
        1,
        2,
        
      },
    },
    
    // Group07
    {
      
      {
        8,
        9,
        2,
        1,
        1,
        2,
      },
      
      {
        4,
      },
      
      {
        2,
        1,
        1,
        8,
        8,
        4,
        4,
        8,
        6,
        1,
        1,
        7,
        5,
        2,
        2,
        8,
        8,
        1,
        8,
        3,
        1,
        8,
        6,
        1,
        1,
        1,
        1,
        1,
        1,
        
      },
      
      {
        4,
      },
      
      {
        3,
        8,
        3,
        2,
        8,
        1,
        1,
        1,
        2,
        
      },
    },
    
    // Group08
    {
      {
        8,
        9,
        2,
        1,
        1,
        2,
      },
      
      {
        4,
      },
      
      {
        2,
        1,
        1,
        8,
        8,
        8,
        7,
        3,
        1,
        6,
        6,
        5,
        1,
        1,
        1,
        1,
        1,
        1,
      },
      
      {
        4,
      },
      
      {
        3,
        8,
        3,
        2,
        8,
        1,
        1,
        1,
        2,
      },
    },
    
    // Group09
    {
      
      {
        8,
        9,
        2,
        1,
        1,
        2,
      },
      
      {
        4,
      },
      
      {
        2,
        1,
        1,
        8,
        8,
        4,
        4,
        8,
        6,
        1,
        1,
        7,
        5,
        2,
        2,
        3,
        1,
        5,
        1,
        1,
        1,
        1,
        1,
        1,
        
      },
      
      {
        4,
      },
      
      {
        3,
        8,
        3,
        2,
        8,
        1,
        1,
        2,
        
      },
    },
    
    // Group10
    {
      
      {
        8,
        9,
        2,
        1,
        1,
        2,
      },
      
      {
        4,
      },
      
      {
        3,
        1,
        8,
        8,
        8,
        8,
        8,
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
        
        // create key string and test joinability
        /*
        if (false) {
          if (FieldIndex == 1) {
         
            String Key = new String(ByteBuffer, 0, ByteBuffer.length);
            String TranslatedKey = new String(ByteBuffer, 0, ByteBuffer.length, "cp037");
         
            if (FileIndex == 0) {
         
              // put key in ht
              if (!Hashtable.containsKey(Key)) {
         
                //int[] NewValueArray = new int[1];
                //NewValueArray[0] = RecordIndex;
         
                //Hashtable.put(Key, NewValueArray);
                Hashtable.put(Key, StaticObject);
              } else {
                System.out.println("Record# " + (RecordIndex + 1) + " Byte# " + (ByteIndex + 1) + "  repeat key = " + TranslatedKey);
         
              }
            }
         
            else if (FileIndex != 0) {
              // check that key exists in ht
              //int[] ValueArray = (int[]) Hashtable.get(Key);
              if (!Hashtable.containsKey(Key)) {
         
                System.out.println("Record# " + (RecordIndex + 1) +  " Byte# " + (ByteIndex + 1) + "  key not found = " + TranslatedKey);
              } else {
                //System.out.println("key found, record index = " + ValueArray[0]);
              }
            }
          }
        }
         */
        
        
//        if ((RecordIndex + 1) % ReportInterval == 0) {
//          if (FieldIndex == 0) {
//            System.out.println("record # " + (RecordIndex + 1) +  " Byte# " + (ByteIndex + 1) );
//          }
//        }
        
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



