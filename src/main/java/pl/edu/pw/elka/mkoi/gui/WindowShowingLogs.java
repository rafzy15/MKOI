package pl.edu.pw.elka.mkoi.gui;

import pl.edu.pw.elka.mkoi.guicontrollers.LoggingPaaneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class WindowShowingLogs extends Application {

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/fxml/LoggingPane.fxml"));
        Pane stackPane = loader.load();

        Scene scene = new Scene(stackPane);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Panel logowania");
        primaryStage.show();

        //LoggingPaaneController.startReading("Users.txt");
    }

}
