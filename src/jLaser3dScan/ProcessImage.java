package jLaser3dScan;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
	private static final double MIN_LINE_LEN = 2;
	private static final double MIN_LINE_COS_ANGLE = 0.996;//1 градус
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
	/***********************************************************************
	 * 0 2 4 -  H S V  1 3 5 ranged hsv - HSV, hsvm - ranged HSV
	 ***********************************************************************/
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
		
		Core.bitwise_and(tmp1, mats[5], tmp3);
		tmp3.copyTo(hsvm);
		for( int i =0; i< 230; i++){
			Imgproc.line(hsvm, new Point(i,0), new Point(i, hsvm.rows()), new Scalar(0),1);
		
		}
		//Imgproc.line(hsvm, new Point(0,0), new Point(hsvm.cols(), hsvm.rows()), new Scalar(255,0,0), 3);
		//230
	}
	private ArrayList<int[]> FindPoints(Mat in){

		ArrayList<int[]> points = new ArrayList <int[]>();

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
							points.add(new int[]{ j, k });
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
		return points;
	}
	private boolean checkLines(int[] line1, int[] line2){
			 int maxdiff = 20;
			 double[] v1 = { line1[2] -line1[0], line1[3]-line1[1]};
			 double[] v2 = { line2[2] -line2[0], line2[3]-line2[1]};
			 double v1len =Math.sqrt(v1[0]*v1[0]+v1[1]*v1[1]);
			 double v2len =Math.sqrt(v2[0]*v2[0]+v2[1]*v2[1]);
			 //проверим не слишком ли короткие 
			 if(v1len < MIN_LINE_LEN || v2len < MIN_LINE_LEN  ) return true;
			 double cos_a = (v1[0]*v2[0] +v1[1]*v2[1])/v1len/v2len;
			 if( cos_a  < MIN_LINE_COS_ANGLE) return false;// не коллинеальны
			 //проекция line2 на line1
			 //прямая через 1 точку line2
			 //v1[0] *(x - line2[0]) + v1[1]*(y - line2[1]) = 0
			 //v1[0]*x +v1[1]*y = v1[0] * line2[0] + v1[1] * line2[1]
			 //прямая через 2 точку line2
			 //v1[0] *(x - line2[2]) + v1[1]*(y - line2[3]) = 0
			 //v1[0]*x +v1[1]*y = v1[0] * line2[2] + v1[1] * line2[1]
			 
			 //ур-е прямой 1
			 // (x-line1[0])*(line1[3] - line1[1]) - (y - line1[2])*(line1[2] - line1[0]) = 0
			 // (line1[3] - line1[1])*x +(line1[0]-line1[2])*y = line1[0]*((line1[3] - line1[1])) +line1[2]*(line1[0]-line1[2])
			 
			 
				 if(Math.abs(line1[0]-line2[0]) < maxdiff  && 
						 Math.abs(line1[1]-line2[1]) < maxdiff  && 
						 Math.abs(line1[2]-line2[2]) < maxdiff  && 
						 Math.abs(line1[3]-line2[3]) < maxdiff  ){
					 return true;
				 }
		
		return false;
	}
	private ArrayList<int[]> getLines(Mat in){
		ArrayList<int[]> vec = new ArrayList <int[]>();
		Mat lines = new Mat();
		Imgproc.HoughLinesP(tmp3, lines, 1,3.14/180, 50,100,10);
		for (int x = 0; x < lines.rows(); x++)
	    {
			double[] vecd = lines.get(x,0);
			int [] veci = { (int)vecd[0], (int)vecd[1],(int)vecd[2],(int)vecd[3]};
	          
			boolean hasLine = false;
			 Iterator itr = vec.iterator();
			   
			    //use hasNext() and next() methods of Iterator to iterate through the elements
			 while(itr.hasNext()){
				 int[] itm = (int[]) itr.next();
				 if(checkLines(itm, veci)){
					 hasLine = true;
					 break;
				 }
			 }
			 if(!hasLine) vec.add(veci);
	    }

		return vec;
	}
	public ArrayList<int[]> run (Mat mat){
		this.mat = mat;
		FilterHSV(mat);

		//Imgproc.erode(tmp2, tmp3, tmp4);
		//Imgproc.adaptiveThreshold(hsvm, tmp3, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 7, 5);
		//Imgproc.cvtColor(hsvm, tmp1, Imgproc.COLOR_GRAY2RGB);
		Core.bitwise_and(mats[4], hsvm, tmp2);// V & M -> tmp2
		Imgproc.threshold(tmp2, tmp3, 250, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU); //
		tmp2.copyTo(mats[8]);// V & M
		hsvm.copyTo(mats[6]); // Mask
		tmp3.copyTo(mats[7]);// V&M thresh OTSU
		tmp2.copyTo(mats[9]);// V&M
		ArrayList<int[]> points = FindPoints(mats[9]);// точки
		ArrayList<int[]> vecl = getLines(tmp3); //lines from OTSU	
		mat.copyTo(mats[10]); 
		for (int x = 0; x < vecl.size(); x++)
	    {
			int[] vec = vecl.get(x);
			//System.out.println(vec[0] + " " + vec[1]+ " " + vec[2] + " " + vec[3]);
	          
	          int x1 = vec[0], 
	                 y1 = vec[1],
	                 x2 = vec[2],
	                 y2 = vec[3];
	          /*double x1 = lines.get(0, x)[0], 
	                 y1 = lines.get(0, x)[1],
	                 x2 = lines.get(1, x)[0],
	                 y2 = lines.get(1, x)[1];*/
	          Point start = new Point(x1, y1);
	          Point end = new Point(x2, y2);
	          Imgproc.line(mats[10], start, end, new Scalar(255,0,0), 3);
	          //break;
	    }

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
		if(!settings.drawed){
			settings.drawed = true;
			DrawMats();
		}
		return points;
	}
	private void DrawMats() {
		if(!mat.empty()) controller.setImage(0, mat2Image(mat));
		for(int i=0;i<NUM_MATS;i++){
			if(mats[i].empty()) continue;
			controller.setImage(i+1, mat2Image(mats[i]));
		}
	}
}
