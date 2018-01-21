/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server;

import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.json.JSONObject;

/**
 *
 * @author rafal
 */
public class JSONcreator {
    private JSONcreator(){
        
    }
    public JSONObject createLoginMessage(String login,String password){
        JSONObject item = new JSONObject();
        item.put(Properties.MESSAGE_TYPE, Properties.CLIENT_LOG_IN_REQUEST);
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
    public JSONObject createListResponse(String responseType,String status,String files){
        JSONObject item = new JSONObject();
        item.put(Properties.MESSAGE_TYPE, responseType);
        item.put(Properties.MESSAGE_BODY, status);
        item.put(Properties.FILES_LIST, files);
        return item;
    }
    public JSONObject clientFileJson(String request,String file,String login){
        JSONObject item = new JSONObject();
        item.put(Properties.MESSAGE_TYPE, request);
        item.put(Properties.FILE, file);
        item.put(Properties.LOGGED_AS, login);
        return item;
    }
    public JSONObject clientListJson(String request,String login){
        JSONObject item = new JSONObject();
        item.put(Properties.MESSAGE_TYPE, request);
        item.put(Properties.LOGGED_AS, login);
        return item;
    }
    private static class SingletonHolder {
        private static final JSONcreator INSTANCE = new JSONcreator();
    }
    public static JSONcreator getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
