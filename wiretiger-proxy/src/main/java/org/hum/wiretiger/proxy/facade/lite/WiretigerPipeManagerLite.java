package org.hum.wiretiger.proxy.facade.lite;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hum.wiretiger.proxy.facade.enumtype.WiretigerPipeStatus;
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
		List<WiretigerFullPipe> list = new ArrayList<>();
		for (WtPipeHolder holder : WtPipeManager.get().getAll()) {
			InetSocketAddress source = (InetSocketAddress) holder.getClientChannel().remoteAddress();
			InetSocketAddress target = (InetSocketAddress) holder.getServerChannel().remoteAddress();
			WiretigerFullPipe fullPipe = new WiretigerFullPipe();
			fullPipe.setPipeId(holder.getId() + "");
			fullPipe.setProtocol(holder.getProtocol());
			fullPipe.setSourceHost(source.getHostName());
			fullPipe.setSourcePort(source.getPort());
			fullPipe.setTargetHost(target.getHostName());
			fullPipe.setTargetPort(target.getPort());
			fullPipe.setStatus(WiretigerPipeStatus.getEnum(holder.getCurrentStatus().getCode()));
			list.add(fullPipe);
		}
		return list;
	}
}
