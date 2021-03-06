package pl.edu.pw.elka.mkoi.gui;

import pl.edu.pw.elka.mkoi.guicontrollers.ServerPaneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MenuServera extends Application {

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage serverStage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/fxml/ServerPane.fxml"));
        AnchorPane stackPane = loader.load();

        //LoginPaneController controller = loader.getController();
        Scene scene = new Scene(stackPane);
        serverStage.setScene(scene);

        serverStage.setTitle("Panel zarzadzania serwerem");
        serverStage.show();
    }

}
