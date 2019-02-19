package com.jingfei.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

public class loginThread extends Thread {
    Socket socket;
    HashMap<String, Socket> hm;
    Properties properties;
    private String userName;
    HashSet<String> hashSet;

    public loginThread(Socket socket, HashMap<String, Socket> hm, Properties properties, HashSet<String> hashSet) {
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


            while (true) {
                byte[] bytes = new byte[1024];
                int len = in.read(bytes);
                String userNamePwd = new String(bytes, 0, len);
                String[] userNamePwdSpilt = userNamePwd.split("===");
                this.userName = userNamePwdSpilt[0];
                String pwd = userNamePwdSpilt[1];

                File file = new File("userNamePwd.pro");
                if (!file.exists()) {
                    file.createNewFile();
                }
                properties.load(new FileReader(file));
                //得到此用户名的密码
                String s = properties.getProperty(userName, "null");
                if (pwd.equals(s)) {
                    out.write("yes".getBytes());
                    hm.put(userName, socket);
                    hashSet.add(userName);
                    break;
                } else {
                    out.write("no".getBytes());
                }
            }
            //登陆成功开启功能子线程
            new ServerThread(socket, hm, userName, hashSet).start();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
