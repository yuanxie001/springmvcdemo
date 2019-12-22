package netty;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 时间服务器, 简单的nio处理.
 */
public class NioTimeServer {

    public static void main(String[] args) {
        int port = 8080;
        MulitpleexerTimeServer timeServer = new MulitpleexerTimeServer(port);
        System.out.println("开始处理nio版本的");
        new Thread(timeServer).start();
    }

    public static class MulitpleexerTimeServer implements Runnable {

        private Selector selector;
        private ServerSocketChannel serverSocketChannel;
        private volatile boolean stop;

        public MulitpleexerTimeServer(int port) {
            try {
                selector = Selector.open();
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }

        @Override
        public void run() {
            while (!stop) {
                try {
                    selector.select(1000);
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey next = iterator.next();
                        iterator.remove();
                        try {
                            handleInput(next);
                        }catch (IOException e){

                            if (next != null){
                                next.cancel();
                                if (next.channel() != null){
                                    next.channel().close();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleInput(SelectionKey key) throws IOException {
            if (key.isValid()) {
                if (key.isAcceptable()) {
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                }

                if (key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    int readBytes = sc.read(readBuffer);
                    if (readBytes > 0) {
                        readBuffer.flip();
                        byte[] bytes = new byte[readBuffer.remaining()];
                        readBuffer.get(bytes);
                        String body = new String(bytes, "UTF-8");
                        System.out.println("The time server recevice order:" + body);
                        dowrite(sc,System.currentTimeMillis()+"");
                    } else if (readBytes < 0){
                        key.cancel();
                        sc.close();
                    }

                }
            }
        }

        private void dowrite(SocketChannel channel,String response) throws IOException{
            if (StringUtils.isNoneBlank(response)){
                byte[] bytes = response.getBytes();
                ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
                writeBuffer.put(bytes);
                writeBuffer.flip();
                channel.write(writeBuffer);
            }
        }
    }

}
