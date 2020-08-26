package org.hum.wiretiger.proxy.facade.lite;

import java.util.Collection;
import java.util.List;

import org.hum.wiretiger.proxy.pipe.WtPipeManager;
import org.hum.wiretiger.proxy.pipe.bean.WtPipeHolder;

public class WiretigerPipeManagerLite {

	private static class WiretigerPipeManagerLiteHolder {
		private static WiretigerPipeManagerLite instance = new WiretigerPipeManagerLite();
	}

	public static WiretigerPipeManagerLite get() {
		return WiretigerPipeManagerLiteHolder.instance;
	}
	
	private WiretigerPipeManagerLite() {
	}

	public WiretigerFullPipe getById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<WiretigerFullPipe> getAll() {
		List<WtPipeHolder> list = WtPipeManager.get().getAll();
	}
}
