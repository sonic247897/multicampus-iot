package serial.read;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class ArduinoSerialReadUsingEvent {
	public static void main(String[] args) {
		// 시리얼 통신을 위해 접속하고 IO스트림을 만드는 부분은 공통으로 사용한다.
		try {
			CommPortIdentifier commPortIdentifier =
					CommPortIdentifier.getPortIdentifier("COM11");
			if(commPortIdentifier.isCurrentlyOwned()) {
				System.out.println("포트를 사용할 수 없습니다.");
			}else {
				System.out.println("포트 사용 가능");
				
				CommPort commPort 
						= commPortIdentifier.open("basic_serial", 5000);
				
				if(commPort instanceof SerialPort) {
					System.out.println("SerialPort");
					SerialPort serialPort = (SerialPort)commPort;
					// SerialPort에 대한 설정
					serialPort.setSerialPortParams(9600,
							SerialPort.DATABITS_8, 
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);
					InputStream in = serialPort.getInputStream();
					OutputStream out = serialPort.getOutputStream();
					
					// Arduino를 통해서 반복해서 들어오는 데이터를 읽을 수 있도록 코드 작성
					// 이벤트에 반응하도록 연결
					SerialListener listener = new SerialListener(in);
					serialPort.addEventListener(listener);
					// 시리얼 포트로 전송되어 들어오는 데이터가 있다면 notify하며 이벤트 리스너가
					// 실행되도록 처리
					serialPort.notifyOnDataAvailable(true);
					// @ 마스크 설정을 하면 어떤 센서데이터가 들어오는지도 알 수 있다.
				}else {
					System.out.println("SerialPort만 작업할 수 있습니다.");
				}
			}
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		}catch (PortInUseException e) {
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
		
	}
	
}
