package com.jingfei.client;

import com.jingfei.configs.ChatTxt;
import com.jingfei.configs.Configs;
import com.jingfei.utils.InputAndOutputUtil;
import com.jingfei.utils.ScannerNum;
import com.jingfei.utils.Time;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;


public class Client {

    private static OutputStream out;
    private static InputStream in;
    private static Scanner sc;
    private static boolean flagPattern;

    public static void main(String[] args) {
        try {
            sc = new Scanner(System.in);
            System.out.println("请输入服务器IP和端口 格式 IP#端口");
            String ipPort = sc.next();
            String[] split = ipPort.split("#");
            String ip = split[0];
            int port = Integer.parseInt(split[1]);//192.168.6.220#9998
            Socket sk = new Socket(ip, port);
            in = sk.getInputStream();
            out = sk.getOutputStream();
            sc = new Scanner(System.in);
            System.out.println("请选择 1.注册 2.登陆");
            int i = ScannerNum.NumLog();
            switch (i) {
                case 1:
                    System.out.println("进入注册");
                    out.write(Configs.REGISTER.toString().getBytes());
                    userRegister();
                    break;
                case 2:
                    System.out.println("进入登陆");
                    out.write(Configs.LOGIN.toString().getBytes());
                    userLogin();
                    break;
            }

            //System.out.println("开启了客户端读取子线程");
            ClientThread clientThread = new ClientThread(sk);
            clientThread.start();
            boolean flag = true;
            flagPattern = true;
            while (flag) {
                if (flagPattern) {
                    System.out.println("请选择模式:1.私聊模式\t2.公聊模式\t3.获取在线列表\t4.切换状态(隐身/在线)\t5.发文件\t6.查看聊天记录\t7.退出\t");
                    flagPattern = false;
                } else {
                    System.out.println("请选择:");
                }

                int num = ScannerNum.NumPattern();
                switch (num) {
                    case 1:
                        System.out.println("你已进入私聊模式,请输入要私聊的用户名 (按-q退出)");
                        getOnlineList();//获取要私聊的在线列表
                        privateTalk();
                        break;
                    case 2:
                        publicTalk();
                        break;
                    case 3:
                        getOnlineList();
                        break;
                    case 4:
                        translation();
                        break;
                    case 5:
                        sendFile();
                        break;
                    case 6:
                        lookTxt();
                        break;
                    case 7:
                        userExit();
                        flag = false;
                        break;
                    case 100:
                        break;
                }
            }
            clientThread.stop();
            sk.close();
        } catch (SocketException e) {
            //客户端下线后，会抛出一个SocketException异常,但是会在控制台打印异常信息，
            //所以我们捕获一下，做空处理
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("输入格式不正确");
        }
    }

    private static void translation() throws IOException{
        String msg = "null" + "#" + "null" + "#" + Configs.TRANSLATION;
        System.out.println("切换状态成功!");
        out.write(msg.getBytes());
    }

    private static void lookTxt() throws IOException {
        System.out.println("输入y看私聊记录,输入n看公聊记录 (按-q退出)");
        String way = sc.next();
        if (way.equalsIgnoreCase("-q")) {
            return;
        }
        if("y".equalsIgnoreCase(way)){
            System.out.println("请输入要查看聊天记录的用户名 (按-q退出)");
            String reciver = sc.next();
            if (reciver.equalsIgnoreCase("-q")) {
                return;
            }

            String msg = reciver + "#" + "null" + "#" + Configs.LOOKPRIVATETXT;
            out.write(msg.getBytes());
        }else if ("n".equalsIgnoreCase(way)){
            String msg = "null" + "#" + "null" + "#" + Configs.LOOKPUBLICTXT;
            out.write(msg.getBytes());
        }else {
            System.out.println("输入错误!");
        }



    }

