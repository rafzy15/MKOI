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
    private HMAC hmac = new HMAC();

    public ChaffAgent(int serverPort, int clientPort) {
        try {
            ss = new ServerSocket(serverPort);
            toSendSocket = new Socket("localhost", clientPort);
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

    private void saveFile(Socket clientSock) throws IOException {
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
//        FileOutputStream fos = new FileOutputStream("pom11.pdf");
        DataOutputStream dos = new DataOutputStream(toSendSocket.getOutputStream());

        byte[] buffer = new byte[4096];
        byte[] hmacAttached = new byte[64];

        int read = 0;
        int totalRead = 0;
        while ((read = dis.read(buffer, 0, buffer.length)) != -1) {
            totalRead += read;
            dis.read(hmacAttached, 0, hmacAttached.length);
//           add to different position
            int messagePosition =generateMessagePosition(Properties.ADDITIONAL_CHAF_PER_BUFFER+1);
            for (int i = 0; i < Properties.ADDITIONAL_CHAF_PER_BUFFER+1; i++) {
                if(i== messagePosition){
                    dos.write(buffer);
                    dos.write(hmacAttached);
                }else{
                    byte[] chaff = generateChaff();
                    byte[] chaffMac = hmac.hmac("anotherKey".getBytes(), chaff, new SHA3.Digest512(), 64);
                    dos.write(chaff);
                    dos.write(chaffMac);
                }
            }

        }
        dis.close();
    }
    public int generateMessagePosition(int nr){
        return new Random().nextInt(nr);
    }

    public byte[] generateChaff() {
        byte[] randomBytes = new byte[4096];
        new Random().nextBytes(randomBytes);
        return randomBytes;
    }

    public static void main(String[] args) {
        //client sends file
        FileServer fs = new FileServer(Properties.SERVER_RECEIVE_PORT,Properties.SERVER_SEND_PORT);
        fs.start();
        ChaffAgent ca = new ChaffAgent(Properties.CLIENT_SEND_PORT, Properties.SERVER_RECEIVE_PORT);
        ca.start();
    }
}
