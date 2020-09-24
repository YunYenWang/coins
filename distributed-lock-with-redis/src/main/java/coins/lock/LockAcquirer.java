package coins.lock;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LockAcquirer {
	
	@Autowired
	StringRedisTemplate redis;

	String name;
	
	String key = "distributed.lock";
	long expiry = 10_000;
	
	boolean master = false;
	
	@PostConstruct
	void start() {
		name = ManagementFactory.getRuntimeMXBean().getName(); // get the process id
	}
	
	@Scheduled(fixedDelay = 5_000) // half of 'expiry'
	void run() {
		if (master == false) { // I'm slave. I try to acquire the lock.	
			master = redis.opsForValue().setIfAbsent(key, name, expiry, TimeUnit.MILLISECONDS);
			
			if (master == true) {
				log.info("{} is a master now", name);
				
			} else {
				log.info("I'm still a slave");
			}
			
		} else { // I'm master
			redis.expire(key, expiry, TimeUnit.MILLISECONDS);
		}
	}
}
