package org.aio.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.aio.entity.BytePackage;
import org.aio.tools.ByteTool;

public class ClientReadHandler implements CompletionHandler<Integer, Object> {

	private AsynchronousSocketChannel channel;
	private ByteBuffer buffer;

	public ClientReadHandler(AsynchronousSocketChannel channel, ByteBuffer buffer) {
		this.channel = channel;
		this.buffer = buffer;
	}

	@Override
	public void completed(Integer result, Object attachment) {
		if (result > 0) {// 如果有数据,开始读
			this.buffer.flip();
			try {
				BytePackage pack = (BytePackage) attachment;
				pack = ByteTool.readByte(this.buffer, pack, ClientMsgStorage.read);
				// 继续下一次的读
				this.buffer.compact();
				this.channel.read(this.buffer, pack, this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		System.out.println("客户端读取失败.....");
	}

}
