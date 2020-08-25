package org.hum.wiretiger.api.core;

import org.hum.wiretiger.api.core.enumtype.WiretigerPipeStatus;
import org.hum.wiretiger.api.enumtype.Protocol;

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
