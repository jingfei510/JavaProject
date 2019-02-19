package com.jingfei.utils;

import com.jingfei.configs.YorN;

import java.util.Scanner;

public class ScannerNum {
    public static int NumPattern() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                String yn = sc.next();
                if("y".equalsIgnoreCase(yn)){
                    YorN.yn="y";
                    return 100;
                }else if ("n".equalsIgnoreCase(yn)){
                    YorN.yn="n";
                    return 100;
                }else {
                    int num = Integer.parseInt(yn);
                    if (num >= 0 && num <= 7) {
                        return num;
                    } else {
                        System.out.println("输入超过范围,请重新输入:");
                    }

                }



            } catch (Exception e) {
                sc = new Scanner(System.in);
                System.out.println("输入有误,请重新输入:");
            }

        }
    }

    public static int NumLog() {
        Scanner sc = new Scanner(System.in);
        int num = 0;
        System.out.println("请选择:");
        while (true) {
            try {
                num = sc.nextInt();
                if (num >= 1 && num <= 2) {
                    break;
                } else {
                    System.out.println("输入超过范围,请重新输入:");
                }

            } catch (Exception e) {
                sc = new Scanner(System.in);
                System.out.println("输入有误,请重新输入:");
            }

        }
        return num;
    }
}
