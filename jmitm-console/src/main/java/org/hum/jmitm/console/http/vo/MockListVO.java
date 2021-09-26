package org.hum.jmitm.console.http.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockListVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String desc;
	private boolean checked;
	private String status;
}
