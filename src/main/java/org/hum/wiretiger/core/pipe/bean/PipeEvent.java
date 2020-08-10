package org.hum.wiretiger.core.pipe.bean;

import org.hum.wiretiger.core.pipe.enumtype.PipeEventType;

import lombok.Data;

/**
 * @author hudaming
 */
@Data
public class PipeEvent {

	private PipeEventType type;
	private String desc;
}
