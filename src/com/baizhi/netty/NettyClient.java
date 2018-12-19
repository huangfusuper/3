package com.baizhi.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;

/**
 * @author 皇甫
 */
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        //设置启动引导  开机
        Bootstrap bt = new Bootstrap();
        //创建请求的线程池
        EventLoopGroup wi = new NioEventLoopGroup();
        //关联线程池
        bt.group(wi);
        //客户端实现类
        bt.channel(NioSocketChannel.class);
        //配置RPC通讯管道
        bt.handler(new ClientChannelInitializer());
        //设置服务器启动端口 并启动
        ChannelFuture cf = bt.connect("127.0.0.1", 9987).sync();
        //关闭通道
        cf.channel().closeFuture().sync();
        //关闭资源
        wi.shutdownGracefully();
    }
}
