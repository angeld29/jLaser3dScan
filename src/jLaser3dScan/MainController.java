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

import org.opencv.videoio.Videoio;
import org.opencv.videoio.VideoWriter;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;


public class MainController {

    @FXML
    private ImageView currentFrame;
    private Thread captureThread;
    private VideoCapture camera;
	
    public void initialize() {
		System.out.println("init MainController");
		
		int camId = 0;
    	camera = new VideoCapture(camId);
        captureThread = new CaptureThread();
        captureThread.start();
    }
    public void stop() {
    	if( camera != null  && camera.isOpened()) {
            camera.release();
    	}
    	if( captureThread != null ){
    		captureThread.interrupt();
    	}
    }
    private class CaptureThread extends Thread {
    	Image tmp;
    	public CaptureThread() {
    	}
        @Override
        public void run() {
            while (!interrupted()) {
                tmp = grabFrame();
                if( tmp != null ){
                	Platform.runLater(() -> {
                			currentFrame.setImage(tmp);
                	});
                }
            }
           //frameBuffer.stop();
            //serialWriter.disconnect();
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

}