package org.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.BlockingQueue;

import org.aio.entity.BytePackage;
import org.aio.tools.ByteTool;

public class ServerReadHandler implements CompletionHandler<Integer, Object> {

	private BlockingQueue<BytePackage> sucess;
	private ByteBuffer buffer;

	public ServerReadHandler(BlockingQueue<BytePackage> sucess, ByteBuffer buffer) {
		this.sucess = sucess;
		this.buffer = buffer;
	}

	@Override
	public void completed(Integer result, Object attachment) {
		if (result > 0) {
			// 开始读取数据
			this.buffer.flip();
			BytePackage pack = (BytePackage) attachment;
			try {
				ByteTool.readByte(this.buffer, pack, sucess);
				this.buffer.compact();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {

	}

}
