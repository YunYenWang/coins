package com.cht.iot.ev4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FibonacciImpl implements Fibonacci {
	final Logger log = LoggerFactory.getLogger(this.getClass()); 
	
	static final int MIN_NUMBER = 2;

	public FibonacciImpl() {		
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
		
		this.log.info("The result is {}", result);
	}
	
	public static void main(String[] args) {
		int n = 10;
		
		FibonacciImpl fi = new FibonacciImpl();
		
		fi.test(n);
	}
}
