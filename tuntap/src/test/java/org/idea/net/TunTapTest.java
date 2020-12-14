package org.idea.net;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TunTapTest {
	static final int MTU = 1500;	

	String dev = "tap0";
	
	@Test
	void test() throws Exception {
		byte[] bytes = new byte[MTU];
		
		try (TunTap tt = new TunTap(dev)) {
			InputStream is = tt.getInputStream();			
			int s;
			while ((s = is.read(bytes)) >= 0) {
				if (s == 0) {
					log.info("No more packet from {}, timeout: {} ms", dev, tt.getTimeout());
					continue;
				}
				
				log.info("{}", toString(bytes, s));
			}
		}		
	}
	
	String toString(byte[] bytes, int len) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < len; i++) {
			sb.append(String.format("%02X ", bytes[i] & 0x0FF));
		}
		
		return sb.toString();
	}	
}
