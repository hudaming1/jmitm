package org.hum.wiretiger.http.codec.impl;

import java.util.HashMap;
import java.util.Map;

import org.hum.wiretiger.http.codec.IContentCodec;
import org.hum.wiretiger.http.common.exception.WtHttpException;

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
		throw new WtHttpException("found unsupport content-encode type=" + contentEncodingType);
	}
}
