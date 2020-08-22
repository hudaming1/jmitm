package org.hum.wiretiger.common.wrapper;

import java.net.SocketAddress;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class ChannelWrapper implements Channel {

	private Channel channel;
	
	public ChannelWrapper(Channel channel) {
		this.channel = channel;
	}

	public <T> Attribute<T> attr(AttributeKey<T> key) {
		return channel.attr(key);
	}

	public ChannelFuture bind(SocketAddress localAddress) {
		return channel.bind(localAddress);
	}

	public <T> boolean hasAttr(AttributeKey<T> key) {
		return channel.hasAttr(key);
	}

	public ChannelFuture connect(SocketAddress remoteAddress) {
		return channel.connect(remoteAddress);
	}

	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
		return channel.connect(remoteAddress, localAddress);
	}

	public ChannelFuture disconnect() {
		return channel.disconnect();
	}

	public ChannelFuture close() {
		return channel.close();
	}

	public ChannelId id() {
		return channel.id();
	}

	public EventLoop eventLoop() {
		return channel.eventLoop();
	}

	public Channel parent() {
		return channel.parent();
	}

	public ChannelConfig config() {
		return channel.config();
	}

	public ChannelFuture deregister() {
		return channel.deregister();
	}

	public boolean isOpen() {
		return channel.isOpen();
	}

	public boolean isRegistered() {
		return channel.isRegistered();
	}

	public boolean isActive() {
		return channel.isActive();
	}

	public ChannelMetadata metadata() {
		return channel.metadata();
	}

	public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
		return channel.bind(localAddress, promise);
	}

	public SocketAddress localAddress() {
		return channel.localAddress();
	}

	public int compareTo(Channel o) {
		return channel.compareTo(o);
	}

	public SocketAddress remoteAddress() {
		return channel.remoteAddress();
	}

	public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
		return channel.connect(remoteAddress, promise);
	}

	public ChannelFuture closeFuture() {
		return channel.closeFuture();
	}

	public boolean isWritable() {
		return channel.isWritable();
	}

	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
		return channel.connect(remoteAddress, localAddress, promise);
	}

	public long bytesBeforeUnwritable() {
		return channel.bytesBeforeUnwritable();
	}

	public long bytesBeforeWritable() {
		return channel.bytesBeforeWritable();
	}

	public Unsafe unsafe() {
		return channel.unsafe();
	}

	public ChannelFuture disconnect(ChannelPromise promise) {
		return channel.disconnect(promise);
	}

	public ChannelPipeline pipeline() {
		return channel.pipeline();
	}

	public ByteBufAllocator alloc() {
		return channel.alloc();
	}

	public Channel read() {
		return channel.read();
	}

	public Channel flush() {
		return channel.flush();
	}

	public ChannelFuture close(ChannelPromise promise) {
		return channel.close(promise);
	}

	public ChannelFuture deregister(ChannelPromise promise) {
		return channel.deregister(promise);
	}

	public ChannelFuture write(Object msg) {
		return channel.write(msg);
	}

	public ChannelFuture write(Object msg, ChannelPromise promise) {
		return channel.write(msg, promise);
	}

	public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
		return channel.writeAndFlush(msg, promise);
	}

	public ChannelFuture writeAndFlush(Object msg) {
		return channel.writeAndFlush(msg);
	}

	public ChannelPromise newPromise() {
		return channel.newPromise();
	}

	public ChannelProgressivePromise newProgressivePromise() {
		return channel.newProgressivePromise();
	}

	public ChannelFuture newSucceededFuture() {
		return channel.newSucceededFuture();
	}

	public ChannelFuture newFailedFuture(Throwable cause) {
		return channel.newFailedFuture(cause);
	}

	public ChannelPromise voidPromise() {
		return channel.voidPromise();
	}
}
