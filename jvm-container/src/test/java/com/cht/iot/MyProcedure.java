package com.cht.iot;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyProcedure implements Procedure {

	@Override
	public void hello() {
		log.info("This is my code");
	}
}
