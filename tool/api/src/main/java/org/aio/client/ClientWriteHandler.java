package org.aio.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.BlockingQueue;

import org.aio.entity.BytePackage;
import org.aio.tools.ObjectUtil;

public class ClientWriteHandler extends Thread implements CompletionHandler<Integer, Object> {

	private AsynchronousSocketChannel channel;
	private BlockingQueue<BytePackage> sucess;

	public ClientWriteHandler(AsynchronousSocketChannel channel, BlockingQueue<BytePackage> sucess) {
		this.channel = channel;
		this.sucess = sucess;
	}

	@Override
	public void completed(Integer result, Object attachment) {
		if (result > 0) {
			// 还有未写完的数据
			this.channel.write((ByteBuffer) attachment, attachment, this);
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		System.out.println("客户端写失败.....");
	}

	@Override
	public void run() {
		BytePackage pack;
		for (;;) {
			try {
				pack = sucess.take();
				if (pack != null) {
					ByteBuffer buffer = ByteBuffer.wrap(ObjectUtil.toByteArray(pack));
					this.channel.write(buffer, buffer, this);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}