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
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import org.bouncycastle.jcajce.provider.digest.BCMessageDigest;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONException;
import org.json.JSONObject;
import pl.edu.pw.elka.mkoi.server.crypto.HMAC;

/**
 *
 * @author rafal
 */
public class FileServer extends Thread {

    private ServerSocket ss;
    private HMAC hmac = new HMAC();
    private Socket toSendSocket = null;
    private int transmitingFileAction = -1;
    private JSONcreator jSONcreator = new JSONcreator();
    private String login = "rafal";
    public FileServer(int receivePort) {
        try {
            ss = new ServerSocket(receivePort);
            toSendSocket = new Socket("localhost", Properties.SERVER_SEND_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket clientSock = ss.accept();
                whatComming(clientSock);
                Thread.sleep(100);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void whatComming(Socket incomingSocket) throws Exception {
        byte[] buffer = new byte[4096];
        byte[] hmacAttached = new byte[64];
        FileOutputStream fos = new FileOutputStream(login+"newFile.pdf");
        DataOutputStream dos = new DataOutputStream(toSendSocket.getOutputStream());
        DataInputStream dis = new DataInputStream(incomingSocket.getInputStream());
        while (dis.read(buffer, 0, buffer.length) != -1) {
            byte[] ownGeneratedMac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
            dis.read(hmacAttached, 0, hmacAttached.length);
            if (hmacsEquals(hmacAttached, ownGeneratedMac)) {
                String comming = new String(buffer);
                if (comming.startsWith("{")) {
                    JSONObject clientMessage = new JSONObject(new String(buffer));
                    System.out.println("Server says :  I received json message \n" + clientMessage.toString());
                    if (clientMessage.getString(Properties.MESSAGE_TYPE).
                            equals(Properties.CLIENT_SEND_FILE)) {
                        System.out.println("Server says :  I received (HMAC OK) \n" + clientMessage.toString());
                        byte[] json = jSONcreator.createGeneralMessage(Properties.RESPONSE_TYPE,
                                Properties.ACK).toString().getBytes();
                        byte[] buffer1 = new byte[4096];
                        buffer1 = fillArray(buffer1, json);
                        dos.write(buffer1);
                        byte[] ownGeneratedMacSend = hmac.hmac("key".getBytes(), buffer1, new SHA3.Digest512(), 64);
                        dos.write(ownGeneratedMacSend);
                        transmitingFileAction = Properties.ACTION_SEND_FILE;
                    } else if (clientMessage.getString(Properties.MESSAGE_TYPE).
                            equals(Properties.FINISHED_SENDING)) {
                        System.out.println("Server says :  I received json message \n" + clientMessage.toString());
                        transmitingFileAction = -1;
                    } else if (clientMessage.getString(Properties.MESSAGE_TYPE).
                            equals(Properties.CLIENT_REQUEST_FILE)){
                        transmitingFileAction = Properties.ACTION_GET_FILE;
                        clientMessage.get(Properties.FILE);
                        login = clientMessage.getString("login");
                        byte[] json = jSONcreator.createGeneralMessage(Properties.RESPONSE_TYPE,
                                "ACK-TO-GET").toString().getBytes();
                        byte[] buffer1 = new byte[4096];
                        buffer1 = fillArray(buffer1, json);
                        dos.write(buffer1);
                        byte[] ownGeneratedMacSend = hmac.hmac("key".getBytes(), buffer1, new SHA3.Digest512(), 64);
                        dos.write(ownGeneratedMacSend);
                        sendFile(dos, "newFile.pdf");
                    }else if(clientMessage.getString(Properties.MESSAGE_TYPE).
                            equals(Properties.CLIENT_GET_HASH)){
                        Path path = Paths.get("newFile.pdf");
                        byte[] bytesFile = Files.readAllBytes(path);
                        BCMessageDigest SHA3 = new SHA3.Digest512();
                        byte[] ownGeneratedSHA = SHA3.digest(bytesFile);
                        byte[] json = jSONcreator.createGeneralMessage(Properties.RESPONSE_TYPE,
                                "ACK-HASH:" + ownGeneratedSHA).toString().getBytes();
                       
                        dos.write(json);
                    }
                    else {
                        System.out.println("Server says :  I received json message \n" + clientMessage.toString());

                    }
                } else if (transmitingFileAction == Properties.ACTION_SEND_FILE) {
                    System.out.println("Server says : HMAC OK " + Hex.toHexString(ownGeneratedMac));
                    fos.write(buffer, 0, buffer.length);
                }

            } else {
                System.out.println("Server says : Wrong MAC not reading " + Hex.toHexString(ownGeneratedMac));

            }
        }
        fos.close();
        dis.close();
        dos.close();
    }

    private boolean hmacsEquals(byte[] ownHmac, byte[] attachedHmac) {
        return Arrays.areEqual(ownHmac, attachedHmac);
    }

    private void saveStreamFile(byte[] buffer, FileOutputStream fos) throws IOException {
        fos.write(buffer, 0, buffer.length);
    }

    public static void main(String[] args) {
        FileServer fs = new FileServer(Properties.SERVER_RECEIVE_PORT);
        fs.start();
    }

    private byte[] fillArray(byte[] buffer, byte[] toFill) {
        for (int i = 0; i < toFill.length; i++) {
            buffer[i] = toFill[i];
        }
        return buffer;

    }
     private void sendFile(DataOutputStream dos, String file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        while (fis.read(buffer) > 0) {
            byte[] mac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
            dos.write(buffer);
            dos.write(mac);
            System.out.println("Server says : my mac = "
                    + Hex.toHexString(mac));
            System.out.println("Server says : bufor = "
                    + Hex.toHexString(buffer));

        }
        byte[] buffer1 = new byte[4096];
        byte[] json = jSONcreator.createGeneralMessage(Properties.FINISHED_SENDING, "OK").
                toString().getBytes();
        buffer1 = fillArray(buffer1, json);
        byte[] mac = hmac.hmac("key".getBytes(), buffer1, new SHA3.Digest512(), 64);
        dos.write(buffer1);
        dos.write(mac);
        System.out.println("Server says : sending ACK with MAC = "
                + Hex.toHexString(mac));
        System.out.println("Server says : I finished sending file");
        fis.close();
    }
}
