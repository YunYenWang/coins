package com.cht.iot.ev5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FibonacciImpl {
	final Logger log = LoggerFactory.getLogger(getClass()); 
	
	static final int MIN_NUMBER = 2;

	public FibonacciImpl() {		
	}
	
	int calculate(int number) {	
		int r;
		
		if (number < MIN_NUMBER) {
			r = number;
		} else {
			r = calculate(number - 1) + calculate(number - 2);
		}
		
		return r;
	}
	
	public void test(int number) {
		int result = calculate(number);
		
		log.info("The result is {}", result);
	}
	
	public static void main(String[] args) {
		int n = 10;
		
		FibonacciImpl fi = new FibonacciImpl();
		
		fi.test(n);
	}
}
