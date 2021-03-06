package serial.read;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class ArduinoSerialReadUsingThread {
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
					// 코드 작성 - 스레드로 처리
					Thread readThread = new Thread(new Runnable() {
						@Override
						public void run() {
							// 바이트 단위로 전송하므로 BufferedReader는 못 쓰고, 대신 버퍼에 쌓아놓고 보낼 수 있게 함
							// 버퍼를 하나 만드는데 사이즈는 마음대로 정해도 된다!
							byte[] buffer = new byte[1024];
							int len = -1; // ('데이터 없음'부터 시작) 
							try {
								// 내가 지정한 1024바이트 짜리 버퍼에 값을 쌓아서 리턴
								while((len=in.read(buffer)) != -1) {
									// String객체로 변환해서 출력1
									System.out.println(new String(buffer,0,len));
									Thread.sleep(1000);
								}
							}catch(IOException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					});
					readThread.start();
					
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
		}
		
	}
	
}
