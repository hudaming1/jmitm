package org.hum.jmitm.console.common.codec.impl;

import java.util.HashMap;
import java.util.Map;

import org.hum.jmitm.console.common.codec.IContentCodec;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodecFactory {

	private static final Map<String, IContentCodec> CodecImplMap = new HashMap<>(); 
	static {
		CodecImplMap.put(GzipContentCodec.Name, new GzipContentCodec());
	}
	
	public static IContentCodec create(String contentEncodingType) {
		if (CodecImplMap.containsKey(contentEncodingType)) {
			return CodecImplMap.get(contentEncodingType);
		}
		log.error("found unsupport content-encode type=" + contentEncodingType);
		return null;
	}
}
