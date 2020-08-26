package org.hum.wiretiger.api.proxy;

import org.hum.wiretiger.api.enumtype.Protocol;
import org.hum.wiretiger.api.proxy.enumtype.WiretigerPipeStatus;

import lombok.Data;

@Data
public class WireTigerPipe {

	private String pipeId;
	private String sourceHost;
	private Integer sourcePort;
	private String targetHost;
	private Integer targetPort;
	private Protocol protocol;
	private WiretigerPipeStatus status;
}
