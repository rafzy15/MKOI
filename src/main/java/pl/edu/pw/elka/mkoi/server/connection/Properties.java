/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server.connection;

/**
 *
 * @author rafal
 */
public class Properties {

    public final static int CLIENT_SEND_PORT = 1988;
    public final static int SERVER_RECEIVE_PORT = 1989;
    public final static int SERVER_SEND_PORT = 1990;
    public final static int CLIENT_RECEIVE_PORT = 1991;
    public final static int ADDITIONAL_CHAF_PER_BUFFER = 1;
    
    public final static String CLIENT_REQUEST_FILE = "Request-file";
    public final static String CLIENT_SEND_FILE = "Send-file";
    public final static String MESSAGE_TYPE = "Message-type";
    public final static String RESPONSE_TYPE = "Response-type";
    public final static String MESSAGE_BODY = "Message-Body";
    public final static String FINISHED_SENDING = "Finished-sending";
    
    public final static String ACK = "ACK";
}
