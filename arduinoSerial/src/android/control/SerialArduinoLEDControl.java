package android.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

// @모든 통신 공통 - 포트 열기
// 아두이노 시리얼 모니터를 키면 아두이노PORT~시리얼모니터가 통신중이므로 
// 아두이노 PORT에 접근할 수가 없다.
public class SerialArduinoLEDControl {
	OutputStream out;
	
	public SerialArduinoLEDControl() {
	
	}
	
	public void connect(String portName) {
		try {
			// COM포트가 실제 존재하고 사용가능한 상태인지 확인
			CommPortIdentifier commPortIdentifier =
					CommPortIdentifier.getPortIdentifier(portName);
			// 포트가 사용중인지 아닌지 체크
			if(commPortIdentifier.isCurrentlyOwned()) {
				System.out.println("포트를 사용할 수 없습니다.");
			}else {
				System.out.println("포트 사용 가능");
				// port가 사용 가능하면 포트를 열고 포트객체 얻어오기
				// 포트를 사용할 수 있게 모델링한 객체 - CommPort
				// 매개변수1: 포트를 열고 사용하는 프로그램의 이름(문자열)(아무렇게나 줘도 됨)
				// 매개변수2: 포트를 열고 통신하기 위해서 기다리는 시간(밀리세컨드)
				//			ex) 톰캣의 timeout과 같음
				// 접속, 통신 -> Exception
				CommPort commPort 
						= commPortIdentifier.open("basic_serial", 5000);
				//System.out.println(commPort);
				// 유효성 검사 - instanceof 쓰면 거의 캐스팅 목적이다!
				if(commPort instanceof SerialPort) {
					System.out.println("SerialPort");
					// 실제 쓸 때는 지역변수가 아니라 멤버변수로 정의한다.
					SerialPort serialPort = (SerialPort)commPort;
					// SerialPort에 대한 설정
					serialPort.setSerialPortParams(9600,
							SerialPort.DATABITS_8, 
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);
					InputStream in = serialPort.getInputStream();
					out = serialPort.getOutputStream();
					
					// 데이터를 주고 받는 작업을 여기에 작성(스레드 버전)
					// 안드로이드에서 입력받은 값을 아두이노로 전송하는 스레드
					// new SerialArduinoWriterThread(out).start();
					
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
	// 시리얼 출력을 위한 OutputStream 리턴 - 데이터를 car로 내보내서 제어하기 위한 작업
	public OutputStream getOutput() {
		return out;
	}
	
	public static void main(String[] args) {
		// 아두이노와 통신할 수 있는 각자의 포트 번호
		new SerialArduinoLEDControl().connect("COM11");
		
	}
}
