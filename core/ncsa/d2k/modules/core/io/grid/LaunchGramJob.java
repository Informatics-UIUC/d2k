/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package ncsa.d2k.modules.core.io.grid;

import  ncsa.d2k.infrastructure.modules.*;
import  java.io.*;
import  org.globus.gram.*;
import  org.globus.mds.*;
import  org.globus.security.*;
import  org.globus.myproxy.*;
import  org.globus.tools.ui.proxy.GridProxyProperties;
import  org.globus.io.ftp.*;
import  java.util.*;
import  java.security.cert.*;
import  java.security.*;


/**
 WriteVTToFile.java
 Write the contents of a Table to a flat file.
 @author David Clutter
 */
public class LaunchGramJob extends OutputModule
    implements GramJobListener {
  /////////////// Properties //////////////////

  /** this is the name of the remote process. */
  private boolean batch = false;

  /**
   returns true if this is to be a batch job
   */
  public boolean getBatchJob () {
    return  batch;
  }

  /**
   set if this is to be a batch job.
   @param remote true if batch job
   */
  public void setBatchJob (boolean remote) {
    batch = remote;
  }

  /** this is the name of the remote process. */
  private String remoteExecutable = "/home/redman/d2kremote";

  /**
   Returns a reference to the name of the remote
   executable.
   */
  public String getRemoteExecutable () {
    return  remoteExecutable;
  }

  /**
   Set the name of the remote executable.
   @param remote the name and path of the remote app.
   */
  public void setRemoteExecutable (String remote) {
    remoteExecutable = remote;
  }
  String username = "Thomas L Redman";

  /**
   Returns the users name.
   @returns the users name.
   */
  public String getUserName () {
    return  username;
  }

  /**
   Set the name of the user.
   @param remote the name and path of the remote app.
   */
  public void setUserName (String remote) {
    username = remote;
  }
  transient private String password = null;

  /**
   Returns the users password.
   @returns the users password.
   */
  public String getPassword () {
    return  password;
  }

  /**
   Set the password of the user.
   @param pw the password.
   */
  public void setPassword (String pw) {
    password = pw;
  }
  String remoteAddress = "lorax.ncsa.uiuc.edu";

  /**
   get the remoteAddress of the agenda which this proximity represents
   */
  public String getRemoteAddress () {
    return  this.remoteAddress;
  }

  /**
   get the remoteAddress of the agenda which this proximity represents
   */
  public void setRemoteAddress (String newAddress) {
    this.remoteAddress = newAddress;
  }

  /**
   Return a description of the function of this module.
   @return A description of this module.
   */
  public String getModuleInfo () {
    return  "NOT";
  }
  private int status;

  /**
   method is called each time the status of the gram job changes.
   @param job the gram job being monitored.
   */
  public void statusChanged (GramJob job) {
    synchronized (this) {
      this.status = job.getStatus();
    }
    System.out.println("status : " + status);
    System.out.println("Job status change \n" + "    ID     : " + job.getIDAsString()
        + "\n" + "    Status : " + job.getStatusAsString());
  }

  /**
   Return a String array containing the datatypes the inputs to this
   module.
   @return The datatypes of the inputs.
   */
  public String[] getInputTypes () {
    String[] in =  {
      "java.lang.Object"
    };
    return  in;
  }

  /**
   Return a String array containing the datatypes of the outputs of this
   module.
   @return The datatypes of the outputs.
   */
  public String[] getOutputTypes () {
    String[] in =  {
      "java.lang.String"
    };
    return  in;
  }

  /**
   Return a description of a specific input.
   @param i The index of the input
   @return The description of the input
   */
  public String getInputInfo (int i) {
    switch (i) {
      case 0:
        return  "This is a triggger only, subclass may use this as input to the job however.";
      default:
        return  "No such input.";
    }
  }

  /**
   Return the description of a specific output.
   @param i The index of the output.
   @return The description of the output.
   */
  public String getOutputInfo (int i) {
    return  "Not!";
  }
  /**
   Set up the proxy, either get or put them. We first try to get them,
   if they are invalite reinit them.
   */
  GlobusProxy proxy = null;

  /**
   * put your documentation comment here
   * @return
   */
  private boolean authenticate () {
    // first get the globus proxy.
    try {
      // Try to get the user proxy, this will fail with an exception
      // if the proxy has expired.
      proxy = GlobusProxy.getDefaultUserProxy();
    } catch (GlobusProxyException e) {
      // this just means our credintials have expired.
      GridProxyProperties gridProps = new GridProxyProperties();
      int hours = gridProps.getHours();
      // public boolean createProxy(int hours) {
      X509Certificate userCert = null;
      PrivateKey userKey = null;
      String pwd;
      try {
        // proxy have expired, we must go out and get another proxy.
        // first get our certifications.
        userCert = CertUtil.loadCert(gridProps.getUserCertFile());
        if (userCert == null)
          return  false;
        // Get our user key.
        userKey = CertUtil.loadUserKey(gridProps.getUserKeyFile(), password);
        if (userKey == null)
          return  false;
        // create a new proxy.
        proxy = CertUtil.createProxy(userCert, userKey, gridProps.getBits(),
            hours, gridProps.getLimited());
        if (proxy == null)
          return  false;
        proxy.save(gridProps.getProxyFile());
      } catch (Exception e2) {
        e2.printStackTrace();
        return  false;
      }
      return  true;
    }
    if (proxy.getTimeLeft() <= 0)
      return  false;
    return  true;
  }
  /**
   Launch the gram job and get the standard out.
   */
  private GramJob job;

  /**
   * put your documentation comment here
   * @exception Exception
   */
  public void doit () throws Exception {
    Object obj = this.pullInput(0);
    String arg = null;
    if (obj instanceof String)
      arg = (String)obj;
    // First authenticate ourselves.
    if (!this.authenticate())
      // Either the user canceled, or didn't authenticate properly
      throw  new Exception("Expired Credentials.");
    // Gram job submision bean guy.
    if (arg != null)
      job = new GramJob("&(executable=" + remoteExecutable + ")(stdout=STDOUT)"
          + "(stderr=STDERR)(arguments=" + arg + ")");
    else
      job = new GramJob("&(executable=" + remoteExecutable + ")" + "(stdout=STDOUT)(stderr=STDERR)");
    job.addListener(this);
    // Launch on the machine with the most processors.
    Gram.request(remoteAddress + ":2119", job, batch);
    // Wait for the process to get fired up.
    while (this.status != GramJob.STATUS_DONE)
      Thread.currentThread().yield();
    // get an ftp client
    int ftpserverport = 2811;
    GSIFTPClient client = null;
    client = new GSIFTPClient(remoteAddress, 2811);
    client.connect();
    client.authenticate(proxy);
    File copy = new File("STDOUT");
    while (!client.exists("STDOUT")) {
      System.out.println("File doesn't exist yet.");
      this.wait(1000);
    }
    client.get("STDOUT", copy);
    // Now read in the raw bytes and pass them as the output.
    try {
      int len = (int)copy.length();
      char[] results = new char[len];
      BufferedReader reader = new BufferedReader(new FileReader(copy));
      for (int pos = 0; pos < len;) {
        int howMuch = reader.read(results, pos, len - pos);
        if (howMuch == -1)
          break;
        else
          pos += howMuch;
      }
      this.pushOutput(new String(results), 0);
    } catch (IOException e) {
      throw  new Exception("The result file from the remote run could not be opened.");
    }
  }
}



