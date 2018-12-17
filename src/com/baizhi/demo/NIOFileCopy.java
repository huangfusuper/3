package com.baizhi.demo;

import jdk.nashorn.internal.objects.annotations.Where;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileCopy {
    public static void main(String[] args) throws IOException {
        //创建读通道 将磁盘文件写入缓存区
        FileInputStream fis = new FileInputStream(new File("C:/Users/皇甫/Desktop/大数据课后笔记整理.md"));
        FileChannel channel = fis.getChannel();
        //创建写通道 将缓存区文件写入磁盘
        FileOutputStream fos = new FileOutputStream(new File("C:/Users/皇甫/Desktop/1.md"));
        FileChannel channelOut = fos.getChannel();
        //定义一个缓存区
        ByteBuffer wrap = ByteBuffer.wrap(new byte[1024]);
        //开始进行读写操作
        while (true){
            //在写入缓存区时需要将缓存区内容清空
            wrap.clear();
            //读进缓存区
            int n = channel.read(wrap);
            if(n==-1) break;
            wrap.flip();
            channelOut.write(wrap);
        }
        channel.close();
        channelOut.close();
    }
}
