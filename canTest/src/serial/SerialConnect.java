package serial;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

// 시리얼 통신을 담당하는 객체 [기능으로 일반화된 객체]
// 	1. 포트 열고 input/output Stream 받기
public class SerialConnect{
	InputStream in;
	// 바이너리 통신이라서 Reader/Writer 계열만 안 쓰면 된다
	// (~InputStream/~OutputStream 계열은 바이트 통신)
	BufferedInputStream bis;
	OutputStream out;
	
	/* 입출력 부분 분리  - 리스닝 하는 부분 */
	CommPort commPort;
	
	// portName: CAN 포트랑 아두이노 포트가 다르므로
	// appName: 다른 프로그램 처리
	public void connect(String portName, String appName) {
		try {
			// COM포트가 실제 존재하고 사용가능한 상태인지 확인
			CommPortIdentifier commPortIdentifier =
					CommPortIdentifier.getPortIdentifier(portName);
			// 포트가 사용중인지 아닌지 체크
			if(commPortIdentifier.isCurrentlyOwned()) {
				System.out.println("포트를 사용할 수 없습니다.");
			}else {
				System.out.println("포트 사용 가능");
				commPort = commPortIdentifier.open(appName, 5000);
				System.out.println(commPort);
				// CAN, 아두이노 - SerialPort객체인지 확인하고 정보 셋팅
				if(commPort instanceof SerialPort) {
					System.out.println("SerialPort");
					// 실제 쓸 때는 지역변수가 아니라 멤버변수로 정의한다.
					SerialPort serialPort = (SerialPort)commPort;
					// SerialPort에 대한 설정
					// : 속도는 변수처리 해야 한다!
					// - CAN통신 속도(환경설정의 'Serial 통신 속도') 38400
					// - 아두이노 통신 속도: 9600
					serialPort.setSerialPortParams(38400,
							SerialPort.DATABITS_8, 
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);
					in = serialPort.getInputStream();
					bis = new BufferedInputStream(in);
					out = serialPort.getOutputStream();
				}
			}
		} catch (NoSuchPortException e) { // 포트 못 찾음
			e.printStackTrace();
		}catch (PortInUseException e) { // 포트가 사용중
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public BufferedInputStream getBis() {
		return bis;
	}

	public OutputStream getOut() {
		return out;
	}

	public CommPort getCommPort() {
		return commPort;
	}
	
} // end class



