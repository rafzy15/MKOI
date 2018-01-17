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

    private Socket s;
    private HMAC hmac = new HMAC();
    private JSONcreator jSONcreator = new JSONcreator();

    public static void main(String[] args) throws IOException {
        TcpClient tcpClient = new TcpClient(Properties.CLIENT_SEND_PORT);

//        tcpClient.sendFile("/home/rafal/Downloads/SzymaniukRafal-KPF-esej(empiryzm w ujęciu Bacona).pdf", tcpClient.ss);
        tcpClient.sendE2EFile("/home/rafal/Downloads/SzymaniukRafal-KPF-esej(empiryzm w ujęciu Bacona).pdf", tcpClient.s);
    }

    public TcpClient(int clientPort) {
        try {
//            s = new Socket("localhost", port);
            s = new Socket("localhost", clientPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendE2EFile(String file, Socket s) throws IOException {
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        DataInputStream dis = new DataInputStream(s.getInputStream());
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
//        Arrays.fill(buffer,(byte) 0);
        
//        dos.write(jSONcreator.createLoginMessage("rafal", "rafal").toString().getBytes());
//        dos.write(jSONcreator.clientRequestSendFile(Properties.REQUEST_FILE, "pom.xml").toString().getBytes());
        System.out.println("Client says : I'm sending request to send file " + file);
        //TODO Make it depandable on button click
        byte[] json = jSONcreator.clientRequestFile(Properties.CLIENT_SEND_FILE, file).toString().getBytes();
        for(int i=0;i<json.length;i++){
           buffer[i] = json[i]; 
        }
        byte[] mac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
        dos.write(json); 
        dos.write(mac);
        while (dis.read(buffer, 0, buffer.length) != -1) {
            JSONObject jobject = new JSONObject(new String(buffer));
            if (jobject.getString(Properties.MESSAGE_TYPE).equals(Properties.RESPONSE_TYPE)) {
                if (jobject.getString(Properties.MESSAGE_BODY).equals(Properties.ACK)) {
                    System.out.println("Client says: Received ACK, I'm sending file " + file );
                    sendFile(dos, fis, buffer);
                } else {
                    System.out.println("Client says: Server did not send ACK");
                }
            }
        }

        dis.close();
        fis.close();
        dos.close();
    }

    private void sendFile(DataOutputStream dos, FileInputStream fis,byte[] buffer) throws IOException{
        while (fis.read(buffer) > 0) {
            byte[] mac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
            dos.write(buffer);
            dos.write(mac);
            System.out.println("mac = "
                    + Hex.toHexString(mac));
        }
        
        dos.write(jSONcreator.createGeneralMessage(Properties.FINISHED_SENDING, "OK").
                toString().getBytes());
        System.out.println("Client says : I finished sending file");
        
    }
}
