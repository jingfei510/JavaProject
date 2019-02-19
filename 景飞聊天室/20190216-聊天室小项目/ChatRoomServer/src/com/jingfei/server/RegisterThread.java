package com.jingfei.server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

public class RegisterThread extends Thread {
    Socket socket;
    HashMap<String, Socket> hm;
    private String userName;
    Properties properties;
    HashSet<String> hashSet;
    public RegisterThread(Socket socket, HashMap<String, Socket> hm, Properties properties, HashSet<String> hashSet) {
        this.socket = socket;
        this.hm = hm;
        this.properties = properties;
        this.hashSet =hashSet;
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            HashMap<String, String> userNameAndPwd = new HashMap<>();

            while (true) {
                byte[] bytes = new byte[1024];
                int len = in.read(bytes);
                String userNamePwd = new String(bytes, 0, len);
                String[] userNamePwdSpilt = userNamePwd.split("===");
                this.userName = userNamePwdSpilt[0];
                String pwd = userNamePwdSpilt[1];
                File file = new File("userNamePwd.pro");//关联配置文件
                if (!file.exists()) {
                    file.createNewFile();
                }
                properties.load(new FileReader(file));//读取配置文件
                String property = properties.getProperty(userName, "null");//匹配此名称的用户是否已经存在

                //注册用户,若配置文件中没有此用户名,则注册
                if (property.equals("null")) {
                    hm.put(userName, socket);
                    hashSet.add(userName);
                    properties.setProperty(userName, pwd);
                    properties.store(new FileOutputStream("userNamePwd.pro"), "用户名与密码");
                    out.write("yes".getBytes());
                    break;
                } else {
                    out.write("no".getBytes());

                }
            }
            //注册成功开启功能子线程
            new ServerThread(socket, hm, userName, hashSet).start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
