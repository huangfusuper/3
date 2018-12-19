package com.baizhi.netty2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
/**
 * @author 皇甫
 */
public class NettyServer {
    /*
    * 将原有数据提取出来
    *
    * */
    /**启动服务*/
    private ServerBootstrap sbs = new ServerBootstrap();
    /**创建两个线程池*/
    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup wok = new NioEventLoopGroup();
    private int port;


    public NettyServer(int port) {
        this.port = port;
        //关联两个线程池
        sbs.group(boss, wok);
        //设置服务端的实现类
        sbs.channel(NioServerSocketChannel.class);
    }

    public static void main(String[] args) throws InterruptedException {
        NettyServer nettyServer = new NettyServer(9987);
        try{
            nettyServer.serverStrap();
        }finally {
            nettyServer.close();
        }
    }


    public void serverStrap() throws InterruptedException {

        //设置通讯管道
        sbs.childHandler(new ChannelInitializer<SocketChannel>() {
            /***
             *
             * @param sc     通道类型
             * @throws Exception
             */
            @Override
            protected void initChannel(SocketChannel sc) throws Exception {
                //创建通道
                ChannelPipeline pipeline = sc.pipeline();
                pipeline.addLast(new ChannelHandlerAdapter(){
                    /***
                     *
                     * @param ctx    不知道
                     * @param cause  异常
                     * @throws Exception
                     */
                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        System.err.println(cause.getMessage());
                    }

                    /***
                     *
                     * @param ctx   处理者
                     * @param msg   请求信息
                     * @throws Exception
                     */
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        System.out.println(buf);
                        ChannelFuture channelFuture = ctx.writeAndFlush(msg);
                        channelFuture.addListener(ChannelFutureListener.CLOSE);
                        channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                        channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                    }
                });
            }
        });
        /**设置端口 正常启动*/
        System.out.println("9987监视中。。。。。。。。。。。。。。");
        ChannelFuture cf = sbs.bind(port).sync();
        cf.channel().closeFuture().sync();
    }
    public void close(){
        //关闭资源
        boss.shutdownGracefully();
        wok.shutdownGracefully();
    }
}
