package org.hum.jmitm.console.common.codec.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.hum.jmitm.console.common.codec.IContentCodec;

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

	@Override
	public byte[] compress(byte[] bytes) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(bytes);
		gzip.close();
		return out.toByteArray();
	}
}
