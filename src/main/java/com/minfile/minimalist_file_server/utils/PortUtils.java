package com.minfile.minimalist_file_server.utils;
import java.io.IOException;
import java.net.ServerSocket;

public class PortUtils {
    /**
     * 检查指定端口是否被占用
     *
     * @param port 端口号
     * @return true 表示端口已经被占用，false 表示端口可用
     */
    public static boolean isPortInUse(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            return false;
        } catch (IOException e) {
            releasePort(port);
            return true;
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private static void releasePort(int port) {
        String command = "cmd /c netstat -ano | findstr :" + port;
        String result = executeCommand(command);
        if (result != null && !result.isEmpty()) {
            String[] split = result.split("\\s+");
            String pid = split[split.length - 1];
            command = "cmd /c taskkill /F /PID " + pid;
            executeCommand(command);
        }
    }

    private static String executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[4096];
            int len;
            while ((len = process.getInputStream().read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len));
            }
            while ((len = process.getErrorStream().read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len));
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return sb.toString().trim();
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }
}

