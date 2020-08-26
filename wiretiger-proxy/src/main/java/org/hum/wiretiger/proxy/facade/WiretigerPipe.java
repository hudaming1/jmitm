package org.hum.wiretiger.proxy.facade;

import org.hum.wiretiger.proxy.facade.enumtype.WiretigerPipeStatus;
import org.hum.wiretiger.proxy.pipe.enumtype.Protocol;

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
