package com.baizhi.serverclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOServer {
    public static void main(String[] args) throws IOException {
        server();
    }

    public static void server() throws IOException {
        //创建服务
        ServerSocketChannel channel = ServerSocketChannel.open();
        //设置服务提供光端口
        channel.bind(new InetSocketAddress("172.16.7.76",9999));
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //开始读取和处理客户端响应
        while (true){
            //等待请求到来  否则处于阻塞状态
            System.out.println("端口9999正在对外提供服务中...................");
            final SocketChannel s = channel.accept();
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        //创建一个缓冲区
                        ByteBuffer allocate = ByteBuffer.allocate(1024);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        while (true){
                            //度进先将缓存清空
                            allocate.clear();
                            int read = s.read(allocate);
                            if(read==-1) break;
                            allocate.flip();
                            bos.write(allocate.array(), 0, read);
                        }
                        String str = new String(bos.toByteArray());
                        SocketAddress localAddress = channel.getLocalAddress();
                        System.out.println("访问者"+localAddress.toString()+"；访问者请求为:"+ str);
                        //处理请求相应客户端
                        if(str.matches("^.*\\?")){
                            str = str.replace("?", "");
                        }else if (str.matches("^.*\\？")){
                            str = str.replace("？", "");
                        }else if(str.matches("^.*吗")){
                            str = str.replace("吗", "！");
                        }else{
                            str =  "无法识别您的请求！请重试！";
                        }
                        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
                        s.write(byteBuffer);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        //告知结束
                        try {
                            s.shutdownOutput();
                            //关闭资源
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
