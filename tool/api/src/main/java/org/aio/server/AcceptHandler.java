package org.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.aio.client.ClientReadHandler;
import org.aio.client.ClientWriteHandler;
import org.aio.entity.ByteFactory;
import org.aio.entity.BytePackage;
import org.aio.entity.MessageInfo;
import org.aio.entity.UserInfo;
import org.aio.entity.UserStorage;
import org.aio.tools.ByteTool;
import org.aio.tools.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, BlockingQueue<BytePackage>> {

	/**
	 * 日志
	 */
	private final static Logger log = LoggerFactory.getLogger(AcceptHandler.class);

	private AsynchronousServerSocketChannel server;

	public AcceptHandler(AsynchronousServerSocketChannel server) {
		this.server = server;
	}

	/**
	 * 成功连接
	 * 
	 * @param result
	 *            当前管道
	 * @param attachment
	 *            附件
	 */
	@Override
	public void completed(AsynchronousSocketChannel channel, BlockingQueue<BytePackage> read) {
		try {
			String hostAddress = ((InetSocketAddress) channel.getLocalAddress()).getAddress().getHostAddress();
			log.info("Server accept......" + hostAddress);
			// 成功连接,继续等待下个连接
			server.accept(read, this);

			BytePackage pack = new BytePackage();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			channel.read(buffer, pack, new ServerReadHandler(channel, read, buffer));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void failed(Throwable exc, BlockingQueue<BytePackage> read) {
		log.error("Accept error.........");
	}

}
