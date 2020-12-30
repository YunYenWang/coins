package com.cht.iot;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mother {
	Date alarm;		// 永遠都在說謊的鬧鈴
	int threshold;	// 準備爆發的容忍時間
	
	public Mother() {	
	}
	
	public void morningCall() {
		log.info("再不起床，你就死定了！");
	}
}

