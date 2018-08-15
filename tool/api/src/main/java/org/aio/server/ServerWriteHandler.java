package org.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.aio.entity.BytePackage;
import org.aio.entity.MessageInfo;
import org.aio.tools.ByteTool;
import org.aio.tools.ObjectUtil;

/**
 * server向客户端写数据的handler
 * 
 * @author H02
 *
 */
public class ServerWriteHandler implements CompletionHandler<Integer, Object> {

	private AsynchronousSocketChannel channel;
	private String id;

	public ServerWriteHandler(AsynchronousSocketChannel channel, String id) {
		this.channel = channel;
		this.id = id;
	}

	@Override
	public void completed(Integer result, Object attachment) {
		if (result > 0) {//如果还有数据则继续写
			ByteBuffer buffer = (ByteBuffer) attachment;
			channel.write(buffer, buffer, this);
		} else {
			try {
				BytePackage pack = ServerListen.users.get(id).take();//从消息队列中获取一个数据包
				ByteBuffer buffer = ByteBuffer.wrap(ByteTool.formatByte(pack));
				channel.write(buffer, buffer, this);//发送一个数据包
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		System.out.println("wirte failed....");
	}

}
