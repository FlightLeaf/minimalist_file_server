package com.minfile.minimalist_file_server.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DeleteFileServer extends ServerSocket {

    private DataInputStream dis;

    public DeleteFileServer(int port) throws Exception {
        super(port);
    }

    public void deleteFileServerLoad() throws Exception {
        while (true){
            Socket deleteFileServer = this.accept();
            try {
                dis = new DataInputStream(deleteFileServer.getInputStream());
                String filePath = dis.readUTF();

                System.out.println(filePath);
                // 删除文件
                boolean deleteState = deleteFiles(filePath);
                // 反馈文件是否删除成功
                OutputStream outToClient = deleteFileServer.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToClient);
                out.writeUTF(String.valueOf(deleteState));

                System.out.println("======== 删除成功 [File Name：" + filePath + "] =============");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(dis != null)
                        dis.close();
                    deleteFileServer.close();
                } catch (Exception e) {}
            }
        }

    }

    public boolean deleteFiles(String pathName){
        boolean flag = false;
        // 根据路径创建文件对象
        File file = new File(pathName);
        // 路径是个文件且不为空时删除文件
        if(file.isFile()&&file.exists()){
            flag = file.delete();
        }
        // 删除失败时，返回false
        return flag;
    }
}
