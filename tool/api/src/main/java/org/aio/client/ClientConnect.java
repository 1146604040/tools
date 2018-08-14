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

		AsynchronousChannelGroup group = AsynchronousChannelGroup
				.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
		AsynchronousSocketChannel channel = AsynchronousSocketChannel.open(group);
		channel.connect(new InetSocketAddress("localhost", 8383), ClientMsgStorage.read,
				new ClientAcceptHandler(channel));
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						BytePackage t = ClientMsgStorage.read.take();
						if (t != null && t.getBody().length > 0) {
							MessageInfo msg = ObjectUtil.toObject(t.getBody(), MessageInfo.class);
							System.out.println(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner sc = new Scanner(System.in);
				try {
					while (true) {
						MessageInfo m = new MessageInfo();
						System.out.println("目标:");
						m.setTargetIP(sc.nextLine());
						System.out.println("message:");
						m.setMessage(sc.nextLine());
						BytePackage p = new BytePackage();
						p.setBody(ObjectUtil.toByteArray(m));
						p.setTotal(200);
						p.setLength(p.getBody().length);
						ClientMsgStorage.write.put(p);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();
	}
}
