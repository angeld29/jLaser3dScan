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
	public boolean isFile;
	public String filename;
	public String port; 
	public double hAngle;
	public boolean drawed = false;
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
		userPrefs.putInt("hmin", hmin);
		userPrefs.putInt("hmax", hmax);
		userPrefs.putInt("umin", umin);
		userPrefs.putInt("umax", umax);
		userPrefs.putInt("vmin", vmin);
		userPrefs.putInt("vmax", vmax);
	}
	public void Load(){
		camID = userPrefs.getInt("camID", 0);
		isFile = userPrefs.getBoolean("isFile", false);
		hAngle = userPrefs.getDouble("hAngle", 56.0);
		filename = userPrefs.get("filename", "");
		port = userPrefs.get("port", "emul");
		hmin = userPrefs.getInt("hmin", 0 );
		hmax = userPrefs.getInt("hmax", 150);
		umin = userPrefs.getInt("umin", 0);
		umax = userPrefs.getInt("umax", 255);
		vmin = userPrefs.getInt("vmin", 200);
		vmax = userPrefs.getInt("vmax", 255);
	}
}
