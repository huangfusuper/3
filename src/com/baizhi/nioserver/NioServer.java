package com.baizhi.nioserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;

/**
 * @author 皇甫
 */
public class NioServer {
    public static void main(String[] args) throws IOException {
        //创建监听新进来的TCP连接的通道的对象
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //设置通道端口
        ssc.bind(new InetSocketAddress("172.16.7.76", 9999));
        //设置通道非阻塞
        ssc.configureBlocking(false);
        //创建通道选择器
        Selector selector = Selector.open();
        //注册ACCEPT事件类型 转发
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        //迭代遍历事件key
        while (true){
            //拿到事件key的集合
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()){
                //拿到具体时间的key值
                SelectionKey key = keys.next();
                //判断是否为转发事件
                if(key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    //立即返回一个不为null的SocketChannel事件
                    SocketChannel s = channel.accept();
                    //开始注册读
                    s.configureBlocking(false);
                    s.register(selector, SelectionKey.OP_READ,new ByteArrayInputStream(new byte[1024]));
                    //如果已经是读事件 则开始处理读请求
                }else if(key.isReadable()){
                    SocketChannel channel = (SocketChannel)key.channel();
                    //设置读的缓冲区
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    //设置写缓冲
                    ByteArrayOutputStream baos = (ByteArrayOutputStream)key.attachment();
                    int n = channel.read(byteBuffer);
                    if(n==-1){
                        //注册写
                        channel.register(selector,SelectionKey.OP_WRITE,baos);
                    }else{
                        byteBuffer.flip();
                        baos.write(byteBuffer.array(),0,n);
                    }

                }else if(key.isWritable()){
                    System.out.println("处理写...");

                    //处理写事件
                    SocketChannel s= (SocketChannel) key.channel();
                    //根据请求参数给出响应
                    ByteArrayOutputStream att = (ByteArrayOutputStream)key.attachment();
                    System.out.println("服务器收到："+new String(att.toByteArray()));
                    s.write(ByteBuffer.wrap((new Date().toLocaleString()).getBytes()));
                    s.shutdownOutput();//告知写结束
                    s.close(); //关闭通道
                }
                //移除key
                keys.remove();

            }
        }
    }
}

