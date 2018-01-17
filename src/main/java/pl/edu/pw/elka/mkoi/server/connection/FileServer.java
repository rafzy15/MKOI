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
import java.util.Locale;
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
    private Socket toSendSocket;
    private HMAC hmac = new HMAC();
    private boolean transmitingFile = false;
    private JSONcreator jSONcreator = new JSONcreator();

    public FileServer(int receivePort) {
        try {
            ss = new ServerSocket(receivePort);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket clientSock = ss.accept();
                saveFile(clientSock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //make it on button click
    private void saveFile(Socket clientSock) throws IOException {
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        DataOutputStream dos = new DataOutputStream(clientSock.getOutputStream());
        FileOutputStream fos = new FileOutputStream("pom1244.pdf");

        byte[] buffer = new byte[4096];

        boolean read;
        while (read = (dis.read(buffer,0, buffer.length)) != -1) {
            byte[] hmacAttached = new byte[64];
            dis.read(hmacAttached, 0, hmacAttached.length);
            if (!transmitingFile) {
                waitForClient(buffer, dos, hmacAttached);
            } else {
                handleTransmitingFile(buffer, dis, fos, hmacAttached);
                System.out.println("readed " + buffer.length);
                try {
                    JSONObject clientMessage = new JSONObject(new String(buffer));
                    System.out.println("Server says : Received message " + clientMessage);
                    if (clientMessage.getString(Properties.MESSAGE_TYPE).equals(Properties.FINISHED_SENDING)) {
                        System.out.println("Server says : Received message " + clientMessage);
                        transmitingFile = false;
                    }
                } catch (JSONException e) {
                    System.out.println("exception");
                }
            }
        }

        dos.close();
        dis.close();
        fos.close();
    }

//    public void sendFile(String file, Socket s) throws IOException {
//        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
//        FileInputStream fis = new FileInputStream(file);
//        byte[] buffer = new byte[4096];
//        while (fis.read(buffer) > 0) {
//            byte[] mac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
//            dos.write(buffer);
//            dos.write(mac);
//            System.out.println("mac = "
//                    + Hex.toHexString(mac));
//        }
//        fis.close();
//        dos.close();
//    }
    private void waitForClient(byte[] buffer, DataOutputStream dos, byte[] hmacAttached) throws IOException {
        byte[] ownGeneratedMac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);;
        if (hmacsEquals(hmacAttached, ownGeneratedMac)) {
            JSONObject clientMessage = new JSONObject(new String(buffer));
            if (clientMessage.getString(Properties.MESSAGE_TYPE).equals(Properties.CLIENT_SEND_FILE)) {
                System.out.println("Server says :  I received (HMAC OK) \n" + clientMessage.toString());
                dos.write(jSONcreator.createGeneralMessage(Properties.RESPONSE_TYPE, Properties.ACK).toString().getBytes());
                transmitingFile = true;
            } else if (clientMessage.getString(Properties.MESSAGE_TYPE).equals(Properties.CLIENT_REQUEST_FILE)) {

            }
        }else{
            System.out.println("bad hmac" +Hex.toHexString(ownGeneratedMac));
        }
    }

    public static void main(String[] args) {
        FileServer fs = new FileServer(Properties.SERVER_RECEIVE_PORT);
        fs.start();
    }

    private boolean hmacsEquals(byte[] ownHmac, byte[] attachedHmac) {
        return Arrays.areEqual(ownHmac, attachedHmac);
    }

    private void handleTransmitingFile(byte[] buffer, DataInputStream dis,
            FileOutputStream fos, byte[] hmacAttached) throws IOException {
        byte[] ownGeneratedMac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
        if (hmacsEquals(ownGeneratedMac, hmacAttached)) {
            System.out.println("Server says : HMAC OK " + Hex.toHexString(ownGeneratedMac));
            fos.write(buffer, 0, buffer.length);
        } else {
            System.out.println("Server says : wrong HMAC " + Hex.toHexString(ownGeneratedMac));
        }

    }
}
