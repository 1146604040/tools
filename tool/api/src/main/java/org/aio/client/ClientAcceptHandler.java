package org.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
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

	@SuppressWarnings("unchecked")
	@Override
	public void completed(Void v, Object attachment) {
		try {
			LOG.info("Client connection....."
					+ ((InetSocketAddress) channel.getLocalAddress()).getAddress().getHostAddress());
			new ClientReadHandler(channel, (BlockingQueue<BytePackage>) attachment).run();
			new ClientWriteHandler(channel, (BlockingQueue<BytePackage>) attachment).run();;
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {

	}

}
