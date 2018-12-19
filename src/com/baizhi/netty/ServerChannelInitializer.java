package com.baizhi.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 皇甫
 */
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        //创建套接字管道
        ChannelPipeline pipeline = sc.pipeline();
        //挂在最终处理者
        pipeline.addLast(new ServerChannelHandlerAdapter());
    }
}
