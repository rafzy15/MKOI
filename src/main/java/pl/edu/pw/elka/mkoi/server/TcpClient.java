/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import jdk.nashorn.internal.objects.Global;
import org.bouncycastle.jcajce.provider.digest.BCMessageDigest;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;
import pl.edu.pw.elka.mkoi.crypto.HMAC;

/**
 *
 * @author rafal
 */
public class TcpClient extends Thread {

    private Socket sendSocket;
    private ServerSocket serverSocket;
    private HMAC hmac = new HMAC();
    private JSONcreator jSONcreator = JSONcreator.getInstance();
    private String user = "rafal";
    private String filePath = "";
    private String fileName = "";
    private String hash;
    Socket socket = null;

    public static void main(String[] args) throws IOException {
        try {
            TcpClient tcpClient = new TcpClient(Properties.CLIENT_SEND_PORT, Properties.CLIENT_RECEIVE_PORT);

//            tcpClient.sendMessages("/home/rafal/Downloads/SzymaniukRafal-KPF-esej(empiryzm w ujęciu Bacona).pdf", tcpClient.sendSocket,Properties.ACTION_REQUEST_TO_GET_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TcpClient(int clientPort, int serverPort) {
        try {
            sendSocket = new Socket("localhost", clientPort);
            serverSocket = new ServerSocket(serverPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class SingletonHolder {

        private static final TcpClient INSTANCE = new TcpClient(Properties.CLIENT_SEND_PORT, Properties.CLIENT_RECEIVE_PORT);
    }

    public static TcpClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public byte[] createByteJson(String filePath,String loggedAs) {
        this.filePath = filePath;
        File f = new File(filePath);
        fileName = f.getName(); 
        return jSONcreator.clientFileJson(Properties.CLIENT_SEND_FILE, fileName, loggedAs).toString().getBytes();
    }

    public int sendMessages(byte[] jsonRequest, int action) throws Exception {

        DataOutputStream dos = new DataOutputStream(sendSocket.getOutputStream());
        DataInputStream dis = new DataInputStream(sendSocket.getInputStream());

        //TODO Make it depandable on button click
        if (action == Properties.ACTION_REQUEST_TO_SEND_FILE) {
            requestMessage(dos, jsonRequest);
            return handleResponse(dos);
        } else if (action == Properties.ACTION_REQUEST_TO_GET_FILE) {
//            byte[] json = jSONcreator.clientRequestFile(Properties.CLIENT_REQUEST_FILE, "newFile.pdf", "rafal").toString().getBytes();
//            requestToSendFile(dos, file, json);
//            handleResponse(file, dos);
        } else if (action == Properties.ACTION_REQUEST_TO_HASH_FILE) {
//            byte[] json = jSONcreator.clientRequestFile(Properties.CLIENT_GET_HASH, "newFile.pdf", "rafal").toString().getBytes();
//            requestToSendFile(dos, file, json);
//            handleResponse(file, dos);
        }
        if (action == Properties.ACTION_LOG_IN) {
//            byte[] json = jSONcreator.clientRequestFile(Properties.CLIENT_REQUEST_FILE, "newFile.pdf", "rafal").toString().getBytes();
            requestMessage(dos, jsonRequest);
            return handleResponse(dos);

        }
//        dos.close();
        return -1;
    }

    private int handleResponse(DataOutputStream dos) throws IOException {
        byte[] buffer = new byte[4096];
        byte[] hmacAttached = new byte[64];
        boolean receivedACK = false;
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        if (socket == null) {
            socket = serverSocket.accept();
        }
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(socket.getInputStream());
            while (dis.read(buffer, 0, buffer.length) != -1) {
                byte[] ownGeneratedMac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);;
                dis.read(hmacAttached, 0, hmacAttached.length);
                if (hmacsEquals(hmacAttached, ownGeneratedMac)) {
                    String comming = new String(buffer);
                    if (comming.startsWith("{")) {
                        JSONObject jobject = new JSONObject(new String(buffer));
                        if (jobject.getString(Properties.MESSAGE_TYPE).equals(Properties.RESPONSE_TYPE)) {
                            if (jobject.getString(Properties.MESSAGE_BODY).equals("ACK-to-send")) {
                                System.out.println("Client says: Received ACK," + jobject.toString() + " I'm sending file ");
                                sendFile(dos, filePath);
                                return 1;
                            } else if (jobject.getString(Properties.MESSAGE_BODY).equals("ACK-TO-GET")) {
                                System.out.println("Client says: Received ACK-TO-GET, I'm sending file \n" + jobject);
                                filePath = jobject.getString(Properties.FILE);
                                return Properties.ACTION_GET_FILE;
                            } else if (jobject.getString(Properties.MESSAGE_BODY).startsWith("ACK-HASH")) {
                                System.out.println("Client says: Received ACK-HASH \n" + jobject);
                                filePath = jobject.getString(Properties.FILE);
                                hash = jobject.getString(Properties.MESSAGE_BODY).split(":")[1];
                                System.out.println("Client says : " + hash);
                                return Properties.ACTION_HASH;
                            } else if (jobject.getString(Properties.MESSAGE_BODY).startsWith("ACK-to-login")) {
                                System.out.println("Client says: ACK to login \n" + jobject);
                                receivedACK = true;
                                return 1;
                            } else {
                                System.out.println("Client says: Server did not send ACK" + jobject.toString());
                                return -1;
                            }
                        }
                    } else {

                    }

                } else {
                    System.out.println("Client says : wrong HMAC " + Hex.toHexString(ownGeneratedMac));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private void sendFile(DataOutputStream dos, String file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        while (fis.read(buffer) > 0) {
            byte[] mac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
            dos.write(buffer);
            dos.write(mac);
            System.out.println("Client says : mac = "
                    + Hex.toHexString(mac));
            System.out.println("Client says : bufor = "
                    + Hex.toHexString(buffer));

        }
        byte[] buffer1 = new byte[4096];
        byte[] json = jSONcreator.createGeneralMessage(Properties.FINISHED_SENDING, "OK").
                toString().getBytes();
        System.out.println(new JSONObject(new String(json)));
        buffer1 = fillArray(buffer1, json);
        byte[] mac = hmac.hmac("key".getBytes(), buffer1, new SHA3.Digest512(), 64);
        dos.write(buffer1);
        dos.write(mac);
        System.out.println("Client says : sending ACK with MAC = "
                + Hex.toHexString(mac));
        System.out.println("Client says : I finished sending file");
        fis.close();
    }

    private void requestMessage(DataOutputStream dos, byte[] json)
            throws IOException {
        byte[] buffer = new byte[4096];

        buffer = fillArray(buffer, json);
        byte[] mac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
        dos.write(buffer);
        dos.write(mac);
    }

    private byte[] fillArray(byte[] buffer, byte[] toFill) {
        for (int i = 0; i < toFill.length; i++) {
            buffer[i] = toFill[i];
        }
        return buffer;

    }

    private boolean compareHashes(String file) throws Exception {
        byte[] buffer = new byte[4096];
        Path path = Paths.get(file);
        byte[] bytesFile = Files.readAllBytes(path);
        BCMessageDigest SHA3 = new SHA3.Digest512();
        byte[] ownGeneratedSHA = SHA3.digest(bytesFile);
        if (Hex.toHexString(ownGeneratedSHA).equals(hash)) {
            System.out.println("Client says : hashes are equal " + Hex.toHexString(ownGeneratedSHA));
            System.out.println("Client says :  " + hash);
            return true;
        }
        System.out.println("Client says : hashes are different " + Hex.toHexString(ownGeneratedSHA));
        System.out.println("Client says :  " + hash);
        return false;
    }

    private void saveFile(Socket acceptSocket, String file) throws Exception {
        FileOutputStream fos = new FileOutputStream(file + "client" + ".pdf");
        byte[] buffer = new byte[4096];
        byte[] hmacAttached = new byte[64];;
        DataInputStream dis = new DataInputStream(acceptSocket.getInputStream());
        while (dis.read(buffer, 0, buffer.length) != -1) {
            byte[] ownGeneratedMac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
            dis.read(hmacAttached, 0, hmacAttached.length);
            if (hmacsEquals(ownGeneratedMac, hmacAttached)) {
                System.out.println("Client says : HMAC ok " + Hex.toHexString(ownGeneratedMac));
                fos.write(buffer);
            } else {
                System.out.println("Client says : Wrong HMAC " + Hex.toHexString(ownGeneratedMac));

            }
        }
        System.out.println("Client says : Finished saving");
        dis.close();
    }

    private boolean hmacsEquals(byte[] ownHmac, byte[] attachedHmac) {
        return org.bouncycastle.util.Arrays.areEqual(ownHmac, attachedHmac);
    }
}
