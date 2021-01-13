package com.cht.iot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadFileTest {

	@Test
	void readXlsx() throws IOException, TikaException {
		Tika tika = new Tika();
		try (FileInputStream fis = new FileInputStream("/home/rickwang/download/Scrum-Sprint-Plan.xlsx")) {
			Reader reader = tika.parse(fis);
			
			BufferedReader br = new BufferedReader(reader);
			String ln;
			while ((ln = br.readLine()) != null) {
				log.info("{}", ln);
			}
		}
	}
}
