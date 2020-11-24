package com.cht.iot;

import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Main {
	static long BYTES_PER_MB = 1024 * 1024;
	static long MILLIS_IN_NANOS = 1_000_000L;
	
	static DateFormat DF = new SimpleDateFormat("HH:mm:ss");

	public static void main(String[] args) throws Exception {		
		Action action = Action.dump_runnable_threads;
		String endpoint = "127.0.0.1:9005";
		List<String> excludeds = new ArrayList<>();		
		List<Long> tids = new ArrayList<>();
		
		for (int i = 0;i < args.length;i++) {
			String arg = args[i];
			
			if ("--info".equals(arg)) {
				action = Action.info;
				
			} else if ("--gc".equals(arg)) {
				action = Action.gc;
				
			} else if ("--dump-runnable-threads".equals(arg)) {
				action = Action.dump_runnable_threads;
				
			} else if ("--dump-specified-threads".equals(arg)) {
				action = Action.dump_specified_threads;
				
			} else if ("-d".equals(arg)) {
				endpoint = args[++i];
				
			} else if ("-e".equals(arg)) {
				excludeds.add(args[++i]);
				
			} else if ("-t".equals(arg)) {
				tids.add(Long.parseLong(args[++i]));
				
			} else {
				System.out.println("jmx-cli -d 127.0.0.1:9005 --info");
				System.out.println("jmx-cli -d 127.0.0.1:9005 --gc");
				System.out.println("jmx-cli -d 127.0.0.1:9005 --dump-runnable-threads [-e excluded_name] [-e excluded_name]");
				System.out.println("jmx-cli -d 127.0.0.1:9005 --dump-specified-threads -t 1 -t 2 -t 3");				
				System.exit(1);
			}
		}
		
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + endpoint + "/jmxrmi");
		try (JMXConnector jmxc = JMXConnectorFactory.connect(url)) {
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			
			OperatingSystemMXBean osxb = JMX.newMXBeanProxy(mbsc, new ObjectName("java.lang:type=OperatingSystem"), OperatingSystemMXBean.class);
			MemoryMXBean mxb = JMX.newMXBeanProxy(mbsc, new ObjectName("java.lang:type=Memory"), MemoryMXBean.class);						
			ThreadMXBean txb = JMX.newMXBeanProxy(mbsc, new ObjectName("java.lang:type=Threading"), ThreadMXBean.class);
			
			if (action == Action.dump_runnable_threads) {
				dumpRunnableThreads(txb, excludeds);				
			
			} else if (action == Action.dump_specified_threads) {
				dumpSpecifiedThreads(txb, tids);
				
			} else if (action == Action.gc) {
				gc(mxb);
				
			} else {
				for (;;) {				
					info(osxb, mxb);					
					Thread.sleep(1_000L);
				}
			}
		}
	}
	
	static void info(OperatingSystemMXBean osxb, MemoryMXBean mxb) {
		MemoryUsage mu = mxb.getHeapMemoryUsage();
		
		System.out.printf("[%s] Memory max: %,d MB, committed: %,d MB, used: %,d MB, Loading: %.2f %%\n",
				DF.format(new Date()),
				mu.getMax() / BYTES_PER_MB,
				mu.getCommitted() / BYTES_PER_MB,
				mu.getUsed() / BYTES_PER_MB,
				osxb.getSystemLoadAverage() / osxb.getAvailableProcessors() * 100d);
	}	
	
	static void gc(MemoryMXBean mxb) {
		mxb.gc();
	}
	
	static void dumpRunnableThreads(ThreadMXBean txb, List<String> excludeds) {
		long[] tids = txb.getAllThreadIds();
		Arrays.sort(tids);
		
		for (long tid : tids) {
			ThreadInfo ti = txb.getThreadInfo(tid, 0);
			if (ti == null) {
				continue;
			}
			
			if (contains(ti.getThreadName(), excludeds)) { // don't show the system threads
				continue;
			}
			
			if (isRunnable(ti)) {
				ti = txb.getThreadInfo(tid, Integer.MAX_VALUE);
				dump(txb, ti);
			}
		}
	}
	
	static boolean contains(String name, List<String> names) {
		for (String n : names) {
			if (name.contains(n)) {
				return true;
			}
		}
		
		return false;
	}
	
	static boolean isRunnable(ThreadInfo ti) {
		return (ti.getThreadState() == Thread.State.RUNNABLE);
	}
	
	static void dumpSpecifiedThreads(ThreadMXBean mxb, List<Long> tids) {
		tids.stream()
			.map(tid -> mxb.getThreadInfo(tid, Integer.MAX_VALUE))
			.forEach(ti -> dump(mxb, ti));
	}
	
	static void dump(ThreadMXBean mxb, ThreadInfo ti) {
		System.out.printf("[%d] %s, [%s] CPU: %,d ms\n",
				ti.getThreadId(), ti.getThreadName(),
				ti.getThreadState(),
				mxb.getThreadCpuTime(ti.getThreadId()) / MILLIS_IN_NANOS);
		
		for (StackTraceElement e : ti.getStackTrace()) {
			System.out.printf("\t%s\n", e);
		}
	}
	
	enum Action {
		info,
		gc,
		dump_runnable_threads,
		dump_specified_threads
	}
}
