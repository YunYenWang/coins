package coins.jmx.pojo;

import java.util.concurrent.atomic.AtomicInteger;

public class Metrics implements MetricsMBean {

	AtomicInteger count = new AtomicInteger();
	
	@Override
	public int getCount() {
		return count.addAndGet((int) (Math.random() * 100));
	}
}
