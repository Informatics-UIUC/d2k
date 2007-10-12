package ncsa.d2k.modules.core.io.file.input;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import ncsa.d2k.core.modules.InputModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxyFactory;

public class Nfdump extends InputModule {
    private String cacheDir;
    private DataObjectProxy dataObject;
    private String fileName;

    public void doit() throws Exception {
        if (cacheDir == null || cacheDir.length() == 0) {
            throw new Exception(getAlias() + ": No cache directory was given.");
        }

        dataObject = (DataObjectProxy) pullInput(0);
        fileName = dataObject.getURL().getFile();
        
        // Determine the outFileName, and only run nfdump if the outFileName
        // doesn't yet exist.
        File f = new File(cacheDir + java.io.File.separator + fileName + ".csv");
        String outFileName = f.getCanonicalPath();
        if (!f.isFile()) {
            runNfdump(outFileName);
        }
        
        // Create the output DataObjectProxy.
        URL url = f.toURI().toURL();
        dataObject = DataObjectProxyFactory.getDataObjectProxy(url);
        pushOutput(dataObject, 0);
    }

    private void runNfdump(String outFileName) throws Exception {
        // Make the "dirname" for the output file.
        File f = new File(dirName(outFileName)); 
        f.mkdirs();
        
        // Invoke nfdump.  Stream the dataObject to nfdump's stdin, and parse
        // the lines coming off of stdout.
        Runtime rt = Runtime.getRuntime();
        String cmd = new String();
        cmd = "/usr/local/bin/nfdump -o extended";
        Process p = rt.exec(cmd);
        
        // Stream dataObject to nfdump's stdin.
        NfdumpStdinThread tIn = new NfdumpStdinThread(p, dataObject.getInputStream());
        
        // Stream nfdump's stdout to System.out.
        NfdumpStdoutThread tOut = new NfdumpStdoutThread(p, outFileName);
        
        tIn.start();
        tOut.start();
        
        int e = p.waitFor();
        if (e != 0) {
            throw new Exception(getAlias() + ": nfdump execution failed.");
        }
    }
    
    class NfdumpStdinThread extends Thread {
        private BufferedOutputStream cmdIn;
        private BufferedInputStream dataIn;
        
        NfdumpStdinThread(Process p, InputStream is) {
            cmdIn = new BufferedOutputStream(p.getOutputStream());
            dataIn = new BufferedInputStream(is);
        }
        
        public void run() {
            int b;
            try {
                while ((b = dataIn.read()) != -1) {
                    cmdIn.write(b);
                }
                cmdIn.close();
            }
            catch (Exception e) {
                System.out.println("i");
            }
            System.out.println("NfdumpStdinThread:  Done.");
        }
    }
    
    class NfdumpStdoutThread extends Thread {
        private String outFileName;
        private BufferedReader cmdOut;
        private FileWriter fw;
        private String lineIn;
        private String lineOut;
        private String [] parts;
        private String startYear, startMonth, startDate;
        private String startHour, startMinute, startSecond;
        private String durationSeconds;
        private String proto;
        private String srcIp, srcPort, dstIp, dstPort;
        private String [] flags = new String[6];
        private String typeOfService, packets, bytes;
        private String packetsPerSecond, bitsPerSecond, bytesPerSecond;
        private String flows;
          
        // Parse each lineIn into the desired fields.  Skip the first row, as it
        // is field names.
//        lineIn = cmdOut.readLine();
        
        NfdumpStdoutThread(Process p, String outFileName) {
            cmdOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
            this.outFileName = outFileName;
        }
        
