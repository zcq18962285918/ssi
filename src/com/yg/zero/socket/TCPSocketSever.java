package com.yg.zero.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 实现TCP协议的服务器端
 * 便是服务器程序的类: java.net.ServerSocket
 * 构造方法:
 *    ServerSocket(int port) 传递端口号
 *
 * 服务端无法自己创建Socket类,需要获取到客户端的Socket套接字对象
 * (服务端接收到很多客户端链接申请,无法知道是那个客户端的申请,所以需要服务端获取到客户端的Socket套接字对象.来确定那个客户端的申请)
 * 方法:
 *   Socket accept()
 */
public class TCPSocketSever {
    public static void socketSever(int port, String data) throws IOException {
        //创建服务器程序类.
        ServerSocket serverSocket = new ServerSocket(port);
        //使用服务器程序类Socket accept()方法获取客户端套接字Socket类对象
        Socket socket = serverSocket.accept();
        //通过套接字获取到,字节输入流对象
        InputStream inputStream = socket.getInputStream();
        //创建字节数组,存储输入流读取客户端的字节数据
        byte[] bytes = new byte[1024];
        int len = inputStream.read(bytes);
        System.out.println(new String(bytes, 0 ,len));
        //向客户端传递数据
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(data.getBytes());
        //关闭资源
        outputStream.close();
        inputStream.close();
        socket.close();
    }
}
