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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
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
            byte[] jsonBytes = tcpClient.createGetByteJson(filePath,fileName, LoginPaneController.loggedAs);
            int response = tcpClient.sendMessages(jsonBytes);
        }

    }

    @FXML
    public void OnClickPobierzSkrot() throws Exception{
        TcpClient tcpClient = TcpClient.getInstance();
        
        String fileName = ListFilePane.getSelectionModel().getSelectedItem().trim();
        if(fileName != null || !fileName.isEmpty()){
            fileName = fileName.replace(LoginPaneController.loggedAs, "");
            byte[] jsonBytes = tcpClient.createHashMessage(fileName ,LoginPaneController.loggedAs);
            int response = tcpClient.sendMessages(jsonBytes);
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

    @FXML
    void OnClickSprawdzSerwerKlient() {
        try {
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
