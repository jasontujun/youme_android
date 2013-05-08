package com.soulware.youme.mgr;

import java.io.*;

/**
 * Created by jasontujun.
 * Date: 13-2-27
 * Time: 下午7:49
 */
public class FileMgr {

    /**
     * 复制文件
     * @param oldFile
     * @param newFile
     */
    public static boolean copyFile(File oldFile, File newFile) {
        try {
            int bytesum = 0;
            int byteread = 0;
            if (oldFile.exists()) { // 文件存在时
//				 if(!newFile.exists()){
//					 newFile.createNewFile();
//				 }
                InputStream is = new FileInputStream(oldFile); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newFile);
                byte[] buffer = new byte[1444];
                while ((byteread = is.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                is.close();
                return true;
            }
        }catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 直接将.wav音频文件转换为byte[]
     * @param soundFile
     * @return
     * @throws java.io.IOException
     */
    public static byte[] file2byte(File soundFile) throws IOException {
        if(soundFile == null) {
            return null;
        }

        InputStream is = new FileInputStream(soundFile);

        // 判断文件大小
        long length = soundFile.length();
        if (length > Integer.MAX_VALUE) {// 文件太大，无法读取
            throw new IOException("File is to large "+soundFile.getName());
        }
        System.out.println("Read sound file size:"+length);

        // 创建一个数据来保存文件数据
        byte[] bytes = new byte[(int)length];

        // 读取数据到byte数组中
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+soundFile.getName());
        }
        is.close();

        return bytes;
    }

    public static boolean byte2file(byte[] bytes, File file) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
