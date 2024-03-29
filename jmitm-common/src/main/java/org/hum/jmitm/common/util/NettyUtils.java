package org.hum.jmitm.common.util;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;

import org.hum.jmitm.common.util.HttpMessageUtil.InetAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class NettyUtils {

    private static final boolean SUPPORT_NATIVE_ET;

    static {
        boolean epoll;
        try {
            Class.forName("io.netty.channel.epoll.Native");
            epoll = true;
        } catch (Throwable e) {
            epoll = false;
        }
        SUPPORT_NATIVE_ET = epoll;
    }

    /**
     * The native socket transport for Linux using JNI.
     */
    public static boolean isSupportNativeET() {
        return SUPPORT_NATIVE_ET;
    }

	public static EventLoopGroup initEventLoopGroup(int threadCount, ThreadFactory threadFactory) {
		return isSupportNativeET() ? new EpollEventLoopGroup(threadCount, threadFactory) : new NioEventLoopGroup(threadCount, threadFactory);
	}
	
	public static InetAddress toHostAndPort(Channel channel) {
		InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
		return new InetAddress(address.getHostString(), address.getPort());
	}
	
	public static ChannelHandler findChannelHandler(Channel channel, Class<?> findType) {
		Iterator<Entry<String, ChannelHandler>> iterator = channel.pipeline().iterator();
		while (iterator.hasNext()) {
			ChannelHandler channelHandler = iterator.next().getValue();
			if (findType.isInstance(channelHandler)) {
				return channelHandler;
			}
		}
		return null;
	}
}
