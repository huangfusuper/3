package com.baizhi.netty2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * @author 皇甫
 */
public class NettyClient {
    /**创建启动引导*/
    private Bootstrap bt = new Bootstrap();
    /**创建地响应线程池*/
    private EventLoopGroup wi = new NioEventLoopGroup();

    public NettyClient() {
        //关联线程池
        bt.group(wi);
        //设置服务的实现类型
        bt.channel(NioSocketChannel.class);
    }

    public void clientStart(HostAndPost hostAndPost, final Object obj) throws InterruptedException {
        //接口回调  设置客户端具体实现内容
        bt.handler(new ChannelInitializer<SocketChannel>(){
            @Override
            protected void initChannel(SocketChannel sc) throws Exception {
                //创建管道
                ChannelPipeline pipeline = sc.pipeline();
                pipeline.addLast(new ChannelHandlerAdapter(){
                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        System.err.println(cause.getMessage());
                    }

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        ByteBuf buf = ctx.alloc().buffer();
                        buf.writeBytes(SerializationUtils.serialize((Serializable) obj));
                        ctx.writeAndFlush(buf);
                    }

                    /**
                     *
                     * @param ctx   可以操作服务端
                     * @param msg   服务器端给出的响应
                     * @throws Exception
                     */
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        System.out.println("客户端响应:----->>"+(buf.toString(CharsetUtil.UTF_8)));
                    }
                });
            }
        });
        //设置将要连接的服务器的端口号
        ChannelFuture connect = bt.connect(hostAndPost.getHost(), hostAndPost.getPost()).sync();
        connect.channel().closeFuture().sync();
    }

    public void close(){
        //关闭资源
        wi.shutdownGracefully();
    }
}
