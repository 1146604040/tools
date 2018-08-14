package org.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

import org.aio.entity.BytePackage;
import org.aio.entity.MessageInfo;
import org.aio.entity.UserStorage;
import org.aio.tools.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerListen {

	/**
	 * 日志
	 */
	private final static Logger LOG = LoggerFactory.getLogger(ServerListen.class);

	/**
	 * 连接管道
	 */
	private final AsynchronousServerSocketChannel server;

	public static final BlockingQueue<BytePackage> queue = new ArrayBlockingQueue<BytePackage>(1024);

	public static final Map<String, AsynchronousSocketChannel> users = new HashMap<>();

	public static MessageInfo getMessage() {
		try {
			BytePackage pack = queue.take();
			if (pack == null)
				return null;
			else {
				return ObjectUtil.toObject(pack.getBody(), MessageInfo.class);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ServerListen() throws IOException {
		// 设置线程数为CPU核数
		AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup
				.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
		server = AsynchronousServerSocketChannel.open(channelGroup);
		// 重用端口
		server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		// 绑定端口并设置连接请求队列长度
		server.bind(new InetSocketAddress(8383), 80);
	}

	public void listen() {
		LOG.info("Server start listen .......");

		// 开始监听, 传入一个附件, 一个任务器
		// 将消息队列放进每个连接中.
		server.accept(queue, new AcceptHandler(server));
	}

	public static void main(String[] args) throws IOException {
		new ServerListen().listen();
		while (true) {
			try {
				Thread.sleep(5000);
				// BytePackage pack = UserStorage.queue.take();
				// if (pack != null) {
				// MessageInfo msg = ObjectUtil.toObject(pack.getBody(),
				// MessageInfo.class);
				// System.out.println(msg);
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
