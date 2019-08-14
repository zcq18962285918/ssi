package com.yg.zero.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 此类实现TCP协议的客户端,链接到服务器
 * 和服务器实现数据交换
 * 实现TCP客户端程序类 java.net.Socket
 * <p>
 * 构造方法:
 * Socket(String host,int port) 传递服务器IP和端口号
 * 注意构造器一旦运行就会和服务器进行链接,链接失败,抛出异常
 * <p>
 * 由于是在互联网传输数据使用的字节输出流和字节输入流必须使用Socket类的getxxxxx()方法返回的字节输出流和输入流对象
 * Socket 被称呼为套接字
 * <p>
 * 输出流和输入方法:
 * OutputStream getOutputStream() 返回套接字的输出流
 * 作用:将数据输出,输出到服务器 (服务器运行Socket方法获取到套接字输出流则输出到Socket对象客户端)
 * <p>
 * InputStream getInputStream() 返回套接字输入流
 * 作用:从服务端中读取数据 (服务器运行Socket方法获取到套接字输入流则读取客户端数据)
 */
public class TCPSocket {
    public static void socket(int port) throws IOException {
        //创建Socket类对象,链接服务器
        Socket socket = new Socket("127.0.0.1", port);
        //通过客户端套接字对象Socket方法,获取字节输出流,将数据写入服务器
        OutputStream ops = socket.getOutputStream();
        //字节输出流,传递只能是字节,传递字节数组
        ops.write("客户端".getBytes());
        //读取服务器传递的数据
        InputStream ips = socket.getInputStream();
        byte[] bytes = new byte[1024];
        int len = ips.read(bytes);
        System.out.println(new String(bytes, 0, len));
        //关闭资源
        ips.close();
        ops.close();
        socket.close();
    }
}
