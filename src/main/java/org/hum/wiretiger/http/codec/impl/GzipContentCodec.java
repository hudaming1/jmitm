package org.hum.wiretiger.http.codec.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.hum.wiretiger.http.codec.IContentCodec;

public class GzipContentCodec implements IContentCodec {
	
	public static final String Name = "gzip";
	
	GzipContentCodec() {
	}

	@Override
	public byte[] decompress(byte[] bytes) throws IOException {
		GZIPInputStream gzipIs = new GZIPInputStream(new ByteArrayInputStream(bytes));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = gzipIs.read(buffer)) > 0) {
			baos.write(buffer, 0, len);
		}
		return baos.toByteArray();
	}
}
