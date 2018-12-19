package com.baizhi.serverclienty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;

public class Server {
    public static void main(String[] args) throws IOException {
        //创建服务转发ServerSocket
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //设置端口
        ssc.bind(new InetSocketAddress("172.16.7.74", 9987));
    }
}
