package pl.edu.pw.elka.mkoi.guicontrollers;

import java.awt.Desktop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.glass.events.MouseEvent;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.edu.pw.elka.mkoi.server.ChaffAgent;
import pl.edu.pw.elka.mkoi.server.JSONcreator;
import pl.edu.pw.elka.mkoi.server.Properties;
import pl.edu.pw.elka.mkoi.server.TcpClient;

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
    private ListView<String> ListFilePane;

    public ClientPaneController() {
    }

    @FXML
    public void OnClickPobierzPlikKlient() throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Wybierz ścieżkę");
        Stage stage = (Stage) PobierzPlikKlient.getScene().getWindow();
        File file = directoryChooser.showDialog(stage);
        if (file != null) {
            String filePath = file.getAbsolutePath();
            String fileName = ListFilePane.getSelectionModel().getSelectedItem().trim();

            fileName = fileName.replace(LoginPaneController.loggedAs, "");
            System.out.println(filePath + fileName);
            TcpClient tcpClient = TcpClient.getInstance();
            byte[] jsonBytes = tcpClient.createGetByteJson(filePath, fileName, LoginPaneController.loggedAs);
            int response = tcpClient.sendMessages(jsonBytes);

        }

    }

    @FXML
    public void OnClickPobierzSkrot() throws Exception {
        TcpClient tcpClient = TcpClient.getInstance();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Otworz plik");
        Stage stage = (Stage) PobierzPlikKlient.getScene().getWindow();
        File localFile = fileChooser.showOpenDialog(stage);
        if (localFile != null) {
            String fileName = ListFilePane.getSelectionModel().getSelectedItem().trim();
            if (fileName != null || !fileName.isEmpty()) {
                fileName = fileName.replace(LoginPaneController.loggedAs, "");
                byte[] jsonBytes = tcpClient.createHashMessage(fileName, LoginPaneController.loggedAs);
                int response = tcpClient.sendMessages(jsonBytes);
                if (response == 1) {
                    String hash = tcpClient.getHash();
                    String localFileHash = tcpClient.computeHash(localFile);
                    String text = "";
                    if (localFileHash.equals(hash)) {
                        text = "Pliki są takie same \n";
                    } else {
                        text = "Pliki są różne \n";
                    }
                    text += "hash na serwerze " + hash +"\n";
                    text += "hash pliku lokalnego " + localFileHash+"\n";
                    createHashWindow(text);
                }
            }
        }
    }

    @FXML
    public void OnClickPrzekazPlikKlient() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Otworz plik");
            //fileChooser.showOpenDialog(PobierzPlikKlient.getParentPopup().getScene().getWindow());
            Stage stage = (Stage) PobierzPlikKlient.getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                String filePath = file.getAbsolutePath();
                TcpClient tcpClient = TcpClient.getInstance();
                byte[] jsonBytes = tcpClient.createSendByteJson(filePath, LoginPaneController.loggedAs);
                int response = tcpClient.sendMessages(jsonBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createHashWindow(String hashInfo) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("path/to/other/view.fxml"), resources);
        Stage stage = new Stage();
        Text text = new Text();
        text.setText(hashInfo);
        text.setX(50);
        text.setY(50);

        Group group = new Group(text);

        //Creating a scene object 
        Scene scene = new Scene(group, 600, 300);

        //Setting title to the Stage 
        stage.setTitle("Sample Application");

        //Adding scene to the stage 
        stage.setScene(scene);

        //Displaying the contents of the stage 
        stage.show();
    }

    @FXML
    void OnClickSprawdzSerwerKlient() {
        try {
            ListFilePane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

                public void changed(
                        ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    if (newValue != null && !newValue.isEmpty()) {
                        PobierzPlikKlient.setDisable(false);
                        PobierzSkrot.setDisable(false);
                    }
                }
            });
            TcpClient tcpClient = TcpClient.getInstance();
            byte[] jsonBytes = tcpClient.createByteJsonList(LoginPaneController.loggedAs);
            int response = tcpClient.sendMessages(jsonBytes);
            if (response == 1) {
                String list = tcpClient.getListFiles().substring(1,
                        tcpClient.getListFiles().length() - 1);
                List<String> items = Arrays.asList(list.split("\\s*,\\s*"));
                addToList(items);
//                printText(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToList(List<String> items) {
        final ObservableList fileList
                = ListFilePane.getItems();
        fileList.clear();
        fileList.setAll(items);
    }

}
