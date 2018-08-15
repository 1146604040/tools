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

public class ClientWriteHandler implements CompletionHandler<Integer, ByteBuffer> {

	private final static Logger log = LoggerFactory.getLogger(ClientWriteHandler.class);
	private AsynchronousSocketChannel channel;
	private ByteFactory rw;
	private BytePackage pack;

	public ClientWriteHandler(AsynchronousSocketChannel channel, ByteFactory rw) {
		this.channel = channel;
		this.rw = rw;
	}

	@Override
	public void completed(Integer result, ByteBuffer buffer) {
		if (result > 0) {
			// 还有未写完的数据
			this.channel.write(buffer, buffer, this);
		} else {
			this.pack = rw.getByte();
			if (pack != null) {
				buffer = ByteBuffer.wrap(ByteTool.formatByte(pack));
				this.channel.write(buffer, buffer, this);
			}
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer buffer) {
		try {
			log.error("Write error:====>"
					+ ((InetSocketAddress) channel.getLocalAddress()).getAddress().getHostAddress());
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
