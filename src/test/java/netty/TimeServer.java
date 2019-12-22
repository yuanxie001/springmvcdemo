package netty;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * bio版本日期处理
 */
public class TimeServer {

    private static ExecutorService executorService = new ThreadPoolExecutor(3, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));

    public static void main(String[] args) {
        int port = 8080;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("The time server is start in port : " + port);
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                executorService.submit(new TimeServerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static class TimeServerHandler implements Runnable {
        private Socket socket;

        public TimeServerHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            BufferedInputStream in = null;
            PrintWriter out = null;
            try {
                System.out.println("time worker start:" + Thread.currentThread().getName());
                in = new BufferedInputStream(this.socket.getInputStream());
                out = new PrintWriter(this.socket.getOutputStream(), true);
                byte[] buffer = new byte[1024];
                int len = 0;
                if ((len=in.read(buffer, 0, 1024))!=0) {
                    String body = new String(buffer,0,len);
                    System.out.println("this time server order:" + body);
                }
                out.println(System.currentTimeMillis());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    out.close();
                }
                try {
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
