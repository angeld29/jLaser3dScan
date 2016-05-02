package jLaser3dScan;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;

import org.opencv.core.Core;
import org.opencv.core.Scalar; 
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

public class ProcessImage {
	private MainController controller;
	private ScanSettings settings;
	private  static final int NUM_MATS = 6;
	private Mat mat;
	private Mat[] mats = new Mat[NUM_MATS];
	public ProcessImage ( MainController controller, ScanSettings settings) {
		this.controller = controller;
		this.settings = settings;
			for(int i=0;i<6;i++){
				mats[i]=new Mat();
			}
	}
	public Image mat2Image(Mat frame) {
		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".bmp", frame, buffer);
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
	public void run (Mat mat){
		this.mat = mat;
		Imgproc.cvtColor(mat, mats[0], Imgproc.COLOR_RGB2HSV);
		//controller.setImage(1, mat2Image(mats[0]));
		//Imgproc.cvt
		Core.extractChannel(mat, mats[2], 0);
		Core.extractChannel(mat, mats[3], 1);
		Core.extractChannel(mat, mats[4], 2);
		Core.inRange(mats[0], new Scalar(settings.hmin, settings.umin,settings.vmin,0),  
				new Scalar(settings.hmax, settings.umax, settings.vmax,255), mats[1]);  
		//			Imgproc.adaptiveThreshold(mat2, mat3, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 11, 2);
		//Imgproc.threshold(mat2, mat3, 0,255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
		//Imgproc.Laplacian(mat2,mat3, 128);
		DrawMats();
	}
	private void DrawMats() {
		if(!mat.empty()) controller.setImage(0, mat2Image(mat));
		for(int i=0;i<NUM_MATS;i++){
			if(mats[i].empty()) continue;
			controller.setImage(i+1, mat2Image(mats[i]));
		}
	}
}
