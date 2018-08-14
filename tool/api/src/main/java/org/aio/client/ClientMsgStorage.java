package org.aio.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.aio.entity.BytePackage;

public class ClientMsgStorage {
	public final static BlockingQueue<BytePackage> read = new ArrayBlockingQueue<>(1024);
	public final static BlockingQueue<BytePackage> write = new ArrayBlockingQueue<>(1024);
}
