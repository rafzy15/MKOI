package pl.edu.pw.elka.mkoi.guicontrollers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.fxml.Initializable;
import pl.edu.pw.elka.mkoi.server.ChaffAgent;
import pl.edu.pw.elka.mkoi.server.JSONcreator;
import pl.edu.pw.elka.mkoi.server.Properties;
import pl.edu.pw.elka.mkoi.server.TcpClient;

public class LoginPaneController implements Initializable {
    static String loggedAs ="";
    private static final Exception Exception = null;
    JSONcreator jSONcreator = JSONcreator.getInstance();
    TcpClient tcpClient = null;
    @FXML
    private TextField LoginUserField;

    @FXML
    private Button ZalogujButton;

    @FXML
    private PasswordField LoginPasswordField;
    

    public LoginPaneController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ChaffAgent ca = new ChaffAgent(Properties.CLIENT_SEND_PORT, Properties.SERVER_RECEIVE_PORT);
        ca.start();
        tcpClient = TcpClient.getInstance();
    }

    @FXML
    public void onClickZalogujButton() throws Exception {
        byte[] jsonBytes = jSONcreator.createLoginMessage(LoginUserField.getText(), LoginPasswordField.getText()).
                toString().getBytes();
        int response = tcpClient.sendMessages(jsonBytes, Properties.ACTION_LOG_IN);
        if (response != 1) {
            showFailureWindow();
        } else {
            loggedAs = LoginUserField.getText();
            showMenuClient();
        }
    }

    private void showFailureWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/fxml/AuthorizationFailedWindow.fxml"));
        StackPane stackPane1 = loader.load();
        Stage authfail = new Stage();
        Scene scene = new Scene(stackPane1);
        authfail.setScene(scene);

        authfail.setTitle("Blad uwierzytelnienia");
        authfail.show();
    }

    private void showMenuClient() {
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

        } catch (Exception e) {
            System.out.println("Nie mozna otworzyc nowego okna");
        }
    }
    

}
