package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.Serializable;
import java.util.*;

/**
 * The <code>PageManager</code> class is the <code>PagingTable</code>'s
 * mechanism for managing concurrent accesses. <code>PagingTable</code>s that
 * access the same <code>Page</code>s should share a common
 * <code>PageManager</code>.
 *
 * @author gpape
 */
class PageManager implements Serializable {
	static final long serialVersionUID = -5147263888550325945L;

   // private Page[] allPages;

   private Lock WSLock;        // lock changes to the working set
   HashMap workingSet; // hash pages to locks

   Lock globalLock; // lock operations that affect the entire table

   int capacity;    // to facilitate making deep copies of PagingTables

   /**
    * Constructs a new <code>PageManager</code> with the given page set and
    * set of pages to keep initially in memory.
    *
    * @param pageSet         the set of pages to manage
    * @param initialPages    the set of pages to keep in memory initially
    */
   PageManager(Page[] pageSet, Page[] initialPages) {

      capacity = initialPages.length;

      // allPages = new Page[pageSet.length];
      // for (int i = 0; i < pageSet.length; i++)
      //    allPages[i] = pageSet[i];

      WSLock = new Lock();
      globalLock = new Lock();

      workingSet = new HashMap();

      for (int i = 0; i < initialPages.length; i++)
         workingSet.put(initialPages[i], new Lock());

   }

   PageManager(Page[] pageSet, int initialCapacity) {

      capacity = initialCapacity;

      WSLock = new Lock();
      globalLock = new Lock();

      workingSet = new HashMap();

      for (int i = 0; i < initialCapacity; i++)
         workingSet.put(pageSet[i], new Lock());

   }

   /**
    * Requests a <code>Lock</code> to correspond to the given <code>Page</code>.
    * If a <code>Lock</code> is already associated in the manager with that
    * <code>Page</code>, that <code>Lock</code> is returned. Otherwise, an
    * attempt is made to return that <code>Lock</code> which has been least
    * recently accessed.
    * <p>
    * <b>WARNING:</b> Acquiring a lock via this method does not mean that you
    * have acquired read or write access to the lock; this must still be done
    * with the appropriate <code>acquireRead</code> and
    * <code>acquireWrite</code> methods. Also, unless accesses are synchronized
    * externally, there is no guarantee that the page is not paged out of memory
    * before this can be done. (So: synchronize externally.)
    *
    * @param page            the <code>Page</code> to associate with a
    *                        <code>Lock</code>
    * @return                the associated <code>Lock</code>
    */
   Lock request(Page page) {

      Lock lock = null; // make the compiler happy

      try {

         WSLock.acquireWrite();

         if (workingSet.containsKey(page))
            lock = (Lock)workingSet.get(page);
         else {

            // which active page has been used least recently?
            Iterator i = workingSet.keySet().iterator();

            /*
            if (!i.hasNext())
               System.out.println("empty iterator " + this);
            */

            long earliestTime = Long.MAX_VALUE;
            Page P = null, toRemove = null;
            while (i.hasNext()) {

               P = (Page)i.next();
               if (P.time() < earliestTime) {
                  earliestTime = P.time();
                  toRemove = P;
                  lock = (Lock)workingSet.get(toRemove);
               }

            }

            // free that page and page the new one in, associating it with the
            // lock. must have a write lock for this!
            lock.acquireWrite();
               toRemove.free();
               page.pageIn();
               workingSet.put(page, workingSet.remove(toRemove));
            lock.releaseWrite();

         }

      }
      catch(Exception e) {
         System.err.println("PageManager: exception on request:");
         e.printStackTrace();
      }
      finally {
         WSLock.releaseWrite();
      }

      return lock;

   }

   /**
    * Checks whether the given <code>Page</code> is associated with the given
    * <code>Lock</code> in this manager. Of course, this information is
    * meaningless without external synchronization, as that state may change
    * immediately after the end of the method call.
    *
    * @param page            a <code>Page</code>
    * @param lock            a <code>Lock</code>
    * @return                whether <code>page</code> is associated with
    *                        <code>lock</code>
    */
   boolean check(Page page, Lock lock) {
      return ((Lock)workingSet.get(page) == lock);
   }

   void clearWorkingSet(Page[] newPages, int capacity) {
           // blah
      try {
      WSLock.acquireWrite();
      workingSet = new HashMap();
      for (int i = 0; i < capacity; i++) {
         // System.out.println("putting " + newPages[i] + " in");
         newPages[i].pageIn();
         workingSet.put(newPages[i], new Lock());
      }
      WSLock.releaseWrite();
      } catch(InterruptedException e) { e.printStackTrace(); };

   }

}
