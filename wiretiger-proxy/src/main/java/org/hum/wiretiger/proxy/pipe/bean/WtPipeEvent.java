package org.hum.wiretiger.proxy.pipe.bean;

import java.io.Serializable;

import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hudaming
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WtPipeEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	private PipeEventType type;
	private String desc;
	private long time;
}