        public void run() {
            try {
                fw = new FileWriter(outFileName);

                // Write out the field label row.
                lineOut = "StartYear,StartMonth,StartDate,StartHour,StartMinute,StartSecond";
                lineOut += ",DurationSeconds,Proto,SrcIP,SrcPort,DstIP,DstPort";
                lineOut += ",Flag1,Flag2,Flag3,Flag4,Flag5,Flag6";
                lineOut += ",TypeOfService,Packets,Bytes,PacketsPerSecond,BitsPerSecond,BytesPerSecond";
                lineOut += ",Flows";
                fw.write(lineOut + "\n");
                // Parse each lineIn into the desired fields.  Skip the first row, as it
                // is field names.
                lineIn = cmdOut.readLine();
                while ((lineIn = cmdOut.readLine()) != null) {
//                for (int n = 1; n <= 8; n++) {
//                    lineIn = cmdOut.readLine();
                    
                    String [] pl = lineIn.split("[\\s]+");

                    // Note that numeric quantities can be like "4.0 G", so
                    // that notwithstanding, we have:
                    // pl[0] => "Date flow start" (1 of 2), like YYYY-MM-DD
                    // pl[1] => "Date flow start" (2 of 2), like HH:MM:SS.ddd
                    // pl[2] => "Duration", float with 3 decimal places.
                    // pl[3] => "Proto", string like TCP, UDP, ICMP, ...
                    // pl[4] => "Src IP Addr:Port", like 123.456.789.012:12345
                    // pl[5] => "->", to be ignored.
                    // pl[6] => "Dst IP Addr:Port", like 123.456.789.012:12345
                    // pl[7] => "Flags", 6 in total, like "......"
                    // pl[8] => "Tos", type of service, 0-255 inclusive
                    // pl[9] => "Packets", integer
                    // pl[10] => "Bytes", integer
                    // pl[11] => "pps", packets-per-second
                    // pl[12] => "bps", bits-per-second
                    // pl[13] => "Bpp", bytes-per-packet
                    // pl[14] => "Flows"

                    // Create/manipulate the desired fields for output.
                    try {
                        int i = 0;
                        Long m;
                        
                        parts = pl[i].split("-");
                        startYear = parts[0];
                        startMonth = parts[1];
                        startDate = parts[2];
                        i++;
                        
                        parts = pl[i].split(":");
                        startHour = parts[0];
                        startMinute = parts[1];
                        startSecond = parts[2];
                        i++;
                        
                        durationSeconds = pl[i];
                        i++;
                        m = checkForMultiplier(pl[i]);
                        durationSeconds = String.format("%.3f", Double.valueOf(durationSeconds) * m);
                        if (m != 1)
                            i++;
                        
                        proto = pl[i];
                        i++;
                        
                        parts = pl[i].split(":");
                        srcIp = parts[0];
                        srcPort = parts[1];
                        i++;
                        
                        // Skip the "->" part.
                        i++;
                        
                        parts = pl[i].split(":");
                        dstIp = parts[0];
                        dstPort = parts[1];
                        i++;
                        
                        for (int j = 0; j < 6; j++) {
                            flags[j] = pl[i].substring(j, j+1);
                        }
                        i++;
                        
                        typeOfService = pl[i];
                        i++;
                        
                        packets = pl[i];
                        i++;
                        m = checkForMultiplier(pl[i]);
                        packets = String.format("%.0f", Double.valueOf(packets) * m);
                        if (m != 1)
                            i++;
                        
                        bytes = pl[i];
                        i++;
                        m = checkForMultiplier(pl[i]);
                        bytes = String.format("%.0f", Double.valueOf(bytes) * m);
                        if (m != 1)
                            i++;

                        packetsPerSecond = pl[i];
                        i++;
                        m = checkForMultiplier(pl[i]);
                        packetsPerSecond = String.format("%.0f", Double.valueOf(packetsPerSecond) * m);
                        if (m != 1)
                            i++;

                        bitsPerSecond = pl[i];
                        i++;
                        m = checkForMultiplier(pl[i]);
                        bitsPerSecond = String.format("%.0f", Double.valueOf(bitsPerSecond) * m);
                        if (m != 1)
                            i++;
                        
                        bytesPerSecond = pl[i];
                        i++;
                        m = checkForMultiplier(pl[i]);
                        bytesPerSecond = String.format("%.0f", Double.valueOf(bytesPerSecond) * m);
                        if (m != 1)
                            i++;

                        flows = pl[i];
                        i++;
                        if (i < pl.length) {
                            m = checkForMultiplier(pl[i]);
                            flows = String.format("%.0f", Double.valueOf(flows) * m);
                            if (m != 1)
                                i++;
                        }
                        
//                        System.out.println("startYear: " + startYear);
//                        System.out.println("startMonth: " + startMonth);
//                        System.out.println("startDate: " + startDate);
//                        System.out.println("startHour: " + startHour);
//                        System.out.println("startMinute: " + startMinute);
//                        System.out.println("startSecond: " + startSecond);
//                        System.out.println("durationSeconds: " + durationSeconds);
//                        System.out.println("proto: " + proto);
//                        System.out.println("srcIp: " + srcIp);
//                        System.out.println("srcPort: " + srcPort);
//                        System.out.println("dstIp: " + dstIp);
//                        System.out.println("dstPort: " + dstPort);
//                        System.out.println("flags: " + flags[0] + flags[1] + flags[2] + flags[3] + flags[4] + flags[5]);
//                        System.out.println("typeOfService: " + typeOfService);
//                        System.out.println("packets: " + packets);
//                        System.out.println("bytes: " + bytes);
//                        System.out.println("packetsPerSecond: " + packetsPerSecond);
//                        System.out.println("bitsPerSecond: " + bitsPerSecond);
//                        System.out.println("bytesPerSecond: " + bytesPerSecond);
//                        System.out.println("flows: " + flows);
//                        System.out.println();
                        
                        lineOut = startYear + ",";
                        lineOut += startMonth + ",";
                        lineOut += startDate + ",";
                        lineOut += startHour + ",";
                        lineOut += startMinute + ",";
                        lineOut += startSecond + ",";
                        lineOut += durationSeconds + ",";
                        lineOut += proto + ",";
                        lineOut += srcIp + ",";
                        lineOut += srcPort + ",";
                        lineOut += dstIp + ",";
                        lineOut += dstPort + ",";
                        lineOut += flags[0] + ",";
                        lineOut += flags[1] + ",";
                        lineOut += flags[2] + ",";
                        lineOut += flags[3] + ",";
                        lineOut += flags[4] + ",";
                        lineOut += flags[5] + ",";
                        lineOut += typeOfService + ",";
                        lineOut += packets + ",";
                        lineOut += bytes + ",";
                        lineOut += packetsPerSecond + ",";
                        lineOut += bitsPerSecond + ",";
                        lineOut += bytesPerSecond + ",";
                        lineOut += flows;
                        
                        fw.write(lineOut + "\n");
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Skipping: \"" + lineIn + "\"");
                    }
                }
                fw.close();
                cmdOut.close();
            }
            catch (Exception e) {
                System.out.println("o");
            }
            System.out.println("NfdumpStdoutThread:  Done.");
        }
    }
    
    private Long checkForMultiplier(String s) {
        if (s.equals("M"))
            return 1000000L;
        if (s.equals("G"))
            return 1000000000L;
        if (s.equals("T"))
            return 1000000000000L;
        return 1L;
    }
    
    public String getCacheDir() {
        return cacheDir;
    }
    
    public String getInputInfo(int i) {
        return "Data Object pointing to a resource.";
    }
    
    public String getInputName(int i) {
        return "DataObjectProxy";
    }
    
    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.io.proxy.DataObjectProxy"};
        return in;
    }
    
    public String getModuleInfo() {
        String s = "<p>Overview: ";
        s += "This module runs nfdump on an input nfcapd file, creating an output";
        s += "file which is a processed version of the nfdump result more amenable";
        s += "to subsequent processing.";
        s += "</p>";
        return s;
    }

    public String getModuleName() {
        return "Nfdump";
    }
    
    public String getOutputInfo(int i) {
        return "Data Object pointing to a resource.";
    }
    
    public String getOutputName(int i) {
        return "DataObjectProxy";
    }
    
    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.io.proxy.DataObjectProxy"};
        return out;
    }
    
    public PropertyDescription[] getPropertiesDescriptions() {
        PropertyDescription[] pds = new PropertyDescription[1];
        pds[0] = new PropertyDescription(
            "cacheDir",
            "Cache Directory",
            "The directory in which nfdump results are cached."
        );
        return pds;
    }
    
    public void setCacheDir(String s) {
        cacheDir = s;
    }
    
    private String dirName(String s) {
        /*String [] parts = s.split(java.io.File.separator);
        String dir = new String();
        for (int i = 0; i < parts.length - 1; i++) {
            dir += parts[i] + java.io.File.separator;
        }*/
    	int index = s.lastIndexOf(java.io.File.separator);
    	String dir = s.substring(0, index+1);
        return dir; 
    }
}
