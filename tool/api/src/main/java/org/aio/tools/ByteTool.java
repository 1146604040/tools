package org.aio.tools;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

import org.aio.entity.BytePackage;

public class ByteTool {

	/**
	 * 将字节信息格式化为标准字节包 </br>
	 * <h3>标准字节包:前4字节为包头<br/>
	 * 接着32字节为消息id<br/>
	 * 4字节为消息长度<br/>
	 * 剩下的为消息体</h3>
	 * 
	 * @param pack
	 * @return 完整消息字节包
	 */
	public static byte[] formatByte(BytePackage pack) {
		ByteBuffer buffer = ByteBuffer.allocate(pack.getLength() + 40);
		buffer.putInt(pack.getTotal());
		buffer.put(pack.getId());
		buffer.putInt(pack.getLength());
		buffer.put(pack.getBody());
		return buffer.array();
	}

	public static BytePackage readByte(ByteBuffer buffer, BytePackage pack, BlockingQueue<BytePackage> sucess)
			throws InterruptedException {
		while (buffer.remaining() > 0) {// 如果获取到了数据
			if (pack.packLength() < 40) {// 如果包头未完整,获取完整包头
				byte[] ti;
				if (buffer.remaining() > 40 - pack.packLength()) {// 如果数据大于包头大小,获取剩余包头,反之获取所有
					ti = new byte[40 - pack.packLength()];
				} else {
					ti = new byte[buffer.remaining()];
				}
				buffer.get(ti);
				pack.setPack(joinByte(pack.getPack(), ti));
			} else {// 如果包头完整
				if (pack.getTotal() <= 0) {
					pack.setTotal(NumberUtil.byte4ToInt(pack.getPack(), 0));
					pack.setId(new byte[32]);
					System.arraycopy(pack.getPack(), 4, pack.getId(), 0, 32);
					pack.setLength(NumberUtil.byte4ToInt(pack.getPack(), 36));
				}
				if (pack.getBody() == null || pack.getLength() > pack.getBody().length) {// 如果传输内容还没读完继续读取
					if (pack.getBody() == null) {
						pack.setBody(new byte[0]);
					}
					if (buffer.remaining() >= pack.getLength() - pack.getBody().length) {
						byte[] ti = new byte[pack.getLength() - pack.getBody().length];
						buffer.get(ti);
						pack.setBody(joinByte(pack.getBody(), ti));
						sucess.put((BytePackage) ObjectUtil.clone(pack));
						pack = new BytePackage();
					} else {
						byte[] ti = new byte[buffer.remaining()];
						buffer.get(ti);
						pack.setBody(joinByte(pack.getBody(), ti));
					}
				} else {// 读取完传输内容后,将完整的数据包添加到集合中,并初始化一个数据包读取剩下的数据
					sucess.put((BytePackage) ObjectUtil.clone(pack));
					pack = new BytePackage();
				}
			}
		}
		return pack;
	}

	public static boolean readByteOne(ByteBuffer buffer, BytePackage pack) throws InterruptedException {
		while (buffer.remaining() > 0) {// 如果获取到了数据
			if (pack.packLength() < 40) {// 如果包头未完整,获取完整包头
				byte[] ti;
				if (buffer.remaining() > 40 - pack.packLength()) {// 如果数据大于包头大小,获取剩余包头,反之获取所有
					ti = new byte[40 - pack.packLength()];
				} else {
					ti = new byte[buffer.remaining()];
				}
				buffer.get(ti);
				pack.setPack(joinByte(pack.getPack(), ti));
			} else {// 如果包头完整
				if (pack.getTotal() <= 0) {
					pack.setTotal(NumberUtil.byte4ToInt(pack.getPack(), 0));
					pack.setId(new byte[32]);
					System.arraycopy(pack.getPack(), 4, pack.getId(), 0, 32);
					pack.setLength(NumberUtil.byte4ToInt(pack.getPack(), 36));
				}
				if (pack.getBody() == null || pack.getLength() > pack.getBody().length) {// 如果传输内容还没读完继续读取
					if (pack.getBody() == null) {
						pack.setBody(new byte[0]);
					}
					if (buffer.remaining() >= pack.getLength() - pack.getBody().length) {
						byte[] ti = new byte[pack.getLength() - pack.getBody().length];
						buffer.get(ti);
						pack.setBody(joinByte(pack.getBody(), ti));
						return true;
					} else {
						byte[] ti = new byte[buffer.remaining()];
						buffer.get(ti);
						pack.setBody(joinByte(pack.getBody(), ti));
					}
				} else {
					return true;
				}
			}
		}
		return false;
	}

	public static byte[] joinByte(byte[] bt, byte[] tar) {
		byte[] obj = new byte[bt.length + tar.length];
		for (int i = 0; i < obj.length; i++) {
			if (i >= bt.length) {
				obj[i] = tar[i];
			} else {
				obj[i] = bt[i];
			}
		}
		return obj;
	}

