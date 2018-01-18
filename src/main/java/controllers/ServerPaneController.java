package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import kurs.java.fx.LogReader;

public class ServerPaneController
{
	//private static final TextFlow TextArea1 = null;

	
	
	@FXML
    private Button ServerNoweKontoUzytkownika;
	
	  	
	  @FXML
	    private TextArea textArea;
	  @FXML
	    private  TextFlow TextFlowField;
	  @FXML
	    private ScrollPane ScrollPaneField;
	 // @FXML
	    //private Button WyswietlLogi;
	  
	  

    @FXML
    void OnClickServerNoweKontoUzytkownika() 
    {
    	    	
    	try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("/fxml/CreateNewUserWindow.fxml"));
			Pane pane = loader.load();
			
			Stage newUserstage = new Stage();
			
			Scene scene = new Scene(pane);
			newUserstage.setScene(scene);
		
			newUserstage.setTitle("Utworz nowe konto uzytkownika");
			newUserstage.show();				
			printText("Otwarty zostal panel administratora");
			//readLogs();
			//editFile();
			//newUserstage.setOnCloseRequest(e -> Platform.exit());
			//Stage stage = (Stage) ServerNoweKontoUzytkownika.getScene().getWindow();
	        //stage.close();
		
    	}catch (Exception e) {
			System.out.println("Nie mozna otworzyc nowego okna");
			
			
		}
    }
    
   /* @FXML
    void WyswietlLogiButton() 
    {
    	try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("/fxml/LoggingPane.fxml"));
			Pane pane = loader.load();
			
			Stage newLoggingStage = new Stage();
			
			Scene scene = new Scene(pane);
			newLoggingStage.setScene(scene);
		
			newLoggingStage.setTitle("Logi z aplikacji");
			newLoggingStage.show();		
    	}catch (Exception e) {
			System.out.println("Nie mozna otworzyc nowego okna");
			
			
		}
    }*/
    
    public  void printText(String string)
    {
    	Text text1 = new Text(string);
         Text newline = new Text("\n");            
        TextFlowField.getChildren().addAll(text1, newline);        
    }
    
    
    
    /*PrintStream outStream = new PrintStream( new TextAreaOutputStream(TextArea1));

    System.setOut( outStream );
    System.setErr( outStream );
    
    
    
    public class TextAreaOutputStream extends OutputStream {        
        private javafx.scene.control.TextArea TextArea1;
        
        public TextAreaOutputStream( TextFlow TextFlowField ) {
            this.TextArea1 = textArea;
        }

        public void write( int b ) throws IOException {
        	TextArea1.appendText( String.valueOf( ( char )b ) );        	
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
        	TextArea1.appendText(new String(cbuf, off, len));
        	        	
        }
    }*/
    
   
    public void readLogs()
    {
    	try {
			File file = new File("Users.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while (true) {
				line = bufferedReader.readLine();
				stringBuffer.append(line);
				stringBuffer.append("\n");
				System.out.println(line);
			}
			//fileReader.close();
			//System.out.println("Contents of file:");
			//System.out.println(stringBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void editFile() throws IOException, InterruptedException
    {
        File file = new File("Users.txt");
        if (file.exists())
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            try
            {
                String line;
               // while ((line = bufferedReader.readLine()) != null)
                	while (true)
                {
                	line = bufferedReader.readLine();
                    if (line == null)
                    {
                        //line = null;
                        //System.out.println("a");
                    	Thread.sleep(1000);
                    }
                    else
                    System.out.println(line);
                }
            } finally {
                bufferedReader.close();
            }
        }
    }
    
    
	
	
	
}
