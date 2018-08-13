package org.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.aio.entity.BytePackage;
import org.aio.entity.MessageInfo;
import org.aio.entity.UserStorage;
import org.aio.tools.ByteTool;
import org.aio.tools.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

	/**
	 * 日志
	 */
	private final static Logger LOG = LoggerFactory.getLogger(AcceptHandler.class);

	private AsynchronousServerSocketChannel server;
	private ByteBuffer buffer;

	public AcceptHandler(AsynchronousServerSocketChannel server) {
		this.server = server;
		// 初始化一个容器用于读数据
		this.buffer = ByteBuffer.allocate(1024);
	}

	/**
	 * 成功连接
	 * 
	 * @param result
	 *            当前管道
	 * @param attachment
	 *            附件
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void completed(AsynchronousSocketChannel channel, Object attachment) {
		try {
			LOG.info("Server accept......"
					+ ((InetSocketAddress) channel.getLocalAddress()).getAddress().getHostAddress());
			UserStorage.users.put(String.valueOf(UserStorage.users.size()), channel);
			// 成功连接,继续等待下个连接
			server.accept(attachment, this);
			// 创建一个数据包传给读取器将读取的包解析后放入其中
			BytePackage pack = new BytePackage();
			// 开始读取数据
			channel.read(this.buffer, pack,
					new ServerReadHandler((BlockingQueue<BytePackage>) attachment, this.buffer));
			AsynchronousSocketChannel userCh = UserStorage.users.get(UserStorage.getMessage().getTargetIP());
			if (userCh != null) {
				userCh.write(ByteBuffer.wrap(UserStorage.getMessage().getMessage().getBytes()), pack,
						new ServerWriteHandler(channel, this.buffer));
			} else {
				MessageInfo msg = new MessageInfo();
				msg.setMessage("用户不在线");
				msg.setTargetIP("0");
				BytePackage p = new BytePackage();
				p.setBody(ObjectUtil.toByteArray(msg));
				p.setLength(p.getBody().length + 8);
				p.setTotal(200);
				try {
					channel.write(ByteBuffer.wrap(ByteTool.formatByte(p)));
					channel.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		LOG.error("连接失败.........");
	}

}
