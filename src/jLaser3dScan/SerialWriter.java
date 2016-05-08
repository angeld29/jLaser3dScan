package jLaser3dScan;

import javafx.application.Platform;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialWriter {
	SerialPort serialPort = null;
	private volatile boolean isReady = true;
	public SerialWriter(String port) {
		if(port != "emul" ){
			try {
				serialPort = new SerialPort(port);
				System.out.println("Port opened: " + serialPort.openPort());
				System.out.println("Params setted: " + serialPort.setParams(9600, 8, 1, 0, true, false));
				int mask = SerialPort.MASK_RXCHAR;
				serialPort.setEventsMask(mask);
				serialPort.addEventListener(new SerialPortReader());
			} catch (SerialPortException ex) {
				ex.printStackTrace();
			}
		}
	}
    public boolean rotate(int steps) {
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
    		return true;
    	}else{
    		try {
    			return serialPort.writeString("r" + steps + "\r\n");
    		} catch (SerialPortException ex) {
    			ex.printStackTrace();
    			return false;
    		}
    	}
    }
    public boolean isRotateReady () {
    	return isReady;
    }
    
    public boolean disconnect() {
    	if( serialPort != null){
    		try {
    			serialPort.closePort();
    			return true;
    		} catch (SerialPortException ex) {
    			ex.printStackTrace();
    			return false;
    		}
    	}
    	return true;
    }
    class SerialPortReader implements SerialPortEventListener {
        StringBuilder message = new StringBuilder();
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    byte buffer[] = serialPort.readBytes();
                    for (byte b : buffer) {
                        if ((b == '\r' || b == '\n') && message.length() > 0) {
                            if (message.toString().contentEquals("r:Done")) {
                                Thread.sleep(1000);
                                isReady = true;
                                //Platform.runLater(() -> controller.setTakeShoot(true));
                            }
                            //System.out.println(message.toString());
                            message.setLength(0);
                        } else {
                            if (b != '\n' && b != '\r') message.append((char) b);
                        }
                    }
                } catch (SerialPortException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
