package com.baizhi.netty2;

/**
 * @author 皇甫
 */
public class ClientTest {
    public static void main(String[] args) throws InterruptedException {
        NettyClient nettyClient = new NettyClient();

        try {
            nettyClient.clientStart(new HostAndPost("172.16.0.1",9987), new MethodInvokeMeta(
                    "sum",
                    new Class[]{Integer.class, Integer.class},
                    new Object[]{2,3},
                    "com.baizhi.doubledemo.Demo")
            );
        }finally {
            nettyClient.close();
        }
    }
}
