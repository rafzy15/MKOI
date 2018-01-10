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
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import pl.edu.pw.elka.mkoi.server.crypto.HMAC;

/**
 *
 * @author rafal
 */
public class FileServer extends Thread {

    private ServerSocket ss;
    private Socket toSendSocket;
    private HMAC hmac = new HMAC();

    public FileServer(int sendPort, int receivePort) {
        try {
            ss = new ServerSocket(receivePort);
//            toSendSocket = new Socket("localhost", sendPort);
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
       
        FileOutputStream fos = new FileOutputStream("pom112.pdf");
        byte[] buffer = new byte[4096];
        byte[] hmacAttached = new byte[64];

        int filesize = 15123; // Send file size in separateport msg
        int read = 0;
        int totalRead = 0;
        int remaining = filesize;
        while ((read = dis.read(buffer, 0, buffer.length)) != -1) {
            totalRead += read;
//            remaining -= read;
            dis.read(hmacAttached, 0, hmacAttached.length);
//            System.out.println("read " + totalRead + " bytes.");
            byte[] ownGeneratedMac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
            System.out.println("MY OWN mac = "
                    + Hex.toHexString(ownGeneratedMac));
            System.out.println(" received mac = "
                    + Hex.toHexString(hmacAttached));
            if (hmacsEquals(ownGeneratedMac, hmacAttached)) {
                System.out.println("OK");
                fos.write(buffer, 0, read);
            } else {
                System.out.println("Wrong HMAC");
            }
        }
        fos.close();
        dis.close();
    }
     //make it on button click
    public void sendFile(String file, Socket s) throws IOException {
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        while (fis.read(buffer) > 0) {
            byte[] mac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
            dos.write(buffer);
            dos.write(mac);
            System.out.println("mac = "
                    + Hex.toHexString(mac));
        }
        fis.close();
        dos.close();
    }

    public static void main(String[] args) {
        FileServer fs = new FileServer(Properties.SERVER_RECEIVE_PORT, Properties.SERVER_SEND_PORT);
        fs.start();
    }

    private boolean hmacsEquals(byte[] ownHmac, byte[] attachedHmac) {
        return Arrays.areEqual(ownHmac, attachedHmac);
    }

}
