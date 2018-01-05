package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class StackPaneController 
{
	@FXML
	private Button button; //wstrzykujemy (nie musimy inicjalizowac przysisku w kontroerze)

	public StackPaneController()
	{
		System.out.println("Tekst do wyswietlenia");
	}
	
	@FXML
	void initialize() //metoda ktora gwarantuje zainicjalizowanie wszystkich elemntow fxml
	{
		button.setText("Nazwa");
	}
	
	@FXML
	public void onActionButton() 
	{
		//System.out.println("To jest metoda onActionButton");
		
	}
	
	@FXML
	public void OnMouseClick()
	{
		System.out.println("Kliknięcie przycisku");
		
	}
	
	
	
}
