package org.hum.wiredog.ssl.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.hum.jmitm.ssl.CA_Station;

public class CA_StationTest {

	public static void main(String args[]) throws Exception {
		long start = System.currentTimeMillis();
		ByteArrayInputStream bis = CA_Station.createWithCache("hudaming996.com");
		byte[] certBytes = new byte[bis.available()];
		bis.read(certBytes);
		
		FileOutputStream fos = new FileOutputStream(new File("/tmp/huming996.crt"));
		fos.write(certBytes);
		fos.flush();
	}
}
