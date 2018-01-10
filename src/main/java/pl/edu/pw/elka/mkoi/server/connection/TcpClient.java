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
import java.net.Socket;
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

    Socket s;
    private HMAC hmac = new HMAC();

    public static void main(String[] args) throws Exception {
        TcpClient tcpClient = new TcpClient(Properties.CLIENT_SEND_PORT);
        tcpClient.sendFile("/home/rafal/Downloads/SzymaniukRafal-KPF-esej(empiryzm w ujęciu Bacona).pdf");
//        tcpClient.sendFile("pom.xml",tcpClient.s);
    }

    public TcpClient(int port) {
        try {
            s = new Socket("localhost", port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(String filename) {
        JSONcreator jSONcreator = new JSONcreator();
        JSONObject message = jSONcreator.getFileMessage(Properties.MESSAGE_GET_FILE, filename);

    }

    public void sendMessage(JSONObject jsonMessage) throws IOException {
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        dos.write(jsonMessage.toString().getBytes());
        byte[] buffer = new byte[4096];
        dos.close();
    }

    public void sendFile(String file) throws IOException {
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

}
