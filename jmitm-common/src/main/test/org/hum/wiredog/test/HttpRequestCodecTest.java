package org.hum.wiredog.test;

import java.io.IOException;

import org.hum.jmitm.common.util.HttpRequestCodec;
import org.junit.Test;

public class HttpRequestCodecTest {

	@Test
	public void testDecode() throws IOException {
		String httpRequestWithoutBody = "";
		HttpRequestCodec.decode(httpRequestWithoutBody);
	}
}
