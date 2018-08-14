package org.aio.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.BlockingQueue;

import org.aio.entity.BytePackage;
import org.aio.tools.ByteTool;

public class ClientWriteHandler implements CompletionHandler<Integer, Object> {

	private AsynchronousSocketChannel channel;

	public ClientWriteHandler(AsynchronousSocketChannel channel) {
		this.channel = channel;
	}

	@Override
	public void completed(Integer result, Object attachment) {
		if (result > 0) {
			// 还有未写完的数据
			this.channel.write((ByteBuffer) attachment, attachment, this);
		} else {
			BlockingQueue<BytePackage> read = ClientMsgStorage.read;
			BytePackage pack;
			try {// 如果没有数据要写就一直等在这里
				pack = ClientMsgStorage.write.take();
				if (pack != null) {
					ByteBuffer buffer = ByteBuffer.wrap(ByteTool.formatByte(pack));
					this.channel.write(buffer, buffer, this);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		System.out.println("客户端写失败.....");
	}

}