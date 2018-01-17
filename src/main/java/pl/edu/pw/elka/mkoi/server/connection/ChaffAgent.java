/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import pl.edu.pw.elka.mkoi.server.crypto.HMAC;

/**
 *
 * @author rafal
 */
public class ChaffAgent extends Thread {

    private ServerSocket ss;
    private Socket toSendSocket;
    private int clientPort;
    private HMAC hmac = new HMAC();

    public ChaffAgent(int serverPort, int clientPort) {
        try {
            ss = new ServerSocket(serverPort);
//            toSendSocket = new Socket("localhost", clientPort);
            this.clientPort = clientPort;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChaffAgent(int serverPort) {
        try {
            ss = new ServerSocket(serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket clientSock = ss.accept();
                attachChaff(clientSock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void attachChaff(Socket clientSock) throws IOException {

        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        DataOutputStream dos = null;

        byte[] buffer = new byte[4096];
        byte[] hmacAttached = new byte[64];

        int read = 0;
        int totalRead = 0;

        while ((read = dis.read(buffer, 0, buffer.length)) != -1) {
            initializeSocket();
            if (dos == null) {
                dos = new DataOutputStream(toSendSocket.getOutputStream());
            }
            totalRead += read;
            dis.read(hmacAttached, 0, hmacAttached.length);
//           add to different position
            int messagePosition = generateMessagePosition(Properties.ADDITIONAL_CHAF_PER_BUFFER + 1);
            for (int i = 0; i < Properties.ADDITIONAL_CHAF_PER_BUFFER + 1; i++) {
                if (i == messagePosition) {
                    dos.write(buffer);
                    dos.write(hmacAttached);
                    System.out.println("CA says : received HMAC " +Hex.toHexString(hmacAttached));
                } else {
                    byte[] chaff = generateChaff();
                    byte[] chaffMac = hmac.hmac("anotherKey".getBytes(), chaff, new SHA3.Digest512(), 64);
                    System.out.println("CA says : generated winnnowed HMAC " +Hex.toHexString(chaffMac));
                    dos.write(chaff);
                    dos.write(chaffMac);
                }
            }

        }
        dis.close();
        dos.close();
    }

    public int generateMessagePosition(int nr) {
        return new Random().nextInt(nr);
    }

    private byte[] generateChaff() {
        byte[] randomBytes = new byte[4096];
        new Random().nextBytes(randomBytes);
        return randomBytes;
    }

    public static void main(String[] args) {
        //client sends file
        ChaffAgent responseSecure = new ChaffAgent(Properties.SERVER_SEND_PORT, Properties.CLIENT_RECEIVE_PORT);
        responseSecure.start();
        ChaffAgent ca = new ChaffAgent(Properties.CLIENT_SEND_PORT, Properties.SERVER_RECEIVE_PORT);
        ca.start();
        
    }

    private void initializeSocket() throws IOException {
        if (toSendSocket == null) {
            toSendSocket = new Socket("localhost", clientPort);
        }
    }
}
