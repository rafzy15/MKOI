/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server.connection;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import pl.edu.pw.elka.mkoi.server.crypto.HMAC;

/**
 *
 * @author rafal
 */
class Packet {

    int MAC;
}

public class TcpClient {
    private HMAC hmac = new HMAC();
    public static void main(String[] args) throws Exception {
        TcpClient tcpClient = new TcpClient();
        tcpClient.createSocketAndSend("pom.xml",1988);
    }
    public void createSocketAndSend(String file,int port){
        try {
            Socket s;
            TcpClient tcpClient = new TcpClient();
            s = new Socket("localhost",port);
            tcpClient.sendFile(file,s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  void sendFile(String file,Socket s) throws IOException {
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        
        while (fis.read(buffer) > 0) {
            byte[] mac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
            dos.write(buffer);
            dos.write(mac);
            System.out.println("mess = " + Hex.toHexString(buffer) + "mac = " 
                    + Hex.toHexString(mac));
        }

        fis.close();
        dos.close();
    }
     
}
