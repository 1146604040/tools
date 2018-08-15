package org.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.aio.entity.ByteFactory;
import org.aio.entity.BytePackage;
import org.aio.entity.MessageInfo;
import org.aio.tools.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerListen {

	/**
	 * 日志
	 */
	private final static Logger log = LoggerFactory.getLogger(ServerListen.class);

	/**
	 * 连接管道
	 */
	private AsynchronousServerSocketChannel server;

	public BlockingQueue<BytePackage> read;

	public static final Map<String, BlockingQueue<BytePackage>> users = new HashMap<>();

	private AsynchronousChannelGroup channelGroup;

	/**
	 * 
	 * @param port
	 *            端口
	 * @param threadNumber
	 *            初始线程池线程数
	 * @param threadFactory
	 *            线程工厂
	 * @param read
	 *            客户端数据存放队列
	 * @throws IOException
	 */
	public void init(Integer port, Integer threadNumber, ThreadFactory threadFactory, BlockingQueue<BytePackage> read)
			throws IOException {
		this.read = read;
		channelGroup = AsynchronousChannelGroup.withFixedThreadPool(threadNumber, threadFactory);
		server = AsynchronousServerSocketChannel.open(channelGroup);
		server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		// 绑定端口并设置连接请求队列长度
		server.bind(new InetSocketAddress(port), 1024);
	}

	public void listen() {
		log.info("Server start listen .......");

		// 开始监听
		server.accept(this.read, new AcceptHandler(server));
	}

}
