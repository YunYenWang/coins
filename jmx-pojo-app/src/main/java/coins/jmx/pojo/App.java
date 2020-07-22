/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package coins.jmx.pojo;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class App {

	public static void main(String[] args) throws MalformedObjectNameException, InstanceAlreadyExistsException,	MBeanRegistrationException, NotCompliantMBeanException, InterruptedException {
		Metrics metrics = new Metrics();

		MBeanServer server = ManagementFactory.getPlatformMBeanServer();

		ObjectName name = new ObjectName("coins:name=Metrics");
		server.registerMBean(metrics, name);

		Object lck = new Object();
		synchronized (lck) {
			lck.wait();
		}
	}
}
