package org.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.aio.entity.ByteFactory;
import org.aio.entity.BytePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientAcceptHandler implements CompletionHandler<Void, ByteFactory> {

	private final static Logger log = LoggerFactory.getLogger(ClientAcceptHandler.class);

	private AsynchronousSocketChannel channel;

	public ClientAcceptHandler(AsynchronousSocketChannel channel) {
		this.channel = channel;
	}

	@Override
	public void completed(Void v, ByteFactory rw) {
		try {
			log.info("Client connection:====>"
					+ ((InetSocketAddress) channel.getLocalAddress()).getAddress().getHostAddress());

			ByteBuffer buffer = ByteBuffer.allocate(1024);
			BytePackage pack = new BytePackage();
			// 读取数据,给一个buffer,如果有数据可读,将读取到的数据放入这个buffer.
			channel.read(buffer, pack, new ClientReadHandler(channel, buffer, rw));

			ByteBuffer wb = ByteBuffer.allocate(0);
			// 向主机写内容,如果有内容可写.
			// 给主机一个没有内容的buffer,这时将等待write队列添加数据包
			channel.write(wb, wb, new ClientWriteHandler(channel, rw));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void failed(Throwable exc, ByteFactory attachment) {
		try {
			log.error("Cconnect errot:====>"
					+ ((InetSocketAddress) channel.getLocalAddress()).getAddress().getHostAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
