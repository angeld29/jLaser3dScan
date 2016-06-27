package jLaser3dScan;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;

public class ScanModel {
	private MainController controller;
	private ScanSettings settings;
	private ArrayList<double[]> points3d;
	private double width;
	private double height;
	public ScanModel ( MainController controller, ScanSettings settings, int width, int height ) {
		this.controller = controller;
		this.settings = settings;
		this.width = (double)width;
		this.height = (double)height;
		this.points3d = new ArrayList<double[]>();
	}
	public void clear(){
		points3d.clear();
	}
	public void AddPoints(double angle, ArrayList<int[]> points ){
		for (int i = 0; i < points.size(); i++)
		{
			int[] point = points.get(i);
			double[] point3d = GetPoint(point[0], point[1], Math.toRadians(angle));
			points3d.add(point3d);
		}
		points3d.add(new double[]{0,0,0});
	}
	double [] turnPoints(double[] point, double angle){
		double shaft_x = settings.shaftX;
		double shaft_y = settings.shaftY;
		double x = (point[0] - shaft_x)*Math.cos(angle)- (point[1]-shaft_y)*Math.sin(angle);
		double y = (point[1] - shaft_y)*Math.cos(angle)- (point[0]-shaft_x)*Math.sin(angle);
		return new double[]{x, y, point[2]};
	}
	double [] GetPoint_old(int x, int y, double angle){
		double a_man = -1 * Math.tan(Math.toRadians(settings.hAngle/2)) / settings.hLen;
		double b_man = Math.tan(Math.toRadians(settings.fiAngle)) / settings.hLen;
		double tan_alfa = Math.tan(Math.toRadians(settings.vAngle/2));

		double x3 =  1 / (b_man + a_man * (width / 2 - x) / (width / 2));
		double y3 = ((width/2 - (double)x) / (width / 2)) * x3 * a_man * -1 * settings.hLen;
     //profileCoords[j][1] = (FrameBuffer.frameWidth / 2 - frameCoords[i]) / (FrameBuffer.frameWidth / 2) * profileCoords[j][0] * a_man * -1 * h;
		double z3  = ((height / 2) - (double) x) / (height / 2) *  x3 * tan_alfa;
		//System.out.printf("%.0f %d %d %.2f %f %f %f %f\n", width, x,y, settings.hLen, x3,y3,z3,a_man);

		return new double[] { x3, y3, z3 };
	
	}
	double [] GetPoint(int x, int y, double angle_r){
		
		double alpha_r  = Math.PI/2 + Math.atan( Math.tan(Math.toRadians(settings.hAngle/2 )) * (width/2 - (double)x) / (width / 2) );
		//(settings.hAngle/2) * ( width/2 - (double)x)/ (width/2);
		//double alpha_r = Math.toRadians(alpha);
		//double gamma = 180 - (90 - settings.fiAngle) - alpha;
		double betta_r = Math.toRadians(90- settings.fiAngle);
		double gamma_r = Math.PI - betta_r - alpha_r ;//Math.toRadians(gamma);
		double a_len = settings.hLen * Math.sin(alpha_r)/Math.sin(gamma_r);
		double xx = settings.hLen * a_len * Math.sin(betta_r);
		double yy = settings.hLen - a_len * Math.cos(betta_r);
		double vAnglecam_r = Math.toRadians(settings.hAngle /2 * height/width) ;//settings.vAngle;
		//double vAngle = (vAnglecam/2)*(height/2 - y)/(height/2);
		//double vAngle_r = Math.atan( Math.tan(vAnglecam_r) * (height/2 - (double)y) / (height / 2) );
		//double zz = xx * Math.tan(vAngle_r);
		double zz = xx *  Math.tan(vAnglecam_r) * (height/2 - (double)y) / (height / 2) ;

		return turnPoints(new double[]{xx,yy,zz}, angle_r);
		//return new double[]{(xx - settings.shaftX) ,(yy - settings.shaftY), zz};
		//return new double[]{(xx - settings.shaftX) * Math.cos(angle_r),(yy - settings.shaftY)*Math.sin(angle_r), zz};
	}
	void SaveTxt(String fileName){
		Path path;
		try {
			path = Paths.get(fileName);
			try {
				Files.createFile(path);
			} catch (FileAlreadyExistsException e) {
				System.err.println("already exists: " + e.getMessage());
				return;
			}
		} catch (IOException e) {
			System.out.println("Error creating file");
			e.printStackTrace();
			return;
		}

		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path.toString(), true)))) {
			//output to the file a line
			for (int i = 0; i < points3d.size(); i++)
			{
				double[] point = points3d.get(i);
				out.printf("%f %f %f\n", point[0],point[1],point[2]);
//				System.out.printf("%f %f %f\n", point[0],point[1],point[2]);
			}
			out.close();
		} catch (IOException e) {
			//TODO exception handling
		}
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
