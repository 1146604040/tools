package org.aio.entity;

import java.io.Serializable;

public class MessageInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6594475319630806870L;

	/**
	 * 接收对象IP
	 */
	private String targetIP;

	/**
	 * 对象id
	 */
	private String targetId;

	/**
	 * 发送对象IP
	 */
	private String thisIP;

	/**
	 * 消息
	 */
	private String message;

	/**
	 * 文件
	 */
	private byte[][] files;

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getTargetIP() {
		return targetIP;
	}

	public String getThisIP() {
		return thisIP;
	}

	public String getMessage() {
		return message;
	}

	public void setTargetIP(String targetIP) {
		this.targetIP = targetIP;
	}

	public void setThisIP(String thisIP) {
		this.thisIP = thisIP;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public byte[][] getFiles() {
		return files;
	}

	public void setFiles(byte[][] files) {
		this.files = files;
	}

	@Override
	public String toString() {
		return "MessageInfo [targetIP=" + targetIP + ", thisIP=" + thisIP + ", message=" + message + ", files=" + files
				+ "]";
	}

}
