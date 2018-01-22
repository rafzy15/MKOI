/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import org.bouncycastle.jcajce.provider.digest.BCMessageDigest;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONException;
import org.json.JSONObject;
import pl.edu.pw.elka.mkoi.crypto.HMAC;

/**
 *
 * @author rafal
 */
public class FileServer extends Thread {

    private ServerSocket ss;
    private HMAC hmac = new HMAC();
    private Socket toSendSocket = null;
    private int transmitingFileAction = -1;
    private boolean clientSendingFile = false;
    private JSONcreator jSONcreator = JSONcreator.getInstance();
    private String login = "";
    private String ClientPublicKey = "";

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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void whatComming(Socket incomingSocket) throws Exception {
        byte[] buffer = new byte[4096];
        byte[] hmacAttached = new byte[64];
        FileOutputStream fos = new FileOutputStream(login + "newFile.pdf");
        DataOutputStream dos = new DataOutputStream(toSendSocket.getOutputStream());
        DataInputStream dis = new DataInputStream(incomingSocket.getInputStream());
        while (dis.read(buffer, 0, buffer.length) != -1) {
            byte[] ownGeneratedMac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
            dis.read(hmacAttached, 0, hmacAttached.length);
            if (hmacsEquals(hmacAttached, ownGeneratedMac)) {
                String comming = new String(buffer);
                comming = comming.trim();
                if (comming.startsWith("{") && comming.endsWith("}")) {
                    JSONObject clientMessage = new JSONObject(new String(buffer));
                    switch (clientMessage.getString(Properties.MESSAGE_TYPE)) {
                        case Properties.CLIENT_LOG_IN_REQUEST: {
                            System.out.println("Server says :  I received (HMAC OK) \n" + clientMessage.toString());
                            byte[] buffer1 = new byte[4096];
                            byte[] json = null;
                            if (verifyUser(clientMessage.getString("Login"), clientMessage.getString("Password"))) {
                                json = jSONcreator.createGeneralMessage(Properties.RESPONSE_TYPE,
                                        "ACK-to-login").toString().getBytes();

                            } else {
                                json = jSONcreator.createGeneralMessage(Properties.RESPONSE_TYPE,
                                        "Not allowed to log in").toString().getBytes();
                            }
                            buffer1 = fillArray(buffer1, json);
                            dos.write(buffer1);
                            byte[] ownGeneratedMacSend = hmac.hmac("key".getBytes(), buffer1, new SHA3.Digest512(), 64);
                            dos.write(ownGeneratedMacSend);
                            break;
                        }
                        case Properties.CLIENT_SEND_FILE: {
                            System.out.println("Server says :  I received (HMAC OK) \n" + clientMessage.toString());
                            byte[] json = jSONcreator.createGeneralMessage(Properties.RESPONSE_TYPE,
                                    "ACK-to-send").toString().getBytes();
                            byte[] buffer1 = new byte[4096];
                            buffer1 = fillArray(buffer1, json);
                            dos.write(buffer1);
                            byte[] ownGeneratedMacSend = hmac.hmac("key".getBytes(), buffer1, new SHA3.Digest512(), 64);
                            dos.write(ownGeneratedMacSend);
                            String fileName = clientMessage.getString(Properties.FILE);
                            String login = clientMessage.getString(Properties.LOGGED_AS);
                            fos = new FileOutputStream(login + fileName);
                            clientSendingFile = true;
                            break;
                        }
                        case Properties.FINISHED_SENDING: {
                            System.out.println("Server says :  I received json message \n" + clientMessage.toString());
                            clientSendingFile = false;
                            break;
                        }
                        case Properties.CLIENT_LIST_MY_FILES: {
                            System.out.println("Server says :  I received (HMAC OK) \n" + clientMessage.toString());
                            List<String> userFiles = checkUsersFile(clientMessage.getString(Properties.LOGGED_AS));
                            byte[] json = jSONcreator.createListResponse(Properties.RESPONSE_TYPE, "ACK-to-list",
                                    userFiles.toString()).toString().getBytes();
                            byte[] buffer1 = new byte[4096];
                            buffer1 = fillArray(buffer1, json);
                            dos.write(buffer1);
                            byte[] ownGeneratedMacSend = hmac.hmac("key".getBytes(), buffer1, new SHA3.Digest512(), 64);
                            dos.write(ownGeneratedMacSend);
                            break;
                        }
                        case Properties.CLIENT_GET_FILE: {
                            System.out.println("Server says :  I received (HMAC OK) \n" + clientMessage.toString());
                            login = clientMessage.getString(Properties.LOGGED_AS);
                            String fileName = login + clientMessage.getString(Properties.FILE).trim();
                            byte[] json = jSONcreator.createGeneralMessage(Properties.RESPONSE_TYPE,
                                    "ACK-to-get").toString().getBytes();
                            byte[] buffer1 = new byte[4096];
                            buffer1 = fillArray(buffer1, json);
                            dos.write(buffer1);
                            byte[] ownGeneratedMacSend = hmac.hmac("key".getBytes(), buffer1, new SHA3.Digest512(), 64);
                            dos.write(ownGeneratedMacSend);
                            sendFile(dos, fileName);
                            break;
                        }
                        case Properties.CLIENT_GET_HASH: {
                            System.out.println("Server says :  I received (HMAC OK) \n" + clientMessage.toString());
                            login = clientMessage.getString(Properties.LOGGED_AS);
                            String fileName = login + clientMessage.getString(Properties.FILE).trim();
                            Path path = Paths.get(fileName);
                            byte[] bytesFile = Files.readAllBytes(path);
                            BCMessageDigest SHA3 = new SHA3.Digest512();
                            byte[] ownGeneratedSHA = SHA3.digest(bytesFile);

                            byte[] json = jSONcreator.createHashResponse(Properties.RESPONSE_TYPE,
                                    "ACK-hash", Hex.toHexString(ownGeneratedSHA)).toString().getBytes();
                            byte[] buffer1 = new byte[4096];
                            buffer1 = fillArray(buffer1, json);
                            System.out.println(buffer1.length);
                            dos.write(buffer1);
                            byte[] ownGeneratedMacSend = hmac.hmac("key".getBytes(), buffer1, new SHA3.Digest512(), 64);
                            dos.write(ownGeneratedMacSend);

                            break;
                        }

                        default:
                            System.out.println("Server says :  I received json message, but I do not know it : \n" + clientMessage.toString());
                            break;
                    }
                } else if (clientSendingFile) {
                    System.out.println("Server says : HMAC OK " + Hex.toHexString(ownGeneratedMac));
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

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(new File("serverLog.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ChaffAgent responseSecure = new ChaffAgent(Properties.SERVER_SEND_PORT, Properties.CLIENT_RECEIVE_PORT);
        responseSecure.start();
//        ChaffAgent ca = new ChaffAgent(Properties.CLIENT_SEND_PORT, Properties.SERVER_RECEIVE_PORT);
//        ca.start();
        FileServer fs = new FileServer(Properties.SERVER_RECEIVE_PORT);
        fs.start();
    }

    private byte[] fillArray(byte[] buffer, byte[] toFill) {
        for (int i = 0; i < toFill.length; i++) {
            buffer[i] = toFill[i];
        }
        return buffer;

    }

    private void sendFile(DataOutputStream dos, String file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        while (fis.read(buffer) > 0) {
            byte[] mac = hmac.hmac("key".getBytes(), buffer, new SHA3.Digest512(), 64);
            dos.write(buffer);
            dos.write(mac);
            System.out.println("Server says : my mac = "
                    + Hex.toHexString(mac));
            System.out.println("Server says : bufor = "
                    + Hex.toHexString(buffer));

        }
        byte[] buffer1 = new byte[4096];
        byte[] json = jSONcreator.createGeneralMessage(Properties.FINISHED_SENDING, "OK").
                toString().getBytes();
        buffer1 = fillArray(buffer1, json);
        byte[] mac = hmac.hmac("key".getBytes(), buffer1, new SHA3.Digest512(), 64);
        dos.write(buffer1);
        dos.write(mac);
        System.out.println("Server says : sending ACK with MAC = "
                + Hex.toHexString(mac));
        System.out.println("Server says : I finished sending file");
        fis.close();
    }

    public static boolean verifyUser(String UserName, String Password) throws FileNotFoundException {
        //int NumberOfLinesInFile = LineNumberReader();

        boolean ifUsrPassCorrect = false;

        Scanner input = new Scanner(new File("Users.txt"));

        while (input.hasNext()) {
            String usr = input.next();
            String pass = input.next();
            if (usr.equals(UserName) && pass.equals(Password)) {
                ifUsrPassCorrect = true;
                break;
            } else {
                ifUsrPassCorrect = false;
            }
        }

        return ifUsrPassCorrect;
    }

    public List<String> checkUsersFile(String user) {
        List<String> results = new ArrayList<>();

        File[] files = new File(".").listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null. 

        for (File file : files) {
            if (file.isFile()) {
                if (file.getName().startsWith(user)) {
                    results.add(file.getName());
                }
            }
        }
        return results;
    }

    public String ReadUserKey(String UserName) throws FileNotFoundException {
        Scanner input = new Scanner(new File("klucze_server.txt"));

        while (input.hasNext()) {
            String usr = input.next();
            String pass = input.next();

            if (usr.equals(UserName)) {
                ClientPublicKey = pass;
                break;
            }
        }
        return ClientPublicKey;
    }
}
