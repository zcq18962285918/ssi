package com.yg.zero.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 实现UDP接收端
 *      实现拆封数据包 java.net.DatagramPacket 调用方法对数据包拆包
 *      实现接收数据包 java.net.DatagramSocket 接收数据包端口调用方法封装数据包到DatagramPacket类对象
 *
 * 实现步骤:
 *   1.创建DatagramSocket对象,绑定端口号  必须要和发送端(发送数据包到的端口号)一致
 *   2.创建字节数组,接收发来的数据
 *   3.创建DatagramPacket类对象
 *   4.调用DatagramSocket对象方法  receive(DatagramPacket dp) 接收数据,将数据封装到数据包对象中
 *   5.拆包
 *      发送端ip地址
 *        数据包对象DatagramPacket类方法getAddress()返回封装ip地址对象
 *      接收到的字节个数
 *        数据包对象DatagramPacket类方法getLength() 返回数据包中的字节数组长度
 *      发送方的端口
 *        数据包对象DatagramPacket类方法getport() 返回发送或接收端的端口号
 *   6.关闭资源
 */
public class UDPReceive {
    public static void receive() throws IOException {
        //在构造方法中指定端口号
        DatagramSocket datagramSocket = new DatagramSocket(8888);
        //由于是接收,所以只指定数组长度 一般指定数组长度为64kb
        byte[] bytes = new byte[1024];
        //第一个参数传递字节数组用于接收拆封数据包后的数据 第二个参数传递需要接收的数组长度
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
        //调用接收端方法接收数据,传递封装数据包对象
        datagramSocket.receive(datagramPacket);

        InetAddress address = datagramPacket.getAddress();
        System.out.println(address.getHostName() + "---" +address.getHostAddress());

        int len = datagramPacket.getLength();
        System.out.println(len);

        int port = datagramPacket.getPort();
        System.out.println(port);

        System.out.println(new String(bytes, 0, len));
        datagramSocket.close();
    }
}
