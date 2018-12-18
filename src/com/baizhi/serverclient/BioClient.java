package com.baizhi.serverclient;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class BioClient {
    public static void main(String[] args) throws IOException {
        String msg = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入您的请求！");
        msg = scanner.next();
        test2(msg);
    }
    public static void test2(String msg) throws IOException {
        //创建Socket对象
        Socket s = new Socket();
        s.connect(new InetSocketAddress("172.16.7.76", 9999));
        //发送请求
        OutputStream os = s.getOutputStream();
        PrintWriter pw = new PrisdsntWriter(os);
        pw.println(msg);
        pw.flush();
        //告知服务器结束
        s.shutdownOutput();
        //开始接受响应
        InputStream is = s.getInputStream();
        InputStreamReader ins = new InputStreamReader(is);
        BufferedReader bs = new BufferedReader(ins);
        String line = null;
        StringBuilder str = new StringBuilder();
        while ((line = bs.readLine())!=null){
            str.append(line);
        }
        System.out.println("客户端接受响应:"+str.toString());
        s.close();
    }

}
