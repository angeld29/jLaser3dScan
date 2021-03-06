package jLaser3dScan;
import java.util.prefs.Preferences;

public class ScanSettings {
	private Preferences userPrefs;
	public int camID;
	public int hmin;
	public int hmax;
	public int umin;
	public int umax;
	public int vmin;
	public int vmax;
	public int cropLeft;
	public int cropRight;
	public int camW;
	public int camH;
	public boolean isFile;
	public boolean isMJPG;
	public boolean isRecordVideo;
	public String filename;
	public String port; 
	public double hAngle;
	public double vAngle;
	public double fiAngle;
	public double hLen;
	public double shaftX;
	public double shaftY;
	public double turnAngle;
	public boolean drawed = false;
	public ScanSettings(){
		userPrefs = Preferences.userRoot().node("jLaser3dScan");
		
		Load();
	}
	public void Save(){
		userPrefs.putInt("camID", camID);
		userPrefs.putInt("camW", camW);
		userPrefs.putInt("camH", camH);
		userPrefs.putBoolean("isFile", isFile);
		userPrefs.putBoolean("isMJPG", isMJPG);
		userPrefs.putBoolean("isRecordVideo", isRecordVideo);
		userPrefs.putDouble("hAngle", hAngle);
		userPrefs.putDouble("vAngle", vAngle);
		userPrefs.put("filename", filename);
		userPrefs.put("port", port);
		userPrefs.putInt("hmin", hmin);
		userPrefs.putInt("hmax", hmax);
		userPrefs.putInt("umin", umin);
		userPrefs.putInt("umax", umax);
		userPrefs.putInt("vmin", vmin);
		userPrefs.putInt("vmax", vmax);
		userPrefs.putInt("cropLeft", cropLeft);
		userPrefs.putInt("cropRight", cropRight);
		userPrefs.putDouble("shaftX", shaftX);
		userPrefs.putDouble("fiAngle", fiAngle);
		userPrefs.putDouble("hLen", hLen);
		userPrefs.putDouble("shaftY", shaftY);
		userPrefs.putDouble("turnAngle", turnAngle);
	}
	public void Load(){
		camID = userPrefs.getInt("camID", 0);
		camW = userPrefs.getInt("camW", 0);
		camH = userPrefs.getInt("camH", 0);
		isFile = userPrefs.getBoolean("isFile", false);
		isMJPG = userPrefs.getBoolean("isMJPG", true);
		isRecordVideo = userPrefs.getBoolean("isRecordVideo", false);
		hAngle = userPrefs.getDouble("hAngle", 56.0);
		vAngle = userPrefs.getDouble("vAngle", 32.0);
		filename = userPrefs.get("filename", "");
		port = userPrefs.get("port", "emul");
		hmin = userPrefs.getInt("hmin", 0 );
		hmax = userPrefs.getInt("hmax", 150);
		umin = userPrefs.getInt("umin", 0);
		umax = userPrefs.getInt("umax", 255);
		vmin = userPrefs.getInt("vmin", 200);
		vmax = userPrefs.getInt("vmax", 255);
		cropLeft = userPrefs.getInt("cropLeft", 30);
		cropRight = userPrefs.getInt("cropRight", 85);
		hLen = userPrefs.getDouble("hLen", 14.5);
		fiAngle = userPrefs.getDouble("fiAngle", 35.5);
		shaftX = userPrefs.getDouble("shaftX", 19.6);
		shaftY = userPrefs.getDouble("shaftY", 0.7);
		turnAngle = userPrefs.getDouble("turnAngle", 2.0);
	}
}
