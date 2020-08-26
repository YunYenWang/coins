package com.cht.iot.ev6;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FibonacciImplTest {

	FibonacciImpl fibonacci;
	
	@BeforeEach
	void init() {
		fibonacci = new FibonacciImpl();
	}
	
	@Test
	void calculate() {
		int number = 10;
		
		int actual = fibonacci.calculate(number);
		
		int expected = 55;
		
		assertEquals(expected, actual);		
	}
}
