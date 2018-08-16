package org.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import org.aio.entity.BytePackage;
import org.aio.entity.MessageInfo;
import org.aio.entity.UserInfo;
import org.aio.tools.ByteTool;
import org.aio.tools.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerReadHandler implements CompletionHandler<Integer, BytePackage> {

	private final static Logger log = LoggerFactory.getLogger(ServerReadHandler.class);
	private final MessageDispose md;
	private final ByteBuffer buffer;
	private final AsynchronousSocketChannel channel;
	private final UserInfo user;

	public ServerReadHandler(AsynchronousSocketChannel channel, MessageDispose md, ByteBuffer buffer) {
		this.channel = channel;
		this.buffer = buffer;
		this.md = md;
		this.user = new UserInfo(1024);
		try {
			this.user.setIp(((InetSocketAddress) channel.getLocalAddress()).getAddress().getHostAddress());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		this.user.setStartConnection(new Date());
	}

	@Override
	public void completed(Integer result, BytePackage pack) {
		if (result > 0) {// 如果读取到了数据
			this.buffer.flip();// 将指针指到起始位
			try {
				// 将数据解析为格式包,并将一个完整包添加到队列中,剩下的数据包等下次读取
				if (user.getLastRead() == null) {// 第一次读取
					if (ByteTool.readByteOne(buffer, pack)) {
						UserInfo info = ObjectUtil.toObject(pack.getBody(), UserInfo.class);
						if (info != null) {
							user.setId(info.getId());
							user.setUserName(info.getUserName());
							user.setPassword(info.getPassword());
							user.setLastRead(new Date());
							md.addUser(user);
							MessageInfo msg = new MessageInfo();
							msg.setMessage("login in ....");
							BytePackage p = new BytePackage();
							p.setTotal(200);
							p.setId(info.getId().getBytes());
							p.setBody(ObjectUtil.toByteArray(msg));
							p.setLength(p.getBody().length);
							ByteBuffer wb = ByteBuffer.wrap(ByteTool.formatByte(p));
							channel.write(wb, wb, new ServerWriteHandler(channel, user));
							pack = new BytePackage();
						} else {
							log.error("登陆失败....");
						}
					} else {
						this.buffer.compact();
						this.channel.read(this.buffer, pack, this);// 等待下次数据
						return;
					}
				} else {
					user.setLastRead(new Date());
				}

				pack = ByteTool.readByte(this.buffer, pack, md.getRead());
				this.buffer.compact();
				this.channel.read(this.buffer, pack, this);// 等待下次数据
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, BytePackage attachment) {
		System.out.println("读取失败");
	}
}
