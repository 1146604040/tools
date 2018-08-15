package org.aio.entity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.aio.tools.ObjectUtil;

public final class UserStorage {
	public static final BlockingQueue<BytePackage> queue = new ArrayBlockingQueue<BytePackage>(1024);

	public static final Map<String, AsynchronousSocketChannel> users = new HashMap<>();

	public static MessageInfo getMessage() {
		try {
			BytePackage pack = queue.take();
			if (pack == null)
				return null;
			else {
				return ObjectUtil.toObject(pack.getBody(), MessageInfo.class);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		// try {
		// readZipFile("E:/360Downloads/李兴华_Java8 编程开发视频教程[215讲].zip");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		Decompressing2();
	}

	public static void readZipFile(String file) throws Exception {
		ZipFile zf = new ZipFile(file, Charset.forName("UTF-8"));
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		ZipInputStream zin = new ZipInputStream(in, Charset.forName("UTF-8"));
		ZipEntry ze;
		while ((ze = zin.getNextEntry()) != null) {
			if (ze.isDirectory()) {
			} else {
				System.err.println("file - " + ze.getName() + " : " + ze.getSize() + " bytes");
				long size = ze.getSize();
				if (size > 0) {
					BufferedReader br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)));
					String line;
					while ((line = br.readLine()) != null) {
						System.out.println(line);
					}
					br.close();
				}
				System.out.println();
			}
		}
		zin.closeEntry();
	}

	public static void Decompressing2() throws IOException {
		String path = "E:\\360Downloads";
		ZipEntry zipEntry = null;
		try (
				// ZipInputStream读取压缩文件
				ZipInputStream zipInputStream = new ZipInputStream(
						new FileInputStream(path + "\\李兴华_Java8 编程开发视频教程[215讲].zip"), Charset.forName("GBK"));
				// 写入到缓冲流中
				BufferedInputStream bufferedInputStream = new BufferedInputStream(zipInputStream);) {
			File fileOut = null;
			// 读取压缩文件中的一个文件
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				// 若当前zipEntry是一个文件夹
				if (zipEntry.isDirectory()) {
					fileOut = new File(path + "//" + zipEntry.getName());
					// 在指定路径下创建文件夹
					if (!fileOut.exists()) {
						fileOut.mkdirs();
					}
					// 若是文件
				} else {
					// 原文件名与指定路径创建File对象(解压后文件的对象)
					fileOut = new File(path, zipEntry.getName());
					try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut);
							BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);) {
						// 将文件写入到指定file中
						int b = 0;
						while ((b = bufferedInputStream.read()) != -1) {
							bufferedOutputStream.write(b);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
