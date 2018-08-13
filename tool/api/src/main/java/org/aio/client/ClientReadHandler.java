package org.aio.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.BlockingQueue;

import org.aio.entity.BytePackage;
import org.aio.tools.ByteTool;

public class ClientReadHandler extends Thread implements CompletionHandler<Integer, Object> {

	private AsynchronousSocketChannel channel;
	private ByteBuffer buffer;
	private BytePackage pack;
	private BlockingQueue<BytePackage> sucess;

	public ClientReadHandler(AsynchronousSocketChannel channel, BlockingQueue<BytePackage> sucess) {
		this.channel = channel;
		buffer = ByteBuffer.allocate(1024);
		pack = new BytePackage();
		this.sucess = sucess;
	}

	@Override
	public void completed(Integer result, Object attachment) {
		if (result > 0) {
			// 开始读取数据
			this.buffer.flip();
			BytePackage pack = (BytePackage) attachment;
			try {
				ByteTool.readByte(this.buffer, pack, this.sucess);
				this.buffer.compact();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		System.out.println("客户端读取失败.....");
	}

	@Override
	public void run() {
		this.channel.read(this.buffer, this.pack, this);
	}

}
