package org.hum.wiretiger.ssl.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.hum.wiretiger.ssl.CA_Station;

public class CA_StationTest {

	public static void main(String args[]) throws Exception {
		long start = System.currentTimeMillis();
		ByteArrayInputStream byteArrayInputStream = CA_Station.createWithCache("www.163.com");
		System.out.println(byteArrayInputStream.available() + ":" + (System.currentTimeMillis() - start));
		for (int i = 0 ;i < 5 ;i ++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					long start = System.currentTimeMillis();
					ByteArrayInputStream byteArrayInputStream = CA_Station.createWithCache("firefox.dns.nextdns.io");
					System.out.println(byteArrayInputStream.available() + ":" + (System.currentTimeMillis() - start));
				}
			}).start();
		}
		System.in.read();
	}
}
