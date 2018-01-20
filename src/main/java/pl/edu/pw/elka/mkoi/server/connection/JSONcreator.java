/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server.connection;

import jdk.nashorn.internal.ir.debug.JSONWriter;
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
    public JSONObject createGeneralMessage(String responseType,String status){
        JSONObject item = new JSONObject();
        item.put(Properties.MESSAGE_TYPE, responseType);
        item.put(Properties.MESSAGE_BODY, status);
        item.put(Properties.FILE, "myNew");
        return item;
    }
    public JSONObject clientRequestFile(String request,String file,String login){
        JSONObject item = new JSONObject();
        item.put(Properties.MESSAGE_TYPE, request);
        item.put(Properties.FILE, file);
        item.put("login", login);
        return item;
    }

}
