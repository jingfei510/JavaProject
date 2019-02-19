package com.jingfei.client;

import com.jingfei.configs.ChatTxt;
import com.jingfei.configs.Configs;
import com.jingfei.configs.YorN;
import com.jingfei.utils.InputAndOutputUtil;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientThread extends Thread {
    Socket sk;

    public ClientThread(Socket sk) {
        this.sk = sk;

    }

    @Override
    public void run() {

        try {
            InputStream in = sk.getInputStream();
            OutputStream out = sk.getOutputStream();
            while (true) {
                //收消息
                byte[] bytes = new byte[1024 * 10];
                int len = in.read(bytes);
                String string = new String(bytes, 0, len).trim();
                //System.out.println("客户端收到:" + string);
                String[] msgs = string.split("#");
                String sender = msgs[0];
                String msg = msgs[1];
                String msgFlag = msgs[2];
                String time = msgs[3];

                if (msgFlag.equals(Configs.PRIVATE.toString())) {
                    //收到的为   发信者#信息#信息标记#时间
                    String s = time + " " + sender + " 私聊给你发来信息: " + msg;
                    System.out.println(s);
                    String chattxt=time+": "+sender+"给你发来消息: "+msg;
                    ChatTxt.store(sender,chattxt);//收到sender的消息,所以存为发给我消息的人为名的txt
                } else if (msgFlag.equals(Configs.PUBLIC.toString())) {
                    System.out.println(time + " " + sender + " 公聊给大家发来信息: " + msg);
                    String chattxt=time+": "+sender+"给大家发来消息: "+msg;
                    ChatTxt.store("publicTxt",chattxt);
                } else if (msgFlag.equals(Configs.ONLINE.toString())) {
                    System.out.println(time + " " + sender + "上线了,快去和他聊天吧!");
                } else if (msgFlag.equals(Configs.ONLINELIST.toString())) {
                    System.out.println(time + " 在线列表为:");
                    System.out.println(msg);
                } else if (msgFlag.equals(Configs.SENDFILE.toString())) {
                    String[] split = msg.split("==");
                    String fileName = split[0];

                    long fileLength = Long.parseLong(split[1]);
                    System.out.println(time + " "+sender+"给你发来一个文件,文件名为: " + fileName + "大小为: " + fileLength / 1024.0 + "kb");
                    System.out.println("你是否接收此文件 y/n ?(确保自己退出到选择模式的界面,在选择是否接收)");
                    while (true) {
                        //循环读取标记,直到主线程输入,读取到标记为止
                        if ("y".equalsIgnoreCase(YorN.yn)) {
                            break;
                        } else if ("n".equalsIgnoreCase(YorN.yn)) {
                            break;
                        }
                    }
                    if (YorN.yn.equalsIgnoreCase("y")) {
                        YorN.yn="null";//将标记改回默认值;
                        System.out.println("文件默认保存到D盘MyChatRoomDocs目录下)");
                        File dir = new File(YorN.filePath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        long length = 0;
                        byte[] bytes1 = new byte[1024];
                        while (true) {
                            int len1 = in.read(bytes1);
                            baos.write(bytes1, 0, len1);
                            length += len1;
                            if (length == fileLength) {
                                break;
                            }
                        }
                        byte[] allBytes = baos.toByteArray();
                        //文件保存的路径
                        File file = new File(YorN.filePath, fileName);
                        boolean b = InputAndOutputUtil.writeFile(file, allBytes);
                        if (b) {
                            System.out.println("文件成功保存在" + YorN.filePath+"文件夹中");
                        } else {
                            System.out.println("文件保存失败");
                        }

                    } else if (YorN.yn.equalsIgnoreCase("n")) {
                        YorN.yn="null";//将标记改回默认值;
                        //in.reset();无法重置socket流,所以只能先接收,而不写入到文件中,达到目的.
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        long length = 0;
                        byte[] bytes1 = new byte[1024];
                        while (true) {
                            int len1 = in.read(bytes1);
                            baos.write(bytes1, 0, len1);
                            length += len1;
                            if (length == fileLength) {
                                break;
                            }
                        }

                        System.out.println("你拒收了此文件");
                    } else if (YorN.yn.equalsIgnoreCase("null")) {
                        System.out.println("没有收到文件null");
                    }
                }else if (msgFlag.equals(Configs.TRANSLATION.toString())) {
                    System.out.println(sender+msg);
                } else if (msgFlag.equals(Configs.LOOKPRIVATETXT.toString())) {
                    String chatStr = ChatTxt.load(sender);
                    System.out.println(chatStr);
                }else if (msgFlag.equals(Configs.LOOKPUBLICTXT.toString())) {
                    String chatStr = ChatTxt.load("publicTxt");
                    System.out.println(chatStr);
                }else if (msgFlag.equals(Configs.EXIT.toString())) {
                    System.out.println(sender+msg);
                }

            }
        }catch (SocketException e){
            System.out.println("服务器已经关闭,可能断电了!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}