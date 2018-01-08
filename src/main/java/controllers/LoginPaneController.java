package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LoginPaneController
{
	@FXML
    private TextField LoginUserField;

    @FXML
    private Button ZalogujButton;

    @FXML
    private PasswordField LoginPasswordField;
	
	  public LoginPaneController()
		{}

	@FXML
	public void onClickZalogujButton() 
	{
		
		System.out.println(LoginUserField.getText());
		System.out.println(LoginPasswordField.getText());
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("/fxml/ClientPane.fxml"));
			StackPane stackPane = loader.load();
			
			Stage loginstage = new Stage();
			//StackPaneController controller = loader.getController();
			Scene scene = new Scene(stackPane);
			loginstage.setScene(scene);
		
			loginstage.setTitle("Menu klienta");
			loginstage.show();		
			
			Stage stage = (Stage) ZalogujButton.getScene().getWindow();
		    stage.close();	
		
		}catch (Exception e) {
			System.out.println("Nie mozna otworzyc nowego okna");
		}
	}

}