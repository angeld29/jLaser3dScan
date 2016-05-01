package jLaser3dScan;
import java.util.prefs.Preferences;

public class ScanSettings {
	private Preferences userPrefs;
	public int camID;
	public boolean isFile;
	public String filename;
	public String port; 
	public double hAngle;
	public ScanSettings(){
		userPrefs = Preferences.userRoot().node("jLaser3dScan");
		
		Load();
	}
	public void Save(){
		userPrefs.putInt("camID", camID);
		userPrefs.putBoolean("isFile", isFile);
		userPrefs.putDouble("hAngle", hAngle);
		userPrefs.put("filename", filename);
		userPrefs.put("port", port);
	}
	public void Load(){
		camID = userPrefs.getInt("camID", 0);
		isFile = userPrefs.getBoolean("isFile", false);
		hAngle = userPrefs.getDouble("hAngle", 56.0);
		filename = userPrefs.get("filename", "");
		port = userPrefs.get("port", "emul");
	}
}
