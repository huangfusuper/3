package com.baizhi.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.EventListener;

/**
 * @author 皇甫
 */
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        //启动服务引导 框架启动引导类。屏蔽网络通讯配置信息    -------开机启动引导
        ServerBootstrap sbt = new ServerBootstrap();
        //创建请求转发、请求响应的线程池                     --------异步请求  创建线程池
        EventLoopGroup boos = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        //关联线程 池组                                     -------将线程池和引导关联起来
        sbt.group(boos,worker);
        //设置服务端实现类                                   ------服务器端实现类
        sbt.channel(NioServerSocketChannel.class);
        //配置RPC通讯管道                                    ------未来接口回调，
        sbt.childHandler(new ServerChannelInitializer());
        //设置服务器的启动端口 启动服务
        System.out.println("9987监视中。。。。。。。。。。。。。。");
        ChannelFuture cf = sbt.bind(9987).sync();
        //关闭通讯管道
        cf.channel().closeFuture().sync();
        //关闭资源
        boos.shutdownGracefully();
        worker.shutdownGracefully();

    }
}
