package com.baizhi.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 皇甫
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        //创建管道
        ChannelPipeline pipeline = sc.pipeline();
        //挂载最终处理者
        pipeline.addLast(new ClientChannelHandlerAdapter());
    }
}
