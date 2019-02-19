package utils;

import java.util.Scanner;

public class ScannerNum {
    public static int getNum() {
        Scanner sc = new Scanner(System.in);
        int num = 0;
        while (true) {
            try {
                num = sc.nextInt();
                if (num >= 8000 && num <= 65535) {
                    break;
                } else {
                    System.out.println("输入的没有在安全范围内,请重新输入:");
                }

            } catch (Exception e) {
                sc = new Scanner(System.in);
                System.out.println("输入有误,请重新输入:");
            }

        }
        return num;
    }
}
