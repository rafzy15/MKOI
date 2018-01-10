/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server.connection;

import org.json.JSONObject;

/**
 *
 * @author rafal
 */
public class JSONcreator {
    public JSONObject createLoginMessage(String login,String password){
        JSONObject item = new JSONObject();
        item.put("Messsage-type", "login");
        item.put("Login", login);
        item.put("Password", password);
        return item;
    }
    public JSONObject createReturnMessage(String status,String responseType){
        JSONObject item = new JSONObject();
        item.put("Messsage-type", responseType);
        item.put("Status", status);
        return item;
    }
    public JSONObject getFileMessage(String messageType,String requestedFile){
        JSONObject item = new JSONObject();
        item.put("Messsage-type", messageType);
        item.put("Requested-file", requestedFile);
        return item;
    }
}
