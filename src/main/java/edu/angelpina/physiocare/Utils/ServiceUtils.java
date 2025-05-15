package edu.angelpina.physiocare.Utils;

import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

public class ServiceUtils {
    static Session session;
    private static String path = "records";
    private static Vector<ChannelSftp.LsEntry> list;

    private static void connection() {
        JSch jsch = new JSch();
        try {
            session = jsch.getSession("physiocaredata", "angelpg.es", 22);
            session.setPassword("DamDaw");

            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            System.out.println("Connected to SFTP server");
        } catch (Exception e) {
            System.out.println("Error connecting to SFTP server: " + e.getMessage());
        }
    }

    private static void closeConnections() {
        try {
            if (session != null) {
                session.disconnect();
                System.out.println("Disconnected from SFTP server");
            }
        } catch (Exception ignored) {}
    }

    private static void listDir() {
        ChannelSftp sftpChannel = null;
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;

            list = sftpChannel.ls(path);
            System.out.println(list.size() + " pdfs found");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sftpChannel != null) {
                sftpChannel.exit();
            }
        }
    }

    public static void uploadFile(String filename) {
        connection();
        listDir();
        ChannelSftp sftpChannel = null;
        try {
            File file = new File(filename);
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;

            if(list.contains(file.getName())) {
                System.out.println("File already exists");
                if(deleteFile(file)) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        sftpChannel.put(fis, path + "/" + file.getName());
                        System.out.println("File overwritten");
                    }
                }
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    sftpChannel.put(fis, path + "/" + file.getName());
                    System.out.println("File uploaded");
                }
            }

        } catch (Exception e) {
            System.out.println("Error uploading file: " + e.getMessage());
        } finally {
            if (sftpChannel != null) {
                sftpChannel.exit();
            }
            closeConnections();
        }
    }

    public static boolean deleteFile(File file) {
        ChannelSftp sftpChannel = null;
        boolean deleted = false;
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;

            sftpChannel.rm(path + "/" + file.getName());
            System.out.println("File deleted");
            listDir();
            deleted = true;
        } catch (SftpException e) {
        } catch (Exception e) {
        } finally {
            if (sftpChannel != null) {
                sftpChannel.exit();
            }
        }
        return deleted;
    }
}
