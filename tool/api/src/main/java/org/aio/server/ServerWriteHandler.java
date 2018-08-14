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

	public ServerWriteHandler(AsynchronousSocketChannel channel) {
		this.channel = channel;
	}

	@Override
	public void completed(Integer result, Object attachment) {
		if (result > 0) {
			System.out.println("write.....");
			channel.write((ByteBuffer) attachment, attachment, this);
		} else {
			try {
				BytePackage pack = ServerListen.queue.take();
				if (pack != null && pack.getBody().length > 0) {
					MessageInfo msg = ObjectUtil.toObject(pack.getBody(), MessageInfo.class);
					if (msg != null) {
						System.out.println(msg);
						AsynchronousSocketChannel ch = ServerListen.users.get(msg.getTargetIP());
						ByteBuffer buffer = ByteBuffer.wrap(ByteTool.formatByte(pack));
						if (ch.equals(this.channel)) {
							this.channel.write(buffer, buffer, this);
						} else {
							ch.write(buffer, buffer, new ServerWriteHandler(ch));
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		System.out.println("wirte failed....");
	}

}