	private static ByteBuffer buffer;

	public static void main(String[] args) {
		ByteBuffer b = ByteBuffer.allocate(50);
		b.putInt(200);
		// b.flip();
		System.out.println("position" + b.position());
		System.out.println("remaining" + b.remaining());
		ByteBuffer bt = b.compact();
		System.out.println("bt" + bt.position());
		System.out.println("bt" + bt.remaining());
		byte[] bb = new byte[4];
		System.out.println(b.get(bb));
		System.out.println("position" + b.position());
		System.out.println("remaining" + b.remaining());
		b.clear();
		System.out.println("position" + b.position());
		System.out.println("remaining" + b.remaining());
		// System.out.println(Integer.toBinaryString(3));
		// System.out.println(Integer.toBinaryString(0xff));
		// System.out.println(3 & 0xff);

		// System.out.println("capacity" + b.capacity());
		//
		// b.put(Integer.valueOf(200).toString().getBytes());
		// System.out.println("position" + b.position());
		// b.flip();
		// System.out.println("position" + b.position());
		// System.out.println("remaining" + b.remaining());
		// System.out.println("compact" + b.compact());
		// System.out.println(b.get(0));
		// System.out.println(Integer.valueOf(200).toString().getBytes().length);
		// System.out.println(Integer.toBinaryString(200));
		// System.out.println("\r\n".getBytes().length);
		//
		// System.out.println(Integer.MAX_VALUE);
		// System.out.println(Long.MAX_VALUE);
		// System.out.println(UUID.randomUUID().toString().getBytes().length);

		/*
		 * initbuffer(); testByte(); testChar(); testMark(); testInt();
		 * testFloat(); testDouble(); testLong(); testRemaining();
		 * testOverFlow(); testReset(); testClear(); testCompact();
		 */
	}

	/**
	 * 初始化缓存空间
	 */
	public static void initbuffer() {
		buffer = buffer.allocate(32);
		System.out.println("===============init status============");
		System.out.println("position:" + buffer.position());
		System.out.println("limit:" + buffer.limit());
		System.out.println("capacity:" + buffer.capacity());
		System.out.println("remaining:" + buffer.remaining());
	}

	/**
	 * 测试Byte，占用一个字节
	 */
	public static void testByte() {
		System.out.println("===============put byte============");
		// 字节
		byte bbyte = 102;
		buffer.put(bbyte);// buffer
		buffer.get(0);// byte
		System.out.println("position:" + buffer.position());
		System.out.println("limit:" + buffer.limit());
		System.out.println("capacity:" + buffer.capacity());
		System.out.println("======get byte:" + buffer.get(0));
		System.out.println("remaining:" + buffer.remaining());
	}

	/**
	 * 测试Char，占用2个字节
	 */
	public static void testChar() {
		System.out.println("===============put char============");
		// 字符
		char aChar = 'a';
		buffer.putChar(aChar);
		System.out.println("position:" + buffer.position());
		System.out.println("limit:" + buffer.limit());
		System.out.println("capacity:" + buffer.capacity());
		System.out.println("======get Char:" + buffer.getChar(1));
		System.out.println("remaining:" + buffer.remaining());
	}

	/**
	 * 标记位置，以便reset，返回这个标记位置
	 */
	public static void testMark() {
		// 标记位置
		buffer.mark();
		System.out.println("===============mark============");
		System.out.println("position:" + buffer.position());
		System.out.println("limit:" + buffer.limit());
		System.out.println("capacity:" + buffer.capacity());
		System.out.println("remaining:" + buffer.remaining());
	}

	/**
	 * 测试int，占用4个字节
	 */
	public static void testInt() {
		System.out.println("===============put int============");
		// int
		int int4 = 4;
		buffer.putInt(int4);
		System.out.println("position:" + buffer.position());
		System.out.println("limit:" + buffer.limit());
		System.out.println("capacity:" + buffer.capacity());
		// 这里为什么从第三个字节开始读取，因为前面一个字节和一个字符总共三个字节
		System.out.println("======get int:" + buffer.getInt(3));
		System.out.println("remaining:" + buffer.remaining());
	}

	/**
	 * 测试float，占用4个字节
	 */
	public static void testFloat() {
		System.out.println("===============put float============");
		// float
		float float5 = 10;
		buffer.putFloat(float5);
		System.out.println("position:" + buffer.position());
		System.out.println("limit:" + buffer.limit());
		System.out.println("capacity:" + buffer.capacity());
		// 这里为什么从第7个字节开始读取，因为前面一个字节和一个字符，一个int总共7个字节
		System.out.println("======get float:" + buffer.getFloat(7));
		System.out.println("remaining:" + buffer.remaining());
	}

