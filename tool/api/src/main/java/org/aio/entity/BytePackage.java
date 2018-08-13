package org.aio.entity;

import org.aio.exception.MessageException;

public class BytePackage {

	/**
	 * 消息头 200:表示正常内容.4字节
	 */
	private int total;

	/**
	 * 消息长度,4字节
	 */
	private int length;

	/**
	 * 包长
	 */
	private int packLength;

	/**
	 * 消息內容
	 */
	private byte[] body;

	private byte[] pack = new byte[0];

	public byte[] getPack() {
		return pack;
	}

	public void setPack(byte[] pack) {
		packLength = pack.length;
		this.pack = pack;
	}

	public int packLength() {
		return packLength;
	}

	public int getTotal() {
		return total;
	}

	public int getLength() {
		return length;
	}

	public byte[] getBody() {
		return body;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setBody(byte[] body) {
		if (body.length > Integer.MAX_VALUE - 8)
			throw new MessageException("消息内容过大");
		this.body = body;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
