/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server.connection;

//import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
//import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import static pl.edu.pw.elka.mkoi.server.connection.Server.FILE_TO_SEND;

/**
 *
 * @author rafal
 */
public class TcpServer {

    public static void main(String[] args) throws IOException {
        send();
    }
    static BufferedInputStream bis = null;
    static OutputStream os = null;
    public static void send() throws IOException{
        
        ServerSocket socket = new ServerSocket(1234);
        Socket sock = socket.accept();
        File myFile = new File(FILE_TO_SEND);
        byte[] mybytearray = new byte[(int) myFile.length()];
        bis = new BufferedInputStream(new FileInputStream(myFile));
        System.out.println(bis.toString());
        bis.read(mybytearray, 0, mybytearray.length);
        os = sock.getOutputStream();
        socket.close();
        os.write(mybytearray, 0, mybytearray.length);
        os.flush();
    }

    private void openFile(String path) {

    }
}
