package com.baizhi.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

/**
 * @author 皇甫
 */
public class ClientChannelHandlerAdapter extends ChannelHandlerAdapter {
    /**
     * 主动发送消息
     * @param ctx   发送响应处理器
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String msg = "我爱你，昝昝";
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeBytes(msg.getBytes());
        ctx.writeAndFlush(buf);
    }

    /**
     * 被动发送消息   或者说 接收服务器响应
     * @param ctx
     * @param msg   服务器响应
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        System.out.println("客户端收到:"+buf.toString(CharsetUtil.UTF_8));
    }

    /**
     * 异常捕获
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println(cause.getMessage());
    }
}
