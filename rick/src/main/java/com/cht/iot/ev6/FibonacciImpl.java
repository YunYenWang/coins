package com.cht.iot.ev6;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FibonacciImpl {
	static final int MIN_NUMBER = 2;
	
	public void test(int number) {
		int r = calculate(number);
		
		log.info("The result is {}", r);
	}
	
	int calculate(int number) {	
		if (number < MIN_NUMBER) {
			return number;
			
		} else {
			return calculate(number - 1) + calculate(number - 2);
		}		
	}
}
