package org.hum.wiretiger.proxy.util;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

import io.netty.channel.Channel;
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
	
	public static String toHostAndPort(Channel channel) {
		InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
		return address.getHostName() + ":" + address.getPort();
	}
	
}
