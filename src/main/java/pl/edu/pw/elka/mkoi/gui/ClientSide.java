package pl.edu.pw.elka.mkoi.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ClientSide extends Application {

    private StackPane stackPane = null;

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/fxml/LoginPane.fxml"));
        stackPane = loader.load();
        
        
        Scene scene = new Scene(stackPane);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Panel logowania");
        primaryStage.show();

    }

    

}
