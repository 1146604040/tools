package org.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.BlockingQueue;

import org.aio.entity.BytePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientAcceptHandler implements CompletionHandler<Void, Object> {

	private final static Logger LOG = LoggerFactory.getLogger(ClientAcceptHandler.class);

	private AsynchronousSocketChannel channel;

	public ClientAcceptHandler(AsynchronousSocketChannel channel) {
		this.channel = channel;
	}

	@Override
	public void completed(Void v, Object attachment) {
		try {
			LOG.info("Client connection....."
					+ ((InetSocketAddress) channel.getLocalAddress()).getAddress().getHostAddress());
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			BytePackage pack = new BytePackage();
			channel.read(buffer, pack, new ClientReadHandler(channel, buffer));

			ByteBuffer wb = ByteBuffer.allocate(0);
			channel.write(wb, wb, new ClientWriteHandler(channel));
			System.out.println("client over ......");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {

	}

}
