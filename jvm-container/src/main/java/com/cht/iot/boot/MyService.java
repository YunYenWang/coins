package com.cht.iot.boot;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import com.cht.iot.MyClassLoader;
import com.cht.iot.Procedure;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MyService {
	
	@Autowired
	AutowireCapableBeanFactory factory;
	
	ClassLoader loader;
	
	@PostConstruct
	void start() {
		loader = new MyClassLoader();
	}
	
	public String showMeTheMoney() {
		return String.format("%f", Math.random());
	}
	
	public void run() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = loader.loadClass("com.cht.iot.MyRichProcedure"); // Spring Object
		
		Procedure p = (Procedure) clazz.newInstance();
		
		factory.autowireBean(p); // ask spring framework to inject the fields
		
		log.info("Procedure's ClassLoader    : {}", clazz.getClassLoader());
		
		p.hello();
	}
}
