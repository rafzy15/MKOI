package pl.edu.pw.elka.mkoi.guicontrollers;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import pl.edu.pw.elka.mkoi.gui.LogReader;

public class LoggingPaaneController {

    @FXML
    private Button UruchomLoggingButton;

    @FXML
    private Label LbaelLogging;

    @FXML
    private ScrollPane ScrollPaneLogging;

    @FXML
    private TextFlow TextFlowLogging;
    @FXML
    private TextArea TextAreaLogging;

    @FXML
    void OnClickUruchomLoggingButton() throws InterruptedException, IOException {
        startReading("Users.txt");
        //printText("ala ma kota");
    }

    public void printText(String string) {
        Text text1 = new Text(string);
        Text newline = new Text("\n");
        TextFlowLogging.getChildren().addAll(text1, newline);
    }

    private boolean canBreak = false;

    public void startReading(String filename) throws InterruptedException, IOException {
        canBreak = false;
        String line;
        try {
            LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
            line = lnr.readLine();
            while (!canBreak) {
                line = lnr.readLine();
                if (line == null) {
                    //System.out.println("czekam 3 sekundy");
                    Thread.sleep(3000);
                    continue;
                } else {
                    processLine(line);
                }
            }
            lnr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void stopReading() {
        canBreak = true;
    }

    private void processLine(String s) throws IOException {
        //processing line
        //System.out.println(s);
        Text text1 = new Text(s);
        Text newline = new Text("\n");
        //TextFlowLogging.getChildren().addAll(text1, newline);   
        TextFlowLogging.getChildren().addAll(text1);
        //TextAreaLogging.appendText(s);
        //SaveLogsToFile(s);
    }

    public void SaveLogsToFile(String text) throws IOException {
        File users = new File("ZapisaneLogi.txt");
        try (PrintWriter out = new PrintWriter(new FileWriter(users, true))) {
            out.println(text);
        }
    }

}
