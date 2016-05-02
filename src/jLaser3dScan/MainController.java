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
import javafx.scene.control.Slider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

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
	@FXML private ImageView img1;
	@FXML private ImageView img2;
	@FXML private ImageView img3;
	@FXML private ImageView img4;
	@FXML private ImageView img5;
	@FXML private ImageView img6;
	@FXML private Slider hmin;
	@FXML private Slider hmax;
	@FXML private Slider umin;
	@FXML private Slider umax;
	@FXML private Slider vmin;
	@FXML private Slider vmax;
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
		hmin.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                    settings.hmin=new_val.intValue();
            } });
		hmax.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                    settings.hmax=new_val.intValue();
            } });
		umin.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                    settings.umin=new_val.intValue();
            } });
		umax.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                    settings.umax=new_val.intValue();
            } });
		vmin.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                    settings.vmin=new_val.intValue();
            } });
		vmax.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                    settings.vmax=new_val.intValue();
            } });
		startCapture();
    }
    private void startCapture(){
        captureThread = new CaptureThread( this, settings);
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
    	hmin.setValue(settings.hmin);
    	hmax.setValue(settings.hmax);
    	umin.setValue(settings.umin);
    	umax.setValue(settings.umax);
    	vmin.setValue(settings.vmin);
    	vmax.setValue(settings.vmax);
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
    public void setImage(int id, Image img){
    	if( id == 0 ) {currentFrame.setImage(img); }
    	if( id == 1 ) {img1.setImage(img); }
    	if( id == 2 ) {img2.setImage(img); }
    	if( id == 3 ) {img3.setImage(img); }
    	if( id == 4 ) {img4.setImage(img); }
    	if( id == 5 ) {img5.setImage(img); }
    	if( id == 6 ) {img6.setImage(img); }
    }
    @FXML
    protected void startScan() {
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
