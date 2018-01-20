/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
import java.util.Timer;
import java.util.TimerTask;
import org.bouncycastle.jcajce.provider.digest.BCMessageDigest;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;
import pl.edu.pw.elka.mkoi.server.crypto.HMAC;

/**
 *
 * @author rafal
 */
public class TcpClient {

    private Socket sendSocket;
    private ServerSocket receiveSocket;
    private HMAC hmac = new HMAC();
    private JSONcreator jSONcreator = new JSONcreator();
    private int action = -1;
    private boolean transmitted = false;
    private String user = "rafal";
    private String fileName = "";
    private String hash;
    public static void main(String[] args) throws IOException {
        try {
            TcpClient tcpClient = new TcpClient(Properties.CLIENT_SEND_PORT,
                    Properties.ACTION_REQUEST_TO_SEND_FILE);
            tcpClient.sendMessages("/home/rafal/Downloads/SzymaniukRafal-KPF-esej(empiryzm w ujęciu Bacona).pdf", tcpClient.sendSocket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TcpClient(int clientPort, int action) {
        try {
            sendSocket = new Socket("localhost", clientPort);
            this.action = action;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessages(String file, Socket s) throws Exception {
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        DataInputStream dis = new DataInputStream(s.getInputStream());

        //TODO Make it depandable on button click
        if (action == Properties.ACTION_REQUEST_TO_SEND_FILE) {
            byte[] json = jSONcreator.clientRequestFile(Properties.CLIENT_SEND_FILE, file, "rafal").toString().getBytes();
            requestToSendFile(dos, file, json);
            handleResponse(file, dos);
        } else if (action == Properties.ACTION_REQUEST_TO_GET_FILE) {
            byte[] json = jSONcreator.clientRequestFile(Properties.CLIENT_REQUEST_FILE, "newFile.pdf", "rafal").toString().getBytes();
            requestToSendFile(dos, file, json);
            handleResponse(file, dos);
        }else if (action == Properties.ACTION_REQUEST_TO_HASH_FILE) {
            byte[] json = jSONcreator.clientRequestFile(Properties.CLIENT_GET_HASH, "newFile.pdf", "rafal").toString().getBytes();
            requestToSendFile(dos, file, json);
            handleResponse(file, dos);
        }
//        Socket receiveSocket = new Socket("localhost",2584);

        dos.close();
    }

    public int whatComming(String file, Socket acceptSocket) throws IOException {
        byte[] buffer = new byte[4096];
        byte[] hmacAttached = new byte[64];
        DataInputStream dis = new DataInputStream(acceptSocket.getInputStream());
        while (dis.read(buffer, 0, buffer.length) != -1) {
            byte[] ownGeneratedMac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);;
            dis.read(hmacAttached, 0, hmacAttached.length);
            if (hmacsEquals(hmacAttached, ownGeneratedMac)) {
                String comming = new String(buffer);
                if (comming.startsWith("{")) {
                    JSONObject jobject = new JSONObject(new String(buffer));
                    if (jobject.getString(Properties.MESSAGE_TYPE).equals(Properties.RESPONSE_TYPE)) {
                        System.out.println("Client says:  \n" + jobject);
                        if (jobject.getString(Properties.MESSAGE_BODY).equals(Properties.ACK)) {
                            System.out.println("Client says: Received ACK, I'm sending file " + file);
                            dis.close();
                            return Properties.ACTION_SEND_FILE;
                        } else if (jobject.getString(Properties.MESSAGE_BODY).equals("ACK-TO-GET")) {
                            System.out.println("Client says: Received ACK-TO-GET, I'm sending file \n" + jobject);
                            fileName = jobject.getString(Properties.FILE);
                            return Properties.ACTION_GET_FILE;
                        } else if (jobject.getString(Properties.MESSAGE_BODY).startsWith("ACK-HASH")) {
                            System.out.println("Client says: Received ACK-HASH \n" + jobject);
                            fileName = jobject.getString(Properties.FILE);
                            hash = jobject.getString(Properties.MESSAGE_BODY).split(":")[1];
                            System.out.println("Client says : " + hash);
                            return Properties.ACTION_HASH;
                        } else {
                            System.out.println("Client says: Server did not send ACK");
                            dis.close();
                        }
                    }
                } else {

                }

            } else {
                System.out.println("Client says : wrong HMAC " + Hex.toHexString(ownGeneratedMac));
            }
        }
        dis.close();
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
        buffer1 = fillArray(buffer1, json);
        byte[] mac = hmac.hmac("key".getBytes(), buffer1, new SHA3.Digest512(), 64);
        dos.write(buffer1);
        dos.write(mac);
        System.out.println("Client says : sending ACK with MAC = "
                + Hex.toHexString(mac));
        System.out.println("Client says : I finished sending file");
        fis.close();
    }

    private void requestToSendFile(DataOutputStream dos, String file, byte[] json)
            throws IOException {
        byte[] buffer = new byte[4096];
        System.out.println("Client says : I'm sending request to send file " + file);
        
        buffer = fillArray(buffer, json);
        byte[] mac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
        dos.write(json);
        dos.write(mac);
        transmitted = true;
    }

    private byte[] fillArray(byte[] buffer, byte[] toFill) {
        for (int i = 0; i < toFill.length; i++) {
            buffer[i] = toFill[i];
        }
        return buffer;

    }

    private void handleResponse(String file, DataOutputStream dos) throws Exception {
        Socket acceptSocket = null;
        if (receiveSocket == null) {
            receiveSocket = new ServerSocket(Properties.CLIENT_RECEIVE_PORT);
        }
        if (acceptSocket == null) {
            long elapsedTime = 0L;
//            while (elapsedTime < Properties.TIMEOUT) {

            while (true) {

                acceptSocket = receiveSocket.accept();

                switch (whatComming(file, acceptSocket)) {
                    case Properties.ACTION_SEND_FILE:
                        sendFile(dos, file);
                        action = -1;
                        transmitted = false;
                        break;
                    case Properties.ACTION_GET_FILE:
                        action = -1;
                        saveFile(acceptSocket, fileName);
                        transmitted = false;
                        break;
                    case Properties.ACTION_HASH:
                        compareHashes("newFile.pdf");
                        break;
                    case Properties.ACTION_LIST_FILES:
                        break;
                }

            }
        }
    }
    private boolean compareHashes(String file)throws Exception{
        byte[] buffer = new byte[4096];
        Path path = Paths.get(file);
        byte[] bytesFile = Files.readAllBytes(path);
        BCMessageDigest SHA3 = new SHA3.Digest512();
        byte[] ownGeneratedSHA = SHA3.digest(bytesFile);
        if(Hex.toHexString(ownGeneratedSHA).equals(hash)){
            System.out.println("Client says : hashes are equal " + Hex.toHexString(ownGeneratedSHA) );
            System.out.println("Client says :  " + hash );
            return true;
        }
        System.out.println("Client says : hashes are different " + Hex.toHexString(ownGeneratedSHA) );
        System.out.println("Client says :  " + hash );
        return false;
    }

    private void saveFile(Socket acceptSocket, String file) throws Exception {
        FileOutputStream fos = new FileOutputStream(file + "client" +".pdf");
        byte[] buffer = new byte[4096];
        byte[] hmacAttached = new byte[64];;
        DataInputStream dis = new DataInputStream(acceptSocket.getInputStream());
        while (dis.read(buffer, 0, buffer.length) != -1) {
            byte[] ownGeneratedMac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
            dis.read(hmacAttached, 0, hmacAttached.length);
            if (hmacsEquals(ownGeneratedMac, hmacAttached)) {
                System.out.println("Client says : HMAC ok " + Hex.toHexString(ownGeneratedMac));
                fos.write(buffer);
            }else{
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
