package org.hum.wiretiger.facade.proxy;

import org.hum.wiretiger.facade.enumtype.Protocol;
import org.hum.wiretiger.facade.proxy.enumtype.WiretigerPipeStatus;

import lombok.Data;

@Data
public class WiretigerPipe {

	private String pipeId;
	private String sourceHost;
	private Integer sourcePort;
	private String targetHost;
	private Integer targetPort;
	private Protocol protocol;
	private WiretigerPipeStatus status;
}