    private static void sendFile() throws IOException {
        System.out.println("输入目标用户");
        String reciver = sc.next();
        System.out.println("输入文件路径");
        String root = sc.next();
        File file = new File(root);
        if (file.exists()) {
            String fzMsg = reciver + "#" + (file.getName() + "==" + file.length()) + "#" + Configs.SENDFILE;
            byte[] msgBytes = fzMsg.getBytes();
            byte[] emptyBytes = new byte[1024 * 10 - msgBytes.length];
            byte[] fileBytes = InputAndOutputUtil.readFile(root);
            //用内存操作流对字节先缓冲起来,再一起发送
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(msgBytes);
            baos.write(emptyBytes);
            baos.write(fileBytes);
            byte[] allMsg = baos.toByteArray();
            out.write(allMsg);
            System.out.println("文件已发送");
        } else {
            System.out.println("文件不存在");
        }


    }


    private static void userLogin() throws IOException {
        while (true) {
            //登陆
            System.out.println("请输入用户名登陆:");
            String userName = sc.next();
            System.out.println("请输入登陆密码:");
            String pwd = sc.next();

            //将用户名和密码提交服务器,验证
            String userNamePwd = userName + "===" + pwd;
            out.write(userNamePwd.getBytes());
            byte[] bytes = new byte[1024];
            int len = in.read(bytes);
            String string = new String(bytes, 0, len);
            if ("yes".equals(string)) {
                System.out.println("登陆成功");
                pwd = "null" + "#" + "null" + "#" + Configs.ONLINE;
                out.write(pwd.getBytes());
                break;
            } else if ("no".equals(string)) {
                System.out.println("密码或用户名错误");
            }

        }

    }

    private static void userRegister() throws IOException {
        while (true) {
            //注册
            System.out.println("请输入用户名注册:");
            String userName = sc.next();
            System.out.println("请输入注册密码: ");
            String pwd = sc.next();

            //将用户名和密码提交服务器,保存
            String userNamePwd = userName + "===" + pwd;
            out.write(userNamePwd.getBytes());
            byte[] bytes = new byte[1024];
            int len = in.read(bytes);
            String string = new String(bytes, 0, len);
            System.out.println("正在读服务器回应...");
            if ("yes".equals(string)) {
                System.out.println("注册成功");
                pwd = "null" + "#" + "null" + "#" + Configs.ONLINE;
                out.write(pwd.getBytes());
                break;
            } else if ("no".equals(string)) {
                System.out.println("用户名被占用,请换一个注册");
            }

        }
    }

    private static void userExit() throws IOException {
        String msg = "null" + "#" + "null" + "#" + Configs.EXIT;
        out.write(msg.getBytes());
    }

    private static void getOnlineList() throws IOException {
        String msg = "null" + "#" + "null" + "#" + Configs.ONLINELIST;//接收者#消息#消息类型
        out.write(msg.getBytes());
    }

    private static void publicTalk() throws IOException {
        while (true) {
            System.out.println("你已进入公聊模式,格式为 消息 按-q退出.");
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入消息:");
            String msg = sc.next();
            if ("-q".equals(msg)) {
                flagPattern = true;
                break;
            }
            String chattxt=Time.getTime()+ ": 我给大家发信息: " + msg;
            ChatTxt.store("publicTxt",chattxt);
            msg = "null" + "#" + msg + "#" + Configs.PUBLIC;//接收者#消息#消息类型
            out.write(msg.getBytes());

        }

    }

    private static void privateTalk() throws IOException {
        String user = sc.next();
        if ("-q".equals(user)) {
            flagPattern = true;
            return;
        }
        while (true) {
            System.out.println("输入内容:");
            String msg = sc.next();
            if ("-q".equals(msg)) {
                flagPattern = true;
                break;
            }

            String chattxt = Time.getTime() + ": 我发消息: " + msg;
            ChatTxt.store(user, chattxt);//我要给user发消息,所以是以接收者命名的txt
            msg = user + "#" + msg + "#" + Configs.PRIVATE;//接收者#消息#消息类型
            out.write(msg.getBytes());


        }
    }
}