	/**
	 * 测试double，占用8个字节
	 */
	public static void testDouble() {
		System.out.println("===============put double============");
		// double
		double double6 = 20.0;
		buffer.putDouble(double6);
		System.out.println("position:" + buffer.position());
		System.out.println("limit:" + buffer.limit());
		System.out.println("capacity:" + buffer.capacity());
		// 这里为什么从第11个字节开始读取，因为前面一个字节和一个字符，一个int,一个float总共11个字节
		System.out.println("======get double:" + buffer.getDouble(11));
		System.out.println("remaining:" + buffer.remaining());
	}

	/**
	 * 测试Long，占用8个字节
	 */
	public static void testLong() {
		System.out.println("===============put long============");
		// long
		long long7 = (long) 30.0;
		buffer.putLong(long7);
		System.out.println("position:" + buffer.position());
		System.out.println("limit:" + buffer.limit());
		System.out.println("capacity:" + buffer.capacity());
		// 这里为什么从第19个字节开始读取，因为前面一个字节和一个字符，一个int,一个float，一个double总共19个字节
		System.out.println("======get long:" + buffer.getLong(19));
		System.out.println("remaining:" + buffer.remaining());
	}

	/**
	 * 测试字节缓冲的剩余空间函数
	 */
	public static void testRemaining() {
		System.out.println("======buffer 剩余空间大小:" + buffer.remaining());
		System.out.println("remaining:" + buffer.remaining());
	}

	/**
	 * 测试添加元素字节长度，大于剩余空间时的情况
	 */
	public static void testOverFlow() {
		/*
		 * Exception in thread "main" java.nio.BufferOverflowException at
		 * java.nio.Buffer.nextPutIndex(Buffer.java:519) at
		 * java.nio.Heapbuffer.putLong(Heapbuffer.java:417) at
		 * socket.Testbuffer.main(Testbuffer.java:60)
		 * 超出空间，则抛出BufferOverflowException异常
		 */
		try {
			buffer.putLong((long) 30.0);
			System.out.println("remaining:" + buffer.remaining());
		} catch (BufferOverflowException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试回到标记，position为标记的mark
	 */
	public static void testReset() {
		System.out.println("===============reset============");
		// 回到mark标记位置，position为标记的mark
		buffer.reset();
		System.out.println("position:" + buffer.position());
		System.out.println("limit:" + buffer.limit());
		System.out.println("capacity:" + buffer.capacity());
		System.out.println("======get  int from mark:" + buffer.getInt(3));
		System.out.println("remaining:" + buffer.remaining());
		// 重新，从标记位置put一个int值，原来的内容被覆盖掉
		int int5 = 5;
		buffer.putInt(int5);
		System.out.println("position:" + buffer.position());
		System.out.println("limit:" + buffer.limit());
		System.out.println("capacity:" + buffer.capacity());
		System.out.println("======get int from mark after put new int value:" + buffer.getInt(3));
		System.out.println("remaining:" + buffer.remaining());
	}

	/**
	 * clear重置position，mark，limit位置，原始缓存区内容并不清掉
	 */
	public static void testClear() {
		System.out.println("===============clear============");
		// clear重置position，mark，limit位置，原始缓存区内容并不清掉
		buffer.clear();
		System.out.println("position:" + buffer.position());
		System.out.println("limit:" + buffer.limit());
		System.out.println("capacity:" + buffer.capacity());
		System.out.println("======get int  after clear:" + buffer.getInt(3));

	}

	public static void testCompact() {
		System.out.println("===============compact============");
		/*
		 * compact操作用于当 while (in.read(buf) >= 0 || buf.position != 0) {
		 * buf.flip(); out.write(buf); buf.compact(); // In case of partial
		 * write } 当out发送数据，即读取buf的数据，write方法可能只发送了部分数据，buf里还有剩余，
		 * 这时调用buf.compact()函数将position与limit之间的数据，copy到buf的0到limit-position，
		 * 进行压缩（非实际以压缩，只是移动）， 以便下次 向写入缓存。
		 */

		buffer.compact();
		System.out.println("position:" + buffer.position());
		System.out.println("limit:" + buffer.limit());
		System.out.println("capacity:" + buffer.capacity());
		System.out.println("======get int:" + buffer.getInt(3));
		System.out.println("===============flip============");
		/*
		 * buf.put(magic); // Prepend header in.read(buf); // Read data into
		 * rest of buffer buf.flip(); // Flip buffer out.write(buf);
		 * 当in从缓冲中读取数据后，如果想要将缓存中的数据发送出去，则调用buf.flip()函数，limit为当前position，
		 * position为0， / // buffer.flip();
		 * System.out.println("===============rewind============"); /*
		 * out.write(buf); // Write remaining data buf.rewind(); // Rewind
		 * buffer buf.get(array); // Copy data into array</pre></blockquote>
		 * 当out写出数据，即读取buf的数据后，如果想要从缓存中，从0位置，获取缓存数据，则调用buf.rewind()
		 */
		// buffer.rewind();

	}

}
