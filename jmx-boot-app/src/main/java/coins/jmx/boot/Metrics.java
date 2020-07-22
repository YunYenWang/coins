package coins.jmx.boot;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource("coins:name=Metrics")
public class Metrics {

	AtomicInteger count = new AtomicInteger();
	
	@ManagedAttribute
	public int getCount() {
		return count.addAndGet((int) (Math.random() * 100));
	}
}
