package org.hum.wiretiger.core.pipe.bean;

import java.io.Serializable;

import org.hum.wiretiger.core.pipe.enumtype.PipeEventType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hudaming
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PipeEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	private PipeEventType type;
	private String desc;
	private long time;
}
