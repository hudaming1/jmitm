package org.hum.wiretiger.core.external.conmonitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Connection {

	// 地址
	private String host;
	// 端口
	private int port;
}
