/*
 * Created on Apr 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ncsa.d2k.modules.core.datatype.table.paging;

import java.io.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.*;

/**
 * @author redman
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FileChannelInputStream extends InputStream {

	File file;
	ByteBuffer buffer;
	FileChannel channel;
	int size = 102400;
	FileChannelInputStream (File fc) throws IOException {
		super();
		this.file = fc;
		//size = (int) fc.length();
		channel = new FileInputStream (file).getChannel();
		buffer = ByteBuffer.allocateDirect(size);
		channel.read(buffer);
		buffer.rewind();
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		if (buffer.remaining() == 0) {
			int amount;
			buffer.clear();
			amount = channel.read(buffer);
			if (amount == -1) {
				return -1;
			}
			buffer.rewind();
		}
		try {
			return buffer.get() & 0x000000FF;
		} catch (BufferUnderflowException fufe) {
			System.out.println("Buffer underflow.");
			return -1;
		}
	}

}
