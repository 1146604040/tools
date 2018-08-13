package org.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

import org.aio.entity.BytePackage;
import org.aio.entity.MessageInfo;
import org.aio.tools.ByteTool;
import org.aio.tools.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientConnect {
	/**
	 * 日志
	 */
	private final static Logger LOG = LoggerFactory.getLogger(ClientConnect.class);

	public static void main(String[] args) throws IOException, InterruptedException {
		BlockingQueue<BytePackage> sucess = new ArrayBlockingQueue<>(1024);

		AsynchronousChannelGroup group = AsynchronousChannelGroup
				.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
		AsynchronousSocketChannel channel = AsynchronousSocketChannel.open(group);
		channel.connect(new InetSocketAddress("localhost", 8383), sucess, new ClientAcceptHandler(channel));
		new Thread(new Runnable() {
			public void run() {
				Scanner sc = new Scanner(System.in);
				for (;;) {
					System.out.println("输入消息");
					String line = sc.nextLine();
					if (line != null && !line.isEmpty()) {
						MessageInfo msg = new MessageInfo();
						msg.setMessage(line);
						System.out.println("输入目标:");
						msg.setTargetIP(sc.nextLine());
						BytePackage pack = new BytePackage();
						pack.setBody(ObjectUtil.toByteArray(msg));
						pack.setLength(pack.getBody().length + 8);
						try {
							sucess.put(pack);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();

		LOG.wait();
	}
}
