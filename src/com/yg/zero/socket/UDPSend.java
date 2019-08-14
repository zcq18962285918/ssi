package com.yg.zero.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 实现UDP协议的发送端
 *  UDP发送端和接收端必须有DatagramPacket类对象和DatagramSocket类对象
 *  发送端使用DatagramPacket类封装数据包使用DatagramSocket类发送数据包
 *  接收端需要DatagramSocket类接收数据包使用DatagramPacket类拆封数据包
 *
 *      实现封装数据类 java.net.DatagramPacket  将数据封装为数据包
 *      实现数据包传输类 java.net.DatagramSocket 将数据包发出去
 *
 * 实现步骤:
 *  1.创建DatagramPacket 对象封装数据,接收的地址和端口
 *  2.创建DatagramSocket 对象
 *  3.调用DatagramSocket 类send(DatagramPacket dp)方法传递数据包,发送数据包
 *  4.关闭资源close();
 *
 *  DatagramPacket构造方法:
 *     DatagramPacket(byte[] buf,int length,InetAddress address,int port)
 *     buf: 字节数组
 *     length: 需要传递的数组元素个数
 *     address: 需要发送的主机地址
 *     port: 端口号
 *
 *  DatagramSocket构造方法:
 *     DatagramSocket()
 *     方法:send(DatagramPacket dp)
 */
public class UDPSend {
    public static void send() throws IOException {
        //创建数据包对象，封装要发送的数据，接收端ip，端口
        byte[] bytes = "UDP发送".getBytes();
        InetAddress address = InetAddress.getByName("127.0.0.1");
        //封装发送数据包
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, address, 8888);
        //创建发送数据包
        DatagramSocket datagramSocket = new DatagramSocket();
        //调用发送数据包方法传递数据对象
        datagramSocket.send(datagramPacket);
        datagramSocket.close();
    }
}
