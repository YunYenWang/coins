package com.cht.iot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyClassLoader extends ClassLoader {

	public MyClassLoader() {
		log.info("My Parent ClassLoader  : {}", getParent());
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {		
		String fn = String.format("bin/test/%s.class", name.replace('.', '/'));		
		File f = new File(fn);
		
		if (f.canRead() == false) { // not found from the given class path
			throw new ClassNotFoundException("No such class from 'bin/test'");
		}		
		
		try (FileInputStream fis = new FileInputStream(f)) { // load from byte stream
			byte[] bytes = toBytes(fis);
			
			return defineClass(name, bytes, 0, bytes.length); // singleton

		} catch (Exception e) {
			throw new ClassNotFoundException("Failed to load " + fn, e);
		}
	}
	
	byte[] toBytes(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bytes = new byte[4096];
		int s;
		while ((s = is.read(bytes)) > 0) {
			baos.write(bytes, 0, s);
		}
		
		baos.flush();
		
		return baos.toByteArray();
	}
}
