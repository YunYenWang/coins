package org.idea.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class TunTap implements Closeable {
	static final int MTU = 4096;
	
	static native int open(String dev);	
	static native void close(int fd);	
	static native int write(int fd, ByteBuffer d, int len);	
	static native boolean await(int fd, long timeout);	
	static native int read(int fd, ByteBuffer d, int len);
	
	final int fd;
	
	long timeout = 1000L;
	
	/**
	 * sudo ip tuntap add dev tap0 mode tap
	 * sudo ifconfig tap0 172.0.0.1 up
	 * 
	 * @param dev
	 * @throws IOException
	 */
	
	public TunTap(String dev) throws IOException {
		System.loadLibrary("tuntap");
		
		fd = open(dev);
		if (fd < 0) {
			throw new IOException("Failed to open device: " + fd);
		}
	}
	
	public long getTimeout() {
		return timeout;
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	@Override
	public void close() throws IOException {
		close(fd);		
	}
	
	public OutputStream getOutputStream() {
		return new OutputStream() {
			ByteBuffer bb = ByteBuffer.allocateDirect(MTU);
			
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				len = Math.min(len, MTU);
				
				bb.position(0);
				bb.limit(bb.capacity());
				bb.put(b, off, len);
				bb.flip();
				
				TunTap.write(fd, bb, len);
			}
			
			@Override
			public void write(int b) throws IOException {
				bb.position(0);
				bb.limit(bb.capacity());
				bb.put((byte) b);
				bb.flip();
				
				TunTap.write(fd, bb, 1);												
			}
			
			@Override
			public void close() throws IOException {
				TunTap.this.close();
			}
		};
	}
	
	public InputStream getInputStream() {
		return new InputStream() {
			ByteBuffer bb = ByteBuffer.allocateDirect(MTU);
			
			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				if (TunTap.await(fd, timeout) == false) {
					return 0;
				}
				
				len = Math.min(len, MTU);
				
				int s = TunTap.read(fd, bb, len);
				if (s < 0) {
					throw new IOException("Failed to read: " + s);
				}
				
				bb.position(0);
				bb.limit(s);				
				
				for (int i = 0;i < s;i++) {
					b[off + i] = bb.get(i);
				}
				
				return s;
			}

			@Override
			public int read() throws IOException {
				if (TunTap.await(fd, timeout) == false) {
					return -1;
				}
				
				int s = TunTap.read(fd, bb, 1);
				if (s < 0) {
					throw new IOException("Failed to read: " + s);
				}
				
				bb.position(0);
				bb.limit(s);
				
				return bb.get();
			}
			
			@Override
			public int available() throws IOException {
				return TunTap.await(fd, timeout)? 1 : 0; // XXX - could be better
			}
			
			@Override
			public long skip(long n) throws IOException {
				throw new IOException("Not yet implemented");
			}
			
			@Override
			public void close() throws IOException {
				TunTap.this.close();
			}
		};
	}
}
