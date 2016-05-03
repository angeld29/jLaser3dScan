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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;


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
	//запись видео
    private VideoWriter outputVideo;
    private Integer frameW = 640;
    private Integer frameH = 480;
    Mat resframe = new Mat();

	public CaptureThread( MainController controller, ScanSettings settings) {
		this.controller = controller;
		this.settings = settings;
        outputVideo = new VideoWriter();
		procimg = new ProcessImage(controller, settings);
	}
	public void startScan() {
		step = 0;
		framen =0;
		camera.set(Videoio.CAP_PROP_POS_FRAMES, framen);
		isScaning = true;

        Date date = new Date(); 
        String filename = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS").format(date) ;
        System.out.println(filename+ " " );
        File theDir = new File("video");
        if (!theDir.exists()) {
            try{
                theDir.mkdirs();
            } 
            catch(SecurityException se){
                //handle it
            }        
        }
        try{
//        	outputVideo.open(filename, outputVideo.fourcc('M','P','4','V'), 20, new Size(frameW, frameH));
        	//outputVideo.open("video/"+filename+".avi", outputVideo.fourcc('H','2','6','4'), 25, new Size(frameW, frameH));
      	//outputVideo.open(filename, outputVideo.fourcc('A','V','C','1'), 20, new Size(frameW, frameH));
      	outputVideo.open("video/"+filename+".avi", outputVideo.fourcc('X','V','I','D'), 25, new Size(frameW, frameH), true);
      	//outputVideo.open("video/"+filename+".avi", outputVideo.fourcc('M','S','V','C'), 25, new Size(frameW, frameH), true);
        //outputVideo.open(filename+".avi", -1, 30, new Size(frameW, frameH));
        } catch (Exception e) {
            e.printStackTrace();
            // log the error
            System.err.println("ERROR: " + e.getMessage());
        }
        if(!outputVideo.isOpened()){
            System.err.println("ERROR openvideo: " + filename);
        }
        

	}
	public void stopScan() {
		isScaning = false;
        if( outputVideo.isOpened() ){
        	outputVideo.release();
        	System.out.println("outputVideo.release");
        }
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
	    frameW = (int) camera.get(Videoio.CAP_PROP_FRAME_WIDTH);
	    frameH = (int) camera.get(Videoio.CAP_PROP_FRAME_HEIGHT);

		while (!interrupted()) 
		{
			if( mat.empty()  || !settings.isFile) {//Для видео читаем только первый кадр для отображения
				if(!grabFrameMat()) continue;
			}
			if( isScaning && writer.isRotateReady()){
				if( settings.isFile ){
					if(!grabFrameMat()){//при сканировании читаем кадры из видео 
						isScaning= false;
						Platform.runLater(() -> controller.startScan());	
						continue;
					}
					//double pos = camera.get(Videoio.CAP_PROP_POS_FRAMES);	
					//System.out.println(framen + " " + pos + " " + maxframes);
					framen += 1;
				}
            	if(outputVideo.isOpened()){
            		Imgproc.resize(mat, resframe, new Size(frameW, frameH));
            		outputVideo.write(resframe);
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
