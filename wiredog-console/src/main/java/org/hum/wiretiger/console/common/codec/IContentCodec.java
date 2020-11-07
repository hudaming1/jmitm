package org.hum.wiredog.console.common.codec;

import java.io.IOException;

public interface IContentCodec {

	public byte[] decompress(byte[] bytes) throws IOException;
}
