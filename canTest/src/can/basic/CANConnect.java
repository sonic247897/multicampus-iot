package can.basic;

import java.io.IOException;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import gnu.io.SerialPort;
import serial.SerialConnect;

/* CAN통신을 위한 처리
 * 1. 수신 시작하기 - 데이터를 수신할 준비가 되었음을 셋팅
 * 2. 데이터 수신하기
 * 3. 데이터 송신하기
*/

// 캔통신 하기 위한 정보 셋팅만 담당 [CAN에 특화된 객체]
// 	1. 시리얼 통신 얻고 이벤트 붙이기
public class CANConnect {
	SerialConnect serialConnect; // CAN시리얼 포트 연결
	OutputStream out; // CAN과 output통신할 스트림
	
	public CANConnect(String portname) {
		// 시리얼 통신을 위해 연결
		serialConnect = new SerialConnect();
		// 클래스 이름을 넘겨줌
		serialConnect.connect(portname, this.getClass().getName());
		
		// input, output 작업을 하기 위해 리스너를 port에 연결
		SerialPort commport = (SerialPort)serialConnect.getCommPort();
		SerialListener listener = new SerialListener(serialConnect.getBis());
		try {
			commport.addEventListener(listener);
			commport.notifyOnDataAvailable(true); // 데이터가 발생될 때마다 호출
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
		// output스트림 얻기
		out = serialConnect.getOut();
		
		// 스레드가 실행될 수 있도록
		new Thread(new CanSerialWrite()).start();
		System.out.println("캔 통신이 시작되었습니다...");
	}
	
	// CAN통신을 하기 위한 스레드 - CAN output통신을 하는 스레드
	class CanSerialWrite implements Runnable{
		String data; // CAN으로 내보낼 데이터
		
		CanSerialWrite(){
			// 1. CAN통신을 할 수 있는 상태라는 것을 알려주기 - CAN수신을 설정
			// 11: 수신 동작 시작 / A9: checksum / \r: 캐리지 리턴
			this.data = ":G11A9\r"; // 수신 시작에 관련된 데이터 프레임
			// checksum은 공식으로 만들어야 하는데 시작은 고정이라 그냥 계산한 값을 넣어준다.
		}

		@Override
		public void run() {
			// 통신해야 하므로 문자열 -> 바이트 배열
			byte[] outputdata = data.getBytes();
			try { // 수신 상태라고 브로드캐스트
				out.write(outputdata);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		new CANConnect("COM8");
		// 결과: Can시리얼 포트로 전송된 데이터==> :G01A8
	}
}
