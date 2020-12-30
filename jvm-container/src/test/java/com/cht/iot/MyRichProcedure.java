package com.cht.iot;

import org.springframework.beans.factory.annotation.Autowired;

import com.cht.iot.boot.MyService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyRichProcedure implements Procedure {

	@Autowired
	MyService service;	
	
	@Override
	public void hello() {
		log.info("I got money ${} from MyService", service.showMeTheMoney());
	}
}

