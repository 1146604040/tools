package org.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.aio.entity.ByteFactory;
import org.aio.entity.BytePackage;
import org.aio.tools.ByteTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientReadHandler implements CompletionHandler<Integer, BytePackage> {

	private final static Logger log = LoggerFactory.getLogger(ClientReadHandler.class);
	private AsynchronousSocketChannel channel;
	private ByteBuffer buffer;
	private ByteFactory rw;

	/**
	 * 
	 * @param channel
	 *            连接
	 * @param buffer
	 *            读取缓存区
	 * @param rw
	 *            读写队列
	 */
	public ClientReadHandler(AsynchronousSocketChannel channel, ByteBuffer buffer, ByteFactory rw) {
		this.channel = channel;
		this.buffer = buffer;
		this.rw = rw;
	}

	@Override
	public void completed(Integer result, BytePackage pack) {
		if (result > 0) {// 如果有数据,开始读
			this.buffer.flip();
			try {
				pack = ByteTool.readByte(this.buffer, pack, rw.getRead());
				this.buffer.compact();
				this.channel.read(this.buffer, pack, this);// 等待下一次数据
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, BytePackage pack) {
		try {
			log.error(
					"Read error:====>" + ((InetSocketAddress) channel.getLocalAddress()).getAddress().getHostAddress());
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
