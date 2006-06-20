
package ncsa.d2k.modules.projects.dtcheng.ssa;

public class MBRFileSpecification {
  
  
    //  descriptions of each of the DCF group files
  int  NumFiles = 1;
  
  
  String [] MatrixNames = {
    "MBR"
  };
  
  
  String [] FileDescriptions = {
    "Disabled Beneficiaries and Dependents MBR File (DBADMBR)",
  };
  
  
  int [] NumRecords = {
     (int) 32441199,
  };
  
  public String  pathHeader = "C:/data/ssa/raw/mbr/";
  
  String [] FilePaths = {
    pathHeader + "do410x.txt.gz",
  };
  
  
  String [] NestingMode = {
    "normal",
  };
  
  String [] CompressionMode = {
    "gzip",
  };
  
  
  
  boolean [] FixedFormat = {
    true,
  };
  
  
  int [][] ComponentNumRepetitions = {
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
  
  
  int personIDFieldIndex = 1;
  
  String [][][] ComponentFieldNames =  {
    
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
  
}
