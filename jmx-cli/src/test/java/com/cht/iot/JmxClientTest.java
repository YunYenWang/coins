package com.cht.iot;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.jupiter.api.Test;

public class JmxClientTest {

	@Test
	void dumpThreads() throws Exception {
		String endpoint = "127.0.0.1:9002";
		
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + endpoint + "/jmxrmi");
		try (JMXConnector jmxc = JMXConnectorFactory.connect(url)) {
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			
			ThreadMXBean txb = JMX.newMXBeanProxy(mbsc, new ObjectName("java.lang:type=Threading"), ThreadMXBean.class);

			for (long id : txb.getAllThreadIds()) {
				ThreadInfo ti = txb.getThreadInfo(id, Integer.MAX_VALUE);
				
				System.out.printf("id: %d, name: %s, state: %s \n", id, ti.getThreadName(), ti.getThreadState());				
				
				for (StackTraceElement e : ti.getStackTrace()) {
					System.out.printf("\t%s\n", e);
				}
			}
		}
	}
}
