package coins.jmx.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableMBeanExport;

@SpringBootApplication
@EnableMBeanExport
public class JmxBootAppApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(JmxBootAppApplication.class, args);
		
		Object lck = new Object();
		synchronized (lck) {
			lck.wait();
		}
	}

}
