package TestFX1.TestFX1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LoginPaneController
{
	  @FXML
	    private Button ZalogujButton;
	
	  public LoginPaneController()
		{}

	@FXML
	public void onClickZalogujButton() 
	{
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
		
		}catch (Exception e) {
			System.out.println("Nie mozna otworzyc nowego okna");
		}
	}

}