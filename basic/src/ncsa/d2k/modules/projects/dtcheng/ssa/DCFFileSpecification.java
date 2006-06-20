/*
 * DCFileSpecification.java
 *
 * Created on July 30, 2005, 7:12 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ncsa.d2k.modules.projects.dtcheng.ssa;

/**
 *
 * @author dtcheng
 */
public class DCFFileSpecification {
  
  DCFFileSpecification () {
    
  }
    //  descriptions of each of the DCF group files
  int  NumFiles = 10;
  
  String [] MatrixNames = {
     "Group1",
     "Group2",
     "Group3",
     "Group4",
     "Group5",
     "Group6",
     "Group7",
     "Group8",
     "Group9",
     "Group10",
  };
  
  String [] FileDescriptions = {
     "Group1 Fixed Header/Person Data",
     "Group2 Disability Claim Data",
     "Group3 Earnings",
     "Group4 Ticket To Work",
     "Group5 T2 Medical Data",
     "Group6 T16 Medical",
     "Group7 T16 Child",
     "Group8 T2 Work CDR",
     "Group9 Expedited Reinstatement",
     "Group10 Development Worksheet",
  };
  
  int [] NumRecords = {
     (int) 22000616,
     (int)   298410,
     (int)  2077137,
     (int)  9808372,
     (int) 13894837,
     (int) 14833637,
     (int)  1233172,
     (int)   897359,
     (int)     5378,
     (int)   354744,
  };
  
  
  public String pathHeader = "C:/data/ssa/raw/dcf/";
  // String pathHeader = "C:/data/ssa/";
  
  
  
  String [] FilePaths = {
    pathHeader + "group01.txt.gz",
     pathHeader + "group02.txt.gz",
     pathHeader + "group03.txt.gz",
     pathHeader + "group04.txt.gz",
     pathHeader + "group05.txt.gz",
     pathHeader + "group06.txt.gz",
     pathHeader + "group07.txt.gz",
     pathHeader + "group08.txt.gz",
     pathHeader + "group09.txt.gz",
     pathHeader + "group10.txt.gz",
  };
  
  
  String [] NestingMode = {
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
  };
  
  
  
  boolean [] FixedFormat = {
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
  
  
  int personIDFieldIndex = 1;
  
  String [][][] ComponentFieldNames =  {
    
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
  
}
