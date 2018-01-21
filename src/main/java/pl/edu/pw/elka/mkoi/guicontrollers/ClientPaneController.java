package pl.edu.pw.elka.mkoi.guicontrollers;

import java.awt.Desktop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.glass.events.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
    private TextFlow TextFlowClient;

    public ClientPaneController() {
    }

    @FXML
    public void OnClickPobierzPlikKlient() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Otworz plik");
        //fileChooser.showOpenDialog(PobierzPlikKlient.getParentPopup().getScene().getWindow());
        Stage stage = (Stage) PobierzPlikKlient.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            //Desktop.getDesktop().open(file);
            String filePath = file.getAbsolutePath();
        }

    }

    @FXML
    public void OnClickPobierzSkrot() {

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
                //Desktop.getDesktop().open(file);
                String filePath = file.getAbsolutePath();
                TcpClient tcpClient = TcpClient.getInstance();
                byte[] jsonBytes = tcpClient.createByteJson(filePath, LoginPaneController.loggedAs);
                int response = tcpClient.sendMessages(jsonBytes, Properties.ACTION_LOG_IN);
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
            int response = tcpClient.sendMessages(jsonBytes, Properties.ACTION_LOG_IN);
            if(response == 1){
                printText(tcpClient.getListFiles());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printText(String string) {
        Text text1 = new Text(string);
        Text newline = new Text("\n");
        TextFlowClient.getChildren().addAll(text1, newline);
    }

}
