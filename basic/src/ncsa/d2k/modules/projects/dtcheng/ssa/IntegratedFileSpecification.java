package ncsa.d2k.modules.projects.dtcheng.ssa;

public class IntegratedFileSpecification {
  
  int  NumFiles = 3;
  int  NumGroups = 3;
  int  GroupFileNumber = 1;
  int  GroupFeatureNumber = 3;
  
  
  String [] FileDescriptions = {
    "DCF",
    "MBR",
    "SSI",
  };
  
  int [] InputStartIndices = {
    3,
    1,
    1,
  };
  
  
  
  
  
  String pathHeader = "C:/data/ssa/extracts/";
  
  String [] GroupFileNames = {
    pathHeader + "dcf.sorted.tab.gz",
    pathHeader + "mbr.sorted.tab.gz",
    pathHeader + "ssi.sorted.tab.gz",
  };
  
  
  String [] NestingMode = {
    "normal",
    "normal",
    "normal",
  };
  
  String [] CompressionMode = {
    "gzip",
    "gzip",
    "gzip",
  };
  
  String [][] FieldNames =  {
    
    // DCF
    {
      "key",
      "filler",
      "FIXED-TKT-SLTD-DTAge",
      "FixedTicketTitleIsT2Only?",
      "FixedTicketTitleIsT2AndT16?",
      "FixedTicketTitleIsT16Only?",
      "AgeOfPerson",
      "HasPersonBlindAge?",
      "PersonBlindAge",
      "HasPersonNationalDDSSystemAge?",
      "PersonNationalDDSSystemAge",
      "ClaimNumberOfEntries",
      "EarningsExists?",
      "EarningsMonthsNumberOfEntries",
      "T16EarningsGrossAmount",
      "T2EarningsGrossAmount",
      "T2MedicalExists?",
      "T2MedicalNumberOfEntries",
      "MedicalDataNumberOfEntries",
      "T16MedicalExists?",
      "MedicalNumberOfEntries",
      "MedicalDataNumberOfEntries",
      "T16ChildExists?",
      "ChildhoodNumberOfEntries",
      "ChildhoodDataNumberOfEntries",
      "T2WorkCDRExists?",
      "WorkNumberOfEntries",
      "WorkDataNumberOfEntries",
      "ExpeditedReinstatementExists?",
      "ExpeditedReinstatementNumberOfEntries",
      "ExpeditedReinstatementDataNumberOfEntries",
      "DevelopmentWorksheetExists?",
      "DevelopmentWorksheetNumberOfEntries",
      "class"
    },
    
    // MBR
    {
      "EncryptedKey",
      "SexIsM?",
      "LANGIsBlank?",
      "NPIH",
      "NINSD",
      "NBCLM",
      "NDENY",
      "NDDIB",
      "NDIB",
      "NXRD",
      "NDED",
      "NENFD",
      "NHIST",
      "NDTPD",
      "NHI",
      "NSMI",
      "NDRAM",
      "NCTZN",
    },
    
    // SSI
    {
      "EncryptedKey",
      "RaceIsA?",
      "RaceIsB?",
      "RaceIsH?",
      "RaceIsI?",
      "RaceIsN?",
      "RaceIsW?",
      "RaceIsO?",
      "RaceIsU?",
      "CLM-FIL-JDNotBlank?",
      "AgeOfCLM-FIL-JD",
      "DIB-DIGNotBlank?",
      "DIB-DIGDigit1",
      "DIB-DIGDigit2",
      "DIB-DIGDigit3",
      "DIB-DIGDigit4",
    },
  };
  
  
  
}
