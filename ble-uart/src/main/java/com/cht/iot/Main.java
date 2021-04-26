package com.cht.iot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.Instant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
	
	public static void main(String[] args) throws Exception {
		Runtime r = Runtime.getRuntime();
		
		Process p = r.exec(new String[] {				
					"/bin/bash", "-c", "sudo python3 ble_uart.py" });
		
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		PrintWriter pw = new PrintWriter(p.getOutputStream());
					
		String ln;
		while ((ln = br.readLine()) != null) {
			if (ln.startsWith("rx ")) {
				String rx = ln.substring(3);				
				log.info("RX: {}", rx);
				
				String tx = Instant.now().toString();
				
				pw.println(tx);
				pw.flush();
				
			} else if (ln.startsWith("connect ")) {
				String mac = ln.substring(8);
				log.info("CONNECTED: {}", mac);
				
			} else if (ln.startsWith("disconnect ")) {
				String mac = ln.substring(11);				
				log.info("DISCONNECTED: {}", mac);
				
			} else {
				log.info(ln);
			}
		}		
	}
}
