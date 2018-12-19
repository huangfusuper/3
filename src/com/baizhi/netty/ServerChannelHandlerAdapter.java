package com.baizhi.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;

public class ServerChannelHandlerAdapter extends ChannelHandlerAdapter {
    /**
     * @param ctx    处理者
     * @param msg   请求消息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf bb = (ByteBuf)msg;
        System.out.println("服务器收到请求为:"+bb.toString(CharsetUtil.UTF_8));
        ChannelFuture channelFuture = ctx.writeAndFlush(msg);
        //关闭通道
        channelFuture.addListener(ChannelFutureListener.CLOSE);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println(cause.getMessage());
    }


}
