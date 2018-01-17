/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;
import pl.edu.pw.elka.mkoi.server.crypto.HMAC;

/**
 *
 * @author rafal
 */
class Packet {

    int MAC;
}

public class TcpClient {

    private Socket sendSocket;
    private ServerSocket receiveSocket;
    private HMAC hmac = new HMAC();
    private JSONcreator jSONcreator = new JSONcreator();
    private int action = -1;

    public static void main(String[] args) throws IOException {
        TcpClient tcpClient = new TcpClient(Properties.CLIENT_SEND_PORT,
                Properties.ACTION_REQUEST_TO_SEND_FILE);
        tcpClient.sendMessages("/home/rafal/Downloads/SzymaniukRafal-KPF-esej(empiryzm w ujęciu Bacona).pdf", tcpClient.sendSocket);
    }

    public TcpClient(int clientPort, int action) {
        try {
            sendSocket = new Socket("localhost", clientPort);
            this.action = action;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessages(String file, Socket s) throws IOException {
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
//        DataInputStream dis = new DataInputStream(s.getInputStream());

        //TODO Make it depandable on button click
        if (action == Properties.ACTION_REQUEST_TO_SEND_FILE) {
            requestToSendFile(dos, file);
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
                JSONObject jobject = new JSONObject(new String(buffer));
                if (jobject.getString(Properties.MESSAGE_TYPE).equals(Properties.RESPONSE_TYPE)) {
                    if (jobject.getString(Properties.MESSAGE_BODY).equals(Properties.ACK)) {
                        System.out.println("Client says: Received ACK, I'm sending file " + file);
                        dis.close();
                        return Properties.ACTION_SEND_FILE;
                    } else {
                        System.out.println("Client says: Server did not send ACK");
                        dis.close();
                    }
                }
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
            if(buffer.length < 4096){
                System.out.println("buff < " + buffer.length);
            }
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

    private void requestToSendFile(DataOutputStream dos, String file)
            throws IOException {
        byte[] buffer = new byte[4096];
        System.out.println("Client says : I'm sending request to send file " + file);
        byte[] json = jSONcreator.clientRequestFile(Properties.CLIENT_SEND_FILE, file).toString().getBytes();
        buffer = fillArray(buffer, json);
        byte[] mac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
        dos.write(json);
        dos.write(mac);

    }

    private byte[] fillArray(byte[] buffer, byte[] toFill) {
        for (int i = 0; i < toFill.length; i++) {
            buffer[i] = toFill[i];
        }
        return buffer;

    }

    private void handleResponse(String file, DataOutputStream dos) throws IOException {
        Socket acceptSocket = null;
        if (receiveSocket == null) {
            receiveSocket = new ServerSocket(Properties.CLIENT_RECEIVE_PORT);
        }
        if (acceptSocket == null) {
            long elapsedTime = 0L;
            while (elapsedTime < Properties.TIMEOUT) {
                acceptSocket = receiveSocket.accept();
                switch (whatComming(file, acceptSocket)) {
                    case Properties.ACTION_SEND_FILE:
                        sendFile(dos, file);
                        action = -1;
                        break;
                    case Properties.ACTION_GET_FILE:
                        break;
                    case Properties.ACTION_HASH:
                        break;
                    case Properties.ACTION_LIST_FILES:
                        break;
                }

            }
        }
    }

    private boolean hmacsEquals(byte[] ownHmac, byte[] attachedHmac) {
        return org.bouncycastle.util.Arrays.areEqual(ownHmac, attachedHmac);
    }
}
