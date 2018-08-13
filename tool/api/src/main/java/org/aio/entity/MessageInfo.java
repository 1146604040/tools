package org.aio.entity;

import java.io.File;
import java.io.Serializable;
import java.util.List;

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
	 * 发送对象IP
	 */
	private String thisIP;

	/**
	 * 消息
	 */
	private String message;

	/**
	 * 文件/图片
	 */
	private List<File> files;

	public String getTargetIP() {
		return targetIP;
	}

	public String getThisIP() {
		return thisIP;
	}

	public String getMessage() {
		return message;
	}

	public List<File> getFiles() {
		return files;
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

	public void setFiles(List<File> files) {
		this.files = files;
	}

}
