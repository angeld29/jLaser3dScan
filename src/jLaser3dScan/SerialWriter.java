package jLaser3dScan;

import javafx.application.Platform;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialWriter {
	SerialPort serialPort = null;
	private volatile boolean isReady = true;
	public SerialWriter(String port) /*throws SerialPortException*/ {
        /*serialPort = new SerialPort(port);
        System.out.println("Port opened: " + serialPort.openPort());
        System.out.println("Params setted: " + serialPort.setParams(9600, 8, 1, 0, true, false));
        int mask = SerialPort.MASK_RXCHAR;
        serialPort.setEventsMask(mask);
        serialPort.addEventListener(new SerialPortReader());*/
    }
    public void rotate(int steps) {
    	isReady = false;
    	if( serialPort == null){
    		Thread thread = new Thread(() -> {
    			try {
    				Thread.sleep(10);
    			} catch (InterruptedException ex) {
    				ex.printStackTrace();
    			}
    			isReady = true;
    		});
    		thread.start();
    	}
        /*try {
            return serialPort.writeString("r" + steps + "\r\n");
        } catch (SerialPortException ex) {
            ex.printStackTrace();
            return false;
        }*/
    }
    public boolean isRotateReady () {
    	return isReady;
    }
    
    public boolean disconnect() {
/*        try {
            serialPort.closePort();
            return true;
        } catch (SerialPortException ex) {
            ex.printStackTrace();
            return false;
        }*/
    	return true;
    }

}
