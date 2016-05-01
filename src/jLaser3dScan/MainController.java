package jLaser3dScan;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import org.opencv.videoio.Videoio;
import org.opencv.videoio.VideoWriter;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.*;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {

	@FXML private BorderPane mainWin;
	@FXML private ImageView currentFrame;
    @FXML private Button startScanBtn;
    @FXML private TextField fHAngle;
    @FXML private TextField iCamID;
    @FXML private Label sFilename;
    @FXML private ChoiceBox<String> sPort;
    @FXML CheckBox bIsFile;

    private CaptureThread captureThread;
    private boolean isScaning = false;
    private ScanSettings settings; 
    private String videoFilename;
	
    public void initialize() {
		System.out.println("init MainController");
		settings = new ScanSettings();
		LoadSettings();
		startCapture();
    }
    private void startCapture(){
        captureThread = new CaptureThread(currentFrame, this, settings);
        captureThread.start();
    }
    @FXML protected void SelectVideoFile(){
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open Video File");
    	if( videoFilename.isEmpty()){
    		fileChooser.setInitialDirectory( new File(System.getProperty("user.dir"))); 
    	}else{
    		fileChooser.setInitialDirectory( new File(videoFilename).getParentFile()); 
    	}
    	File file = fileChooser.showOpenDialog((Stage) mainWin.getScene().getWindow());

        if (file != null ) {
        	setFilename(file.getAbsolutePath());
        } 
    }
    protected void setFilename(String filename){
    	Path p = Paths.get(filename);
    	String file = p.getFileName().toString();;
    	sFilename.setText(file);
    	videoFilename = filename;
    }
    @FXML protected void LoadSettings(){
    	settings.Load();
    	bIsFile.setSelected(settings.isFile);
    	fHAngle.setText(String.format("%.0f",settings.hAngle));
    	iCamID.setText(String.valueOf(settings.camID));
    	sPort.getItems().clear();
    	sPort.getItems().add("emul");
    	sPort.getSelectionModel().select(settings.port);
    	setFilename(settings.filename);
    }
    @FXML protected void SaveSettings(){
    	settings.isFile = bIsFile.isSelected();
    	settings.hAngle = Double.parseDouble(fHAngle.getText());
    	settings.camID = Integer.parseInt(iCamID.getText());
    	settings.port = sPort.getValue();
    	settings.filename = videoFilename;
    	settings.Save();
    	captureThread.interrupt();
    	try{
    		captureThread.join();	
    	}catch(InterruptedException e){}
    	startCapture();
    }
    public void stop() {
    	if( captureThread != null ){
    		captureThread.interrupt();
    	}
    }
    @FXML
    protected void startScan() {
		System.out.println("startScan");
    	if( captureThread == null ){
    		isScaning = false;
    		return;
    	}
    	if( isScaning ){
    		captureThread.stopScan();
            startScanBtn.setText("Start scan");
    		isScaning = false;
    	}else{
    		captureThread.startScan();
            startScanBtn.setText("Stop scan");
    		isScaning = true;
    	}
    }
}
