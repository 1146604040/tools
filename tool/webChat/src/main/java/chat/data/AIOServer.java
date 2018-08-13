package chat.data;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AIOServer {

	public final static int PORT = 9001;
	public final static String IP = "127.0.0.1";

	private AsynchronousServerSocketChannel server = null;

	public AIOServer() {
		try {
			// 异步通道 AsynchronousServerSocketChannel
			server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(IP, PORT));

			System.out.println("Server is started");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {

		// 注册事件和事件完成后的处理器
		server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

			final ByteBuffer buffer = ByteBuffer.allocate(1024);

			@Override
			public void completed(AsynchronousSocketChannel result, Object attachment) {

				System.out.println(Thread.currentThread().getName());
				Future<Integer> writeResult = null;

				try {
					buffer.clear();
					result.read(buffer).get(1000, TimeUnit.SECONDS);

					System.out.println(result.getRemoteAddress().toString() + ": " + new String(buffer.array()));

					buffer.flip();
					writeResult = result.write(buffer);
				} catch (InterruptedException | IOException | ExecutionException | TimeoutException e) {
					e.printStackTrace();
				} finally {
					server.accept(null, this);
					try {
						writeResult.get();
						result.close();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}

			@Override
			public void failed(Throwable exc, Object attachment) {
				System.out.println("failed:" + exc);
			}

		});
	}

	public static void main(String[] args) {
		new AIOServer().start();
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}