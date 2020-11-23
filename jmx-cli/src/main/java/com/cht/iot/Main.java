package com.cht.iot;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Main {
	static long MILLIS_IN_NANOS = 1_000_000L;

	public static void main(String[] args) throws Exception {		
		Action action = Action.dump_runnable_threads;
		String endpoint = "127.0.0.1:9005";
		List<String> excludeds = new ArrayList<>();		
		List<Long> tids = new ArrayList<>();
		
		for (int i = 0;i < args.length;i++) {
			String arg = args[i];
			if ("--dump-runnable-threads".equals(arg)) {
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
				System.out.println("jmx-cli -d 127.0.0.1:9005 --dump-runnable-threads [-e excluded_name] [-e excluded_name]");
				System.out.println("jmx-cli -d 127.0.0.1:9005 --dump-specified-threads -t 1 -t 2 -t 3");				
				System.exit(1);
			}
		}
		
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + endpoint + "/jmxrmi");
		try (JMXConnector jmxc = JMXConnectorFactory.connect(url)) {
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			
			ObjectName on = new ObjectName("java.lang:type=Threading");			
			ThreadMXBean mxb = JMX.newMXBeanProxy(mbsc, on, ThreadMXBean.class);
			
			if (action == Action.dump_specified_threads) {
				dumpSpecifiedThreads(mxb, tids);
				
			} else {
				dumpRunnableThreads(mxb, excludeds);
			}
		}
	}
	
	static void dumpRunnableThreads(ThreadMXBean mxb, List<String> excludeds) {
		long[] tids = mxb.getAllThreadIds();
		Arrays.sort(tids);
		
		for (long tid : tids) {
			ThreadInfo ti = mxb.getThreadInfo(tid, 0);
			
			if (contains(ti.getThreadName(), excludeds)) { // don't show the system threads
				continue;
			}
			
			if (isRunnable(ti)) {
				ti = mxb.getThreadInfo(tid, Integer.MAX_VALUE);
				dump(mxb, ti);
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
		dump_runnable_threads,
		dump_specified_threads
	}
}
