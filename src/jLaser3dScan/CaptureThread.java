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

import java.io.ByteArrayInputStream;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

public class CaptureThread extends Thread {
	Image tmp;
	private static final int MAX_STEPS = 456;
	private int step = 0;
	private VideoCapture camera;
	private ImageView outputFrame;
	private boolean isScaning = false;
	private MainController controller;
	public CaptureThread( ImageView outputFrame, MainController controller ) {
		this.controller = controller;
		this.outputFrame = outputFrame;
	}
	public void startScan() {
		step = 0;
		isScaning = true;
	}
	public void stopScan() {
		isScaning = false;
	}
	@Override
	public void run() {
		int camId = 0;
		int numSteps = 4;
		SerialWriter writer = new SerialWriter(""); 
		camera = new VideoCapture(camId);
		while (!interrupted()) {
			tmp = grabFrame();
			if( tmp == null ) continue;
			//Platform.runLater(() -> {
			outputFrame.setImage(tmp);
			//});
			if( isScaning && writer.isRotateReady()){
				writer.rotate(numSteps);
				step += numSteps;
				System.out.println(step);
				if( step >= MAX_STEPS){
					isScaning= false;
					Platform.runLater(() -> controller.startScan());	
				}
			}
		}
		//frameBuffer.stop();
		writer.disconnect();
		if( camera != null  && camera.isOpened()) {
			camera.release();
		}
	}
	private Image mat2Image(Mat frame) {
		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".bmp", frame, buffer);
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}

	private Image grabFrame() {
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
	}
}
