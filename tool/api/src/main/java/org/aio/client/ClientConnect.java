package org.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

import org.aio.entity.ByteFactory;
import org.aio.entity.BytePackage;
import org.aio.tools.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientConnect {
	/**
	 * 日志
	 */
	private final static Logger log = LoggerFactory.getLogger(ClientConnect.class);

	private static AsynchronousChannelGroup group;
	private static AsynchronousSocketChannel channel;

	/**
	 * 
	 * @param ip
	 *            主机名
	 * @param port
	 *            端口
	 * @param read
	 *            用于存放获取到的主机数据包队列
	 * @param write
	 *            用于存放需要发送给主机的数据包队列
	 * @throws IOException
	 */
	public void init(String ip, Integer port, 
			BlockingQueue<BytePackage> read, 
			BlockingQueue<BytePackage> write) throws IOException {
		log.info(DateUtils.getNowDateStr("yyyy-MM-dd hh:mm:ss") 
				+ ":[local:" + ip + "]\t"
				+ "[port:" + port + "]\t"
				+ "[readQueue:" + read.size() + "]\t"
				+ "[write:" + write.size() + "]\r\n");
		group = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(),
				Executors.defaultThreadFactory());
		channel = AsynchronousSocketChannel.open(group);
		ByteFactory factory = new ByteFactory(read, write);
		channel.connect(new InetSocketAddress(ip, port), factory, new ClientAcceptHandler(channel));
	}

}
