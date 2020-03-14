package main;

import java.io.IOException;

import BrickBreaker.BrickBreaker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		Parent root = null;
		
		try {
			root = FXMLLoader.load(getClass().getResource("/brickbreaker.fxml"));
			
		}
		catch (IOException e) {
			System.out.println("Cannot load FXML file!");
			System.exit(1);
		}
		Scene scene = new Scene(root, 640, 600);
		BrickBreaker game = new BrickBreaker(primaryStage, scene);
		primaryStage.setTitle(game.getTitle());
		primaryStage.setResizable(false);
		
		primaryStage.show();
		game.run();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	 @Override
	 public void stop() throws Exception {
       Platform.exit();
     }
	 
	 
}
