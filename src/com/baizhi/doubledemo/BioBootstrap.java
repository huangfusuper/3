package com.baizhi.doubledemo;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author 皇甫
 */
public class BioBootstrap {
    public static void main(String[] args) throws IOException {
        Object returnValue = send(new MethodInvokeMeta("sum",
                new Class[]{Integer.class, Integer.class},
                new Object[]{2,3}, "com.baizhi.doubledemo.Demo"));
        System.out.println(returnValue.getClass()+" "+returnValue);

    }
    public static Object send(Object cmd) throws IOException {

        SocketChannel sc=SocketChannel.open();
        sc.connect(new InetSocketAddress("127.0.0.1",9987));

        ByteBuffer buffer=ByteBuffer.wrap(SerializationUtils.serialize((Serializable) cmd));
        sc.write(buffer);
        sc.shutdownOutput();

        //读取响应
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        while(true){
            buffer.clear();
            int n=sc.read(buffer);
            if(n==-1) break;
            buffer.flip();
            baos.write(buffer.array(),0,n);
        }
        Object returnValue=SerializationUtils.deserialize(baos.toByteArray());
        //关闭资源
        sc.close();
        return  returnValue;
    }
}