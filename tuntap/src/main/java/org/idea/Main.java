package org.idea;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.idea.net.TunTap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
	static final int MTU = 1500;
	
	final TunTap tap;
	
	final InputStream rx;
	final OutputStream tx;
	
	final InetAddress group;
	final int port;
	
	// sudo route add -net 230.0.0.0 netmask 255.255.255.0 eno1
	// sudo route add -net 230.0.0.0 netmask 255.255.255.0 enp1s0
	
	MulticastSocket socket;

	int id = (int) (Math.random() * Integer.MAX_VALUE);
	
	public Main(String dev, InetAddress group, int port) throws IOException {
		tap = new TunTap(dev);
		
		rx = tap.getInputStream();
		tx = tap.getOutputStream();
		
		this.group = group;
		this.port = port;
		
		socket = new MulticastSocket(port);
		socket.joinGroup(group);
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
			
			Packet p = new Packet();
			p.owner = id;
			p.size = s;
			p.body = packet;
			
			byte[] b = p.toBytes();
			
			socket.send(new DatagramPacket(b, b.length, group, port));
			
			log.info("TAP to NET - {}", toString(packet, s));
		}
	}	
	
	void net2tap() throws IOException {
		byte[] packet = new byte[MTU];
		for (;;) {					
			DatagramPacket dp = new DatagramPacket(packet, packet.length);			
			socket.receive(dp);
			
			Packet p = Packet.from(dp);
			if (p.owner == id) { // this packet is sent by me
				continue;
			}
			
			tx.write(p.body, 0, p.size);
			
			log.info("NET to TAP - {}", toString(p.body, p.size));
		}
	}
	
	String toString(byte[] bytes, int len) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < len; i++) {
			sb.append(String.format("%02X ", bytes[i] & 0x0FF));
		}
		
		return sb.toString();
	}
	
	static class Packet {
		int owner;
		int size;
		byte[] body;
		
		byte[] toBytes() throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			
			dos.writeInt(owner);
			dos.writeInt(size);
			dos.write(body);			
			
			return baos.toByteArray();
		}
		
		static Packet from(DatagramPacket p) throws IOException {
			ByteArrayInputStream bais = new ByteArrayInputStream(p.getData(), 0, p.getLength());
			DataInputStream dis = new DataInputStream(bais);
			
			return from(dis);
		}
		
		static Packet from(DataInputStream dis) throws IOException {
			Packet p = new Packet();
			p.owner = dis.readInt();
			p.size = dis.readInt();
			p.body = new byte[p.size];
			dis.readFully(p.body);
			
			return p;
		}
	}
	
	public static void main(String[] args) throws Exception {
		String dev = "tap0";
		InetAddress group = InetAddress.getByName("230.0.0.1"); // 224.0.0.0 to 239.255.255.255
		int port = 4511;
		
		Main m = new Main(dev, group, port);
		m.start();		
	}
}
