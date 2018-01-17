<<<<<<< HEAD
package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.*;
import controllers.ServerPaneController;
public class CreateNewUserController
{
	  @FXML
	    private TextField NewUserPodajNazwe;

	    @FXML
	    private PasswordField NewUserPodajHaslo;

	    @FXML
	    private Button ButtonCreateNewUser;

	    @FXML
	    void OnClickCreateUser() throws IOException
	    {
	    	
	    	
	    	String username = NewUserPodajNazwe.getText();
	    	String password = NewUserPodajHaslo.getText();	    	
	    		    	
	    	WriteNewUserToFile(username, password);    	
	    		    	
	    	Stage stage = (Stage) ButtonCreateNewUser.getScene().getWindow();
	    	stage.close();
	    	
	    	
	    }
	    
	    public static void WriteNewUserToFile(String user, String password) throws IOException
	    {
	    	File users = new File("Users.txt");
	    	try(  PrintWriter out = new PrintWriter(new FileWriter(users, true))  )
	    	{
	    	    out.println(user + " " + password );	    	    
	    	}	    	
	    }
	    
	    
	    
}

=======
package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.*;
import controllers.ServerPaneController;
public class CreateNewUserController
{
	  @FXML
	    private TextField NewUserPodajNazwe;

	    @FXML
	    private PasswordField NewUserPodajHaslo;

	    @FXML
	    private Button ButtonCreateNewUser;

	    @FXML
	    void OnClickCreateUser() throws IOException
	    {
	    	
	    	
	    	String username = NewUserPodajNazwe.getText();
	    	String password = NewUserPodajHaslo.getText();	    	
	    		    	
	    	WriteNewUserToFile(username, password);    	
	    		    	
	    	Stage stage = (Stage) ButtonCreateNewUser.getScene().getWindow();
	    	stage.close();
	    	
	    	
	    }
	    
	    public static void WriteNewUserToFile(String user, String password) throws IOException
	    {
	    	File users = new File("Users.txt");
	    	try(  PrintWriter out = new PrintWriter(new FileWriter(users, true))  )
	    	{
	    	    out.println(user + " " + password );	    	    
	    	}	    	
	    }
	    
	    
	    
}

>>>>>>> f567f7b67b7c53b15920ecfe411d415cfb086c04
