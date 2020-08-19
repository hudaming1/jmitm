package org.hum.wiretiger.http.codec;

import java.io.IOException;

public interface IContentCodec {

	public byte[] decompress(byte[] bytes) throws IOException;
}
