package com.jingfei.configs;

import java.io.*;

public class ChatTxt {

    public static void store(String name, String msg) throws IOException {
        File dir = new File(YorN.chatTxtPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File chatFile = new File(YorN.chatTxtPath, name + ".txt");
        FileOutputStream out = new FileOutputStream(chatFile, true);
        out.write(msg.getBytes());
        out.write("\r\n".getBytes());
        out.flush();

    }

    public static String load(String name) throws IOException {
        File dir = new File(YorN.chatTxtPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File chatFile = new File(YorN.chatTxtPath, name + ".txt");
        if (!chatFile.exists()) {
            chatFile.createNewFile();
        }
        FileInputStream in = new FileInputStream(chatFile);
        byte[] bytes = new byte[1024 * 10];
        int len = 0;
        String s = "";
        while ((len = in.read(bytes)) != -1) {
            s = new String(bytes, 0, len);

        }
        return s;
    }
}
