package ncsa.d2k.modules.core.control;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.util.QuickQueue;

/**
 * User: redman
 * Date: Aug 28, 2003
 * Time: 11:14:33 AM
 * This module will push the first input it receives on input 0, but will not push subsequent inputs on 0
 * until it receives an input on one.
 */
public class QueuedTriggerPush extends DataPrepModule{

   protected boolean debug;

   /**
    * We are ready to run if we have any data on any input.
    * @return true if we are ready to fire.
    */
   public boolean isReady() {
      if (this.getInputPipeSize(0) > 0 || 
      		(queue.getSize() > 0 && this.getInputPipeSize(1) > 0)) {
      		return true;
      } else
      		return false;
   }
   
   QuickQueue queue;
   /**
    * set the first execution flag.
    */
   public void beginExecution () {
   		queue = new QuickQueue();
   }

   /**
    * set the first execution flag.
    */
   public void endExecution () {
   		if (queue.getSize() > 0)
   			System.out.println(this.getAlias()+": There were "+queue.getSize()+" items still in the queue.");
   		queue = new QuickQueue();
   }

   /**
    * If first time, just pull input 0 and push it, subsequent runs, pull input 1, push it
    * and pull an input off the second input port, discard it.
    * @throws Exception
    */
   public void doit() throws Exception {
   		if (this.getInputPipeSize(0) > 0) {
   			
   			// Always prefer to read the first pipe, keep it clear.
   			Object obj = this.pullInput (0);
   			queue.push(obj);
   		} else {
   			
   			// Here we know we have something in the queue, and there is something
   			// in the trigger pipe, or we would not have enabled.
   			Object obj = this.pullInput (1);
   			if (queue.getSize() > 0)
   				this.pushOutput(queue.pop(), 0);
   		}
   }


   ////////////////////////
   /// D2K Info Methods
   /////////////////////


   public String getModuleInfo(){
      return "<p>Overview: This module will collect the input received on its" +
      		" first input. When an input is received on the second input, it will push" +
      		" one of the items saved off the first input, if there are any.</p>";
   }

   public String getModuleName() {
      return "Queued Trigger Push";
   }

   public String[] getInputTypes(){
      String[] types = {"java.lang.Object","java.lang.Object"};
      return types;
   }

   public String getInputInfo(int index){
      switch (index) {
         case 0: return "The object to push.";
         case 1: return "The triggering object.";
         default: return "No such input";
      }
   }

   public String getInputName(int index) {
      switch(index) {
         case 0:
            return "Object";
         case 1:
            return "Trigger";
         default: return "NO SUCH INPUT!";
      }
   }
   public String[] getOutputTypes(){
      String[] types = {"java.lang.Object"};
      return types;
   }

   public String getOutputInfo(int index){
      switch (index) {
         case 0: return "The object received on the first input is pushed after it is received the first time, and after any input on the second input.";
         default: return "No such output";
      }
   }
   public String getOutputName(int index) {
      switch(index) {
         case 0:
            return "Object";
         default: return "NO SUCH OUTPUT!";
      }
   }
   public void setDebug(boolean b){
      debug=b;
   }
   public boolean getDebug(){
      return debug;
   }
   
   /**
    * Return a list of the property descriptions.
    * 
    * @return a list of the property descriptions.
    */
   public PropertyDescription[] getPropertiesDescriptions() {
     PropertyDescription[] pds = new PropertyDescription[1];
     pds[0] = new PropertyDescription(
         "debug",
         "Generate Debug Output",
         "If this property is true, the module will write verbose debug information to the console.");
     return pds;
   }
}
