package org.hum.wiredog.console.common.codec;

import java.io.IOException;

public interface IContentCodec {

	public byte[] compress(byte[] bytes) throws IOException;
	
	public byte[] decompress(byte[] bytes) throws IOException;
}
