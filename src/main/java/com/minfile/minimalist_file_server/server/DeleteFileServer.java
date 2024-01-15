package com.minfile.minimalist_file_server.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class DeleteFileServer extends ServerSocket {

    private DataInputStream dis;

    public DeleteFileServer(int port) throws IOException {
        super(port);
    }

    public void deleteFileServerLoad() throws IOException, InterruptedException {
        while (true) {
            Socket deleteFileServer = this.accept();
            try {
                dis = new DataInputStream(deleteFileServer.getInputStream());
                String filePath = dis.readUTF();

                boolean deleteSuccess = deleteFiles(filePath);
                sendDeleteResponse(deleteFileServer, deleteSuccess);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dis != null) {
                        dis.close();
                    }
                    deleteFileServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendDeleteResponse(Socket socket, boolean success) throws IOException {
        OutputStream outToClient = socket.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToClient);
        out.writeUTF(String.valueOf(success));
        out.flush();
        socket.close();
    }

    public boolean deleteFiles(String pathName) {
        File file = new File(pathName);
        return file.isFile() && file.exists() && file.delete();
    }
}
