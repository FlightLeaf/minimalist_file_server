package com.minfile.minimalist_file_server.server;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class DownloadFileServer {

    public void downloadFileServerLoad(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                     InputStream is = socket.getInputStream();
                     DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int length = is.read(buffer.array());
                    String fileName = new String(buffer.array(), 0, length);
                    System.out.println("请求下载" + fileName);

                    File file = new File(fileName);
                    if (!file.exists()) {
                        String notFoundMessage = "未发现" + fileName;
                        dos.writeUTF(notFoundMessage);
                        break;
                    }

                    long fileLength = file.length();
                    dos.writeLong(fileLength);
                    dos.flush();

                    try (FileInputStream fis = new FileInputStream(file)) {
                        while (fis.available() > 0) {
                            int bytesRead = fis.read(buffer.array());dos.write(buffer.array(), 0, bytesRead);
                            dos.write(buffer.array(), 0, bytesRead);
                        }
                    }
                } catch (FileNotFoundException e) {
                    // Log the exception
                    System.err.println("文件下载过程中发生错误：" + e.getMessage());
                } catch (IOException e) {
                    // Log the exception
                    System.err.println("与客户端通信期间发生错误：" + e.getMessage());
                }
            }
        } catch (IOException e) {
            // Log the exception
            System.err.println("启动服务器时发生错误： " + e.getMessage());
        }
    }
}
