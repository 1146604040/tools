package org.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.aio.entity.BytePackage;
import org.aio.entity.MessageInfo;
import org.aio.entity.UserInfo;
import org.aio.tools.ByteTool;
import org.aio.tools.ObjectUtil;

/**
 * server向客户端写数据的handler
 * 
 * @author H02
 *
 */
public class ServerWriteHandler implements CompletionHandler<Integer, ByteBuffer> {

	private final AsynchronousSocketChannel channel;
	private final UserInfo user;

	public ServerWriteHandler(AsynchronousSocketChannel channel, UserInfo user) {
		this.channel = channel;
		this.user = user;
	}

	@Override
	public void completed(Integer result, ByteBuffer buffer) {
		if (result > 0) {// 如果还有数据则继续写
			channel.write(buffer, buffer, this);
		} else {
			try {
				buffer = ByteBuffer.wrap(ByteTool.formatByte(user.getWrite().take()));
				channel.write(buffer, buffer, this);// 发送一个数据包
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		System.out.println("wirte failed....");
	}
}
