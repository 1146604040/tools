package org.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import org.aio.entity.BytePackage;
import org.aio.entity.UserInfo;
import org.aio.tools.ByteTool;
import org.aio.tools.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerReadHandler implements CompletionHandler<Integer, Object> {

	private final static Logger log = LoggerFactory.getLogger(ServerReadHandler.class);
	private BlockingQueue<BytePackage> sucess;
	private ByteBuffer buffer;
	private AsynchronousSocketChannel channel;
	private UserInfo user;

	public ServerReadHandler(AsynchronousSocketChannel channel, BlockingQueue<BytePackage> sucess, ByteBuffer buffer) {
		this.channel = channel;
		this.buffer = buffer;
		this.sucess = sucess;
		this.user = new UserInfo(1024);
		try {
			this.user.setIp(((InetSocketAddress) channel.getLocalAddress()).getAddress().getHostAddress());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		this.user.setStartConnection(new Date());
	}

	@Override
	public void completed(Integer result, Object attachment) {
		if (result > 0) {// 如果读取到了数据
			this.buffer.flip();// 将指针指到起始位
			BytePackage pack = (BytePackage) attachment;
			try {
				// 将数据解析为格式包,并将一个完整包添加到队列中,剩下的数据包等下次读取
				if (user.getLastRead() == null) {// 第一次读取
					if(ByteTool.readByteOne(buffer, pack)){
						UserInfo info = ObjectUtil.toObject(pack.getBody(), UserInfo.class);
						user.setId(info.getId());
						user.setUserName(info.getUserName());
						user.setPassword(info.getPassword());
					}else{
						this.buffer.compact();
						this.channel.read(this.buffer, pack, this);// 等待下次数据
						return;
					}
				}else{
					user.setLastRead(new Date());
				}

				pack = ByteTool.readByte(this.buffer, pack, sucess);
				this.buffer.compact();
				this.channel.read(this.buffer, pack, this);// 等待下次数据
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void failed(Throwable exc, Object attachment) {
		System.out.println("读取失败");
	}

}
