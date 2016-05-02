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

import org.opencv.videoio.VideoWriter;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.ByteArrayInputStream;
import org.opencv.core.Core;
import org.opencv.core.Scalar; 
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

public class CaptureThread extends Thread {
	private Mat mat = new Mat();
	private static final int MAX_STEPS = 456;
	private int step = 0;
	private VideoCapture camera;
	private boolean isScaning = false;
	private MainController controller;
	private ScanSettings settings;
	private ProcessImage procimg;
	private double framen=0;
	private double maxframes=0;
	public CaptureThread( MainController controller, ScanSettings settings) {
		this.controller = controller;
		this.settings = settings;
		procimg = new ProcessImage(controller, settings);
	}
	public void startScan() {
		step = 0;
		framen =0;
		camera.set(Videoio.CAP_PROP_POS_FRAMES, framen);
		isScaning = true;
	}
	public void stopScan() {
		isScaning = false;
	}
	@Override
	public void run() {
		int numSteps = 4;
		SerialWriter writer;
		if( settings.isFile ){
			camera = new VideoCapture(settings.filename);
			maxframes =  camera.get(Videoio.CAP_PROP_FRAME_COUNT );	
			writer = new SerialWriter("emul");
		}else{
			camera = new VideoCapture(settings.camID);
			writer = new SerialWriter(settings.port);
		}
		while (!interrupted()) 
		{
			if( mat.empty()  || !settings.isFile) {
				if(!grabFrameMat()) continue;
			}
			if( isScaning && writer.isRotateReady()){
				if( settings.isFile ){
					if(!grabFrameMat()){
						isScaning= false;
						Platform.runLater(() -> controller.startScan());	
						continue;
					}
					//double pos = camera.get(Videoio.CAP_PROP_POS_FRAMES);	
					//System.out.println(framen + " " + pos + " " + maxframes);
					framen += 1;
				}
				writer.rotate(numSteps);
				step += numSteps;
			//	System.out.println(step);
				if( step >= MAX_STEPS ){
					isScaning= false;
					Platform.runLater(() -> controller.startScan());	
				}
			}
			procimg.run(mat);
		}
		//frameBuffer.stop();
		writer.disconnect();
		if( camera != null  && camera.isOpened()) {
			camera.release();
		}
	}

	private boolean grabFrameMat() {

		// check if the capture is open
		try {
			if( camera == null  || !camera.isOpened()) {
				return false;
			}
			if( camera.read(mat) && !mat.empty()){
				//tmp = mat2Image(mat);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// log the error
			System.err.println("ERROR: " + e.getMessage());
		}
		return false;
	}
	/*private Image grabFrame() {
		//init
		Image imageToShow = null;
		Mat frame;

		// check if the capture is open
		try {
			if( camera == null  || !camera.isOpened()) {
				return null;
			}
			Mat tmpFrame = new Mat();
			if (camera.read(tmpFrame) && !tmpFrame.empty()) {
				imageToShow = mat2Image(tmpFrame);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// log the error
			System.err.println("ERROR: " + e.getMessage());
		}
		return imageToShow;
	}*/
}
