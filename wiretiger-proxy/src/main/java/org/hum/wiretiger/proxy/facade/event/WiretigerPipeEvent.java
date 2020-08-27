package org.hum.wiretiger.proxy.facade.event;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WiretigerPipeEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	private String type;
	private String desc;
	private long time;
}
