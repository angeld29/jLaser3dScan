package jLaser3dScan;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

import org.opencv.core.Core;

public class Main extends Application {
	private MainController controller;
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
			BorderPane root = loader.load();
			controller = loader.getController();
			//BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle( "jLaser3dScan" );
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}
	@Override
	public void stop(){
	    System.out.println("Stage is closing");
	    // Save file
	    controller.stop();
	}
}
