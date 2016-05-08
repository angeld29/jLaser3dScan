package jLaser3dScan;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Scalar; 
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

public class ProcessImage {
	private MainController controller;
	private ScanSettings settings;
	private static final int NUM_MATS = 12;
	private Mat tmp1 = new Mat();
	private Mat tmp2 = new Mat();
	private Mat tmp3 = new Mat();
	private Mat tmp4 = new Mat();
	private Mat mat;
	private Mat hsv = new Mat();
	private Mat hsvm = new Mat();
	private Mat[] mats = new Mat[NUM_MATS];
	public ProcessImage ( MainController controller, ScanSettings settings) {
		this.controller = controller;
		this.settings = settings;
			for(int i=0;i<NUM_MATS;i++){
				mats[i]=new Mat();
			}
	}
	public Image mat2Image(Mat frame) {
		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".bmp", frame, buffer);
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
	private void FilterHSV(Mat mat){
		Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_BGR2HSV);
		Core.extractChannel(hsv, mats[0], 0);
		Core.extractChannel(hsv, mats[2], 1);
		Core.extractChannel(hsv, mats[4], 2);

		Core.inRange(mats[2], new Scalar(settings.umin), new Scalar(settings.umax), mats[3]);  
		Core.inRange(mats[4], new Scalar(settings.vmin), new Scalar(settings.vmax), mats[5]);  
		if(settings.hmin < settings.hmax ){
			Core.inRange(mats[0], new Scalar(settings.hmin), new Scalar(settings.hmax), mats[1]);  
		}else{
			Core.inRange(mats[0], new Scalar(0), new Scalar(settings.hmax), tmp1);  
			Core.inRange(mats[0], new Scalar(settings.hmin), new Scalar(255), tmp2);  
			Core.add(tmp1, tmp2, mats[1]);
		}
		/*List<Mat> ioutplanes = new ArrayList<Mat>(3);
		ioutplanes.add(mats[1]);
		ioutplanes.add(mats[3]);
		ioutplanes.add(mats[5]);

		Core.merge(ioutplanes, hsvf);*/
		Core.bitwise_and(mats[1], mats[3], tmp1);
		Core.bitwise_and(tmp1, mats[5], hsvm);
	}
	public void FindPoints(Mat in){
		for (int j = 0; j < mat.rows(); j++) {
			int firstOff = -1;
			int lastOff = -1;
			int maxVal = -1;
			int numdark = 0;

			for (int k = 0; k < mat.cols(); k++) {
				int val =(int) in.get(j, k)[0];
				in.put(j, k, 0 );
				if(val <= 1 ){
					numdark++;
					if( numdark > 5 || k == mat.cols()-1) {
						if(maxVal > 0 ) {
							//System.out.println(j+ " " + maxVal + " " + lastOff + " " + firstOff);
							int off = (firstOff + lastOff)/2;
							in.put(j, firstOff,  255);
							maxVal = -1;
							firstOff = -1;
							lastOff = -1;
							maxVal = -1;
						}
					}
				}else{
					numdark = 0;
					if( val > maxVal) {
						maxVal = val;
						firstOff = lastOff = k;
					}
					if( val == maxVal ){
						lastOff = k;
					}
				}
			}
			if( maxVal > 0 ){
				System.out.println(maxVal + " " + lastOff + " " + firstOff);
			}
		}
	}
	public void run (Mat mat){
		this.mat = mat;
		FilterHSV(mat);

		//Imgproc.erode(tmp2, tmp3, tmp4);
		//Imgproc.adaptiveThreshold(hsvm, tmp3, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 7, 5);
		//Imgproc.cvtColor(hsvm, tmp1, Imgproc.COLOR_GRAY2RGB);
		Core.bitwise_and(mats[4], hsvm, tmp2);
		Imgproc.threshold(tmp2, tmp3, 250, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
		tmp2.copyTo(mats[8]);
		hsvm.copyTo(mats[6]);
		tmp3.copyTo(mats[7]);
		tmp2.copyTo(mats[9]);
		FindPoints(mats[9]);
		//Imgproc.morphologyEx(mats[2], mats[5], Imgproc.MORPH_TOPHAT, kernel);
		//Imgproc.erode(mats[7], mats[6], tmp2);
		//Imgproc.erode(tmp2, mats[0], Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4,1)));
		//Imgproc.erode(tmp2, mats[2], Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4,1)), new Point(2,0), 4);
		//Imgproc.adaptiveThreshold(mats[4], mats[7], 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 7, 5);
		//Imgproc.cvtColor(mats[7], tmp2, Imgproc.COLOR_GRAY2RGB);
		
		//Core.bitwise_and(mats[4], tmp2, mats[0]);
		//Imgproc.adaptiveThreshold(mats[0], mats[2], 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 5, 5);
		//Imgproc.threshold(mats[0], mats[3], 150, 255, Imgproc.THRESH_TOZERO);
		
		//Imgproc.GaussianBlur(mats[5], mats[1], new Size(7,7), 1.5);
		//Imgproc.Canny(mats[1], mats[3], 0, 50);
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
