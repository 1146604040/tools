package org.aio.entity;

import java.io.Serializable;

import org.aio.exception.MessageException;

public class BytePackage implements Serializable {

	/**
	 * 序列id
	 */
	private static transient final long serialVersionUID = 879678901974261351L;

	/**
	 * 消息头 200:表示正常内容.4字节
	 */
	private int total;

	/**
	 * 消息id,为32位uuid
	 * 
	 */
	private byte[] id;

	/**
	 * 消息长度,4字节
	 */
	private int length;

	/**
	 * 包头长
	 */
	private int packLength;

	/**
	 * 消息內容
	 */
	private byte[] body;

	/**
	 * 解析数据包时用来存放包头
	 */
	private byte[] pack = new byte[0];

	public byte[] getId() {
		return id;
	}

	public void setId(byte[] id) {
		this.id = id;
	}

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
