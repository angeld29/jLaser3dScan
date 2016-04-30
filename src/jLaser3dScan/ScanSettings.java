package jLaser3dScan;

public class ScanSettings {
	public int camID;
	public boolean isFile;
	public String filename;
	public String port; 
	public double hAngle;
	public ScanSettings(){
		Load();
	}
	public void Save(){
		
	}
	public void Load(){
		camID = 0;
		isFile = false;
		hAngle = 56.0;
		filename = "";
		port ="emul";
	}
}
