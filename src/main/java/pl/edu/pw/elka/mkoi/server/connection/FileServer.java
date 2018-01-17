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
    private HMAC hmac = new HMAC();
    private Socket toSendSocket = null;
    private boolean transmitingFile = false;
    private JSONcreator jSONcreator = new JSONcreator();

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
                Thread.sleep(10000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //make it on button click
    /*private void saveFile(Socket clientSock) throws IOException {
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        FileOutputStream fos = new FileOutputStream("pom1244.pdf");
        DataOutputStream dos = new DataOutputStream(toSendSocket.getOutputStream());
        byte[] buffer = new byte[4096];

        int read;
        while ((read = dis.read(buffer, 0, buffer.length)) != -1) {
            byte[] hmacAttached = new byte[64];
            dis.read(hmacAttached, 0, hmacAttached.length);
            if (!transmitingFile) {
                waitForClient(buffer, dos, hmacAttached);
            } else {
                handleTransmitingFile(buffer, dis, fos, hmacAttached);
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
        dis.close();
        fos.close();
    }*/
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
        } else {
            System.out.println("bad hmac" + Hex.toHexString(ownGeneratedMac));
        }
    }

    public void handleCommingMessages(Socket clientSock) throws IOException {
//        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
////        FileOutputStream fos = new FileOutputStream("pom1244.pdf");
//        DataOutputStream dos = new DataOutputStream(toSendSocket.getOutputStream());
//        switch (whatComming(dis)) {
//            case Properties.ACTION_REQUEST_TO_SEND_FILE:
//                System.out.println("wchodzi");
//                dos.write(jSONcreator.createGeneralMessage(Properties.RESPONSE_TYPE, Properties.ACK).toString().getBytes());
//                dos.flush();
//                break;
//            case Properties.ACTION_GET_FILE:
//                break;
//            case Properties.ACTION_HASH:
//                break;
//            case Properties.ACTION_LIST_FILES:
//                break;
//        }
    }

    public void whatComming(Socket incomingSocket) throws Exception {
        byte[] buffer = new byte[4096];
        byte[] hmacAttached = new byte[64];
        FileOutputStream fos = new FileOutputStream("newFile.pdf");
        DataOutputStream dos = new DataOutputStream(toSendSocket.getOutputStream());
        DataInputStream dis = new DataInputStream(incomingSocket.getInputStream());
        while (dis.read(buffer, 0, buffer.length) != -1) {
            byte[] ownGeneratedMac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
            dis.read(hmacAttached, 0, hmacAttached.length);
            System.out.println("buffer= "+ Hex.toHexString(buffer));
            if (hmacsEquals(hmacAttached, ownGeneratedMac)) {
                String comming = new String(buffer);
                if (comming.startsWith("{")) {
                    JSONObject clientMessage = new JSONObject(new String(buffer));
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
                        transmitingFile = true;
                    } else if (clientMessage.getString(Properties.MESSAGE_TYPE).
                            equals(Properties.FINISHED_SENDING)) {
                        System.out.println("Server says :  I received json message \n" + clientMessage.toString());
                        transmitingFile = false;
                    } else {
                        System.out.println("Server says :  I received json message \n" + clientMessage.toString());

                    }
                } else if (transmitingFile) {
                    System.out.println("Server says : HMAC OK "+ Hex.toHexString(ownGeneratedMac));
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
}
