/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server.connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author rafal
 */
class Packet {
    int MAC;
}

public class TcpClient {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 1234);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.print(new Packet());
            
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
