package com.jingfei.server;

import configs.Configs;
import utils.Time;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ServerThread extends Thread {
    Socket socket;
    HashMap<String, Socket> hm;
    String userName;
    HashSet<String> hashSet;
    boolean isHidden=true;//默认代表上线

    public ServerThread(Socket socket, HashMap<String, Socket> hm, String username, HashSet<String> hashSet) {
        this.socket = socket;
        this.hm = hm;
        this.userName = username;
        this.hashSet =hashSet;
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            while (true) {
                //接收客户端消息
                byte[] bytes = new byte[1024 * 10];//要求一次读10kb,大于10kb的部分可能为发送的文件
                int len = in.read(bytes);
                String string = new String(bytes, 0, len).trim();
                //System.out.println("服务端收到: " + string);
                String[] spiltString = string.split("#");
                String revicer = spiltString[0];
                String msg = spiltString[1];
                String msgFlag = spiltString[2];
                //System.out.println(msgFlag);

                if (msgFlag.equals(Configs.PRIVATE.toString())) {
                    //发信者#信息#信息标记#时间
                    String zfMsg = userName + "#" + msg + "#" + msgFlag + "#" + Time.getTime();
                    hm.get(revicer).getOutputStream().write(zfMsg.getBytes());
                } else if (msgFlag.equals(Configs.PUBLIC.toString())) {
                    Set<String> keys = hm.keySet();
                    for (String key : keys) {
                        if (userName.equals(key)) {
                            continue;
                        }
                        String zfMsg = userName + "#" + msg + "#" + msgFlag + "#" + Time.getTime();
                        hm.get(key).getOutputStream().write(zfMsg.getBytes());
                    }
                } else if (msgFlag.equals(Configs.ONLINE.toString())) {
                    Set<String> keys = hm.keySet();
                    for (String key : keys) {
                        if (userName.equals(key)) {
                            continue;//给自己不用发
                        }
                        String zfMsg = userName + "#" + "上线了" + "#" + msgFlag + "#" + Time.getTime();
                        hm.get(key).getOutputStream().write(zfMsg.getBytes());

                    }
                } else if (msgFlag.equals(Configs.ONLINELIST.toString())) {
                    StringBuffer sb = new StringBuffer();
                    int i = 1;
                    sb.append("null#");
                    for (String key : hashSet) {
                        if (userName.equals(key)) continue;
                        sb.append(key).append(",");
                    }
                    sb.deleteCharAt(sb.lastIndexOf(",")).append("#").append(msgFlag).append("#").append(Time.getTime());
                    hm.get(userName).getOutputStream().write(sb.toString().getBytes());
                } else if (msgFlag.equals(Configs.SENDFILE.toString())) {
                    //发信者#信息#信息标记#时间
                    String[] split = msg.split("==");
                    String fileName = split[0];
                    long fileLength = Long.parseLong(split[1]);

                    String zfMsg = userName + "#" + msg + "#" + msgFlag + "#" + Time.getTime();
                    byte[] msgBytes = zfMsg.getBytes();
                    byte[] emptyBytes = new byte[1024 * 10 - msgBytes.length];
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
                    byte[] fileBytes = baos.toByteArray();
                    baos.reset();
                    baos.write(msgBytes);
                    baos.write(emptyBytes);
                    baos.write(fileBytes);
                    byte[] allBytes = baos.toByteArray();
                    hm.get(revicer).getOutputStream().write(allBytes);

                } else if (msgFlag.equals(Configs.LOOKPRIVATETXT.toString())) {
                        String zfMsg = revicer + "#" + "这是你和"+revicer+"的聊天记录" + "#" + msgFlag + "#" + Time.getTime();//将要查询的聊天记录的用户,传回去.
                        hm.get(userName).getOutputStream().write(zfMsg.getBytes());

                }else if (msgFlag.equals(Configs.LOOKPUBLICTXT.toString())) {
                    String zfMsg = "publicTxt" + "#" + "这是你和大家的聊天记录" + "#" + msgFlag + "#" + Time.getTime();//将要查询的聊天记录的用户,传回去.
                    hm.get(userName).getOutputStream().write(zfMsg.getBytes());

                }else if (msgFlag.equals(Configs.TRANSLATION.toString())) {
                    if(isHidden){
                        //变成隐身
                        hashSet.remove(userName);
                        //改变开关
                        //isHidden=false;

                    }else{
                        //变成上线
                        hashSet.add(userName);
                        Set<String> keySet = hm.keySet();
                        //发个再次上线消息
                        for (String key : keySet) {
                            if (userName.equals(key)) {
                                continue;
                            }
                            String zfMsg = userName + "#" + "又上线了" + "#" + msgFlag + "#" + Time.getTime();
                            hm.get(key).getOutputStream().write(zfMsg.getBytes());
                        }
                        //发个上线消息
                        //改变开关
                        //isHidden=true;
                    }
                    //变换开关
                    isHidden=!isHidden;

                }else if (msgFlag.equals(Configs.EXIT.toString())) {
                    Set<String> keys = hm.keySet();
                    for (String key : keys) {
                        if (userName.equals(key)) continue;
                        String zfMsg = userName + "#" + "下线了" + "#" + msgFlag + "#" + Time.getTime();
                        //System.out.println(zfMsg);
                        hm.get(key).getOutputStream().write(zfMsg.getBytes());

                    }
                    hm.get(userName).close();
                    hm.remove(userName);
                    hashSet.remove(userName);
                    break;
                }

            }
        }catch (SocketException e) {
            System.out.println(userName+"强制从服务器断开连接");
        }catch (IOException e) {
            e.printStackTrace();
        }catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(userName+"输入的消息格式错误,服务器断开了与他的连接");
        }
    }
}