package com.minfile.minimalist_file_server.controller;

import com.minfile.minimalist_file_server.server.DeleteFileServer;
import com.minfile.minimalist_file_server.server.DownloadFileServer;
import com.minfile.minimalist_file_server.server.GetFileListServer;
import com.minfile.minimalist_file_server.server.UploadFileServer;
import com.minfile.minimalist_file_server.utils.PortUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private TextField deleteFilePath;

    @FXML
    private TextField downloadFilePort;

    @FXML
    private TextField filePath;

    @FXML
    private TextField getFileListPort;

    @FXML
    private TextField uploadFilePort;

    @FXML
    private Button selectPathBtn;

    @FXML
    private Button startBtn;

    String downloadPathStr = "";

    @FXML
    void selectPath(ActionEvent event) {
        Stage stage = new Stage();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择文件夹");
        File selectedDir = directoryChooser.showDialog(stage);
        if (selectedDir != null) {
            filePath.setText(selectedDir.getAbsolutePath());
            filePath.setEditable(false);
            downloadPathStr = selectedDir.getAbsolutePath();
            System.out.println(downloadPathStr);
        }
    }

    int btnCount = 0;

    @FXML
    void startServer(ActionEvent event) {
        btnCount++;

        if(downloadPathStr.equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("路径为空");
            alert.setHeaderText(null);
            alert.setContentText("请先选择下载路径");
            alert.showAndWait();
        }else{
            boolean deleteFilePortFlag = PortUtils.isPortInUse(Integer.parseInt(deleteFilePath.getText()));
            boolean downloadFilePortFlag = PortUtils.isPortInUse(Integer.parseInt(downloadFilePort.getText()));
            boolean getFileListPortFlag = PortUtils.isPortInUse(Integer.parseInt(getFileListPort.getText()));
            boolean uploadFilePortFlag = PortUtils.isPortInUse(Integer.parseInt(uploadFilePort.getText()));
            System.out.println(deleteFilePortFlag+" "+downloadFilePortFlag+" "+getFileListPortFlag+" "+uploadFilePortFlag);
            try {
                // 创建文件传输服务器线程
                Thread fileTransferServerThread = new Thread(() -> {
                    try {
                        System.out.println("文件传输服务器启动成功！");
                        int port = Integer.parseInt(uploadFilePort.getText());
                        UploadFileServer server = new UploadFileServer(port,downloadPathStr); // 启动服务端
                        server.load();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                // 创建文件链表服务器线程
                Thread fileLinkedListServerThread = new Thread(() -> {
                    try {
                        System.out.println("文件链表服务器启动成功！");
                        GetFileListServer getFileListServer = new GetFileListServer();
                        int port = Integer.parseInt(getFileListPort.getText());
                        getFileListServer.start(port,downloadPathStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                Thread fileDeleteServerThread = new Thread(() -> {
                    try {
                        System.out.println("文件删除服务器启动成功！");
                        int port = Integer.parseInt(deleteFilePath.getText());
                        DeleteFileServer deleteFileServer = new DeleteFileServer(port);
                        deleteFileServer.deleteFileServerLoad();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                Thread fileDowmloadServerThread = new Thread(() -> {
                    try {
                        System.out.println("文件下载服务器启动成功！");
                        int port = Integer.parseInt(downloadFilePort.getText());
                        DownloadFileServer server = new DownloadFileServer();
                        server.downloadFileServerLoad(port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                if(btnCount%2 == 1){
                    startBtn.setText("停止运行");
                    fileDeleteServerThread.start();
                    fileTransferServerThread.start();
                    fileLinkedListServerThread.start();
                    fileDowmloadServerThread.start();
                    selectPathBtn.setVisible(false);
                }else {
                    startBtn.setText("启动服务器");
                    fileDeleteServerThread.interrupt();
                    fileTransferServerThread.interrupt();
                    fileLinkedListServerThread.interrupt();
                    fileDowmloadServerThread.interrupt();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        downloadFilePort.setText(String.valueOf(8885));
        uploadFilePort.setText(String.valueOf(8886));
        deleteFilePath.setText(String.valueOf(8888));
        getFileListPort.setText(String.valueOf(8887));
    }
}
