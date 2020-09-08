package org.hum.wiretiger.ssl.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.hum.wiretiger.ssl.CA_Station;

public class CA_StationTest {

	public void testCreateCa4Concurrency() throws IOException {
		for (int i = 0 ;i < 5 ;i ++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					long start = System.currentTimeMillis();
					ByteArrayInputStream byteArrayInputStream = CA_Station.createWithCache("www.baidu.com");
					System.out.println(byteArrayInputStream.available() + ":" + (System.currentTimeMillis() - start));
				}
			}).start();
		}
		System.in.read();
	}
}
