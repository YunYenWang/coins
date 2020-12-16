package org.idea;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.idea.net.TunTap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main2 {
	static final int MTU = 2048;
	
	final TunTap tap;
	
	final InputStream rx;
	final OutputStream tx;
	
	final InetAddress peer;
	final int port;
	
	// sudo route add -net 230.0.0.0 netmask 255.255.255.0 eno1
	// sudo route add -net 230.0.0.0 netmask 255.255.255.0 enp1s0
	
	final DatagramSocket socket;

	int id = (int) (Math.random() * Integer.MAX_VALUE);
	
	public Main2(String dev, InetAddress peer, int port) throws IOException {
		tap = new TunTap(dev);
		
		rx = tap.getInputStream();
		tx = tap.getOutputStream();
		
		this.peer = peer;
		this.port = port;
		
		socket = new DatagramSocket(port);
	}
	
	void start() {
		new Thread(() -> {
			try {
				tap2net();
				
			} catch (Exception e) {
				log.error("Error", e);
			}
			
		}).start();
		
		new Thread(() -> {
			try {
				net2tap();
				
			} catch (Exception e) {
				log.error("Error", e);
			}
			
		}).start();
	}
	
	void tap2net() throws IOException {
		byte[] packet = new byte[MTU];
		int s;
		while ((s = rx.read(packet)) >= 0) {
			if (s == 0) {
				continue;
			}
			
			socket.send(new DatagramPacket(packet, s, peer, port));
			
//			log.info("TAP to NET - ({}) {}", s, toString(packet, s));
		}
	}	
	
	void net2tap() throws IOException {
		byte[] packet = new byte[MTU];
		for (;;) {					
			DatagramPacket p = new DatagramPacket(packet, packet.length);			
			socket.receive(p);
			
			tx.write(p.getData(), 0, p.getLength());
			
//			log.info("NET to TAP - ({}) {}", p.getLength(), toString(p.getData(), p.getLength()));
		}
	}
	
	String toString(byte[] bytes, int len) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < len; i++) {
			sb.append(String.format("%02X ", bytes[i] & 0x0FF));
		}
		
		return sb.toString();
	}	
	
	public static void main(String[] args) throws Exception {
		String dev = "tap0";
		InetAddress group = InetAddress.getByName("230.0.0.1");
		int port = 4511;
		
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("-d".equals(arg)) {
				group = InetAddress.getByName(args[++i]);
				
			} else if ("-p".equals(arg)) {
				port = Integer.parseInt(args[++i]);
				
			} else if ("-i".equals(arg)) {
				dev = args[++i];
				
			} else {
				log.info("usage: bin/tuntap [-d host] [-p port] [-i tap0]");				
				System.exit(1);
			}
		}		
		
		Main2 m = new Main2(dev, group, port);
		m.start();		
	}
}
