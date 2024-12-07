package org.hum.wiredog.ssl.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.hum.jmitm.ssl.CA_Station;

public class CA_StationTest {

	public static void main(String args[]) throws Exception {
		ByteArrayInputStream bis = CA_Station.createWithCache("163.com");
		byte[] certBytes = new byte[bis.available()];
		bis.read(certBytes);
		
		FileOutputStream fos = new FileOutputStream(new File("/tmp/huming996.crt"));
		fos.write(certBytes);
		fos.flush();
		System.out.println("123123");
	}
}
