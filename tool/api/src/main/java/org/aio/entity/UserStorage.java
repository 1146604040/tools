package org.aio.entity;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.aio.tools.ObjectUtil;

public final class UserStorage {
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

}
