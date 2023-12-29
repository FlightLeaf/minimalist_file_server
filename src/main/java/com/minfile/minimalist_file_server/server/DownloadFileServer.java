package com.minfile.minimalist_file_server.server;

import java.io.*;
import java.net.*;

public class DownloadFileServer extends ServerSocket {

    public DownloadFileServer() throws IOException {
        // Constructor code
    }

    public void downloadFileServerLoad(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();

                InputStream is = socket.getInputStream();
                byte[] bytes = new byte[1024];
                int length = is.read(bytes);
                String fileName = new String(bytes, 0, length);
                System.out.println("请求下载" + fileName);

                File file = new File(fileName);
                if (!file.exists()) {
                    byte[] notFoundBytes = ("未发现" + fileName).getBytes();
                    OutputStream os = socket.getOutputStream();
                    os.write(notFoundBytes);
                    os.flush();
                    break;
                }

                long fileLength = file.length();
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeLong(fileLength);
                dos.flush();

                FileInputStream fis = new FileInputStream(file);
                OutputStream os = socket.getOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
                fis.close();
                os.close();
                dos.close();
                is.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
