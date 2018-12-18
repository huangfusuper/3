package com.baizhi.doubledemo;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class NioBootsrapServer {

    //该线程池主要负责请求的转发
    private static ExecutorService master= Executors.newFixedThreadPool(150);
    //该线程池主要负责请求的响应
    private static ExecutorService worker= Executors.newFixedThreadPool(150);

    //请求转发队列
    private static final AtomicBoolean NEED_REG_DISPATH= new AtomicBoolean(false);
    //需要注册读队列
    private static final List<ChannelAndAtt> READ_QUEUE= new Vector<ChannelAndAtt>();
    //注册写队列
    private static final List<ChannelAndAtt> WRITE_QUEUE= new Vector<ChannelAndAtt>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc=ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(9987));
        //设置通道非阻塞
        ssc.configureBlocking(false);
        //创建通道选择器
        Selector selector=Selector.open();
        //注册ACCEPT事件类型 转发
        ssc.register(selector,SelectionKey.OP_ACCEPT);

        //迭代遍历事件key
        while(true){
            //返回需要处理的事件个数，如果没有该方法会阻塞，也有可能直接返回0（当程序调用Selector#wakeup）
            // System.out.println("尝试选择待处理的keys...");
            int num = selector.select(1);
            if(num >0){
                //事件处理
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while(keys.hasNext()){
                    SelectionKey key = keys.next();
                    //处理对应的事件key
                    if(key.isAcceptable()){
                        key.cancel();//取消转发注册
                        master.submit(new ProcessDispatcher(key,selector));
                    }else if(key.isReadable()){
                        key.cancel();//取消读注册
                        worker.submit(new ProcessRead(key,selector));
                    }else if(key.isWritable()){
                        key.cancel();//取消写注册
                        worker.submit(new ProcessWrite(key,selector));
                    }
                    //移除key
                    keys.remove();
                }
            }else {
                if(NEED_REG_DISPATH.get()){//需要重新注册ACCEPT
                    System.out.println("重新注册ACCEPT");
                    ssc.register(selector,SelectionKey.OP_ACCEPT);
                    NEED_REG_DISPATH.set(false);
                }
                while(READ_QUEUE.size()>0){
                    ChannelAndAtt channelAndAtt = READ_QUEUE.remove(0);
                    //注册读
                    System.out.println("注册READ");
                    channelAndAtt.getChannel().register(selector,SelectionKey.OP_READ,channelAndAtt.att);
                }
                while(WRITE_QUEUE.size()>0){
                    ChannelAndAtt channelAndAtt = WRITE_QUEUE.remove(0);
                    //注册写
                    System.out.println("注册写");
                    channelAndAtt.getChannel().register(selector,SelectionKey.OP_WRITE,channelAndAtt.att);
                }
            }
        }
    }
    public static class ProcessWrite implements Runnable{
        private SelectionKey key;
        private Selector selector;

        public ProcessWrite(SelectionKey key, Selector selector) {
            this.key = key;
            this.selector = selector;
        }
        @Override
        public void run() {
            try {
                SocketChannel s= (SocketChannel) key.channel();
                ByteArrayInputStream bais = (ByteArrayInputStream)key.attachment();

                byte[] bytes=new byte[1024];
                int n = bais.read(bytes);//最多从bais获取一个缓冲区的数据

                if(n==-1){
                    s.shutdownOutput();//告知写结束
                    s.close(); //关闭通道
                }else{
                    //最多写一个缓冲区的数据
                    s.write(ByteBuffer.wrap(bytes,0,n));
                    //恢复写注册
                    WRITE_QUEUE.add(new ChannelAndAtt(s,bais));
                }
                //打破main线程
                selector.wakeup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static class ProcessRead implements Runnable{
        private SelectionKey key;
        private Selector selector;

        public ProcessRead(SelectionKey key, Selector selector) {
            this.key = key;
            this.selector = selector;
        }
        @Override
        public void run() {
            try {
                //处理读事件
                SocketChannel s= (SocketChannel) key.channel();
                //处理读
                ByteBuffer buffer=ByteBuffer.allocate(1024);
                ByteArrayOutputStream baos= (ByteArrayOutputStream) key.attachment();

                int n=s.read(buffer);
                if(n==-1){
                    //根据请求参数给出响应
                    Object req= SerializationUtils.deserialize(baos.toByteArray());
                    MethodInvokeMeta re =(MethodInvokeMeta) req;
                    /*----------------------------------------------------------------------------------*/
                    /*-------------------------------------开始处反射调用结果---------------------------------------------*/
                    Class<?> obj = Class.forName(re.getTargetClass());
                    Method methodTarget = obj.getMethod(re.getMethod(), re.getParameterType());
                    Object invoke = methodTarget.invoke(obj.newInstance(), re.getArgs());
                    /*----------------------------------------------------------------------------------*/
                    /*----------------------------------------------------------------------------------*/
                    ByteArrayInputStream bais=new ByteArrayInputStream(SerializationUtils.serialize((Serializable) invoke ));
                    //注册写
                    WRITE_QUEUE.add(new ChannelAndAtt(s,bais));
                }else{
                    buffer.flip();
                    baos.write(buffer.array(),0,n);
                    //恢复读注册
                    READ_QUEUE.add(new ChannelAndAtt(s,baos));
                }
                //打断mian线程阻塞
                selector.wakeup();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    public static class ProcessDispatcher implements Runnable{
        private SelectionKey key;
        private Selector selector;

        public ProcessDispatcher(SelectionKey key, Selector selector) {
            this.key = key;
            this.selector = selector;
        }
        @Override
        public void run() {
            try {
                //获取通道
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel s = ssc.accept();
                s.configureBlocking(false);
                //将需要重新注册
                NEED_REG_DISPATH.set(true);
                //注册读
                READ_QUEUE.add(new ChannelAndAtt(s,new ByteArrayOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //打断mian线程阻塞
            selector.wakeup();
        }
    }
    public static class ChannelAndAtt{
        private SelectableChannel channel;
        private Object att;

        public ChannelAndAtt(SelectableChannel channel, Object att) {
            this.channel = channel;
            this.att = att;
        }

        public SelectableChannel getChannel() {
            return channel;
        }

        public void setChannel(SelectableChannel channel) {
            this.channel = channel;
        }

        public Object getAtt() {
            return att;
        }

        public void setAtt(Object att) {
            this.att = att;
        }
    }
}
