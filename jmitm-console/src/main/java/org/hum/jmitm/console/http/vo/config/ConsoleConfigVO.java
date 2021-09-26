package org.hum.jmitm.console.http.vo.config;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConsoleConfigVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean httpsProxy;
	
	private boolean mock;
}
