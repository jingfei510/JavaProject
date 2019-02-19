package com.jingfei.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class InputAndOutputUtil {
	public static byte[] readFile(String path){
		File file = new File(path);
		//�������������ȡ������ �൱��ˮ��
		byte datas[] = null;
		if(!file.exists()){
			datas = null;
		}else{
			try {
				//�ֽ���������� �������ڴ���д�ֽ�����  ��������ƴ���ֽ����� 
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				//�����ļ�������
				FileInputStream fis = new FileInputStream(file);
				//��������ÿ�ζ������� �൱ˮư(ÿ�ζ�1024�ֽ� ���ǲ�һ��ÿ���ܶ���ô��  ʵ�ʶ�ȡ�ĳ�����len����)
				byte data[] = new byte[1024*1024];
				//��������ÿ�ζ�ȡ���ֽڴ�С
				int len = 0;
				//���ϵĶ�ȡ ֱ�����ݶ���
				while((len = fis.read(data))>0){
					//��ÿ�ζ�������� ������ֽ����������ڴ���
					baos.write(data, 0, len);
				}
				//���ֽ��������е�����תΪ�ֽ�����
				datas = baos.toByteArray();
				baos.flush();
				baos.close();
				//�ر���
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return datas;
	}
	
	public static boolean writeFile(File file,byte datas[]){
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(datas);
			//�㵹�ر�
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
