package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ServerPaneController
{
	@FXML
    private Button ServerNoweKontoUzytkownika;

    @FXML
    void OnClickServerNoweKontoUzytkownika() 
    {
    	try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("/fxml/CreateNewUserWindow.fxml"));
			Pane pane = loader.load();
			
			Stage newUserstage = new Stage();
			
			Scene scene = new Scene(pane);
			newUserstage.setScene(scene);
		
			newUserstage.setTitle("Utworz nowe konto uzytkownika");
			newUserstage.show();	
			//newUserstage.setOnCloseRequest(e -> Platform.exit());
			//Stage stage = (Stage) ServerNoweKontoUzytkownika.getScene().getWindow();
	        //stage.close();
		
		}catch (Exception e) {
			System.out.println("Nie mozna otworzyc nowego okna");
		}
    }
    
    

	
	
	
}
