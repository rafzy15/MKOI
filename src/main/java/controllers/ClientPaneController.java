package controllers;

import java.awt.Desktop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.glass.events.MouseEvent;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ClientPaneController {
	
	  @FXML
	    private Button PrzekazPlikKlient;

	    @FXML
	    private Button PobierzPlikKlient;

	    @FXML
	    private Button SprawdzSerwerKlient;

	    @FXML
	    private Button PobierzSkrot;
	    
	    @FXML
	    private ScrollPane ScrollPaneClient;

	    @FXML
	    private TextFlow TextFlowClient;
	    
	    
	    public ClientPaneController()
		{}

	    @FXML
	   public void OnClickPobierzPlikKlient() throws IOException 
	    {
	    	FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Otworz plik"); 
	        //fileChooser.showOpenDialog(PobierzPlikKlient.getParentPopup().getScene().getWindow());
	        Stage stage = (Stage) PobierzPlikKlient.getScene().getWindow();
	    File file = fileChooser.showOpenDialog(stage);
	    if (file != null) 
	    {
	    	//Desktop.getDesktop().open(file);
	    	String filePath = file.getAbsolutePath(); 
	    }
	    
        
	    
	    }

	    @FXML
	   public void OnClickPobierzSkrot() {

	    }

	    @FXML
	   public void OnClickPrzekazPlikKlient() 
	    {
	    	FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Otworz plik"); 
	        //fileChooser.showOpenDialog(PobierzPlikKlient.getParentPopup().getScene().getWindow());
	        Stage stage = (Stage) PobierzPlikKlient.getScene().getWindow();
	    File file = fileChooser.showOpenDialog(stage);
	    if (file != null) 
	    {
	    	//Desktop.getDesktop().open(file);
	    	String filePath = file.getAbsolutePath(); 
	    }
	    
	    }

	    @FXML
	    void OnClickSprawdzSerwerKlient() 
	    {
	    	List<String> results = new ArrayList<String>();

	    	File[] files = new File("C:\\Users\\Kamil\\Documents\\Gitara Blanka\\Kolędy").listFiles();
	    	//If this pathname does not denote a directory, then listFiles() returns null. 

	    	for (File file : files) {
	    	    if (file.isFile()) {
	    	        results.add(file.getName());
	    	        printText(file.getName());
	    	    }
	    	}
	    }
	    
	    public  void printText(String string)
	    {
	    	Text text1 = new Text(string);
	         Text newline = new Text("\n");            
	         TextFlowClient.getChildren().addAll(text1, newline);        
	    }
	    
	    

}
