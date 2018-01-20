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
import java.util.Scanner;

public class LoginPaneController {

    private static final Exception Exception = null;

    @FXML
    private TextField LoginUserField;

    @FXML
    private Button ZalogujButton;

    @FXML
    private PasswordField LoginPasswordField;

    public LoginPaneController() {
    }

    @FXML
    public void onClickZalogujButton() throws IOException {
        if (verifyUser(LoginUserField.getText(), LoginPasswordField.getText()) == false) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/fxml/AuthorizationFailedWindow.fxml"));
            StackPane stackPane1 = loader.load();
            Stage authfail = new Stage();
            Scene scene = new Scene(stackPane1);
            authfail.setScene(scene);

            authfail.setTitle("Blad uwierzytelnienia");
            authfail.show();

        } else {
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

    public static boolean verifyUser(String UserName, String Password) throws FileNotFoundException {
        //int NumberOfLinesInFile = LineNumberReader();

        boolean ifUsrPassCorrect = false;

        Scanner input = new Scanner(new File("Users.txt"));

        while (input.hasNext()) {
            String usr = input.next();
            String pass = input.next();

            //System.out.println("Funkcja VerifyUsr");
            //System.out.println(usr);
            //System.out.println(UserName);
            //System.out.println(pass);
            //System.out.println(Password);
            //System.out.println(" ");
            if (usr.equals(UserName) && pass.equals(Password)) {
                ifUsrPassCorrect = true;
                //System.out.println(" true!!!!!!!!!!!!!!!!!!!!!!!!!!");
                break;
            } else {
                ifUsrPassCorrect = false;
            }
        }

        return ifUsrPassCorrect;

    }

    public static int LineNumberReader() {
        int linenumber = 0;

        try {

            File file = new File("Users.txt");

            if (file.exists()) {

                FileReader fr = new FileReader(file);
                LineNumberReader lnr = new LineNumberReader(fr);

                while (lnr.readLine() != null) {
                    linenumber++;
                }

                System.out.println("Total number of lines : " + linenumber);

                lnr.close();

            } else {
                System.out.println("File does not exists!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return linenumber;

    }

}
