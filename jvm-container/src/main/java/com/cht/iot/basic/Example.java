package com.cht.iot.basic;

import com.cht.iot.MyClassLoader;
import com.cht.iot.Procedure;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Example {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		ClassLoader loader;
		Class<?> clazz;
		
		// general case		
		loader = Example.class.getClassLoader();	
		
		clazz = loader.loadClass("java.lang.Object");
		Object o = clazz.newInstance(); // equals to 'Object o = new Object()'
		
		log.info("Plain old object : {}", o);
		
		// customized case		
		loader = new MyClassLoader();		
		
		clazz = loader.loadClass("com.cht.iot.MyProcedure");		
		Procedure p = (Procedure) clazz.newInstance();		
		log.info("Procedure's ClassLoader      : {}", clazz.getClassLoader());
		
		p.hello();
	}
}
