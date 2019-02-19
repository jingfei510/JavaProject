package com.jingfei.server;

import configs.Configs;
import utils.ScannerNum;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;

public class Server {


    public static void main(String[] args) {
        HashMap<String, Socket> hm = new HashMap<>();
        Properties properties = new Properties();
        HashSet<String> hashSet = new HashSet<>();
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("输入服务器暴露的端口号(8000-65535):");
            int num = ScannerNum.getNum();
            ServerSocket ss = new ServerSocket(num);
            System.out.println("服务端已经开启等待连接...");
            int i = 1;
            while (true) {
                Socket socket = ss.accept();
                System.out.println((i++) + "个已经连接");


                byte[] bytes = new byte[1024];
                int len = socket.getInputStream().read(bytes);
                String string = new String(bytes, 0, len);
                //System.out.println(string);
                if (Configs.REGISTER.toString().equals(string)) {
                    //开启注册线程
                    System.out.println("开启注册线程");
                    new RegisterThread(socket, hm, properties,hashSet).start();
                } else if (Configs.LOGIN.toString().equals(string)) {
                    //开启登陆线程
                    System.out.println("开启登陆线程");
                    new loginThread(socket, hm, properties,hashSet).start();
                }

            }
        }catch (SocketException e){
            System.out.println("有连接强制退出了");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
