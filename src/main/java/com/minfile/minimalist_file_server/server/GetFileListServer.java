package com.minfile.minimalist_file_server.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GetFileListServer {
    public void start(int port, String downloadPathStr) throws IOException {
        // 创建对象
        ServerSocket ss = new ServerSocket(port);
        while (true) {
            // 接收连接
            Socket socket = ss.accept();

            // 获取文件列表
            File[] files = getFileList(downloadPathStr);

            // 将文件列表转换为 JSON 字符串
            String fileListString = convertFileListToJson(files);

            // 发送文件列表给客户端
            sendFileListToClient(socket, fileListString);

            socket.close();
        }
    }

    private static File[] getFileList(String downloadPathStr) {
        File file = new File(downloadPathStr);  // 获取其file对象
        return file.listFiles(File::isFile);  // 返回文件数组
    }

    private static String convertFileListToJson(File[] files) {
        JSONArray jsonArray = new JSONArray();
        for (File file : files) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", file.getName());
            jsonObject.put("path", file.getAbsolutePath());
            jsonObject.put("size", file.length());
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    private static void sendFileListToClient(Socket socket, String fileListString) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bw.write(fileListString);
        bw.newLine();
        bw.flush();
        bw.close();
    }
}
