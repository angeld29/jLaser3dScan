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
/*
public class FormulaSolver {
    boolean manual_mode;
    boolean valuesSetted = false;
    double h;
    double a_man;
    double b_man;
    double a_auto;
    double b_auto;
    double tan_alfa;
    double shaft_x;
    double shaft_y;

    public void setMode(boolean manual_mode) {
        this.manual_mode = manual_mode;
    }

    public void setVars(double th, double fi, double alfa, double h, double shaft_x, double shaft_y) {
        manual_mode = true;
        this.h = h;
        a_man = -1 * Math.tan(Math.toRadians(th)) / h;
        b_man = Math.tan(Math.toRadians(fi)) / h;
        tan_alfa = Math.tan(Math.toRadians(alfa));
        this.shaft_x = shaft_x;
        this.shaft_y = shaft_y;
        valuesSetted = true;
    }

    public synchronized double[][] getCoordinates(double[] frameCoords, double angle) {
        if (valuesSetted) {
            int count = 0;
            for (double x : frameCoords) if (x != -1) count++;
            double[][] profileCoords = new double[count][3];
            int j = 0;
            for (int i = 0; i < frameCoords.length; i++) {
                if (frameCoords[i] != -1) {
                    profileCoords[j][0] = distance(frameCoords[i]);
                    profileCoords[j][1] = (FrameBuffer.frameWidth / 2 - frameCoords[i]) / (FrameBuffer.frameWidth / 2) * profileCoords[j][0] * a_man * -1 * h;
                    profileCoords[j][2] = ((FrameBuffer.frameHeight / 2) - (double) i) / (FrameBuffer.frameHeight / 2) * profileCoords[j][0] * tan_alfa;
                    j = j + 1;
                }

            }
            return turnProfile(profileCoords, Math.toRadians(angle));
        } else {
            return null;
        }
    }



    public double[][] turnProfile(double[][] coords, double angle) {
        if(coords != null) {
            double[][] newCoords = new double[coords.length][coords[0].length];
            for (int i = 0; i < coords.length;i++) {
                newCoords[i][0] = (coords[i][0] - shaft_x) * Math.cos(angle) - (coords[i][1] - shaft_y) * Math.sin(angle);
                newCoords[i][1] = (coords[i][1] - shaft_y) * Math.cos(angle) + (coords[i][0] - shaft_x) * Math.sin(angle);
                newCoords[i][2] = coords[i][2];
            }
            return newCoords;
        } else {
            return null;
        }
    }

    private synchronized double distance(double x) {
        if (manual_mode) {
            return 1 / (b_man + a_man * (FrameBuffer.frameWidth / 2 - x) / (FrameBuffer.frameWidth / 2));
        } else {
            return 1 / (b_auto + a_auto * (FrameBuffer.frameWidth / 2 - x) / (FrameBuffer.frameWidth / 2));
        }
    }
}
*/
