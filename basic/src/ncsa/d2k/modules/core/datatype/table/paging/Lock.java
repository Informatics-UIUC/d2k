package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * The <code>Lock</code> class provides a lock on a resource that permits
 * concurrent reads but strictly serial writes. Reads take priority over
 * writes, and neither type of operation can proceed while the other is in
 * progress.
 * <p>
 * Reads to a resource controlled by this lock must take place in between
 * a call to <code>acquireRead</code> and <code>releaseRead</code>. Similarly,
 * writes must take place in between a call to <code>acquireWrite</code> and
 * <code>releaseWrite</code>.
 */
class Lock implements Serializable {
	static final long serialVersionUID = 4610323135080242311L;

   private int active_readers = 0, active_writers = 0, waiting_readers = 0;
   private LinkedList writer_locks = new LinkedList();

   synchronized void acquireRead() throws InterruptedException {

      if (active_writers == 0 && writer_locks.size() == 0)
         active_readers++;
      else {
         waiting_readers++;
         wait();
      }

   }

   void acquireWrite() throws InterruptedException {

      Object lock = new Object();
      synchronized(lock) {
         synchronized(this) {
            if (writer_locks.size() == 0 &&
                active_readers == 0 &&
                active_writers == 0) {
               active_writers++;
               return;
            }
            writer_locks.addLast(lock);
         }
         lock.wait();
      }

   }

   synchronized void releaseRead() {

      active_readers--;
      if (active_readers == 0)
         notifyWriters();

   }

   synchronized void releaseWrite() {

      active_writers--;
      if (waiting_readers > 0)
         notifyReaders();
      else
         notifyWriters();

   }

   // WARNING: notifyReaders must only be called from synchronized methods.
   private void notifyReaders() {

      active_readers += waiting_readers;
      waiting_readers = 0;
      notifyAll();

   }

   // WARNING: notifyWriters must only be called from synchronized methods.
   private void notifyWriters() {

      if (writer_locks.size() == 0)
         return;

      Object first = writer_locks.removeFirst();
      active_writers++;

      synchronized(first) {
         first.notify();
      }

   }

}
