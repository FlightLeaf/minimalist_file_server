package com.minfile.minimalist_file_server.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.RoundingMode;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;

public class UploadFileServer extends ServerSocket {

    private static DecimalFormat df = null;

    private DataInputStream dis;

    private FileOutputStream fos;

    String downloadPathStr;

    static {
        // 设置数字格式，保留一位有效小数
        df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    }

    public UploadFileServer(int port, String downloadPathStr) throws Exception {
        super(port);
        this.downloadPathStr = downloadPathStr;
    }

    public void load() throws Exception {
        while (true) {
            Socket socket = this.accept();
            try {
                dis = new DataInputStream(socket.getInputStream());

                // 文件名和长度
                String fileName = dis.readUTF();
                long fileLength = dis.readLong();
                //服务器储存的地址
                File directory = new File("C:\\Users\\Administrator\\Desktop\\target");
                if(!directory.exists()) {
                    directory.mkdir();
                }
                File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
                fos = new FileOutputStream(file);

                // 开始接收文件
                byte[] bytes = new byte[1024];
                int length = 0;
                while((length = dis.read(bytes, 0, bytes.length)) != -1) {
                    fos.write(bytes, 0, length);
                    fos.flush();
                }

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                if(file.exists()){
                    dos.writeUTF("success");
                    System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength) + "] ========");
                }else {
                    dos.writeUTF("fail");
                    System.out.println("======== 文件接收失败 ========");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(fos != null)
                        fos.close();
                    if(dis != null)
                        dis.close();
                    socket.close();
                } catch (Exception e) {}
            }
        }
    }

    private String getFormatFileSize(long length) {
        double size = ((double) length) / (1 << 30);
        if(size >= 1) {
            return df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if(size >= 1) {
            return df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if(size >= 1) {
            return df.format(size) + "KB";
        }
        return length + "B";
    }
}