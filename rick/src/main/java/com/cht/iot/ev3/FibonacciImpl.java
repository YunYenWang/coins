package com.cht.iot.ev3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FibonacciImpl implements Fibonacci {
	static final Logger LOG = LoggerFactory.getLogger(FibonacciImpl.class); 
	
	static final int MIN_NUMBER = 2;

	public FibonacciImpl() {		
	}
	
	static void println(int n) {
		LOG.info("The result is " + n);
	}
	
	int calculate(int n) {	
		int result;
		
		if (n < MIN_NUMBER) {
			result = n;
		} else {
			result = calculate(n - 1) + calculate(n - 2);
		}
		
		return result;
	}
	
	public void test(int n) {
		int result = this.calculate(n);
		
		FibonacciImpl.println(result);
	}
	
	public static void main(String[] args) {
		int n = 10;
		
		FibonacciImpl fi = new FibonacciImpl();
		
		fi.test(n);
	}
}
