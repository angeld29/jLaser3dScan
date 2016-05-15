package jLaser3dScan;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

public class ScanModel {
	private MainController controller;
	private ScanSettings settings;
	public ScanModel ( MainController controller, ScanSettings settings) {
		this.controller = controller;
		this.settings = settings;
	}
	void AddPoints(double angle, Mat mat ){
		
	}
	void SaveTxt(String filename){
		
	}

}
