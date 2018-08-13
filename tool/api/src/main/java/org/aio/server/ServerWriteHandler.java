package org.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * server向客户端写数据的handler
 * 
 * @author H02
 *
 */
public class ServerWriteHandler implements CompletionHandler<Integer, Object> {

	private AsynchronousSocketChannel channel;
	private ByteBuffer buffer;

	public ServerWriteHandler(AsynchronousSocketChannel channel, ByteBuffer buffer) {
		this.channel = channel;
		this.buffer = buffer;
	}

	@Override
	public void completed(Integer result, Object attachment) {
		if (result > 0) {
			// 还有未写完数据继续写
			channel.write(this.buffer, attachment, this);
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		System.out.println("写出数据给客户端失败");
	}

}
